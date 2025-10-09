/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.model;

import java.util.List;
import java.util.function.Function;
import lombok.NonNull;

/**
 * Utility class for mapping between data models and entity models.
 */
public class EntitiesMapper {
    private EntitiesMapper() {}

    /**
     * Mappable for Booth.Key
     */
    public static final Mappable<DataModel.Booth.Key, EntityModel.Booth.Key> BOOTH_KEY =
            new Mappable<>() {
                @Override
                public @NonNull Function<DataModel.Booth.Key, EntityModel.Booth.Key>
                        objectToEntity() {
                    return input ->
                            EntityModel.Booth.Key.builder().boothId(input.boothId()).build();
                }

                @Override
                public @NonNull Function<EntityModel.Booth.Key, DataModel.Booth.Key>
                        entityToObject() {
                    return entity ->
                            DataModel.Booth.Key.builder().boothId(entity.getBoothId()).build();
                }
            };

    @NonNull
    public static EntityModel.Booth.Key objectToEntity(@NonNull DataModel.Booth.Key key) {
        return BOOTH_KEY.objectToEntity(key);
    }

    @NonNull
    public static DataModel.Booth.Key entityToObject(@NonNull EntityModel.Booth.Key key) {
        return BOOTH_KEY.entityToObject(key);
    }

    /**
     * Mappable for Booth
     */
    public static final Mappable<DataModel.Booth, EntityModel.Booth> BOOTH =
            new Mappable<>() {
                @Override
                public @NonNull Function<DataModel.Booth, EntityModel.Booth> objectToEntity() {
                    return input -> {
                        EntityModel.Booth.BoothBuilder builder = EntityModel.Booth.builder();
                        if (input.key() != null) {
                            builder.key(BOOTH_KEY.objectToEntity(input.key()));
                        }
                        return builder.description(input.description())
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
                        return builder.description(entity.getDescription())
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
        return BOOTH.objectToEntity(booth);
    }

    @NonNull
    public static DataModel.Booth entityToObject(@NonNull EntityModel.Booth booth) {
        return BOOTH.entityToObject(booth);
    }

    /**
     * Mappable for Vendor.Key
     */
    public static final Mappable<DataModel.Vendor.Key, EntityModel.Vendor.Key> VENDOR_KEY =
            new Mappable<>() {
                @Override
                public @NonNull Function<DataModel.Vendor.Key, EntityModel.Vendor.Key>
                        objectToEntity() {
                    return input -> {
                        EntityModel.Vendor.Key.KeyBuilder builder =
                                EntityModel.Vendor.Key.builder();
                        if (input.booth() != null) {
                            builder.booth(BOOTH_KEY.objectToEntity(input.booth()));
                        }
                        return builder.vendorId(input.vendorId()).build();
                    };
                }

                @Override
                public @NonNull Function<EntityModel.Vendor.Key, DataModel.Vendor.Key>
                        entityToObject() {
                    return entity -> {
                        DataModel.Vendor.Key.KeyBuilder builder = DataModel.Vendor.Key.builder();
                        if (entity.getBooth() != null) {
                            builder.booth(BOOTH_KEY.entityToObject(entity.getBooth()));
                        }
                        return builder.vendorId(entity.getVendorId()).build();
                    };
                }
            };

    @NonNull
    public static EntityModel.Vendor.Key objectToEntity(@NonNull DataModel.Vendor.Key key) {
        return VENDOR_KEY.objectToEntity(key);
    }

    @NonNull
    public static DataModel.Vendor.Key entityToObject(@NonNull EntityModel.Vendor.Key key) {
        return VENDOR_KEY.entityToObject(key);
    }

    /**
     * Mappable for Vendor
     */
    public static final Mappable<DataModel.Vendor, EntityModel.Vendor> VENDOR =
            new Mappable<>() {
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
        return VENDOR.objectToEntity(vendor);
    }

    @NonNull
    public static DataModel.Vendor entityToObject(@NonNull EntityModel.Vendor vendor) {
        return VENDOR.entityToObject(vendor);
    }

    /**
     * Mappable for Purchase.Key
     */
    public static final Mappable<DataModel.Purchase.Key, EntityModel.Purchase.Key> PURCHASE_KEY =
            new Mappable<>() {
                @Override
                public @NonNull Function<DataModel.Purchase.Key, EntityModel.Purchase.Key>
                        objectToEntity() {
                    return input -> {
                        EntityModel.Purchase.Key.KeyBuilder builder =
                                EntityModel.Purchase.Key.builder();
                        if (input.booth() != null) {
                            builder.booth(BOOTH_KEY.objectToEntity(input.booth()));
                        }
                        return builder.purchaseId(input.purchaseId()).build();
                    };
                }

                @Override
                public @NonNull Function<EntityModel.Purchase.Key, DataModel.Purchase.Key>
                        entityToObject() {
                    return entity -> {
                        DataModel.Purchase.Key.KeyBuilder builder =
                                DataModel.Purchase.Key.builder();
                        if (entity.getBooth() != null) {
                            builder.booth(BOOTH_KEY.entityToObject(entity.getBooth()));
                        }
                        return builder.purchaseId(entity.getPurchaseId()).build();
                    };
                }
            };

    @NonNull
    public static EntityModel.Purchase.Key objectToEntity(@NonNull DataModel.Purchase.Key key) {
        return PURCHASE_KEY.objectToEntity(key);
    }

    @NonNull
    public static DataModel.Purchase.Key entityToObject(@NonNull EntityModel.Purchase.Key key) {
        return PURCHASE_KEY.entityToObject(key);
    }

    /**
     * Mappable for PurchaseItem.Key
     */
    public static final Mappable<DataModel.PurchaseItem.Key, EntityModel.PurchaseItem.Key>
            PURCHASE_ITEM_KEY =
                    new Mappable<>() {
                        @Override
                        public @NonNull Function<
                                        DataModel.PurchaseItem.Key, EntityModel.PurchaseItem.Key>
                                objectToEntity() {
                            return input -> {
                                EntityModel.PurchaseItem.Key.KeyBuilder builder =
                                        EntityModel.PurchaseItem.Key.builder();
                                if (input.purchase() != null) {
                                    builder.purchase(PURCHASE_KEY.objectToEntity(input.purchase()));
                                }
                                return builder.itemId(input.itemId()).build();
                            };
                        }

                        @Override
                        public @NonNull Function<
                                        EntityModel.PurchaseItem.Key, DataModel.PurchaseItem.Key>
                                entityToObject() {
                            return entity -> {
                                DataModel.PurchaseItem.Key.KeyBuilder builder =
                                        DataModel.PurchaseItem.Key.builder();
                                if (entity.getPurchase() != null) {
                                    builder.purchase(
                                            PURCHASE_KEY.entityToObject(entity.getPurchase()));
                                }
                                return builder.itemId(entity.getItemId()).build();
                            };
                        }
                    };

    @NonNull
    public static EntityModel.PurchaseItem.Key objectToEntity(
            @NonNull DataModel.PurchaseItem.Key key) {
        return PURCHASE_ITEM_KEY.objectToEntity(key);
    }

    @NonNull
    public static DataModel.PurchaseItem.Key entityToObject(
            @NonNull EntityModel.PurchaseItem.Key key) {
        return PURCHASE_ITEM_KEY.entityToObject(key);
    }

    /**
     * Mappable for PurchaseItem
     */
    public static final Mappable<DataModel.PurchaseItem, EntityModel.PurchaseItem> PURCHASE_ITEM =
            new Mappable<>() {
                @Override
                public @NonNull Function<DataModel.PurchaseItem, EntityModel.PurchaseItem>
                        objectToEntity() {
                    return input -> {
                        EntityModel.PurchaseItem.PurchaseItemBuilder builder =
                                EntityModel.PurchaseItem.builder();
                        if (input.key() != null) {
                            builder.key(PURCHASE_ITEM_KEY.objectToEntity(input.key()));
                        }
                        if (input.vendor() != null) {
                            builder.vendorId(input.vendor().vendorId());
                        }
                        return builder.price(input.price())
                                .purchasedOn(input.purchasedOn())
                                .build();
                    };
                }

                @Override
                public @NonNull Function<EntityModel.PurchaseItem, DataModel.PurchaseItem>
                        entityToObject() {
                    return entity -> {
                        DataModel.PurchaseItem.PurchaseItemBuilder builder =
                                DataModel.PurchaseItem.builder();
                        DataModel.PurchaseItem.Key key = null;
                        if (entity.getKey() != null) {
                            key = PURCHASE_ITEM_KEY.entityToObject(entity.getKey());
                            builder.key(key);
                        }
                        if (entity.getVendorId() != null) {
                            DataModel.Vendor.Key.KeyBuilder vendorKeyBuilder =
                                    DataModel.Vendor.Key.builder().vendorId(entity.getVendorId());
                            if (key != null && key.purchase() != null) {
                                vendorKeyBuilder.booth(key.purchase().booth());
                            }
                            builder.vendor(vendorKeyBuilder.build());
                        }
                        return builder.price(entity.getPrice())
                                .purchasedOn(entity.getPurchasedOn())
                                .build();
                    };
                }
            };

    @NonNull
    public static EntityModel.PurchaseItem objectToEntity(@NonNull DataModel.PurchaseItem item) {
        return PURCHASE_ITEM.objectToEntity(item);
    }

    @NonNull
    public static DataModel.PurchaseItem entityToObject(@NonNull EntityModel.PurchaseItem item) {
        return PURCHASE_ITEM.entityToObject(item);
    }

    /**
     * Mappable for Purchase
     */
    public static final Mappable<DataModel.Purchase, EntityModel.Purchase> PURCHASE =
            new Mappable<>() {
                @Override
                public @NonNull Function<DataModel.Purchase, EntityModel.Purchase>
                        objectToEntity() {
                    return input -> {
                        EntityModel.Purchase.PurchaseBuilder builder =
                                EntityModel.Purchase.builder();
                        if (input.key() != null) {
                            builder.key(PURCHASE_KEY.objectToEntity(input.key()));
                        }
                        if (input.items() != null) {
                            List<EntityModel.PurchaseItem> items =
                                    input.items().stream()
                                            .map(PURCHASE_ITEM::objectToEntity)
                                            .toList();
                            builder.items(items);
                        }
                        return builder.value(input.value())
                                .purchasedOn(input.purchasedOn())
                                .build();
                    };
                }

                @Override
                public @NonNull Function<EntityModel.Purchase, DataModel.Purchase>
                        entityToObject() {
                    return entity -> {
                        DataModel.Purchase.PurchaseBuilder builder = DataModel.Purchase.builder();
                        if (entity.getKey() != null) {
                            builder.key(PURCHASE_KEY.entityToObject(entity.getKey()));
                        }
                        if (entity.getItems() != null) {
                            List<DataModel.PurchaseItem> items =
                                    entity.getItems().stream()
                                            .map(PURCHASE_ITEM::entityToObject)
                                            .toList();
                            builder.items(items);
                        }
                        return builder.value(entity.getValue())
                                .purchasedOn(entity.getPurchasedOn())
                                .build();
                    };
                }
            };

    @NonNull
    public static EntityModel.Purchase objectToEntity(@NonNull DataModel.Purchase purchase) {
        return PURCHASE.objectToEntity(purchase);
    }

    @NonNull
    public static DataModel.Purchase entityToObject(@NonNull EntityModel.Purchase purchase) {
        return PURCHASE.entityToObject(purchase);
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
