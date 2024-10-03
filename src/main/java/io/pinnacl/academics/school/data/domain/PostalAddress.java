package io.pinnacl.academics.school.data.domain;

import io.pinnacl.commons.data.domain.Country;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
public record PostalAddress(UUID id, String streetAddress, String addressLocality,
                            String addressRegion, Country addressCountry, String postalCode,
                            String postOfficeBoxNumber, Boolean deleted, Integer revision,
                            LocalDateTime createdOn, LocalDateTime updatedOn, UUID createdBy,
                            UUID updatedBy, UUID ownerId, String hash)
                           implements io.pinnacl.commons.data.domain.PostalAddress {

    public PostalAddress withCountry(Country aNewCountry) {
        return new PostalAddress(id, streetAddress, addressLocality, addressRegion, aNewCountry,
                postalCode, postOfficeBoxNumber, deleted, revision, createdOn, updatedOn, createdBy,
                updatedBy, ownerId, hash);
    }
}
