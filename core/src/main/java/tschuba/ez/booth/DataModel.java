package tschuba.ez.booth;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class DataModel {
    private DataModel() {
    }

    @Builder
    public record BoothEvent(
            Key key,
            String description,
            LocalDate date,
            BigDecimal participationFee,
            BigDecimal salesFee,
            BigDecimal feesRoundingStep,
            boolean closed,
            LocalDateTime closedOn
    ) {

        @Builder
        public record Key(
                String eventId
        ) {
        }

    }

    @Builder
    public record Purchase(
            Key key,
            List<PurchaseItem> items,
            BigDecimal value,
            LocalDateTime purchasedOn
    ) {

        @Builder
        public record Key(
                BoothEvent.Key event,
                String purchaseId
        ) {
        }
    }

    @Builder
    public record PurchaseItem(
            Key key,
            BigDecimal price,
            LocalDateTime purchasedOn
    ) {

        @Builder
        public record Key(
                Vendor.Key vendor,
                String itemId
        ) {
        }
    }

    @Builder
    public record Vendor(
            Key key
    ) {

        @Builder
        public record Key(
                BoothEvent.Key event,
                String vendorId
        ) {
        }
    }
}
