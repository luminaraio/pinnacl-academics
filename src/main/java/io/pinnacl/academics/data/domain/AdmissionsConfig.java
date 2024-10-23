package io.pinnacl.academics.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Objects;

public record AdmissionsConfig(@Size(min = 2, max = 4) String applicationNumberPrefix,
                               Integer applicationNumberPrefixLength,
                               Integer applicationNumberPostfixLength, Boolean alphaNumeric) {

    public static int DEFAULT_APPLICATION_NUMBER_PREFIX = 4;
    public static int DEFAULT_APPLICATION_NUMBER_POSTFIX = 8;

    public static AdmissionsConfig pinnaclDefaults() {
        return new AdmissionsConfig(null, DEFAULT_APPLICATION_NUMBER_PREFIX,
                DEFAULT_APPLICATION_NUMBER_POSTFIX, true);
    }

    @Override
    @JsonProperty
    public String applicationNumberPrefix() {
        return StringUtils.isNotBlank(applicationNumberPrefix)
                ? applicationNumberPrefix.toUpperCase(Locale.ROOT) : null;
    }

    @Override
    @JsonProperty
    public Integer applicationNumberPrefixLength() {
        return StringUtils.isNotBlank(applicationNumberPrefix) ? applicationNumberPrefix.length()
                : applicationNumberPrefixLength;
    }

    @Override
    @JsonProperty
    public Integer applicationNumberPostfixLength() {
        return Objects.isNull(applicationNumberPostfixLength) ? DEFAULT_APPLICATION_NUMBER_POSTFIX
                : applicationNumberPostfixLength;
    }

    @Override
    @JsonProperty
    public Boolean alphaNumeric() {
        return Objects.isNull(alphaNumeric) || alphaNumeric;
    }

    @JsonIgnore
    public String generateApplicationNumber() {
        var prefix = StringUtils.isBlank(applicationNumberPrefix()) ? RandomStringUtils.secure()
                .nextAlphabetic(DEFAULT_APPLICATION_NUMBER_PREFIX)
                .toUpperCase(Locale.ROOT) : applicationNumberPrefix();
        String postfix = RandomStringUtils.secure()
                .nextAlphabetic(DEFAULT_APPLICATION_NUMBER_PREFIX)
                .toUpperCase(Locale.ROOT);
        if (alphaNumeric()) {
            postfix =
                    RandomStringUtils.secure().nextAlphanumeric(DEFAULT_APPLICATION_NUMBER_PREFIX);
        }
        return "%s-%s".formatted(prefix, postfix).toUpperCase(Locale.ROOT);
    }

    @JsonIgnore
    public AdmissionsConfig assignPrefix() {
        var prefix = RandomStringUtils.secure()
                .nextAlphabetic(DEFAULT_APPLICATION_NUMBER_PREFIX)
                .toUpperCase(Locale.ROOT);
        return new AdmissionsConfig(prefix, applicationNumberPrefixLength,
                applicationNumberPostfixLength, alphaNumeric);
    }
}
