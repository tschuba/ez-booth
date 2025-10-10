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
  Stream<DataModel.Booth> findAll();

  /**
   * @param booth the booth key
   * @return the booth if found, otherwise an empty optional
   */
  @NonNull
  Optional<DataModel.Booth> findById(@NonNull DataModel.Booth.Key booth);

  /**
   * @param booth the booth key
   * @return the updated booth with closed status
   */
  @NonNull
  DataModel.Booth close(@NonNull DataModel.Booth.Key booth);

  /**
   * @param booth the booth key
   * @return the updated booth with opened status
   */
  @NonNull
  DataModel.Booth open(@NonNull DataModel.Booth.Key booth);

  /**
   * @param booth the booth key to delete
   */
  void delete(@NonNull DataModel.Booth.Key booth);
}
