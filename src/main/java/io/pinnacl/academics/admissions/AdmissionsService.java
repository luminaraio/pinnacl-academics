package io.pinnacl.academics.admissions;

import io.pinnacl.academics.admissions.repository.AdmissionRepository;
import io.pinnacl.academics.data.domain.AdmissionsConfig;
import io.pinnacl.academics.admissions.data.domain.Admission;
import io.pinnacl.academics.admissions.data.domain.AdmissionQuestionAnswer;
import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.academics.school.SchoolService;
import io.pinnacl.academics.school.data.domain.SchoolQuestion;
import io.pinnacl.commons.Strings;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.data.mapper.Mapper;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.features.forms.data.domain.Document;
import io.pinnacl.commons.features.forms.data.domain.DocumentDefinition;
import io.pinnacl.commons.repository.Repository;
import io.pinnacl.commons.service.DefaultRecordService;
import io.pinnacl.commons.service.MessageService;
import io.pinnacl.commons.validation.Validator;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Predicate;

public class AdmissionsService extends DefaultRecordService<Admission, AdmissionEntity>
                               implements MessageService {

    private final Vertx _vertx;
    private final ServiceDiscovery _discovery;
    private final AdmissionsConfig _admissionsConfig;


    private AdmissionsService(Vertx vertx, ServiceDiscovery discovery,
                              Mapper<Admission, AdmissionEntity> mapper,
                              Repository<AdmissionEntity> repository,
                              Validator<Admission> validator, AdmissionsConfig admissionsConfig) {
        super(Admission.class, mapper, repository, validator);
        _vertx            = vertx;
        _discovery        = discovery;
        _admissionsConfig = admissionsConfig;
    }

    public static AdmissionsService create(Vertx vertx, ServiceDiscovery discovery,
                                           Mapper<Admission, AdmissionEntity> mapper,
                                           Repository<AdmissionEntity> repository,
                                           Validator<Admission> validator,
                                           AdmissionsConfig admissionsConfig) {
        return new AdmissionsService(vertx, discovery, mapper, repository, validator,
                admissionsConfig);
    }

    @Override
    public ServiceDiscovery discovery() {
        return _discovery;
    }

    @Override
    public EventBus eventBus() {
        return _vertx.eventBus();
    }

    @Override
    public Future<Admission> onCreate(AuthUser authUser, Admission admission) {
        logMethodEntry("AdmissionsService.onCreate");
        return super.onCreate(authUser, admission)
                .flatMap(withOwner -> validator().validation(authUser, withOwner))
                .flatMap(_admission -> doExtraValidation(authUser, _admission))
                .map(_admission -> _admission.draft(generateApplicationNumber(admission)));
    }

    @Override
    public Future<Admission> onUpdate(AuthUser authUser, Admission admission) {
        return super.onUpdate(authUser, admission)
                .flatMap(withOwner -> validator().validation(authUser, withOwner))
                .flatMap(_admission -> doExtraValidation(authUser, _admission));
    }


    @Override
    public Future<Admission> onPostCreate(AuthUser authUser, Admission persisted) {
        logMethodEntry("AdmissionsService.onPostCreate");
        return super.onPostCreate(authUser, persisted).onSuccess(this::onCreatePostProcessing);
    }

    private String generateApplicationNumber(Admission admission) {
        logMethodEntry("AdmissionsService.generateApplicationNumber");
        if (Objects.nonNull(admission.transientSchool())) {
            return processAdmissionApplicationNumber(admission);
        }
        return defaultApplicationNumber();
    }

    private void onCreatePostProcessing(Admission admission) {
        logMethodEntry("AdmissionsService.onCreatePostProcessing");
    }

    private Future<Admission> doExtraValidation(AuthUser authUser, Admission admission) {
        if (Objects.nonNull(admission.school())) {
            var schoolById = JsonObject.of("id", admission.school().id().toString());
            // TODO: Fix by moving extra validation back to the validator class
            return Future.succeededFuture(admission);
            // return _schoolService.retrieve(authUser, schoolById).flatMap(schools -> {
            // if (schools.isEmpty()) {
            // return Future.failedFuture(Problems.PAYLOAD_VALIDATION_ERROR
            // .withProblemError("school", "not a valid school")
            // .toException());
            // }
            // return Future.succeededFuture(admission.withTransientSchool(schools.getFirst()));
            // })
            // .flatMap(withSchool -> Future
            // .all(checkExtraQuestions(withSchool), checkDocuments(withSchool))
            // .map(_ -> withSchool));
        }
        return Future.succeededFuture(admission);
    }

    private Future<Void> checkExtraQuestions(Admission withSchool) {
        var schoolQuestions = withSchool.transientSchool().extraAdmissionQuestions();
        var requiredExtraQuestions =
                schoolQuestions.stream().filter(SchoolQuestion::required).toList();

        Predicate<AdmissionQuestionAnswer> isAnswered =
                (AdmissionQuestionAnswer qa) -> requiredExtraQuestions.stream()
                        .anyMatch(x -> x.type() == qa.type()
                                && StringUtils.equals(x.name(), qa.name()));


        var allMatch = withSchool.questionAnswers().stream().allMatch(isAnswered::test);

        if (Boolean.FALSE.equals(allMatch)) {
            return Future
                    .failedFuture(Problems.PAYLOAD_VALIDATION_ERROR
                            .withProblemError("missingQuestionAnswers",
                                    "not all required extra-questions have been answered")
                            .toException());
        }

        return Future.succeededFuture();
    }

    private Future<Void> checkDocuments(Admission withSchool) {
        var supportingDocuments = withSchool.transientSchool().supportingDocuments();
        var requiredSupportingDocuments =
                supportingDocuments.stream().filter(DocumentDefinition::required).toList();

        Predicate<DocumentDefinition> isAttached =
                (DocumentDefinition x) -> requiredSupportingDocuments.stream()
                        .anyMatch(y -> StringUtils.equals(x.name(), y.name()));

        var allMatch = withSchool.documents()
                .stream()
                .map(Document::definition)
                .allMatch(isAttached::test);

        if (Boolean.FALSE.equals(allMatch)) {
            return Future.failedFuture(Problems.PAYLOAD_VALIDATION_ERROR
                    .withProblemError("missingDocuments",
                            "not all required supporting documents have been uploaded")
                    .toException());
        }

        return Future.succeededFuture();
    }

    private String processAdmissionApplicationNumber(Admission admission) {
        return Optional.ofNullable(admission.transientSchool().metadata().admissionsConfig())
                .or(() -> Optional.ofNullable(_admissionsConfig))
                .map(AdmissionsConfig::generateApplicationNumber)
                .get();
    }

    private String defaultApplicationNumber() {
        return "%s-%s".formatted(
                Strings.generateUniqueCharString(_admissionsConfig.applicationNumberPrefixLength()),
                Strings.generateUniqueCharString(
                        _admissionsConfig.applicationNumberPostfixLength()))
                .toUpperCase(Locale.ROOT);
    }

    public Future<List<Admission>> retrieveSchoolAdmissions(AuthUser authUser, UUID schoolId) {
        return ((AdmissionRepository) repository()).retrieveBySchoolId(authUser, schoolId)
                .flatMap(results -> mapper().toDomainObjects(results));
    }
}
