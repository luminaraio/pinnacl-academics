package io.pinnacl.academics.admissions.data.persistence;

import io.pinnacl.academics.admissions.data.Gender;
import io.pinnacl.academics.admissions.data.Status;
import io.pinnacl.academics.school.data.SchoolType;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.commons.data.persistence.BaseEntity;
import io.pinnacl.commons.features.forms.data.persistence.DocumentEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "admissions")
@Getter
@Setter
@ToString(callSuper = true, exclude = {
        "school", "metadata", "documents", "questionAnswers"
})
@EqualsAndHashCode(callSuper = true, exclude = {
        "school", "metadata", "documents", "questionAnswers"
})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class AdmissionEntity extends BaseEntity {

    @ManyToOne(targetEntity = SchoolEntity.class)
    @JoinTable(name = "_link_schools_and_admissions",
            joinColumns = @JoinColumn(name = "admission_id"),
            inverseJoinColumns = @JoinColumn(name = "school_id"))
    private SchoolEntity school;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column
    private Status status;

    @Column(name = "application_number")
    private String applicationNumber;


    @OneToOne(targetEntity = AdmissionMetadataEntity.class, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    })
    @JoinTable(name = "_link_admissions_and_metadata",
            joinColumns = @JoinColumn(name = "admission_id"),
            inverseJoinColumns = @JoinColumn(name = "metadata_id"))
    private AdmissionMetadataEntity metadata;

    @OneToMany(targetEntity = AdmissionQuestionAnswerEntity.class, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    @JoinTable(name = "_link_admissions_and_question_answers",
            joinColumns = @JoinColumn(name = "admission_id"),
            inverseJoinColumns = @JoinColumn(name = "question_answer_id"))
    private Set<AdmissionQuestionAnswerEntity> questionAnswers;

    @OneToMany(targetEntity = DocumentEntity.class, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    @JoinTable(name = "_link_admissions_and_documents",
            joinColumns = @JoinColumn(name = "admission_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id"))
    private Set<DocumentEntity> documents;
}
