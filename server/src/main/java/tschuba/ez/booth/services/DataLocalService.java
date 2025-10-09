/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.data.PurchaseRepository;
import tschuba.ez.booth.data.VendorRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;

@Service
public class DataLocalService implements DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLocalService.class);

    private final BoothRepository booths;
    private final VendorRepository vendors;
    private final PurchaseRepository purchases;

    @Autowired
    public DataLocalService(
            @NonNull BoothRepository booths,
            @NonNull VendorRepository vendors,
            @NonNull PurchaseRepository purchases) {
        this.booths = booths;
        this.vendors = vendors;
        this.purchases = purchases;
    }

    @Override
    @Transactional
    public void merge(ServiceModel.@NonNull ExchangeData data) {
        DataModel.Booth dataBooth = data.booth();

        // check for matching local booth by description and date
        List<EntityModel.Booth> matchingLocalBooths =
                booths.findAll().stream()
                        .filter(
                                localBooth ->
                                        Objects.equals(
                                                        localBooth.getDescription(),
                                                        dataBooth.description())
                                                && Objects.equals(
                                                        localBooth.getDate(), dataBooth.date()))
                        .limit(2)
                        .toList();
        if (matchingLocalBooths.size() > 1) {
            throw new DataExchangeExcpetion(
                    "Multiple local booths matching the received booth's description and date! %s"
                            .formatted(dataBooth));
        }

        if (matchingLocalBooths.size() == 1) {
            DataModel.Booth localBooth =
                    EntitiesMapper.entityToObject(matchingLocalBooths.getFirst());
            LOGGER.debug("Found matching local booth for remote booth: {}", dataBooth);

            List<EntityModel.@NonNull Vendor> missingVendorsList =
                    data.vendors().stream()
                            .map(EntitiesMapper::objectToEntity)
                            .filter(vendor -> !vendors.existsById(vendor.getKey()))
                            .map(
                                    vendor -> {
                                        // adopt local booth key for missing vendors
                                        EntityModel.Vendor.Key localKey =
                                                vendor.getKey().toBuilder()
                                                        .booth(
                                                                EntitiesMapper.objectToEntity(
                                                                        localBooth.key()))
                                                        .build();
                                        return vendor.toBuilder().key(localKey).build();
                                    })
                            .toList();
            if (!missingVendorsList.isEmpty()) {
                LOGGER.debug("Creating local copies of remote vendors: {}", missingVendorsList);
                vendors.saveAll(missingVendorsList);
            } else {
                LOGGER.debug("All remote vendors already exist locally.");
            }

            List<EntityModel.@NonNull Purchase> missingPurchaseList =
                    data.purchases().stream()
                            .map(EntitiesMapper::objectToEntity)
                            .filter(purchase -> !purchases.existsById(purchase.getKey()))
                            .map(
                                    purchase -> {
                                        EntityModel.Purchase.Key localPurchaseKey =
                                                purchase.getKey().toBuilder()
                                                        .booth(
                                                                EntitiesMapper.objectToEntity(
                                                                        localBooth.key()))
                                                        .build();

                                        // adopt local purchase key for all items
                                        List<EntityModel.PurchaseItem> localItemList =
                                                purchase.getItems().stream()
                                                        .map(
                                                                item -> {
                                                                    EntityModel.PurchaseItem.Key
                                                                            localItemKey =
                                                                                    item
                                                                                            .getKey()
                                                                                            .toBuilder()
                                                                                            .purchase(
                                                                                                    localPurchaseKey)
                                                                                            .build();
                                                                    return item.toBuilder()
                                                                            .key(localItemKey)
                                                                            .build();
                                                                })
                                                        .toList();

                                        // adopt local booth key for purchase
                                        return purchase.toBuilder()
                                                .key(localPurchaseKey)
                                                .items(localItemList)
                                                .build();
                                    })
                            .toList();
            if (!missingPurchaseList.isEmpty()) {
                LOGGER.debug("Creating local copies of remote purchases: {}", missingPurchaseList);
                purchases.saveAll(missingPurchaseList);
            } else {
                LOGGER.debug("All remote purchases already exist locally.");
            }

        } else {
            // No matching booth found, create a new one
            LOGGER.debug("Creating local copy of remote booth: {}", dataBooth);
            EntitiesMapper.entityToObject(booths.save(EntitiesMapper.objectToEntity(dataBooth)));

            List<EntityModel.Vendor> vendorList =
                    data.vendors().stream().map(EntitiesMapper::objectToEntity).toList();
            LOGGER.debug("Creating local copy of remote vendors: {}", vendorList);
            vendors.saveAll(vendorList);

            List<EntityModel.@NonNull Purchase> purchaseList =
                    data.purchases().stream().map(EntitiesMapper::objectToEntity).toList();
            LOGGER.debug("Creating local copy of remote purchases: {}", purchaseList);
            purchases.saveAll(purchaseList);
        }

        LOGGER.debug("Data import completed for booth: {}", dataBooth);
    }

    @Override
    public @NonNull ServiceModel.ExchangeData export(DataModel.Booth.@NonNull Key boothKey) {
        LOGGER.debug("Exporting data for booth: {}", boothKey);
        EntityModel.Booth booth =
                booths.findById(EntitiesMapper.objectToEntity(boothKey))
                        .orElseThrow(
                                () ->
                                        new DataExchangeExcpetion(
                                                "Booth not found: %s".formatted(boothKey)));
        LOGGER.debug("Found booth: {}", booth);

        List<DataModel.@NonNull Vendor> vendorList =
                vendors.findAllByBoothId(boothKey.boothId()).stream()
                        .map(EntitiesMapper::entityToObject)
                        .toList();
        LOGGER.debug("Found vendors: {}", vendorList);

        List<DataModel.@NonNull Purchase> purchaseList =
                purchases.findPurchasesByBooth(boothKey.boothId()).stream()
                        .map(EntitiesMapper::entityToObject)
                        .toList();
        LOGGER.debug("Found purchases: {}", purchaseList);

        return ServiceModel.ExchangeData.builder()
                .booth(EntitiesMapper.entityToObject(booth))
                .vendors(vendorList)
                .purchases(purchaseList)
                .build();
    }
}
