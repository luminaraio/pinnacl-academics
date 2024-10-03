package io.pinnacl.academics.school.data.domain;

import java.util.UUID;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
public record ImageObject(UUID id, String name, String caption, String contentSize,
                          URL contentUrl) {

    public static ImageObject fromId(UUID id) {
        return new ImageObject(id, null, null, null, null);
    }
}
