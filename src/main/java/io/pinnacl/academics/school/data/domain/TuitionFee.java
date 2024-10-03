package io.pinnacl.core.education.school.data.domain;

import io.pinnacl.commons.data.domain.Domain;
import io.pinnacl.core.education.school.data.PriceSpecification;

import java.time.LocalDateTime;
import java.util.UUID;

public record TuitionFee(UUID id, String name, PriceSpecification amount, Boolean deleted,
                         Integer revision, LocalDateTime createdOn, LocalDateTime updatedOn,
                         UUID createdBy, UUID updatedBy, UUID ownerId, String hash)
                        implements Domain {
    @Override
    public String description() {
        return null;
    }

    @Override
    public String alternateName() {
        return null;
    }
}
