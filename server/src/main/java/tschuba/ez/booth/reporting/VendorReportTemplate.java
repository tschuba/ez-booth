/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.reporting;

import java.util.List;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.services.ServiceModel;

/**
 * HTML report template for vendor reports.
 */
public class VendorReportTemplate
        extends ClassLoaderHtmlTemplate<List<ServiceModel.VendorReportData>> {

    private static final String TEMPLATE = "VendorReport";

    /**
     * Creates a new HTML report template instance.
     */
    public VendorReportTemplate() {
        super(TEMPLATE);
    }

    /**
     * Generates a filename for the vendor report.
     *
     * @return the generated filename
     */
    public static String reportFileName() {
        return Reports.htmlFileName(TEMPLATE);
    }

    /**
     * Generates a filename for the vendor report for a specific vendor.
     *
     * @param vendor the vendor key
     * @return the generated filename
     */
    public static String reportFileName(DataModel.Vendor.Key vendor) {
        String prefix = TEMPLATE + "%s-%s".formatted(vendor.booth().boothId(), vendor.vendorId());
        return Reports.htmlFileName(prefix);
    }
}
