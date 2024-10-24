package io.pinnacl.academics.admissions;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.pinnacl.academics.admissions.data.domain.*;
import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.academics.school.data.domain.SchoolQuestion;
import io.pinnacl.academics.school.mapper.SchoolMapper;
import io.pinnacl.academics.school.repository.SchoolRepository;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.features.forms.data.domain.Document;
import io.pinnacl.commons.features.forms.data.domain.DocumentDefinition;
import io.pinnacl.commons.validation.*;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class AdmissionsValidator extends BaseDomainValidator<Admission>
        implements UniqueConstraintsValidator<Admission, AdmissionEntity> {

    private final PhoneNumberValidator phoneNumberValidator;
    private final SchoolRepository _schoolRepository;

    private AdmissionsValidator(List<Validator<Admission>> preValidators,
                                List<Validator<Admission>> postValidator,
                                SchoolRepository schoolRepository) {
        super(preValidators, postValidator);
        this.phoneNumberValidator = PhoneNumberValidator.create(PhoneNumberUtil.getInstance());
        _schoolRepository = schoolRepository;
    }

    private AdmissionsValidator(List<Validator<Admission>> preValidators,
                                SchoolRepository schoolRepository) {
        this(preValidators, List.of(), schoolRepository);
    }

    public static AdmissionsValidator create(SchoolRepository schoolRepository) {
        return new AdmissionsValidator(List.of(StructuralValidator.create()), schoolRepository);
    }

    @Override
    public Future<Admission> validation(AuthUser authUser, Admission admission) {

        var metadataValidation = switch (admission.metadata()) {
            case GenericSchoolAdmission x -> doValidation(authUser, x);
            case NurseryAdmission x -> doValidation(x);
            case PrimaryAdmission x -> doValidation(x);
            case SecondaryAdmission x -> doValidation(x);
            case CollegeAdmission x -> doValidation(x);
            case UniversityAdmission x -> doValidation(x);
        };

        return metadataValidation.flatMap(_ -> doExtraValidation(authUser, admission))
                .map(withTransients -> withTransients);
    }

    private Future<Metadata> doValidation(AuthUser authUser, GenericSchoolAdmission admission) {
        if (Objects.isNull(admission.guardianTelephone())
                || Objects.isNull(admission.dateOfBirth())) {
            return Future
                    .failedFuture(Problems.PAYLOAD_VALIDATION_ERROR
                            .withProblemError("missingAttribute",
                                    "guardianTelephone, dateOfBirth, must be provided")
                            .toException());
        }
        return phoneNumberValidator.validate(authUser, admission.guardianTelephone())
                .map(admission);
    }

    private Future<Metadata> doValidation(NurseryAdmission admission) {
        return Future.succeededFuture(admission);
    }

    private Future<Metadata> doValidation(PrimaryAdmission admission) {
        return Future.succeededFuture(admission);
    }

    private Future<Metadata> doValidation(SecondaryAdmission admission) {
        return Future.succeededFuture(admission);
    }

    private Future<Metadata> doValidation(CollegeAdmission admission) {
        return Future.succeededFuture(admission);
    }

    private Future<Metadata> doValidation(UniversityAdmission admission) {
        return Future.succeededFuture(admission);
    }

    private Future<Admission> doExtraValidation(AuthUser authUser, Admission admission) {
        if (Objects.nonNull(admission.school())) {
            var schoolById = JsonObject.of("id", admission.school().id().toString());
            return _schoolRepository.retrieve(authUser, schoolById)
                    .flatMap(SchoolMapper.INSTANCE::toDomainObjects)
                    .flatMap(schools -> {
                        if (schools.isEmpty()) {
                            return Future.failedFuture(Problems.PAYLOAD_VALIDATION_ERROR
                                    .withProblemError("school", "not a valid school")
                                    .toException());
                        }
                        return Future.succeededFuture(admission.withTransientSchool(schools.getFirst()));
                    })
                    .flatMap(withSchool -> Future
                            .all(checkExtraQuestions(withSchool), checkDocuments(withSchool))
                            .map(_ -> withSchool));
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
}
