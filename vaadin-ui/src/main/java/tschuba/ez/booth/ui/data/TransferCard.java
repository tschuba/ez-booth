/**
 * Copyright (c) 2026 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.data;

import static org.vaadin.lineawesome.LineAwesomeIcon.PLAY_SOLID;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import tschuba.ez.booth.Try;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.ui.Constraints;
import tschuba.ez.booth.ui.components.BusyIndicator;
import tschuba.ez.booth.ui.components.NoBoothSelectedPlaceholder;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.services.DataExchangeClient;
import tschuba.ez.booth.ui.util.AddressCodec;
import tschuba.ez.booth.ui.util.Buttons;
import tschuba.ez.booth.ui.util.Notifications;

/**
 * Card component for transferring booth data to another booth via peer-to-peer exchange.
 */
@SpringComponent
@UIScope
@Slf4j
public class TransferCard extends Composite<Card> {

  private final BoothService booths;
  private final DataExchangeClient dataExchangeClient;

  private final TextField addressField = new TextField();
  private final Button transferButton = new Button(PLAY_SOLID.create());
  private final Paragraph description = new Paragraph();
  private final NoBoothSelectedPlaceholder noBoothSelectedPlaceholder =
      new NoBoothSelectedPlaceholder();
  private final BusyIndicator busyIndicator = new BusyIndicator();

  public TransferCard(
      @NonNull BoothService booths, @NonNull DataExchangeClient dataExchangeClient) {
    this.booths = booths;
    this.dataExchangeClient = dataExchangeClient;

    addressField.setId("data-exchange-target");
    addressField.addClassNames(LumoUtility.Width.FULL);
    addressField.setRequired(true);
    addressField.setRequiredIndicatorVisible(true);
    addressField.setMinLength(2);
    addressField.setAutoselect(true);
    addressField.setAutocomplete(Autocomplete.OFF);
    addressField.setPattern(Constraints.DataExchange.Transfer.ADDRESS_PATTERN);
    addressField.addValueChangeListener(this::onAddressChange);

    transferButton.setEnabled(false);
    Buttons.disableUntilAfterClick(transferButton, this::onClickTransferButton);

    ProgressBar progressBar = new ProgressBar();
    progressBar.setIndeterminate(true);
    busyIndicator.setTarget(transferButton);

    description.setWhiteSpace(HasText.WhiteSpace.PRE_LINE);

    getContent().setSubtitle(description);
  }

  @Override
  protected void onAttach(AttachEvent event) {
    Optional<DataModel.Booth> currentBooth = BoothSelection.get().flatMap(booths::findById);
    getContent().setTitle(getTranslation(TranslationKeys.DataExchangeView.Transfer.TITLE));

    getContent().removeAll();
    if (currentBooth.isEmpty()) {
      getContent().add(noBoothSelectedPlaceholder);
    } else {
      description.setText(
          currentBooth
              .map(
                  booth ->
                      getTranslation(
                          TranslationKeys.DataExchangeView.Transfer.TRANSFER_DESCRIPTION__TEXT,
                          booth.description()))
              .orElse(""));
      addressField.setLabel(
          getTranslation(TranslationKeys.DataExchangeView.Transfer.ADDRESS_FIELD__LABEL));
      transferButton.setText(
          getTranslation(TranslationKeys.DataExchangeView.Transfer.TRANSFER_BUTTON__LABEL));

      busyIndicator.setText(
          getTranslation(TranslationKeys.DataExchangeView.Transfer.TRANSFER_IN_PROGRESS__TEXT));

      getContent().add(new VerticalLayout(addressField, transferButton), busyIndicator);
    }
  }

  private void onAddressChange(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
    transferButton.setEnabled(!event.getSource().isInvalid());
  }

  private void onClickTransferButton(ClickEvent<Button> event) {
    String input = addressField.getValue();
    Try<String> decodedValue = AddressCodec.Exchange.tryDecode(input);
    if (decodedValue.failed()) {
      log.debug("Invalid target address entered: {}", input);
      addressField.focus();
      Notifications.error(
          getTranslation(
              TranslationKeys.DataExchangeView.Transfer.NOTIFICATION__INVALID_ADDRESS, input));
      return;
    }
    String targetAddress = decodedValue.get();

    DataModel.Booth.Key booth = BoothSelection.get().orElseThrow();
    log.debug("Starting data transfer to {} for {}", targetAddress, booth.boothId());
    try {
      addressField.setVisible(false);
      busyIndicator.open();

      dataExchangeClient.exchangeDataWith(targetAddress, booth);
      log.debug(
          "Data transfer to {} for {} completed successfully.", targetAddress, booth.boothId());
      Notifications.success(
          getTranslation(
              TranslationKeys.DataExchangeView.Transfer.NOTIFICATION__TRANSFER_COMPLETED));
    } catch (Exception ex) {
      log.error("Data transfer to {} for {} failed!", targetAddress, booth, ex);
      Notifications.error(
          getTranslation(TranslationKeys.DataExchangeView.Transfer.NOTIFICATION__TRANSFER_FAILED),
          ex);
    } finally {
      busyIndicator.close();
      addressField.setVisible(true);
    }
  }
}
