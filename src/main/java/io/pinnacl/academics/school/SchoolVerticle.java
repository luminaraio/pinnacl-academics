package io.pinnacl.academics.school;

import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.commons.config.VerticleConfig;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.verticle.ValidatedResourceVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SchoolVerticle extends ValidatedResourceVerticle<School, SchoolEntity> {

    private static final Logger CLASS_LOGGER = LoggerFactory.getLogger(SchoolVerticle.class);

    private SchoolVerticle(ServiceDiscovery discovery, VerticleConfig config,
                           SchoolService service) {
        super(discovery, config, List.of(), service);
    }

    public static SchoolVerticle create(ServiceDiscovery discovery, VerticleConfig config,
                                        SchoolService service) {
        return new SchoolVerticle(discovery, config, service);
    }

    @Override
    public Logger logger() {
        return CLASS_LOGGER;
    }

    @Override
    public void start(Promise<Void> promise) throws Exception {
        var handlers = List.of(addMessageHandler("createSchool", this::createHandler),
                addMessageHandler("updateSchool", this::updateHandler),
                addMessageHandler("retrieveSchools", this::fetchHandler),
                addMessageHandler("retrieveSchool", this::fetchByIdHandler),
                addMessageHandler("archiveSchool", this::deleteByIdHandler), addMessageHandler(
                        "retrieveSchoolAdmissions", this::retrieveSchoolAdmissionsHandler));


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

    private void retrieveSchoolAdmissionsHandler(Message<JsonObject> message) {
        logMethodEntry("SchoolVerticle.retrieveSchoolAdmissionsHandler");
        operationHandler(message,
                () -> resourceIdentifierValue(message).map(Future::succeededFuture)
                        .orElseGet(() -> Future
                                .failedFuture(Problems.PAYLOAD_VALIDATION_ERROR.toException())),
                ((SchoolService) service())::retrieveSchoolAdmissionsHandler);
    }

}
