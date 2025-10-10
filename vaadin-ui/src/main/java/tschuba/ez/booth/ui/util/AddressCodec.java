/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tschuba.ez.booth.Try;

/**
 * Utility class for encoding and decoding network addresses.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressCodec {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Exchange {

        private static final Logger LOGGER = LoggerFactory.getLogger(Exchange.class);

        private static final char PREFIX_NO_PORT = ':';

        /**
         * Encodes a URI to a string. The URI must be an HTTP or HTTPS URL with an IPv4 address as the host.
         *
         * @param address the address to encode
         * @return the encoded string
         */
        public static String encode(String address) {
            Matcher validator = Patterns.dataExchange().address().wholeInput().matcher(address);
            if (!validator.find()) {
                throw new IllegalArgumentException(
                        "Only gRPC address supported! Given URL is not valid: %s"
                                .formatted(address));
            }

            int portDelimiterIdx = address.indexOf(':');
            boolean hasPort = portDelimiterIdx > 0;
            String host = (hasPort) ? address.substring(0, portDelimiterIdx) : address;
            byte[] encodedIp = IPv4.encode(host);
            ByteBuffer buffer =
                    ByteBuffer.allocate(encodedIp.length + ((hasPort) ? 2 : 0)).put(encodedIp);
            if (hasPort) {
                int port = Integer.parseUnsignedInt(address.substring(portDelimiterIdx + 1));
                PortCodec.of(buffer).encode(port);
            }

            String base64Address = Base64.getEncoder().encodeToString(buffer.array());
            return ((hasPort) ? "" : "" + PREFIX_NO_PORT) + base64Address;
        }

        /**
         * Decodes a URL encoded via {@link #encode(String)}. The input must start with either '@' or '#'.
         *
         * @param input URL encoded via {@link #encode(String)}.
         * @return decoded URL
         */
        public static String decode(@NonNull String input) {
            boolean hasPort = input.charAt(0) != PREFIX_NO_PORT;
            String base64Authority = input.substring((hasPort) ? 0 : 1);
            ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(base64Authority));

            byte[] encodedAddressBytes = new byte[buffer.capacity() - ((hasPort) ? 2 : 0)];
            buffer.get(encodedAddressBytes);
            String host = IPv4.decodeToString(encodedAddressBytes);
            int port = (hasPort) ? PortCodec.of(buffer).decode() : -1;
            LOGGER.debug("Input {} decoded to: host = {}, port = {}", input, host, port);
            return host + ((hasPort) ? (":" + port) : "");
        }

        /**
         * Tries to decode a URL encoded via {@link #encode(String)}.
         *
         * @param input URL encoded via {@link #encode(String)}.
         * @return a {@link Try} containing the decoded URL or an error if decoding fails.
         */
        public static Try<String> tryDecode(@NonNull String input) {
            try {
                LOGGER.debug("Trying to decode input: {}", input);
                return Try.success(decode(input));
            } catch (Exception ex) {
                LOGGER.debug("Failed to decode input: {}", input, ex);
                return Try.fail(ex);
            }
        }

        /**
         * Encodes a port number to a byte. The port number must be in the range of 0 to 65535.
         */
        public static class PortCodec {
            private final ByteBuffer buffer;

            /**
             * Creates a new Port instance with the given buffer.
             *
             * @param buffer the buffer to use for encoding and decoding the port number
             * @throws IllegalArgumentException if the buffer is less than 2 bytes long
             */
            private PortCodec(@NonNull ByteBuffer buffer) {
                if (buffer.capacity() < 2) {
                    throw new IllegalArgumentException("Buffer must be at least 2 bytes long!");
                }
                this.buffer = buffer;
            }

            public static PortCodec of(@NonNull ByteBuffer buffer) {
                return new PortCodec(buffer);
            }

            public void encode(int port) {
                short portAsShort = (short) (port + Short.MIN_VALUE);
                buffer.putShort(portAsShort);
            }

            public int decode() {
                short port = buffer.getShort(buffer.capacity() - 2);
                return ((int) port) - Short.MIN_VALUE;
            }
        }
    }

    /**
     * Utility class for encoding and decoding IPv4 addresses in dot-decimal notation.
     * */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IPv4 {

        private static final Logger LOGGER = LoggerFactory.getLogger(IPv4.class);

        /**
         * @param ip IPv4 address in dot-decimal notation, e.g. 127.0.0.1,
         * @return the encoded IPv4 address
         * @throws IllegalArgumentException if given byteValue is no valid IPv4 address
         */
        public static byte[] encode(String ip) {
            if (!Patterns.ipAddress().v4().wholeInput().matcher(ip).find()) {
                throw new IllegalArgumentException(
                        "Given byteValue is no valid IPv4 address: %s".formatted(ip));
            }

            ByteBuffer buffer = ByteBuffer.allocate(4);
            Arrays.stream(ip.split("\\."))
                    .mapToInt(Integer::parseUnsignedInt)
                    .mapToObj(Bytes::fromUnsignedInt)
                    .map(Bytes::byteValue)
                    .forEach(buffer::put);
            byte[] encoded = encode(buffer.array());
            LOGGER.debug("IP '{}' encoded to {}", ip, Arrays.toString(encoded));
            return encoded;
        }

        /**
         * @param addressBytes bytes of the IPv4 address to encode
         * @return bytes of encoded address
         * @throws IllegalArgumentException if address bytes has not exact length of 4
         */
        public static byte[] encode(byte[] addressBytes) {
            if (addressBytes.length != 4) {
                throw new IllegalArgumentException("Expecting addressBytes to have length 4!");
            }
            int firstOctet = Byte.toUnsignedInt(addressBytes[0]);
            int secondOctet = Byte.toUnsignedInt(addressBytes[1]);

            ByteBuffer buffer = ByteBuffer.allocate(4); // Maximum 5 bytes needed

            if (firstOctet == 172) {
                buffer =
                        ByteBuffer.allocate(3)
                                .put(addressBytes, 1, 3); // Store last 3 bytes (24 bits)
            } else if (firstOctet == 192 && secondOctet == 168) {
                int thirdOctet = Byte.toUnsignedInt(addressBytes[2]);
                if (thirdOctet == 178) {
                    buffer = ByteBuffer.allocate(1).put(addressBytes[3]);
                } else {
                    buffer =
                            ByteBuffer.allocate(2)
                                    .put(addressBytes, 2, 2); // Store last 2 bytes (16 bits)}
                }
            } else {
                buffer.put(addressBytes); // Store all 4 bytes (32 bits)
            }

            return buffer.array();
        }

        public static byte[] decode(byte[] encoded) {
            ByteBuffer buffer = ByteBuffer.wrap(encoded);
            byte[] address = new byte[4];

            if (encoded.length == 1) { // 192.168.178.x
                address[0] = (byte) 192;
                address[1] = (byte) 168;
                address[2] = (byte) 178;
                address[3] = encoded[0];
            } else if (encoded.length == 2) { // 192.168.x.x
                address[0] = (byte) 192;
                address[1] = (byte) 168;
                address[2] = encoded[0];
                address[3] = encoded[1];
            } else if (encoded.length == 3) { // 172.x.x.x
                address[0] = (byte) 172;
                address[1] = encoded[0];
                address[2] = encoded[1];
                address[3] = encoded[2];
            } else {
                buffer.get(address); // Read all 4 bytes
            }

            return address;
        }

        public static String decodeToString(byte[] encoded) {
            byte[] addressBytes = decode(encoded);
            List<String> addressOctets =
                    Arrays.stream(ArrayUtils.toObject(addressBytes))
                            .map(byteValue -> Integer.toString(Byte.toUnsignedInt(byteValue)))
                            .toList();
            return String.join(".", addressOctets);
        }
    }
}
