/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.i18n;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Map;
import lombok.Getter;

@Getter
@JsonDeserialize(builder = I18NMapping.Builder.class)
public class I18NMapping {
    private String defaultLocale;
    private Map<String, String> supportedLocales;

    private I18NMapping() {}

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private final I18NMapping mapping;

        private Builder() {
            this.mapping = new I18NMapping();
        }

        public Builder setDefaultLocale(String defaultLocale) {
            mapping.defaultLocale = defaultLocale;
            return this;
        }

        public Builder setSupportedLocales(Map<String, String> supportedLocales) {
            mapping.supportedLocales = supportedLocales;
            return this;
        }

        public I18NMapping build() {
            requireNonNull(mapping.defaultLocale, "defaultLocale must not be null!");
            requireNonNull(mapping.supportedLocales, "supportedLocales must not be null!");
            if (mapping.supportedLocales.isEmpty()) {
                throw new IllegalStateException("supportedLocales must not be empty!");
            }
            return mapping;
        }
    }
}
