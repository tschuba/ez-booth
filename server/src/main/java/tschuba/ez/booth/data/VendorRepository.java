/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tschuba.ez.booth.model.EntityModel;

/**
 * Repository for {@link EntityModel.Vendor}.
 */
@Repository
public interface VendorRepository
        extends JpaRepository<EntityModel.Vendor, EntityModel.Vendor.Key> {}
