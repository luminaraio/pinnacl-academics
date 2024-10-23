package io.pinnacl.academics.school.data.domain.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.pinnacl.academics.data.domain.AdmissionsConfig;
import io.pinnacl.academics.school.data.SchoolType;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.UUID;

public record Generic(UUID id, SchoolType type, String principalName, Integer numberOfStudents,
                      @Valid AdmissionsConfig admissionsConfig, Integer numberOfMaleStudents,
                      Integer numberOfFemaleStudents, Integer numberOfOtherStudents,
                      Boolean deleted, Integer revision, LocalDateTime createdOn,
                      LocalDateTime updatedOn, UUID createdBy, UUID updatedBy, UUID ownerId,
                      String hash)
                     implements Metadata {

    @Override
    public SchoolType type() {
        return SchoolType.GENERIC;
    }

}
