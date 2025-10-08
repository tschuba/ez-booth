/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.event;

import static tschuba.ez.booth.i18n.TranslationKeys.UpsertEventDialog.*;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import java.util.Optional;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;

public class UpsertEventDialog extends Dialog {
    private final UpsertEventForm upsertEventForm;
    private final BoothRepository booths;
    private final Button saveButton;

    public UpsertEventDialog(BoothRepository booths) {
        super();
        this.booths = booths;

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
        footer.addClassNames(
                Display.FLEX, AlignItems.CENTER, JustifyContent.END, Gap.MEDIUM, Margin.Top.MEDIUM);

        add(upsertEventForm, footer);
    }

    @Override
    public void open() {
        super.open();
        upsertEventForm.clear();
    }

    public void open(DataModel.Booth booth) {
        this.open();
        upsertEventForm.setEvent(booth);
    }

    public void addEventSavedListener(ComponentEventListener<BoothSavedEvent> listener) {
        addListener(BoothSavedEvent.class, listener);
    }

    private void onCreateEventFormSubmitEvent(CreateEventFormSubmitEvent event) {
        onSaveEvent(event);
    }

    private void onClickCancel(ClickEvent<Button> buttonClickEvent) {
        this.close();
    }

    private void onSaveEvent(ComponentEvent<? extends Component> event) {
        saveButton.setEnabled(false);
        Optional<DataModel.Booth> formData = upsertEventForm.validate(true);
        if (formData.isPresent()) {
            DataModel.Booth boothData = formData.get();
            EntityModel.Booth entity = EntitiesMapper.objectToEntity(boothData);
            booths.save(entity);
            close();
            fireEvent(new BoothSavedEvent(this, event.isFromClient(), boothData));
        }
        saveButton.setEnabled(true);
    }
}
