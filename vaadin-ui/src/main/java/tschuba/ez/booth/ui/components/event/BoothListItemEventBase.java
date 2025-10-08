package tschuba.ez.booth.ui.components.event;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import tschuba.ez.booth.model.DataModel;

@Getter
public class BoothListItemEventBase extends ComponentEvent<BoothListItem> {
    private final DataModel.Booth booth;

    public BoothListItemEventBase(BoothListItem source, boolean fromClient, DataModel.Booth booth) {
        super(source, fromClient);
        this.booth = booth;
    }

}
