package io.pinnacl.academics.school.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SchoolType {
    GENERIC("SCHOOL"),
    NURSERY("PRESCHOOL"),
    PRIMARY("ELEMENTARY_SCHOOL"),
    SECONDARY("MIDDLE_SCHOOL"),
    COLLEGE("HIGH_SCHOOL"),
    UNIVERSITY("COLLEGE_OR_UNIVERSITY");

    private final String _type;

    SchoolType(String type) {
        _type = type;
    }

    @JsonValue
    public String type() {
        return _type;
    }
}
