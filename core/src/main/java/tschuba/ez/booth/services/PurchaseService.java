/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

import java.util.Optional;
import java.util.stream.Stream;

public interface PurchaseService {

    @NonNull
    DataModel.Purchase checkout(@NonNull ServiceModel.Checkout checkout);

    @NonNull
    Optional<DataModel.Purchase> getPurchaseByKey(@NonNull DataModel.Purchase.Key purchase);

    @NonNull
    Stream<DataModel.Purchase> getPurchasesByBooth(@NonNull DataModel.Booth.Key booth);
}
