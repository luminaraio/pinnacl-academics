package io.pinnacl.academics.admissions.data.persistence;

import io.pinnacl.academics.admissions.data.Gender;
import io.pinnacl.academics.school.data.SchoolType;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.commons.data.persistence.BaseEntity;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "admissions_metadata")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class AdmissionMetadataEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column
    private SchoolType type;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "data", columnDefinition = "jsonb")
    private JsonObject data;
}
