/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui;

import static lombok.AccessLevel.PRIVATE;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import lombok.NoArgsConstructor;

@PWA(name = "ez-booth", shortName = "ez-booth")
@NpmPackage(value = "@fontsource/varela-round", version = "4.5.0")
@Theme(value = "booth")
@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings("unused")
public class AppShell implements AppShellConfigurator {
}
