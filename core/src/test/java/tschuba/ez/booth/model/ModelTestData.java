/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.model;

import com.google.protobuf.Timestamp;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tschuba.ez.booth.proto.ProtoModel;

/**
 * Test data for model and proto classes.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelTestData {

  /**
   * Data model test data.
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Objects {
    public static final DataModel.Booth.Key BOOTH_KEY =
        DataModel.Booth.Key.builder().boothId(ModelTestValues.BOOTH_ID).build();

    public static final DataModel.Booth BOOTH =
        DataModel.Booth.builder()
            .key(BOOTH_KEY)
            .description(ModelTestValues.BOOTH_DESCRIPTION)
            .closed(true)
            .closedOn(ModelTestValues.BOOTH_CLOSED_ON)
            .feesRoundingStep(BigDecimal.valueOf(ModelTestValues.FEES_ROUNDING_STEP))
            .participationFee(BigDecimal.valueOf(ModelTestValues.PARTICIPATION_FEE))
            .salesFee(BigDecimal.valueOf(ModelTestValues.SALES_FEE))
            .build();

    public static final DataModel.Vendor.Key VENDOR_KEY =
        DataModel.Vendor.Key.builder().booth(BOOTH_KEY).vendorId(ModelTestValues.VENDOR_ID).build();

    public static final DataModel.Vendor VENDOR =
        DataModel.Vendor.builder().key(VENDOR_KEY).build();

    public static final DataModel.Purchase.Key PURCHASE_KEY =
        DataModel.Purchase.Key.builder()
            .booth(BOOTH_KEY)
            .purchaseId(ModelTestValues.PURCHASE_ID)
            .build();

    public static final DataModel.PurchaseItem.Key PURCHASE_ITEM_KEY =
        DataModel.PurchaseItem.Key.builder()
            .purchase(PURCHASE_KEY)
            .itemId(ModelTestValues.ITEM_ID)
            .build();

    public static final DataModel.PurchaseItem PURCHASE_ITEM =
        DataModel.PurchaseItem.builder()
            .key(PURCHASE_ITEM_KEY)
            .vendor(VENDOR_KEY)
            .purchasedOn(ModelTestValues.ITEM_PURCHASED_ON)
            .price(ModelTestValues.ITEM_PRICE)
            .build();

    public static final DataModel.Purchase PURCHASE =
        DataModel.Purchase.builder()
            .key(PURCHASE_KEY)
            .value(ModelTestValues.ITEM_PRICE)
            .items(List.of(PURCHASE_ITEM))
            .purchasedOn(ModelTestValues.ITEM_PURCHASED_ON)
            .build();
  }

  /**
   * Proto messages test data.
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Messages {
    public static final ProtoModel.BoothKey BOOTH_KEY =
        ProtoModel.BoothKey.newBuilder().setBoothId(ModelTestValues.BOOTH_ID).build();

    public static final ProtoModel.Booth BOOTH =
        ProtoModel.Booth.newBuilder()
            .setKey(BOOTH_KEY)
            .setDescription(ModelTestValues.BOOTH_DESCRIPTION)
            .setClosed(true)
            .setClosedOn(
                Timestamp.newBuilder()
                    .setSeconds(ModelTestValues.BOOTH_CLOSED_ON.toEpochSecond(ZoneOffset.UTC))
                    .setNanos(ModelTestValues.BOOTH_CLOSED_ON.getNano())
                    .build())
            .setFeesRoundingStep(ModelTestValues.FEES_ROUNDING_STEP)
            .setParticipationFee(ModelTestValues.PARTICIPATION_FEE)
            .setSalesFee(ModelTestValues.SALES_FEE)
            .build();

    public static final ProtoModel.VendorKey VENDOR_KEY =
        ProtoModel.VendorKey.newBuilder()
            .setBooth(BOOTH_KEY)
            .setVendorId(ModelTestValues.VENDOR_ID)
            .build();

    public static final ProtoModel.Vendor VENDOR =
        ProtoModel.Vendor.newBuilder().setKey(VENDOR_KEY).build();

    public static final ProtoModel.PurchaseKey PURCHASE_KEY =
        ProtoModel.PurchaseKey.newBuilder()
            .setBooth(BOOTH_KEY)
            .setPurchaseId(ModelTestValues.PURCHASE_ID)
            .build();

    public static final ProtoModel.PurchaseItem PURCHASE_ITEM =
        ProtoModel.PurchaseItem.newBuilder()
            .setPurchase(PURCHASE_KEY)
            .setItemId(ModelTestValues.ITEM_ID)
            .setVendor(VENDOR_KEY)
            .setPrice(ModelTestValues.ITEM_PRICE.floatValue())
            .setPurchasedOn(
                Timestamp.newBuilder()
                    .setSeconds(ModelTestValues.ITEM_PURCHASED_ON.toEpochSecond(ZoneOffset.UTC))
                    .setNanos(ModelTestValues.ITEM_PURCHASED_ON.getNano())
                    .build())
            .build();

    public static final ProtoModel.Purchase PURCHASE =
        ProtoModel.Purchase.newBuilder()
            .setKey(
                ProtoModel.PurchaseKey.newBuilder()
                    .setBooth(BOOTH_KEY)
                    .setPurchaseId(ModelTestValues.PURCHASE_ID)
                    .build())
            .addItems(PURCHASE_ITEM)
            .setValue(ModelTestValues.ITEM_PRICE.floatValue())
            .setPurchasedOn(
                Timestamp.newBuilder()
                    .setSeconds(ModelTestValues.ITEM_PURCHASED_ON.toEpochSecond(ZoneOffset.UTC))
                    .setNanos(ModelTestValues.ITEM_PURCHASED_ON.getNano())
                    .build())
            .build();
  }
}
