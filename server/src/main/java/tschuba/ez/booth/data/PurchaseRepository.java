/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tschuba.ez.booth.model.EntityModel;

/**
 * Repository for {@link EntityModel.Purchase}.
 */
@Repository
public interface PurchaseRepository
        extends JpaRepository<EntityModel.Purchase, EntityModel.Purchase.Key> {

    @Query("SELECT p FROM Purchase p WHERE p.key.booth.boothId = :boothId")
    List<EntityModel.Purchase> findPurchasesByBooth(String boothId);
}
