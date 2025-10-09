/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.util.stream.Stream;
import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

public interface VendorService {
    @NonNull
    Stream<DataModel.Vendor> findByBooth(DataModel.Booth.Key booth);
}
