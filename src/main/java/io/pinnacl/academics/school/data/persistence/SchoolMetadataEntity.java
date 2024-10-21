package io.pinnacl.academics.school.data.persistence;

import io.pinnacl.academics.school.data.SchoolType;
import io.pinnacl.commons.data.persistence.BaseEntity;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "schools_metadata")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class SchoolMetadataEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private SchoolType type;

    @Column(name = "principal_name")
    private String principalName;

    @Column(name = "number_of_students")
    private Integer numberOfStudents;

    @Column(name = "application_number_prefix")
    private String applicationNumberPrefix;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "data", columnDefinition = "jsonb")
    private JsonObject data;
}
