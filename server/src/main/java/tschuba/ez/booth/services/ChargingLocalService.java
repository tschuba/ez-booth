/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.data.PurchaseItemRepository;
import tschuba.ez.booth.data.RecordNotFoundException;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;

/**
 * Local service implementation for charging calculations.
 * This class provides the core business logic for calculating fees and balances.
 */
@Service
public class ChargingLocalService implements ChargingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChargingLocalService.class);

  private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
  private static final MathContext BALANCE_MATH_CONTEXT =
      new MathContext(MathContext.DECIMAL64.getPrecision(), ROUNDING_MODE);

  private final BoothRepository booths;
  private final PurchaseItemRepository purchaseItems;

  @Autowired
  public ChargingLocalService(
      @NonNull BoothRepository booths, @NonNull PurchaseItemRepository purchaseItems) {
    this.booths = booths;
    this.purchaseItems = purchaseItems;
  }

  @Override
  @NonNull
  public ServiceModel.ChargedFees calculateFees(DataModel.Vendor.@NonNull Key vendor) {
    EntityModel.Booth booth =
        booths
            .findById(EntitiesMapper.objectToEntity(vendor.booth()))
            .orElseThrow(() -> new RecordNotFoundException("Booth not found: " + vendor.booth()));

    BigDecimal vendorItemsSum =
        purchaseItems
            .findAllByVendor(EntitiesMapper.objectToEntity(vendor))
            .map(EntityModel.PurchaseItem::getPrice)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO);
    LOGGER.debug(
        "Calculating fees for vendor {}: itemsSum={}, booth={}", vendor, vendorItemsSum, booth);

    ServiceModel.ChargingConfig chargingConfig =
        ServiceModel.ChargingConfig.of(EntitiesMapper.entityToObject(booth));
    ServiceModel.ChargedFees chargedFees = chargingConfig.calculateFees(vendorItemsSum);
    LOGGER.debug("Calculated fees for vendor {}: {}", vendor, chargedFees);
    return chargedFees;
  }

  @Override
  @NonNull
  public ServiceModel.Balance.Output calculateBalance(@NonNull ServiceModel.Balance.Input input) {
    LOGGER.debug("Calculating balance for input: {}", input);
    BigDecimal totalSalesAmount = input.totalSalesAmount();
    ServiceModel.ChargingConfig chargingConfig = input.chargingConfig();
    ServiceModel.ChargedFees chargedFees = chargingConfig.calculateFees(totalSalesAmount);
    BigDecimal totalRevenue = totalSalesAmount.subtract(chargedFees.total());
    LOGGER.debug(
        "Calculated balance: totalSalesAmount={}, chargedFees={}, totalRevenue={}",
        totalSalesAmount,
        chargedFees.total(),
        totalRevenue);

    BigDecimal roundingStep = chargingConfig.roundingStep();
    if (roundingStep.compareTo(BigDecimal.ZERO) != 0) {
      BigDecimal remainder = totalRevenue.remainder(roundingStep, BALANCE_MATH_CONTEXT);
      if (remainder.compareTo(BigDecimal.ZERO) != 0) {
        totalRevenue = totalRevenue.add(roundingStep.subtract(remainder));
      }
    }

    return ServiceModel.Balance.Output.builder()
        .totalRevenue(totalRevenue)
        .chargedFees(chargedFees)
        .build();
  }
}
