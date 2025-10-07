package tschuba.basarix.ui.components.model;

import tschuba.basarix.data.model.Purchase;
import tschuba.basarix.services.EventService;
import tschuba.basarix.ui.components.event.EventSelection;

import java.util.stream.Stream;

public class PurchaseGrid<F> extends ModelGrid<Purchase, ModelFilter<Purchase, F>, F> {
    public PurchaseGrid(EventService eventService) {
        super(() -> EventSelection.get().map(eventService::allPurchases).orElse(Stream.empty()));
    }
}
