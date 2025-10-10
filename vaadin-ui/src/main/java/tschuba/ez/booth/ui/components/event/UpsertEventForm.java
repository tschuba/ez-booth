/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.event;

import static java.math.BigDecimal.ZERO;
import static tschuba.ez.booth.i18n.TranslationKeys.UpsertBoothForm.*;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Predicate;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.Ids;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.ui.Constraints;
import tschuba.ez.booth.ui.util.Notifications;

public class UpsertEventForm extends FormLayout {
    private final TextField descriptionField;
    private final DatePicker dateField;
    private final BigDecimalField participationFeeField;
    private final BigDecimalField salesFeeField;
    private final BigDecimalField feesRoundingStepField;
    private DataModel.Booth.BoothBuilder boothBuilder;

    public UpsertEventForm() {
        descriptionField = new TextField();
        descriptionField.setLabel(getTranslation(DESCRIPTION_FIELD__LABEL));
        descriptionField.setAutofocus(true);
        descriptionField.setRequired(true);
        descriptionField.setAllowedCharPattern(
                Constraints.Events.Description.ALLOWED_CHARS_PATTERN);
        descriptionField.addKeyDownListener(Key.ENTER, this::onEventToCreate);

        dateField = new DatePicker();
        dateField.setLabel(getTranslation(DATE_FIELD__LABEL));
        dateField.setAutoOpen(true);
        dateField.setRequired(true);
        dateField.setWeekNumbersVisible(true);

        participationFeeField = new BigDecimalField();
        participationFeeField.setLabel(getTranslation(PARTICIPATION_FEE_FIELD__LABEL));
        participationFeeField.setPrefixComponent(LineAwesomeIcon.EURO_SIGN_SOLID.create());

        salesFeeField = new BigDecimalField();
        salesFeeField.setLabel(getTranslation(SALES_FEE_FIELD__LABEL));
        salesFeeField.setPrefixComponent(LineAwesomeIcon.PERCENT_SOLID.create());

        feesRoundingStepField = new BigDecimalField();
        feesRoundingStepField.setLabel(getTranslation(FEES_ROUNDING_STEP_FIELD__LABEL));
        feesRoundingStepField.setPrefixComponent(LineAwesomeIcon.EURO_SIGN_SOLID.create());

        add(
                descriptionField,
                dateField,
                participationFeeField,
                salesFeeField,
                feesRoundingStepField);
    }

    public void clear() {
        descriptionField.clear();
        dateField.clear();
        participationFeeField.clear();
        salesFeeField.clear();
        feesRoundingStepField.clear();

        DataModel.Booth.Key boothKey = DataModel.Booth.Key.builder().boothId(Ids.UUID()).build();
        boothBuilder = DataModel.Booth.builder().key(boothKey);

        descriptionField.focus();
    }

    public void addCreateEventFormSubmitListener(
            ComponentEventListener<CreateEventFormSubmitEvent> listener) {
        addListener(CreateEventFormSubmitEvent.class, listener);
    }

    private void onEventToCreate(ComponentEvent<? extends Component> event) {
        Optional<DataModel.Booth> validation = validate(true);
        validation.ifPresent(
                newEvent ->
                        fireEvent(
                                new CreateEventFormSubmitEvent(
                                        this, event.isFromClient(), newEvent)));
    }

    public Optional<DataModel.Booth> validate(boolean withNotification) {
        if (checkFieldNotValidOrEmpty(
                        descriptionField,
                        withNotification,
                        getTranslation(NOTIFICATION__INVALID_DESCRIPTION))
                || checkFieldNotValidOrEmpty(
                        dateField, withNotification, getTranslation(NOTIFICATION__INVALID_DATE))
                || checkFieldNotValid(
                        participationFeeField,
                        HasValidationProperties::isInvalid,
                        withNotification,
                        getTranslation(NOTIFICATION__INVALID_PARTICIPATION_FEE))
                || checkFieldNotValid(
                        salesFeeField,
                        HasValidationProperties::isInvalid,
                        withNotification,
                        getTranslation(NOTIFICATION__INVALID_SALES_FEE))
                || checkFieldNotValid(
                        feesRoundingStepField,
                        HasValidationProperties::isInvalid,
                        withNotification,
                        getTranslation(NOTIFICATION__INVALID_FEES_ROUNDING_STEP))) {
            return Optional.empty();
        }

        DataModel.Booth booth = getBooth();
        return Optional.of(booth);
    }

    public DataModel.Booth getBooth() {
        BigDecimal participationFee =
                Optional.ofNullable(participationFeeField.getValue()).orElse(ZERO);
        BigDecimal salesFee = Optional.ofNullable(salesFeeField.getValue()).orElse(ZERO);
        BigDecimal feesRoundingStep =
                Optional.ofNullable(feesRoundingStepField.getValue()).orElse(ZERO);
        return boothBuilder
                .description(descriptionField.getValue())
                .date(dateField.getValue())
                .participationFee(participationFee)
                .salesFee(salesFee)
                .feesRoundingStep(feesRoundingStep)
                .build();
    }

    public void setEvent(DataModel.Booth booth) {
        boothBuilder.key(booth.key());
        descriptionField.setValue(booth.description());
        dateField.setValue(booth.date());
        participationFeeField.setValue(booth.participationFee());
        salesFeeField.setValue(booth.salesFee());
        feesRoundingStepField.setValue(booth.feesRoundingStep());
    }

    private <C extends AbstractField<?, ?>> boolean checkFieldNotValidOrEmpty(
            C field, boolean withNotification, String message) {
        return checkFieldNotValid(
                field,
                c -> {
                    boolean fieldIsInvalid = false;
                    if (field instanceof HasValidationProperties) {
                        fieldIsInvalid = ((HasValidationProperties) field).isInvalid();
                    }
                    return fieldIsInvalid || field.isEmpty();
                },
                withNotification,
                message);
    }

    private <C extends AbstractField<?, ?>> boolean checkFieldNotValid(
            C field, Predicate<C> validator, boolean withNotification, String notificationMessage) {
        if (validator.test(field)) {
            if (withNotification) {
                Notifications.message(notificationMessage);
            }
            if (field instanceof Focusable<?>) {
                ((Focusable<?>) field).focus();
            }
            return true;
        }
        return false;
    }
}
