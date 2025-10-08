package tschuba.ez.booth.ui.lifecycle;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tschuba.ez.booth.ui.views.EntryView;
import tschuba.commons.core.WithDelay;
import tschuba.commons.vaadin.Browser;

import java.time.Duration;

import static tschuba.commons.spring.SpringServer.byContext;

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
            WithDelay.execute(delay, () -> Browser.launch(byContext(applicationContext).config(), service, EntryView.class));
        }
    }
}
