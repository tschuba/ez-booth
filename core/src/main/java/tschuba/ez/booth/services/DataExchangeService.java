/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

/**
 * Service for data exchange operations.
 */
public interface DataExchangeService {
    /**
     * Export local data to the given data object and return it.
     * @return the exported data object
     */
    @NonNull
    ServiceModel.ExchangeData exportLocalData(DataModel.Booth.Key boothId);

    /**
     * Import remote data from the given data object.
     * @param data the data object to import from
     */
    void importRemoteData(@NonNull ServiceModel.ExchangeData data);

    /**
     * Subscribe for exchange data using the given receiver.
     * @param receiver the receiver to use
     * @return the subscription object
     */
    ServiceModel.ExchangeSubscription subscribeForExchange(
            @NonNull ServiceModel.ExchangeReceiver receiver);
}
