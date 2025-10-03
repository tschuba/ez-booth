/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Local service implementation for booth events.
 * This class provides the core business logic for managing booth events.
 */
@Service
public class BoothLocalService implements BoothService {

    private final BoothRepository booths;

    @Autowired
    public BoothLocalService(@NonNull BoothRepository booths) {
        this.booths = booths;
    }

    @Override
    public void save(DataModel.@NonNull Booth booth) {
        booths.save(EntitiesMapper.objectToEntity(booth));
    }

    @Override
    public @NonNull Stream<DataModel.Booth> getAll() {
        return booths.findAll().stream().map(EntitiesMapper::entityToObject);
    }

    @Override
    public @NonNull Optional<DataModel.Booth> get(@NonNull DataModel.Booth.Key booth) {
        return booths.findById(EntitiesMapper.objectToEntity(booth))
                .map(EntitiesMapper::entityToObject);
    }

    @Override
    public @NonNull DataModel.Booth close(@NonNull DataModel.Booth.Key booth) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull DataModel.Booth open(@NonNull DataModel.Booth.Key booth) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(@NonNull DataModel.Booth.Key booth) {
        booths.deleteById(EntitiesMapper.objectToEntity(booth));
    }
}
