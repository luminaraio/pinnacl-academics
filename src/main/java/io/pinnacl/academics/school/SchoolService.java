package io.pinnacl.academics.school;

import io.pinnacl.academics.admissions.AdmissionsService;
import io.pinnacl.academics.admissions.data.domain.Admission;
import io.pinnacl.academics.data.domain.AdmissionsConfig;
import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.data.mapper.Mapper;
import io.pinnacl.commons.repository.Repository;
import io.pinnacl.commons.service.DefaultRecordService;
import io.pinnacl.commons.validation.Validator;
import io.vertx.core.Future;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SchoolService extends DefaultRecordService<School, SchoolEntity> {

    private final AdmissionsConfig _defaultAdmissionConfig;
    private final AdmissionsService _admissionsService;

    private SchoolService(Mapper<School, SchoolEntity> mapper, Repository<SchoolEntity> repository,
                          Validator<School> validator, AdmissionsConfig defaultAdmissionConfig,
                          AdmissionsService admissionsService) {
        super(School.class, mapper, repository, validator);
        _defaultAdmissionConfig = defaultAdmissionConfig;
        _admissionsService      = admissionsService;
    }

    public static SchoolService create(Mapper<School, SchoolEntity> mapper,
                                       Repository<SchoolEntity> repository,
                                       Validator<School> validator,
                                       AdmissionsConfig defaultAdmissionConfig,
                                       AdmissionsService admissionsService) {
        return new SchoolService(mapper, repository, validator, defaultAdmissionConfig,
                admissionsService);
    }

    @Override
    public Future<School> onCreate(AuthUser authUser, School domain) {
        logMethodEntry("SchoolService.onCreate");
        if (Objects.isNull(domain.metadata().admissionsConfig())) {
            return super.onCreate(authUser,
                    domain.admissionsConfig(_defaultAdmissionConfig.assignPrefix()));
        }
        return super.onCreate(authUser, domain);
    }

    public Future<List<Admission>> retrieveSchoolAdmissionsHandler(AuthUser authUser,
                                                                   UUID schoolId) {
        return _admissionsService.retrieveSchoolAdmissions(authUser, schoolId);
    }
}
