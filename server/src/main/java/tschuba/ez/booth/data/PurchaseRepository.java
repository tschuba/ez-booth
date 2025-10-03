/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tschuba.ez.booth.model.EntityModel;

import java.util.stream.Stream;

/**
 * Repository for {@link EntityModel.Purchase}.
 */
@Repository
public interface PurchaseRepository
        extends JpaRepository<EntityModel.Purchase, EntityModel.Purchase.Key> {

    @Query("SELECT p FROM Purchase p WHERE p.key.booth = :booth")
    Stream<EntityModel.Purchase> findPurchasesByBooth(EntityModel.Booth.Key booth);
}
