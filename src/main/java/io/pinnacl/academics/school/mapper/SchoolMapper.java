package io.pinnacl.core.education.school.mapper;

import io.pinnacl.commons.data.mapper.JsonMapper;
import io.pinnacl.core.education.school.data.domain.School;
import io.pinnacl.core.education.school.data.persistence.SchoolEntity;
import io.pinnacl.core.organisation.data.domain.ImageObject;
import io.pinnacl.core.organisation.mapper.PostalAddressMapper;
import io.vertx.core.json.JsonObject;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
@Mapper(imports = {
        Objects.class, JsonMapper.class, String.class
}, uses = {
        TermMapper.class, TuitionFeeMapper.class, PostalAddressMapper.class
})
public interface SchoolMapper extends io.pinnacl.commons.data.mapper.Mapper<School, SchoolEntity> {
    SchoolMapper INSTANCE = Mappers.getMapper(SchoolMapper.class);

    @Mappings({
            @Mapping(source = "version", target = "revision"),
            @Mapping(target = "logo", expression = "java( SchoolMapper.toLogo(entity) )"),
            @Mapping(target = "socialLinks",
                    expression = "java( JsonMapper.fromJArray(entity.getSocialLinks(), URL.class) )"),
            @Mapping(target = "contactInformation",
                    expression = "java( JsonMapper.fromJsonObject(entity.getContactInformation(), ContactPoint.class) )"),
            @Mapping(target = "banner", expression = "java( SchoolMapper.toBanner(entity) )")
    })
    School asDomainObject(SchoolEntity entity);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "logo", expression = "java( SchoolMapper.fromLogo(domain) )"),
            @Mapping(target = "socialLinks",
                    expression = "java( JsonMapper.toJArray(domain.socialLinks()) )"),
            @Mapping(target = "contactInformation",
                    expression = "java( JsonMapper.toJsonObject(domain.contactInformation()) )"),
            @Mapping(target = "banner", expression = "java( SchoolMapper.fromBanner(domain) )")
    })
    SchoolEntity asEntity(School domain);

    @Override
    List<School> asDomainObjects(List<SchoolEntity> entities);

    @Override
    List<SchoolEntity> asEntities(List<School> list);

    static ImageObject toLogo(SchoolEntity entity) {
        if (Objects.isNull(entity.getLogo())) {
            return null;
        }
        return JsonMapper.fromJsonObject(entity.getLogo(), ImageObject.class);
    }

    static JsonObject fromLogo(School domainObject) {
        if (Objects.isNull(domainObject.logo())) {
            return null;
        }
        return JsonMapper.toJsonObject(domainObject.logo());
    }

    static JsonObject fromBanner(School domainObject) {
        return JsonMapper.toJsonObject(domainObject.banner());
    }

    static ImageObject toBanner(SchoolEntity entity) {
        return JsonMapper.fromJsonObject(entity.getBanner(), ImageObject.class);
    }
}
