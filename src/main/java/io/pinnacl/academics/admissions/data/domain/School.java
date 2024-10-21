package io.pinnacl.academics.admissions.data.domain;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record School(@NotNull UUID id, String name) {
}
