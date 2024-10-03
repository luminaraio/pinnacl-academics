package io.pinnacl.academics.school.repository;

import io.pinnacl.commons.repository.BaseJpaRepository;
import io.pinnacl.commons.repository.RetrievalBy;
import io.pinnacl.commons.repository.UniqueConstraints;
import io.pinnacl.academics.school.data.persistence.TermEntity;
import org.hibernate.reactive.mutiny.Mutiny;

public class TermRepository extends BaseJpaRepository<TermEntity>
                            implements RetrievalBy<TermEntity>, UniqueConstraints<TermEntity> {
    protected TermRepository(Class<TermEntity> clazz, Mutiny.SessionFactory sessionFactory) {
        super(clazz, sessionFactory);
    }

    public static TermRepository create(Mutiny.SessionFactory sessionFactory) {
        return new TermRepository(TermEntity.class, sessionFactory);
    }
}
