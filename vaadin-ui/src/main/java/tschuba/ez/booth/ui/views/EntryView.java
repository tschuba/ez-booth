/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.views;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.ui.components.event.BoothSelection;
import tschuba.ez.booth.ui.layouts.app.BasicAppLayout;

@Route(value = "", layout = BasicAppLayout.class)
@SpringComponent
@UIScope
public class EntryView extends BoothSelectionView {
    public EntryView(@Autowired BoothService boothService) {
        super(boothService);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);

        if (BoothSelection.get().isPresent()) {
            event.rerouteTo(CheckoutView.class);
        }
    }
}
