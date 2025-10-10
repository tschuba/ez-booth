/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

/**
 * Service interface for calculating fees and balances.
 */
public interface ChargingService {

  /**
   * Calculate the fees for a given vendor.
   *
   * @param vendor the vendor key
   * @return the calculated fees
   */
  @NonNull
  ServiceModel.ChargedFees calculateFees(@NonNull DataModel.Vendor.Key vendor);

  /**
   * Calculate the balance for a given vendor.
   *
   * @param input the balance input data
   * @return the calculated balance
   */
  @NonNull
  ServiceModel.Balance.Output calculateBalance(@NonNull ServiceModel.Balance.Input input);
}
