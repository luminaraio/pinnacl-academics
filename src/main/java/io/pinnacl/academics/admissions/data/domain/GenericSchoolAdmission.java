package io.pinnacl.academics.admissions.data.domain;

import io.pinnacl.academics.admissions.data.Gender;
import io.pinnacl.academics.admissions.data.Status;
import io.pinnacl.academics.school.data.SchoolType;
import io.pinnacl.academics.school.data.domain.School;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record GenericSchoolAdmission(UUID id, @NotNull SchoolType type, String name,
                                     String applicationNumber, String className, Gender gender,
                                     LocalDateTime dateOfBirth, String guardianName,
                                     String guardianOccupation, String guardianEmail,
                                     String guardianTelephone, String guardianAddress,
                                     Status status, String sickness, String disabilities,
                                     Boolean deleted, Integer revision, LocalDateTime createdOn,
                                     LocalDateTime updatedOn, UUID createdBy, UUID updatedBy,
                                     UUID ownerId, String hash)
                                    implements Metadata {

}
