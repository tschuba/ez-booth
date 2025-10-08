/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.i18n;

import static java.util.Objects.requireNonNull;

import com.vaadin.flow.server.VaadinSession;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utility class to format numbers and dates according to the current locale.
 */
public class Formats {
    private final I18N i18N;

    public Formats(I18N i18N) {
        requireNonNull(i18N, "i18N must not be null!");
        this.i18N = i18N;
    }

    public static Formats of(I18N i18N) {
        return new Formats(i18N);
    }

    public static Formats formats() {
        return requireNonNull(
                VaadinSession.getCurrent().getAttribute(Formats.class),
                "No formats instance assigned to current session!");
    }

    private Locale getDefaultLocale() {
        return VaadinSession.getCurrent().getLocale();
    }

    private I18NFormat getFormat(Locale locale) {
        return i18N.getFormat(locale);
    }

    public Format currency(Locale locale) {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setCurrency(i18N.getCurrency(locale));
        return format;
    }

    public Format decimalNumber(Locale locale) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        return new DecimalFormat(getFormat(locale).getDecimal(), symbols);
    }

    public DateTimeFormatter dateTime(Locale locale) {
        return DateTimeFormatter.ofPattern(getFormat(locale).getDateTime(), locale);
    }

    public String dateTime(LocalDateTime dateTime) {
        return dateTime(dateTime, getDefaultLocale());
    }

    public String dateTime(LocalDateTime dateTime, Locale locale) {
        return dateTime(locale).format(dateTime);
    }

    public DateTimeFormatter date() {
        return date(getDefaultLocale());
    }

    public DateTimeFormatter date(Locale locale) {
        return DateTimeFormatter.ofPattern(getFormat(locale).getDate(), locale);
    }

    public String date(LocalDate date, Locale locale) {
        return date(locale).format(date);
    }

    public String date(LocalDate date) {
        return date(date, getDefaultLocale());
    }

    public String currency(Number value) {
        return currency(value, getDefaultLocale());
    }

    public String currency(Number value, Locale locale) {
        return currency(locale).format(value);
    }

    public String decimalNumber(Number value) {
        return decimalNumber(getDefaultLocale()).format(value);
    }

    public String decimalNumber(Number value, Locale locale) {
        return decimalNumber(locale).format(value);
    }
}
