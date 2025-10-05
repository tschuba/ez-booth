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
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.DataExchangeServiceGrpc;
import tschuba.ez.booth.proto.ProtoModel;
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
    public void exportLocalData(
            ProtoModel.BoothKey booth,
            StreamObserver<ProtoServices.ExchangeData> responseObserver) {
        try {
            ServiceModel.ExchangeData data =
                    localService.exportLocalData(ProtoMapper.messageToObject(booth));
            responseObserver.onNext(ProtoMapper.objectToMessage(data));
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error exporting local data", ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public StreamObserver<ProtoServices.ExchangeData> importRemoteData(
            StreamObserver<Empty> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ProtoServices.ExchangeData input) {
                ServiceModel.ExchangeData data = ProtoMapper.messageToObject(input);
                localService.importRemoteData(data);
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.error("Error importing remote data", throwable);
            }

            @Override
            public void onCompleted() {
                LOGGER.info("Completed importing remote data");
            }
        };
    }

    @Override
    public void subscribeForExchange(
            ProtoServices.ExchangeReceiver request,
            StreamObserver<ProtoServices.ExchangeSubscription> responseObserver) {
        try {
            ServiceModel.ExchangeReceiver receiver = ProtoMapper.messageToObject(request);
            ServiceModel.ExchangeSubscription subscription =
                    localService.subscribeForExchange(receiver);
            responseObserver.onNext(ProtoMapper.objectToMessage(subscription));
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error subscribing for data exchange", ex);
            responseObserver.onError(ex);
        }
    }
}
