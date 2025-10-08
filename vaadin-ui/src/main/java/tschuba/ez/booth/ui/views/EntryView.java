package tschuba.ez.booth.ui.views;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.basarix.services.EventService;
import tschuba.ez.booth.ui.components.event.EventSelection;
import tschuba.ez.booth.ui.layouts.app.BasicAppLayout;

@Route(value = "", layout = BasicAppLayout.class)
public class EntryView extends EventSelectionView {
    public EntryView(@Autowired EventService eventService) {
        super(eventService);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);

        if (EventSelection.get().isPresent()) {
            event.rerouteTo(CheckoutView.class);
        }
    }
}
