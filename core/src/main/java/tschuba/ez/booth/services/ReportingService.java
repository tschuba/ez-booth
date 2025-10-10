/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.net.URI;
import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

/**
 * Service interface for generating reports.
 */
public interface ReportingService {
  /**
   * Create a vendor report data for the given vendor.
   *
   * @param vendor the vendor key
   * @return the vendor report data
   */
  @NonNull
  ServiceModel.VendorReportData createVendorReportData(@NonNull DataModel.Vendor.Key vendor);

  /**
   * Generate a vendor report for the given vendors.
   *
   * @param vendors the vendor keys
   * @return the URI to the generated vendor report
   */
  @NonNull
  URI generateVendorReport(@NonNull DataModel.Vendor.Key... vendors);
}
