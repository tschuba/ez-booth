/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.lifecycle;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import tschuba.ez.booth.ui.util.Server;
import tschuba.ez.booth.ui.util.WithDelay;
import tschuba.ez.booth.ui.views.EntryView;

@Component
@RequiredArgsConstructor
public class LaunchBrowserOnStartupListener implements VaadinServiceInitListener {

    private final ApplicationContext applicationContext;
    private final BrowserLauncherConfig config;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        final VaadinService service = event.getSource();
        if (config.launch()) {
            Duration delay = Duration.ofSeconds(config.delayInSeconds());
            Environment environment = applicationContext.getEnvironment();
            Server.Serving launcher = Server.parse(environment).with(service);
            WithDelay.execute(delay, () -> launcher.launchView(EntryView.class));
        }
    }
}
