/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.data.*;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;

/**
 * Local service implementation for purchase processing.
 * This class provides the core business logic for handling purchases.
 */
@Service
public class PurchaseLocalService implements PurchaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseLocalService.class);

    private final BoothRepository booths;
    private final PurchaseRepository purchases;
    private final PurchaseItemRepository purchaseItems;
    private final VendorRepository vendors;

    @Autowired
    public PurchaseLocalService(
            @NonNull BoothRepository booths,
            @NonNull PurchaseRepository purchases,
            @NonNull PurchaseItemRepository purchaseItems,
            @NonNull VendorRepository vendors) {
        this.booths = booths;
        this.purchases = purchases;
        this.purchaseItems = purchaseItems;
        this.vendors = vendors;
    }

    @Override
    public @NonNull DataModel.Purchase checkout(@NonNull ServiceModel.Checkout checkout) {
        DataModel.Booth.Key booth = checkout.booth();
        if (!booths.existsById(EntitiesMapper.objectToEntity(booth))) {
            throw new CheckoutException("Checkout failed! Booth not found: %s".formatted(booth));
        }

        List<DataModel.PurchaseItem> items = checkout.items();
        BigDecimal purchaseValue =
                items.stream()
                        .map(DataModel.PurchaseItem::price)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);
        DataModel.Purchase.Key purchaseKey =
                DataModel.Purchase.Key.builder().booth(booth).purchaseId(Ids.UUID()).build();
        DataModel.Purchase purchaseData =
                DataModel.Purchase.builder()
                        .key(purchaseKey)
                        .value(purchaseValue)
                        .purchasedOn(LocalDateTime.now())
                        .items(items)
                        .build();

        List<EntityModel.Vendor> unregisteredVendors =
                items.stream()
                        .map(DataModel.PurchaseItem::vendor)
                        .map(EntitiesMapper::objectToEntity)
                        .filter(Predicate.not(vendors::existsById))
                        .map(EntityModel.Vendor::new)
                        .toList();

        Stream<EntityModel.PurchaseItem> itemEntities =
                items.stream().map(EntitiesMapper::objectToEntity);

        if (!unregisteredVendors.isEmpty()) {
            LOGGER.debug("Registering new vendors: {}", unregisteredVendors);
            vendors.saveAll(unregisteredVendors);
        }
        LOGGER.debug("Saving purchase items: {}", itemEntities);
        purchaseItems.saveAll(itemEntities.toList());
        LOGGER.debug("Saving purchase: {}", purchaseData);
        EntityModel.Purchase purchaseEntity =
                purchases.save(EntitiesMapper.objectToEntity(purchaseData));
        LOGGER.info("Checkout completed: {}", purchaseKey);
        return EntitiesMapper.entityToObject(purchaseEntity);
    }

    @Override
    public @NonNull Optional<DataModel.Purchase> getPurchaseByKey(
            @NonNull DataModel.Purchase.Key purchase) {
        return purchases
                .findById(EntitiesMapper.objectToEntity(purchase))
                .map(EntitiesMapper::entityToObject);
    }

    @Override
    public @NonNull Stream<DataModel.Purchase> getPurchasesByBooth(
            @NonNull DataModel.Booth.Key booth) {
        return purchases.findPurchasesByBooth(booth.boothId()).stream()
                .map(EntitiesMapper::entityToObject);
    }
}
