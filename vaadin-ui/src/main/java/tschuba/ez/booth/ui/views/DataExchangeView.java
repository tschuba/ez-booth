/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Anchor;
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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.streams.DownloadEvent;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.InMemoryUploadCallback;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.TransferContext;
import com.vaadin.flow.server.streams.TransferProgressListener;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.vaadin.barcodes.Barcode;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.Try;
import tschuba.ez.booth.i18n.TranslationKeys.Common;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.FileExchange;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.FileExport;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.FileImport;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.SelfInfo;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.Transfer;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.proto.ProtoModel;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.ui.Constraints;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.layouts.OneColumnLayout;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;
import tschuba.ez.booth.ui.services.DataExchangeClient;
import tschuba.ez.booth.ui.util.AddressCodec;
import tschuba.ez.booth.ui.util.Badges;
import tschuba.ez.booth.ui.util.Buttons;
import tschuba.ez.booth.ui.util.NavigateTo;
import tschuba.ez.booth.ui.util.Notifications;
import tschuba.ez.booth.ui.util.Routing;
import tschuba.ez.booth.ui.util.Server;
import tschuba.ez.booth.ui.util.Spacing;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.vaadin.lineawesome.LineAwesomeIcon.PLAY_SOLID;
import static tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.SelfInfo.ADDRESS_LABEL__TEXT;
import static tschuba.ez.booth.proto.ProtoServices.ExchangeData;

@Route(value = "data-exchange", layout = AppLayoutWithMenu.class)
@SpringComponent
@UIScope
public class DataExchangeView extends OneColumnLayout {

  private final Tab transferTab;
  private final Tab fileExchangeTab;

  public DataExchangeView(@NonNull SelfInfoCard selfInfo,
                          @NonNull TransferCard transferCard,
                          @NonNull FileExportCard fileExportCard,
                          @NonNull FileImportCard fileImportCard) {
    HorizontalLayout transferCards = new HorizontalLayout(selfInfo, transferCard);

    HorizontalLayout fileExchangeCards = new HorizontalLayout(JustifyContentMode.BETWEEN, fileExportCard, fileImportCard);
    fileExchangeCards.setFlexGrow(1, fileExportCard);
    fileExchangeCards.setFlexGrow(2, fileImportCard);


    TabSheet tabSheet = new TabSheet();
    transferTab = new Tab();
    tabSheet.add(transferTab, transferCards);
    fileExchangeTab = new Tab();
    tabSheet.add(fileExchangeTab, fileExchangeCards);

    Main content = new Main();
    content.add(tabSheet);
    setContent(content);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    transferTab.setLabel(getTranslation(Transfer.TITLE));
    fileExchangeTab.setLabel(getTranslation(FileExchange.TITLE));
  }

  @SpringComponent
  @UIScope
  public static class SelfInfoCard extends Composite<Card> {

    private final Environment environment;

    private final VerticalLayout contentLayout = new VerticalLayout();
    private final Span address = new Span();
    private final NativeLabel addressLabel = new NativeLabel();
    private final Span shortAddress = new Span();
    private final NativeLabel shortAddressLabel = new NativeLabel();
    private Barcode qrCode;

    public SelfInfoCard(@NonNull Environment environment) {
      this.environment = environment;

      Badges.highlight(address);
      Badges.highlight(shortAddress);

      HorizontalLayout addressLayout =
          new HorizontalLayout(Alignment.CENTER, addressLabel, address);
      addressLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
      addressLayout.addClassNames(Width.FULL);

      HorizontalLayout shortAddressLayout =
          new HorizontalLayout(Alignment.CENTER, shortAddressLabel, shortAddress);
      shortAddressLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
      shortAddressLayout.addClassNames(Width.FULL);

      contentLayout.setAlignItems(Alignment.CENTER);
      contentLayout.add(addressLayout, shortAddressLayout);
      Spacing.spacing(contentLayout).small();
      getContent().add(contentLayout);
    }

    @Override
    protected void onAttach(AttachEvent event) {
      String externalGrpcAddress = Server.externalGrpcAddress(environment);
      String shortExternalGrpcAddress = AddressCodec.Exchange.encode(externalGrpcAddress);

      getContent().setTitle(getTranslation(SelfInfo.TITLE));

      address.setText(externalGrpcAddress);
      addressLabel.setText(getTranslation(ADDRESS_LABEL__TEXT));
      shortAddress.setText(shortExternalGrpcAddress);
      shortAddressLabel.setText(getTranslation(Common.OR));

      if (qrCode != null) {
        contentLayout.remove(qrCode);
      }
      qrCode = new Barcode(externalGrpcAddress, Barcode.Type.qrcode);
      contentLayout.add(qrCode);
    }
  }

  @SpringComponent
  @UIScope
  @Slf4j
  public static class TransferCard extends Composite<Card> {

    private final BoothService booths;
    private final DataExchangeClient dataExchangeClient;

    private final TextField addressField = new TextField();
    private final Button transferButton = new Button(PLAY_SOLID.create());
    private final Paragraph description = new Paragraph();
    private final Paragraph noBoothSelectedText = new Paragraph();
    private final Button selectBoothButton =
        new Button(LineAwesomeIcon.PERSON_BOOTH_SOLID.create());
    private final VerticalLayout noBoothSelectedLayout =
        new VerticalLayout(Alignment.CENTER, noBoothSelectedText, selectBoothButton);
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

      Buttons.disableUntilAfterClick(
          selectBoothButton,
          _ -> {
            try {
              RouteParameters routeParameters =
                  Routing.Parameters.builder().returnToView(DataExchangeView.class).build();
              NavigateTo.view(BoothSelectionView.class, routeParameters).currentWindow();
            } catch (Exception ex) {
              log.error("Failed to navigate to booth selection view", ex);
            }
          });

      getContent().setSubtitle(description);
    }

    @Override
    protected void onAttach(AttachEvent event) {
      Optional<DataModel.Booth> currentBooth = BoothSelection.get().flatMap(booths::findById);
      getContent().setTitle(getTranslation(Transfer.TITLE));

      getContent().removeAll();
      if (currentBooth.isEmpty()) {
        noBoothSelectedText.setText(getTranslation(Transfer.NOTIFICATION__NO_BOOTH_SELECTED));
        selectBoothButton.setText(getTranslation(Transfer.SELECT_BOOTH_BUTTON__TEXT));

        getContent().add(noBoothSelectedLayout);
      } else {
        description.setText(
            currentBooth
                .map(
                    booth ->
                        getTranslation(Transfer.TRANSFER_DESCRIPTION__TEXT, booth.description()))
                .orElse(""));
        addressField.setLabel(getTranslation(Transfer.ADDRESS_FIELD__LABEL));
        transferButton.setText(getTranslation(Transfer.TRANSFER_BUTTON__LABEL));

        busyIndicatorText.setText(getTranslation(Transfer.TRANSFER_IN_PROGRESS__TEXT));

        getContent().add(new VerticalLayout(addressField, transferButton), busyIndicator);
      }
    }

    private void onAddressChange(ComponentValueChangeEvent<TextField, String> event) {
      transferButton.setEnabled(!event.getSource().isInvalid());
    }

    private void onClickTransferButton(ClickEvent<Button> event) {
      String input = addressField.getValue();
      Try<String> decodedValue = AddressCodec.Exchange.tryDecode(input);
      if (decodedValue.failed()) {
        log.debug("Invalid target address entered: {}", input);
        addressField.focus();
        Notifications.error(getTranslation(Transfer.NOTIFICATION__INVALID_ADDRESS, input));
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
        Notifications.success(getTranslation(Transfer.NOTIFICATION__TRANSFER_COMPLETED));
      } catch (Exception ex) {
        log.error("Data transfer to {} for {} failed!", targetAddress, booth, ex);
        Notifications.error(getTranslation(Transfer.NOTIFICATION__TRANSFER_FAILED), ex);
      } finally {
        busyIndicator.close();
        addressField.setVisible(true);
      }
    }
  }

  @SpringComponent
  @UIScope
  @Slf4j
  public static class FileExportCard extends Composite<Card> {

    private final @NonNull DataExchangeClient dataExchangeClient;
    private final Anchor exportLink;

    public FileExportCard(@NonNull DataExchangeClient dataExchangeClient) {
      this.dataExchangeClient = dataExchangeClient;

      exportLink = new Anchor(new ExportHandler(), null);
      Card content = getContent();
      content.setHeaderPrefix(LineAwesomeIcon.FILE_EXPORT_SOLID.create());
      content.add(exportLink);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
      getContent().setTitle(getTranslation(FileExport.TITLE));
      exportLink.setText(getTranslation(FileExport.EXPORT_LINK__TEXT));
    }

    /**
     * Handler for exporting booth data as a file.
     */
    class ExportHandler implements DownloadHandler {

      @Override
      public void handleDownloadRequest(DownloadEvent download) throws IOException {
        try {
          download.getSession().lock();

          DataModel.Booth.Key key = BoothSelection.get().orElseThrow();
          ExchangeData exchangeData = dataExchangeClient.exportData(key);
          ProtoModel.Booth booth = exchangeData.getBooth();

          String fileName = booth.getDescription() + "-" + booth.getDate() + "-" + LocalDateTime.now() + ".ezb";
          download.setFileName(fileName);
          VaadinResponse response = download.getResponse();
          response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
          response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(fileName));
          exchangeData.writeTo(download.getOutputStream());
        } catch (Exception ex) {
          log.error("Failed to export data for download", ex);
          // TODO: translate error message and show more user-friendly message
          Notifications.error("Failed to export data. Please try again.", ex);
        } finally {
          download.getSession().unlock();
        }
      }
    }
  }

  @SpringComponent
  @UIScope
  @Slf4j
  public static class FileImportCard extends Composite<Card> {

    private final @NonNull DataExchangeClient dataExchangeClient;

    private final BusyIndicator busyIndicator = new BusyIndicator();
    private final Upload upload;

    public FileImportCard(@NonNull DataExchangeClient dataExchangeClient) {
      this.dataExchangeClient = dataExchangeClient;

      InMemoryUploadHandler uploadHandler = UploadHandler.inMemory(new ImportUploadCallback(), new ImportProgressHandler());
      upload = new Upload(uploadHandler);

      Card content = getContent();
      content.setHeaderPrefix(LineAwesomeIcon.FILE_IMPORT_SOLID.create());
      content.add(upload);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
      getContent().setTitle(getTranslation(FileImport.TITLE));

      String i18nPrefix = "%s.%s".formatted(DataExchangeView.class.getSimpleName(), FileImportCard.class.getSimpleName());
      UploadI18N uploadI18N = tschuba.ez.booth.i18n.Upload.upload(i18nPrefix, getLocale());
      upload.setI18n(uploadI18N);
    }

    /**
     * Callback for handling uploaded data files.
     */
    class ImportUploadCallback implements InMemoryUploadCallback {
      @Override
      public void complete(UploadMetadata metadata, byte[] bytes) {
        try {
          ExchangeData exchangeData = ExchangeData.parseFrom(bytes);
          String boothDescription = exchangeData.getBooth().getDescription();
          log.debug("Received data upload for booth '{}'", boothDescription);
          dataExchangeClient.mergeData(exchangeData);
          log.debug("Data upload with booth description '{}' merged successfully", boothDescription);
        } catch (InvalidProtocolBufferException ex) {
          log.error("Failed to parse uploaded data file", ex);
          // TODO: translate error message and show more user-friendly message
          Notifications.error("Failed to parse uploaded data file. Please make sure to upload a valid .ezb file.", ex);
        }
      }
    }

    /**
     * Listener for tracking the progress of data uploads.
     */
    class ImportProgressHandler implements TransferProgressListener {

      @Override
      public void onStart(TransferContext context) {
        busyIndicator.open();
      }

      @Override
      public void onComplete(TransferContext context, long transferredBytes) {
        busyIndicator.close();
      }
    }
  }

  /**
   * A simple busy indicator component that can be shown during long-running operations.
   */
  public static class BusyIndicator extends Composite<Popover> {

    private final Span busyIndicatorText = new Span();

    public BusyIndicator() {
      ProgressBar progressBar = new ProgressBar();
      progressBar.setIndeterminate(true);

      Popover popover = getContent();
      popover.setTarget(this);
      popover.setModal(true);
      popover.setBackdropVisible(true);
      popover.setOpenOnClick(false);
      popover.setOpenOnFocus(false);
      popover.setOpenOnHover(false);
      popover.setCloseOnEsc(false);
      popover.setCloseOnOutsideClick(false);
      popover.setPosition(PopoverPosition.TOP);
      popover.add(new VerticalLayout(busyIndicatorText, progressBar));
    }

    public void setTarget(Component target) {
      getContent().setTarget(target);
    }

    public void setText(String text) {
      busyIndicatorText.setText(text);
    }

    public void open() {
      getContent().open();
    }

    public void close() {
      getContent().close();
    }
  }
}
