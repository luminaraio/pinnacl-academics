package io.pinnacl.academics.application;

import io.pinnacl.commons.config.VerticleConfig;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.message.Events;
import io.pinnacl.commons.service.ValidatedService;
import io.pinnacl.commons.verticle.ValidatedResourceVerticle;
import io.pinnacl.academics.application.data.domain.Application;
import io.pinnacl.academics.application.data.persistence.ApplicationEntity;
import io.vavr.control.Try;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ApplicationVerticle extends ValidatedResourceVerticle<Application, ApplicationEntity> {

    private static final Logger CLASS_LOGGER = LoggerFactory.getLogger(Application.class);

    private ApplicationVerticle(ServiceDiscovery discovery, VerticleConfig config,
                                ValidatedService<Application, ApplicationEntity> service) {
        super(discovery, config, List.of(), service);
    }

    public static ApplicationVerticle create(ServiceDiscovery discovery, VerticleConfig config,
                                             ValidatedService<Application, ApplicationEntity> service) {
        return new ApplicationVerticle(discovery, config, service);
    }

    @Override
    public Logger logger() {
        return CLASS_LOGGER;
    }

    @Override
    public void start(Promise<Void> promise) throws Exception {
        var handlers = List.of(addMessageHandler("retrieveApplications", this::fetchHandler),
                addMessageHandler("retrieveApplication", this::fetchByIdHandler),
                addMessageHandler("createApplication", this::createHandler),
                addMessageHandler("updateApplication", this::updateHandler),
                addMessageHandler("archiveApplication", this::deleteByIdHandler), addMessageHandler(
                        "retrieveApplicationsAnalytics", this::fetchApplicationsAnalyticsHandler));
        Future.all(handlers)
                .onSuccess(_void -> Try.run(() -> super.start(promise)).onFailure(failure -> {
                    logError("Could not start verticle {} due to the following exception: {}",
                            this.getClass().getSimpleName(), failure.getMessage());
                    promise.fail(failure.getCause());
                }))
                .onFailure(promise::fail);
    }

    private void fetchApplicationsAnalyticsHandler(Message<JsonObject> message) {
        logMethodEntry("ExternalOrganisationViewVerticle.fetchApplicationsAnalyticsHandler");
        authUser(message)
                .flatMap(authUser -> ((ApplicationService) service())
                        .fetchApplicationsAnalytics(authUser))
                .flatMap(applicationAnalytics -> Events.replyAsCloudEvent(message, discovery(),
                        applicationAnalytics))
                .onSuccess(response -> successfulReply(message, response))
                .onFailure(error -> failureReply(message, Problems.fromThrowable(error)));;
    }


}
