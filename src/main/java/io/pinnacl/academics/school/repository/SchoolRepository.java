package io.pinnacl.academics.school.repository;

import io.pinnacl.commons.repository.BaseJpaRepository;
import io.pinnacl.commons.repository.RetrievalBy;
import io.pinnacl.commons.repository.UniqueConstraints;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import org.hibernate.reactive.mutiny.Mutiny;

public class SchoolRepository extends BaseJpaRepository<SchoolEntity> implements
                              RetrievalBy<SchoolEntity>, UniqueConstraints<SchoolEntity> {

    private final TermRepository _termRepository;
    private final TuitionFeeRepository _tuitionFeeRepository;

    protected SchoolRepository(Class<SchoolEntity> clazz, Mutiny.SessionFactory sessionFactory,
                               TermRepository termRepository,
                               TuitionFeeRepository tuitionFeeRepository) {
        super(clazz, sessionFactory);
        _termRepository       = termRepository;
        _tuitionFeeRepository = tuitionFeeRepository;
    }

    public static SchoolRepository create(Mutiny.SessionFactory sessionFactory,
                                          TermRepository termRepository,
                                          TuitionFeeRepository tuitionFeeRepository) {
        return new SchoolRepository(SchoolEntity.class, sessionFactory, termRepository,
                tuitionFeeRepository);
    }
}
