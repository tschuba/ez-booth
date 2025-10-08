package tschuba.ez.booth.ui.views;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import tschuba.basarix.data.model.VendorKey;
import tschuba.basarix.reporting.ReportingException;
import tschuba.basarix.reporting.VendorReportingService;
import tschuba.ez.booth.ui.layouts.BaseLayout;
import tschuba.ez.booth.ui.util.RoutingParameters;
import tschuba.commons.vaadin.NavigateTo;
import tschuba.commons.vaadin.Notifications;

import java.io.File;
import java.util.Optional;

import static tschuba.ez.booth.ui.i18n.TranslationKeys.VendorReportPrintView.*;

@Route(value = "reports/vendor/print/:eventId/:vendorId")
public class VendorReportPrintView extends BaseLayout implements BeforeEnterObserver {
    private final VendorReportingService reportingService;

    public VendorReportPrintView(VendorReportingService reportingService) {
        this.reportingService = reportingService;

        setTitle(getTranslation(TITLE));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent viewEvent) {
        Optional<VendorKey> parameter = RoutingParameters.parser(viewEvent.getRouteParameters()).vendorKey();
        if (parameter.isEmpty()) {
            String message = getTranslation(NOTIFICATION__ILLEGAL_ARGUMENTS);
            Notifications.error(message);
            return;
        }

        VendorKey vendor = parameter.get();
        File reportFile;
        try {
            reportFile = reportingService.generateVendorReport(vendor);
        } catch (ReportingException ex) {
            String message = getTranslation(NOTIFICATION__REPORT_GENERATION_FAILED);
            Notifications.error(message, ex);
            return;
        }

        NavigateTo.file(reportFile).currentWindow();
    }

    public static void newWindowFor(VendorKey vendorKey) {
        RouteParameters routeParameters = RoutingParameters.builder()
                .vendor(vendorKey)
                .build();
        NavigateTo.view(VendorReportPrintView.class, routeParameters).newWindow();
    }
}
