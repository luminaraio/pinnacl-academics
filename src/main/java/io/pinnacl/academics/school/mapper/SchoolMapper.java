package io.pinnacl.academics.school.mapper;

import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.commons.features.mapper.PinnaclFeatureMapper;
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
@Mapper(uses = {
        TermMapper.class, TuitionFeeMapper.class, PinnaclFeatureMapper.class,
        SchoolMetadataMapper.class
})
public interface SchoolMapper extends io.pinnacl.commons.data.mapper.Mapper<School, SchoolEntity> {
    SchoolMapper INSTANCE = Mappers.getMapper(SchoolMapper.class);

    @Mapping(source = "version", target = "revision")
    School asDomainObject(SchoolEntity entity);

    @InheritInverseConfiguration
    SchoolEntity asEntity(School domain);

    @Override
    List<School> asDomainObjects(List<SchoolEntity> entities);

    @Override
    List<SchoolEntity> asEntities(List<School> list);
}
