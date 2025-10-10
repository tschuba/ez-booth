/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import lombok.NonNull;

/**
 * Service for data exchange operations.
 */
public interface DataExchangeService extends DataService {
  /**
   * Receive data, process it and return an updated exchange dataset.
   * @param dataReceived the received data
   * @return the updated data
   */
  @NonNull
  ServiceModel.ExchangeData exchangeData(ServiceModel.ExchangeData dataReceived);
}
