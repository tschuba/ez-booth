package tschuba.ez.booth.model;

import com.google.protobuf.Timestamp;
import tschuba.ez.booth.proto.ProtoModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Test data for model and proto classes.
 */
public class ModelTestData {
    public static final String BOOTH_ID = "test-booth";
    public static final String BOOTH_DESCRIPTION = "Test Booth";
    public static final LocalDateTime BOOTH_CLOSED_ON = LocalDateTime.of(2025, 10, 2, 17, 30, 44);
    public static final float FEES_ROUNDING_STEP = .5f;
    public static final float PARTICIPATION_FEE = 10;
    public static final float SALES_FEE = .05f;
    public static final String VENDOR_ID = "test-vendor";
    public static final String ITEM_ID = "test-item";
    public static final LocalDateTime ITEM_PURCHASED_ON = LocalDateTime.of(2025, 10, 2, 19, 12, 27);
    public static final BigDecimal ITEM_PRICE = BigDecimal.valueOf(19.99);
    public static final String PURCHASE_ID = "test-purchase";

    private ModelTestData() {
    }

    /**
     * Data model test data.
     */
    public static class Objects {
        public static final DataModel.Booth.Key BOOTH_KEY = DataModel.Booth.Key.builder()
                .boothId(BOOTH_ID)
                .build();

        public static final DataModel.Booth BOOTH = DataModel.Booth.builder()
                .key(BOOTH_KEY)
                .description(BOOTH_DESCRIPTION)
                .closed(true)
                .closedOn(BOOTH_CLOSED_ON)
                .feesRoundingStep(BigDecimal.valueOf(FEES_ROUNDING_STEP))
                .participationFee(BigDecimal.valueOf(PARTICIPATION_FEE))
                .salesFee(BigDecimal.valueOf(SALES_FEE))
                .build();

        public static final DataModel.Vendor.Key VENDOR_KEY = DataModel.Vendor.Key.builder()
                .booth(BOOTH_KEY)
                .vendorId(VENDOR_ID)
                .build();

        public static final DataModel.Vendor VENDOR = DataModel.Vendor.builder()
                .key(VENDOR_KEY)
                .build();

        public static final DataModel.Purchase.Key PURCHASE_KEY = DataModel.Purchase.Key.builder()
                .booth(BOOTH_KEY)
                .purchaseId(PURCHASE_ID)
                .build();

        public static final DataModel.PurchaseItem.Key PURCHASE_ITEM_KEY = DataModel.PurchaseItem.Key.builder()
                .purchase(PURCHASE_KEY)
                .itemId(ITEM_ID)
                .build();

        public static final DataModel.PurchaseItem PURCHASE_ITEM = DataModel.PurchaseItem.builder()
                .key(PURCHASE_ITEM_KEY)
                .vendor(VENDOR_KEY)
                .purchasedOn(ITEM_PURCHASED_ON)
                .price(ITEM_PRICE)
                .build();

        public static final DataModel.Purchase PURCHASE = DataModel.Purchase.builder()
                .key(PURCHASE_KEY)
                .value(ITEM_PRICE)
                .items(List.of(PURCHASE_ITEM))
                .purchasedOn(ITEM_PURCHASED_ON)
                .build();

        private Objects() {
        }
    }

    /**
     * Proto messages test data.
     */
    public static class Messages {
        public static final ProtoModel.BoothKey BOOTH_KEY = ProtoModel.BoothKey.newBuilder()
                .setBoothId(BOOTH_ID)
                .build();

        public static final ProtoModel.Booth BOOTH = ProtoModel.Booth.newBuilder()
                .setKey(BOOTH_KEY)
                .setDescription(BOOTH_DESCRIPTION)
                .setClosed(true)
                .setClosedOn(Timestamp.newBuilder().setSeconds(BOOTH_CLOSED_ON.toEpochSecond(ZoneOffset.UTC)).setNanos(BOOTH_CLOSED_ON.getNano()).build())
                .setFeesRoundingStep(FEES_ROUNDING_STEP)
                .setParticipationFee(PARTICIPATION_FEE)
                .setSalesFee(SALES_FEE)
                .build();

        public static final ProtoModel.VendorKey VENDOR_KEY = ProtoModel.VendorKey.newBuilder()
                .setBooth(BOOTH_KEY)
                .setVendorId(VENDOR_ID)
                .build();

        public static final ProtoModel.Vendor VENDOR = ProtoModel.Vendor.newBuilder()
                .setKey(VENDOR_KEY)
                .build();

        public static final ProtoModel.PurchaseKey PURCHASE_KEY = ProtoModel.PurchaseKey.newBuilder()
                .setBooth(BOOTH_KEY)
                .setPurchaseId(PURCHASE_ID)
                .build();

        public static final ProtoModel.PurchaseItem PURCHASE_ITEM = ProtoModel.PurchaseItem.newBuilder()
                .setPurchase(PURCHASE_KEY)
                .setItemId(ITEM_ID)
                .setVendor(VENDOR_KEY)
                .setPrice(ITEM_PRICE.floatValue())
                .setPurchasedOn(Timestamp.newBuilder().setSeconds(ITEM_PURCHASED_ON.toEpochSecond(ZoneOffset.UTC)).setNanos(ITEM_PURCHASED_ON.getNano()).build())
                .build();

        public static final ProtoModel.Purchase PURCHASE = ProtoModel.Purchase.newBuilder()
                .setKey(ProtoModel.PurchaseKey.newBuilder()
                        .setBooth(BOOTH_KEY)
                        .setPurchaseId(PURCHASE_ID)
                        .build())
                .addItems(PURCHASE_ITEM)
                .setValue(ITEM_PRICE.floatValue())
                .setPurchasedOn(Timestamp.newBuilder().setSeconds(ITEM_PURCHASED_ON.toEpochSecond(ZoneOffset.UTC)).setNanos(ITEM_PURCHASED_ON.getNano()).build())
                .build();

        private Messages() {
        }
    }

    /**
     * Entity model test data.
     */
    public static class Entities {

        public static final EntityModel.Booth.Key BOOTH_KEY = EntityModel.Booth.Key.builder()
                .boothId(BOOTH_ID)
                .build();

        public static final EntityModel.Booth BOOTH = EntityModel.Booth.builder()
                .key(BOOTH_KEY)
                .description(BOOTH_DESCRIPTION)
                .closed(true)
                .closedOn(BOOTH_CLOSED_ON)
                .feesRoundingStep(BigDecimal.valueOf(FEES_ROUNDING_STEP))
                .participationFee(BigDecimal.valueOf(PARTICIPATION_FEE))
                .salesFee(BigDecimal.valueOf(SALES_FEE))
                .build();

        public static final EntityModel.Vendor.Key VENDOR_KEY = EntityModel.Vendor.Key.builder()
                .booth(BOOTH_KEY)
                .vendorId(VENDOR_ID)
                .build();

        public static final EntityModel.Vendor VENDOR = EntityModel.Vendor.builder()
                .key(VENDOR_KEY)
                .build();

        public static final EntityModel.Purchase.Key PURCHASE_KEY = EntityModel.Purchase.Key.builder()
                .booth(BOOTH_KEY)
                .purchaseId(PURCHASE_ID)
                .build();

        public static final EntityModel.PurchaseItem.Key PURCHASE_ITEM_KEY = EntityModel.PurchaseItem.Key.builder()
                .purchase(PURCHASE_KEY)
                .itemId(ITEM_ID)
                .build();

        public static final EntityModel.PurchaseItem PURCHASE_ITEM = EntityModel.PurchaseItem.builder()
                .key(PURCHASE_ITEM_KEY)
                .vendorId(VENDOR_ID)
                .purchasedOn(ITEM_PURCHASED_ON)
                .price(ITEM_PRICE)
                .build();

        public static final EntityModel.Purchase PURCHASE = EntityModel.Purchase.builder()
                .key(PURCHASE_KEY)
                .value(ITEM_PRICE)
                .items(List.of(PURCHASE_ITEM))
                .purchasedOn(ITEM_PURCHASED_ON)
                .build();

        private Entities() {
        }
    }
}
