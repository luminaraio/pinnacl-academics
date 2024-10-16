package io.pinnacl.academics.admissions.data.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.pinnacl.academics.admissions.data.Gender;
import io.pinnacl.academics.school.data.SchoolType;
import io.pinnacl.academics.school.data.domain.metadata.*;
import io.pinnacl.commons.data.domain.Domain;
import io.pinnacl.commons.features.forms.data.domain.Question;

import java.util.Map;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Generic.class, name = "SCHOOL"),
        @JsonSubTypes.Type(value = Nursery.class, name = "PRESCHOOL"),
        @JsonSubTypes.Type(value = Primary.class, name = "ELEMENTARY_SCHOOL"),
        @JsonSubTypes.Type(value = Secondary.class, name = "MIDDLE_SCHOOL"),
        @JsonSubTypes.Type(value = College.class, name = "HIGH_SCHOOL"),
        @JsonSubTypes.Type(value = University.class, name = "COLLEGE_OR_UNIVERSITY")
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

    default Set<Question> extraQuestions() {
        return Set.of();
    }
}
