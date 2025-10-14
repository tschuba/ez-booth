/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import tschuba.ez.booth.proto.BoothServiceGrpc;
import tschuba.ez.booth.proto.ChargingServiceGrpc;
import tschuba.ez.booth.proto.DataExchangeServiceGrpc;
import tschuba.ez.booth.proto.PurchaseServiceGrpc;
import tschuba.ez.booth.proto.ReportingServiceGrpc;
import tschuba.ez.booth.proto.VendorServiceGrpc;

@Configuration
public class GrpcClientConfig {

  private static final String CHANNEL = "default-channel";

  @Bean
  BoothServiceGrpc.BoothServiceBlockingStub boothServiceBlocking(GrpcChannelFactory channels) {
    return BoothServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
  }

  @Bean
  PurchaseServiceGrpc.PurchaseServiceBlockingStub purchaseServiceBlocking(
      GrpcChannelFactory channels) {
    return PurchaseServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
  }

  @Bean
  VendorServiceGrpc.VendorServiceBlockingStub vendorServiceBlocking(GrpcChannelFactory channels) {
    return VendorServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
  }

  @Bean
  ChargingServiceGrpc.ChargingServiceBlockingStub chargingServiceBlocking(
      GrpcChannelFactory channels) {
    return ChargingServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
  }

  @Bean
  ReportingServiceGrpc.ReportingServiceBlockingStub reportingServiceBlocking(
      GrpcChannelFactory channels) {
    return ReportingServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
  }

  @Bean
  DataExchangeServiceGrpc.DataExchangeServiceBlockingStub dataExchangeServiceBlocking(
      GrpcChannelFactory channels) {
    return DataExchangeServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
  }
}
