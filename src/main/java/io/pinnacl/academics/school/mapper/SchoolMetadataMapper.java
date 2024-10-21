package io.pinnacl.academics.school.mapper;

import io.pinnacl.academics.school.data.domain.metadata.*;
import io.pinnacl.academics.school.data.persistence.SchoolMetadataEntity;
import io.vertx.core.json.JsonObject;
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
public interface SchoolMetadataMapper extends
                                      io.pinnacl.commons.data.mapper.Mapper<Metadata, SchoolMetadataEntity> {
    SchoolMetadataMapper INSTANCE = Mappers.getMapper(SchoolMetadataMapper.class);

    default Metadata asDomainObject(SchoolMetadataEntity entity) {
        var typeClass = switch (entity.getType()) {
            case GENERIC -> Generic.class;
            case NURSERY -> Nursery.class;
            case PRIMARY -> Primary.class;
            case SECONDARY -> Secondary.class;
            case COLLEGE -> College.class;
            case UNIVERSITY -> University.class;
        };
        return entity.getData().mapTo(typeClass);
    }

    @InheritInverseConfiguration
    @Mapping(target = "data", expression = "java( SchoolMetadataMapper.asData(domain) )")
    @Mapping(target = "type", expression = "java( domain.type() )")
    @Mapping(target = "numberOfStudents", expression = "java( domain.numberOfStudents() )")
    @Mapping(target = "applicationNumberPrefix",
            expression = "java( domain.applicationNumberPrefix() )")
    SchoolMetadataEntity asEntity(Metadata domain);

    @Override
    List<Metadata> asDomainObjects(List<SchoolMetadataEntity> entities);

    @Override
    List<SchoolMetadataEntity> asEntities(List<Metadata> list);

    static JsonObject asData(Metadata domain) {
        return JsonObject.mapFrom(domain);
    }
}
