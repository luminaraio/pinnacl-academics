package io.pinnacl.academics.school;

import io.pinnacl.commons.config.VerticleConfig;
import io.pinnacl.commons.service.ValidatedService;
import io.pinnacl.commons.verticle.ValidatedResourceVerticle;
import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.vavr.control.Try;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SchoolVerticle extends ValidatedResourceVerticle<School, SchoolEntity> {

    private static final Logger CLASS_LOGGER = LoggerFactory.getLogger(School.class);

    private SchoolVerticle(ServiceDiscovery discovery, VerticleConfig config,
                           ValidatedService<School, SchoolEntity> service) {
        super(discovery, config, List.of(), service);
    }

    public static SchoolVerticle create(ServiceDiscovery discovery, VerticleConfig config,
                                        ValidatedService<School, SchoolEntity> service) {
        return new SchoolVerticle(discovery, config, service);
    }

    @Override
    public Logger logger() {
        return CLASS_LOGGER;
    }

    @Override
    public void start(Promise<Void> promise) throws Exception {
        var handlers = List.of(addMessageHandler("retrieveSchools", this::fetchHandler),
                addMessageHandler("retrieveSchool", this::fetchByIdHandler),
                addMessageHandler("createSchool", this::createHandler),
                addMessageHandler("updateSchool", this::updateHandler),
                addMessageHandler("archiveSchool", this::deleteByIdHandler));
        Future.all(handlers)
                .onSuccess(_void -> Try.run(() -> super.start(promise)).onFailure(failure -> {
                    logError("Could not start verticle {} due to the following exception: {}",
                            this.getClass().getSimpleName(), failure.getMessage());
                    promise.fail(failure.getCause());
                }))
                .onFailure(promise::fail);
    }

}
