/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.layouts.app;

import static org.vaadin.lineawesome.LineAwesomeIcon.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.ez.booth.data.BoothRepository;
import tschuba.ez.booth.ui.i18n.I18NTextKey;
import tschuba.ez.booth.ui.i18n.TranslationKeys;
import tschuba.ez.booth.ui.views.*;

public class AppLayoutWithMenu extends CustomAppLayout {
    public AppLayoutWithMenu(@Autowired BoothRepository booths) {
        super(booths, List.of(
                MainMenuItem.create(new I18NTextKey(TranslationKeys.EventSelectionView.MENU_ITEM__TEXT), STORE_SOLID.create(), BoothSelectionView.class), MainMenuItem.create(new I18NTextKey(TranslationKeys.CheckoutView.MENU_ITEM__TEXT), CASH_REGISTER_SOLID.create(), CheckoutView.class), MainMenuItem.create(new I18NTextKey(TranslationKeys.VendorReportView.MENU_ITEM__TEXT), COINS_SOLID.create(), VendorReportView.class), MainMenuItem.create(new I18NTextKey(TranslationKeys.InfoView.MENU_ITEM__TEXT), INFO_SOLID.create(), InfoView.class)
        ));
    }

}
