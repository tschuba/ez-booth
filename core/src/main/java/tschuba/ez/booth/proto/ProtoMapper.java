package tschuba.ez.booth.proto;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import tschuba.ez.booth.DataModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Provides mappings between {@link DataModel} and {@link ProtoModel} objects.
 */
public class ProtoMapper {
    /**
     * Mapper for {@link DataModel.BoothEvent.Key} and {@link ProtoModel.BoothEventKey}.
     */
    public static final Mapper<DataModel.BoothEvent.Key, ProtoModel.BoothEventKey> EVENT_KEY = new Mapper<>() {
        @Override
        public Function<DataModel.BoothEvent.Key, ProtoModel.BoothEventKey> objectToMessage() {
            return key -> ProtoModel.BoothEventKey.newBuilder()
                    .setEventId(key.eventId())
                    .build();
        }

        @Override
        public Function<ProtoModel.BoothEventKey, DataModel.BoothEvent.Key> messageToObject() {
            return key -> DataModel.BoothEvent.Key.builder()
                    .eventId(key.getEventId())
                    .build();
        }
    };

    /**
     * Mapper for {@link DataModel.BoothEvent} and {@link ProtoModel.BoothEvent}.
     */
    public static final Mapper<DataModel.BoothEvent, ProtoModel.BoothEvent> EVENT = new Mapper<>() {
        @Override
        public Function<DataModel.BoothEvent, ProtoModel.BoothEvent> objectToMessage() {
            return evt -> {
                ProtoModel.BoothEvent.Builder builder = ProtoModel.BoothEvent.newBuilder();
                if (evt.key() != null) {
                    ProtoModel.BoothEventKey eventKey = EVENT_KEY.objectToMessage(evt.key());
                    builder.setKey(eventKey);
                }
                builder.setDescription(evt.description());
                if (evt.date() != null) {
                    builder.setDate(DateAndTime.dateOf(evt.date()));
                }
                if (evt.participationFee() != null) {
                    builder.setParticipationFee(evt.participationFee().floatValue());
                }
                if (evt.salesFee() != null) {
                    builder.setSalesFee(evt.salesFee().floatValue());
                }
                if (evt.feesRoundingStep() != null) {
                    builder.setFeesRoundingStep(evt.feesRoundingStep().floatValue());
                }
                builder.setClosed(evt.closed());
                if (evt.closedOn() != null) {
                    builder.setClosedOn(DateAndTime.timestampOf(evt.closedOn()));
                }
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.BoothEvent, DataModel.BoothEvent> messageToObject() {
            return evt -> {
                DataModel.BoothEvent.BoothEventBuilder builder = DataModel.BoothEvent.builder();
                if (evt.hasKey()) {
                    DataModel.BoothEvent.Key key = EVENT_KEY.messageToObject(evt.getKey());
                    builder.key(key);
                }
                if (evt.hasDate()) {
                    LocalDate date = DateAndTime.asDate(evt.getDate());
                    builder.date(date);
                }
                builder.description(evt.getDescription());
                builder.participationFee(BigDecimal.valueOf(evt.getParticipationFee()));
                builder.salesFee(BigDecimal.valueOf(evt.getSalesFee()));
                builder.feesRoundingStep(BigDecimal.valueOf(evt.getFeesRoundingStep()));
                builder.closed(evt.getClosed());
                if (evt.hasClosedOn()) {
                    LocalDateTime closedOn = DateAndTime.asDateTime(evt.getClosedOn());
                    builder.closedOn(closedOn);
                }
                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link DataModel.Purchase.Key} and {@link ProtoModel.PurchaseKey}.
     */
    public static final Mapper<DataModel.Purchase.Key, ProtoModel.PurchaseKey> PURCHASE_KEY = new Mapper<>() {
        @Override
        public Function<DataModel.Purchase.Key, ProtoModel.PurchaseKey> objectToMessage() {
            return key -> {
                ProtoModel.PurchaseKey.Builder builder = ProtoModel.PurchaseKey.newBuilder();
                if (key.event() != null) {
                    ProtoModel.BoothEventKey event = EVENT_KEY.objectToMessage(key.event());
                    builder.setEvent(event);
                }
                builder.setPurchaseId(key.purchaseId());
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.PurchaseKey, DataModel.Purchase.Key> messageToObject() {
            return key -> {
                DataModel.Purchase.Key.KeyBuilder builder = DataModel.Purchase.Key.builder();
                if (key.hasEvent()) {
                    DataModel.BoothEvent.Key eventKey = EVENT_KEY.messageToObject(key.getEvent());
                    builder.event(eventKey);
                }
                builder.purchaseId(key.getPurchaseId());
                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link DataModel.Purchase} and {@link ProtoModel.Purchase}.
     */
    public static final Mapper<DataModel.Purchase, ProtoModel.Purchase> PURCHASE = new Mapper<>() {
        @Override
        public Function<DataModel.Purchase, ProtoModel.Purchase> objectToMessage() {
            return purchase -> {
                ProtoModel.Purchase.Builder builder = ProtoModel.Purchase.newBuilder();
                if (purchase.key() != null) {
                    ProtoModel.PurchaseKey purchaseKey = PURCHASE_KEY.objectToMessage(purchase.key());
                    builder.setKey(purchaseKey);
                }
                if (purchase.items() != null) {
                    Repeated.copyToMessage(purchase.items(), PURCHASE_ITEM, builder::setItems);
                }
                if (purchase.value() != null) {
                    builder.setValue(purchase.value().floatValue());
                }
                if (purchase.purchasedOn() != null) {
                    builder.setPurchasedOn(DateAndTime.timestampOf(purchase.purchasedOn()));
                }
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.Purchase, DataModel.Purchase> messageToObject() {
            return purchase -> {
                DataModel.Purchase.PurchaseBuilder builder = DataModel.Purchase.builder();
                if (purchase.hasKey()) {
                    DataModel.Purchase.Key key = PURCHASE_KEY.messageToObject(purchase.getKey());
                    builder.key(key);
                }
                if (purchase.hasPurchasedOn()) {
                    LocalDateTime purchasedOn = DateAndTime.asDateTime(purchase.getPurchasedOn());
                    builder.purchasedOn(purchasedOn);
                }
                builder.value(BigDecimal.valueOf(purchase.getValue()));

                List<DataModel.PurchaseItem> convertedItemsList = purchase.getItemsList().stream().map(PURCHASE_ITEM::messageToObject).toList();
                builder.items(convertedItemsList);

                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link DataModel.PurchaseItem.Key} and {@link ProtoModel.PurchaseItemKey}.
     */
    public static final Mapper<DataModel.PurchaseItem.Key, ProtoModel.PurchaseItemKey> PURCHASE_ITEM_KEY = new Mapper<>() {
        @Override
        public Function<DataModel.PurchaseItem.Key, ProtoModel.PurchaseItemKey> objectToMessage() {
            return key -> {
                ProtoModel.PurchaseItemKey.Builder builder = ProtoModel.PurchaseItemKey.newBuilder();
                if (key.vendor() != null) {
                    ProtoModel.VendorKey vendor = VENDOR_KEY.objectToMessage(key.vendor());
                    builder.setVendor(vendor);
                }
                builder.setItemId(key.itemId());
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.PurchaseItemKey, DataModel.PurchaseItem.Key> messageToObject() {
            return key -> {
                DataModel.PurchaseItem.Key.KeyBuilder builder = DataModel.PurchaseItem.Key.builder();
                if (key.hasVendor()) {
                    DataModel.Vendor.Key vendor = VENDOR_KEY.messageToObject(key.getVendor());
                    builder.vendor(vendor);
                }
                builder.itemId(key.getItemId());
                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link DataModel.PurchaseItem} and {@link ProtoModel.PurchaseItem}.
     */
    public static final Mapper<DataModel.PurchaseItem, ProtoModel.PurchaseItem> PURCHASE_ITEM = new Mapper<>() {
        @Override
        public Function<DataModel.PurchaseItem, ProtoModel.PurchaseItem> objectToMessage() {
            return item -> {
                ProtoModel.PurchaseItem.Builder builder = ProtoModel.PurchaseItem.newBuilder();
                if (item.key() != null) {
                    ProtoModel.PurchaseItemKey itemKey = PURCHASE_ITEM_KEY.objectToMessage(item.key());
                    builder.setKey(itemKey);
                }
                if (item.price() != null) {
                    builder.setPrice(item.price().floatValue());
                }
                if (item.purchasedOn() != null) {
                    builder.setPurchasedOn(DateAndTime.timestampOf(item.purchasedOn()));
                }
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.PurchaseItem, DataModel.PurchaseItem> messageToObject() {
            return item -> {
                DataModel.PurchaseItem.PurchaseItemBuilder builder = DataModel.PurchaseItem.builder();
                if (item.hasKey()) {
                    DataModel.PurchaseItem.Key key = PURCHASE_ITEM_KEY.messageToObject(item.getKey());
                    builder.key(key);
                }
                builder.price(BigDecimal.valueOf(item.getPrice()));
                if (item.hasPurchasedOn()) {
                    LocalDateTime purchasedOn = DateAndTime.asDateTime(item.getPurchasedOn());
                    builder.purchasedOn(purchasedOn);
                }
                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link DataModel.Vendor.Key} and {@link ProtoModel.VendorKey}.
     */
    public static final Mapper<DataModel.Vendor.Key, ProtoModel.VendorKey> VENDOR_KEY = new Mapper<>() {
        @Override
        public Function<DataModel.Vendor.Key, ProtoModel.VendorKey> objectToMessage() {
            return key -> {
                ProtoModel.VendorKey.Builder builder = ProtoModel.VendorKey.newBuilder();
                if (key.event() != null) {
                    ProtoModel.BoothEventKey eventKey = EVENT_KEY.objectToMessage(key.event());
                    builder.setEvent(eventKey);
                }
                builder.setVendorId(key.vendorId());
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.VendorKey, DataModel.Vendor.Key> messageToObject() {
            return key -> {
                DataModel.Vendor.Key.KeyBuilder builder = DataModel.Vendor.Key.builder();
                if (key.hasEvent()) {
                    DataModel.BoothEvent.Key event = EVENT_KEY.messageToObject(key.getEvent());
                    builder.event(event);
                }
                builder.vendorId(key.getVendorId());
                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link DataModel.Vendor} and {@link ProtoModel.Vendor}.
     */
    public static final Mapper<DataModel.Vendor, ProtoModel.Vendor> VENDOR = new Mapper<>() {
        @Override
        public Function<DataModel.Vendor, ProtoModel.Vendor> objectToMessage() {
            return vendor -> {
                ProtoModel.Vendor.Builder builder = ProtoModel.Vendor.newBuilder();
                if (vendor.key() != null) {
                    ProtoModel.VendorKey vendorKey = VENDOR_KEY.objectToMessage(vendor.key());
                    builder.setKey(vendorKey);
                }
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.Vendor, DataModel.Vendor> messageToObject() {
            return vendor -> {
                DataModel.Vendor.VendorBuilder builder = DataModel.Vendor.builder();
                if (vendor.hasKey()) {
                    DataModel.Vendor.Key key = VENDOR_KEY.messageToObject(vendor.getKey());
                    builder.key(key);
                }
                return builder.build();
            };
        }
    };

    /**
     * Interface for implementations of mapping between object and protobuf message.
     *
     * @param <T>
     * @param <M>
     */
    public interface Mapper<T, M extends Message> {

        Function<T, M> objectToMessage();

        Function<M, T> messageToObject();

        default M objectToMessage(@NonNull T input) {
            return objectToMessage().apply(input);
        }

        default T messageToObject(@NonNull M message) {
            return messageToObject().apply(message);
        }
    }

    /**
     * Provides mapping between protobuf date/time types and Java date/time types.
     */
    public static class DateAndTime {

        private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

        private DateAndTime() {
        }

        public static LocalDate asDate(@Nullable ProtoCore.Date date) {
            if (date == null) {
                return null;
            }
            return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
        }

        public static ProtoCore.Date dateOf(@Nullable LocalDate date) {
            if (date == null) {
                return null;
            }
            return ProtoCore.Date.newBuilder().setYear(date.getYear())
                    .setMonth(date.getMonthValue())
                    .setDay(date.getDayOfMonth())
                    .build();
        }

        public static LocalDateTime asDateTime(@Nullable com.google.protobuf.Timestamp timestamp) {
            if (timestamp == null) {
                return null;
            }
            return LocalDateTime.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos(), ZONE_OFFSET);
        }

        public static Timestamp timestampOf(@Nullable LocalDateTime dateTime) {
            if (dateTime == null) {
                return null;
            }
            return com.google.protobuf.Timestamp.newBuilder()
                    .setSeconds(dateTime.toEpochSecond(ZONE_OFFSET))
                    .setNanos(dateTime.getNano())
                    .build();
        }
    }

    /**
     * Helper for mapping repeated fields.
     */
    static class Repeated {
        private Repeated() {
        }

        public static <T, M extends Message> void copyToMessage(@Nullable List<T> items, @NonNull Mapper<T, M> mapper, @NonNull BiConsumer<Integer, M> messageConsumer) {
            if (items != null) {
                for (int index = 0; index < items.size(); index++) {
                    T item = items.get(index);
                    M message = mapper.objectToMessage(item);
                    messageConsumer.accept(index, message);
                }
            }
        }
    }

}
