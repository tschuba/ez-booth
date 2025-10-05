/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import lombok.NonNull;

/**
 * Service for data exchange operations.
 */
public interface DataExchangeService {
    @NonNull
    ServiceModel.ExchangeData exportLocalData(@NonNull ServiceModel.ExchangeData data);

    void importRemoteData(@NonNull ServiceModel.ExchangeData data);
}
