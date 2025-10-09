/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.lifecycle;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import java.time.Duration;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.ui.util.Server;
import tschuba.ez.booth.ui.util.WithDelay;
import tschuba.ez.booth.ui.views.EntryView;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InitListener {

    @Component
    @RequiredArgsConstructor
    public static class BrowserLaunch implements VaadinServiceInitListener {

        private static final Logger LOGGER = LoggerFactory.getLogger(BrowserLaunch.class);

        private final Environment environment;
        private final BrowserLauncherConfig config;

        @Override
        public void serviceInit(ServiceInitEvent event) {

            LOGGER.debug("serviceInit: {}", event);

            final VaadinService service = event.getSource();
            if (config.launch()) {
                Duration delay = Duration.ofSeconds(config.delayInSeconds());
                Server.Serving launcher = Server.parse(environment).with(service);
                WithDelay.execute(delay, () -> launcher.launchView(EntryView.class));
            }
        }
    }

    @Component
    @RequiredArgsConstructor
    public static class InitI18N implements VaadinServiceInitListener {

        private static final Logger LOGGER = LoggerFactory.getLogger(InitI18N.class);

        private final I18N i18n;

        @Override
        public void serviceInit(ServiceInitEvent serviceEvent) {
            LOGGER.debug("serviceInit: {}", serviceEvent);

            VaadinService service = serviceEvent.getSource();

            service.addUIInitListener(
                    uiEvent -> {
                        UI ui = uiEvent.getUI();
                        Locale locale = ui.getLocale();
                        if (locale == null
                                || !i18n.getConfig().providedLocales().contains(locale)) {
                            ui.setLocale(i18n.getConfig().getDefaultLocale());
                        }
                    });

            service.addSessionInitListener(
                    sessionEvent -> {
                        VaadinSession session = sessionEvent.getSession();
                        session.setAttribute(I18N.class, this.i18n);
                    });
        }
    }
}
