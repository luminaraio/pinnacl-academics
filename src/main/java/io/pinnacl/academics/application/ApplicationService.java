package io.pinnacl.academics.application;

import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.data.mapper.JsonMapper;
import io.pinnacl.commons.data.mapper.Mapper;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.message.Events;
import io.pinnacl.commons.repository.Repository;
import io.pinnacl.commons.service.DefaultRecordService;
import io.pinnacl.commons.service.MessageService;
import io.pinnacl.commons.validation.Validator;
import io.pinnacl.academics.application.data.notification.DeliveryRecipient;
import io.pinnacl.academics.application.data.notification.Email;
import io.pinnacl.academics.application.data.Status;
import io.pinnacl.academics.application.data.config.SchoolApplicationEmailConfig;
import io.pinnacl.academics.application.data.domain.Application;
import io.pinnacl.academics.application.data.domain.ApplicationAnalytics;
import io.pinnacl.academics.application.data.persistence.ApplicationEntity;
import io.pinnacl.academics.application.repository.ApplicationRepository;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApplicationService extends DefaultRecordService<Application, ApplicationEntity>
                                implements MessageService {

    private final String _worksDir;
    private final Vertx _vertx;
    private final ServiceDiscovery _discovery;
    private final SchoolApplicationEmailConfig _schoolApplicationEmailConfig;
    private final TemplateEngine _templateEngine;
    private static final String OPERATION_EMAIL = "email";
    private static final String CLOUD_EVENT_SOURCE =
            "io.pinnacl.core.education.application.ApplicationService";
    private static final String GUARDIAN_EMAIL = "guardianEmail";
    private static final String STUDENT_NAME = "name";

    private ApplicationService(Vertx vertx, ServiceDiscovery discovery,
                               Mapper<Application, ApplicationEntity> mapper,
                               Repository<ApplicationEntity> repository,
                               Validator<Application> validator, TemplateEngine templateEngine,
                               String worksDir, SchoolApplicationEmailConfig config) {
        super(Application.class, mapper, repository, validator);
        _vertx                        = vertx;
        _discovery                    = discovery;
        _worksDir                     = worksDir;
        _schoolApplicationEmailConfig = config;
        _templateEngine               = templateEngine;
    }

    public static ApplicationService create(Vertx vertx, ServiceDiscovery discovery,
                                            Mapper<Application, ApplicationEntity> mapper,
                                            Repository<ApplicationEntity> repository,
                                            Validator<Application> validator,
                                            TemplateEngine templateEngine, String worksDir,
                                            SchoolApplicationEmailConfig config) {
        return new ApplicationService(vertx, discovery, mapper, repository, validator,
                templateEngine, worksDir, config);
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
    public Future<Application> create(AuthUser authUser, Application domain) {
        return ((ApplicationRepository) repository())
                .retrieveByAnd(authUser, GUARDIAN_EMAIL, domain.guardianEmail(), STUDENT_NAME,
                        domain.name())
                .flatMap(apps -> {
                    if (!apps.isEmpty()) {
                        return Future.failedFuture(Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR
                                .withProblemError("[guardianEmail, name]",
                                        "The pair application.guardianEmail and application.name already exists")
                                .toException());
                    }
                    return ((ApplicationRepository) repository())
                            .countApplication(authUser, domain.className())
                            .flatMap(count -> super.create(authUser,
                                    domain.withApplicationNumber(count)).flatMap(application -> {
                                        if (_schoolApplicationEmailConfig.notifications()
                                                .onCreateOrUpdate()
                                                .sendEmail()) {
                                            return deliverAmissionConfirmationMail(authUser,
                                                    application).map(application);
                                        }
                                        return Future.succeededFuture(application);
                                    }));
                });
    }

    @Override
    public Future<Application> update(AuthUser authUser, Application domain) {
        return super.update(authUser, domain).flatMap(application -> {
            if (application.status().equals(Status.ADMITTED)
                    || application.status().equals(Status.REJECTED)) {
                if (_schoolApplicationEmailConfig.notifications().onCreateOrUpdate().sendEmail()) {
                    return deliverAdmittedRejectionMail(authUser, application).map(application);
                }
            }
            return Future.succeededFuture(application);
        });
    }

    private Future<Void> deliverAmissionConfirmationMail(AuthUser authUser,
                                                         Application application) {
        logMethodEntry("ApplicationService.deliverAmissionConfirmationMail");

        var supportEmail = StringUtils.replaceOnce(_schoolApplicationEmailConfig.templates()
                .applicationConfirmation()
                .template()
                .params()
                .supportEmail(), "(supportEmail)", "example.email@gmail.com");

        var params = new JsonObject().put("name", application.name())
                .put("gender", application.gender())
                .put("sickness", application.sickness())
                .put("className", application.className())
                .put("guardianEmail", application.guardianEmail())
                .put("guardianAddress", application.guardianAddress())
                .put("applicationNumber", application.applicationNumber())
                .put("applicationSubmissionDate",
                        application.createdOn()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .put("institutionName", "ACME School")
                .put("admissionsEmail", supportEmail)
                .put("institutionWebsite", "www.some-random-school.com")
                .put("admissionsPhone", "+234 650 000 875 999")
                .put("supportEmail",
                        _schoolApplicationEmailConfig.templates()
                                .applicationConfirmation()
                                .template()
                                .params()
                                .supportEmail())
                .put("plazzaaLogoUrl",
                        _schoolApplicationEmailConfig.templates()
                                .applicationConfirmation()
                                .template()
                                .params()
                                .plazzaaLogoUrl())
                .put("signature",
                        _schoolApplicationEmailConfig.templates()
                                .applicationConfirmation()
                                .template()
                                .params()
                                .signature());

        var templatePath = StringUtils.replaceOnce(_schoolApplicationEmailConfig.templates()
                .applicationConfirmation()
                .template()
                .path(), "(messageType)", "email");
        var path = StringUtils.replaceOnce(templatePath, "$workingDir", _worksDir);

        return _templateEngine.render(params, path)
                .onFailure(error -> logError(
                        "An issue occurred while rendering email delivery for school application confirmation",
                        error))
                .map(Buffer::toString)
                .flatMap(messageText -> {
                    var email = new Email(messageText,
                            _schoolApplicationEmailConfig.templates()
                                    .applicationConfirmation()
                                    .template()
                                    .params()
                                    .title(),
                            List.of(new DeliveryRecipient(application.guardianEmail(),
                                    application.guardianName())));
                    return JsonMapper.fromPojo(email)
                            .flatMap(emailPayload -> request(authUser, OPERATION_EMAIL,
                                    CLOUD_EVENT_SOURCE, emailPayload))
                            .flatMap(Events::singleResponse)
                            .recover(cause -> {
                                logError(
                                        "An issue occurred while attempting to deliver school application confirmation email",
                                        cause);
                                return Future.succeededFuture();
                            })
                            .onSuccess(_ -> logDebug(
                                    "Successfully sent school application confirmation email"))
                            .mapEmpty();
                });
    }

    private Future<Void> deliverAdmittedRejectionMail(AuthUser authUser, Application application) {
        logMethodEntry("ApplicationService.deliverAdmittedRejectionMail");

        var supportEmail = StringUtils.replaceOnce(_schoolApplicationEmailConfig.templates()
                .applicationStatus()
                .template()
                .params()
                .supportEmail(), "(supportEmail)", "example.email@gmail.com");

        boolean isAccepted = application.status().equals(Status.ADMITTED);
        var status = isAccepted ? "Accepted" : "Rejected";

        var params = new JsonObject().put("name", application.name())
                .put("status", status)
                .put("isAccepted", isAccepted)
                .put("className", application.className())
                .put("guardianEmail", application.guardianEmail())
                .put("acceptanceDeadline",
                        application.createdOn()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .put("institutionName", "ACME School")
                .put("admissionsEmail", supportEmail)
                .put("institutionWebsite", "www.some-random-school.com")
                .put("admissionsPhone", "+234 650 000 875 999")
                .put("supportEmail",
                        _schoolApplicationEmailConfig.templates()
                                .applicationStatus()
                                .template()
                                .params()
                                .supportEmail())
                .put("plazzaaLogoUrl",
                        _schoolApplicationEmailConfig.templates()
                                .applicationStatus()
                                .template()
                                .params()
                                .plazzaaLogoUrl())
                .put("signature",
                        _schoolApplicationEmailConfig.templates()
                                .applicationStatus()
                                .template()
                                .params()
                                .signature());

        var templatePath = StringUtils.replaceOnce(
                _schoolApplicationEmailConfig.templates().applicationStatus().template().path(),
                "(messageType)", "email");
        var path = StringUtils.replaceOnce(templatePath, "$workingDir", _worksDir);


        return _templateEngine.render(params, path)
                .onFailure(error -> logError(
                        "An issue occurred while rendering email delivery for school application confirmation",
                        error))
                .map(Buffer::toString)
                .flatMap(messageText -> {
                    var email = new Email(messageText,
                            StringUtils.replaceOnce(_schoolApplicationEmailConfig.templates()
                                    .applicationStatus()
                                    .template()
                                    .params()
                                    .title(), "(status)", status),
                            List.of(new DeliveryRecipient(application.guardianEmail(),
                                    application.guardianName())));
                    return JsonMapper.fromPojo(email)
                            .flatMap(emailPayload -> request(authUser, OPERATION_EMAIL,
                                    CLOUD_EVENT_SOURCE, emailPayload))
                            .flatMap(Events::singleResponse)
                            .recover(cause -> {
                                logError(
                                        "An issue occurred while attempting to deliver school application confirmation email",
                                        cause);
                                return Future.succeededFuture();
                            })
                            .onSuccess(_ -> logDebug(
                                    "Successfully sent school application confirmation email"))
                            .mapEmpty();
                });
    }

    public Future<ApplicationAnalytics> fetchApplicationsAnalytics(AuthUser authUser) {
        return repository().count(authUser, new JsonObject().put("deleted", false))
                .flatMap(apps -> countApplicationByStatus(authUser, Status.REJECTED)
                        .flatMap(rej -> countApplicationByStatus(authUser, Status.ADMITTED).flatMap(
                                adm -> countApplicationByStatus(authUser, Status.INCOMPLETE).map(
                                        inc -> new ApplicationAnalytics(apps, rej, adm, inc)))));
    }

    public Future<Long> countApplicationByStatus(AuthUser authUser, Status status) {
        return repository().count(authUser,
                new JsonObject().put("status", status.value).put("deleted", false));
    }
}
