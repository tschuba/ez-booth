/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.lifecycle;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ez.basar.ui.browser")
public class BrowserLauncherConfig {
    private final boolean launch;
    private final int delayInSeconds;

    public BrowserLauncherConfig(boolean launch, int delayInSeconds) {
        this.launch = launch;
        this.delayInSeconds = delayInSeconds;
    }

    public boolean launch() {
        return launch;
    }

    public int delayInSeconds() {
        return delayInSeconds;
    }
}
