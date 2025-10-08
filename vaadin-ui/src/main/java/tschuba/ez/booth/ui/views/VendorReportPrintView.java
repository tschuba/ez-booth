/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import static tschuba.ez.booth.ui.i18n.TranslationKeys.VendorReportPrintView.*;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import java.net.URI;
import java.util.Optional;
import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.reporting.ReportingException;
import tschuba.ez.booth.services.ReportingService;
import tschuba.ez.booth.ui.layouts.BaseLayout;
import tschuba.ez.booth.ui.util.*;

@Route(value = "reports/vendor/print/:eventId/:vendorId")
public class VendorReportPrintView extends BaseLayout implements BeforeEnterObserver {
    @NonNull private final ReportViewHelper helper;
    private final ReportingService reportingService;

    public VendorReportPrintView(
            @NonNull ReportViewHelper helper, @NonNull ReportingService reportingService) {
        this.helper = helper;
        this.reportingService = reportingService;

        setTitle(getTranslation(TITLE));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent viewEvent) {
        Optional<DataModel.Vendor.Key> parameter =
                Routing.Parameters.parser(viewEvent.getRouteParameters()).vendorKey();
        if (parameter.isEmpty()) {
            String message = getTranslation(NOTIFICATION__ILLEGAL_ARGUMENTS);
            Notifications.error(message);
            return;
        }

        DataModel.Vendor.Key vendor = parameter.get();
        URI reportFile;
        try {
            reportFile = reportingService.generateVendorReport(vendor);
        } catch (ReportingException ex) {
            String message = getTranslation(NOTIFICATION__REPORT_GENERATION_FAILED);
            Notifications.error(message, ex);
            return;
        }

        URI reportUrl = helper.reportUrl(reportFile);
        NavigateTo.uri(reportUrl).currentWindow();
    }

    public static void newWindowFor(DataModel.Vendor.Key vendorKey) {
        RouteParameters routeParameters = Routing.Parameters.builder().vendor(vendorKey).build();
        NavigateTo.view(VendorReportPrintView.class, routeParameters).newWindow();
    }
}
