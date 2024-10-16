package io.pinnacl.academics.admissions.data.persistence;

import io.pinnacl.commons.data.persistence.BaseEntity;
import io.pinnacl.commons.features.forms.data.domain.Question;
import io.vertx.core.json.JsonArray;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "admissions_question_answers")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class AdmissionQuestionAnswerEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column
    private Question.Type type;

    @Column
    private String name;

    @Type(io.pinnacl.commons.data.persistence.JsonBArray.class)
    @Column(name = "answer", columnDefinition = "jsonb")
    private JsonArray answer;
}
