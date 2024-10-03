package io.pinnacl.academics.application.mapper;

import io.pinnacl.commons.data.domain.catalog.ImageObject;
import io.pinnacl.commons.data.mapper.JsonMapper;
import io.pinnacl.academics.application.data.domain.Application;
import io.pinnacl.academics.application.data.domain.Documents;
import io.pinnacl.academics.application.data.persistence.ApplicationEntity;
import io.vertx.core.json.JsonObject;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;



/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
@Mapper(imports = {
        Objects.class, JsonMapper.class, String.class
})
public interface ApplicationMapper extends
                                   io.pinnacl.commons.data.mapper.Mapper<Application, ApplicationEntity> {
    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);
    static final Logger CLASS_LOGGER = LoggerFactory.getLogger(ApplicationMapper.class);


    @Mappings({
            @Mapping(source = "version", target = "revision"), @Mapping(target = "documents",
                    expression = "java( ApplicationMapper.toDocuments(entity) )")
    })
    Application asDomainObject(ApplicationEntity entity);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "documents",
                    expression = "java( ApplicationMapper.fromDocuments(domain) )")
    })
    ApplicationEntity asEntity(Application domain);

    @Override
    List<Application> asDomainObjects(List<ApplicationEntity> entities);

    @Override
    List<ApplicationEntity> asEntities(List<Application> list);

    static JsonObject fromDocuments(Application domainObject) {
        return JsonMapper.toJsonObject(domainObject.documents());
    }

    static Documents toDocuments(ApplicationEntity entity) {
        var documents = entity.getDocuments();

        if (Objects.nonNull(documents)) {
            var fieldNames = documents.fieldNames();
            var documentsDomain = new JsonObject();
            for (var field : fieldNames) {
                documentsDomain.put(field, JsonMapper.fromJsonObject(
                        JsonMapper.toJsonObject(documents.getValue(field)), ImageObject.class));
            }
            return JsonMapper.fromJson(documentsDomain, Documents.class).result();
        }
        return null;

    }
}
