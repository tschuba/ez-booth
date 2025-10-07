package tschuba.basarix.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import tschuba.basarix.ui.components.Block;
import tschuba.basarix.ui.layouts.OneColumnLayout;
import tschuba.basarix.ui.layouts.app.AppLayoutWithMenu;
import tschuba.commons.vaadin.NavigateTo;

import java.util.Optional;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;
import static tschuba.basarix.ui.i18n.TranslationKeys.InfoView.*;

@Route(value = "info", layout = AppLayoutWithMenu.class)
public class InfoView extends OneColumnLayout {
    public InfoView() {
        setTitle(getTranslation(TITLE));

        Main content = new Main();
        content.addClassNames(Display.GRID, Grid.Column.COLUMNS_2, Gap.LARGE, AlignItems.START);
        setContent(content);

        final String homepageUrl = "https://github.com/tschuba/ez-basar";
        Block homepageBlock = new Block();
        homepageBlock.setTitle(getTranslation(HOMEPAGE__LABEL));
        Button homepageLink = new Button(homepageUrl);
        homepageLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        homepageLink.addClassNames(Padding.NONE, Margin.NONE);
        homepageLink.addClickListener(clickEvent -> NavigateTo.url(homepageUrl).newWindow());
        homepageBlock.setContent(homepageLink);
        content.add(homepageBlock);

        String appVersion = Optional.ofNullable(getClass().getPackage().getImplementationVersion())
                .orElseGet(() -> getTranslation(VERSION_UNKNOWN__TEXT));
        Block versionBlock = new Block();
        versionBlock.setTitle(getTranslation(VERSION__LABEL));
        versionBlock.setContent(appVersion);
        content.add(versionBlock);
    }
}