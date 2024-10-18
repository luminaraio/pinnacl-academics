package io.pinnacl.academics.admissions.mapper;


import io.pinnacl.academics.admissions.data.domain.AdmissionQuestionAnswer;
import io.pinnacl.academics.admissions.data.persistence.AdmissionQuestionAnswerEntity;
import io.pinnacl.commons.data.mapper.JsonMapper;
import io.vertx.core.json.JsonArray;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
@Mapper
public interface AdmissionQuestionAnswerMapper extends
                                               io.pinnacl.commons.data.mapper.Mapper<AdmissionQuestionAnswer, AdmissionQuestionAnswerEntity> {
    AdmissionQuestionAnswerMapper INSTANCE = Mappers.getMapper(AdmissionQuestionAnswerMapper.class);

    @Mapping(source = "version", target = "revision")
    @Mapping(target = "answers",
            expression = "java( AdmissionQuestionAnswerMapper.fromEntityAnswer(entity) )")
    AdmissionQuestionAnswer asDomainObject(AdmissionQuestionAnswerEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "answers",
            expression = "java( AdmissionQuestionAnswerMapper.fromDomainAnswer(domain) )")
    AdmissionQuestionAnswerEntity asEntity(AdmissionQuestionAnswer domain);

    @Override
    List<AdmissionQuestionAnswer> asDomainObjects(List<AdmissionQuestionAnswerEntity> entities);

    @Override
    List<AdmissionQuestionAnswerEntity> asEntities(List<AdmissionQuestionAnswer> list);

    static JsonArray fromDomainAnswer(AdmissionQuestionAnswer domain) {
        var answers = Objects.nonNull(domain.answers()) ? List.copyOf(domain.answers()) : List.of();
        return JsonMapper.toJArray(answers);
    }

    static Set<Object> fromEntityAnswer(AdmissionQuestionAnswerEntity entity) {
        return Set.copyOf(JsonMapper.fromJArray(entity.getAnswers(), Object.class));
    }


}
