/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

/**
 * Service for data import and export operations.
 */
public interface DataService {
  /**
   * Merge the provided data into the existing dataset.
   * @param data the data to merge
   * @return the key of the booth the data was merged into
   */
  DataModel.Booth.Key merge(@NonNull ServiceModel.ExchangeData data);

  /**
   * Export the data for the specified booth.
   * @param booth the booth to export data for
   * @return the exported data
   */
  @NonNull
  ServiceModel.ExchangeData export(@NonNull DataModel.Booth.Key booth);
}
