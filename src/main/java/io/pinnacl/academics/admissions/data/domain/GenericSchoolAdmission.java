package io.pinnacl.academics.admissions.data.domain;

import io.pinnacl.academics.school.data.SchoolType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record GenericSchoolAdmission(UUID id, SchoolType type, String className,
                                     LocalDateTime dateOfBirth, String guardianName,
                                     String guardianOccupation, String guardianEmail,
                                     String guardianTelephone, String guardianAddress,
                                     Set<String> sickness, Set<String> disabilities,
                                     Map<String, Object> extraMetadata, Boolean deleted,
                                     Integer revision, LocalDateTime createdOn,
                                     LocalDateTime updatedOn, UUID createdBy, UUID updatedBy,
                                     UUID ownerId, String hash)
                                    implements Metadata {

    @Override
    public SchoolType type() {
        return SchoolType.GENERIC;
    }

}
