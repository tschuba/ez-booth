package tschuba.ez.booth.proto;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import tschuba.ez.booth.DataModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ProtoMapper {
    private static final Mapper<DataModel.BoothEvent.Key, ProtoModel.BoothEventKey> EVENT_KEY = new Mapper<>() {
        @Override
        public Function<DataModel.BoothEvent.Key, ProtoModel.BoothEventKey> objectToMessage() {
            return key -> ProtoModel.BoothEventKey.newBuilder()
                    .setEventId(key.eventId())
                    .build();
        }

        @Override
        public Function<ProtoModel.BoothEventKey, DataModel.BoothEvent.Key> messageToObject() {
            return key -> {

            };
        }
    };

    private static final Mapper<DataModel.BoothEvent, ProtoModel.BoothEvent> EVENT = new Mapper<>() {
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

            };
        }
    };

    private static final Mapper<DataModel.Purchase.Key, ProtoModel.PurchaseKey> PURCHASE_KEY = new Mapper<>() {
        @Override
        public Function<DataModel.Purchase.Key, ProtoModel.PurchaseKey> objectToMessage() {
            return key -> {
                ProtoModel.PurchaseKey.Builder builder = ProtoModel.PurchaseKey.newBuilder();
                if (key.event() != null) {
                    ProtoModel.BoothEventKey eventKey = EVENT_KEY.objectToMessage(key.event());
                    builder.setEvent(eventKey);
                }
                builder.setPurchaseId(key.purchaseId());
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.PurchaseKey, DataModel.Purchase.Key> messageToObject() {
            return key -> {

            };
        }
    };

    private static final Mapper<DataModel.Purchase, ProtoModel.Purchase> PURCHASE = new Mapper<>() {
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

            };
        }
    };

    private static final Mapper<DataModel.PurchaseItem.Key, ProtoModel.PurchaseItemKey> PURCHASE_ITEM_KEY = new Mapper<>() {
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

            };
        }
    };

    private static final Mapper<DataModel.PurchaseItem, ProtoModel.PurchaseItem> PURCHASE_ITEM = new Mapper<>() {
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

            };
        }
    };

    private static final Mapper<DataModel.Vendor.Key, ProtoModel.VendorKey> VENDOR_KEY = new Mapper<>() {
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

            };
        }
    };

    private static final Mapper<DataModel.Vendor, ProtoModel.Vendor> VENDOR = new Mapper<>() {
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
            return key -> {

            };
        }
    };

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
