/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.data;

import java.util.stream.Stream;
import lombok.NonNull;
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
            "SELECT p FROM PurchaseItem p WHERE p.vendorId = :vendorId AND p.key.purchase.booth ="
                    + " :booth")
    @NonNull
    Stream<EntityModel.PurchaseItem> findAllByVendor(
            @NonNull String vendorId, @NonNull EntityModel.Booth.Key booth);

    @NonNull
    default Stream<EntityModel.PurchaseItem> findAllByVendor(
            @NonNull EntityModel.Vendor.Key vendor) {
        return findAllByVendor(vendor.getVendorId(), vendor.getBooth());
    }

    @Query("SELECT p FROM PurchaseItem p WHERE p.key.purchase.booth = :booth")
    @NonNull
    Stream<EntityModel.PurchaseItem> findAllByBooth(@NonNull EntityModel.Booth.Key booth);
}
