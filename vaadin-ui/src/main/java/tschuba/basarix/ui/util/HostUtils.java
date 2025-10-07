/* Licensed under MIT

Copyright (c) 2023-2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/** Utility class for host-related operations. */
public class HostUtils {
    /**
     * @return the external host address of the server
     * @throws RuntimeException if the host is unknown
     * @see Inet4Address#getLocalHost()
     * @see InetAddress#getHostAddress()
     */
    public static String externalHostAddress() throws RuntimeException {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            throw new RuntimeException("Host unknown!", ex);
        }
    }

    /**
     * Creates a URI builder for the external servlet URL.
     *
     * @return the external servlet URL builder
     * @throws RuntimeException if the host is unknown
     */
    public static UriComponentsBuilder externalUrlBuilder() throws RuntimeException {
        String hostAddress = externalHostAddress();
        return ServletUriComponentsBuilder.fromCurrentContextPath().host(hostAddress);
    }

    /**
     * Creates a URI for the external servlet URL.
     *
     * @return the external servlet URL
     * @throws RuntimeException if the host is unknown
     */
    public static URI externalUrl() throws RuntimeException {
        return externalUrlBuilder().build().toUri();
    }
}
