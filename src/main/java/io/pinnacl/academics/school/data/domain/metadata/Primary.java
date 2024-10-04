package io.pinnacl.academics.school.data.domain.metadata;

import io.pinnacl.academics.school.data.SchoolType;

import java.time.LocalDateTime;
import java.util.UUID;

public record Primary(UUID id, String principalName, Integer numberOfStudents, Boolean deleted,
                      Integer revision, LocalDateTime createdOn, LocalDateTime updatedOn,
                      UUID createdBy, UUID updatedBy, UUID ownerId, String hash)
                     implements Metadata {

    @Override
    public SchoolType type() {
        return SchoolType.PRIMARY;
    }

}
