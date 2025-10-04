/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Initializes the SQLite database file path if it does not exist.
 * This listener checks the datasource URL for SQLite and ensures
 * that the directory for the database file exists, creating it if necessary.
 */
public class SQLiteInitializer implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteInitializer.class);

    private static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
    private static final String JDBC_SQLITE_SCHEMA = "jdbc:sqlite:";

    @Override
    public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        String dataSourceUrl = environment.getProperty(SPRING_DATASOURCE_URL);
        if (dataSourceUrl == null) {
            LOGGER.info("No datasource URL configured; skipping SQLite initialization.");
        } else if (!dataSourceUrl.startsWith(JDBC_SQLITE_SCHEMA)) {
            LOGGER.info("Datasource URL does not indicate SQLite; skipping SQLite initialization.");
        } else {
            String dbFilePath = dataSourceUrl.substring(JDBC_SQLITE_SCHEMA.length());
            LOGGER.debug("SQLite database file path: {}", dbFilePath);
            Path dbFileDir = Path.of(dbFilePath).getParent();
            if (!Files.exists(dbFileDir)) {
                try {
                    LOGGER.info("Creating directory for SQLite database file: {}", dbFilePath);
                    Files.createDirectories(dbFileDir);
                } catch (IOException ex) {
                    LOGGER.error(
                            "Failed to create directory for SQLite database file: {}",
                            dbFilePath,
                            ex);
                }
            } else {
                LOGGER.debug("Directory for SQLite database file already exists: {}", dbFilePath);
            }
        }
    }
}
