/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceRegistry;
import com.vaadin.flow.server.VaadinSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Optional;

public class NavigateTo {
    private NavigateTo() {}

    public static Target uri(URI uri) {
        return url(uri.toString());
    }

    public static Target url(String url) {
        return new Target(url);
    }

    public static Target view(Class<? extends Component> targetView) {
        return view(targetView, null);
    }

    public static Target view(Class<? extends Component> targetView, RouteParameters parameters) {
        String url =
                Optional.ofNullable(parameters)
                        .map(params -> Routing.urlForView(targetView, params))
                        .orElseGet(() -> Routing.urlForView(targetView));
        return url(url);
    }

    public static Target file(final File file) {
        StreamResource fileResource =
                new StreamResource(
                        file.getName(),
                        () -> {
                            try {
                                return new FileInputStream(file);
                            } catch (FileNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
        StreamResourceRegistry resourceRegistry = VaadinSession.getCurrent().getResourceRegistry();
        StreamRegistration fileStream = resourceRegistry.registerResource(fileResource);
        return uri(fileStream.getResourceUri());
    }

    public static class Target {
        private static final String TARGET_SELF = "_self";
        private static final String TARGET_BLANK = "_blank";

        private final String url;

        private Target(String url) {
            this.url = url;
        }

        public void newWindow() {
            open(TARGET_BLANK);
        }

        public void currentWindow() {
            open(TARGET_SELF);
        }

        public String url() {
            return url;
        }

        private void open(String target) {
            UI.getCurrent().getPage().open(this.url, target);
        }
    }
}
