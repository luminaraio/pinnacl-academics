package io.pinnacl.academics.application.data.domain;


import lombok.Builder;

@Builder
public record ApplicationAnalytics(Long applications, Long rejected, Long admitted,
                                   Long incomplete) {
}
