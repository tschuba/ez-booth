/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts.app;

import static org.vaadin.lineawesome.LineAwesomeIcon.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.ui.views.BoothSelectionView;
import tschuba.ez.booth.ui.views.CheckoutView;
import tschuba.ez.booth.ui.views.VendorReportView;

public class AppLayoutWithMenu extends CustomAppLayout {
    public AppLayoutWithMenu(@Autowired BoothService booths) {
        super(
                booths,
                List.of(
                        MainMenuItem.create(
                                new I18N.TextKey(
                                        TranslationKeys.EventSelectionView.MENU_ITEM__TEXT),
                                STORE_SOLID.create(),
                                BoothSelectionView.class),
                        MainMenuItem.create(
                                new I18N.TextKey(TranslationKeys.CheckoutView.MENU_ITEM__TEXT),
                                CASH_REGISTER_SOLID.create(),
                                CheckoutView.class),
                        MainMenuItem.create(
                                new I18N.TextKey(TranslationKeys.VendorReportView.MENU_ITEM__TEXT),
                                COINS_SOLID.create(),
                                VendorReportView.class)));
    }
}
