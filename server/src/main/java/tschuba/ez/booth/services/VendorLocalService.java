/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.data.VendorRepository;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;

@Service
public class VendorLocalService implements VendorService {

    private final VendorRepository repository;

    @Autowired
    public VendorLocalService(@NonNull VendorRepository repository) {
        this.repository = repository;
    }

    @Override
    public @NonNull Stream<DataModel.Vendor> findByBooth(DataModel.Booth.Key booth) {
        return repository.findAllByBooth(EntitiesMapper.objectToEntity(booth)).stream()
                .map(EntitiesMapper::entityToObject);
    }

    @Override
    public @NonNull Optional<DataModel.Vendor> findById(DataModel.Vendor.@NonNull Key key) {
        return repository
                .findById(EntitiesMapper.objectToEntity(key))
                .map(EntitiesMapper::entityToObject);
    }

    @Override
    public void save(DataModel.@NonNull Vendor vendor) {
        repository.save(EntitiesMapper.objectToEntity(vendor));
    }
}
