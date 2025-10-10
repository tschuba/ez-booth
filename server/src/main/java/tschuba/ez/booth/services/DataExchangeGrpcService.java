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
import tschuba.ez.booth.proto.DataExchangeServiceGrpc;
import tschuba.ez.booth.proto.ProtoModel;
import tschuba.ez.booth.proto.ProtoServices;

/**
 * gRPC service for data exchange operations.
 */
@GrpcService
public class DataExchangeGrpcService extends DataExchangeServiceGrpc.DataExchangeServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeGrpcService.class);

    private final DataExchangeLocalService localService;

    @Autowired
    DataExchangeGrpcService(@NonNull DataExchangeLocalService localService) {
        this.localService = localService;
    }

    @Override
    public void syncData(
            ProtoServices.ExchangeData request,
            StreamObserver<ProtoServices.ExchangeData> responseObserver) {
        try {
            ServiceModel.ExchangeData exportData =
                    localService.exchangeData(ProtoMapper.messageToObject(request));
            responseObserver.onNext(ProtoMapper.objectToMessage(exportData));
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error syncing data", ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void exportData(
            ProtoModel.BoothKey request,
            StreamObserver<ProtoServices.ExchangeData> responseObserver) {
        try {
            ServiceModel.ExchangeData exportData =
                    localService.export(ProtoMapper.messageToObject(request));
            responseObserver.onNext(ProtoMapper.objectToMessage(exportData));
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error exporting data", ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void mergeData(
            ProtoServices.ExchangeData request, StreamObserver<Empty> responseObserver) {
        try {
            localService.merge(ProtoMapper.messageToObject(request));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error merging data", ex);
            responseObserver.onError(ex);
        }
    }
}
