package tschuba.basarix.ui.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.upload.*;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import lombok.NonNull;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.data.model.Event;
import tschuba.basarix.data.model.EventKey;
import tschuba.basarix.services.DataConsolidationService;
import tschuba.basarix.services.EventService;
import tschuba.basarix.ui.components.SectionTitle;
import tschuba.basarix.ui.components.event.EventRequired;
import tschuba.basarix.ui.components.event.EventSelection;
import tschuba.basarix.ui.fragments.DataSyncFragment;
import tschuba.basarix.ui.layouts.OneColumnLayout;
import tschuba.basarix.ui.layouts.app.AppLayoutWithMenu;
import tschuba.basarix.ui.resources.StreamResourceEvent;
import tschuba.basarix.ui.resources.StreamResourceFailedEvent;
import tschuba.basarix.ui.resources.StreamResourceHandler;
import tschuba.basarix.ui.resources.StreamResourceListener;
import tschuba.commons.vaadin.Notifications;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.basarix.ui.i18n.TranslationKeys.ExportImportView.*;

@Route(value = "export-import", layout = AppLayoutWithMenu.class)
@EventRequired
public class ExportImportView extends OneColumnLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportImportView.class);

    private final EventService eventService;
    private final DataConsolidationService dataConsolidationService;
    private final MemoryBuffer uploadBuffer;
    private final Button exportButton;
    private final Upload uploader;

    @Autowired
    public ExportImportView(@NonNull final EventService eventService,
                            @NonNull DataConsolidationService dataConsolidationService,
                            @NonNull DataSyncFragment dataSyncFragment) {
        this.dataConsolidationService = dataConsolidationService;
        this.eventService = eventService;

        setTitle(getTranslation(TITLE));

        Main content = new Main();
        content.addClassNames(Display.GRID, Grid.Column.COLUMNS_1, Gap.XLARGE, AlignItems.START);
        setContent(content);

        content.add(dataSyncFragment);

        Div importSection = new Div();
        content.add(importSection);

        SectionTitle importHeader = new SectionTitle();
        importHeader.setText(getTranslation(IMPORT_HEADER__TEXT));
        Component importIcon = LineAwesomeIcon.FILE_IMPORT_SOLID.create();
        importIcon.addClassNames(Padding.Right.SMALL);
        importHeader.addComponentAsFirst(importIcon);
        importSection.add(importHeader);

        Paragraph importDescription = createDescription(getTranslation(IMPORT_DESCRIPTION__TEXT));
        importSection.add(importDescription);

        uploadBuffer = new MemoryBuffer();
        uploader = new Upload(uploadBuffer);
        uploader.setAcceptedFileTypes("application/json", ".json");
        uploader.setMaxFiles(1);
        uploader.setDropAllowed(false);
        UploadI18N uploadI18N = tschuba.basarix.ui.i18n.Upload.upload(IMPORT_UPLOADER__I18N_PREFIX, getLocale());
        uploader.setI18n(uploadI18N);
        uploader.addStartedListener(this::onUploadStartedEvent);
        uploader.addFailedListener(this::onUploadFailedEvent);
        uploader.addFileRejectedListener(this::onUploadRejectedEvent);
        uploader.addSucceededListener(this::onUploadSucceededEvent);
        importSection.add(uploader);

        Div exportSection = new Div();
        content.add(exportSection);

        SectionTitle exportHeader = new SectionTitle();
        exportHeader.setText(getTranslation(EXPORT_HEADER__TEXT));
        Component exportIcon = LineAwesomeIcon.FILE_EXPORT_SOLID.create();
        exportIcon.addClassName(Padding.Right.SMALL);
        exportHeader.addComponentAsFirst(exportIcon);
        exportSection.add(exportHeader);

        Paragraph exportDescription = createDescription(getTranslation(EXPORT_DESCRIPTION__TEXT));
        exportSection.add(exportDescription);

        exportButton = new Button();
        exportButton.addClickListener(this::onClickExport);
        updateExportButtonText();
        exportSection.add(exportButton);
    }

    private Paragraph createDescription(String text) {
        Paragraph paragraph = new Paragraph();
        paragraph.setText(text);
        paragraph.addClassNames(TextColor.SECONDARY);
        paragraph.setWidth(30, Unit.EM);
        return paragraph;
    }

    private void updateExportButtonText() {
        String currentEvent = EventSelection.get().flatMap(eventService::byKey).map(Event::getDescription).orElse("");
        exportButton.setText(getTranslation(EXPORT_BUTTON__TEXT, currentEvent));
    }

    private void onClickExport(ClickEvent<Button> clickEvent) {
        Optional<EventKey> currentEvent = EventSelection.get();
        if (currentEvent.isPresent()) {
            EventKey eventKey = currentEvent.get();
            String resourceName = String.format("export_%s.json", eventKey.getId());
            InputStreamFactory inputStreamFactory = () -> {
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    dataConsolidationService.export(eventKey, outputStream);
                    return IOUtils.copy(outputStream);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            };

            StreamResourceHandler resourceHandler = StreamResourceHandler.createAndRegister(resourceName, inputStreamFactory);
            resourceHandler.addListener(new StreamResourceListener() {
                @Override
                public void consumed(StreamResourceEvent event) {
                    exportSucceeded();
                    // unregister stream after download
                    unregister(event);
                }

                @Override
                public void failed(StreamResourceFailedEvent event) {
                    Exception error = event.getError();
                    exportFailed(error);
                    LOGGER.error("Failed to pipe export stream to download!", error);
                    // unregister stream after download
                    unregister(event);
                }

                private void unregister(StreamResourceEvent event) {
                    event.getRegistration().unregister();
                }
            });

            resourceHandler.resource().setHeader("Content-Disposition", String.format("attachment; filename =\"%s\"", resourceName));
            URI downloadUri = resourceHandler.resourceUri();
            LOGGER.debug("Redirecting to export URI: {}", downloadUri);
            UI.getCurrent().getPage().setLocation(downloadUri);
        }
    }

    private void exportSucceeded() {
        Notifications.success(getTranslation(NOTIFICATION__EXPORT_SUCCEEDED));
    }

    private void exportFailed(Exception error) {
        Notifications.error(getTranslation(NOTIFICATION__EXPORT_FAILED), error);
    }

    private void onUploadRejectedEvent(FileRejectedEvent event) {
        LOGGER.debug("Upload of file rejected: {}", event.getErrorMessage());
    }

    private void onUploadStartedEvent(StartedEvent event) {
        LOGGER.debug("Upload of file '{}' started (MIME Type: {})", event.getFileName(), event.getMIMEType());
    }

    private void onUploadFailedEvent(FailedEvent event) {
        LOGGER.warn(String.format("Upload of file '%s' (MIME Type: %s) failed!", event.getFileName(), event.getMIMEType()), event.getReason());
        Notifications.error(getTranslation(NOTIFICATION__UPLOAD_FAILED), event.getReason());
    }

    private void onUploadSucceededEvent(SucceededEvent event) {
        LOGGER.info("Upload of file {} succeeded", event.getFileName());
        Notifications.success(getTranslation(NOTIFICATION__UPLOAD_SUCCEEDED));
        try {
            dataConsolidationService.importData(uploadBuffer.getInputStream());
            Notifications.success(getTranslation(NOTIFICATION__IMPORT_SUCCEEDED));
            uploader.clearFileList();
        } catch (Exception ex) {
            Notifications.error(getTranslation(NOTIFICATION__IMPORT_FAILED), ex);
        }
    }
}
