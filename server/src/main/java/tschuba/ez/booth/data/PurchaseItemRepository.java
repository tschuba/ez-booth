/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.data;

import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tschuba.ez.booth.model.EntityModel;

/**
 * Repository for {@link EntityModel.PurchaseItem}.
 */
@Repository
public interface PurchaseItemRepository
        extends JpaRepository<EntityModel.PurchaseItem, EntityModel.PurchaseItem.Key> {

    @Query(
            "SELECT p FROM PurchaseItem p WHERE p.vendorId = :vendor.key.vendorId and"
                    + " p.key.purchase.booth = :vendor.key.booth")
    Stream<EntityModel.PurchaseItem> findPurchaseItemsByVendor(EntityModel.Vendor.Key vendor);
}
