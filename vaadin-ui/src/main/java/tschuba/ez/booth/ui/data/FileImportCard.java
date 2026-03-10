package tschuba.ez.booth.ui.data;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.server.streams.InMemoryUploadCallback;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.TransferContext;
import com.vaadin.flow.server.streams.TransferProgressListener;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.unit.DataSize;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.i18n.UploadTranslation;
import tschuba.ez.booth.proto.ProtoServices;
import tschuba.ez.booth.ui.components.BusyIndicator;
import tschuba.ez.booth.ui.services.DataExchangeClient;
import tschuba.ez.booth.ui.util.Notifications;

/**
 * Card component for importing booth data from a file.
 */
@SpringComponent
@UIScope
@Slf4j
public class FileImportCard extends Composite<Card> {

  private final @NonNull DataExchangeClient dataExchangeClient;

  private final BusyIndicator busyIndicator = new BusyIndicator();
  private final Upload upload;

  public FileImportCard(@NonNull DataExchangeClient dataExchangeClient) {
    this.dataExchangeClient = dataExchangeClient;

    InMemoryUploadHandler uploadHandler =
        UploadHandler.inMemory(new ImportUploadCallback(), new ImportProgressHandler());
    upload = new Upload(uploadHandler);
    upload.setAcceptedFileTypes("application/ez-booth", DataExchangeView.DATA_FILE_EXTENSION);
    upload.setMaxFiles(1);
    upload.setMaxFileSize((int) DataSize.ofMegabytes(50).toBytes());

    Card content = getContent();
    content.setHeaderPrefix(LineAwesomeIcon.FILE_IMPORT_SOLID.create());
    content.add(upload);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    getContent().setTitle(getTranslation(TranslationKeys.DataExchangeView.FileImport.TITLE));

    UploadI18N uploadI18N = new UploadTranslation().get(getLocale());
    upload.setI18n(uploadI18N);
  }

  /**
   * Callback for handling uploaded data files.
   */
  class ImportUploadCallback implements InMemoryUploadCallback {
    @Override
    public void complete(UploadMetadata metadata, byte[] bytes) {
      try {
        ProtoServices.ExchangeData exchangeData = ProtoServices.ExchangeData.parseFrom(bytes);
        String boothDescription = exchangeData.getBooth().getDescription();
        log.debug("Received data upload for booth '{}'", boothDescription);
        dataExchangeClient.mergeData(exchangeData);
        log.debug(
            "Data upload with booth description '{}' merged successfully", boothDescription);
      } catch (InvalidProtocolBufferException ex) {
        log.error("Failed to parse uploaded data file", ex);
        Notifications.error(getTranslation(TranslationKeys.DataExchangeView.FileImport.UPLOAD_FAILED), ex);
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
