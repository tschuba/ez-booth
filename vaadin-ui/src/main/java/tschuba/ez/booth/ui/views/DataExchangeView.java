/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import static org.vaadin.lineawesome.LineAwesomeIcon.PLAY_SOLID;
import static tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.SelfInfo.ADDRESS_LABEL__TEXT;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import java.util.Optional;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import tschuba.ez.booth.Try;
import tschuba.ez.booth.i18n.TranslationKeys.Common;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.SelfInfo;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.Transfer;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.ui.Constraints;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.components.event.EventRequired;
import tschuba.ez.booth.ui.layouts.OneColumnLayout;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;
import tschuba.ez.booth.ui.services.DataExchangeClient;
import tschuba.ez.booth.ui.util.*;

@Route(value = "data-exchange", layout = AppLayoutWithMenu.class)
@EventRequired
public class DataExchangeView extends OneColumnLayout {

    public DataExchangeView(@NonNull SelfInfoCard selfInfo, @NonNull TransferCard transferCard) {
        Main content = new Main();
        setContent(content);
        content.add(new HorizontalLayout(selfInfo, transferCard));
    }

    @SpringComponent
    @UIScope
    public static class SelfInfoCard extends Composite<Card> {

        private static final String GRPC_CLIENT_ADDRESS = "grpc.client.booth.address";

        private final String grpcAddress;

        private final Span address = new Span();
        private final NativeLabel addressLabel = new NativeLabel();
        private final Span shortAddress = new Span();
        private final NativeLabel shortAddressLabel = new NativeLabel();

        public SelfInfoCard(@NonNull Environment environment) {
            this.grpcAddress = environment.getProperty(GRPC_CLIENT_ADDRESS);

            Badges addressBadge = Badges.primary().contrast();

            addressBadge.applyTo(address);
            address.addClassNames(FontWeight.BLACK);

            addressBadge.applyTo(shortAddress);
            shortAddress.addClassNames(FontWeight.BLACK);

            HorizontalLayout addressLayout =
                    new HorizontalLayout(Alignment.CENTER, addressLabel, address);
            addressLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            addressLayout.addClassNames(Width.FULL);

            HorizontalLayout shortAddressLayout =
                    new HorizontalLayout(Alignment.CENTER, shortAddressLabel, shortAddress);
            shortAddressLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            shortAddressLayout.addClassNames(Width.FULL);

            VerticalLayout contentLayout = new VerticalLayout(addressLayout, shortAddressLayout);
            Spacing.spacing(contentLayout).small();
            getContent().add(contentLayout);
        }

        @Override
        protected void onAttach(AttachEvent event) {
            int portDelimiterIdx = this.grpcAddress.indexOf(':');
            String externalHost = Server.externalHostAddress();
            String externalGrpcAddress =
                    (portDelimiterIdx > 0)
                            ? externalHost + this.grpcAddress.substring(portDelimiterIdx)
                            : externalHost;
            String shortExternalGrpcAddress = AddressCodec.Exchange.encode(externalGrpcAddress);

            getContent().setTitle(getTranslation(SelfInfo.TITLE));

            address.setText(externalGrpcAddress);
            addressLabel.setText(getTranslation(ADDRESS_LABEL__TEXT));
            shortAddress.setText(shortExternalGrpcAddress);
            shortAddressLabel.setText(getTranslation(Common.OR));
        }
    }

    @SpringComponent
    @UIScope
    public static class TransferCard extends Composite<Card> {

        private static final Logger LOGGER = LoggerFactory.getLogger(TransferCard.class);

        private final BoothService booths;
        private final DataExchangeClient dataExchangeClient;

        private final TextField addressField = new TextField();
        private final Button transferButton = new Button(PLAY_SOLID.create());
        private final Paragraph description = new Paragraph();
        private final Popover busyIndicator = new Popover();
        private final Span busyIndicatorText = new Span();

        public TransferCard(
                @NonNull BoothService booths, @NonNull DataExchangeClient dataExchangeClient) {
            this.booths = booths;
            this.dataExchangeClient = dataExchangeClient;

            addressField.setId("data-exchange-target");
            addressField.addClassNames(Width.FULL);
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
            busyIndicator.setModal(true);
            busyIndicator.setBackdropVisible(true);
            busyIndicator.setOpenOnClick(false);
            busyIndicator.setOpenOnFocus(false);
            busyIndicator.setOpenOnHover(false);
            busyIndicator.setCloseOnEsc(false);
            busyIndicator.setCloseOnOutsideClick(false);
            busyIndicator.setPosition(PopoverPosition.TOP);
            busyIndicator.add(new VerticalLayout(busyIndicatorText, progressBar));

            description.setWhiteSpace(HasText.WhiteSpace.PRE_LINE);

            getContent().setSubtitle(description);
            getContent().add(new VerticalLayout(addressField, transferButton), busyIndicator);
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            Optional<DataModel.Booth> currentBooth = BoothSelection.get().flatMap(booths::findById);
            getContent().setTitle(getTranslation(Transfer.TITLE));
            addressField.setLabel(getTranslation(Transfer.ADDRESS_FIELD__LABEL));
            transferButton.setText(getTranslation(Transfer.TRANSFER_BUTTON__LABEL));
            description.setText(
                    currentBooth
                            .map(
                                    booth ->
                                            getTranslation(
                                                    Transfer.TRANSFER_DESCRIPTION__TEXT,
                                                    booth.description()))
                            .orElse(""));
            busyIndicatorText.setText(getTranslation(Transfer.TRANSFER_IN_PROGRESS__TEXT));
        }

        private void onAddressChange(ComponentValueChangeEvent<TextField, String> event) {
            transferButton.setEnabled(!event.getSource().isInvalid());
        }

        private void onClickTransferButton(ClickEvent<Button> event) {
            String input = addressField.getValue();
            Try<String> decodedValue = AddressCodec.Exchange.tryDecode(input);
            if (decodedValue.failed()) {
                LOGGER.debug("Invalid target address entered: {}", input);
                addressField.focus();
                Notifications.error(getTranslation(Transfer.NOTIFICATION__INVALID_ADDRESS, input));
                return;
            }
            String targetAddress = decodedValue.get();

            DataModel.Booth.Key booth = BoothSelection.get().orElseThrow();
            LOGGER.debug("Starting data transfer to {} for {}", targetAddress, booth.boothId());
            try {
                addressField.setVisible(false);
                busyIndicator.open();

                dataExchangeClient.exchangeDataWith(targetAddress, booth);
                LOGGER.debug(
                        "Data transfer to {} for {} completed successfully.",
                        targetAddress,
                        booth.boothId());
                Notifications.success(getTranslation(Transfer.NOTIFICATION__TRANSFER_COMPLETED));
            } catch (Exception ex) {
                LOGGER.error("Data transfer to {} for {} failed!", targetAddress, booth, ex);
                Notifications.error(getTranslation(Transfer.NOTIFICATION__TRANSFER_FAILED), ex);
            } finally {
                busyIndicator.close();
                addressField.setVisible(true);
            }
        }
    }
}
