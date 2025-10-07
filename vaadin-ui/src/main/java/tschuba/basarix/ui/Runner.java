/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"tschuba.commons", "tschuba.basarix"})
@ConfigurationPropertiesScan(basePackages = {"tschuba.commons", "tschuba.basarix"})
@NoArgsConstructor(access = PRIVATE)
public class Runner {
    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
    }
}
