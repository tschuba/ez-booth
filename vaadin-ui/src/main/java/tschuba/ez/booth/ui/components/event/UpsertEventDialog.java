package tschuba.ez.booth.ui.components.event;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import tschuba.basarix.data.model.Event;
import tschuba.basarix.services.EventService;

import java.util.Optional;

import static tschuba.ez.booth.ui.i18n.TranslationKeys.UpsertEventDialog.*;

public class UpsertEventDialog extends Dialog {
    private final UpsertEventForm upsertEventForm;
    private final EventService eventService;
    private final Button saveButton;

    public UpsertEventDialog(EventService eventService) {
        super();
        this.eventService = eventService;

        setHeaderTitle(getTranslation(TITLE));
        setCloseOnEsc(true);

        upsertEventForm = new UpsertEventForm();
        upsertEventForm.addCreateEventFormSubmitListener(this::onCreateEventFormSubmitEvent);

        saveButton = new Button();
        saveButton.setText(getTranslation(SAVE_BUTTON__TEXT));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(this::onSaveEvent);

        Button cancelButton = new Button();
        cancelButton.setText(getTranslation(CANCEL_BUTTON__TEXT));
        cancelButton.addClickListener(this::onClickCancel);

        Footer footer = new Footer(cancelButton, saveButton);
        footer.addClassNames(Display.FLEX, AlignItems.CENTER, JustifyContent.END, Gap.MEDIUM, Margin.Top.MEDIUM);

        add(upsertEventForm, footer);
    }

    @Override
    public void open() {
        super.open();
        upsertEventForm.clear();
    }

    public void open(Event event) {
        this.open();
        upsertEventForm.setEvent(event);
    }

    public void addEventSavedListener(ComponentEventListener<EventSavedEvent> listener) {
        addListener(EventSavedEvent.class, listener);
    }

    private void onCreateEventFormSubmitEvent(CreateEventFormSubmitEvent event) {
        onSaveEvent(event);
    }

    private void onClickCancel(ClickEvent<Button> buttonClickEvent) {
        this.close();
    }

    private void onSaveEvent(ComponentEvent<? extends Component> event) {
        saveButton.setEnabled(false);
        Optional<Event> formData = upsertEventForm.validate(true);
        if (formData.isPresent()) {
            Event eventData = formData.get();
            eventService.save(eventData);
            close();
            fireEvent(new EventSavedEvent(this, event.isFromClient(), eventData));
        }
        saveButton.setEnabled(true);
    }
}
