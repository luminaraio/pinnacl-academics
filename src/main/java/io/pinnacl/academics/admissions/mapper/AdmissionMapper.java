package io.pinnacl.academics.admissions.mapper;

import io.pinnacl.academics.admissions.data.domain.Admission;
import io.pinnacl.academics.admissions.data.persistence.AdmissionEntity;
import io.pinnacl.academics.school.data.domain.metadata.Metadata;
import io.pinnacl.academics.school.mapper.SchoolMapper;
import io.pinnacl.commons.forms.mapper.DocumentMapper;
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
@Mapper(uses = {
        DocumentMapper.class, AdmissionMetadataMapper.class, SchoolMapper.class
})
public interface AdmissionMapper extends
                                 io.pinnacl.commons.data.mapper.Mapper<Admission, AdmissionEntity> {
    AdmissionMapper INSTANCE = Mappers.getMapper(AdmissionMapper.class);

    @Mapping(source = "version", target = "revision")
    Admission asDomainObject(AdmissionEntity entity);

    @InheritInverseConfiguration
    AdmissionEntity asEntity(Admission domain);

    @Override
    List<Admission> asDomainObjects(List<AdmissionEntity> entities);

    @Override
    List<AdmissionEntity> asEntities(List<Admission> list);

    static JsonObject asData(Metadata domainObject) {
        return JsonObject.mapFrom(domainObject);
    }

    // static JsonObject fromDocuments(Admission domainObject) {
    // return JsonMapper.toJsonObject(domainObject.documents());
    // }

    // static Documents toDocuments(AdmissionEntity entity) {
    // var documents = entity.getDocuments();
    //
    // if (Objects.nonNull(documents)) {
    // var fieldNames = documents.fieldNames();
    // var documentsDomain = new JsonObject();
    // for (var field : fieldNames) {
    // documentsDomain.put(field, JsonMapper.fromJsonObject(
    // JsonMapper.toJsonObject(documents.getValue(field)), ImageObject.class));
    // }
    // return JsonMapper.fromJson(documentsDomain, Documents.class).result();
    // }
    // return null;
    // }
}
