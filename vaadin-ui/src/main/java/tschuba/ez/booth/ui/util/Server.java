/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletContext;
import com.vaadin.open.Open;
import java.net.*;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Simple server configuration holder.
 */
@Getter
@SuppressWarnings("unused")
public class Server {

  private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

  static final String SERVER_HOST = "server.host";
  static final String SERVER_PORT = "server.port";
  static final String SERVER_SSL_ENABLED = "server.ssl.enabled";
  static final String GRPC_CLIENT_ADDRESS = "grpc.client.booth.address";

  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 8080;

  private final String host;
  private final int port;
  private final boolean sslEnabled;

  private Server(@NonNull String host, int port, boolean sslEnabled) {
    this.host = host;
    this.port = port;
    this.sslEnabled = sslEnabled;
  }

  public static Server parse(@NonNull Environment environment) {
    String hostSetting = environment.getProperty(SERVER_HOST);
    String portSetting = environment.getProperty(SERVER_PORT);
    String sslEnabled = environment.getProperty(SERVER_SSL_ENABLED);
    String host = Optional.ofNullable(hostSetting).orElse(DEFAULT_HOST);
    int port = Optional.ofNullable(portSetting).map(Integer::parseInt).orElse(DEFAULT_PORT);
    return new Server(host, port, Boolean.parseBoolean(sslEnabled));
  }

  public static String externalGrpcAddress(@NonNull Environment environment) {
    String grpcAddress = environment.getProperty(GRPC_CLIENT_ADDRESS);
    if (StringUtils.isBlank(grpcAddress)) {
      return null;
    }
    int portDelimiterIdx = grpcAddress.indexOf(':');
    String externalHost = Server.externalHostAddress();
    return (portDelimiterIdx > 0)
        ? externalHost + grpcAddress.substring(portDelimiterIdx)
        : externalHost;
  }

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

  /**
   * Creates a Serving instance for the given VaadinService.
   *
   * @param service the VaadinService to use
   * @return a Serving instance for the given VaadinService
   */
  public Service with(@NonNull VaadinService service) {
    return new Service(service);
  }

  /**
   * Uses the current VaadinService instance.
   *
   * @return a Serving instance for the current VaadinService
   * @throws IllegalStateException if no current VaadinService is available
   */
  public Service withCurrentService() {
    VaadinService service = VaadinService.getCurrent();
    if (service == null) {
      throw new IllegalStateException("No current VaadinService available!");
    }
    return with(service);
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @Getter
  public class Service {
    private final VaadinService service;

    private VaadinServletContext servletContext() {
      return (VaadinServletContext) service.getContext();
    }

    public String servletUrl() {
      VaadinServletContext servletContext = servletContext();
      String contextPath = servletContext.getContext().getContextPath();
      String protocol = Server.this.isSslEnabled() ? "https" : "http";
      return String.format("%s://localhost:%s%s", protocol, Server.this.getPort(), contextPath);
    }

    public void launchView(Class<? extends Component> view) {
      try {
        if (VaadinService.getCurrent() == null) {
          VaadinService.setCurrent(service);
        }
        String targetPath = NavigateTo.view(view).url();
        String protocol = Server.this.isSslEnabled() ? "https" : "http";
        String contextPath = servletContext().getContext().getContextPath();
        String targetUrl =
            String.format(
                "%s://localhost:%s%s/%s", protocol, Server.this.getPort(), contextPath, targetPath);
        LOGGER.debug("Launching browser at {}", targetUrl);
        Open.open(targetUrl);
      } catch (Exception ex) {
        LOGGER.error("Failed to launch browser on startup!", ex);
        throw new RuntimeException(ex);
      }
    }

    public void launchView(String path) {
      String protocol = Server.this.isSslEnabled() ? "https" : "http";
      String targetUrl =
          String.format("%s://localhost:%s/%s", protocol, Server.this.getPort(), path);
      Open.open(targetUrl);
    }
  }
}
