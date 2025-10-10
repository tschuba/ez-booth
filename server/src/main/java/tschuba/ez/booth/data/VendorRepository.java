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
 * Repository for {@link EntityModel.Vendor}.
 */
@Repository
public interface VendorRepository
    extends JpaRepository<EntityModel.Vendor, EntityModel.Vendor.Key> {

  @Query("SELECT v FROM Vendor v WHERE v.key.booth.boothId = :boothId")
  List<EntityModel.Vendor> findAllByBoothId(String boothId);

  /**
   * Find all vendors by booth.
   * @param booth the booth
   * @return the list of vendors
   */
  default List<EntityModel.Vendor> findAllByBooth(EntityModel.Booth.Key booth) {
    return findAllByBoothId(booth.getBoothId());
  }
}
