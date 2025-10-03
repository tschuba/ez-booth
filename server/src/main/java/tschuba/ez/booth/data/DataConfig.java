/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.data;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class for setting up JPA repositories and entity scanning.
 * This class enables JPA repositories and specifies the packages to scan for JPA entities.
 */
@Configuration
@EnableJpaRepositories
@SuppressWarnings("unused")
public class DataConfig {}
