package io.pinnacl.academics.admissions.mapper;

import io.pinnacl.academics.admissions.data.domain.*;
import io.pinnacl.academics.admissions.data.persistence.AdmissionMetadataEntity;
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
public interface AdmissionMetadataMapper extends
                                         io.pinnacl.commons.data.mapper.Mapper<Metadata, AdmissionMetadataEntity> {
    AdmissionMetadataMapper INSTANCE = Mappers.getMapper(AdmissionMetadataMapper.class);

    default Metadata asDomainObject(AdmissionMetadataEntity entity) {
        var typeClass = switch (entity.getType()) {
            case GENERIC -> GenericSchoolAdmission.class;
            case NURSERY -> NurseryAdmission.class;
            case PRIMARY -> PrimaryAdmission.class;
            case SECONDARY -> SecondaryAdmission.class;
            case COLLEGE -> CollegeAdmission.class;
            case UNIVERSITY -> UniversityAdmission.class;
        };
        return entity.getData().mapTo(typeClass);
    }

    @InheritInverseConfiguration
    @Mapping(target = "data", expression = "java( AdmissionMetadataMapper.asData(domain) )")
    @Mapping(target = "type", expression = "java( domain.type() )")
    AdmissionMetadataEntity asEntity(Metadata domain);

    @Override
    List<Metadata> asDomainObjects(List<AdmissionMetadataEntity> entities);

    @Override
    List<AdmissionMetadataEntity> asEntities(List<Metadata> list);

    static JsonObject asData(Metadata domain) {
        return JsonObject.mapFrom(domain);
    }
}
