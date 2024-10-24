package io.pinnacl.academics.admissions;

import io.pinnacl.academics.admissions.data.domain.Admission;
import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.academics.admissions.repository.AdmissionRepository;
import io.pinnacl.academics.data.domain.AdmissionsConfig;
import io.pinnacl.commons.Strings;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.data.mapper.Mapper;
import io.pinnacl.commons.repository.Repository;
import io.pinnacl.commons.service.DefaultRecordService;
import io.pinnacl.commons.service.MessageService;
import io.pinnacl.commons.validation.Validator;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;

import java.util.*;

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
                .flatMap(_admission -> generateUniqueApplicationNumber(authUser, _admission)
                        .map(_admission::draft));
    }

    @Override
    public Future<Admission> onUpdate(AuthUser authUser, Admission admission) {
        return super.onUpdate(authUser, admission)
                .flatMap(withOwner -> validator().validation(authUser, withOwner));
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

    private Future<Tuple2<String, Boolean>> isApplicationNumberUnique(AuthUser authUser, String applicationNumber) {
        return repository().count(authUser, JsonObject.of("applicationNumber", applicationNumber))
                .map(count -> Tuple.of(applicationNumber, count > 0 ? Boolean.FALSE : Boolean.TRUE));
    }

    private Future<String> generateUniqueApplicationNumber(AuthUser authUser, Admission admission) {
        return isApplicationNumberUnique(authUser, generateApplicationNumber(admission))
                .flatMap(tuple2 -> {
                    if (tuple2._2()) {
                        return Future.succeededFuture(tuple2._1());
                    }
                    return generateUniqueApplicationNumber(authUser, admission);
                });
    }
}
