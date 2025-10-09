/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class I18NConfig {

    static final String MAPPING_FILE = "i18n/i18n.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(I18NConfig.class);

    @Bean
    public I18N i18n() {
        ClassLoader classLoader = getClass().getClassLoader();
        I18N.Config config = I18N.Config.parse(MAPPING_FILE, classLoader);
        LOGGER.debug("i18n config: {}", config);
        return new I18N(config);
    }
}
