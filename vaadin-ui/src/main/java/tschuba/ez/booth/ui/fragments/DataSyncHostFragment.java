/* Licensed under MIT

Copyright (c) 2023-2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.fragments;

import static java.util.Collections.emptyList;
import static org.vaadin.lineawesome.LineAwesomeIcon.COPY_SOLID;
import static org.vaadin.lineawesome.LineAwesomeIcon.PLAY_SOLID;
import static tschuba.basarix.sync.DataSyncSession.filterByEvent;
import static tschuba.ez.booth.ui.i18n.TranslationKeys.DataSyncHostFragment.*;
import static tschuba.commons.vaadin.components.Spacing.spacing;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.basarix.data.model.EventKey;
import tschuba.basarix.sync.*;
import tschuba.ez.booth.ui.components.event.EventSelection;
import tschuba.ez.booth.ui.renderer.SubscriberRenderer;
import tschuba.ez.booth.ui.util.BadgeBuilder;
import tschuba.ez.booth.ui.util.DataSyncSessionEventBroadcaster;
import tschuba.ez.booth.ui.util.HostUtils;
import tschuba.commons.vaadin.Notifications;
import tschuba.commons.vaadin.components.Buttons;
import tschuba.commons.vaadin.components.Spacing;

@SpringComponent
@UIScope
public class DataSyncHostFragment extends VerticalLayout {
    private static final String SYNC_ID_PROPERTY = "ez-basar-data-sync-id";

    private final DataSyncService dataSyncService;

    private final Span syncUrl = new Span();
    private final Span syncUrlShort = new Span();
    private final Span syncUrlDivider = new Span();
    private final Button syncDataButton = new Button(PLAY_SOLID.create());
    private final NativeLabel syncUrlLabel = new NativeLabel();
    private final ProgressBar subscribersPlaceholder = new ProgressBar();
    private final Span subscribersLabel = new Span();
    private final ListBox<Subscriber> subscribersList = new ListBox<>();
    private final Popover busyIndicator = new Popover();
    private final Span busyIndicatorText = new Span();
    private final Button syncUrlShortButton;
    private final Button syncUrlButton;
    private Registration broadcasterRegistration;

    @Autowired
    public DataSyncHostFragment(@Nonnull DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;

        syncDataButton.setDisableOnClick(true);
        syncDataButton.addClickListener(Buttons.enableAfterClick(this::onClickSyncData));

        subscribersLabel.setId("subscribers-label");
        subscribersLabel.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);

        subscribersPlaceholder.setIndeterminate(true);
        subscribersPlaceholder.getElement().setAttribute("aria-labelledby", subscribersLabel.getId().orElseThrow());

        subscribersList.setRenderer(SubscriberRenderer.renderSubscriber());
        subscribersList.setReadOnly(true);

        VerticalLayout activeSyncBlock = new VerticalLayout();
        activeSyncBlock.setPadding(false);
        spacing(activeSyncBlock).xsmall();
        activeSyncBlock.add(subscribersPlaceholder, subscribersLabel, subscribersList);

        BadgeBuilder badgeBuilder = BadgeBuilder.primary().contrast();
        syncUrl.addClassNames(FontWeight.BLACK);
        badgeBuilder.apply(syncUrl);
        syncUrlButton = createSyncUrlButton(syncUrl);

        syncUrlShort.addClassNames(FontWeight.BLACK);
        badgeBuilder.apply(syncUrlShort);
        syncUrlShortButton = createSyncUrlButton(syncUrlShort);

        HorizontalLayout shortUrlContainer = new HorizontalLayout(Alignment.CENTER, syncUrlLabel, syncUrlShortButton);
        HorizontalLayout urlContainer = new HorizontalLayout(Alignment.CENTER, syncUrlDivider, syncUrlButton);
        VerticalLayout syncUrlContainer = new VerticalLayout(shortUrlContainer, urlContainer);
        syncUrlContainer.setAlignItems(Alignment.CENTER);
        Stream.of(shortUrlContainer, urlContainer, syncUrlContainer).map(Spacing::spacing).forEach(Spacing::xsmall);

        busyIndicatorText.setId("busy-indicator-label");
        ProgressBar syncProgress = new ProgressBar();
        syncProgress.setIndeterminate(true);
        syncProgress.getElement().setAttribute("aria-labelledby", busyIndicatorText.getId().orElseThrow());
        busyIndicator.setTarget(syncDataButton);
        busyIndicator.setModal(true);
        busyIndicator.setBackdropVisible(true);
        busyIndicator.setPosition(PopoverPosition.TOP);
        VerticalLayout busyIndicatorLayout = new VerticalLayout(busyIndicatorText, syncProgress);
        spacing(busyIndicatorLayout).xsmall();
        busyIndicator.add(busyIndicatorLayout);

        this.add(syncUrlContainer, activeSyncBlock, syncDataButton, busyIndicator);
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.addClassNames(Padding.NONE);
        spacing(this).small();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        syncDataButton.setText(getTranslation(SYNC_DATA_BUTTON__TEXT));
        syncDataButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        Tooltip.forComponent(syncDataButton).setText(getTranslation(SYNC_DATA_DESCRIPTION__TEXT));

        try {
            URI externalUrl = HostUtils.externalUrl();
            syncUrl.setText(externalUrl.toString());

            String encodedUrl = SyncUrlCodec.encode(externalUrl);
            syncUrlShort.setText(encodedUrl);
        } catch (RuntimeException ex) {
            // TODO: display warning
        }

        String syncUrlDescription = getTranslation(SYNC_URL_DESCRIPTION__TEXT);
        Tooltip.forComponent(syncUrl).setText(syncUrlDescription);
        Tooltip.forComponent(syncUrlShort).setText(syncUrlDescription);

        syncUrlLabel.setText(getTranslation(SYNC_URL_LABEL__TEXT));
        syncUrlDivider.setText(getTranslation(SYNC_URL_DIVIDER__TEXT));

        busyIndicatorText.setText(getTranslation(SYNC_IN_PROGRESS__TEXT));

        DataSyncSession syncSession = EventSelection.get().map(event -> {
            Optional<DataSyncSession> existingSession = dataSyncService.sessions().filter(filterByEvent(event)).findFirst();
            return existingSession.orElseGet(() -> dataSyncService.createSession(event));
        }).orElse(null);
        updateView(syncSession);

        broadcasterRegistration = DataSyncSessionEventBroadcaster.register(
                event -> attachEvent.getUI().access(() -> onDataSyncSessionEvent(event)));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    private void onClickSyncData(ClickEvent<Button> clickEvent) {
        try {
            busyIndicator.open();
            String syncId = clickEvent.getSource().getElement().getProperty(SYNC_ID_PROPERTY);
            SyncId identifier = new SyncId(syncId);
            dataSyncService.startSync(identifier);
            Notifications.success(getTranslation(SYNC_COMPLETED));
        } catch (Exception ex) {
            Notifications.error(getTranslation(SYNC_FAILED), ex);
        } finally {
            busyIndicator.close();
        }
    }

    private void onDataSyncSessionEvent(DataSyncSessionEvent event) {
        EventKey selectedEvent = EventSelection.get().orElse(null);
        DataSyncSession session = event.getSource();
        if (Objects.equals(session.getEvent(), selectedEvent)) {
            DataSyncSession sessionForUpdate = (!DataSyncSessionEvent.Type.Closed.equals(event.type())) ? session : null;
            updateView(sessionForUpdate);
        }
    }

    private void updateView(@Nullable DataSyncSession session) {
        boolean sessionIsNull = session == null;
        Optional<DataSyncSession> sessionOptional = Optional.ofNullable(session);
        List<Subscriber> subscribers = sessionOptional.map(DataSyncSession::subscribers).orElse(emptyList());
        Boolean sessionLocked = sessionOptional.map(DataSyncSession::isLocked).orElse(false);

        Optional<String> syncId = sessionOptional.map(DataSyncSession::getIdentifier).map(SyncId::id);
        syncId.ifPresentOrElse(id -> syncDataButton.getElement().setProperty(SYNC_ID_PROPERTY, id), () -> syncDataButton.getElement().removeProperty(SYNC_ID_PROPERTY));

        syncDataButton.setVisible(!sessionIsNull);
        boolean canSync = !sessionLocked && !subscribers.isEmpty();
        syncDataButton.setEnabled(canSync);
        if (canSync) {
            syncDataButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        } else {
            syncDataButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }
        syncUrlLabel.setVisible(!sessionIsNull);
        syncUrlButton.setVisible(!sessionIsNull);
        syncUrlShortButton.setVisible(!sessionIsNull);
        syncUrlDivider.setVisible(!sessionIsNull);
        subscribersLabel.setVisible(!sessionIsNull);

        subscribersList.setItems(subscribers);
        subscribersList.setVisible(!subscribers.isEmpty());
        subscribersPlaceholder.setVisible(!sessionIsNull && subscribers.isEmpty());
        if (subscribers.isEmpty()) {
            subscribersLabel.setText(getTranslation(NO_SUBSCRIPTIONS__TEXT));
        } else {
            subscribersLabel.setText(getTranslation(WAITING_FOR_SUBSCRIPTION__TEXT, subscribers.size()));
        }
    }

    private Button createSyncUrlButton(final Span urlContainer) {
        HorizontalLayout syncUrlShortButtonContent = new HorizontalLayout(urlContainer, COPY_SOLID.create());
        spacing(syncUrlShortButtonContent).xsmall();
        Button button = new Button(syncUrlShortButtonContent);
        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        button.addClassNames(LumoUtility.Margin.NONE);

        button.addClickListener(event -> event.getSource().getUI().ifPresent(ui -> {
            ui.getPage().executeJs("""
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
                    window.copyToClipboard($0)""", urlContainer.getText());
            Notifications.message(getTranslation(SYNC_URL_COPIED_TO_CLIPBOARD__NOTIFICATION));
        }));

        return button;
    }
}
