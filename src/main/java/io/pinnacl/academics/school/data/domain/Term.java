package io.pinnacl.core.education.school.data.domain;

import io.pinnacl.commons.data.domain.Domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Term(UUID id, String name, LocalDateTime startDate, LocalDateTime endDate,
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
