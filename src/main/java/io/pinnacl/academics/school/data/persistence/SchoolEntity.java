package io.pinnacl.academics.school.data.persistence;

import io.pinnacl.academics.school.data.SchoolType;
import io.pinnacl.commons.data.persistence.BaseEntity;
import io.pinnacl.commons.features.traits.data.persistence.PinnaclTraitEntity;
import io.pinnacl.commons.features.postaladdress.data.persistence.PostalAddressEntity;
import io.pinnacl.commons.features.sociallinks.data.persistence.SocialLinkEntity;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "schools")
@Getter
@Setter
@ToString(callSuper = true, exclude = {
        "metadata", "tuitionFees", "terms", "features"
})
@EqualsAndHashCode(callSuper = true, exclude = {
        "metadata", "tuitionFees", "terms", "features"
})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class SchoolEntity extends BaseEntity {

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "alternate_name")
    private String alternateName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private SchoolType type;

    @Column(name = "number_of_employees")
    private Integer numberOfEmployees;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "logo", columnDefinition = "jsonb")
    private JsonObject logo;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "website", columnDefinition = "jsonb")
    private JsonObject website;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "contact_point", columnDefinition = "jsonb")
    private JsonObject contactPoint;

    @Type(io.pinnacl.commons.data.persistence.JsonBArray.class)
    @Column(name = "extra_contact_point", columnDefinition = "jsonb")
    private JsonArray extraContactPoints;

    @OneToOne(targetEntity = PostalAddressEntity.class, orphanRemoval = true, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    })
    @JoinTable(name = "_link_schools_and_addresses", joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private PostalAddressEntity address;

    @OneToOne(targetEntity = SocialLinkEntity.class, orphanRemoval = true, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    })
    @JoinTable(name = "_link_schools_and_social_links",
            joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "social_links_id"))
    private List<SocialLinkEntity> socialLinks;

    @OneToOne(targetEntity = SchoolMetadataEntity.class, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    })
    @JoinTable(name = "_link_schools_and_metadata", joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "metadata_id"))
    private SchoolMetadataEntity metadata;

    @OneToMany(targetEntity = TuitionFeeEntity.class, orphanRemoval = true, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    @JoinTable(name = "_link_schools_and_tuition_fees",
            joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "tuition_fee_id"))
    private List<TuitionFeeEntity> tuitionFees;

    @OneToMany(targetEntity = TermEntity.class, orphanRemoval = true, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    @JoinTable(name = "_link_school_and_term", joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "term_id"))
    private List<TermEntity> terms;

    @OneToMany(targetEntity = PinnaclTraitEntity.class, orphanRemoval = true, cascade = {
            CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    @JoinTable(name = "_link_schools_and_pinnacl_traits",
            joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id"))
    private List<PinnaclTraitEntity> traits;


}
