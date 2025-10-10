/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

/**
 * Service interface for managing vendors.
 */
public interface VendorService {
  @NonNull
  Stream<DataModel.Vendor> findByBooth(DataModel.Booth.Key booth);

  @NonNull
  Optional<DataModel.Vendor> findById(@NonNull DataModel.Vendor.Key key);

  void save(@NonNull DataModel.Vendor vendor);
}
