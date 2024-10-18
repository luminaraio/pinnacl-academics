package io.pinnacl.academics.admissions.data.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.pinnacl.academics.school.data.SchoolType;
import io.pinnacl.academics.school.data.domain.SchoolQuestion;
import io.pinnacl.commons.data.domain.Domain;

import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GenericSchoolAdmission.class, name = "SCHOOL"),
        @JsonSubTypes.Type(value = NurseryAdmission.class, name = "PRESCHOOL"),
        @JsonSubTypes.Type(value = PrimaryAdmission.class, name = "ELEMENTARY_SCHOOL"),
        @JsonSubTypes.Type(value = SecondaryAdmission.class, name = "MIDDLE_SCHOOL"),
        @JsonSubTypes.Type(value = CollegeAdmission.class, name = "HIGH_SCHOOL"),
        @JsonSubTypes.Type(value = UniversityAdmission.class, name = "COLLEGE_OR_UNIVERSITY")
})
public sealed interface Metadata extends Domain permits GenericSchoolAdmission, NurseryAdmission,
        PrimaryAdmission, SecondaryAdmission, CollegeAdmission, UniversityAdmission {

    SchoolType type();

    default String name() {
        return null;
    }

    default String description() {
        return null;
    }

    default String alternateName() {
        return null;
    }

    default Set<SchoolQuestion> extraQuestions() {
        return Set.of();
    }
}
