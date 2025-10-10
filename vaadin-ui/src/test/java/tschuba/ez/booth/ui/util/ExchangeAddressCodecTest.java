/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.nio.ByteBuffer;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test class for {@link tschuba.ez.booth.ui.util.AddressCodec.Exchange}
 */
class ExchangeAddressCodecTest {
    private static Stream<Arguments> provideValidCodecPairs() {
        return Stream.of(
                arguments("192.168.178.64:8080", "QJ+Q"),
                arguments("192.168.178.84:65535", "VH//"),
                arguments("127.0.0.1", ":fwAAAQ=="),
                arguments("172.0.0.1", ":AAAB"),
                arguments("192.168.0.1", ":AAE="),
                arguments("192.168.178.255", ":/w=="),
                arguments("0.0.0.0", ":AAAAAA=="),
                arguments("255.255.255.255", "://///w=="),
                arguments("192.168.178.76:9090", "TKOC"));
    }

    private static Stream<String> provideValidUrls() {
        return provideValidCodecPairs().map(arguments -> (String) arguments.get()[0]);
    }

    @ParameterizedTest(name = "with input ''{0}'' expecting output ''{1}''")
    @MethodSource("provideValidCodecPairs")
    void testEncodeUriWithValidPairs(String input, String expectedOutput) {
        String actualOutput = AddressCodec.Exchange.encode(input);
        assertThat(actualOutput).isEqualTo(expectedOutput);
    }

    @ParameterizedTest(name = "with input ''{0}''")
    @MethodSource("provideValidUrls")
    void testEncodeAndDecode(String input) {
        assertThatNoException()
                .isThrownBy(
                        () -> {
                            String encodingResult = AddressCodec.Exchange.encode(input);
                            assertThatNoException()
                                    .isThrownBy(
                                            () -> {
                                                String decodingResult =
                                                        AddressCodec.Exchange.decode(
                                                                encodingResult);
                                                assertThat(decodingResult)
                                                        .isNotNull()
                                                        .isEqualTo(input);
                                            });
                        });
    }

    @ParameterizedTest(name = "port = {0}")
    @ValueSource(ints = {0, 8080, 65535})
    void testEncodeAndDecodePort(int port) {
        assertThatNoException()
                .isThrownBy(
                        () -> {
                            ByteBuffer buffer = ByteBuffer.allocate(2);
                            AddressCodec.Exchange.PortCodec codec =
                                    AddressCodec.Exchange.PortCodec.of(buffer);
                            codec.encode(port);
                            buffer.position(0);
                            int decodedPort = codec.decode();
                            assertThat(decodedPort).isEqualTo(port);
                        });
    }
}
