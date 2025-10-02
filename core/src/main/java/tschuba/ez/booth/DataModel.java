package tschuba.ez.booth;

import jakarta.persistence.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class DataModel {

    private static final String SCHEMA = "ez_booth";

    private DataModel() {
    }

    @Builder
    @Entity
    @Table(name = "booths", schema = SCHEMA)
    public record Booth(
            @EmbeddedId Key key,
            @Column(nullable = false) String description,
            @Column(nullable = false) LocalDate date,
            @Column(nullable = false, name = "participation_fee") BigDecimal participationFee,
            @Column(nullable = false, name = "sales_fee") BigDecimal salesFee,
            @Column(nullable = false, name = "fees_rounding_step") BigDecimal feesRoundingStep,
            @Column boolean closed,
            @Column(name = "closed_on") LocalDateTime closedOn
    ) {

        @Builder
        @Embeddable
        public record Key(
                @Column(nullable = false, name = "booth_id") String boothId
        ) {
        }

    }

    @Builder
    @Entity
    @Table(name = "purchases", schema = SCHEMA)
    public record Purchase(
            @EmbeddedId Key key,
            @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
            List<PurchaseItem> items,
            @Column(nullable = false) BigDecimal value,
            @Column(nullable = false, name = "purchased_on") LocalDateTime purchasedOn
    ) {

        @Builder
        @Embeddable
        public record Key(
                @Column(nullable = false) Booth.Key booth,
                @Column(nullable = false, name = "purchase_id") String purchaseId
        ) {
        }
    }

    @Builder
    @Entity
    @Table(name = "purchase_items", schema = SCHEMA)
    public record PurchaseItem(
            @ManyToOne(optional = false)
            @JoinColumns({
                    @JoinColumn(name = "booth_id", referencedColumnName = "booth"),
                    @JoinColumn(name = "purchase_id", referencedColumnName = "purchase_id")
            })
            Purchase purchase,
            @Column(nullable = false) BigDecimal price,
            @Column(nullable = false, name = "purchased_on") LocalDateTime purchasedOn
    ) {

        @Builder
        @Embeddable
        public record Key(
                @Column(nullable = false) Vendor.Key vendor,
                @Column(nullable = false, name = "item_id") String itemId
        ) {
        }
    }

    @Builder
    @Entity
    @Table(name = "vendors", schema = SCHEMA)
    public record Vendor(
            @EmbeddedId Key key
    ) {

        @Builder
        @Embeddable
        public record Key(
                @Column(nullable = false) Booth.Key booth,
                @Column(nullable = false, name = "vendor_id") String vendorId
        ) {
        }
    }
}
