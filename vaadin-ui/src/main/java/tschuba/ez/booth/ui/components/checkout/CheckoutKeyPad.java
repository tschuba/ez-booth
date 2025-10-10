/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.checkout;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static org.vaadin.lineawesome.LineAwesomeIcon.*;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import tschuba.ez.booth.i18n.TranslationKeys;

public class CheckoutKeyPad extends Div implements HasEnabled {
  public static final char DECIMAL_SEPARATOR = '.';
  private FieldAdapter<?> focusField;

  private boolean decimalSeparatorPending = false;
  private boolean decimalZeroPending = false;

  public CheckoutKeyPad(CheckoutItemForm checkoutForm) {
    addClassNames(
        Display.FLEX,
        FlexDirection.COLUMN,
        Gap.LARGE,
        BorderRadius.LARGE,
        Background.CONTRAST_5,
        Padding.Vertical.LARGE,
        Padding.Horizontal.XLARGE,
        Margin.Horizontal.XLARGE);

    Div inputGrid = new Div();
    inputGrid.addClassNames(Display.GRID, Grid.Column.COLUMNS_3, Grid.Row.ROWS_4, Gap.SMALL);
    IntStream.of(2, 1, 0)
        .forEach(
            row ->
                IntStream.of(1, 2, 3)
                    .map(col -> (row * 3) + col)
                    .mapToObj(this::createDigitButton)
                    .forEach(inputGrid::add));

    Button backButton =
        createButton(
            button -> {
              button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
              button.setIcon(BACKSPACE_SOLID.create());
            },
            clickEvent -> {
              boolean setSeparatorPending =
                  Optional.of(focusField)
                      .map(
                          fieldAdapter -> {
                            String value = fieldAdapter.getValueAsString();
                            return value != null
                                && value.length() > 2
                                && value.charAt(value.length() - 2) == DECIMAL_SEPARATOR;
                          })
                      .orElse(false);
              updateBoundField(ValueModifiers.removeLastChar());
              decimalSeparatorPending = setSeparatorPending;
            });
    inputGrid.add(backButton);

    inputGrid.add(createDigitButton(0));

    Button separatorButton =
        createButton(
            button -> {
              button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
              button.setText(",");
            },
            clickEvent -> CheckoutKeyPad.this.decimalSeparatorPending = true);
    inputGrid.add(separatorButton);

    Div actionGrid = new Div();
    actionGrid.addClassNames(Display.GRID, Grid.Column.COLUMNS_2, Gap.SMALL);

    Button clearButton =
        createButton(
            button -> {
              button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
              button.setIcon(TIMES_SOLID.create());
              String text = getTranslation(TranslationKeys.CheckoutKeyPad.CLEAR_BUTTON__TEXT);
              Tooltip.forComponent(button).setText(text);
            },
            clickEvent -> {
              if (focusField != null) {
                focusField.setEmptyValue();
              }
            });
    actionGrid.add(clearButton);

    Button finishButton =
        createButton(
            button -> {
              button.setIcon(CHECK_SOLID.create());
              String text = getTranslation(TranslationKeys.CheckoutKeyPad.FINISH_BUTTON__TEXT);
              Tooltip.forComponent(button).setText(text);
              button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            },
            clickEvent -> checkoutForm.valueConfirmed(focusField.field));
    actionGrid.add(finishButton);

    this.add(inputGrid, actionGrid);

    checkoutForm
        .getPriceField()
        .addFocusListener(event -> focusField = FieldAdapter.bigDecimal(event.getSource()));
    checkoutForm
        .getVendorField()
        .addFocusListener(event -> focusField = FieldAdapter.text(event.getSource()));
  }

  private Button createDigitButton(int value) {
    return createButton(
        button -> {
          button.setText(Integer.toString(value));
          button.addClassNames(Background.CONTRAST_10, FontSize.XLARGE);
        },
        clickEvent -> updateBoundField(ValueModifiers.append(Character.forDigit(value, 10))));
  }

  private Button createButton(
      Consumer<Button> buttonConsumer, Consumer<ClickEvent<Button>> clickHandler) {
    Button button = new Button();
    buttonConsumer.accept(button);
    button.setDisableOnClick(true);
    button.addClassNames(Height.XLARGE);
    button.addThemeVariants(ButtonVariant.LUMO_LARGE);
    button.addClickListener(
        clickEvent -> {
          try {
            clickHandler.accept(clickEvent);
          } finally {
            clickEvent.getSource().setEnabled(true);
          }
        });
    return button;
  }

  private void updateBoundField(Function<String, String> modifier) {
    Optional.ofNullable(focusField)
        .ifPresent(
            field -> {
              String fieldValue = field.getValueAsString();
              if (decimalSeparatorPending) {
                fieldValue = ValueModifiers.append(DECIMAL_SEPARATOR).apply(fieldValue);
                decimalSeparatorPending = false;
              }
              if (decimalZeroPending) {
                fieldValue = ValueModifiers.append('0').apply(fieldValue);
                decimalZeroPending = false;
              }
              String newValue = modifier.apply(fieldValue);
              field.setValueFromString(newValue);
            });
  }

  private interface ValueModifiers {
    static Function<String, String> append(final char ch) {
      return value -> {
        if (value != null) {
          return value + ch;
        } else if (Character.isDigit(ch)) {
          return String.valueOf(ch);
        } else {
          return "0" + ch;
        }
      };
    }

    static Function<String, String> removeLastChar() {
      return value -> {
        if (value == null) {
          return null;
        }
        if (!value.isEmpty()) {
          int lastIndex = value.length() - 1;
          value = value.substring(0, lastIndex);
        }
        return value;
      };
    }
  }

  private record FieldAdapter<T>(Converter<T> converter, AbstractField<?, T> field) {

    private static FieldAdapter<BigDecimal> bigDecimal(BigDecimalField field) {
      return new FieldAdapter<>(new BigDecimalConverter(), field);
    }

    private static FieldAdapter<String> text(TextField field) {
      return new FieldAdapter<>(new TextConverter(), field);
    }

    String getValueAsString() {
      return converter.convert(field.getValue());
    }

    void setValueFromString(String value) {
      T newValue = this.converter.parse(value);
      field.setValue(newValue);
    }

    void setEmptyValue() {
      field.setValue(field.getEmptyValue());
    }
  }

  private interface Converter<T> {
    String convert(T value);

    T parse(String value);
  }

  private static class BigDecimalConverter implements Converter<BigDecimal> {
    @Override
    public String convert(BigDecimal value) {
      if (value == null) {
        return null;
      }

      return value.toPlainString();
    }

    @Override
    public BigDecimal parse(String value) {
      return new BigDecimal(value);
    }
  }

  private static class TextConverter implements Converter<String> {
    @Override
    public String convert(String value) {
      return value;
    }

    @Override
    public String parse(String value) {
      return value;
    }
  }
}
