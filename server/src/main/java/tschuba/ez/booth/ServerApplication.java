/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "tschuba.ez.booth")
@ConfigurationPropertiesScan(basePackages = "tschuba.ez.booth")
@EnableConfigurationProperties
public class ServerApplication {
    static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
