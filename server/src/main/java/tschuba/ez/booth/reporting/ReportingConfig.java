/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.reporting;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ReportingConfig {

    private static final String STATIC_LOCATIONS = "spring.web.resources.static-locations";
    private static final String STATIC_LOCATIONS_SEPARATOR = ",";
    private static final String FILE_PREFIX = "file:";
    private static final String REPORTS_PATH = "reports";
    private static final String SETTINGS_PREFIX = "tschuba.ez.booth.reporting";

    private Path targetBasePath;

    private final Environment environment;

    @Autowired
    public ReportingConfig(@NonNull Environment environment) {
        this.environment = environment;

        String targetLocation =
                staticContentLocations(environment)
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new ReportingException(
                                                "No static content locations configured!"));
        targetBasePath = Path.of(targetLocation, REPORTS_PATH);
    }

    /**
     * Returns the base path for report generation within the static content locations.
     *
     * @return the base path for report generation
     */
    public Path targetBasePath() {
        return targetBasePath;
    }

    public Path htmlOutputPath(String fileName) {
        return targetBasePath().resolve("html").resolve(fileName);
    }

    /**
     * Returns the maximum number of threads to use for report generation.
     * This value is configurable via the application properties.
     *
     * @return the maximum number of threads for report generation
     */
    public int threadLimit() {
        return environment.getProperty(SETTINGS_PREFIX + ".thread-limit", Integer.class, 10);
    }

    /**
     * Returns the timeout duration for report generation tasks.
     * This value is configurable via the application properties.
     *
     * @return the timeout duration for report generation
     */
    public Duration timeout() {
        return environment.getProperty(
                SETTINGS_PREFIX + ".timeout", Duration.class, Duration.ofSeconds(120));
    }

    /**
     * Returns a stream of static content locations that are file-based.
     *
     * @param environment the Spring environment to read properties from
     * @return a stream of file-based static content locations
     */
    public static Stream<String> staticContentLocations(Environment environment) {
        String[] contentLocations =
                Optional.ofNullable(environment.getProperty(STATIC_LOCATIONS))
                        .map(
                                locations ->
                                        locations.split(Pattern.quote(STATIC_LOCATIONS_SEPARATOR)))
                        .orElse(new String[0]);
        return Arrays.stream(contentLocations).filter(location -> location.startsWith(FILE_PREFIX));
    }
}
