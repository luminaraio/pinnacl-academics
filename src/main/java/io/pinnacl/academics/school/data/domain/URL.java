package io.pinnacl.academics.school.data.domain;

import jakarta.validation.constraints.NotEmpty;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
public record URL(@org.hibernate.validator.constraints.URL @NotEmpty String url, String altText) {
}
