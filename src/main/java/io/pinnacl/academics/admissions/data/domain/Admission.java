package io.pinnacl.academics.admissions.data.domain;

import io.pinnacl.academics.admissions.data.Status;
import io.pinnacl.commons.forms.data.domain.Document;
import io.pinnacl.commons.forms.data.domain.Form;
import io.pinnacl.commons.forms.data.domain.FormKind;
import io.pinnacl.academics.school.data.SchoolType;
import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.commons.data.domain.Domain;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record Admission(UUID id, @NotNull SchoolType type, @Valid School school, Status status,
                        @Valid Metadata metadata, @Valid Set<Document> documents, Boolean deleted,
                        Integer revision, LocalDateTime createdOn, LocalDateTime updatedOn,
                        UUID createdBy, UUID updatedBy, UUID ownerId, String hash)
                       implements Domain, Form {

    @Override
    public FormKind kind() {
        return FormKind.ACADEMIC_ADMISSIONS;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public String alternateName() {
        return null;
    }
}
