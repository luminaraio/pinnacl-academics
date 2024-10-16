package io.pinnacl.academics.admissions;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.pinnacl.academics.admissions.data.domain.*;
import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.validation.*;
import io.vertx.core.Future;

import java.util.List;
import java.util.Objects;

public class AdmissionsValidator extends BaseDomainValidator<Admission>
                                 implements UniqueConstraintsValidator<Admission, AdmissionEntity> {

    private final PhoneNumberValidator phoneNumberValidator;

    private AdmissionsValidator(List<Validator<Admission>> preValidators,
                                List<Validator<Admission>> postValidator) {
        super(preValidators, postValidator);
        this.phoneNumberValidator = PhoneNumberValidator.create(PhoneNumberUtil.getInstance());
    }

    private AdmissionsValidator(List<Validator<Admission>> preValidators) {
        this(preValidators, List.of());
    }

    public static AdmissionsValidator create() {
        return new AdmissionsValidator(List.of(StructuralValidator.create()));
    }

    @Override
    public Future<Admission> validation(AuthUser authUser, Admission admission) {
        // TODO: Move to the AdmissionService
        // if school is-not-null:
        // - ensure school exist
        // - ensure that the documents match what is defined by the school

        var metadataValidation = switch (admission.metadata()) {
            case GenericSchoolAdmission x -> doValidation(authUser, x);
            case NurseryAdmission x -> doValidation(x);
            case PrimaryAdmission x -> doValidation(x);
            case SecondaryAdmission x -> doValidation(x);
            case CollegeAdmission x -> doValidation(x);
            case UniversityAdmission x -> doValidation(x);
        };

        return metadataValidation.map(_ -> admission);
    }

    private Future<Metadata> doValidation(AuthUser authUser, GenericSchoolAdmission admission) {
        if (Objects.isNull(admission.guardianTelephone()) || Objects.isNull(admission.dateOfBirth())
                || Objects.isNull(admission.name())) {
            return Future.failedFuture(Problems.PAYLOAD_VALIDATION_ERROR
                    .withProblemError("missingAttribute",
                            "guardianTelephone, dateOfBirth, name or gender must be provided")
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

}
