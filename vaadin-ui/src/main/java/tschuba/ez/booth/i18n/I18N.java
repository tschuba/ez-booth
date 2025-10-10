/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.i18n;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.concat;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.internal.LocaleUtil;
import com.vaadin.flow.server.VaadinSession;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

public class I18N implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(I18N.class);

    @Getter private final Config config;

    public I18N(@NonNull Config config) {
        this.config = config;
    }

    public static I18N current() {
        return requireNonNull(
                VaadinSession.getCurrent().getAttribute(I18N.class),
                "No I18N instance assigned to current session!");
    }

    public String getTranslation(Object key, Object... params) {
        return getTranslation(key, currentLocale(), params);
    }

    public String getTranslation(Locale locale, Object key, Object... params) {
        return getTranslation(key, locale, params);
    }

    public String getTranslation(Object key, Locale locale, Object... params) {
        String translationKey;
        if (key instanceof Enum<?>) {
            translationKey = "%s.%s".formatted(key.getClass().getSimpleName(), key.toString());
        } else {
            translationKey = key.toString();
        }
        Set set = config.localeSet(locale);
        String text = set.texts().get(translationKey);
        if (text == null) {
            return "I18N[%s|%s]".formatted(locale, translationKey);
        }
        return text.formatted(params);
    }

    private static Locale currentLocale() {
        return LocaleUtil.getLocale(LocaleUtil::getI18NProvider);
    }

    public static String translate(Object key, Object... params) {
        return translate(key, currentLocale(), params);
    }

    public static String translate(Locale locale, Object key, Object... params) {
        return translate(key, locale, params);
    }

    public static String translate(Object key, Locale locale, Object... params) {
        return current().getTranslation(key, locale, params);
    }

    public static void translate(DatePicker datePicker) {
        Locale locale = datePicker.getLocale();
        I18N.LocaleFormat format = I18N.format(locale);
        Calendar calendar = Calendar.getInstance(locale);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        List<String> monthNames =
                stream(Month.values()).map(month -> translate(month, locale)).toList();

        List<DayOfWeek> weekdays =
                concat(
                                Stream.of(DayOfWeek.SUNDAY),
                                stream(DayOfWeek.values()).takeWhile(not(DayOfWeek.SUNDAY::equals)))
                        .toList();
        List<String> weekdayNames =
                concat(
                                range(firstDayOfWeek, 7).mapToObj(weekdays::get),
                                range(0, firstDayOfWeek).mapToObj(weekdays::get))
                        .map(day -> translate(day, locale))
                        .toList();
        List<String> weekdayNamesShort =
                weekdayNames.stream().map(day -> StringUtils.left(day, 2)).toList();

        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setMonthNames(monthNames);
        i18n.setWeekdays(weekdayNames);
        i18n.setWeekdaysShort(weekdayNamesShort);
        i18n.setToday(translate(DefaultKeys.DatePicker.TODAY, locale));
        i18n.setCancel(translate(DefaultKeys.DatePicker.CANCEL, locale));
        i18n.setDateFormat(format.format().dateFormat());
        datePicker.setI18n(i18n);
    }

    public static LocaleFormat format(@NonNull Locale locale) {
        Format format = current().config.localeSet(locale).format();
        return new LocaleFormat(format, locale);
    }

    public static Currency currency(@NonNull Locale locale) {
        Format format = current().config.localeSet(locale).format();
        return Currency.getInstance(format.currencyCode());
    }

    public static TextKey textKey(@NonNull String key) {
        return new TextKey(key);
    }

    public static class Config {

        private static final ObjectMapper OBJECT_MAPPER =
                new ObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        private final Map<Locale, Set> sets = new HashMap<>();
        private final URI rootPath;
        @Getter private final Set defaultSet;
        @Getter private final Locale defaultLocale;
        @Getter private final Mapping mapping;

        private Config(URI rootPath, Set defaultSet, Locale defaultLocale, Mapping mapping) {
            this.rootPath = rootPath;
            this.defaultSet = defaultSet;
            this.defaultLocale = defaultLocale;
            this.mapping = mapping;
        }

        public static Config parse(@NonNull String mappingFile, @NonNull ClassLoader classLoader) {
            URL mappingFileUrl = classLoader.getResource(mappingFile);
            if (mappingFileUrl == null) {
                throw new IllegalArgumentException("Resource %s not found!".formatted(mappingFile));
            }
            return parse(mappingFileUrl);
        }

        public static Config parse(@NonNull URL mappingFile) {
            try {
                URI rootPath = parentPath(mappingFile);
                LOGGER.debug("Root path for i18n files: {}", rootPath);
                Mapping mapping = OBJECT_MAPPER.readValue(mappingFile, Mapping.class);
                Locale defaultLocale = parseLocaleString(mapping.defaultLocale());
                Set defaultSet =
                        loadSet(defaultLocale, mapping, rootPath)
                                .orElseThrow(
                                        () ->
                                                new IllegalStateException(
                                                        "No mapping found for default locale"
                                                                + " (\"\")"));
                return new Config(rootPath, defaultSet, defaultLocale, mapping);
            } catch (IOException ex) {
                throw new RuntimeException(
                        "Initialization of %s failed!".formatted(I18N.class), ex);
            }
        }

        private static URI parentPath(URL file) {
            String filePath = file.getPath();
            String parentPath = filePath.substring(0, filePath.lastIndexOf('/'));
            return URI.create(
                    "%s://%s%s".formatted(file.getProtocol(), file.getHost(), parentPath));
        }

        private static Locale parseLocaleString(String localeString) {
            String[] parts = localeString.split("_");
            Locale.Builder builder = new Locale.Builder();
            if (parts.length > 2) {
                builder.setVariant(parts[2]);
            }
            if (parts.length > 1) {
                builder.setRegion(parts[1]);
            }
            if (parts.length > 0) {
                builder.setLanguage(parts[0]);
            }
            return builder.build();
        }

        private static Optional<URI> localeFilePath(Locale locale, Mapping mapping, URI rootPath) {
            final String localeString = (locale != null) ? locale.toString() : "";
            LOGGER.debug("Searching mapping for locale '{}'", localeString);
            return mapping.supportedLocales().entrySet().stream()
                    .filter(entry -> equalsIgnoreCase(entry.getKey(), localeString))
                    .map(Map.Entry::getValue)
                    .map(
                            fileName -> {
                                String urlPath =
                                        String.format("%s/%s", rootPath.getPath(), fileName);
                                return UriComponentsBuilder.fromUri(rootPath)
                                        .replacePath(urlPath)
                                        .build()
                                        .toUri();
                            })
                    .findFirst();
        }

        private static Optional<Set> loadSet(Locale locale, Mapping mapping, URI rootPath)
                throws IOException {
            Optional<URI> resourcePath = localeFilePath(locale, mapping, rootPath);
            LOGGER.debug("Loading mapping for locale '{}' from '{}'", locale, resourcePath);
            if (resourcePath.isPresent()) {
                URL resource = resourcePath.get().toURL();
                Set set = OBJECT_MAPPER.readValue(resource, Set.class);
                return Optional.of(set);
            }
            return Optional.empty();
        }

        private Set localeSet(Locale locale) {
            Locale language = new Locale.Builder().setLanguage(locale.getLanguage()).build();
            if (!sets.containsKey(locale)) {
                if (!locale.getCountry().isEmpty()) {
                    if (sets.containsKey(language)) {
                        return sets.get(language);
                    }
                }
            }

            try {
                Locale lookup =
                        Locale.lookup(
                                Locale.LanguageRange.parse(locale.toLanguageTag()),
                                providedLocales());
                if (lookup == null) {
                    return getDefaultSet();
                }
                Optional<Set> set = Optional.ofNullable(sets.get(lookup));
                if (set.isEmpty()) {
                    try {
                        set = loadSet(lookup);
                    } catch (IOException ex) {
                        LOGGER.warn(
                                String.format("Failed to load set file for locale %s", lookup), ex);
                    }
                    set.ifPresentOrElse(
                            i18NSet -> sets.put(locale, i18NSet),
                            () -> sets.put(locale, getDefaultSet()));
                }
                return set.orElseThrow(
                        () -> new IllegalStateException("Experiencing an unexpected condition!"));
            } catch (Exception ex) {
                throw new RuntimeException(
                        String.format("Failed to load I18N set for %s", locale), ex);
            }
        }

        private Optional<Set> loadSet(Locale locale) throws IOException {
            Optional<URI> resourcePath = localeFilePath(locale);
            if (resourcePath.isPresent()) {
                URL resource = resourcePath.get().toURL();
                Set set = OBJECT_MAPPER.readValue(resource, Set.class);
                return Optional.of(set);
            }
            return Optional.empty();
        }

        public List<Locale> providedLocales() {
            return mapping.supportedLocales().keySet().stream()
                    .map(Locale::forLanguageTag)
                    .toList();
        }

        private Optional<URI> localeFilePath(Locale locale) {
            final String localeString = (locale != null) ? locale.toString() : "";
            return mapping.supportedLocales().entrySet().stream()
                    .filter(entry -> equalsIgnoreCase(entry.getKey(), localeString))
                    .map(Map.Entry::getValue)
                    .map(
                            fileName -> {
                                String urlPath =
                                        String.format("%s/%s", rootPath.getPath(), fileName);
                                return UriComponentsBuilder.fromUri(rootPath)
                                        .replacePath(urlPath)
                                        .build()
                                        .toUri();
                            })
                    .findFirst();
        }
    }

    @Jacksonized
    @Builder
    private record Mapping(
            @NonNull String defaultLocale, @NonNull Map<String, String> supportedLocales) {}

    @Jacksonized
    @Builder
    public record Format(
            @NonNull String currencyCode,
            @NonNull @JsonProperty("dateTime") String dateTimeFormat,
            @NonNull @JsonProperty("date") String dateFormat,
            @NonNull @JsonProperty("decimal") String decimalFormat) {

        public java.text.Format currency(Locale locale) {
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            format.setCurrency(Currency.getInstance(currencyCode()));
            return format;
        }

        public java.text.Format decimalNumber(Locale locale) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            return new DecimalFormat(decimalFormat(), symbols);
        }

        public DateTimeFormatter dateTime(Locale locale) {
            return DateTimeFormatter.ofPattern(dateTimeFormat(), locale);
        }

        public String dateTime(LocalDateTime dateTime, Locale locale) {
            return dateTime(locale).format(dateTime);
        }

        public DateTimeFormatter date(Locale locale) {
            return DateTimeFormatter.ofPattern(dateFormat(), locale);
        }

        public String date(LocalDate date, Locale locale) {
            return date(locale).format(date);
        }

        public String currency(Number value, Locale locale) {
            return currency(locale).format(value);
        }

        public String decimalNumber(Number value, Locale locale) {
            return decimalNumber(locale).format(value);
        }
    }

    public record LocaleFormat(@NonNull Format format, @NonNull Locale locale) {

        public java.text.Format currency() {
            return format.currency(locale);
        }

        public java.text.Format decimalNumber() {
            return format.decimalNumber(locale);
        }

        public DateTimeFormatter dateTime() {
            return format.dateTime(locale);
        }

        public String dateTime(LocalDateTime dateTime) {
            return format.dateTime(dateTime, locale);
        }

        public DateTimeFormatter date() {
            return format.date(locale);
        }

        public String date(LocalDate date) {
            return format.date(locale).format(date);
        }

        public String currency(Number value) {
            return format.currency(value, locale);
        }

        public String decimalNumber(Number value) {
            return format.decimalNumber(locale).format(value);
        }
    }

    @Jacksonized
    @Builder
    private record Set(@NonNull I18N.Format format, @NonNull Map<String, String> texts) {}

    @Builder
    public record TextKey(@NonNull String key) {}

    @Component
    @RequiredArgsConstructor
    public static class Provider implements I18NProvider {

        private final I18N i18N;

        @Override
        public List<Locale> getProvidedLocales() {
            return i18N.getConfig().providedLocales();
        }

        @Override
        public String getTranslation(String key, Locale locale, Object... objects) {
            return i18N.getTranslation(key, locale, objects);
        }

        @Override
        public String getTranslation(Object key, Locale locale, Object... params) {
            return i18N.getTranslation(key, locale, params);
        }
    }
}
