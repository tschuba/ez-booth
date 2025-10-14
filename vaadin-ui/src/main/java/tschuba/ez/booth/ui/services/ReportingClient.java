/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.ProtoCore;
import tschuba.ez.booth.proto.ProtoServices;
import tschuba.ez.booth.proto.ReportingServiceGrpc;
import tschuba.ez.booth.services.ReportingService;
import tschuba.ez.booth.services.ServiceModel;

@Service
public class ReportingClient implements ReportingService {
  private final ReportingServiceGrpc.ReportingServiceBlockingStub blockingClient;

  @Autowired
  public ReportingClient(
      @NonNull ReportingServiceGrpc.ReportingServiceBlockingStub blockingClient) {
    this.blockingClient = blockingClient;
  }

  @Override
  public @NonNull ServiceModel.VendorReportData createVendorReportData(
      @NonNull DataModel.Vendor.Key vendor) {
    ProtoServices.VendorReportData vendorReportData =
        blockingClient.createVendorReportData(ProtoMapper.objectToMessage(vendor));
    return ProtoMapper.messageToObject(vendorReportData);
  }

  /**
   * Asynchronously creates a vendor report data for the specified vendor.
   * @param vendor the vendor to create the report data for
   * @return a Future containing the vendor report data
   */
  public @NonNull CompletableFuture<ServiceModel.VendorReportData> createVendorReportDataAsync(
      @NonNull DataModel.Vendor.Key vendor) {
    return CompletableFuture.supplyAsync(() -> createVendorReportData(vendor));
  }

  @Override
  public @NonNull URI generateVendorReport(@NotNull DataModel.Vendor.@NonNull Key... vendors) {
    ProtoServices.VendorReportInput.Builder inputBuilder =
        ProtoServices.VendorReportInput.newBuilder();
    Arrays.stream(vendors).map(ProtoMapper::objectToMessage).forEach(inputBuilder::addVendor);

    ProtoCore.URI reportUri = blockingClient.generateVendorReport(inputBuilder.build());
    return ProtoMapper.messageToObject(reportUri);
  }

  /**
   * Asynchronously generates a vendor report for the specified vendors.
   *
   * @param vendors the vendors to include in the report
   * @return a Future containing the URI of the generated report
   */
  public @NonNull CompletableFuture<URI> generateVendorReportAsync(
      @NotNull DataModel.Vendor.@NonNull Key... vendors) {
    return CompletableFuture.supplyAsync(() -> generateVendorReport(vendors));
  }
}
