/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.lifecycle;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tschuba.ez.booth.ui.browser")
public class BrowserLauncherConfig {
  private final boolean launch;
  private final Duration delay;

  public BrowserLauncherConfig(boolean launch, Duration delay) {
    this.launch = launch;
    this.delay = delay;
  }

  public boolean launch() {
    return launch;
  }

  public Duration delay() {
    return delay;
  }
}
