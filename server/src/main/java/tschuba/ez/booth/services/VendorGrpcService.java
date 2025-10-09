/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.ProtoModel;
import tschuba.ez.booth.proto.VendorServiceGrpc;

@GrpcService
public class VendorGrpcService extends VendorServiceGrpc.VendorServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendorGrpcService.class);

    private final VendorLocalService service;

    @Autowired
    public VendorGrpcService(@NonNull VendorLocalService service) {
        this.service = service;
    }

    @Override
    public void saveVendor(ProtoModel.Vendor request, StreamObserver<Empty> responseObserver) {
        try {
            service.save(ProtoMapper.messageToObject(request));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error saving vendor: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void getVendorsByBooth(
            ProtoModel.BoothKey request, StreamObserver<ProtoModel.Vendor> responseObserver) {
        try {
            service.findByBooth(ProtoMapper.messageToObject(request))
                    .map(ProtoMapper::objectToMessage)
                    .forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error retrieving vendors by booth: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void getVendorByKey(
            ProtoModel.VendorKey request, StreamObserver<ProtoModel.Vendor> responseObserver) {
        try {
            service.findById(ProtoMapper.messageToObject(request))
                    .map(ProtoMapper::objectToMessage)
                    .ifPresent(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error retrieving vendor by key: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void deleteVendor(ProtoModel.VendorKey request, StreamObserver<Empty> responseObserver) {
        try {
            service.delete(ProtoMapper.messageToObject(request));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error deleting vendor: {}", request, ex);
            responseObserver.onError(ex);
        }
    }
}
