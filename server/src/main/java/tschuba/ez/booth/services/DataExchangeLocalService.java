/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.data.PurchaseRepository;
import tschuba.ez.booth.data.VendorRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;

@Service
public class DataExchangeLocalService implements DataExchangeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeLocalService.class);

    private final BoothRepository booths;
    private final VendorRepository vendors;
    private final PurchaseRepository purchases;

    public DataExchangeLocalService(
            @NonNull BoothRepository booths,
            @NonNull VendorRepository vendors,
            @NonNull PurchaseRepository purchases) {
        this.booths = booths;
        this.vendors = vendors;
        this.purchases = purchases;
    }

    @Override
    public @NonNull Stream<ServiceModel.ExchangeData> exportLocalData() {
        return booths.findAll().parallelStream().map(this::exportBoothData);
    }

    @Override
    @Transactional
    public void importRemoteData(ServiceModel.@NonNull ExchangeData data) {

        // save booth locally if missing
        DataModel.Booth booth = data.booth();
        if (!booths.existsById(EntitiesMapper.objectToEntity(booth.key()))) {
            booths.save(EntitiesMapper.objectToEntity(booth));
        }

        LOGGER.debug("Importing vendors for booth {}", booth.key().boothId());
        data.vendors().parallelStream()
                .filter(vendor -> vendor.key().booth().equals(booth.key()))
                .map(EntitiesMapper::objectToEntity)
                .filter(vendor -> !vendors.existsById(vendor.getKey()))
                .forEach(vendors::save);

        LOGGER.debug("Importing purchases for booth {}", booth.key().boothId());
        data.purchases().parallelStream()
                .filter(purchase -> purchase.key().booth().equals(booth.key()))
                .map(EntitiesMapper::objectToEntity)
                .filter(purchase -> !purchases.existsById(purchase.getKey()))
                .forEach(purchases::save);

        LOGGER.info("Imported data for booth {}", booth.key().boothId());
    }

    /**
     * Export all data related to the given booth.
     *
     * @param booth the booth to export data for
     * @return the exported data
     */
    private ServiceModel.ExchangeData exportBoothData(EntityModel.Booth booth) {

        String boothId = booth.getKey().getBoothId();
        ServiceModel.ExchangeData.ExchangeDataBuilder builder =
                ServiceModel.ExchangeData.builder().booth(EntitiesMapper.entityToObject(booth));

        List<EntityModel.Vendor> allVendors = vendors.findAllByBoothId(boothId);
        List<DataModel.Vendor> listOfVendors =
                allVendors.parallelStream().map(EntitiesMapper::entityToObject).toList();
        builder.vendors(listOfVendors);

        List<EntityModel.Purchase> allPurchases = purchases.findPurchasesByBooth(boothId);
        List<DataModel.Purchase> listOfPurchases =
                allPurchases.parallelStream().map(EntitiesMapper::entityToObject).toList();
        builder.purchases(listOfPurchases);

        return builder.build();
    }
}
