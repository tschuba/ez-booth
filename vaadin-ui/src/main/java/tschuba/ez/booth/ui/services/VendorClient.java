/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.ProtoModel;
import tschuba.ez.booth.proto.VendorServiceGrpc;
import tschuba.ez.booth.services.VendorService;

@Service
public class VendorClient implements VendorService {

    private final VendorServiceGrpc.VendorServiceBlockingStub client;

    @Autowired
    public VendorClient(@NonNull VendorServiceGrpc.VendorServiceBlockingStub client) {
        this.client = client;
    }

    @Override
    public @NonNull Stream<DataModel.Vendor> findByBooth(DataModel.Booth.Key booth) {
        Iterator<ProtoModel.Vendor> vendors =
                client.getVendorsByBooth(ProtoMapper.objectToMessage(booth));
        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(vendors, Spliterator.ORDERED), false)
                .map(ProtoMapper::messageToObject);
    }

    @Override
    public @NonNull Optional<DataModel.Vendor> findById(@NonNull DataModel.Vendor.Key key) {
        ProtoModel.Vendor vendor = client.getVendorByKey(ProtoMapper.objectToMessage(key));
        return Optional.ofNullable(vendor).map(ProtoMapper::messageToObject);
    }

    @Override
    public void save(DataModel.@NonNull Vendor vendor) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(DataModel.Vendor.@NonNull Key key) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
