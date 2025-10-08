package tschuba.ez.booth.ui.views;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.layouts.app.BasicAppLayout;

@Route(value = "", layout = BasicAppLayout.class)
public class EntryView extends BoothSelectionView {
    public EntryView(@Autowired BoothService boothService,
                     @Autowired BoothRepository booths) {
        super(boothService, booths);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);

        if (BoothSelection.get().isPresent()) {
            event.rerouteTo(CheckoutView.class);
        }
    }
}
