/**
 * Copyright (c) 2025-2026 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.layouts.app;

import static com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import static com.vaadin.flow.theme.lumo.LumoUtility.Display;
import static com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import static com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import static com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import static com.vaadin.flow.theme.lumo.LumoUtility.Height;
import static com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import static com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import static com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;
import static org.vaadin.lineawesome.LineAwesomeIcon.CASH_REGISTER_SOLID;
import static org.vaadin.lineawesome.LineAwesomeIcon.COINS_SOLID;
import static org.vaadin.lineawesome.LineAwesomeIcon.EXCHANGE_ALT_SOLID;
import static org.vaadin.lineawesome.LineAwesomeIcon.STORE_SOLID;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouterLink;
import java.util.function.Supplier;
import lombok.Getter;
import tschuba.ez.booth.i18n.I18N;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.ui.data.DataExchangeView;
import tschuba.ez.booth.ui.util.Icons;
import tschuba.ez.booth.ui.views.BoothSelectionView;
import tschuba.ez.booth.ui.views.CheckoutView;
import tschuba.ez.booth.ui.views.VendorReportView;

public class MainMenuItem extends Tab {

  public static final Supplier<MainMenuItem> BOOTH_SELECTION_VIEW =
      () ->
          MainMenuItem.create(
              I18N.textKey(TranslationKeys.BoothSelectionView.MENU_ITEM__TEXT),
              STORE_SOLID.create(),
              BoothSelectionView.class);
  public static final Supplier<MainMenuItem> CHECKOUT_VIEW =
      () ->
          MainMenuItem.create(
              I18N.textKey(TranslationKeys.CheckoutView.MENU_ITEM__TEXT),
              CASH_REGISTER_SOLID.create(),
              CheckoutView.class);
  public static final Supplier<MainMenuItem> VENDOR_REPORT_VIEW =
      () ->
          MainMenuItem.create(
              I18N.textKey(TranslationKeys.VendorReportView.MENU_ITEM__TEXT),
              COINS_SOLID.create(),
              VendorReportView.class);
  public static final Supplier<MainMenuItem> DATA_EXCHANGE_VIEW =
      () ->
          MainMenuItem.create(
              I18N.textKey(TranslationKeys.DataExchangeView.MENU_ITEM__TEXT),
              EXCHANGE_ALT_SOLID.create(),
              DataExchangeView.class);

  private final Span text;
  @Getter private final Class<? extends Component> view;

  public MainMenuItem(I18N.TextKey menuTitle, Component icon, Class<? extends Component> view) {
    RouterLink routerLink = new RouterLink();
    routerLink.addClassNames(
        Display.FLEX,
        Gap.XSMALL,
        Height.MEDIUM,
        AlignItems.CENTER,
        Padding.Horizontal.SMALL,
        Padding.Vertical.MEDIUM,
        TextColor.BODY);
    this.view = view;
    routerLink.setRoute(this.view);

    String title = getTranslation(menuTitle.key());

    text = new Span(title);
    text.setVisible(false);
    text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);
    Tooltip.forComponent(icon).withText(title);

    routerLink.add(icon, text);
    add(routerLink);
  }

  public void toggleText() {
    boolean visible = text.isVisible();
    text.setVisible(!visible);
  }

  public static MainMenuItem create(
      I18N.TextKey title, SvgIcon icon, Class<? extends Component> view) {
    return new MainMenuItem(title, Icons.large(icon), view);
  }
}
