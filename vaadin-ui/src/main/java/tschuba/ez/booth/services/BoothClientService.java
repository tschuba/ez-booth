/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import com.google.protobuf.Empty;
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
import tschuba.ez.booth.proto.BoothServiceGrpc;
import tschuba.ez.booth.proto.ProtoModel;

@Service
public class BoothClientService implements BoothService {

    private final BoothServiceGrpc.BoothServiceBlockingStub client;

    @Autowired
    public BoothClientService(@NonNull BoothServiceGrpc.BoothServiceBlockingStub client) {
        this.client = client;
    }

    @Override
    public void saveBooth(DataModel.@NonNull Booth booth) {
        Empty _ = client.saveBooth(ProtoMapper.objectToMessage(booth));
    }

    @Override
    public @NonNull Stream<DataModel.Booth> findAll() {
        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                client.getAllBooths(Empty.getDefaultInstance()),
                                Spliterator.ORDERED),
                        false)
                .map(ProtoMapper::messageToObject);
    }

    @Override
    public @NonNull Optional<DataModel.Booth> findById(DataModel.Booth.@NonNull Key booth) {
        return Optional.ofNullable(client.getBooth(ProtoMapper.objectToMessage(booth)))
                .map(ProtoMapper::messageToObject);
    }

    @NotNull
    @Override
    public @NonNull DataModel.Booth close(DataModel.Booth.@NonNull Key booth) {
        ProtoModel.Booth msg = client.closeBooth(ProtoMapper.objectToMessage(booth));
        return ProtoMapper.messageToObject(msg);
    }

    @NotNull
    @Override
    public @NonNull DataModel.Booth open(DataModel.Booth.@NonNull Key key) {
        ProtoModel.Booth booth = client.openBooth(ProtoMapper.objectToMessage(key));
        return ProtoMapper.messageToObject(booth);
    }

    @Override
    public void delete(DataModel.Booth.@NonNull Key booth) {
        Empty _ = client.deleteBooth(ProtoMapper.objectToMessage(booth));
    }
}
