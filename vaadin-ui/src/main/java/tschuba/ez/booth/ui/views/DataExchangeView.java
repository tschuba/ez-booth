/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import static org.vaadin.lineawesome.LineAwesomeIcon.COPY_SOLID;
import static org.vaadin.lineawesome.LineAwesomeIcon.PLAY_SOLID;
import static tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.COPIED_TO_CLIPBOARD__NOTIFICATION;
import static tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.SelfInfo.ADDRESS_LABEL__TEXT;
import static tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.SelfInfo.SHORT_ADDRESS_LABEL__TEXT;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.SelfInfo;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.Transfer;
import tschuba.ez.booth.ui.Constraints;
import tschuba.ez.booth.ui.components.event.EventRequired;
import tschuba.ez.booth.ui.layouts.OneColumnLayout;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;
import tschuba.ez.booth.ui.util.*;

@Route(value = "data-exchange", layout = AppLayoutWithMenu.class)
@EventRequired
public class DataExchangeView extends OneColumnLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeView.class);

    public DataExchangeView(@NonNull SelfInfoCard selfInfo, @NonNull TransferCard transferCard) {
        Main content = new Main();
        setContent(content);
        content.add(new HorizontalLayout(selfInfo, transferCard));
    }

    @SpringComponent
    @UIScope
    public static class SelfInfoCard extends Composite<Card> {

        private final String grpcAddress;

        private final Span address = new Span();
        private final NativeLabel addressLabel = new NativeLabel();
        private final Span shortAddress = new Span();
        private final NativeLabel shortAddressLabel = new NativeLabel();

        public SelfInfoCard(@NonNull Environment environment) {
            this.grpcAddress = environment.getProperty("grpc.client.booth.address");

            Badges addressBadge = Badges.primary().contrast();

            addressBadge.applyTo(this.address);
            this.address.addClassNames(FontWeight.BLACK);
            Button addressButton = createCopyButton(this.address);

            addressBadge.applyTo(this.shortAddress);
            this.shortAddress.addClassNames(FontWeight.BLACK);
            Button shortAddressButton = createCopyButton(this.shortAddress);

            getContent()
                    .add(
                            new HorizontalLayout(Alignment.CENTER, addressLabel, addressButton),
                            new HorizontalLayout(
                                    Alignment.CENTER, shortAddressLabel, shortAddressButton));
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
            shortAddressLabel.setText(getTranslation(SHORT_ADDRESS_LABEL__TEXT));
        }

        private Button createCopyButton(final Span urlContainer) {
            HorizontalLayout syncUrlShortButtonContent =
                    new HorizontalLayout(urlContainer, COPY_SOLID.create());
            Spacing.spacing(syncUrlShortButtonContent).xsmall();
            Button button = new Button(syncUrlShortButtonContent);
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            button.addClassNames(Margin.NONE);

            button.addClickListener(
                    event ->
                            event.getSource()
                                    .getUI()
                                    .ifPresent(
                                            ui -> {
                                                ui.getPage()
                                                        .executeJs(
                                                                """
                                                                window.copyToClipboard = (str) => {
                                                                  const textarea = document.createElement("textarea");
                                                                  textarea.value = str;
                                                                  textarea.style.position = "absolute";
                                                                  textarea.style.opacity = "0";
                                                                  document.body.appendChild(textarea);
                                                                  textarea.select();
                                                                  document.execCommand("copy");
                                                                  document.body.removeChild(textarea);
                                                                };
                                                                window.copyToClipboard($0)\
                                                                """,
                                                                urlContainer.getText());
                                                Notifications.message(
                                                        getTranslation(
                                                                COPIED_TO_CLIPBOARD__NOTIFICATION));
                                            }));

            return button;
        }
    }

    @SpringComponent
    @UIScope
    public static class TransferCard extends Composite<Card> {

        private final TextField addressField = new TextField();
        private final Button transferButton = new Button(PLAY_SOLID.create());
        private final Popover busyIndicator = new Popover();
        private final Span busyIndicatorText = new Span();

        public TransferCard() {
            addressField.addClassNames(Width.FULL);
            addressField.setRequired(true);
            addressField.setRequiredIndicatorVisible(true);
            addressField.setMinLength(2);
            addressField.setAutoselect(true);
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

            getContent().add(addressField, transferButton, busyIndicator);
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            getContent().setTitle(getTranslation(Transfer.TITLE));
            addressField.setLabel(getTranslation(Transfer.ADDRESS_FIELD__LABEL));
            transferButton.setText(getTranslation(Transfer.TRANSFER_BUTTON__LABEL));
            busyIndicatorText.setText(getTranslation(Transfer.TRANSFER_IN_PROGRESS__TEXT));
        }

        private void onAddressChange(ComponentValueChangeEvent<TextField, String> event) {
            transferButton.setEnabled(!event.getSource().isInvalid());
        }

        private void onClickTransferButton(ClickEvent<Button> event) {
            LOGGER.info("Starting data transfer to {}", addressField.getValue());
            try {
                addressField.setVisible(false);
                busyIndicator.open();
                Notifications.message(
                        "Starting data transfer to %s".formatted(addressField.getValue()));
            } finally {
                busyIndicator.close();
                addressField.setVisible(true);
            }
        }
    }
}
