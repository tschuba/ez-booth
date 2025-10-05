/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.reporting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
public class StaticContentInitializer
        implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticContentInitializer.class);

    private static final String STATIC_LOCATIONS = "spring.web.resources.static-locations";
    private static final String STATIC_LOCATIONS_SEPARATOR = ",";
    private static final String FILE_PREFIX = "file:";

    @Override
    public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        List<String> fileLocations = ReportingConfig.staticContentLocations(environment).toList();
        if (fileLocations.isEmpty()) {
            LOGGER.info("No static content locations configured.");
        }

        for (String location : fileLocations) {
            Path locationPath = Path.of(location);
            if (!Files.exists(locationPath)) {
                try {
                    LOGGER.info("Creating static content location: {}", locationPath);
                    Files.createDirectories(locationPath);
                } catch (IOException ex) {
                    LOGGER.error("Failed to create static content location: {}", locationPath, ex);
                }
            }
        }
    }
}
