/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.BoothServiceGrpc;
import tschuba.ez.booth.proto.ProtoModel;

/**
 * gRPC service implementation for booth events.
 * This class handles incoming gRPC requests and delegates the operations to the local service layer.
 */
@GrpcService
public class BoothGrpcService extends BoothServiceGrpc.BoothServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoothGrpcService.class);

    private static final Function<StreamObserver<ProtoModel.Booth>, Consumer<DataModel.Booth>>
            BOOTH_ITEM_RECEIVER_FACTORY =
                    responseObserver ->
                            booth -> {
                                ProtoModel.Booth boothMsg = ProtoMapper.objectToMessage(booth);
                                LOGGER.debug("Returning booth: {}", boothMsg);
                                responseObserver.onNext(boothMsg);
                            };

    private final BoothLocalService localService;

    @Autowired
    BoothGrpcService(@NonNull BoothLocalService localService) {
        this.localService = localService;
    }

    @Override
    public void saveBooth(ProtoModel.Booth request, StreamObserver<Empty> responseObserver) {
        try {
            DataModel.Booth booth = ProtoMapper.messageToObject(request);
            localService.saveBooth(booth);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }

    @Override
    public void getAllBooths(Empty request, StreamObserver<ProtoModel.Booth> responseObserver) {
        Consumer<DataModel.Booth> boothItemReceiver =
                BOOTH_ITEM_RECEIVER_FACTORY.apply(responseObserver);
        try {
            Stream<DataModel.Booth> allBooths = localService.findAll();
            allBooths.forEach(boothItemReceiver);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }

    @Override
    public void getBooth(
            ProtoModel.BoothKey request, StreamObserver<ProtoModel.Booth> responseObserver) {
        Consumer<DataModel.Booth> boothItemReceiver =
                BOOTH_ITEM_RECEIVER_FACTORY.apply(responseObserver);
        try {
            DataModel.Booth.Key key = ProtoMapper.messageToObject(request);
            localService.findById(key).ifPresent(boothItemReceiver);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }

    @Override
    public void closeBooth(
            ProtoModel.BoothKey request, StreamObserver<ProtoModel.Booth> responseObserver) {
        try {
            DataModel.Booth.Key key = ProtoMapper.messageToObject(request);
            DataModel.Booth booth = localService.close(key);
            ProtoModel.Booth boothMsg = ProtoMapper.objectToMessage(booth);
            responseObserver.onNext(boothMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }

    @Override
    public void openBooth(
            ProtoModel.BoothKey request, StreamObserver<ProtoModel.Booth> responseObserver) {
        try {
            DataModel.Booth.Key key = ProtoMapper.messageToObject(request);
            DataModel.Booth booth = localService.open(key);
            ProtoModel.Booth boothMsg = ProtoMapper.objectToMessage(booth);
            responseObserver.onNext(boothMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }

    @Override
    public void deleteBooth(ProtoModel.BoothKey request, StreamObserver<Empty> responseObserver) {
        try {
            DataModel.Booth.Key key = ProtoMapper.messageToObject(request);
            localService.delete(key);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }
}
