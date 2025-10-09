/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import tschuba.ez.booth.proto.*;

@Configuration
public class GrpcClientConfig {

    private static final String CHANNEL = "default-channel";

    @Bean
    BoothServiceGrpc.BoothServiceBlockingStub boothClient(GrpcChannelFactory channels) {
        return BoothServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
    }

    @Bean
    PurchaseServiceGrpc.PurchaseServiceBlockingStub purchaseClient(GrpcChannelFactory channels) {
        return PurchaseServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
    }

    @Bean
    VendorServiceGrpc.VendorServiceBlockingStub vendorClient(GrpcChannelFactory channels) {
        return VendorServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
    }

    @Bean
    ChargingServiceGrpc.ChargingServiceBlockingStub chargingClient(GrpcChannelFactory channels) {
        return ChargingServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
    }

    @Bean
    ReportingServiceGrpc.ReportingServiceBlockingStub reportingClient(GrpcChannelFactory channels) {
        return ReportingServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
    }

    @Bean
    DataExchangeServiceGrpc.DataExchangeServiceBlockingStub dataExchangeClient(
            GrpcChannelFactory channels) {
        return DataExchangeServiceGrpc.newBlockingStub(channels.createChannel(CHANNEL));
    }
}
