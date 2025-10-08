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
@JsonDeserialize(builder = I18NSet.Builder.class)
public class I18NSet {
    private I18NFormat format;
    private Map<String, String> texts;

    private I18NSet() {}

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private final I18NSet set = new I18NSet();

        private Builder() {}

        public Builder setFormat(I18NFormat format) {
            set.format = format;
            return this;
        }

        public Builder setTexts(Map<String, String> texts) {
            set.texts = texts;
            return this;
        }

        public I18NSet build() {
            requireNonNull(set.format, "format must not be null!");
            requireNonNull(set.texts, "texts must not be null!");
            return set;
        }
    }
}
