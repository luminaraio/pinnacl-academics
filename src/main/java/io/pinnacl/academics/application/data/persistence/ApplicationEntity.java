package io.pinnacl.academics.application.data.persistence;

import io.pinnacl.commons.data.persistence.BaseEntity;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ApplicationEntity extends BaseEntity {

    @Column
    private String name;

    @Column(name = "class_name")
    private String className;

    @Column(name = "application_number")
    private String applicationNumber;

    @Column
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Column(name = "guardian_name")
    private String guardianName;

    @Column(name = "guardian_occupation")
    private String guardianOccupation;

    @Column(name = "guardian_email")
    private String guardianEmail;

    @Column(name = "guardian_telephone")
    private String guardianTelephone;

    @Column(name = "guardian_address")
    private String guardianAddress;

    @Column
    private String disabilities;

    @Column
    private String sickness;

    private String status;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "documents", columnDefinition = "jsonb")
    private JsonObject documents;
}
