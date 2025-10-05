/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.util.stream.Stream;
import lombok.NonNull;

/**
 * Service for data exchange operations.
 */
public interface DataExchangeService {
    /**
     * Export local data to the given data object and return it.
     * @return the exported data object
     */
    @NonNull
    Stream<ServiceModel.ExchangeData> exportLocalData();

    /**
     * Import remote data from the given data object.
     * @param data the data object to import from
     */
    void importRemoteData(@NonNull ServiceModel.ExchangeData data);
}
