/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.model;

import java.util.stream.Stream;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.PurchaseService;
import tschuba.ez.booth.ui.components.event.BoothSelection;

public class PurchaseGrid<F>
        extends ModelGrid<DataModel.Purchase, ModelFilter<DataModel.Purchase, F>, F> {
    public PurchaseGrid(PurchaseService purchaseService) {
        super(
                () ->
                        BoothSelection.get()
                                .map(purchaseService::getPurchasesByBooth)
                                .orElse(Stream.empty()));
    }
}
