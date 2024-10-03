package io.pinnacl.core.education.school;

import io.pinnacl.commons.validation.BaseDomainValidator;
import io.pinnacl.commons.validation.StructuralValidator;
import io.pinnacl.commons.validation.UniqueConstraintsValidator;
import io.pinnacl.commons.validation.Validator;
import io.pinnacl.core.education.school.data.domain.School;
import io.pinnacl.core.education.school.data.persistence.SchoolEntity;
import io.pinnacl.core.education.school.repository.SchoolRepository;

import java.util.List;

public class SchoolValidator extends BaseDomainValidator<School>
                             implements UniqueConstraintsValidator<School, SchoolEntity> {

    private final SchoolRepository _schoolRepository;

    private SchoolValidator(List<Validator<School>> preValidators,
                            List<Validator<School>> postValidator,
                            SchoolRepository schoolRepository) {
        super(preValidators, postValidator);
        _schoolRepository = schoolRepository;
    }

    private SchoolValidator(List<Validator<School>> preValidators,
                            SchoolRepository schoolRepository) {
        this(preValidators, List.of(), schoolRepository);
    }

    public static SchoolValidator create(SchoolRepository schoolRepository) {
        return new SchoolValidator(List.of(StructuralValidator.create()), schoolRepository);
    }

}
