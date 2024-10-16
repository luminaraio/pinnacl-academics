package io.pinnacl.academics.admissions.data.domain;

import io.pinnacl.academics.admissions.data.Gender;
import io.pinnacl.academics.school.data.SchoolType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record PrimaryAdmission(UUID id, @NotNull SchoolType type, Map<String, Object> extraMetadata,
                               Boolean deleted, Integer revision, LocalDateTime createdOn,
                               LocalDateTime updatedOn, UUID createdBy, UUID updatedBy,
                               UUID ownerId, String hash)
                              implements Metadata {
}
