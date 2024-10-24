package io.pinnacl.academics.school;

import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.academics.school.mapper.SchoolMapper;
import io.pinnacl.academics.school.repository.SchoolRepository;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.validation.BaseDomainValidator;
import io.pinnacl.commons.validation.StructuralValidator;
import io.pinnacl.commons.validation.UniqueConstraintsValidator;
import io.pinnacl.commons.validation.Validator;
import io.vertx.core.Future;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class SchoolValidator extends BaseDomainValidator<School>
                             implements UniqueConstraintsValidator<School, SchoolEntity> {
    private final SchoolRepository _schoolRepository;

    private SchoolValidator(List<Validator<School>> preValidators,
                            List<Validator<School>> postValidator,
                            SchoolRepository schoolRepository) {
        super(preValidators, postValidator);
        _schoolRepository = schoolRepository;
    }

    private SchoolValidator(List<Validator<School>> preValidators, SchoolRepository schoolRepository) {
        this(preValidators, List.of(), schoolRepository);
    }

    public static SchoolValidator create(SchoolRepository schoolRepository) {
        return new SchoolValidator(List.of(StructuralValidator.create()), schoolRepository);
    }

    @Override
    public Future<School> validation(AuthUser authUser, School school) {
        return super.validation(authUser, school);
    }

    @Override
    public Future<School> validationOnCreate(AuthUser authUser, School school) {
        return super.validationOnCreate(authUser, school)
                .flatMap(_school -> validateSchoolMetaData(authUser, _school));
    }

    private Future<School> validateSchoolMetaData(AuthUser authUser, School school) {
        var admissionsConfig = school.metadata().admissionsConfig();
        if (Objects.isNull(admissionsConfig)) {
            return Future.succeededFuture(school);
        }
        var prefix = admissionsConfig.applicationNumberPrefix();
        if (!StringUtils.isAlpha(prefix)) {
            return Future.failedFuture(Problems.PAYLOAD_VALIDATION_ERROR.
                    withProblemError("school.metadata.admissionConfig.applicationNumberPrefix", "must be strictly alphabetic")
                    .toException());
        }
        return _schoolRepository.retrieve(authUser)
                .flatMap(SchoolMapper.INSTANCE::toDomainObjects)
                .flatMap(schools -> {
                    if (anySchoolApplicationNumberStartsWith(schools, prefix)) {
                        return Future.failedFuture(Problems.PAYLOAD_VALIDATION_ERROR
                                .withProblemError("school.metadata.admissionConfig.applicationNumberPrefix",
                                        "application number prefix not available").toException());
                    }
                    return Future.succeededFuture(school);
                })
                .map(school);
    }

    private static boolean anySchoolApplicationNumberStartsWith(List<School> schools, String prefix) {
        return schools.stream().anyMatch(school ->
                StringUtils.equals(school.metadata().admissionsConfig().applicationNumberPrefix(), prefix));
    }
}
