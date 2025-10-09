/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.ProtoModel;
import tschuba.ez.booth.proto.PurchaseServiceGrpc;
import tschuba.ez.booth.services.PurchaseService;
import tschuba.ez.booth.services.ServiceModel;

@Service
public class PurchaseClientService implements PurchaseService {

    private final PurchaseServiceGrpc.PurchaseServiceBlockingStub client;

    @Autowired
    public PurchaseClientService(@NonNull PurchaseServiceGrpc.PurchaseServiceBlockingStub client) {
        this.client = client;
    }

    @NotNull
    @Override
    public @NonNull DataModel.Purchase checkout(ServiceModel.@NonNull Checkout checkout) {
        ProtoModel.Purchase purchase = client.checkout(ProtoMapper.objectToMessage(checkout));
        return ProtoMapper.messageToObject(purchase);
    }

    @Override
    public @NonNull Optional<DataModel.Purchase> findById(DataModel.Purchase.@NonNull Key key) {
        ProtoModel.Purchase purchase = client.getPurchaseByKey(ProtoMapper.objectToMessage(key));
        return Optional.ofNullable(purchase).map(ProtoMapper::messageToObject);
    }

    @Override
    public @NonNull Stream<DataModel.Purchase> findByBooth(DataModel.Booth.@NonNull Key booth) {
        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                client.getPurchasesByBooth(ProtoMapper.objectToMessage(booth)),
                                Spliterator.ORDERED),
                        false)
                .map(ProtoMapper::messageToObject);
    }
}
