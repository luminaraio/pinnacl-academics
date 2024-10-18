package io.pinnacl.academics;

import io.pinnacl.academics.admissions.AdmissionsService;
import io.pinnacl.academics.admissions.AdmissionsValidator;
import io.pinnacl.academics.admissions.AdmissionsVerticle;
import io.pinnacl.academics.admissions.mapper.AdmissionMapper;
import io.pinnacl.academics.admissions.repository.AdmissionRepository;
import io.pinnacl.academics.school.SchoolService;
import io.pinnacl.academics.school.SchoolValidator;
import io.pinnacl.academics.school.SchoolVerticle;
import io.pinnacl.academics.school.mapper.SchoolMapper;
import io.pinnacl.academics.school.repository.SchoolRepository;
import io.pinnacl.academics.school.repository.TermRepository;
import io.pinnacl.academics.school.repository.TuitionFeeRepository;
import io.pinnacl.commons.config.Config;
import io.pinnacl.commons.config.VerticleConfig;
import io.pinnacl.commons.service.DbHealthService;
import io.pinnacl.commons.verticle.AbstractMainVerticle;
import io.pinnacl.commons.verticle.ServiceDetailsVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
public final class MainVerticle extends AbstractMainVerticle {
    private static final Logger CLASS_LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public Logger logger() {
        return CLASS_LOGGER;
    }

    @Override
    public Set<String> features() {
        return Set.of("schools", "admissions");
    }

    @Override
    protected Future<Void> deployVerticles(Config config,
                                           Map<String, AsyncMap<String, Object>> sharedMaps,
                                           Mutiny.SessionFactory sessionFactory) {
        return verticleConfigs(config)
                .flatMap(verticleConfigs -> deployVerticles(config.asJsonObject(), verticleConfigs,
                        sharedMaps, sessionFactory));
    }

    private Future<Void> deployVerticles(JsonObject config,
                                         Map<String, VerticleConfig> verticleConfigs,
                                         Map<String, AsyncMap<String, Object>> sharedMaps,
                                         Mutiny.SessionFactory sessionFactory) {

        var deploymentOptions = new DeploymentOptions().setConfig(config);
        var dbHealthService = DbHealthService.fromSessionFactory(sessionFactory);

        var schoolsConfig = verticleConfigs.get("schools");
        var schoolRepository = SchoolRepository.create(sessionFactory);
        var schoolsService = SchoolService.create(SchoolMapper.INSTANCE, schoolRepository,
                SchoolValidator.create());

        var admissionsConfig = verticleConfigs.get("admissions");
        var admissionRepository = AdmissionRepository.create(sessionFactory);
        var admissionsService = AdmissionsService.create(vertx, discovery(),
                AdmissionMapper.INSTANCE, admissionRepository, AdmissionsValidator.create());

        var serviceDetailsVerticle = ServiceDetailsVerticle.create(vertx, discovery(), config,
                verticleConfigs.get("service"), "academics", dbHealthService);

        var schoolVerticle = SchoolVerticle.create(discovery(), schoolsConfig, schoolsService);

        var admissionsVerticle =
                AdmissionsVerticle.create(discovery(), admissionsConfig, admissionsService);

        return Future.all(List.of(vertx.deployVerticle(serviceDetailsVerticle, deploymentOptions),
                vertx.deployVerticle(schoolVerticle, deploymentOptions),
                vertx.deployVerticle(admissionsVerticle, deploymentOptions))).mapEmpty();
    }
}
