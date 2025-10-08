package tschuba.ez.booth.ui.components.model;

import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.PurchaseService;
import tschuba.ez.booth.ui.components.event.BoothSelection;

import java.util.stream.Stream;

public class PurchaseGrid<F> extends ModelGrid<DataModel.Purchase, ModelFilter<DataModel.Purchase, F>, F> {
    public PurchaseGrid(PurchaseService purchaseService) {
        super(() -> BoothSelection.get().map(purchaseService::getPurchasesByBooth).orElse(Stream.empty()));
    }
}
