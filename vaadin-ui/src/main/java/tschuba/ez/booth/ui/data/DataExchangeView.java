/**
 * Copyright (c) 2025-2026 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.data;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.NonNull;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.FileExchange;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.PeerToPeerExchange;
import tschuba.ez.booth.ui.layouts.OneColumnLayout;
import tschuba.ez.booth.ui.layouts.app.AppLayoutWithMenu;

/**
 * View for data exchange features, including peer-to-peer exchange and file import/export.
 */
@Route(value = "data-exchange", layout = AppLayoutWithMenu.class)
@SpringComponent
@UIScope
public class DataExchangeView extends OneColumnLayout {

  protected static final String DATA_FILE_EXTENSION = ".ezb";
  private final Paragraph transferDescription = new Paragraph();
  private final Tab transferTab = new Tab();
  private final Paragraph fileExchangeDescription = new Paragraph();
  private final Tab fileExchangeTab = new Tab();
  private final Markdown dataExchangeDescription = new Markdown();

  public DataExchangeView(
      @NonNull SelfInfoCard selfInfo,
      @NonNull TransferCard transferCard,
      @NonNull FileExportCard fileExportCard,
      @NonNull FileImportCard fileImportCard) {

    transferDescription.setWhiteSpace(HasText.WhiteSpace.PRE_LINE);
    VerticalLayout transferContents =
        new VerticalLayout(transferDescription, new HorizontalLayout(selfInfo, transferCard));

    fileExchangeDescription.setWhiteSpace(HasText.WhiteSpace.PRE_LINE);
    VerticalLayout fileExchangeContents =
        new VerticalLayout(
            fileExchangeDescription,
            new HorizontalLayout(JustifyContentMode.BETWEEN, fileExportCard, fileImportCard));
    fileExchangeContents.setFlexGrow(1, fileExportCard);
    fileExchangeContents.setFlexGrow(2, fileImportCard);

    TabSheet tabSheet = new TabSheet();
    tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
    tabSheet.add(transferTab, transferContents);
    tabSheet.add(fileExchangeTab, fileExchangeContents);

    dataExchangeDescription.addClassNames(LumoUtility.Padding.Left.XLARGE, LumoUtility.Padding.Right.XLARGE);

    Main content = new Main(tabSheet, dataExchangeDescription);
    setContent(content);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    transferDescription.setText(getTranslation(PeerToPeerExchange.DESCRIPTION));
    transferTab.setLabel(getTranslation(PeerToPeerExchange.TITLE));
    fileExchangeDescription.setText(getTranslation(FileExchange.DESCRIPTION));
    fileExchangeTab.setLabel(getTranslation(FileExchange.TITLE));
    dataExchangeDescription.setContent(getTranslation(TranslationKeys.DataExchangeView.DESCRIPTION));

    Tooltip.forComponent(transferTab).setText(getTranslation(PeerToPeerExchange.DESCRIPTION));
    Tooltip.forComponent(fileExchangeTab).setText(getTranslation(FileExchange.DESCRIPTION));
  }

}
