package io.pinnacl.academics.admissions.data.domain;

import io.pinnacl.commons.features.forms.data.domain.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AdmissionQuestionAnswer<T>(UUID id, @NotNull Type type, @NotBlank String name,
                                         String description, boolean required,
                                         @NotNull @NotEmpty Set<T> answer, Boolean deleted,
                                         Integer revision, LocalDateTime createdOn,
                                         LocalDateTime updatedOn, UUID createdBy, UUID updatedBy,
                                         UUID ownerId, String hash)
                                     implements Question<T> {
}
