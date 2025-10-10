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
 * Service interface for managing purchases.
 */
public interface PurchaseService {

  /**
   * @param checkout the checkout information
   * @return the created purchase
   */
  @NonNull
  DataModel.Purchase checkout(@NonNull ServiceModel.Checkout checkout);

  /**
   * @param purchase the purchase key
   * @return the purchase if found, otherwise an empty optional
   */
  @NonNull
  Optional<DataModel.Purchase> findById(@NonNull DataModel.Purchase.Key purchase);

  /**
   * @param booth the booth key
   * @return a stream of all purchases for the given booth
   */
  @NonNull
  Stream<DataModel.Purchase> findByBooth(@NonNull DataModel.Booth.Key booth);
}
