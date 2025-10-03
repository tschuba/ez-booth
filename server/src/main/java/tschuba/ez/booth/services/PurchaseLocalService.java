package tschuba.ez.booth.services;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.data.Ids;
import tschuba.ez.booth.data.Repositories;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Local service implementation for purchase processing.
 * This class provides the core business logic for handling purchases.
 */
@Service
public class PurchaseLocalService implements PurchaseService {

    private final Repositories.Booth booths;
    private final Repositories.Purchase purchases;
    private final Repositories.PurchaseItem purchaseItems;
    private final Repositories.Vendor vendors;

    @Autowired
    public PurchaseLocalService(@NonNull Repositories.Booth booths,
                                @NonNull Repositories.Purchase purchases,
                                @NonNull Repositories.PurchaseItem purchaseItems,
                                @NonNull Repositories.Vendor vendors) {
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
        BigDecimal purchaseValue = items.stream().map(DataModel.PurchaseItem::price).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        DataModel.Purchase.Key purchaseKey = DataModel.Purchase.Key.builder()
                .booth(booth)
                .purchaseId(Ids.UUID())
                .build();
        DataModel.Purchase purchase = DataModel.Purchase.builder()
                .key(purchaseKey)
                .value(purchaseValue)
                .purchasedOn(LocalDateTime.now())
                .items(items)
                .build();

        List<EntityModel.Vendor> unregisteredVendors = items.stream().map(DataModel.PurchaseItem::vendor).map(EntitiesMapper::objectToEntity)
                .filter(Predicate.not(vendors::existsById))
                .map(EntityModel.Vendor::new)
                .toList();

        Stream<EntityModel.PurchaseItem> itemEntities = items.stream().map(EntitiesMapper::objectToEntity);

        if (!unregisteredVendors.isEmpty()) {
            vendors.saveAll(unregisteredVendors);
        }
        purchaseItems.saveAll(itemEntities.toList());
        purchases.save(EntitiesMapper.objectToEntity(purchase));

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Optional<DataModel.Purchase> getPurchaseByKey(@NonNull DataModel.Purchase.Key purchase) {
        return purchases.findById(EntitiesMapper.objectToEntity(purchase)).map(EntitiesMapper::entityToObject);
    }

    @Override
    public @NonNull Stream<DataModel.Purchase> getPurchasesByBooth(@NonNull DataModel.Booth.Key booth) {
        return purchases.findPurchasesByBooth(EntitiesMapper.objectToEntity(booth)).map(EntitiesMapper::entityToObject);
    }
}
