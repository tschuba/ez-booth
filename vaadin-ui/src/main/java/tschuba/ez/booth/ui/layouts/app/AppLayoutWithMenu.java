/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts.app;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.services.BoothService;
import tschuba.ez.booth.ui.views.BoothSelectionView;
import tschuba.ez.booth.ui.views.CheckoutView;
import tschuba.ez.booth.ui.views.DataExchangeView;
import tschuba.ez.booth.ui.views.VendorReportView;

import java.util.List;

import static org.vaadin.lineawesome.LineAwesomeIcon.CASH_REGISTER_SOLID;
import static org.vaadin.lineawesome.LineAwesomeIcon.COINS_SOLID;
import static org.vaadin.lineawesome.LineAwesomeIcon.EXCHANGE_ALT_SOLID;
import static org.vaadin.lineawesome.LineAwesomeIcon.STORE_SOLID;

public class AppLayoutWithMenu extends CustomAppLayout {
  @Autowired
  public AppLayoutWithMenu(@NonNull BoothService booths, @NonNull Environment environment) {
    super(
        booths,
        List.of(
            MainMenuItem.create(
                I18N.textKey(TranslationKeys.BoothSelectionView.MENU_ITEM__TEXT),
                STORE_SOLID.create(),
                BoothSelectionView.class),
            MainMenuItem.create(
                I18N.textKey(TranslationKeys.CheckoutView.MENU_ITEM__TEXT),
                CASH_REGISTER_SOLID.create(),
                CheckoutView.class),
            MainMenuItem.create(
                I18N.textKey(TranslationKeys.VendorReportView.MENU_ITEM__TEXT),
                COINS_SOLID.create(),
                VendorReportView.class),
            MainMenuItem.create(
                I18N.textKey(TranslationKeys.DataExchangeView.MENU_ITEM__TEXT),
                EXCHANGE_ALT_SOLID.create(),
                DataExchangeView.class)),
        environment);
  }
}
