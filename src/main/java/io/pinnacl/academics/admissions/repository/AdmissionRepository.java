package io.pinnacl.academics.admissions.repository;

import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.repository.BaseJpaRepository;
import io.pinnacl.commons.repository.RetrievalBy;
import io.pinnacl.commons.repository.UniqueConstraints;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.hibernate.reactive.mutiny.Mutiny;

public class AdmissionRepository extends BaseJpaRepository<AdmissionEntity> implements
                                 RetrievalBy<AdmissionEntity>, UniqueConstraints<AdmissionEntity> {


    protected AdmissionRepository(Class<AdmissionEntity> clazz,
                                  Mutiny.SessionFactory sessionFactory) {
        super(clazz, sessionFactory);
    }

    public static AdmissionRepository create(Mutiny.SessionFactory sessionFactory) {
        return new AdmissionRepository(AdmissionEntity.class, sessionFactory);
    }

    public Future<Long> countApplication(AuthUser authUser, String className) {
        var query = new JsonObject().put("className", className.trim());
        return count(authUser, query);
    }
}
