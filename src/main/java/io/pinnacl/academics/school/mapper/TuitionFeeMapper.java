package io.pinnacl.core.education.school.mapper;

import io.pinnacl.commons.data.mapper.JsonMapper;
import io.pinnacl.core.education.school.data.domain.TuitionFee;
import io.pinnacl.core.education.school.data.persistence.TuitionFeeEntity;
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
})
public interface TuitionFeeMapper extends
                                  io.pinnacl.commons.data.mapper.Mapper<TuitionFee, TuitionFeeEntity> {
    TuitionFeeMapper INSTANCE = Mappers.getMapper(TuitionFeeMapper.class);

    @Mappings({
            @Mapping(source = "version", target = "revision"), @Mapping(target = "amount",
                    expression = "java( JsonMapper.fromJsonObject(entity.getAmount(), PriceSpecification.class) )")
    })
    TuitionFee asDomainObject(TuitionFeeEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "amount", expression = "java( JsonMapper.toJsonObject(domain.amount()) )")
    TuitionFeeEntity asEntity(TuitionFee domain);

    @Override
    List<TuitionFee> asDomainObjects(List<TuitionFeeEntity> entities);

    @Override
    List<TuitionFeeEntity> asEntities(List<TuitionFee> list);
}
