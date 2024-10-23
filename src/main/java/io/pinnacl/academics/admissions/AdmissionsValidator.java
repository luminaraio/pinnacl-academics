package io.pinnacl.academics.admissions;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.pinnacl.academics.admissions.data.domain.*;
import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.academics.admissions.repository.AdmissionRepository;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.validation.*;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

public class AdmissionsValidator extends BaseDomainValidator<Admission>
                                 implements UniqueConstraintsValidator<Admission, AdmissionEntity> {

    private final PhoneNumberValidator phoneNumberValidator;
    private final AdmissionRepository _repository;

    private AdmissionsValidator(List<Validator<Admission>> preValidators,
                                List<Validator<Admission>> postValidator,
                                AdmissionRepository repository) {
        super(preValidators, postValidator);
        this.phoneNumberValidator = PhoneNumberValidator.create(PhoneNumberUtil.getInstance());
        _repository               = repository;
    }

    private AdmissionsValidator(List<Validator<Admission>> preValidators,
                                AdmissionRepository repository) {
        this(preValidators, List.of(), repository);
    }

    public static AdmissionsValidator create(AdmissionRepository repository) {
        return new AdmissionsValidator(List.of(StructuralValidator.create()), repository);
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

        return metadataValidation.map(_ -> admission);
    }

    @Override
    public Future<Admission> validationOnUpdate(AuthUser authUser, Admission admission) {
        return super.validationOnUpdate(authUser, admission).flatMap(_ -> _repository
                .retrieveOne(authUser, JsonObject.of("id", admission.id().toString()))
                .flatMap(existing -> {
                    if (existing.getStatus() != admission.status()) {
                        // Validation error
                        return Future.failedFuture(Problems.PAYLOAD_VALIDATION_ERROR
                                .withProblemError("mismatchedStatus", "status cannot be changed")
                                .toException());
                    }
                    return Future.succeededFuture(admission);
                })
                .recover(cause -> {
                    // Map to a validation problem
                    return Future.failedFuture(Problems.NOT_FOUND
                            .withProblemError("missingAdmission", "admission does not exist")
                            .toException());
                }));
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
}
