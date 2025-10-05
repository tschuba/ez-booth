/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import java.net.URI;
import lombok.NonNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.ProtoCore;
import tschuba.ez.booth.proto.ProtoModel;
import tschuba.ez.booth.proto.ProtoServices;
import tschuba.ez.booth.proto.ReportingServiceGrpc;

/**
 * gRPC service implementation for reporting.
 * This class handles incoming gRPC requests and delegates the operations to the local service layer.
 */
@GrpcService
public class ReportingGrpcService extends ReportingServiceGrpc.ReportingServiceImplBase {

    private static final Logger LOGGER =
            org.slf4j.LoggerFactory.getLogger(ReportingGrpcService.class);

    private final ReportingLocalService localService;

    @Autowired
    ReportingGrpcService(@NonNull ReportingLocalService localService) {
        this.localService = localService;
    }

    @Override
    public void createVendorReportData(
            ProtoModel.VendorKey request,
            StreamObserver<ProtoServices.VendorReportData> responseObserver) {
        try {
            DataModel.Vendor.Key vendor = ProtoMapper.messageToObject(request);
            ServiceModel.VendorReportData reportData = localService.createVendorReportData(vendor);

            ProtoServices.VendorReportData reportDataMsg = ProtoMapper.objectToMessage(reportData);
            responseObserver.onNext(reportDataMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error creating vendor report data for vendor: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    @Transactional
    public void generateVendorReport(
            ProtoServices.VendorReportInput request,
            StreamObserver<ProtoCore.URI> responseObserver) {
        try {
            DataModel.Vendor.Key[] vendors =
                    request.getVendorList().stream()
                            .map(ProtoMapper::messageToObject)
                            .toArray(DataModel.Vendor.Key[]::new);
            URI uri = localService.generateVendorReport(vendors);

            ProtoCore.URI uriMsg = ProtoMapper.objectToMessage(uri);
            responseObserver.onNext(uriMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error generating vendor report for vendors: {}", request, ex);
            responseObserver.onError(ex);
        }
    }
}
