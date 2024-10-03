package io.pinnacl.core.education.school.mapper;

import io.pinnacl.core.education.school.data.persistence.TermEntity;
import io.pinnacl.core.education.school.data.domain.Term;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
@Mapper
public interface TermMapper extends io.pinnacl.commons.data.mapper.Mapper<Term, TermEntity> {
    TermMapper INSTANCE = Mappers.getMapper(TermMapper.class);

    @Mapping(source = "version", target = "revision")
    Term asDomainObject(TermEntity entity);

    @InheritInverseConfiguration
    TermEntity asEntity(Term term);

    @Override
    List<Term> asDomainObjects(List<TermEntity> entities);

    @Override
    List<TermEntity> asEntities(List<Term> list);
}
