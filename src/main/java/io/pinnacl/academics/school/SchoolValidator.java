package io.pinnacl.academics.school;

import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.commons.validation.BaseDomainValidator;
import io.pinnacl.commons.validation.StructuralValidator;
import io.pinnacl.commons.validation.UniqueConstraintsValidator;
import io.pinnacl.commons.validation.Validator;

import java.util.List;

public class SchoolValidator extends BaseDomainValidator<School>
                             implements UniqueConstraintsValidator<School, SchoolEntity> {

    private SchoolValidator(List<Validator<School>> preValidators,
                            List<Validator<School>> postValidator) {
        super(preValidators, postValidator);
    }

    private SchoolValidator(List<Validator<School>> preValidators) {
        this(preValidators, List.of());
    }

    public static SchoolValidator create() {
        return new SchoolValidator(List.of(StructuralValidator.create()));
    }

}
