package io.pinnacl.academics.school.data.domain;

import io.pinnacl.commons.data.domain.Domain;
import io.pinnacl.commons.data.domain.base.PriceSpecification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record TuitionFee(UUID id, @NotBlank String name, @NotNull PriceSpecification amount,
                         Boolean deleted, Integer revision, LocalDateTime createdOn,
                         LocalDateTime updatedOn, UUID createdBy, UUID updatedBy, UUID ownerId,
                         String hash)
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
