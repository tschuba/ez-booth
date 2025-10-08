/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

public final class DataModel {

    private DataModel() {}

    @Builder
    public record Booth(
            Key key,
            String description,
            LocalDate date,
            BigDecimal participationFee,
            BigDecimal salesFee,
            BigDecimal feesRoundingStep,
            boolean closed,
            LocalDateTime closedOn) {

        @Builder
        public record Key(String boothId) implements Comparable<Key> {
            @Override
            public int compareTo(@NonNull Key key) {
                return Comparator.comparing(Key::boothId).compare(this, key);
            }
        }
    }

    @Builder
    public record Purchase(
            @EqualsAndHashCode.Include Key key,
            BigDecimal value,
            LocalDateTime purchasedOn,
            List<PurchaseItem> items) {

        @Builder
        public record Key(Booth.Key booth, String purchaseId) implements Comparable<Key> {
            @Override
            public int compareTo(@NonNull Key key) {
                return Comparator.comparing(Key::booth)
                        .thenComparing(Key::purchaseId)
                        .compare(this, key);
            }
        }
    }

    @Builder
    public record PurchaseItem(
            @EqualsAndHashCode.Include Key key,
            @EqualsAndHashCode.Include Vendor.Key vendor,
            BigDecimal price,
            LocalDateTime purchasedOn) {

        @Builder
        public record Key(Purchase.Key purchase, String itemId) implements Comparable<Key> {
            @Override
            public int compareTo(@NonNull Key key) {
                return Comparator.comparing(Key::purchase)
                        .thenComparing(Key::itemId)
                        .compare(this, key);
            }
        }
    }

    @Builder
    public record Vendor(@EqualsAndHashCode.Include Key key) {

        @Builder
        public record Key(Booth.Key booth, String vendorId) {}
    }
}
