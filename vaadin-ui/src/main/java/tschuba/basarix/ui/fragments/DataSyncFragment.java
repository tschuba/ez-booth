/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.basarix.ui.fragments;

import static tschuba.basarix.ui.i18n.TranslationKeys.DataSyncFragment.HOST_TAB;
import static tschuba.basarix.ui.i18n.TranslationKeys.DataSyncFragment.SUBSCRIBER_TAB;
import static tschuba.basarix.ui.i18n.TranslationKeys.ExportImportView.SYNC_HEADER__TEXT;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.basarix.ui.components.SectionTitle;

/**
 * A view fragment for data synchronization.
 */
@SpringComponent
@UIScope
public class DataSyncFragment extends VerticalLayout {
    private final SectionTitle syncSectionTitle;
    private final Tab hostTab;
    private final Tab subscriberTab;

    @Autowired
    public DataSyncFragment(@NonNull DataSyncHostFragment hostFragment, @Nonnull DataSyncSubscriberFragment subscriberFragment) {
        syncSectionTitle = new SectionTitle();

        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS, TabSheetVariant.LUMO_TABS_CENTERED);

        hostTab = tabSheet.add("", hostFragment);
        subscriberTab = tabSheet.add("", subscriberFragment);

        this.add(syncSectionTitle, tabSheet);
        this.addClassNames(Padding.NONE);
        this.setSpacing(false);
        this.setWidth(30, Unit.EM);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        syncSectionTitle.setText(getTranslation(SYNC_HEADER__TEXT));
        syncSectionTitle.addComponentAsFirst(LineAwesomeIcon.SYNC_SOLID.create());

        hostTab.setLabel(getTranslation(HOST_TAB));
        subscriberTab.setLabel(getTranslation(SUBSCRIBER_TAB));
    }
}
