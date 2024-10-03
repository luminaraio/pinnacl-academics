package io.pinnacl.academics.school;

import io.pinnacl.academics.application.ApplicationVerticle;
import io.pinnacl.academics.school.SchoolVerticle;
import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.commons.Logs;
import io.pinnacl.academics.application.ApplicationVerticle;
import io.pinnacl.academics.school.SchoolVerticle;
import io.pinnacl.academics.school.data.domain.School;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;


public class Educations extends AbstractVerticle implements Logs {

    private static final Logger CLASS_LOGGER = LoggerFactory.getLogger(School.class);

    private final Set<Verticle> _verticles;

    private Educations(Set<Verticle> verticles) {
        _verticles = verticles;
    }

    public static Educations create(SchoolVerticle schoolVerticle,
                                    ApplicationVerticle applicationVerticle) {
        return new Educations(Set.of(schoolVerticle, applicationVerticle));
    }

    @Override
    public Logger logger() {
        return CLASS_LOGGER;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        logMethodEntry("Educations.start");
        var verticles = _verticles.stream()
                .map(verticle -> vertx.deployVerticle(verticle))
                .collect(Collectors.toList());

        Future.all(verticles).onSuccess(_res -> {
            logInfo("All {} verticle(s) deployed successfully", getClass().getSimpleName());
            startPromise.complete();
        }).onFailure(cause -> {
            logError("An Issue occurred while deploying {} verticle(s)", getClass().getSimpleName(),
                    cause);
            startPromise.fail(cause);
        });
    }

}
