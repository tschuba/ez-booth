/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.DataExchangeServiceGrpc;
import tschuba.ez.booth.proto.ProtoServices;

/**
 * gRPC service for data exchange operations.
 */
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
}
