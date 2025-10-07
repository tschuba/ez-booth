/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui;

import static lombok.AccessLevel.PRIVATE;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import lombok.NoArgsConstructor;

@PWA(name = "Basarix", shortName = "Basarix")
@NpmPackage(value = "@fontsource/varela-round", version = "4.5.0")
@Theme(value = "basar")
@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings("unused")
public class AppShell implements AppShellConfigurator {
}
