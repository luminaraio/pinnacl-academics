package io.pinnacl.academics.admissions.mapper;

import io.pinnacl.academics.admissions.data.domain.Admission;
import io.pinnacl.academics.admissions.data.domain.School;
import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.commons.features.forms.mapper.DocumentMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;


/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
@Mapper(uses = {
        DocumentMapper.class, AdmissionMetadataMapper.class, AdmissionQuestionAnswerMapper.class
})
public interface AdmissionMapper extends
                                 io.pinnacl.commons.data.mapper.Mapper<Admission, AdmissionEntity> {
    AdmissionMapper INSTANCE = Mappers.getMapper(AdmissionMapper.class);

    @Mapping(source = "version", target = "revision")
    @Mapping(target = "school", expression = "java( AdmissionMapper.asSchoolDomain(entity) )")
    Admission asDomainObject(AdmissionEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "school", expression = "java( AdmissionMapper.asSchoolEntity(domain) )")
    AdmissionEntity asEntity(Admission domain);

    @Override
    List<Admission> asDomainObjects(List<AdmissionEntity> entities);

    @Override
    List<AdmissionEntity> asEntities(List<Admission> list);

    static School asSchoolDomain(AdmissionEntity entity) {
        if (Objects.nonNull(entity.getSchool())) {
            var school = entity.getSchool();
            return new School(school.id(), school.getName());
        }
        return null;
    }

    static SchoolEntity asSchoolEntity(Admission domain) {
        if (Objects.nonNull(domain.school())) {
            return SchoolEntity.builder().id(domain.school().id()).build();
        }
        return null;
    }
}
