/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Entity model test data.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityModelTestData {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Entities {
    public static final EntityModel.Booth.Key BOOTH_KEY =
        EntityModel.Booth.Key.builder().boothId(ModelTestValues.BOOTH_ID).build();
    public static final EntityModel.Purchase.Key PURCHASE_KEY =
        EntityModel.Purchase.Key.builder()
            .booth(BOOTH_KEY)
            .purchaseId(ModelTestValues.PURCHASE_ID)
            .build();
    public static final EntityModel.PurchaseItem.Key PURCHASE_ITEM_KEY =
        EntityModel.PurchaseItem.Key.builder()
            .purchase(PURCHASE_KEY)
            .itemId(ModelTestValues.ITEM_ID)
            .build();
    public static final EntityModel.PurchaseItem PURCHASE_ITEM =
        EntityModel.PurchaseItem.builder()
            .key(PURCHASE_ITEM_KEY)
            .vendorId(ModelTestValues.VENDOR_ID)
            .purchasedOn(ModelTestValues.ITEM_PURCHASED_ON)
            .price(ModelTestValues.ITEM_PRICE)
            .build();
    public static final EntityModel.Purchase PURCHASE =
        EntityModel.Purchase.builder()
            .key(PURCHASE_KEY)
            .value(ModelTestValues.ITEM_PRICE)
            .items(List.of(PURCHASE_ITEM))
            .purchasedOn(ModelTestValues.ITEM_PURCHASED_ON)
            .build();
    public static final EntityModel.Vendor.Key VENDOR_KEY =
        EntityModel.Vendor.Key.builder()
            .booth(BOOTH_KEY)
            .vendorId(ModelTestValues.VENDOR_ID)
            .build();
    public static final EntityModel.Vendor VENDOR =
        EntityModel.Vendor.builder().key(VENDOR_KEY).build();
    public static final EntityModel.Booth BOOTH =
        EntityModel.Booth.builder()
            .key(BOOTH_KEY)
            .date(ModelTestValues.BOOTH_DATE)
            .description(ModelTestValues.BOOTH_DESCRIPTION)
            .closed(true)
            .closedOn(ModelTestValues.BOOTH_CLOSED_ON)
            .feesRoundingStep(BigDecimal.valueOf(ModelTestValues.FEES_ROUNDING_STEP))
            .participationFee(BigDecimal.valueOf(ModelTestValues.PARTICIPATION_FEE))
            .salesFee(BigDecimal.valueOf(ModelTestValues.SALES_FEE))
            .build();
  }

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
            .date(ModelTestValues.BOOTH_DATE)
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
}
