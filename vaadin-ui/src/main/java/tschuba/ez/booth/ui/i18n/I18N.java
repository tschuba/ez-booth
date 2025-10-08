package tschuba.ez.booth.ui.i18n;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.internal.LocaleUtil;
import com.vaadin.flow.server.VaadinSession;
import lombok.Getter;
import lombok.NonNull;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.concat;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class I18N implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(I18N.class);

    private final Map<Locale, I18NSet> sets = new HashMap<>();
    private final I18NSet defaultSet;
    @Getter
    private final Locale defaultLocale;
    private final I18NMapping mapping;
    private final URL rootPath;

    public I18N(@NonNull ClassLoader classLoader, @NonNull String mappingFile) {
        this(requireNonNull(classLoader.getResource(mappingFile), "Resource %s not found!".formatted(mappingFile)));
    }

    public I18N(@NonNull URL mappingFile) {
        try {
            rootPath = parentPath(mappingFile);
            mapping = load(mappingFile, I18NMapping.class);
            defaultLocale = parseLocaleString(mapping.getDefaultLocale());
            defaultSet = loadSet(defaultLocale).orElseThrow(() -> new IllegalStateException("No mapping found for default locale (\"\")"));
        } catch (IOException ex) {
            throw new RuntimeException(format("Initialization of %s failed!", I18N.class), ex);
        }
    }

    public static I18N i18N() {
        return requireNonNull(VaadinSession.getCurrent().getAttribute(I18N.class),
                "No I18N instance assigned to current session!");
    }

    public static Locale parseLocaleString(String localeString) {
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

    public List<Locale> getProvidedLocales() {
        return mapping.getSupportedLocales().keySet().stream().map(Locale::forLanguageTag).toList();
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
        I18NSet i18NSet = localeSet(locale);
        String text = i18NSet.getTexts().get(translationKey);
        if (text == null) {
            return format("I18N[%s|%s]", locale, translationKey);
        }
        return format(text, params);
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
        return i18N().getTranslation(key, locale, params);
    }

    public static void translate(DatePicker datePicker) {
        Locale locale = datePicker.getLocale();
        I18NFormat format = I18N.i18N().getFormat(locale);
        Calendar calendar = Calendar.getInstance(locale);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        List<String> monthNames = stream(Month.values()).map(month -> translate(month, locale)).toList();

        List<DayOfWeek> weekdays = concat(Stream.of(DayOfWeek.SUNDAY), stream(DayOfWeek.values()).takeWhile(not(DayOfWeek.SUNDAY::equals))).toList();
        List<String> weekdayNames = concat(range(firstDayOfWeek, 7).mapToObj(weekdays::get), range(0, firstDayOfWeek).mapToObj(weekdays::get))
                .map(day -> translate(day, locale)).toList();
        List<String> weekdayNamesShort = weekdayNames.stream().map(day -> StringUtils.left(day, 2)).toList();

        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setMonthNames(monthNames);
        i18n.setWeekdays(weekdayNames);
        i18n.setWeekdaysShort(weekdayNamesShort);
        i18n.setToday(translate(DefaultKeys.DatePicker.TODAY, locale));
        i18n.setCancel(translate(DefaultKeys.DatePicker.CANCEL, locale));
        i18n.setDateFormat(format.getDate());
        datePicker.setI18n(i18n);
    }

    public I18NFormat getFormat() {
        return getFormat(currentLocale());
    }

    public I18NFormat getFormat(Locale locale) {
        return localeSet(locale).getFormat();
    }

    public Currency getCurrency() {
        return getCurrency(currentLocale());
    }

    public Currency getCurrency(Locale locale) {
        I18NFormat format = getFormat(locale);
        return Currency.getInstance(format.getCurrencyCode());
    }

    private I18NSet localeSet(Locale locale) {
        Locale language = new Locale.Builder().setLanguage(locale.getLanguage()).build();
        if (!sets.containsKey(locale)) {
            if (!locale.getCountry().isEmpty()) {
                if (sets.containsKey(language)) {
                    return sets.get(language);
                }
            }
        }

        try {
            Locale lookup = Locale.lookup(Locale.LanguageRange.parse(locale.toLanguageTag()), getProvidedLocales());
            if (lookup == null) {
                return defaultSet;
            }
            Optional<I18NSet> set = Optional.ofNullable(sets.get(lookup));
            if (set.isEmpty()) {
                try {
                    set = loadSet(lookup);
                } catch (IOException ex) {
                    LOGGER.warn(format("Failed to load set file for locale %s", lookup), ex);
                }
                set.ifPresentOrElse(i18NSet -> sets.put(locale, i18NSet), () -> sets.put(locale, defaultSet));
            }
            return set.orElseThrow(() -> new IllegalStateException("Experiencing an unexpected condition!"));
        } catch (Exception ex) {
            throw new RuntimeException(format("Failed to load I18N set for %s", locale), ex);
        }
    }

    private Optional<I18NSet> loadSet(Locale locale) throws IOException {
        Optional<URL> resourcePath = localeFilePath(locale);
        if (resourcePath.isPresent()) {
            URL resource = resourcePath.get();
            I18NSet i18NSet = load(resource, I18NSet.class);
            return Optional.of(i18NSet);
        }
        return Optional.empty();
    }

    private <T> T load(URL file, Class<T> type) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper.readValue(file, type);
    }

    private URL parentPath(URL file) throws MalformedURLException {
        String filePath = file.getPath();
        String parentPath = filePath.substring(0, filePath.lastIndexOf('/'));
        return new URL(file.getProtocol(), file.getHost(), file.getPort(), parentPath);
    }

    private Optional<URL> localeFilePath(Locale locale) {
        final String localeString = (locale != null) ? locale.toString() : "";
        return mapping.getSupportedLocales().entrySet().stream()
                .filter(entry -> equalsIgnoreCase(entry.getKey(), localeString))
                .map(Map.Entry::getValue)
                .map(fileName -> {
                    String urlPath = String.format("%s/%s", rootPath.getPath(), fileName);
                    try {
                        return new URL(rootPath.getProtocol(), rootPath.getHost(), rootPath.getPort(), urlPath);
                    } catch (MalformedURLException ex) {
                        LOGGER.error("Failed to create file path for locale '{}'!", locale, ex);
                        throw new RuntimeException(ex);
                    }
                })
                .findFirst();
    }
}