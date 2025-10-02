package tschuba.ez.booth;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class DataModel {

    private DataModel() {
    }

    @Builder
    public record Booth(
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
                String boothId
        ) {
        }

    }

    @Builder
    public record Purchase(
            @EqualsAndHashCode.Include
            Key key,
            BigDecimal value,
            LocalDateTime purchasedOn,
            List<PurchaseItem> items
    ) {

        @Builder
        public record Key(
                Booth.Key booth,
                String purchaseId
        ) {
        }
    }

    @Builder
    public record PurchaseItem(
            @EqualsAndHashCode.Include
            Key key,
            @EqualsAndHashCode.Include
            Vendor.Key vendor,
            BigDecimal price,
            LocalDateTime purchasedOn
    ) {

        @Builder
        public record Key(
                Purchase.Key purchase,
                String itemId
        ) {
        }
    }

    @Builder
    public record Vendor(
            @EqualsAndHashCode.Include
            Key key
    ) {

        @Builder
        public record Key(
                Booth.Key booth,
                String vendorId
        ) {
        }
    }
}
