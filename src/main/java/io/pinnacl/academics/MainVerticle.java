package io.pinnacl.academics;

import io.pinnacl.academics.admissions.AdmissionsService;
import io.pinnacl.academics.admissions.AdmissionsValidator;
import io.pinnacl.academics.admissions.AdmissionsVerticle;
import io.pinnacl.academics.admissions.mapper.AdmissionMapper;
import io.pinnacl.academics.admissions.repository.ApplicationRepository;
import io.pinnacl.academics.school.SchoolService;
import io.pinnacl.academics.school.SchoolValidator;
import io.pinnacl.academics.school.SchoolVerticle;
import io.pinnacl.academics.school.mapper.SchoolMapper;
import io.pinnacl.academics.school.repository.SchoolRepository;
import io.pinnacl.academics.school.repository.TermRepository;
import io.pinnacl.academics.school.repository.TuitionFeeRepository;
import io.pinnacl.commons.config.Config;
import io.pinnacl.commons.config.VerticleConfig;
import io.pinnacl.commons.data.mapper.JsonMapper;
import io.pinnacl.commons.service.DbHealthService;
import io.pinnacl.commons.verticle.AbstractMainVerticle;
import io.pinnacl.commons.verticle.ServiceDetailsVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.ext.web.templ.handlebars.HandlebarsTemplateEngine;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
public final class MainVerticle extends AbstractMainVerticle {
    private static final Logger CLASS_LOGGER = LoggerFactory.getLogger(MainVerticle.class);
    private static final String KEY_GATEWAY_SERVICE = "gateway";
    private static final String SCHOOL = "school";
    private static final String APPLICATION = "schoolApplication";
    private static final String KEY_LANGUAGES = "languages";
    // private static final String FEATURE_SCHOOL_APPLICATION_EMAIL = "schoolApplication";

    @Override
    public Logger logger() {
        return CLASS_LOGGER;
    }

    @Override
    protected Future<Void> deployVerticles(Config config,
                                           Map<String, AsyncMap<String, Object>> sharedMaps,
                                           Mutiny.SessionFactory sessionFactory) {
        return validateRequiredSharedMaps(sharedMaps, Set.of(KEY_GATEWAY_SERVICE, KEY_LANGUAGES))
                .flatMap(_ -> verticleConfigs(config, Set.of("service", SCHOOL, APPLICATION)))
                .flatMap(verticleConfigs -> deployVerticles(config.asJsonObject(), verticleConfigs,
                        sharedMaps, sessionFactory));
    }

    private Future<Void> deployVerticles(JsonObject config,
                                         Map<String, VerticleConfig> verticleConfigs,
                                         Map<String, AsyncMap<String, Object>> sharedMaps,
                                         Mutiny.SessionFactory sessionFactory) {
        return Future.succeededFuture();
        //
        // VerticleConfig applicationVerticleConfig = verticleConfigs.get(APPLICATION);
        //
        //
        // var dbHealthService = DbHealthService.fromSessionFactory(sessionFactory);
        //
        // var termRepository = TermRepository.create(sessionFactory);
        // var tuitionFeeRepository = TuitionFeeRepository.create(sessionFactory);
        // var schoolRepository =
        // SchoolRepository.create(sessionFactory, termRepository, tuitionFeeRepository);
        // var schoolService = SchoolService.create(SchoolMapper.INSTANCE, schoolRepository,
        // SchoolValidator.create(schoolRepository));
        // var applicationRepository = ApplicationRepository.create(sessionFactory);
        // var applicationService = AdmissionsService.create(vertx, discovery(),
        // AdmissionMapper.INSTANCE, applicationRepository, AdmissionsValidator.create());
        //
        //
        // Supplier<DeploymentOptions> deploymentOptions =
        // () -> new DeploymentOptions().setConfig(config);
        //
        // var serviceDetailsVerticle = ServiceDetailsVerticle.create(vertx, discovery(), config,
        // verticleConfigs.get("service"), "academics", dbHealthService);
        //
        // var schoolVerticle =
        // SchoolVerticle.create(discovery(), verticleConfigs.get("school"), schoolService);
        // var applicationVerticle = AdmissionsVerticle.create(discovery(),
        // verticleConfigs.get(APPLICATION), applicationService);
        // var educations = Academics.create(schoolVerticle, applicationVerticle);
        //
        //
        // return Future
        // .all(List.of(vertx.deployVerticle(serviceDetailsVerticle, deploymentOptions.get()),
        // vertx.deployVerticle(educations, deploymentOptions.get())))
        // .mapEmpty();
    }
}
