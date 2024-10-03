package io.pinnacl.academics.school.data.persistence;

import io.pinnacl.commons.data.persistence.BaseEntity;
import io.vertx.core.json.JsonObject;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tuition_fees")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class TuitionFeeEntity extends BaseEntity {

    @Column
    private String name;

    @Type(io.pinnacl.commons.data.persistence.JsonB.class)
    @Column(name = "amount", columnDefinition = "jsonb")
    private JsonObject amount;
}
