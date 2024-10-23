package io.pinnacl.academics.school.data.domain.metadata;

import io.pinnacl.academics.data.domain.AdmissionsConfig;
import io.pinnacl.academics.school.data.SchoolType;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.UUID;

public record Secondary(UUID id, SchoolType type, String principalName, Integer numberOfStudents,
                        @Valid AdmissionsConfig admissionsConfig, Boolean deleted, Integer revision,
                        LocalDateTime createdOn, LocalDateTime updatedOn, UUID createdBy,
                        UUID updatedBy, UUID ownerId, String hash)
                       implements Metadata {

    @Override
    public SchoolType type() {
        return SchoolType.SECONDARY;
    }

    @Override
    public Metadata withAdmissionsConfig(AdmissionsConfig admissionsConfig) {
        return new Secondary(id, type(), principalName, numberOfStudents, admissionsConfig, deleted,
                revision, createdOn, updatedOn, createdBy, updatedBy, ownerId, hash);
    }
}
