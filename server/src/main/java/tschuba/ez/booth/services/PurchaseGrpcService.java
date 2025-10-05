/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.ProtoModel;
import tschuba.ez.booth.proto.ProtoServices;
import tschuba.ez.booth.proto.PurchaseServiceGrpc;

/**
 * gRPC service implementation for purchase processing.
 * This class handles incoming gRPC requests and delegates the operations to the local service layer.
 */
@GrpcService
public class PurchaseGrpcService extends PurchaseServiceGrpc.PurchaseServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseGrpcService.class);

    private final PurchaseLocalService localService;

    @Autowired
    PurchaseGrpcService(@NonNull PurchaseLocalService localService) {
        this.localService = localService;
    }

    @Override
    public void checkout(
            ProtoServices.CheckoutInput request,
            StreamObserver<ProtoModel.Purchase> responseObserver) {
        try {
            ServiceModel.Checkout checkout = ProtoMapper.messageToObject(request);
            DataModel.Purchase purchase = localService.checkout(checkout);

            ProtoModel.Purchase purchaseMsg = ProtoMapper.objectToMessage(purchase);
            responseObserver.onNext(purchaseMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }

    @Override
    public void getPurchaseByKey(
            ProtoModel.PurchaseKey request, StreamObserver<ProtoModel.Purchase> responseObserver) {
        try {
            DataModel.Purchase.Key key = ProtoMapper.messageToObject(request);
            Optional<DataModel.Purchase> purchase = localService.getPurchaseByKey(key);
            if (purchase.isPresent()) {
                ProtoModel.Purchase purchaseMsg = ProtoMapper.objectToMessage(purchase.get());
                responseObserver.onNext(purchaseMsg);
            }
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error getting purchase by key: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    @Transactional
    public void getPurchasesByBooth(
            ProtoModel.BoothKey request, StreamObserver<ProtoModel.Purchase> responseObserver) {
        try {
            DataModel.Booth.Key booth = ProtoMapper.messageToObject(request);
            AtomicInteger count = new AtomicInteger(0);
            localService
                    .getPurchasesByBooth(booth)
                    .forEach(
                            purchase -> {
                                ProtoModel.Purchase purchaseMsg =
                                        ProtoMapper.objectToMessage(purchase);
                                responseObserver.onNext(purchaseMsg);
                                count.incrementAndGet();
                            });
            LOGGER.debug("Returned {} purchases for booth {}", count.get(), booth);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error getting purchases by booth: {}", request, ex);
            responseObserver.onError(ex);
        }
    }
}
