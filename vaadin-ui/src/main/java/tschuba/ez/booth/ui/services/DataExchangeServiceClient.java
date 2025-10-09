/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.DataExchangeServiceGrpc;
import tschuba.ez.booth.proto.ProtoServices;
import tschuba.ez.booth.services.DataExchangeService;
import tschuba.ez.booth.services.ServiceModel;

@Service
public class DataExchangeServiceClient implements DataExchangeService {

    private final DataExchangeServiceGrpc.DataExchangeServiceBlockingStub client;

    @Autowired
    public DataExchangeServiceClient(
            @NonNull DataExchangeServiceGrpc.DataExchangeServiceBlockingStub client) {
        this.client = client;
    }

    @Override
    public @NonNull ServiceModel.ExchangeData exchangeData(ServiceModel.ExchangeData dataReceived) {
        ProtoServices.ExchangeData exportData = client.syncData(ProtoMapper.objectToMessage(dataReceived));
        return ProtoMapper.messageToObject(exportData);
    }
}
