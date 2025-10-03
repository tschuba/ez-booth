package tschuba.ez.booth.services;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;

import java.net.URI;

/**
 * Local service implementation for reporting.
 * This class provides the core business logic for generating reports.
 */
@Service
public class ReportingLocalService implements ReportingService {
    @Override
    public @NonNull ServiceModel.VendorReportData createVendorReportData(DataModel.Vendor.@NonNull Key vendor) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull URI generateVendorReport(DataModel.Vendor.@NonNull Key... vendors) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
