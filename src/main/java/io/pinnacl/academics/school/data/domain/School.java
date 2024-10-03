package io.pinnacl.academics.school.data.domain;

import io.pinnacl.commons.data.domain.Country;
import io.pinnacl.commons.data.domain.Domain;
import io.pinnacl.academics.school.data.SchoolType;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record School(UUID id, String name, String description, String principalName,
                     Integer numberOfStudents, Integer numberOfBoys, Integer numberOfGirls,
                     SchoolType type, @Valid ContactPoint contactInformation,
                     @Valid List<URL> socialLinks, @Valid ImageObject logo,
                     @Valid ImageObject banner,
                     // @Valid PostalAddress address,
                     List<TuitionFee> tuitionFees, List<Term> terms, Boolean deleted,
                     Integer revision, LocalDateTime createdOn, LocalDateTime updatedOn,
                     UUID createdBy, UUID updatedBy, UUID ownerId, String hash)
                    implements Domain {
    @Override
    public String alternateName() {
        return null;
    }

    // public School withCountry(Country country) {
    // if (Objects.isNull(country) || Objects.isNull(address)) {
    // return this;
    // }
    // return new School(id, name, description, principalName, numberOfStudents, numberOfBoys,
    // numberOfGirls, type, contactInformation, socialLinks, logo, banner,
    // address.withCountry(country), tuitionFees, terms, deleted, revision, createdOn,
    // updatedOn, createdBy, updatedBy, ownerId, null);
    // }
}
