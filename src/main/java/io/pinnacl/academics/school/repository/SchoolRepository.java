package io.pinnacl.academics.school.repository;

import io.pinnacl.commons.repository.BaseJpaRepository;
import io.pinnacl.commons.repository.RetrievalBy;
import io.pinnacl.commons.repository.UniqueConstraints;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import org.hibernate.reactive.mutiny.Mutiny;

public class SchoolRepository extends BaseJpaRepository<SchoolEntity> implements
                              RetrievalBy<SchoolEntity>, UniqueConstraints<SchoolEntity> {

    protected SchoolRepository(Class<SchoolEntity> clazz, Mutiny.SessionFactory sessionFactory) {
        super(clazz, sessionFactory);
    }

    public static SchoolRepository create(Mutiny.SessionFactory sessionFactory) {
        return new SchoolRepository(SchoolEntity.class, sessionFactory);
    }
}
