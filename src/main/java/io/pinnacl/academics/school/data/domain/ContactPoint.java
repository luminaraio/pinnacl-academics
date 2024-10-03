package io.pinnacl.academics.school.data.domain;

import io.pinnacl.commons.data.domain.HoursAvailable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
public record ContactPoint(@NotEmpty String name, @Email String email, String telephone,
                           @Valid HoursAvailable hoursAvailable, String areaServed)
                          implements io.pinnacl.commons.data.domain.ContactPoint {
}
