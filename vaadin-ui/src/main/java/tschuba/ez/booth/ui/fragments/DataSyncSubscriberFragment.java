/* Licensed under MIT

Copyright (c) 2023-2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.fragments;

import static org.vaadin.lineawesome.LineAwesomeIcon.SIGN_IN_ALT_SOLID;
import static org.vaadin.lineawesome.LineAwesomeIcon.SIGN_OUT_ALT_SOLID;
import static tschuba.basarix.sync.SyncUtils.createSubscriberId;
import static tschuba.ez.booth.ui.i18n.TranslationKeys.DataSyncSubscriberFragment.*;
import static tschuba.commons.vaadin.components.Spacing.spacing;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import java.net.URI;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.basarix.data.model.Event;
import tschuba.basarix.services.EventService;
import tschuba.basarix.sync.DataSyncConfig;
import tschuba.basarix.sync.Subscriber;
import tschuba.basarix.sync.SubscriberId;
import tschuba.basarix.sync.SyncId;
import tschuba.basarix.sync.api.DataSyncApiClient;
import tschuba.basarix.sync.api.internal.SyncUrl;
import tschuba.ez.booth.ui.Constraints;
import tschuba.ez.booth.ui.components.event.EventSelection;
import tschuba.ez.booth.ui.renderer.SubscriberRenderer;
import tschuba.ez.booth.ui.util.HostUtils;
import tschuba.commons.vaadin.Notifications;

@SpringComponent
@UIScope
public class DataSyncSubscriberFragment extends VerticalLayout {

    private static final String SYNC_ID_PROPERTY = "ez-basar-sync-id";
    private static final String SYNC_URL_PROPERTY = "ez-basar-sync-url";

    private final DataSyncApiClient dataSyncApiClient;
    private final DataSyncConfig dataSyncConfig;
    private final EventService eventService;

    private final SubscriberId subscriberId;

    private final TextField syncUrlInput = new TextField();
    private final Button joinSyncButton = new Button(SIGN_IN_ALT_SOLID.create());
    private final Button leaveSyncButton = new Button(SIGN_OUT_ALT_SOLID.create());
    private final Paragraph description = new Paragraph();
    private final Component subscriberComponent;

    @Autowired
    public DataSyncSubscriberFragment(
                                      @NonNull DataSyncApiClient dataSyncApiClient, @NonNull DataSyncConfig dataSyncConfig, @NonNull EventService eventService) {
        this.dataSyncApiClient = dataSyncApiClient;
        this.dataSyncConfig = dataSyncConfig;
        this.eventService = eventService;

        this.subscriberId = createSubscriberId();
        this.subscriberComponent = SubscriberRenderer.renderId(this.subscriberId);

        syncUrlInput.addClassNames(LumoUtility.Width.FULL);
        syncUrlInput.setRequired(true);
        syncUrlInput.setRequiredIndicatorVisible(true);
        syncUrlInput.setMinLength(1);
        syncUrlInput.setAutoselect(true);
        syncUrlInput.setPattern(Constraints.DataSync.Subscriber.SYNC_URL_PATTERN);
        syncUrlInput.addClassNames(Padding.Top.NONE);
        syncUrlInput.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        syncUrlInput.addValueChangeListener(this::onSyncUrlValueChange);

        joinSyncButton.addClickListener(this::onClickJoinSync);
        joinSyncButton.setEnabled(false);

        leaveSyncButton.addClickListener(this::onClickLeaveSync);
        leaveSyncButton.setVisible(false);

        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        spacing(this).small();
        this.addClassNames(Padding.NONE);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        syncUrlInput.setLabel(getTranslation(SYNC_URL_LABEL__TEXT));
        syncUrlInput.setHelperText(getTranslation(SYNC_URL_HELP__TEXT));

        joinSyncButton.setText(getTranslation(SYNC_JOIN_BUTTON__TEXT));
        joinSyncButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        leaveSyncButton.setText(getTranslation(SYNC_LEAVE_BUTTON__TEXT));
        leaveSyncButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        Tooltip.forComponent(leaveSyncButton).setText(getTranslation(SYNC_LEAVE_DESCRIPTION__TEXT));

        description.setText(getTranslation(SYNC_JOIN_DESCRIPTION__TEXT));

        HorizontalLayout inputBar = new HorizontalLayout(syncUrlInput, joinSyncButton, leaveSyncButton);
        inputBar.setDefaultVerticalComponentAlignment(Alignment.END);
        this.add(syncUrlInput, joinSyncButton, leaveSyncButton, subscriberComponent);

        handleSyncSubscriptionChange(false);
    }

    private void onSyncUrlValueChange(
                                      AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        joinSyncButton.setEnabled(!event.getSource().isInvalid());
    }

    private void onClickJoinSync(ClickEvent<Button> clickEvent) {
        Optional<Event> event = EventSelection.get().flatMap(eventService::byKey);
        if (event.isEmpty()) {
            // TODO: display notification
            return;
        }

        String hostAddress = SyncUrl.host(syncUrlInput.getValue()).uriString();
        try {
            SyncId syncId = dataSyncApiClient.subscribeToHost(hostAddress, subscriberIdentity(), event.get());
            Element leaveButtonElement = leaveSyncButton.getElement();
            leaveButtonElement.setProperty(SYNC_URL_PROPERTY, hostAddress);
            leaveButtonElement.setProperty(SYNC_ID_PROPERTY, syncId.id());
            handleSyncSubscriptionChange(true);
        } catch (Exception ex) {
            Notifications.error(getTranslation(SYNC_JOIN_FAILED), ex);
        }
    }

    private void onClickLeaveSync(ClickEvent<Button> clickEvent) {
        Element button = clickEvent.getSource().getElement();
        String hostAddress = button.getProperty(SYNC_URL_PROPERTY);
        String syncId = button.getProperty(SYNC_ID_PROPERTY);
        dataSyncApiClient.unsubscribeFromHost(hostAddress, new SyncId(syncId), subscriberIdentity());
        handleSyncSubscriptionChange(false);
    }

    private void handleSyncSubscriptionChange(boolean subscribed) {
        joinSyncButton.setVisible(!subscribed);
        leaveSyncButton.setVisible(subscribed);
        syncUrlInput.setReadOnly(subscribed);

        syncUrlInput.focus();
    }

    Subscriber subscriberIdentity() {
        URI endpoint = HostUtils.externalUrlBuilder().path(dataSyncConfig.apiBasePath()).build().toUri();
        return new Subscriber(subscriberId, endpoint);
    }
}
