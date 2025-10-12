/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelTestValues {
  public static final String BOOTH_ID = "test-booth";
  public static final LocalDate BOOTH_DATE = LocalDate.of(2025, 10, 12);
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
}
