package io.pinnacl.academics.school.data.domain;

import io.pinnacl.academics.school.data.SchoolType;
import io.pinnacl.academics.school.data.domain.metadata.Metadata;
import io.pinnacl.commons.data.domain.Brand;
import io.pinnacl.commons.data.domain.ImageObject;
import io.pinnacl.commons.data.domain.Language;
import io.pinnacl.commons.data.domain.Organisation;
import io.pinnacl.commons.data.domain.base.ContactPoint;
import io.pinnacl.commons.data.domain.base.URL;
import io.pinnacl.commons.features.forms.data.domain.DocumentDefinition;
import io.pinnacl.commons.features.traits.data.domain.PinnaclTrait;
import io.pinnacl.commons.features.postaladdress.data.domain.PostalAddress;
import io.pinnacl.commons.features.sociallinks.data.domain.SocialLink;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record School(UUID id, @NotBlank String name, String description, @NotNull SchoolType type,
                     @Valid List<TuitionFee> tuitionFees, @Valid List<Term> terms,
                     @NotNull @Valid Metadata metadata, String alternateName,
                     @NotNull @Valid ContactPoint contactPoint, @NotNull PostalAddress address,
                     ImageObject logo, @Valid List<SocialLink> socialLinks, @Valid URL website,
                     Integer numberOfEmployees, @Valid Set<SchoolQuestion> extraAdmissionQuestions,
                     @Valid Set<DocumentDefinition> supportingDocuments,
                     @Valid List<ContactPoint> extraContactPoints, List<PinnaclTrait> features,
                     Boolean deleted, Integer revision, LocalDateTime createdOn,
                     LocalDateTime updatedOn, UUID createdBy, UUID updatedBy, UUID ownerId,
                     String hash)
                    implements Organisation {

    @Override
    public String legalName() {
        return name;
    }

    public String email() {
        return Objects.nonNull(contactPoint) ? contactPoint.email() : null;
    }


    public String telephone() {
        return Objects.nonNull(contactPoint) ? contactPoint.telephone() : null;
    }

    @Override
    public <T extends Language> T language() {
        return null;
    }

    @Override
    public <T extends Brand> T brand() {
        return null;
    }

    public School applicationNumberPrefix(String string) {
        return new School(id, name, description, type, tuitionFees, terms,
                metadata.withApplicationNumberPrefix(string), alternateName, contactPoint, address,
                logo, socialLinks, website, numberOfEmployees, extraAdmissionQuestions,
                supportingDocuments, extraContactPoints, features, deleted, revision, createdOn,
                updatedOn, createdBy, updatedBy, ownerId, hash);
    }
}
