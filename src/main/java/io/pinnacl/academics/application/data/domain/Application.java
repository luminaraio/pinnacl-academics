package io.pinnacl.academics.application.data.domain;

import io.pinnacl.academics.application.data.Gender;
import io.pinnacl.commons.data.domain.Domain;
import io.pinnacl.academics.application.data.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record Application(UUID id, String name, String applicationNumber, String className,
                          Gender gender, LocalDateTime dateOfBirth, String guardianName,
                          String guardianOccupation, String guardianEmail, String guardianTelephone,
                          String guardianAddress, Status status, String sickness,
                          String disabilities, Documents documents, Boolean deleted,
                          Integer revision, LocalDateTime createdOn, LocalDateTime updatedOn,
                          UUID createdBy, UUID updatedBy, UUID ownerId, String hash)
                         implements Domain {
    @Override
    public String description() {
        return "";
    }

    @Override
    public String alternateName() {
        return null;
    }

    public Application withApplicationNumber(Long count) {
        return new Application(id, name, "%s-%s".formatted(className, count + 1), className, gender,
                dateOfBirth, guardianName, guardianOccupation, guardianEmail, guardianTelephone,
                guardianAddress, status, sickness, disabilities, documents, deleted, revision,
                createdOn, updatedOn, createdBy, updatedBy, ownerId, hash);
    }
}
