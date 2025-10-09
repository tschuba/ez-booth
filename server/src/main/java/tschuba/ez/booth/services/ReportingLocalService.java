/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import jakarta.transaction.Transactional;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.data.PurchaseItemRepository;
import tschuba.ez.booth.data.RecordNotFoundException;
import tschuba.ez.booth.data.VendorRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;
import tschuba.ez.booth.reporting.Reports;
import tschuba.ez.booth.reporting.VendorReportTemplate;

/**
 * Local service implementation for reporting.
 * This class provides the core business logic for generating reports.
 */
@Service
public class ReportingLocalService implements ReportingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportingLocalService.class);

    private final BoothRepository booths;
    private final VendorRepository vendors;
    private final PurchaseItemRepository purchaseItems;
    private final ChargingService chargingService;

    private final Reports.Config reportingConfig;
    private final ServerProperties serverConfig;

    public ReportingLocalService(
            @NonNull BoothRepository booths,
            @NonNull VendorRepository vendors,
            @NonNull PurchaseItemRepository purchaseItems,
            @NonNull ChargingService chargingService,
            @NonNull Reports.Config reportingConfig,
            @NonNull ServerProperties serverConfig) {
        this.booths = booths;
        this.vendors = vendors;
        this.purchaseItems = purchaseItems;
        this.chargingService = chargingService;
        this.reportingConfig = reportingConfig;
        this.serverConfig = serverConfig;
    }

    @Override
    @Transactional
    public @NonNull ServiceModel.VendorReportData createVendorReportData(
            @NonNull DataModel.Vendor.Key vendorKey) {
        EntityModel.Vendor vendor =
                vendors.findById(EntitiesMapper.objectToEntity(vendorKey))
                        .orElseThrow(
                                (() ->
                                        new RecordNotFoundException(
                                                "Vendor not found: " + vendorKey)));

        List<EntityModel.PurchaseItem> vendorItems =
                purchaseItems
                        .findPurchaseItemsByVendor(EntitiesMapper.objectToEntity(vendorKey))
                        .toList();
        BigDecimal vendorItemsSum =
                vendorItems.stream()
                        .map(EntityModel.PurchaseItem::getPrice)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);

        EntityModel.Booth booth =
                booths.findById(EntitiesMapper.objectToEntity(vendorKey.booth()))
                        .orElseThrow(
                                () ->
                                        new RecordNotFoundException(
                                                "Booth not found: " + vendorKey.booth()));
        ServiceModel.ChargingConfig chargingConfig =
                ServiceModel.ChargingConfig.of(EntitiesMapper.entityToObject(booth));

        ServiceModel.Balance.Input balanceInput =
                ServiceModel.Balance.Input.builder()
                        .chargingConfig(chargingConfig)
                        .totalSalesAmount(vendorItemsSum)
                        .build();
        ServiceModel.Balance.Output balance = chargingService.calculateBalance(balanceInput);
        LOGGER.debug(
                "Balance: {}, {}, {}, Total Amount of Sales: {}",
                vendor,
                chargingConfig,
                balance,
                vendorItemsSum);

        BigDecimal totalRevenue = balance.totalRevenue();
        ServiceModel.ChargedFees chargedFees = balance.chargedFees();

        return ServiceModel.VendorReportData.builder()
                .booth(EntitiesMapper.entityToObject(booth))
                .vendor(EntitiesMapper.entityToObject(vendor))
                .items(vendorItems.stream().map(EntitiesMapper::entityToObject).toList())
                .salesSum(vendorItemsSum)
                .participationFee(chargedFees.participationFee())
                .salesFee(chargedFees.salesFee())
                .totalRevenue(totalRevenue)
                .build();
    }

    @Override
    @Transactional
    public @NonNull URI generateVendorReport(@NonNull DataModel.Vendor.Key... vendors) {
        if (vendors.length < 1) {
            throw new IllegalArgumentException("At least one vendor must be specified!");
        }

        DataModel.Vendor.Key firstVendor = vendors[0];
        String reportFileName = (vendors.length == 1) ? VendorReportTemplate.reportFileName(firstVendor) : VendorReportTemplate.reportFileName(firstVendor.booth());
        Reports.Target reportTarget = Reports.Target.of(reportFileName, reportingConfig);
        Path reportFilePath = reportTarget.absolute();
        Path reportOutputPath = reportFilePath.getParent();

        try {
            if (!Files.exists(reportOutputPath)) {
                Files.createDirectories(reportOutputPath);
            }
            if (!Files.exists(reportFilePath)) {
                Files.createFile(reportFilePath);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to create report output file: {}", reportOutputPath, ex);
            throw new ReportingException("Failed to create report output file", ex);
        }

        List<ServiceModel.VendorReportData> vendorReportData =
                Arrays.stream(vendors).map(this::createVendorReportData).toList();

        VendorReportTemplate template = new VendorReportTemplate();
        try (FileWriter fileWriter = new FileWriter(reportFilePath.toFile())) {
            LOGGER.debug(
                    "Rendering {} vendor report items to {}",
                    vendorReportData.size(),
                    reportFilePath);
            template.render(fileWriter, vendorReportData);
        } catch (IOException ex) {
            LOGGER.error("Failed to write report to file {}!", reportOutputPath, ex);
            throw new ReportingException("Failed to write report to file", ex);
        } catch (ReportingException ex) {
            LOGGER.error("Failed to render report!", ex);
            throw ex;
        }
        return reportUrl(reportTarget);
    }

    URI reportUrl(@NonNull Reports.Target target) {
        URI webPath = reportingConfig.getTargetWebPath();
        if (webPath == null) {
            throw new IllegalStateException("No reporting web path configured!");
        }

        String host =
                Optional.ofNullable(serverConfig.getAddress())
                        .orElseGet(
                                () -> {
                                    try {
                                        return Inet4Address.getLocalHost();
                                    } catch (UnknownHostException ex) {
                                        LOGGER.error("Failed to determine local host address!", ex);
                                        throw new RuntimeException(ex);
                                    }
                                })
                        .getHostAddress();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(webPath).host(host);
        Optional.ofNullable(serverConfig.getPort()).ifPresent(uriBuilder::port);
        Optional.ofNullable(serverConfig.getSsl())
                .ifPresent(
                        ssl -> {
                            if (ssl.isEnabled()) {
                                uriBuilder.scheme("https");
                            } else {
                                uriBuilder.scheme("http");
                            }
                        });

        return uriBuilder.pathSegment(target.relativeFilePath()).build().toUri();
    }
}
