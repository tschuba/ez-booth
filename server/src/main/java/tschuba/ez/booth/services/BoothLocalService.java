/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tschuba.ez.booth.data.*;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;

/**
 * Local service implementation for booth events.
 * This class provides the core business logic for managing booth events.
 */
@Service
public class BoothLocalService implements BoothService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BoothLocalService.class);

    private final BoothRepository booths;
    private final PurchaseRepository purchases;
    private final PurchaseItemRepository purchaseItems;
    private final VendorRepository vendors;

    @Autowired
    public BoothLocalService(
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
    public void saveBooth(DataModel.@NonNull Booth booth) {
        booths.save(EntitiesMapper.objectToEntity(booth));
    }

    @Override
    public @NonNull Stream<DataModel.Booth> findAll() {
        return booths.findAll().stream().map(EntitiesMapper::entityToObject);
    }

    @Override
    public @NonNull Optional<DataModel.Booth> findById(@NonNull DataModel.Booth.Key booth) {
        return booths.findById(EntitiesMapper.objectToEntity(booth))
                .map(EntitiesMapper::entityToObject);
    }

    @Override
    public @NonNull DataModel.Booth close(@NonNull DataModel.Booth.Key key) {
        EntityModel.Booth boothToClose =
                booths.findById(EntitiesMapper.objectToEntity(key))
                        .orElseThrow(() -> new RecordNotFoundException("Booth not found: " + key));

        if (boothToClose.isClosed()) {
            LOGGER.debug("Booth already closed: {}", key);
            return EntitiesMapper.entityToObject(boothToClose);
        }

        EntityModel.Booth closedBooth =
                boothToClose.toBuilder().closed(true).closedOn(LocalDateTime.now()).build();
        LOGGER.debug("Closing booth: {}", key);
        closedBooth = booths.save(closedBooth);
        LOGGER.debug("Booth closed: {}", key);
        return EntitiesMapper.entityToObject(closedBooth);
    }

    @Override
    public @NonNull DataModel.Booth open(@NonNull DataModel.Booth.Key booth) {
        EntityModel.Booth boothToOpen =
                booths.findById(EntitiesMapper.objectToEntity(booth))
                        .orElseThrow(
                                () -> new RecordNotFoundException("Booth not found: " + booth));
        if (!boothToOpen.isClosed()) {
            LOGGER.debug("Booth already open: {}", booth);
            return EntitiesMapper.entityToObject(boothToOpen);
        }

        EntityModel.Booth openedBooth =
                boothToOpen.toBuilder().closed(false).closedOn(null).build();
        LOGGER.debug("Opening booth: {}", booth);
        openedBooth = booths.save(openedBooth);
        LOGGER.debug("Booth opened: {}", booth);
        return EntitiesMapper.entityToObject(openedBooth);
    }

    @Override
    @Transactional
    public void delete(@NonNull DataModel.Booth.Key booth) {
        EntityModel.Booth.Key boothKey = EntitiesMapper.objectToEntity(booth);
        LOGGER.debug("Deleting booth: {}", booth);

        List<EntityModel.PurchaseItem.Key> purchaseItemKeys =
                purchaseItems
                        .findAllByBooth(boothKey)
                        .map(EntityModel.PurchaseItem::getKey)
                        .toList();
        LOGGER.debug("Deleting {} PurchaseItem(s) of booth: {}", purchaseItemKeys.size(), boothKey);
        purchaseItems.deleteAllByIdInBatch(purchaseItemKeys);

        List<EntityModel.Purchase.Key> purchaseKeys =
                purchases.findAllByBooth(boothKey).map(EntityModel.Purchase::getKey).toList();
        LOGGER.debug("Deleting {} Purchase(s) of booth: {}", purchaseKeys.size(), boothKey);
        purchases.deleteAllByIdInBatch(purchaseKeys);

        List<EntityModel.Vendor.Key> vendorKeys =
                vendors.findAllByBooth(boothKey).stream().map(EntityModel.Vendor::getKey).toList();
        LOGGER.debug("Deleting {} Vendor(s) of booth: {}", vendorKeys.size(), boothKey);
        vendors.deleteAllByIdInBatch(vendorKeys);

        booths.deleteById(boothKey);
        LOGGER.debug("Booth deleted: {}", booth);
    }
}
