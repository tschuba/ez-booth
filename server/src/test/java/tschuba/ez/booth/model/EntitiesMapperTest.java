/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tschuba.ez.booth.model.ModelTestData.Entities;
import tschuba.ez.booth.model.ModelTestData.Objects;

/**
 * Unit tests for {@link EntitiesMapper}.
 */
class EntitiesMapperTest {

  /**
   * Tests for BoothKey mapping.
   */
  @Nested
  class BoothKeyTests {
    @Test
    void testObjectToEntityAndBackShouldReturnEqualData() {
      assertThat(EntitiesMapper.objectToEntity(Objects.BOOTH_KEY)).isEqualTo(Entities.BOOTH_KEY);
      assertThat(EntitiesMapper.entityToObject(Entities.BOOTH_KEY)).isEqualTo(Objects.BOOTH_KEY);
    }
  }

  /**
   * Tests for Booth mapping.
   */
  @Nested
  class BoothTests {
    @Test
    void testObjectToEntityAndBackShouldReturnEqualData() {
      assertThat(EntitiesMapper.objectToEntity(Objects.BOOTH)).isEqualTo(Entities.BOOTH);
      assertThat(EntitiesMapper.entityToObject(Entities.BOOTH)).isEqualTo(Objects.BOOTH);
    }
  }

  /**
   * Tests for VendorKey mapping.
   */
  @Nested
  class VendorKeyTests {
    @Test
    void testObjectToEntityAndBackShouldReturnEqualData() {
      assertThat(EntitiesMapper.objectToEntity(Objects.VENDOR_KEY)).isEqualTo(Entities.VENDOR_KEY);
      assertThat(EntitiesMapper.entityToObject(Entities.VENDOR_KEY)).isEqualTo(Objects.VENDOR_KEY);
    }
  }

  /**
   * Tests for Vendor mapping.
   */
  @Nested
  class VendorTests {
    @Test
    void testObjectToEntityAndBackShouldReturnEqualData() {
      assertThat(EntitiesMapper.objectToEntity(Objects.VENDOR)).isEqualTo(Entities.VENDOR);
      assertThat(EntitiesMapper.entityToObject(Entities.VENDOR)).isEqualTo(Objects.VENDOR);
    }
  }

  /**
   * Tests for PurchaseItemKey mapping.
   */
  @Nested
  class PurchaseItemKeyTests {
    @Test
    void testObjectToEntityAndBackShouldReturnEqualData() {
      assertThat(EntitiesMapper.objectToEntity(Objects.PURCHASE_ITEM_KEY))
          .isEqualTo(Entities.PURCHASE_ITEM_KEY);
      assertThat(EntitiesMapper.entityToObject(Entities.PURCHASE_ITEM_KEY))
          .isEqualTo(Objects.PURCHASE_ITEM_KEY);
    }
  }

  /**
   * Tests for PurchaseItem mapping.
   */
  @Nested
  class PurchaseItemTests {
    @Test
    void testObjectToEntityAndBackShouldReturnEqualData() {
      assertThat(EntitiesMapper.objectToEntity(Objects.PURCHASE_ITEM))
          .isEqualTo(Entities.PURCHASE_ITEM);
      assertThat(EntitiesMapper.entityToObject(Entities.PURCHASE_ITEM))
          .isEqualTo(Objects.PURCHASE_ITEM);
    }
  }

  /**
   * Tests for PurchaseKey mapping.
   */
  @Nested
  class PurchaseKeyTests {
    @Test
    void testObjectToEntityAndBackShouldReturnEqualData() {
      assertThat(EntitiesMapper.objectToEntity(Objects.PURCHASE_KEY))
          .isEqualTo(Entities.PURCHASE_KEY);
      assertThat(EntitiesMapper.entityToObject(Entities.PURCHASE_KEY))
          .isEqualTo(Objects.PURCHASE_KEY);
    }
  }

  /**
   * Tests for Purchase mapping.
   */
  @Nested
  class PurchaseTests {
    @Test
    void testObjectToEntityAndBackShouldReturnEqualData() {
      assertThat(EntitiesMapper.objectToEntity(Objects.PURCHASE)).isEqualTo(Entities.PURCHASE);
      assertThat(EntitiesMapper.entityToObject(Entities.PURCHASE)).isEqualTo(Objects.PURCHASE);
    }
  }
}
