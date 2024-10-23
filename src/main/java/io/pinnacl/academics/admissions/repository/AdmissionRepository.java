package io.pinnacl.academics.admissions.repository;

import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.error.Problems;
import io.pinnacl.commons.repository.BaseJpaRepository;
import io.pinnacl.commons.repository.RetrievalBy;
import io.pinnacl.commons.repository.UniqueConstraints;
import io.smallrye.mutiny.vertx.UniHelper;
import io.vavr.Tuple2;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class AdmissionRepository extends BaseJpaRepository<AdmissionEntity> implements
                                 RetrievalBy<AdmissionEntity>, UniqueConstraints<AdmissionEntity> {


    protected AdmissionRepository(Class<AdmissionEntity> clazz,
                                  Mutiny.SessionFactory sessionFactory) {
        super(clazz, sessionFactory);
    }

    public static AdmissionRepository create(Mutiny.SessionFactory sessionFactory) {
        return new AdmissionRepository(AdmissionEntity.class, sessionFactory);
    }

    @Override
    public String selectQuery() {
        return """
               SELECT %1$s
               FROM %2$s %1$s
               LEFT JOIN FETCH %1$s.questionAnswers qa
               LEFT JOIN FETCH %1$s.documents d
               """.formatted(alias(), className());
    }

    @Override
    public List<String> joinFetchQueries() {
        return List.of(joinFetchQuery("questionAnswers", "qa"), joinFetchQuery("documents", "d"));
    }


    public Future<Long> countApplication(AuthUser authUser, String className) {
        var query = new JsonObject().put("className", className.trim());
        return count(authUser, query);
    }


    public Future<List<AdmissionEntity>> retrieveBySchoolId(AuthUser authUser, UUID schoolId) {
        logMethodEntry("AdmissionRepository.retrieveBySchoolId", schoolId);
        if (Objects.isNull(schoolId)) {
            return Future.failedFuture(Problems.OBJECT_VALIDATION_ERROR
                    .withProblemError("schoolId", "The id should not be null")
                    .toException());
        }

        var selectQuery = """
                          SELECT %1$s
                          FROM %2$s %1$s
                          LEFT JOIN FETCH %1$s.school t
                          """.formatted(alias(), className());;
        var whereClause = """
                          t.id = :schoolId
                          """;

        Function<Tuple2<List<UUID>, String>, Future<List<AdmissionEntity>>> dbQuery = owners -> {
            String sqlStatement =
                    sqlStatement(selectQuery, whereClause.formatted(alias()), owners._2());
            return UniHelper.toFuture(_sessionFactory.withSession(session -> {
                Mutiny.Query<AdmissionEntity> query = session.createQuery(sqlStatement);
                query.setParameter("schoolId", schoolId);
                if (StringUtils.isNotBlank(owners._2())) {
                    query.setParameter(PARAMETER_OWNER_ID, owners._1());
                }
                return joinFetches(session, query.getResultList());
            })).flatMap(results -> {
                if (results.isEmpty()) {
                    return Future.succeededFuture(List.<AdmissionEntity>of());
                } else {
                    return Future.succeededFuture(results);
                }
            }).recover(this::recoverDBError);
        };
        return executeQuery(authUser, dbQuery);
    }
}
