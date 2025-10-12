/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.model;

import static org.assertj.core.api.Assertions.assertThat;
import static tschuba.ez.booth.model.CoreModelTestData.Messages;
import static tschuba.ez.booth.model.CoreModelTestData.Objects;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ProtoMapper}.
 */
class ProtoMapperTest {

  /**
   * Tests for BoothKey mapping.
   */
  @Nested
  class BoothKeyTests {

    @Test
    void testObjectToMessageAndBackShouldReturnEqualData() {
      assertThat(ProtoMapper.messageToObject(Messages.BOOTH_KEY)).isEqualTo(Objects.BOOTH_KEY);
      assertThat(ProtoMapper.objectToMessage(Objects.BOOTH_KEY)).isEqualTo(Messages.BOOTH_KEY);
    }
  }

  /**
   * Tests for Booth mapping.
   */
  @Nested
  class BoothTests {

    @Test
    void testObjectToMessageAndBackShouldReturnEqualData() {
      assertThat(ProtoMapper.messageToObject(Messages.BOOTH)).isEqualTo(Objects.BOOTH);
      assertThat(ProtoMapper.objectToMessage(Objects.BOOTH)).isEqualTo(Messages.BOOTH);
    }
  }

  /**
   * Tests for VendorKey mapping.
   */
  @Nested
  class VendorKeyTests {

    @Test
    void testObjectToMessageAndBackShouldReturnEqualData() {
      assertThat(ProtoMapper.messageToObject(Messages.VENDOR_KEY)).isEqualTo(Objects.VENDOR_KEY);
      assertThat(ProtoMapper.objectToMessage(Objects.VENDOR_KEY)).isEqualTo(Messages.VENDOR_KEY);
    }
  }

  /**
   * Tests for Vendor mapping.
   */
  @Nested
  class VendorTests {

    @Test
    void testObjectToMessageAndBackShouldReturnEqualData() {
      assertThat(ProtoMapper.messageToObject(Messages.VENDOR)).isEqualTo(Objects.VENDOR);
      assertThat(ProtoMapper.objectToMessage(Objects.VENDOR)).isEqualTo(Messages.VENDOR);
    }
  }

  /**
   * Tests for PurchaseItem mapping.
   */
  @Nested
  class PurchaseItemTests {

    @Test
    void testObjectToMessageAndBackShouldReturnEqualData() {
      assertThat(ProtoMapper.messageToObject(Messages.PURCHASE_ITEM))
          .isEqualTo(Objects.PURCHASE_ITEM);
      assertThat(ProtoMapper.objectToMessage(Objects.PURCHASE_ITEM))
          .isEqualTo(Messages.PURCHASE_ITEM);
    }
  }

  @Nested
  class PurchaseKeyTests {

    @Test
    void testObjectToMessageAndBackShouldReturnEqualData() {
      assertThat(ProtoMapper.messageToObject(Messages.PURCHASE_KEY))
          .isEqualTo(Objects.PURCHASE_KEY);
      assertThat(ProtoMapper.objectToMessage(Objects.PURCHASE_KEY))
          .isEqualTo(Messages.PURCHASE_KEY);
    }
  }

  @Nested
  class PurchaseTests {

    @Test
    void testObjectToMessageAndBackShouldReturnEqualData() {
      assertThat(ProtoMapper.messageToObject(Messages.PURCHASE)).isEqualTo(Objects.PURCHASE);
      assertThat(ProtoMapper.objectToMessage(Objects.PURCHASE)).isEqualTo(Messages.PURCHASE);
    }
  }
}
