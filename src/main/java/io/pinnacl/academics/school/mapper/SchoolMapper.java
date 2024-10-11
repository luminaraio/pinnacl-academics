package io.pinnacl.academics.school.mapper;

import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.commons.data.domain.base.ContactPoint;
import io.pinnacl.commons.data.domain.base.ImageObject;
import io.pinnacl.commons.data.domain.base.URL;
import io.pinnacl.commons.data.mapper.JsonMapper;
import io.pinnacl.commons.features.traits.mapper.PinnaclTraitMapper;
import io.pinnacl.commons.features.postaladdress.mapper.PostalAddressMapper;
import io.pinnacl.commons.features.sociallinks.mapper.SocialLinkMapper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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
        TermMapper.class, TuitionFeeMapper.class, PinnaclTraitMapper.class,
        SchoolMetadataMapper.class, PostalAddressMapper.class, SocialLinkMapper.class
})
public interface SchoolMapper extends io.pinnacl.commons.data.mapper.Mapper<School, SchoolEntity> {
    SchoolMapper INSTANCE = Mappers.getMapper(SchoolMapper.class);

    @Mapping(source = "version", target = "revision")
    @Mapping(target = "logo", expression = "java( SchoolMapper.fromEntityLogo(entity) )")
    @Mapping(target = "website", expression = "java( SchoolMapper.fromEntityWebsite(entity) )")
    @Mapping(target = "contactPoint",
            expression = "java( SchoolMapper.fromEntityContactPoint(entity) )")
    @Mapping(target = "extraContactPoints",
            expression = "java( SchoolMapper.fromEntityContactPoints(entity) )")
    School asDomainObject(SchoolEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "logo", expression = "java( SchoolMapper.fromDomainLogo(domain) )")
    @Mapping(target = "website", expression = "java( SchoolMapper.fromDomainWebsite(domain) )")
    @Mapping(target = "contactPoint",
            expression = "java( SchoolMapper.fromDomainContactPoint(domain) )")
    @Mapping(target = "extraContactPoints",
            expression = "java( SchoolMapper.fromDomainContactPoints(domain) )")
    SchoolEntity asEntity(School domain);

    @Override
    List<School> asDomainObjects(List<SchoolEntity> entities);

    @Override
    List<SchoolEntity> asEntities(List<School> list);

    static JsonObject fromDomainLogo(School domain) {
        return JsonMapper.toJsonObject(domain.logo());
    }

    static ImageObject fromEntityLogo(SchoolEntity entity) {
        return JsonMapper.fromJsonObject(entity.getLogo(), ImageObject.class);
    }

    static JsonObject fromDomainWebsite(School domain) {
        return JsonMapper.toJsonObject(domain.website());
    }

    static URL fromEntityWebsite(SchoolEntity entity) {
        return JsonMapper.fromJsonObject(entity.getLogo(), URL.class);
    }

    static JsonObject fromDomainContactPoint(School domain) {
        return JsonMapper.toJsonObject(domain.contactPoint());
    }

    static ContactPoint fromEntityContactPoint(SchoolEntity entity) {
        return JsonMapper.fromJsonObject(entity.getContactPoint(), ContactPoint.class);
    }

    static JsonArray fromDomainContactPoints(School domain) {
        return JsonMapper.toJArray(domain.extraContactPoints());
    }

    static List<ContactPoint> fromEntityContactPoints(SchoolEntity entity) {
        return JsonMapper.fromJArray(entity.getExtraContactPoints(), ContactPoint.class);
    }
}
