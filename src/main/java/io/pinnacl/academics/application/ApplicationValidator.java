package io.pinnacl.academics.application;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.validation.*;
import io.pinnacl.academics.application.data.domain.Application;
import io.pinnacl.academics.application.data.persistence.ApplicationEntity;
import io.vertx.core.Future;

import java.util.List;
import java.util.Objects;

public class ApplicationValidator extends BaseDomainValidator<Application> implements
                                  UniqueConstraintsValidator<Application, ApplicationEntity> {

    private final PhoneNumberValidator phoneNumberValidator;

    private ApplicationValidator(List<Validator<Application>> preValidators,
                                 List<Validator<Application>> postValidator) {
        super(preValidators, postValidator);
        this.phoneNumberValidator = PhoneNumberValidator.create(PhoneNumberUtil.getInstance());
    }

    private ApplicationValidator(List<Validator<Application>> preValidators) {
        this(preValidators, List.of());
    }

    public static ApplicationValidator create() {
        return new ApplicationValidator(List.of(StructuralValidator.create()));
    }

    @Override
    public Future<Application> validation(AuthUser authUser, Application application) {
        if (Objects.isNull(application.guardianTelephone()) || Objects.isNull(application.gender())
                || Objects.isNull(application.dateOfBirth())
                || Objects.isNull(application.name())) {
            return Future.failedFuture(Problems.PAYLOAD_VALIDATION_ERROR
                    .withProblemError("missingAttribute",
                            "guardianTelephone, dateOfBirth, name or gender must be provided")
                    .toException());
        }
        return phoneNumberValidator.validate(authUser, application.guardianTelephone())
                .map(application);

    }

}
