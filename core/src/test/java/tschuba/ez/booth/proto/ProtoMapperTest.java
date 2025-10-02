package tschuba.ez.booth.proto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tschuba.ez.booth.ModelTestData.Messages;
import static tschuba.ez.booth.ModelTestData.Objects;
import static tschuba.ez.booth.proto.ProtoMapper.*;

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
        void testObjectToMessageShouldReturnNonNull() {
            assertThat(BOOTH_KEY.objectToMessage()).isNotNull();
        }

        @Test
        void testMessageToObjectShouldReturnNonNull() {
            assertThat(BOOTH_KEY.messageToObject()).isNotNull();
        }

        @Test
        void testObjectToMessageAndBackShouldReturnEqualData() {
            assertThat(BOOTH_KEY.messageToObject(Messages.BOOTH_KEY)).isEqualTo(Objects.BOOTH_KEY);
            assertThat(BOOTH_KEY.objectToMessage(Objects.BOOTH_KEY)).isEqualTo(Messages.BOOTH_KEY);
        }
    }

    /**
     * Tests for Booth mapping.
     */
    @Nested
    class BoothTests {
        @Test
        void testObjectToMessageShouldReturnNonNull() {
            assertThat(BOOTH.objectToMessage(Objects.BOOTH)).isNotNull();
        }

        @Test
        void testMessageToObjectShouldReturnNonNull() {
            assertThat(BOOTH.messageToObject(Messages.BOOTH)).isNotNull();
        }

        @Test
        void testObjectToMessageAndBackShouldReturnEqualData() {
            assertThat(BOOTH.messageToObject(Messages.BOOTH)).isEqualTo(Objects.BOOTH);
            assertThat(BOOTH.objectToMessage(Objects.BOOTH)).isEqualTo(Messages.BOOTH);
        }
    }

    /**
     * Tests for VendorKey mapping.
     */
    @Nested
    class VendorKeyTests {
        @Test
        void testObjectToMessageShouldReturnNonNull() {
            assertThat(VENDOR_KEY.objectToMessage(Objects.VENDOR_KEY)).isNotNull();
        }

        @Test
        void testMessageToObjectShouldReturnNonNull() {
            assertThat(VENDOR_KEY.messageToObject(Messages.VENDOR_KEY)).isNotNull();
        }

        @Test
        void testObjectToMessageAndBackShouldReturnEqualData() {
            assertThat(VENDOR_KEY.messageToObject(Messages.VENDOR_KEY)).isEqualTo(Objects.VENDOR_KEY);
            assertThat(VENDOR_KEY.objectToMessage(Objects.VENDOR_KEY)).isEqualTo(Messages.VENDOR_KEY);
        }
    }

    /**
     * Tests for Vendor mapping.
     */
    @Nested
    class VendorTests {
        @Test
        void testObjectToMessageShouldReturnNonNull() {
            assertThat(VENDOR.objectToMessage(Objects.VENDOR)).isNotNull();
        }

        @Test
        void testMessageToObjectShouldReturnNonNull() {
            assertThat(VENDOR.messageToObject(Messages.VENDOR)).isNotNull();
        }

        @Test
        void testObjectToMessageAndBackShouldReturnEqualData() {
            assertThat(VENDOR.messageToObject(Messages.VENDOR)).isEqualTo(Objects.VENDOR);
            assertThat(VENDOR.objectToMessage(Objects.VENDOR)).isEqualTo(Messages.VENDOR);
        }
    }

    /**
     * Tests for PurchaseItem mapping.
     */
    @Nested
    class PurchaseItemTests {
        @Test
        void testObjectToMessageShouldReturnNonNull() {
            assertThat(PURCHASE_ITEM.objectToMessage(Objects.PURCHASE_ITEM)).isNotNull();
        }

        @Test
        void testMessageToObjectShouldReturnNonNull() {
            assertThat(PURCHASE_ITEM.messageToObject(Messages.PURCHASE_ITEM)).isNotNull();
        }

        @Test
        void testObjectToMessageAndBackShouldReturnEqualData() {
            assertThat(PURCHASE_ITEM.messageToObject(Messages.PURCHASE_ITEM)).isEqualTo(Objects.PURCHASE_ITEM);
            assertThat(PURCHASE_ITEM.objectToMessage(Objects.PURCHASE_ITEM)).isEqualTo(Messages.PURCHASE_ITEM);
        }
    }
}
