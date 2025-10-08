/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import java.net.URI;
import java.nio.file.Path;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import tschuba.ez.booth.reporting.ReportingConfig;

@Component
@Lazy
public class ReportViewHelper {
    private final Environment environment;
    private final ReportingConfig reportingConfig;

    @Autowired
    public ReportViewHelper(
            @NonNull Environment environment, @NonNull ReportingConfig reportingConfig) {
        this.environment = environment;
        this.reportingConfig = reportingConfig;
    }

    public URI reportUrl(@NonNull URI reportFile) {
        String staticPathPattern = environment.getProperty("spring.mvc.static-path-pattern");
        if (staticPathPattern == null) {
            throw new IllegalStateException("No static path pattern configured!");
        }

        String staticPath = staticPathPattern;
        if (staticPathPattern.indexOf('*') >= 0) {
            staticPath = staticPathPattern.substring(0, staticPathPattern.indexOf('*'));
        }

        String servletUrl = Server.parse(environment).withCurrentService().servletUrl();
        String relativePath =
                reportingConfig
                        .targetBasePath()
                        .relativize(Path.of(reportFile))
                        .toString()
                        .replace("\\", "/");
        return URI.create(servletUrl + staticPath + relativePath);
    }
}
