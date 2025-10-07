package tschuba.basarix.ui.views;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.basarix.services.EventService;
import tschuba.basarix.ui.components.event.EventSelection;
import tschuba.basarix.ui.layouts.app.BasicAppLayout;

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
