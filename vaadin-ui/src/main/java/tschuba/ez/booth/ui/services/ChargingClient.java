/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.ChargingServiceGrpc;
import tschuba.ez.booth.proto.ProtoServices;
import tschuba.ez.booth.services.ChargingService;
import tschuba.ez.booth.services.ServiceModel;

@Service
public class ChargingClient implements ChargingService {
    private final ChargingServiceGrpc.ChargingServiceBlockingStub client;

    @Autowired
    public ChargingClient(@NonNull ChargingServiceGrpc.ChargingServiceBlockingStub client) {
        this.client = client;
    }

    @Override
    public @NonNull ServiceModel.ChargedFees calculateFees(DataModel.Vendor.@NonNull Key vendor) {
        ProtoServices.ChargedFees chargedFees =
                client.calculateFees(ProtoMapper.objectToMessage(vendor));
        return ProtoMapper.messageToObject(chargedFees);
    }

    @Override
    public @NonNull ServiceModel.Balance.Output calculateBalance(
            ServiceModel.Balance.@NonNull Input input) {
        ProtoServices.SalesBalance balance =
                client.calculateBalance(ProtoMapper.objectToMessage(input));
        return ProtoMapper.messageToObject(balance);
    }
}
