package io.pinnacl.academics.admissions;

import io.pinnacl.academics.admissions.data.domain.Admission;
import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.commons.config.VerticleConfig;
import io.pinnacl.commons.service.ValidatedService;
import io.pinnacl.commons.verticle.ValidatedResourceVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdmissionsVerticle extends ValidatedResourceVerticle<Admission, AdmissionEntity> {

    private static final Logger CLASS_LOGGER = LoggerFactory.getLogger(Admission.class);

    private AdmissionsVerticle(ServiceDiscovery discovery, VerticleConfig config,
                               ValidatedService<Admission, AdmissionEntity> service) {
        super(discovery, config, List.of(), service);
    }

    public static AdmissionsVerticle create(ServiceDiscovery discovery, VerticleConfig config,
                                            ValidatedService<Admission, AdmissionEntity> service) {
        return new AdmissionsVerticle(discovery, config, service);
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
                addMessageHandler("archiveApplication", this::deleteByIdHandler));

        Future.all(handlers).onSuccess(_ -> {
            try {
                super.start(promise);
            } catch (Exception failure) {
                logError("Could not start verticle {} due to the following exception: {}",
                        this.getClass().getSimpleName(), failure.getMessage());
                promise.fail(failure.getCause());
            }
        }).onFailure(promise::fail);
    }

}
