package io.pinnacl.core.education.school.repository;

import io.pinnacl.commons.repository.BaseJpaRepository;
import io.pinnacl.commons.repository.RetrievalBy;
import io.pinnacl.commons.repository.UniqueConstraints;
import io.pinnacl.core.education.school.data.persistence.TuitionFeeEntity;
import org.hibernate.reactive.mutiny.Mutiny;

public class TuitionFeeRepository extends BaseJpaRepository<TuitionFeeEntity>
                                  implements RetrievalBy<TuitionFeeEntity>,
                                  UniqueConstraints<TuitionFeeEntity> {
    protected TuitionFeeRepository(Class<TuitionFeeEntity> clazz,
                                   Mutiny.SessionFactory sessionFactory) {
        super(clazz, sessionFactory);
    }

    public static TuitionFeeRepository create(Mutiny.SessionFactory sessionFactory) {
        return new TuitionFeeRepository(TuitionFeeEntity.class, sessionFactory);
    }
}
