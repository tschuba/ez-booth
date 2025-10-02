package tschuba.ez.booth.proto;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import tschuba.ez.booth.DataModel;
import tschuba.ez.booth.services.ServiceModel;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Provides mappings between {@link DataModel} and {@link ProtoModel} objects.
 */
public class ProtoMapper {
    /**
     * Mapper for {@link DataModel.Booth.Key} and {@link ProtoModel.BoothKey}.
     */
    public static final Mappable<DataModel.Booth.Key, ProtoModel.BoothKey> BOOTH_KEY = new Mappable<>() {
        @Override
        @NonNull
        public Function<DataModel.Booth.Key, ProtoModel.BoothKey> objectToMessage() {
            return key -> ProtoModel.BoothKey.newBuilder()
                    .setBoothId(key.boothId())
                    .build();
        }

        @Override
        @NonNull
        public Function<ProtoModel.BoothKey, DataModel.Booth.Key> messageToObject() {
            return key -> DataModel.Booth.Key.builder()
                    .boothId(key.getBoothId())
                    .build();
        }
    };

    @NonNull
    public static ProtoModel.BoothKey objectToMessage(@NonNull DataModel.Booth.Key object) {
        return BOOTH_KEY.objectToMessage(object);
    }

    @NonNull
    public static DataModel.Booth.Key messageToObject(@NonNull ProtoModel.BoothKey message) {
        return BOOTH_KEY.messageToObject(message);
    }

    /**
     * Mapper for {@link DataModel.Booth} and {@link ProtoModel.Booth}.
     */
    public static final Mappable<DataModel.Booth, ProtoModel.Booth> BOOTH = new Mappable<>() {
        @Override
        @NonNull
        public Function<DataModel.Booth, ProtoModel.Booth> objectToMessage() {
            return booth -> {
                ProtoModel.Booth.Builder builder = ProtoModel.Booth.newBuilder();
                if (booth.key() != null) {
                    ProtoModel.BoothKey key = BOOTH_KEY.objectToMessage(booth.key());
                    builder.setKey(key);
                }
                builder.setDescription(booth.description());
                if (booth.date() != null) {
                    builder.setDate(DateAndTime.dateOf(booth.date()));
                }
                if (booth.participationFee() != null) {
                    builder.setParticipationFee(booth.participationFee().floatValue());
                }
                if (booth.salesFee() != null) {
                    builder.setSalesFee(booth.salesFee().floatValue());
                }
                if (booth.feesRoundingStep() != null) {
                    builder.setFeesRoundingStep(booth.feesRoundingStep().floatValue());
                }
                builder.setClosed(booth.closed());
                if (booth.closedOn() != null) {
                    builder.setClosedOn(DateAndTime.timestampOf(booth.closedOn()));
                }
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoModel.Booth, DataModel.Booth> messageToObject() {
            return booth -> {
                DataModel.Booth.BoothBuilder builder = DataModel.Booth.builder();
                if (booth.hasKey()) {
                    DataModel.Booth.Key key = BOOTH_KEY.messageToObject(booth.getKey());
                    builder.key(key);
                }
                if (booth.hasDate()) {
                    LocalDate date = DateAndTime.asDate(booth.getDate());
                    builder.date(date);
                }
                builder.description(booth.getDescription());
                builder.participationFee(BigDecimal.valueOf(booth.getParticipationFee()));
                builder.salesFee(BigDecimal.valueOf(booth.getSalesFee()));
                builder.feesRoundingStep(BigDecimal.valueOf(booth.getFeesRoundingStep()));
                builder.closed(booth.getClosed());
                if (booth.hasClosedOn()) {
                    LocalDateTime closedOn = DateAndTime.asDateTime(booth.getClosedOn());
                    builder.closedOn(closedOn);
                }
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoModel.Booth objectToMessage(@NonNull DataModel.Booth object) {
        return BOOTH.objectToMessage(object);
    }

    @NonNull
    public static DataModel.Booth messageToObject(@NonNull ProtoModel.Booth message) {
        return BOOTH.messageToObject(message);
    }

    /**
     * Mapper for {@link DataModel.Purchase.Key} and {@link ProtoModel.PurchaseKey}.
     */
    public static final Mappable<DataModel.Purchase.Key, ProtoModel.PurchaseKey> PURCHASE_KEY = new Mappable<>() {
        @Override
        @NonNull
        public Function<DataModel.Purchase.Key, ProtoModel.PurchaseKey> objectToMessage() {
            return key -> {
                ProtoModel.PurchaseKey.Builder builder = ProtoModel.PurchaseKey.newBuilder();
                if (key.booth() != null) {
                    ProtoModel.BoothKey booth = BOOTH_KEY.objectToMessage(key.booth());
                    builder.setBooth(booth);
                }
                builder.setPurchaseId(key.purchaseId());
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoModel.PurchaseKey, DataModel.Purchase.Key> messageToObject() {
            return key -> {
                DataModel.Purchase.Key.KeyBuilder builder = DataModel.Purchase.Key.builder();
                if (key.hasBooth()) {
                    DataModel.Booth.Key booth = BOOTH_KEY.messageToObject(key.getBooth());
                    builder.booth(booth);
                }
                builder.purchaseId(key.getPurchaseId());
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoModel.PurchaseKey objectToMessage(@NonNull DataModel.Purchase.Key object) {
        return PURCHASE_KEY.objectToMessage(object);
    }

    @NonNull
    public static DataModel.Purchase.Key messageToObject(@NonNull ProtoModel.PurchaseKey message) {
        return PURCHASE_KEY.messageToObject(message);
    }

    /**
     * Mapper for {@link DataModel.Purchase} and {@link ProtoModel.Purchase}.
     */
    public static final Mappable<DataModel.Purchase, ProtoModel.Purchase> PURCHASE = new Mappable<>() {
        @Override
        @NonNull
        public Function<DataModel.Purchase, ProtoModel.Purchase> objectToMessage() {
            return purchase -> {
                ProtoModel.Purchase.Builder builder = ProtoModel.Purchase.newBuilder();
                if (purchase.key() != null) {
                    ProtoModel.PurchaseKey purchaseKey = PURCHASE_KEY.objectToMessage(purchase.key());
                    builder.setKey(purchaseKey);
                }
                if (purchase.items() != null) {
                    purchase.items().stream().map(PURCHASE_ITEM::objectToMessage).forEach(builder::addItems);
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
        @NonNull
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

    @NonNull
    public static ProtoModel.Purchase objectToMessage(@NonNull DataModel.Purchase object) {
        return PURCHASE.objectToMessage(object);
    }

    @NonNull
    public static DataModel.Purchase messageToObject(@NonNull ProtoModel.Purchase message) {
        return PURCHASE.messageToObject(message);
    }

    /**
     * Mapper for {@link DataModel.PurchaseItem} and {@link ProtoModel.PurchaseItem}.
     */
    public static final Mappable<DataModel.PurchaseItem, ProtoModel.PurchaseItem> PURCHASE_ITEM = new Mappable<>() {
        @Override
        @NonNull
        public Function<DataModel.PurchaseItem, ProtoModel.PurchaseItem> objectToMessage() {
            return item -> {
                ProtoModel.PurchaseItem.Builder builder = ProtoModel.PurchaseItem.newBuilder();
                if (item.key() != null) {
                    builder.setItemId(item.key().itemId());
                }
                if (item.vendor() != null) {
                    ProtoModel.VendorKey vendor = VENDOR_KEY.objectToMessage(item.vendor());
                    builder.setVendor(vendor);
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
        @NonNull
        public Function<ProtoModel.PurchaseItem, DataModel.PurchaseItem> messageToObject() {
            return item -> {
                DataModel.PurchaseItem.PurchaseItemBuilder builder = DataModel.PurchaseItem.builder();
                builder.key(DataModel.PurchaseItem.Key.builder().itemId(item.getItemId()).build());
                if (item.hasVendor()) {
                    builder.vendor(VENDOR_KEY.messageToObject(item.getVendor()));
                }
                builder.price(new BigDecimal(Float.toString(item.getPrice())));
                if (item.hasPurchasedOn()) {
                    LocalDateTime purchasedOn = DateAndTime.asDateTime(item.getPurchasedOn());
                    builder.purchasedOn(purchasedOn);
                }
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoModel.PurchaseItem objectToMessage(@NonNull DataModel.PurchaseItem object) {
        return PURCHASE_ITEM.objectToMessage(object);
    }

    @NonNull
    public static DataModel.PurchaseItem messageToObject(@NonNull ProtoModel.PurchaseItem message) {
        return PURCHASE_ITEM.messageToObject(message);
    }

    /**
     * Mapper for {@link DataModel.Vendor.Key} and {@link ProtoModel.VendorKey}.
     */
    public static final Mappable<DataModel.Vendor.Key, ProtoModel.VendorKey> VENDOR_KEY = new Mappable<>() {
        @Override
        @NonNull
        public Function<DataModel.Vendor.Key, ProtoModel.VendorKey> objectToMessage() {
            return key -> {
                ProtoModel.VendorKey.Builder builder = ProtoModel.VendorKey.newBuilder();
                if (key.booth() != null) {
                    ProtoModel.BoothKey eventKey = BOOTH_KEY.objectToMessage(key.booth());
                    builder.setBooth(eventKey);
                }
                builder.setVendorId(key.vendorId());
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoModel.VendorKey, DataModel.Vendor.Key> messageToObject() {
            return key -> {
                DataModel.Vendor.Key.KeyBuilder builder = DataModel.Vendor.Key.builder();
                if (key.hasBooth()) {
                    DataModel.Booth.Key booth = BOOTH_KEY.messageToObject(key.getBooth());
                    builder.booth(booth);
                }
                builder.vendorId(key.getVendorId());
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoModel.VendorKey objectToMessage(@NonNull DataModel.Vendor.Key object) {
        return VENDOR_KEY.objectToMessage(object);
    }

    @NonNull
    public static DataModel.Vendor.Key messageToObject(@NonNull ProtoModel.VendorKey message) {
        return VENDOR_KEY.messageToObject(message);
    }

    /**
     * Mapper for {@link DataModel.Vendor} and {@link ProtoModel.Vendor}.
     */
    public static final Mappable<DataModel.Vendor, ProtoModel.Vendor> VENDOR = new Mappable<>() {
        @Override
        @NonNull
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
        @NonNull
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

    @NonNull
    public static ProtoModel.Vendor objectToMessage(@NonNull DataModel.Vendor object) {
        return VENDOR.objectToMessage(object);
    }

    @NonNull
    public static DataModel.Vendor messageToObject(@NonNull ProtoModel.Vendor message) {
        return VENDOR.messageToObject(message);
    }

    /**
     * Mapper for {@link ServiceModel.Checkout} and {@link ProtoServices.CheckoutInput}.
     */
    public static final Mappable<ServiceModel.Checkout, ProtoServices.CheckoutInput> CHECKOUT = new Mappable<>() {
        @Override
        @NonNull
        public Function<ServiceModel.Checkout, ProtoServices.CheckoutInput> objectToMessage() {
            return checkout -> {
                ProtoServices.CheckoutInput.Builder builder = ProtoServices.CheckoutInput.newBuilder();
                ProtoModel.BoothKey boothKey = BOOTH_KEY.objectToMessage(checkout.booth());
                builder.setBooth(boothKey);
                checkout.items().stream().map(PURCHASE_ITEM::objectToMessage).forEach(builder::addItems);
                builder.setPrintReceipt(checkout.printReceipt());
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoServices.CheckoutInput, ServiceModel.Checkout> messageToObject() {
            return checkout -> {
                ServiceModel.Checkout.CheckoutBuilder builder = ServiceModel.Checkout.builder();
                if (checkout.hasBooth()) {
                    DataModel.Booth.Key boothKey = BOOTH_KEY.messageToObject(checkout.getBooth());
                    builder.booth(boothKey);
                }
                List<DataModel.PurchaseItem> convertedItemsList = checkout.getItemsList().stream().map(PURCHASE_ITEM::messageToObject).toList();
                builder.items(convertedItemsList);
                builder.printReceipt(checkout.getPrintReceipt());
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoServices.CheckoutInput objectToMessage(@NonNull ServiceModel.Checkout object) {
        return CHECKOUT.objectToMessage(object);
    }

    @NonNull
    public static ServiceModel.Checkout messageToObject(@NonNull ProtoServices.CheckoutInput message) {
        return CHECKOUT.messageToObject(message);
    }

    /**
     * Mapper for {@link ServiceModel.ChargedFees} and {@link ProtoServices.ChargedFees}.
     */
    public static final Mappable<ServiceModel.ChargedFees, ProtoServices.ChargedFees> CHARGED_FEES = new Mappable<>() {
        @Override
        @NonNull
        public Function<ServiceModel.ChargedFees, ProtoServices.ChargedFees> objectToMessage() {
            return fees -> {
                ProtoServices.ChargedFees.Builder builder = ProtoServices.ChargedFees.newBuilder();
                builder.setParticipationFee(fees.participationFee().floatValue());
                builder.setSalesFee(fees.salesFee().floatValue());
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoServices.ChargedFees, ServiceModel.ChargedFees> messageToObject() {
            return fees -> {
                ServiceModel.ChargedFees.ChargedFeesBuilder builder = ServiceModel.ChargedFees.builder();
                builder.participationFee(BigDecimal.valueOf(fees.getParticipationFee()));
                builder.salesFee(BigDecimal.valueOf(fees.getSalesFee()));
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoServices.ChargedFees objectToMessage(@NonNull ServiceModel.ChargedFees object) {
        return CHARGED_FEES.objectToMessage(object);
    }

    @NonNull
    public static ServiceModel.ChargedFees messageToObject(@NonNull ProtoServices.ChargedFees message) {
        return CHARGED_FEES.messageToObject(message);
    }

    /**
     * Mapper for {@link ServiceModel.ChargingConfig} and {@link ProtoServices.ChargingConfig}.
     */
    public static final Mappable<ServiceModel.ChargingConfig, ProtoServices.ChargingConfig> CHARGING_CONFIG = new Mappable<>() {
        @Override
        @NonNull
        public Function<ServiceModel.ChargingConfig, ProtoServices.ChargingConfig> objectToMessage() {
            return config -> {
                ProtoServices.ChargingConfig.Builder builder = ProtoServices.ChargingConfig.newBuilder();
                builder.setParticipationFee(config.participationFee().floatValue());
                builder.setSalesFee(config.salesFee().floatValue());
                builder.setRoundingStep(config.roundingStep().floatValue());
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoServices.ChargingConfig, ServiceModel.ChargingConfig> messageToObject() {
            return config -> {
                ServiceModel.ChargingConfig.ChargingConfigBuilder builder = ServiceModel.ChargingConfig.builder();
                builder.participationFee(BigDecimal.valueOf(config.getParticipationFee()));
                builder.salesFee(BigDecimal.valueOf(config.getSalesFee()));
                builder.roundingStep(BigDecimal.valueOf(config.getRoundingStep()));
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoServices.ChargingConfig objectToMessage(@NonNull ServiceModel.ChargingConfig object) {
        return CHARGING_CONFIG.objectToMessage(object);
    }

    @NonNull
    public static ServiceModel.ChargingConfig messageToObject(@NonNull ProtoServices.ChargingConfig message) {
        return CHARGING_CONFIG.messageToObject(message);
    }

    /**
     * Mapper for {@link ServiceModel.Balance.Input} and {@link ProtoServices.BalanceInput}.
     */
    public static final Mappable<ServiceModel.Balance.Input, ProtoServices.BalanceInput> BALANCE_INPUT = new Mappable<>() {
        @Override
        @NonNull
        public Function<ServiceModel.Balance.Input, ProtoServices.BalanceInput> objectToMessage() {
            return input -> {
                ProtoServices.BalanceInput.Builder builder = ProtoServices.BalanceInput.newBuilder();
                builder.setTotalSalesAmount(input.totalSalesAmount().floatValue());
                ProtoServices.ChargingConfig config = CHARGING_CONFIG.objectToMessage(input.chargingConfig());
                builder.setChargingConfig(config);
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoServices.BalanceInput, ServiceModel.Balance.Input> messageToObject() {
            return input -> {
                ServiceModel.Balance.Input.InputBuilder builder = ServiceModel.Balance.Input.builder();
                builder.totalSalesAmount(BigDecimal.valueOf(input.getTotalSalesAmount()));
                if (input.hasChargingConfig()) {
                    ServiceModel.ChargingConfig config = CHARGING_CONFIG.messageToObject(input.getChargingConfig());
                    builder.chargingConfig(config);
                }
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoServices.BalanceInput objectToMessage(@NonNull ServiceModel.Balance.Input object) {
        return BALANCE_INPUT.objectToMessage(object);
    }

    @NonNull
    public static ServiceModel.Balance.Input messageToObject(@NonNull ProtoServices.BalanceInput message) {
        return BALANCE_INPUT.messageToObject(message);
    }

    /**
     * Mapper for {@link ServiceModel.Balance.Output} and {@link ProtoServices.SalesBalance}.
     */
    public static final Mappable<ServiceModel.Balance.Output, ProtoServices.SalesBalance> BALANCE_OUTPUT = new Mappable<>() {
        @Override
        @NonNull
        public Function<ServiceModel.Balance.Output, ProtoServices.SalesBalance> objectToMessage() {
            return output -> {
                ProtoServices.SalesBalance.Builder builder = ProtoServices.SalesBalance.newBuilder();
                builder.setTotalRevenue(output.totalRevenue().floatValue());
                ProtoServices.ChargedFees fees = CHARGED_FEES.objectToMessage(output.chargedFees());
                builder.setFees(fees);
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoServices.SalesBalance, ServiceModel.Balance.Output> messageToObject() {
            return output -> {
                ServiceModel.Balance.Output.OutputBuilder builder = ServiceModel.Balance.Output.builder();
                builder.totalRevenue(BigDecimal.valueOf(output.getTotalRevenue()));
                if (output.hasFees()) {
                    ServiceModel.ChargedFees fees = CHARGED_FEES.messageToObject(output.getFees());
                    builder.chargedFees(fees);
                }
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoServices.SalesBalance objectToMessage(@NonNull ServiceModel.Balance.Output object) {
        return BALANCE_OUTPUT.objectToMessage(object);
    }

    @NonNull
    public static ServiceModel.Balance.Output messageToObject(@NonNull ProtoServices.SalesBalance message) {
        return BALANCE_OUTPUT.messageToObject(message);
    }

    /**
     * Mapper for {@link ServiceModel.VendorReportInput} and {@link ProtoServices.VendorReportInput}.
     */
    public static final Mappable<ServiceModel.VendorReportInput, ProtoServices.VendorReportInput> VENDOR_REPORT_INPUT = new Mappable<>() {
        @Override
        @NonNull
        public Function<ServiceModel.VendorReportInput, ProtoServices.VendorReportInput> objectToMessage() {
            return input -> {
                ProtoServices.VendorReportInput.Builder builder = ProtoServices.VendorReportInput.newBuilder();
                if (input.vendors() != null) {
                    Arrays.stream(input.vendors()).map(VENDOR_KEY::objectToMessage).forEach(builder::addVendor);
                }
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoServices.VendorReportInput, ServiceModel.VendorReportInput> messageToObject() {
            return input -> {
                ServiceModel.VendorReportInput.VendorReportInputBuilder builder = ServiceModel.VendorReportInput.builder();
                DataModel.Vendor.Key[] convertedVendors = input.getVendorList().stream().map(VENDOR_KEY::messageToObject).toArray(DataModel.Vendor.Key[]::new);
                builder.vendors(convertedVendors);
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoServices.VendorReportInput objectToMessage(@NonNull ServiceModel.VendorReportInput object) {
        return VENDOR_REPORT_INPUT.objectToMessage(object);
    }

    @NonNull
    public static ServiceModel.VendorReportInput messageToObject(@NonNull ProtoServices.VendorReportInput message) {
        return VENDOR_REPORT_INPUT.messageToObject(message);
    }

    /**
     * Mapper for {@link ServiceModel.VendorReportData} and {@link ProtoServices.VendorReportData}.
     */
    public static final Mappable<ServiceModel.VendorReportData, ProtoServices.VendorReportData> VENDOR_REPORT_DATA = new Mappable<>() {
        @Override
        @NonNull
        public Function<ServiceModel.VendorReportData, ProtoServices.VendorReportData> objectToMessage() {
            return reportData -> {
                ProtoServices.VendorReportData.Builder builder = ProtoServices.VendorReportData.newBuilder();
                ProtoModel.Vendor vendorMsg = VENDOR.objectToMessage(reportData.vendor());
                builder.setVendor(vendorMsg);
                ProtoModel.Booth boothMsg = BOOTH.objectToMessage(reportData.booth());
                builder.setBooth(boothMsg);
                List<ProtoModel.PurchaseItem> itemsMsg = reportData.items().stream().map(PURCHASE_ITEM::objectToMessage).toList();
                builder.addAllItems(itemsMsg);
                return builder.build();
            };
        }

        @Override
        @NonNull
        public Function<ProtoServices.VendorReportData, ServiceModel.VendorReportData> messageToObject() {
            return reportData -> {
                ServiceModel.VendorReportData.VendorReportDataBuilder builder = ServiceModel.VendorReportData.builder();
                if (reportData.hasVendor()) {
                    DataModel.Vendor vendor = VENDOR.messageToObject(reportData.getVendor());
                    builder.vendor(vendor);
                }
                if (reportData.hasBooth()) {
                    DataModel.Booth booth = BOOTH.messageToObject(reportData.getBooth());
                    builder.booth(booth);
                }
                List<DataModel.PurchaseItem> convertedItemsList = reportData.getItemsList().stream().map(PURCHASE_ITEM::messageToObject).toList();
                builder.items(convertedItemsList);
                return builder.build();
            };
        }
    };

    @NonNull
    public static ProtoServices.VendorReportData objectToMessage(@NonNull ServiceModel.VendorReportData object) {
        return VENDOR_REPORT_DATA.objectToMessage(object);
    }

    @NonNull
    public static ServiceModel.VendorReportData messageToObject(@NonNull ProtoServices.VendorReportData message) {
        return VENDOR_REPORT_DATA.messageToObject(message);
    }

    /**
     * Mapper for {@link URI} and {@link ProtoCore.URI}.
     */
    public static final Mappable<URI, ProtoCore.URI> URI = new Mappable<>() {
        @Override
        @NonNull
        public Function<URI, ProtoCore.URI> objectToMessage() {
            return uri -> ProtoCore.URI.newBuilder().setResource(uri.toString()).build();
        }

        @Override
        @NonNull
        public Function<ProtoCore.URI, URI> messageToObject() {
            return uriMsg -> java.net.URI.create(uriMsg.getResource());
        }
    };

    @NonNull
    public static ProtoCore.URI objectToMessage(@NonNull URI object) {
        return URI.objectToMessage(object);
    }

    @NonNull
    public static URI messageToObject(@NonNull ProtoCore.URI message) {
        return URI.messageToObject(message);
    }

    /**
     * Interface for implementations of mapping between object and protobuf message.
     *
     * @param <T>
     * @param <M>
     */
    public interface Mappable<T, M extends Message> {

        @NonNull
        Function<T, M> objectToMessage();

        @NonNull
        Function<M, T> messageToObject();

        @NonNull
        default M objectToMessage(@NonNull T input) {
            return objectToMessage().apply(input);
        }

        @NonNull
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

}
