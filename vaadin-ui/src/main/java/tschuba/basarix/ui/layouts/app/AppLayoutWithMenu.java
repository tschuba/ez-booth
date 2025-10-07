/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.layouts.app;

import static org.vaadin.lineawesome.LineAwesomeIcon.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import tschuba.basarix.services.EventService;
import tschuba.basarix.ui.i18n.TranslationKeys;
import tschuba.basarix.ui.views.*;
import tschuba.commons.vaadin.i18n.I18NTextKey;

public class AppLayoutWithMenu extends CustomAppLayout {
    public AppLayoutWithMenu(@Autowired EventService eventService) {
        super(eventService, List.of(
                MainMenuItem.create(new I18NTextKey(TranslationKeys.EventSelectionView.MENU_ITEM__TEXT), STORE_SOLID.create(), EventSelectionView.class), MainMenuItem.create(new I18NTextKey(TranslationKeys.CheckoutView.MENU_ITEM__TEXT), CASH_REGISTER_SOLID.create(), CheckoutView.class), MainMenuItem.create(new I18NTextKey(TranslationKeys.VendorReportView.MENU_ITEM__TEXT), COINS_SOLID.create(), VendorReportView.class), MainMenuItem.create(new I18NTextKey(TranslationKeys.ExportImportView.MENU_ITEM__TEXT), SD_CARD_SOLID.create(), ExportImportView.class), MainMenuItem.create(new I18NTextKey(TranslationKeys.InfoView.MENU_ITEM__TEXT), INFO_SOLID.create(), InfoView.class)
        ));
    }

}
