/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.DataExchangeServiceGrpc;
import tschuba.ez.booth.proto.ProtoServices;

@Service
public class DataExchangeServiceClient implements DataExchangeService {

    private final DataExchangeServiceGrpc.DataExchangeServiceBlockingStub client;

    @Autowired
    public DataExchangeServiceClient(
            @NonNull DataExchangeServiceGrpc.DataExchangeServiceBlockingStub client) {
        this.client = client;
    }

    @Override
    public @NonNull ServiceModel.ExchangeData exportLocalData(DataModel.Booth.Key boothId) {
        ProtoServices.ExchangeData exchangeData =
                client.exportLocalData(ProtoMapper.objectToMessage(boothId));
        return ProtoMapper.messageToObject(exchangeData);
    }

    @Override
    public void importRemoteData(ServiceModel.@NonNull ExchangeData data) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ServiceModel.ExchangeSubscription subscribeForExchange(
            ServiceModel.@NonNull ExchangeReceiver receiver) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
