package tschuba.ez.booth.services;

import lombok.NonNull;
import tschuba.ez.booth.DataModel;

import java.net.URI;

public interface ReportingService {
    @NonNull
    ServiceModel.VendorReportData createVendorReportData(@NonNull DataModel.Vendor.Key vendor);

    @NonNull
    URI generateVendorReport(@NonNull DataModel.Vendor.Key... vendors);
}
