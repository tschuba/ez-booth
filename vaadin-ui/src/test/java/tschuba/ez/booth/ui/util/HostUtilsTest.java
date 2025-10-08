/* Licensed under MIT

Copyright (c) 2023-2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.util;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Test class for {@link HostUtils} */
class HostUtilsTest {

    @Test
    void testExternalHostAddress() {
        assertThatNoException().isThrownBy(
                () -> {
                    String externalHostAddress = Server.externalHostAddress();
                    assertThat(externalHostAddress).isNotIn("127.0.0.1", "localhost");
                });
    }

    @Test
    void testExternalHostAddressShouldThrowIfHostIsUnknown() {
        try (MockedStatic<InetAddress> addressMockedStatic = mockStatic(InetAddress.class)) {
            addressMockedStatic.when(InetAddress::getLocalHost).thenThrow(UnknownHostException.class);

            assertThatExceptionOfType(RuntimeException.class).isThrownBy(Server::externalHostAddress).withMessage("Host unknown!");
        }
    }

    @Test
    void testExternalUrlBuilder() {
        withUriBuilder(
                builder -> assertThatNoException().isThrownBy(() -> assertThat(Server.externalUrlBuilder()).isNotNull()));
    }

    @Test
    void testExternalUrl() {
        withUriBuilder(
                builder -> assertThatNoException().isThrownBy(
                        () -> assertThat(Server.externalUrl()).isNotNull().extracting(URI::getHost).isNotIn("127.0.0.1", "localhost")));
    }

    private static void withUriBuilder(Consumer<ServletUriComponentsBuilder> consumer) {
        try (MockedStatic<ServletUriComponentsBuilder> builderStaticMock = Mockito.mockStatic(ServletUriComponentsBuilder.class)) {
            UriComponents componentsMock = mock(UriComponents.class);
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            doAnswer(
                    invocation -> {
                        String host = invocation.getArgument(0);
                        when(componentsMock.toUri()).thenReturn(new URI("http", host, null, null));
                        return builder;
                    }).when(builder).host(any());
            when(builder.build()).thenReturn(componentsMock);

            builderStaticMock.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            consumer.accept(builder);
        }
    }
}
