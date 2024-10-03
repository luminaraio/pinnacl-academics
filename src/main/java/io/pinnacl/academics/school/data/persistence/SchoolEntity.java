package io.pinnacl.academics.school.data.persistence;

import io.pinnacl.commons.data.persistence.BaseEntity;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "schools")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class SchoolEntity extends BaseEntity {

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "principal_name")
    private String principalName;

    @Column(name = "number_of_students")
    private Integer numberOfStudents;

    @Column(name = "number_of_boys")
    private Integer numberOfBoys;

    @Column(name = "number_of_girls")
    private Integer numberOfGirls;

    @Column(name = "type")
    private String type;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "contact_point", columnDefinition = "jsonb")
    private JsonObject contactInformation;

    @Type(io.pinnacl.commons.data.persistence.JsonBArray.class)
    @Column(name = "social_links", columnDefinition = "jsonb")
    private JsonArray socialLinks;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "logo", columnDefinition = "jsonb")
    private JsonObject logo;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "banner", columnDefinition = "jsonb")
    private JsonObject banner;

    // @OneToOne(targetEntity = AddressEntity.class, cascade = {
    // CascadeType.PERSIST, CascadeType.MERGE
    // })
    // @JoinTable(name = "_link_school_and_addresses", joinColumns = @JoinColumn(name =
    // "school_id"),
    // inverseJoinColumns = @JoinColumn(name = "address_id"))
    // private AddressEntity address;

    @OneToMany(targetEntity = TuitionFeeEntity.class, orphanRemoval = true, cascade = {
            CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "_link_school_and_tuition_fee", joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "tuition_fee_id"))
    private List<TuitionFeeEntity> tuitionFees;

    @OneToMany(targetEntity = TermEntity.class, orphanRemoval = true, cascade = {
            CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "_link_school_and_term", joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "term_id"))
    private List<TermEntity> terms;


}
