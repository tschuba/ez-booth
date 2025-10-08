package tschuba.ez.booth.ui.i18n;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
@JsonDeserialize(builder = I18NFormat.Builder.class)
public class I18NFormat {
    private String currencyCode;
    private String dateTime;
    private String date;
    private String decimal;

    private I18NFormat() {
    }

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private final I18NFormat format = new I18NFormat();

        public Builder() {
        }

        public Builder setCurrencyCode(String currencyCode) {
            this.format.currencyCode = currencyCode;
            return this;
        }

        public Builder setDateTime(String dateTimeFormat) {
            this.format.dateTime = dateTimeFormat;
            return this;
        }

        public Builder setDate(String dateFormat) {
            this.format.date = dateFormat;
            return this;
        }

        public Builder setDecimal(String decimalFormat) {
            this.format.decimal = decimalFormat;
            return this;
        }

        public I18NFormat build() {
            requireNonNull(format.currencyCode, "currencyCode must not be null!");
            requireNonNull(format.dateTime, "dateTimeFormat must not be null!");
            requireNonNull(format.date, "dateFormat must not be null!");
            requireNonNull(format.decimal, "decimalFormat must not be null!");
            return format;
        }
    }
}