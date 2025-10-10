/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.reporting;

import java.io.IOException;
import java.nio.file.Files;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Initializes static content directories if they do not exist.
 * This listener checks the configured static content locations and ensures
 * that any file-based directories exist, creating them if necessary.
 */
public class ReportingPathsInitializer
    implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportingPathsInitializer.class);

  @Override
  public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
    ConfigurableEnvironment environment = event.getEnvironment();
    Reports.Config.targetBasePath(environment)
        .ifPresent(
            path -> {
              LOGGER.info("Report output path configured: {}", path.toAbsolutePath());
              if (!Files.exists(path)) {
                try {
                  LOGGER.info("Creating static content location: {}", path);
                  Files.createDirectories(path);
                } catch (IOException ex) {
                  LOGGER.error("Failed to create static content location: {}", path, ex);
                }
              }
            });
  }
}
