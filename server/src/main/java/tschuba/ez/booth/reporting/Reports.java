/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.reporting;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Utility class for report generation.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Reports {

  @Builder
  public record Target(@NonNull Path outputPath, @NonNull String relativeFilePath) {

    /**
     * Generates a report target with a filename based on the given base name and the current date.
     * @param fileName base name of the report file
     * @param config reporting configuration
     * @return the report target
     */
    public static Target of(@NonNull String fileName, @NonNull Reports.Config config) {
      return Target.builder()
          .outputPath(config.getTargetBasePath())
          .relativeFilePath("%s.html".formatted(fileName))
          .build();
    }

    /**
     * @return the absolute path to the report file
     */
    public Path absolute() {
      return outputPath.resolve(relativeFilePath);
    }
  }

  /**
   * Configuration properties for reporting.
   */
  @Configuration
  @Getter
  public static class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private static final String REPORTING_NAMESPACE = "tschuba.ez.booth.reporting";
    public static final String REPORTING_OUTPUT_PATH = REPORTING_NAMESPACE + ".outputPath";
    public static final String REPORTING_WEB_PATH = REPORTING_NAMESPACE + ".webPath";

    private final Path targetBasePath;
    private final URI targetWebPath;

    @Autowired
    public Config(@NonNull Environment environment) {
      this.targetBasePath = targetBasePath(environment).orElseThrow();
      this.targetWebPath =
          Optional.ofNullable(environment.getProperty(REPORTING_WEB_PATH))
              .map(URI::create)
              .orElseThrow();
      LOGGER.debug("Report target base path: {}", targetBasePath);
    }

    public static Optional<Path> targetBasePath(@NonNull Environment environment) {
      return Optional.ofNullable(environment.getProperty(REPORTING_OUTPUT_PATH)).map(Path::of);
    }
  }
}
