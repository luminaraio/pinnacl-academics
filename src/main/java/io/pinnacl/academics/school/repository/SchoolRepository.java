package io.pinnacl.academics.school.repository;

import io.pinnacl.commons.repository.BaseJpaRepository;
import io.pinnacl.commons.repository.RetrievalBy;
import io.pinnacl.commons.repository.UniqueConstraints;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

public class SchoolRepository extends BaseJpaRepository<SchoolEntity> implements
                              RetrievalBy<SchoolEntity>, UniqueConstraints<SchoolEntity> {

    protected SchoolRepository(Class<SchoolEntity> clazz, Mutiny.SessionFactory sessionFactory) {
        super(clazz, sessionFactory);
    }

    public static SchoolRepository create(Mutiny.SessionFactory sessionFactory) {
        return new SchoolRepository(SchoolEntity.class, sessionFactory);
    }

    @Override
    public String selectQuery() {
        return """
               SELECT %1$s
               FROM %2$s %1$s
               LEFT JOIN FETCH %1$s.terms t
               LEFT JOIN FETCH %1$s.tuitionFees tf
               LEFT JOIN FETCH %1$s.socialLinks sl
               """.formatted(alias(), className());
    }

    @Override
    public List<String> joinFetchQueries() {
        return List.of(joinFetchQuery("terms", "t"), joinFetchQuery("tuitionFees", "tf"),
                joinFetchQuery("socialLinks", "sl"));
    }
}
