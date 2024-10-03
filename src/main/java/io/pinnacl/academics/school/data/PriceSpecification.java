package io.pinnacl.academics.school.data;

public record PriceSpecification(Double price, String priceCurrency)
                                implements io.pinnacl.commons.data.domain.PriceSpecification {
}
