package io.pinnacl.academics.application.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    INCOMPLETE("INCOMPLETE"),
    PENDING("PENDING"),
    ADMITTED("ADMITTED"),
    REJECTED("REJECTED");

    public final String value;

    private Status(String value) {
        this.value = value;
    }

    @JsonValue
    public String Status() {
        return this.value;
    }
}
