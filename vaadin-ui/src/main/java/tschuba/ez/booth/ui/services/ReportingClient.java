/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import java.net.URI;
import java.util.Arrays;
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
  private final ReportingServiceGrpc.ReportingServiceBlockingStub client;

  @Autowired
  public ReportingClient(@NonNull ReportingServiceGrpc.ReportingServiceBlockingStub client) {
    this.client = client;
  }

  @Override
  public @NonNull ServiceModel.VendorReportData createVendorReportData(
      DataModel.Vendor.@NonNull Key vendor) {
    ProtoServices.VendorReportData vendorReportData =
        client.createVendorReportData(ProtoMapper.objectToMessage(vendor));
    return ProtoMapper.messageToObject(vendorReportData);
  }

  @Override
  public @NonNull URI generateVendorReport(@NotNull DataModel.Vendor.@NonNull Key... vendors) {
    ProtoServices.VendorReportInput.Builder inputBuilder =
        ProtoServices.VendorReportInput.newBuilder();
    Arrays.stream(vendors).map(ProtoMapper::objectToMessage).forEach(inputBuilder::addVendor);

    ProtoCore.URI reportUri = client.generateVendorReport(inputBuilder.build());
    return ProtoMapper.messageToObject(reportUri);
  }
}
