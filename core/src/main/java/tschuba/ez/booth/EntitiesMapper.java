package tschuba.ez.booth;

import lombok.NonNull;

import java.util.List;
import java.util.function.Function;

/**
 * Utility class for mapping between data models and entity models.
 */
public class EntitiesMapper {
    private EntitiesMapper() {
    }

    /**
     * Mappable for Booth.Key
     */
    public static final Mappable<DataModel.Booth.Key, EntityModel.Booth.Key> BOOTH_KEY = new Mappable<>() {
        @Override
        public @NonNull Function<DataModel.Booth.Key, EntityModel.Booth.Key> objectToEntity() {
            return input -> EntityModel.Booth.Key.builder()
                    .boothId(input.boothId())
                    .build();
        }

        @Override
        public @NonNull Function<EntityModel.Booth.Key, DataModel.Booth.Key> entityToObject() {
            return entity -> DataModel.Booth.Key.builder()
                    .boothId(entity.getBoothId())
                    .build();
        }
    };

    @NonNull
    public static EntityModel.Booth.Key objectToEntity(@NonNull DataModel.Booth.Key key) {
        return EntityModel.Booth.Key.builder()
                .boothId(key.boothId())
                .build();
    }

    @NonNull
    public static DataModel.Booth.Key entityToObject(@NonNull EntityModel.Booth.Key key) {
        return DataModel.Booth.Key.builder()
                .boothId(key.getBoothId())
                .build();
    }

    /**
     * Mappable for Booth
     */
    public static final Mappable<DataModel.Booth, EntityModel.Booth> BOOTH = new Mappable<>() {
        @Override
        public @NonNull Function<DataModel.Booth, EntityModel.Booth> objectToEntity() {
            return input -> {
                EntityModel.Booth.BoothBuilder builder = EntityModel.Booth.builder();
                if (input.key() != null) {
                    builder.key(BOOTH_KEY.objectToEntity(input.key()));
                }
                return builder
                        .description(input.description())
                        .date(input.date())
                        .participationFee(input.participationFee())
                        .salesFee(input.salesFee())
                        .feesRoundingStep(input.feesRoundingStep())
                        .closed(input.closed())
                        .closedOn(input.closedOn())
                        .build();
            };
        }

        @Override
        public @NonNull Function<EntityModel.Booth, DataModel.Booth> entityToObject() {
            return entity -> {
                DataModel.Booth.BoothBuilder builder = DataModel.Booth.builder();
                if (entity.getKey() != null) {
                    builder.key(BOOTH_KEY.entityToObject(entity.getKey()));
                }
                return builder
                        .description(entity.getDescription())
                        .date(entity.getDate())
                        .participationFee(entity.getParticipationFee())
                        .salesFee(entity.getSalesFee())
                        .feesRoundingStep(entity.getFeesRoundingStep())
                        .closed(entity.isClosed())
                        .closedOn(entity.getClosedOn())
                        .build();
            };
        }
    };

    @NonNull
    public static EntityModel.Booth objectToEntity(@NonNull DataModel.Booth booth) {
        EntityModel.Booth.BoothBuilder builder = EntityModel.Booth.builder();
        if (booth.key() != null) {
            builder.key(BOOTH_KEY.objectToEntity(booth.key()));
        }
        return builder
                .description(booth.description())
                .date(booth.date())
                .participationFee(booth.participationFee())
                .salesFee(booth.salesFee())
                .feesRoundingStep(booth.feesRoundingStep())
                .closed(booth.closed())
                .closedOn(booth.closedOn())
                .build();
    }

    @NonNull
    public static DataModel.Booth entityToObject(@NonNull EntityModel.Booth booth) {
        DataModel.Booth.BoothBuilder builder = DataModel.Booth.builder();
        if (booth.getKey() != null) {
            builder.key(BOOTH_KEY.entityToObject(booth.getKey()));
        }
        return builder
                .description(booth.getDescription())
                .date(booth.getDate())
                .participationFee(booth.getParticipationFee())
                .salesFee(booth.getSalesFee())
                .feesRoundingStep(booth.getFeesRoundingStep())
                .closed(booth.isClosed())
                .closedOn(booth.getClosedOn())
                .build();
    }

    /**
     * Mappable for Vendor.Key
     */
    public static final Mappable<DataModel.Vendor.Key, EntityModel.Vendor.Key> VENDOR_KEY = new Mappable<>() {
        @Override
        public @NonNull Function<DataModel.Vendor.Key, EntityModel.Vendor.Key> objectToEntity() {
            return input -> {
                EntityModel.Vendor.Key.KeyBuilder builder = EntityModel.Vendor.Key.builder();
                if (input.booth() != null) {
                    builder.booth(BOOTH_KEY.objectToEntity(input.booth()));
                }
                return builder
                        .vendorId(input.vendorId())
                        .build();
            };
        }

        @Override
        public @NonNull Function<EntityModel.Vendor.Key, DataModel.Vendor.Key> entityToObject() {
            return entity -> {
                DataModel.Vendor.Key.KeyBuilder builder = DataModel.Vendor.Key.builder();
                if (entity.getBooth() != null) {
                    builder.booth(BOOTH_KEY.entityToObject(entity.getBooth()));
                }
                return builder
                        .vendorId(entity.getVendorId())
                        .build();
            };
        }
    };

    @NonNull
    public static EntityModel.Vendor.Key objectToEntity(@NonNull DataModel.Vendor.Key key) {
        EntityModel.Vendor.Key.KeyBuilder builder = EntityModel.Vendor.Key.builder();
        if (key.booth() != null) {
            builder.booth(BOOTH_KEY.objectToEntity(key.booth()));
        }
        return builder
                .vendorId(key.vendorId())
                .build();
    }

    @NonNull
    public static DataModel.Vendor.Key entityToObject(@NonNull EntityModel.Vendor.Key key) {
        DataModel.Vendor.Key.KeyBuilder builder = DataModel.Vendor.Key.builder();
        if (key.getBooth() != null) {
            builder.booth(BOOTH_KEY.entityToObject(key.getBooth()));
        }
        return builder
                .vendorId(key.getVendorId())
                .build();
    }

    /**
     * Mappable for Vendor
     */
    public static final Mappable<DataModel.Vendor, EntityModel.Vendor> VENDOR = new Mappable<>() {
        @Override
        public @NonNull Function<DataModel.Vendor, EntityModel.Vendor> objectToEntity() {
            return input -> {
                EntityModel.Vendor.VendorBuilder builder = EntityModel.Vendor.builder();
                if (input.key() != null) {
                    builder.key(VENDOR_KEY.objectToEntity(input.key()));
                }
                return builder.build();
            };
        }

        @Override
        public @NonNull Function<EntityModel.Vendor, DataModel.Vendor> entityToObject() {
            return entity -> {
                DataModel.Vendor.VendorBuilder builder = DataModel.Vendor.builder();
                if (entity.getKey() != null) {
                    builder.key(VENDOR_KEY.entityToObject(entity.getKey()));
                }
                return builder.build();
            };
        }
    };

    @NonNull
    public static EntityModel.Vendor objectToEntity(@NonNull DataModel.Vendor vendor) {
        EntityModel.Vendor.VendorBuilder builder = EntityModel.Vendor.builder();
        if (vendor.key() != null) {
            builder.key(VENDOR_KEY.objectToEntity(vendor.key()));
        }
        return builder.build();
    }

    @NonNull
    public static DataModel.Vendor entityToObject(@NonNull EntityModel.Vendor vendor) {
        DataModel.Vendor.VendorBuilder builder = DataModel.Vendor.builder();
        if (vendor.getKey() != null) {
            builder.key(VENDOR_KEY.entityToObject(vendor.getKey()));
        }
        return builder.build();
    }

    /**
     * Mappable for Purchase.Key
     */
    public static final Mappable<DataModel.Purchase.Key, EntityModel.Purchase.Key> PURCHASE_KEY = new Mappable<>() {
        @Override
        public @NonNull Function<DataModel.Purchase.Key, EntityModel.Purchase.Key> objectToEntity() {
            return input -> {
                EntityModel.Purchase.Key.KeyBuilder builder = EntityModel.Purchase.Key.builder();
                if (input.booth() != null) {
                    builder.booth(BOOTH_KEY.objectToEntity(input.booth()));
                }
                return builder
                        .purchaseId(input.purchaseId())
                        .build();
            };
        }

        @Override
        public @NonNull Function<EntityModel.Purchase.Key, DataModel.Purchase.Key> entityToObject() {
            return entity -> {
                DataModel.Purchase.Key.KeyBuilder builder = DataModel.Purchase.Key.builder();
                if (entity.getBooth() != null) {
                    builder.booth(BOOTH_KEY.entityToObject(entity.getBooth()));
                }
                return builder
                        .purchaseId(entity.getPurchaseId())
                        .build();
            };
        }
    };

    @NonNull
    public static EntityModel.Purchase.Key objectToEntity(@NonNull DataModel.Purchase.Key key) {
        EntityModel.Purchase.Key.KeyBuilder builder = EntityModel.Purchase.Key.builder();
        if (key.booth() != null) {
            builder.booth(BOOTH_KEY.objectToEntity(key.booth()));
        }
        return builder
                .purchaseId(key.purchaseId())
                .build();
    }

    @NonNull
    public static DataModel.Purchase.Key entityToObject(@NonNull EntityModel.Purchase.Key key) {
        DataModel.Purchase.Key.KeyBuilder builder = DataModel.Purchase.Key.builder();
        if (key.getBooth() != null) {
            builder.booth(BOOTH_KEY.entityToObject(key.getBooth()));
        }
        return builder
                .purchaseId(key.getPurchaseId())
                .build();
    }

    /**
     * Mappable for PurchaseItem.Key
     */
    public static final Mappable<DataModel.PurchaseItem.Key, EntityModel.PurchaseItem.Key> PURCHASE_ITEM_KEY = new Mappable<>() {
        @Override
        public @NonNull Function<DataModel.PurchaseItem.Key, EntityModel.PurchaseItem.Key> objectToEntity() {
            return input -> {
                EntityModel.PurchaseItem.Key.KeyBuilder builder = EntityModel.PurchaseItem.Key.builder();
                return builder
                        .itemId(input.itemId())
                        .build();
            };
        }

        @Override
        public @NonNull Function<EntityModel.PurchaseItem.Key, DataModel.PurchaseItem.Key> entityToObject() {
            return entity -> {
                DataModel.PurchaseItem.Key.KeyBuilder builder = DataModel.PurchaseItem.Key.builder();
                if (entity.getPurchase() != null) {
                    builder.purchase(PURCHASE_KEY.entityToObject(entity.getPurchase()));
                }
                return builder
                        .itemId(entity.getItemId())
                        .build();
            };
        }
    };

    @NonNull
    public static EntityModel.PurchaseItem.Key objectToEntity(@NonNull DataModel.PurchaseItem.Key key) {
        EntityModel.PurchaseItem.Key.KeyBuilder builder = EntityModel.PurchaseItem.Key.builder();
        return builder
                .itemId(key.itemId())
                .build();
    }

    @NonNull
    public static DataModel.PurchaseItem.Key entityToObject(@NonNull EntityModel.PurchaseItem.Key key) {
        DataModel.PurchaseItem.Key.KeyBuilder builder = DataModel.PurchaseItem.Key.builder();
        if (key.getPurchase() != null) {
            builder.purchase(PURCHASE_KEY.entityToObject(key.getPurchase()));
        }
        return builder
                .itemId(key.getItemId())
                .build();
    }

    /**
     * Mappable for PurchaseItem
     */
    public static final Mappable<DataModel.PurchaseItem, EntityModel.PurchaseItem> PURCHASE_ITEM = new Mappable<>() {
        @Override
        public @NonNull Function<DataModel.PurchaseItem, EntityModel.PurchaseItem> objectToEntity() {
            return input -> {
                EntityModel.PurchaseItem.PurchaseItemBuilder builder = EntityModel.PurchaseItem.builder();
                if (input.key() != null) {
                    builder.key(PURCHASE_ITEM_KEY.objectToEntity(input.key()));
                }
                if (input.vendor() != null) {
                    builder.vendor(VENDOR_KEY.objectToEntity(input.vendor()));
                }
                return builder
                        .price(input.price())
                        .purchasedOn(input.purchasedOn())
                        .build();
            };
        }

        @Override
        public @NonNull Function<EntityModel.PurchaseItem, DataModel.PurchaseItem> entityToObject() {
            return entity -> {
                DataModel.PurchaseItem.PurchaseItemBuilder builder = DataModel.PurchaseItem.builder();
                if (entity.getKey() != null) {
                    builder.key(PURCHASE_ITEM_KEY.entityToObject(entity.getKey()));
                }
                if (entity.getVendor() != null) {
                    builder.vendor(VENDOR_KEY.entityToObject(entity.getVendor()));
                }
                return builder
                        .price(entity.getPrice())
                        .purchasedOn(entity.getPurchasedOn())
                        .build();
            };
        }
    };

    @NonNull
    public static EntityModel.PurchaseItem objectToEntity(@NonNull DataModel.PurchaseItem item) {
        EntityModel.PurchaseItem.PurchaseItemBuilder builder = EntityModel.PurchaseItem.builder();
        if (item.key() != null) {
            builder.key(PURCHASE_ITEM_KEY.objectToEntity(item.key()));
        }
        if (item.vendor() != null) {
            builder.vendor(VENDOR_KEY.objectToEntity(item.vendor()));
        }
        return builder
                .price(item.price())
                .purchasedOn(item.purchasedOn())
                .build();
    }

    @NonNull
    public static DataModel.PurchaseItem entityToObject(@NonNull EntityModel.PurchaseItem item) {
        DataModel.PurchaseItem.PurchaseItemBuilder builder = DataModel.PurchaseItem.builder();
        if (item.getKey() != null) {
            builder.key(PURCHASE_ITEM_KEY.entityToObject(item.getKey()));
        }
        if (item.getVendor() != null) {
            builder.vendor(VENDOR_KEY.entityToObject(item.getVendor()));
        }
        return builder
                .price(item.getPrice())
                .purchasedOn(item.getPurchasedOn())
                .build();
    }

    /**
     * Mappable for Purchase
     */
    public static final Mappable<DataModel.Purchase, EntityModel.Purchase> PURCHASE = new Mappable<>() {
        @Override
        public @NonNull Function<DataModel.Purchase, EntityModel.Purchase> objectToEntity() {
            return input -> {
                EntityModel.Purchase.PurchaseBuilder builder = EntityModel.Purchase.builder();
                if (input.key() != null) {
                    builder.key(PURCHASE_KEY.objectToEntity(input.key()));
                }
                if (input.items() != null) {
                    List<EntityModel.PurchaseItem> items = input.items().stream().map(PURCHASE_ITEM::objectToEntity).toList();
                    builder.items(items);
                }
                return builder
                        .value(input.value())
                        .purchasedOn(input.purchasedOn())
                        .build();
            };
        }

        @Override
        public @NonNull Function<EntityModel.Purchase, DataModel.Purchase> entityToObject() {
            return entity -> {
                DataModel.Purchase.PurchaseBuilder builder = DataModel.Purchase.builder();
                if (entity.getKey() != null) {
                    builder.key(PURCHASE_KEY.entityToObject(entity.getKey()));
                }
                if (entity.getItems() != null) {
                    List<DataModel.PurchaseItem> items = entity.getItems().stream().map(PURCHASE_ITEM::entityToObject).toList();
                    builder.items(items);
                }
                return builder
                        .value(entity.getValue())
                        .purchasedOn(entity.getPurchasedOn())
                        .build();
            };
        }
    };

    @NonNull
    public static EntityModel.Purchase objectToEntity(@NonNull DataModel.Purchase purchase) {
        EntityModel.Purchase.PurchaseBuilder builder = EntityModel.Purchase.builder();
        if (purchase.key() != null) {
            builder.key(PURCHASE_KEY.objectToEntity(purchase.key()));
        }
        if (purchase.items() != null) {
            List<EntityModel.PurchaseItem> items = purchase.items().stream().map(PURCHASE_ITEM::objectToEntity).toList();
            builder.items(items);
        }
        return builder
                .value(purchase.value())
                .purchasedOn(purchase.purchasedOn())
                .build();
    }

    @NonNull
    public static DataModel.Purchase entityToObject(@NonNull EntityModel.Purchase purchase) {
        DataModel.Purchase.PurchaseBuilder builder = DataModel.Purchase.builder();
        if (purchase.getKey() != null) {
            builder.key(PURCHASE_KEY.entityToObject(purchase.getKey()));
        }
        if (purchase.getItems() != null) {
            List<DataModel.PurchaseItem> items = purchase.getItems().stream().map(PURCHASE_ITEM::entityToObject).toList();
            builder.items(items);
        }
        return builder
                .value(purchase.getValue())
                .purchasedOn(purchase.getPurchasedOn())
                .build();
    }

    /**
     * Generic mappable interface
     *
     * @param <T> Type of the data model
     * @param <E> Type of the entity model
     */
    public interface Mappable<T, E> {

        @NonNull
        Function<T, E> objectToEntity();

        @NonNull
        Function<E, T> entityToObject();

        @NonNull
        default E objectToEntity(@NonNull T input) {
            return objectToEntity().apply(input);
        }

        @NonNull
        default T entityToObject(@NonNull E entity) {
            return entityToObject().apply(entity);
        }
    }
}
