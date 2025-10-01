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
    public static final Mapper<DataModel.Booth.Key, ProtoModel.BoothKey> EVENT_KEY = new Mapper<>() {
        @Override
        public Function<DataModel.Booth.Key, ProtoModel.BoothKey> objectToMessage() {
            return key -> ProtoModel.BoothKey.newBuilder()
                    .setBoothId(key.boothId())
                    .build();
        }

        @Override
        public Function<ProtoModel.BoothKey, DataModel.Booth.Key> messageToObject() {
            return key -> DataModel.Booth.Key.builder()
                    .boothId(key.getBoothId())
                    .build();
        }
    };

    /**
     * Mapper for {@link DataModel.Booth} and {@link ProtoModel.Booth}.
     */
    public static final Mapper<DataModel.Booth, ProtoModel.Booth> EVENT = new Mapper<>() {
        @Override
        public Function<DataModel.Booth, ProtoModel.Booth> objectToMessage() {
            return booth -> {
                ProtoModel.Booth.Builder builder = ProtoModel.Booth.newBuilder();
                if (booth.key() != null) {
                    ProtoModel.BoothKey key = EVENT_KEY.objectToMessage(booth.key());
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
        public Function<ProtoModel.Booth, DataModel.Booth> messageToObject() {
            return booth -> {
                DataModel.Booth.BoothBuilder builder = DataModel.Booth.builder();
                if (booth.hasKey()) {
                    DataModel.Booth.Key key = EVENT_KEY.messageToObject(booth.getKey());
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

    /**
     * Mapper for {@link DataModel.Purchase.Key} and {@link ProtoModel.PurchaseKey}.
     */
    public static final Mapper<DataModel.Purchase.Key, ProtoModel.PurchaseKey> PURCHASE_KEY = new Mapper<>() {
        @Override
        public Function<DataModel.Purchase.Key, ProtoModel.PurchaseKey> objectToMessage() {
            return key -> {
                ProtoModel.PurchaseKey.Builder builder = ProtoModel.PurchaseKey.newBuilder();
                if (key.booth() != null) {
                    ProtoModel.BoothKey booth = EVENT_KEY.objectToMessage(key.booth());
                    builder.setBooth(booth);
                }
                builder.setPurchaseId(key.purchaseId());
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.PurchaseKey, DataModel.Purchase.Key> messageToObject() {
            return key -> {
                DataModel.Purchase.Key.KeyBuilder builder = DataModel.Purchase.Key.builder();
                if (key.hasBooth()) {
                    DataModel.Booth.Key booth = EVENT_KEY.messageToObject(key.getBooth());
                    builder.booth(booth);
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
                if (key.booth() != null) {
                    ProtoModel.BoothKey eventKey = EVENT_KEY.objectToMessage(key.booth());
                    builder.setBooth(eventKey);
                }
                builder.setVendorId(key.vendorId());
                return builder.build();
            };
        }

        @Override
        public Function<ProtoModel.VendorKey, DataModel.Vendor.Key> messageToObject() {
            return key -> {
                DataModel.Vendor.Key.KeyBuilder builder = DataModel.Vendor.Key.builder();
                if (key.hasBooth()) {
                    DataModel.Booth.Key booth = EVENT_KEY.messageToObject(key.getBooth());
                    builder.booth(booth);
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
     * Mapper for {@link ServiceModel.Checkout} and {@link ProtoServices.CheckoutInput}.
     */
    public static final Mapper<ServiceModel.Checkout, ProtoServices.CheckoutInput> CHECKOUT = new Mapper<>() {
        @Override
        public Function<ServiceModel.Checkout, ProtoServices.CheckoutInput> objectToMessage() {
            return checkout -> {
                ProtoServices.CheckoutInput.Builder builder = ProtoServices.CheckoutInput.newBuilder();
                ProtoModel.BoothKey boothKey = EVENT_KEY.objectToMessage(checkout.booth());
                builder.setBooth(boothKey);
                checkout.items().stream().map(PURCHASE_ITEM::objectToMessage).forEach(builder::addItems);
                builder.setPrintReceipt(checkout.printReceipt());
                return builder.build();
            };
        }

        @Override
        public Function<ProtoServices.CheckoutInput, ServiceModel.Checkout> messageToObject() {
            return checkout -> {
                ServiceModel.Checkout.CheckoutBuilder builder = ServiceModel.Checkout.builder();
                if (checkout.hasBooth()) {
                    DataModel.Booth.Key boothKey = EVENT_KEY.messageToObject(checkout.getBooth());
                    builder.booth(boothKey);
                }
                List<DataModel.PurchaseItem> convertedItemsList = checkout.getItemsList().stream().map(PURCHASE_ITEM::messageToObject).toList();
                builder.items(convertedItemsList);
                builder.printReceipt(checkout.getPrintReceipt());
                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link ServiceModel.ChargedFees} and {@link ProtoServices.ChargedFees}.
     */
    public static final Mapper<ServiceModel.ChargedFees, ProtoServices.ChargedFees> CHARGED_FEES = new Mapper<>() {
        @Override
        public Function<ServiceModel.ChargedFees, ProtoServices.ChargedFees> objectToMessage() {
            return fees -> {
                ProtoServices.ChargedFees.Builder builder = ProtoServices.ChargedFees.newBuilder();
                builder.setParticipationFee(fees.participationFee().floatValue());
                builder.setSalesFee(fees.salesFee().floatValue());
                return builder.build();
            };
        }

        @Override
        public Function<ProtoServices.ChargedFees, ServiceModel.ChargedFees> messageToObject() {
            return fees -> {
                ServiceModel.ChargedFees.ChargedFeesBuilder builder = ServiceModel.ChargedFees.builder();
                builder.participationFee(BigDecimal.valueOf(fees.getParticipationFee()));
                builder.salesFee(BigDecimal.valueOf(fees.getSalesFee()));
                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link ServiceModel.ChargingConfig} and {@link ProtoServices.ChargingConfig}.
     */
    public static final Mapper<ServiceModel.ChargingConfig, ProtoServices.ChargingConfig> CHARGING_CONFIG = new Mapper<>() {
        @Override
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

    /**
     * Mapper for {@link ServiceModel.Balance.Input} and {@link ProtoServices.BalanceInput}.
     */
    public static final Mapper<ServiceModel.Balance.Input, ProtoServices.BalanceInput> BALANCE_INPUT = new Mapper<>() {
        @Override
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

    /**
     * Mapper for {@link ServiceModel.Balance.Output} and {@link ProtoServices.SalesBalance}.
     */
    public static final Mapper<ServiceModel.Balance.Output, ProtoServices.SalesBalance> BALANCE_OUTPUT = new Mapper<>() {
        @Override
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

    /**
     * Mapper for {@link ServiceModel.VendorReportInput} and {@link ProtoServices.VendorReportInput}.
     */
    public static final Mapper<ServiceModel.VendorReportInput, ProtoServices.VendorReportInput> VENDOR_REPORT_INPUT = new Mapper<>() {
        @Override
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
        public Function<ProtoServices.VendorReportInput, ServiceModel.VendorReportInput> messageToObject() {
            return input -> {
                ServiceModel.VendorReportInput.VendorReportInputBuilder builder = ServiceModel.VendorReportInput.builder();
                DataModel.Vendor.Key[] convertedVendors = input.getVendorList().stream().map(VENDOR_KEY::messageToObject).toArray(DataModel.Vendor.Key[]::new);
                builder.vendors(convertedVendors);
                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link ServiceModel.VendorReportData} and {@link ProtoServices.VendorReportData}.
     */
    public static final Mapper<ServiceModel.VendorReportData, ProtoServices.VendorReportData> VENDOR_REPORT_DATA = new Mapper<>() {
        @Override
        public Function<ServiceModel.VendorReportData, ProtoServices.VendorReportData> objectToMessage() {
            return reportData -> {
                ProtoServices.VendorReportData.Builder builder = ProtoServices.VendorReportData.newBuilder();
                ProtoModel.Vendor vendorMsg = VENDOR.objectToMessage(reportData.vendor());
                builder.setVendor(vendorMsg);
                ProtoModel.Booth boothMsg = EVENT.objectToMessage(reportData.booth());
                builder.setBooth(boothMsg);
                List<ProtoModel.PurchaseItem> itemsMsg = reportData.items().stream().map(PURCHASE_ITEM::objectToMessage).toList();
                builder.addAllItems(itemsMsg);
                return builder.build();
            };
        }

        @Override
        public Function<ProtoServices.VendorReportData, ServiceModel.VendorReportData> messageToObject() {
            return reportData -> {
                ServiceModel.VendorReportData.VendorReportDataBuilder builder = ServiceModel.VendorReportData.builder();
                if (reportData.hasVendor()) {
                    DataModel.Vendor vendor = VENDOR.messageToObject(reportData.getVendor());
                    builder.vendor(vendor);
                }
                if (reportData.hasBooth()) {
                    DataModel.Booth booth = EVENT.messageToObject(reportData.getBooth());
                    builder.booth(booth);
                }
                List<DataModel.PurchaseItem> convertedItemsList = reportData.getItemsList().stream().map(PURCHASE_ITEM::messageToObject).toList();
                builder.items(convertedItemsList);
                return builder.build();
            };
        }
    };

    /**
     * Mapper for {@link URI} and {@link ProtoCore.URI}.
     */
    public static final Mapper<URI, ProtoCore.URI> URI = new Mapper<>() {
        @Override
        public Function<URI, ProtoCore.URI> objectToMessage() {
            return uri -> ProtoCore.URI.newBuilder().setResource(uri.toString()).build();
        }

        @Override
        public Function<ProtoCore.URI, URI> messageToObject() {
            return uriMsg -> java.net.URI.create(uriMsg.getResource());
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

}
