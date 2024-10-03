package io.pinnacl.academics.application.repository;

import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.repository.BaseJpaRepository;
import io.pinnacl.commons.repository.RetrievalBy;
import io.pinnacl.commons.repository.UniqueConstraints;
import io.pinnacl.academics.application.data.persistence.ApplicationEntity;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.hibernate.reactive.mutiny.Mutiny;

public class ApplicationRepository extends BaseJpaRepository<ApplicationEntity>
                                   implements RetrievalBy<ApplicationEntity>,
                                   UniqueConstraints<ApplicationEntity> {


    protected ApplicationRepository(Class<ApplicationEntity> clazz,
                                    Mutiny.SessionFactory sessionFactory) {
        super(clazz, sessionFactory);
    }

    public static ApplicationRepository create(Mutiny.SessionFactory sessionFactory) {
        return new ApplicationRepository(ApplicationEntity.class, sessionFactory);
    }

    public Future<Long> countApplication(AuthUser authUser, String className) {
        var query = new JsonObject().put("className", className.trim());
        return count(authUser, query);
    }
}
