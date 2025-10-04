/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import jakarta.transaction.Transactional;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.data.PurchaseItemRepository;
import tschuba.ez.booth.data.RecordNotFoundException;
import tschuba.ez.booth.data.VendorRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.EntityModel;
import tschuba.ez.booth.reporting.ReportingConfig;
import tschuba.ez.booth.reporting.ReportingException;
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

    private final ReportingConfig config;

    public ReportingLocalService(
            @NonNull BoothRepository booths,
            @NonNull VendorRepository vendors,
            @NonNull PurchaseItemRepository purchaseItems,
            @NonNull ChargingService chargingService,
            @NonNull ReportingConfig config) {
        this.booths = booths;
        this.vendors = vendors;
        this.purchaseItems = purchaseItems;
        this.chargingService = chargingService;
        this.config = config;
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
        ServiceModel.ChargingConfig chargingConfig = ServiceModel.ChargingConfig.of(booth);

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
    public @NonNull URI generateVendorReport(@NonNull DataModel.Vendor.Key... vendors) {
        Optional<URI> result =
                CompletableFuture.supplyAsync(() -> tryGenerateVendorReport(vendors))
                        .orTimeout(config.timeout().toMillis(), TimeUnit.MILLISECONDS)
                        .join();
        return result.orElseThrow(() -> new ReportingException("Report generation failed!"));
    }

    /**
     * Tries to generate a vendor report and returns the URI of the generated report file if successful.
     *
     * @param vendors the vendor keys for which to generate the report
     * @return an Optional containing the URI of the generated report file, or empty if generation failed
     */
    Optional<URI> tryGenerateVendorReport(@NonNull DataModel.Vendor.Key... vendors) {
        String reportFileName = VendorReportTemplate.reportFileName();
        Path reportOutputPath = config.htmlOutputPath(reportFileName);

        try {
            Path outputDir = reportOutputPath.getParent();
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            if (!Files.exists(reportOutputPath)) {
                Files.createFile(reportOutputPath);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to create report output file: {}", reportOutputPath, ex);
            throw new ReportingException("Failed to create report output file", ex);
        }

        List<ServiceModel.VendorReportData> vendorReportData =
                Arrays.stream(vendors).parallel().map(this::createVendorReportData).toList();

        VendorReportTemplate template = new VendorReportTemplate();
        try (FileWriter fileWriter = new FileWriter(reportOutputPath.toFile())) {
            template.render(fileWriter, vendorReportData);
        } catch (IOException ex) {
            LOGGER.error("Failed to write report to file {}!", reportOutputPath, ex);
            return Optional.empty();
        } catch (ReportingException ex) {
            LOGGER.error("Failed to render report!", ex);
            return Optional.empty();
        }

        return Optional.of(reportOutputPath.toUri());
    }
}
