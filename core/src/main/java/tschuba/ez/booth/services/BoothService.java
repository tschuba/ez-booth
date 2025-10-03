/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

/**
 * Service interface for managing booth events.
 */
public interface BoothService {
    void saveBooth(@NonNull DataModel.Booth booth);

    /**
     * @return a stream of all booths
     */
    @NonNull
    Stream<DataModel.Booth> getAllBooths();

    /**
     * @param booth the booth key
     * @return the booth if found, otherwise an empty optional
     */
    @NonNull
    Optional<DataModel.Booth> getBooth(@NonNull DataModel.Booth.Key booth);

    /**
     * @param booth the booth key
     * @return the updated booth with closed status
     */
    @NonNull
    DataModel.Booth closeBooth(@NonNull DataModel.Booth.Key booth);

    /**
     * @param booth the booth key
     * @return the updated booth with opened status
     */
    @NonNull
    DataModel.Booth openBooth(@NonNull DataModel.Booth.Key booth);

    /**
     * @param booth the booth key to delete
     */
    void deleteBooth(@NonNull DataModel.Booth.Key booth);
}
