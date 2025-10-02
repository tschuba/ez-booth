package tschuba.ez.booth;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tschuba.ez.booth.ModelTestData.Entities;
import tschuba.ez.booth.ModelTestData.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static tschuba.ez.booth.EntitiesMapper.*;


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
        void testObjectToEntityShouldReturnNonNull() {
            assertThat(BOOTH_KEY.objectToEntity()).isNotNull();
        }

        @Test
        void testEntityToObjectShouldReturnNonNull() {
            assertThat(BOOTH_KEY.objectToEntity()).isNotNull();
        }

        @Test
        void testObjectToEntityAndBackShouldReturnEqualData() {
            assertThat(BOOTH_KEY.objectToEntity(Objects.BOOTH_KEY)).isEqualTo(Entities.BOOTH_KEY);
            assertThat(BOOTH_KEY.entityToObject(Entities.BOOTH_KEY)).isEqualTo(Objects.BOOTH_KEY);
        }
    }

    /**
     * Tests for Booth mapping.
     */
    @Nested
    class BoothTests {
        @Test
        void testObjectToEntityShouldReturnNonNull() {
            assertThat(BOOTH.objectToEntity()).isNotNull();
        }

        @Test
        void testEntityToObjectShouldReturnNonNull() {
            assertThat(BOOTH.entityToObject()).isNotNull();
        }

        @Test
        void testObjectToEntityAndBackShouldReturnEqualData() {
            assertThat(BOOTH.objectToEntity(Objects.BOOTH)).isEqualTo(Entities.BOOTH);
            assertThat(BOOTH.entityToObject(Entities.BOOTH)).isEqualTo(Objects.BOOTH);
        }
    }

    /**
     * Tests for VendorKey mapping.
     */
    @Nested
    class VendorKeyTests {
        @Test
        void testObjectToEntityShouldReturnNonNull() {
            assertThat(VENDOR_KEY.objectToEntity()).isNotNull();
        }

        @Test
        void testEntityToObjectShouldReturnNonNull() {
            assertThat(VENDOR_KEY.entityToObject()).isNotNull();
        }

        @Test
        void testObjectToEntityAndBackShouldReturnEqualData() {
            assertThat(VENDOR_KEY.objectToEntity(Objects.VENDOR_KEY)).isEqualTo(Entities.VENDOR_KEY);
            assertThat(VENDOR_KEY.entityToObject(Entities.VENDOR_KEY)).isEqualTo(Objects.VENDOR_KEY);
        }
    }

    /**
     * Tests for Vendor mapping.
     */
    @Nested
    class VendorTests {
        @Test
        void testObjectToEntityShouldReturnNonNull() {
            assertThat(VENDOR.objectToEntity()).isNotNull();
        }

        @Test
        void testEntityToObjectShouldReturnNonNull() {
            assertThat(VENDOR.entityToObject()).isNotNull();
        }

        @Test
        void testObjectToEntityAndBackShouldReturnEqualData() {
            assertThat(VENDOR.objectToEntity(Objects.VENDOR)).isEqualTo(Entities.VENDOR);
            assertThat(VENDOR.entityToObject(Entities.VENDOR)).isEqualTo(Objects.VENDOR);
        }
    }

    /**
     * Tests for PurchaseItemKey mapping.
     */
    @Nested
    class PurchaseItemKeyTests {
        @Test
        void testObjectToEntityShouldReturnNonNull() {
            assertThat(PURCHASE_ITEM_KEY.objectToEntity()).isNotNull();
        }

        @Test
        void testEntityToObjectShouldReturnNonNull() {
            assertThat(PURCHASE_ITEM_KEY.entityToObject()).isNotNull();
        }

        @Test
        void testObjectToEntityAndBackShouldReturnEqualData() {
            assertThat(PURCHASE_ITEM_KEY.objectToEntity(Objects.PURCHASE_ITEM_KEY)).isEqualTo(Entities.PURCHASE_ITEM_KEY);
            assertThat(PURCHASE_ITEM_KEY.entityToObject(Entities.PURCHASE_ITEM_KEY)).isEqualTo(Objects.PURCHASE_ITEM_KEY);
        }
    }

    /**
     * Tests for PurchaseItem mapping.
     */
    @Nested
    class PurchaseItemTests {
        @Test
        void testObjectToEntityShouldReturnNonNull() {
            assertThat(PURCHASE_ITEM.objectToEntity()).isNotNull();
        }

        @Test
        void testEntityToObjectShouldReturnNonNull() {
            assertThat(PURCHASE_ITEM.entityToObject()).isNotNull();
        }

        @Test
        void testObjectToEntityAndBackShouldReturnEqualData() {
            assertThat(PURCHASE_ITEM.objectToEntity(Objects.PURCHASE_ITEM)).isEqualTo(Entities.PURCHASE_ITEM);
            assertThat(PURCHASE_ITEM.entityToObject(Entities.PURCHASE_ITEM)).isEqualTo(Objects.PURCHASE_ITEM);
        }
    }

    /**
     * Tests for PurchaseKey mapping.
     */
    @Nested
    class PurchaseKeyTests {
        @Test
        void testObjectToEntityShouldReturnNonNull() {
            assertThat(PURCHASE_KEY.objectToEntity()).isNotNull();
        }

        @Test
        void testEntityToObjectShouldReturnNonNull() {
            assertThat(PURCHASE_KEY.entityToObject()).isNotNull();
        }

        @Test
        void testObjectToEntityAndBackShouldReturnEqualData() {
            assertThat(PURCHASE_KEY.objectToEntity(Objects.PURCHASE_KEY)).isEqualTo(Entities.PURCHASE_KEY);
            assertThat(PURCHASE_KEY.entityToObject(Entities.PURCHASE_KEY)).isEqualTo(Objects.PURCHASE_KEY);
        }
    }

    /**
     * Tests for Purchase mapping.
     */
    @Nested
    class PurchaseTests {
        @Test
        void testObjectToEntityShouldReturnNonNull() {
            assertThat(PURCHASE.objectToEntity()).isNotNull();
        }

        @Test
        void testEntityToObjectShouldReturnNonNull() {
            assertThat(PURCHASE.entityToObject()).isNotNull();
        }

        @Test
        void testObjectToEntityAndBackShouldReturnEqualData() {
            assertThat(PURCHASE.objectToEntity(Objects.PURCHASE)).isEqualTo(Entities.PURCHASE);
            assertThat(PURCHASE.entityToObject(Entities.PURCHASE)).isEqualTo(Objects.PURCHASE);
        }
    }
}
