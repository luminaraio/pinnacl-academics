package io.pinnacl.academics.school.mapper;

import io.pinnacl.academics.school.data.domain.TuitionFee;
import io.pinnacl.academics.school.data.persistence.TuitionFeeEntity;
import io.pinnacl.commons.data.domain.base.PriceSpecification;
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
public interface TuitionFeeMapper extends
                                  io.pinnacl.commons.data.mapper.Mapper<TuitionFee, TuitionFeeEntity> {
    TuitionFeeMapper INSTANCE = Mappers.getMapper(TuitionFeeMapper.class);

    @Mapping(source = "version", target = "revision")
    @Mapping(target = "amount",
            expression = "java( TuitionFeeMapper.toPriceSpecification(entity) )")
    TuitionFee asDomainObject(TuitionFeeEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "price",
            expression = "java( TuitionFeeMapper.priceFromPriceSpecification(domain) )")
    @Mapping(target = "priceCurrency",
            expression = "java( TuitionFeeMapper.currencyFromPriceSpecification(domain) )")
    TuitionFeeEntity asEntity(TuitionFee domain);

    @Override
    List<TuitionFee> asDomainObjects(List<TuitionFeeEntity> entities);

    @Override
    List<TuitionFeeEntity> asEntities(List<TuitionFee> list);

    static PriceSpecification toPriceSpecification(TuitionFeeEntity entity) {
        return new PriceSpecification(entity.getPrice(), entity.getPriceCurrency());
    }

    static Double priceFromPriceSpecification(TuitionFee domain) {
        return domain.amount().price();
    }


    static String currencyFromPriceSpecification(TuitionFee domain) {
        return domain.amount().priceCurrency();
    }

}
