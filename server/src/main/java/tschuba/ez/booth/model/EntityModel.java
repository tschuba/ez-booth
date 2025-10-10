/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

/**
 * JPA entities for {@link DataModel}.
 */
public class EntityModel {

    private static final String SCHEMA = "ez_booth";

    private EntityModel() {}

    @Entity(name = "Booth")
    @Table(name = "booths", schema = SCHEMA)
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class Booth {

        @EmbeddedId @EqualsAndHashCode.Include private Key key;

        @Column(nullable = false, name = "description")
        private String description;

        @Column(nullable = false, name = "date")
        private LocalDate date;

        @Column(nullable = false, name = "participation_fee")
        private BigDecimal participationFee;

        @Column(nullable = false, name = "sales_fee")
        private BigDecimal salesFee;

        @Column(nullable = false, name = "fees_rounding_step")
        private BigDecimal feesRoundingStep;

        @Column(name = "closed")
        private boolean closed;

        @Column(name = "closed_on")
        private LocalDateTime closedOn;

        @Embeddable
        @Builder(toBuilder = true)
        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @EqualsAndHashCode
        @ToString
        public static class Key {
            @Column(nullable = false, name = "booth_id")
            private String boothId;
        }
    }

    @Entity(name = "Vendor")
    @Table(name = "vendors", schema = SCHEMA)
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class Vendor {

        @EmbeddedId @EqualsAndHashCode.Include private Key key;

        @Embeddable
        @Builder(toBuilder = true)
        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @EqualsAndHashCode
        @ToString
        public static class Key {
            @Embedded private Booth.Key booth;

            @Column(nullable = false, name = "vendor_id")
            private String vendorId;
        }
    }

    @Entity(name = "Purchase")
    @Table(name = "purchases", schema = SCHEMA)
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class Purchase {

        @EmbeddedId @EqualsAndHashCode.Include private Purchase.Key key;

        @Column(nullable = false, name = "value")
        private BigDecimal value;

        @Column(nullable = false, name = "purchased_on")
        private LocalDateTime purchasedOn;

        @OneToMany
        @JoinColumns({
            @JoinColumn(name = "booth_id", referencedColumnName = "booth_id"),
            @JoinColumn(name = "purchase_id", referencedColumnName = "purchase_id")
        })
        private List<PurchaseItem> items;

        @Embeddable
        @Builder(toBuilder = true)
        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @EqualsAndHashCode
        @ToString
        public static class Key {
            @Embedded private Booth.Key booth;

            @Column(nullable = false, name = "purchase_id")
            private String purchaseId;
        }
    }

    @Entity(name = "PurchaseItem")
    @Table(name = "purchase_items", schema = SCHEMA)
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class PurchaseItem {

        @EmbeddedId @EqualsAndHashCode.Include private Key key;

        @Column(name = "vendor_id", nullable = false)
        private String vendorId;

        @Column(nullable = false, name = "price")
        private BigDecimal price;

        @Column(nullable = false, name = "purchased_on")
        private LocalDateTime purchasedOn;

        @Embeddable
        @Builder(toBuilder = true)
        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @EqualsAndHashCode
        @ToString
        public static class Key {
            @Embedded private Purchase.Key purchase;

            @Column(nullable = false, name = "item_id")
            private String itemId;
        }
    }
}
