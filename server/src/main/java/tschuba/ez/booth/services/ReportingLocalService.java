/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.data.PurchaseItemRepository;
import tschuba.ez.booth.data.RecordNotFoundException;
import tschuba.ez.booth.data.VendorRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;

/**
 * Local service implementation for reporting.
 * This class provides the core business logic for generating reports.
 */
@Service
public class ReportingLocalService implements ReportingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportingLocalService.class);

    private final BoothRepository booths;
    private final VendorRepository vendors;
    private final PurchaseItemRepository purchaseItems;
    private final ChargingService chargingService;

    public ReportingLocalService(
            @NonNull BoothRepository booths,
            @NonNull VendorRepository vendors,
            @NonNull PurchaseItemRepository purchaseItems,
            @NonNull ChargingService chargingService) {
        this.booths = booths;
        this.vendors = vendors;
        this.purchaseItems = purchaseItems;
        this.chargingService = chargingService;
    }

    @Override
    @Transactional
    public @NonNull ServiceModel.VendorReportData createVendorReportData(
            @NonNull DataModel.Vendor.Key vendorKey) {
        EntityModel.Vendor vendor =
                vendors.findById(EntitiesMapper.objectToEntity(vendorKey))
                        .orElseThrow(
                                (() ->
                                        new RecordNotFoundException(
                                                "Vendor not found: " + vendorKey)));

        List<EntityModel.PurchaseItem> vendorItems =
                purchaseItems
                        .findPurchaseItemsByVendor(EntitiesMapper.objectToEntity(vendorKey))
                        .toList();
        BigDecimal vendorItemsSum =
                vendorItems.stream()
                        .map(EntityModel.PurchaseItem::getPrice)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);

        EntityModel.Booth booth =
                booths.findById(EntitiesMapper.objectToEntity(vendorKey.booth()))
                        .orElseThrow(
                                () ->
                                        new RecordNotFoundException(
                                                "Booth not found: " + vendorKey.booth()));
        ServiceModel.ChargingConfig chargingConfig = ServiceModel.ChargingConfig.of(booth);

        ServiceModel.Balance.Input balanceInput =
                ServiceModel.Balance.Input.builder()
                        .chargingConfig(chargingConfig)
                        .totalSalesAmount(vendorItemsSum)
                        .build();
        ServiceModel.Balance.Output balance = chargingService.calculateBalance(balanceInput);
        LOGGER.debug(
                "Balance: {}, {}, {}, Total Amount of Sales: {}",
                vendor,
                chargingConfig,
                balance,
                vendorItemsSum);

        BigDecimal totalRevenue = balance.totalRevenue();
        ServiceModel.ChargedFees chargedFees = balance.chargedFees();

        return ServiceModel.VendorReportData.builder()
                .booth(EntitiesMapper.entityToObject(booth))
                .vendor(EntitiesMapper.entityToObject(vendor))
                .items(vendorItems.stream().map(EntitiesMapper::entityToObject).toList())
                .salesSum(vendorItemsSum)
                .participationFee(chargedFees.participationFee())
                .salesFee(chargedFees.salesFee())
                .totalRevenue(totalRevenue)
                .build();
    }

    @Override
    public @NonNull URI generateVendorReport(@NonNull DataModel.Vendor.Key... vendors) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
