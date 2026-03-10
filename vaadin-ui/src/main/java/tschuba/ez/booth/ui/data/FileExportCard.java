/**
 * Copyright (c) 2026 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.data;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.streams.DownloadEvent;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.proto.ProtoModel;
import tschuba.ez.booth.proto.ProtoServices;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.ui.components.NoBoothSelectedPlaceholder;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.services.DataExchangeClient;
import tschuba.ez.booth.ui.util.Notifications;

/**
 * Card component for exporting booth data as a file.
 */
@SpringComponent
@UIScope
@Slf4j
public class FileExportCard extends Composite<Card> {

  private final @NonNull BoothService booths;
  private final @NonNull DataExchangeClient dataExchangeClient;
  private final Anchor exportLink = new Anchor(new ExportHandler(), null);
  private final VerticalLayout exportLinkContainer =
      new VerticalLayout(
          FlexComponent.Alignment.CENTER,
          new HorizontalLayout(FlexComponent.JustifyContentMode.CENTER, exportLink));
  private final NoBoothSelectedPlaceholder noBoothSelectedPlaceholder =
      new NoBoothSelectedPlaceholder();

  public FileExportCard(
      @NonNull BoothService boothService, @NonNull DataExchangeClient dataExchangeClient) {
    this.booths = boothService;
    this.dataExchangeClient = dataExchangeClient;

    Card content = getContent();
    content.setHeaderPrefix(LineAwesomeIcon.FILE_EXPORT_SOLID.create());
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    Card content = getContent();
    content.setTitle(getTranslation(TranslationKeys.DataExchangeView.FileExport.TITLE));

    Optional<DataModel.Booth> booth = BoothSelection.get().flatMap(booths::findById);

    if (booth.isEmpty()) {
      exportLink.removeFromParent();
      content.add(noBoothSelectedPlaceholder);
    } else {
      noBoothSelectedPlaceholder.removeFromParent();
      content.add(exportLinkContainer);
      exportLink.setText(
          getTranslation(TranslationKeys.DataExchangeView.FileExport.EXPORT_LINK__TEXT));
      exportLink.setText(booth.get().description());
    }
  }

  /**
   * Handler for exporting booth data as a file.
   */
  class ExportHandler implements DownloadHandler {

    @Override
    public void handleDownloadRequest(DownloadEvent download) {
      try {
        download.getSession().lock();

        DataModel.Booth.Key key = BoothSelection.get().orElseThrow();
        ProtoServices.ExchangeData exchangeData = dataExchangeClient.exportData(key);
        ProtoModel.Booth booth = exchangeData.getBooth();

        String fileName =
            booth.getDescription()
                + "-"
                + LocalDateTime.now()
                + DataExchangeView.DATA_FILE_EXTENSION;
        download.setFileName(fileName);
        VaadinResponse response = download.getResponse();
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(fileName));
        exchangeData.writeTo(download.getOutputStream());
      } catch (Exception ex) {
        log.error("Failed to export data for download", ex);
        Notifications.error(
            getTranslation(TranslationKeys.DataExchangeView.FileExport.EXPORT_FAILED), ex);
      } finally {
        download.getSession().unlock();
      }
    }
  }
}
