package tschuba.ez.booth.services;

import lombok.Builder;
import lombok.NonNull;
import tschuba.ez.booth.DataModel;

import java.math.BigDecimal;
import java.util.List;

public final class ServiceModel {
    private ServiceModel() {
    }

    @Builder
    public record Checkout(
            @NonNull DataModel.BoothEvent.Key event,
            @NonNull List<DataModel.PurchaseItem> items,
            boolean printReceipt
    ) {
    }

    @Builder
    public record ChargedFees(
            @NonNull BigDecimal participationFee,
            @NonNull BigDecimal salesFee
    ) {
    }

    @Builder
    public record ChargingConfig(
            @NonNull BigDecimal participationFee,
            @NonNull BigDecimal salesFeePercent,
            @NonNull BigDecimal roundingStep
    ) {
    }

    public final static class Balance {
        private Balance() {
        }

        @Builder
        public record Input(
                @NonNull DataModel.BoothEvent.Key event,
                @NonNull DataModel.Vendor.Key vendor
        ) {
        }

        @Builder
        public record Output(
                @NonNull BigDecimal totalRevenue,
                @NonNull ChargedFees chargedFees
        ) {

        }
    }

    @Builder
    public record VendorReportData(
            @NonNull DataModel.Vendor vendor,
            @NonNull DataModel.BoothEvent event,
            @NonNull List<DataModel.PurchaseItem> items
    ) {
    }
}
