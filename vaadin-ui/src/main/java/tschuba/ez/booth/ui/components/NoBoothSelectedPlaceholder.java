package tschuba.ez.booth.ui.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouteParameters;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.lineawesome.LineAwesomeIcon;
import tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.Transfer;
import tschuba.ez.booth.ui.util.Buttons;
import tschuba.ez.booth.ui.util.NavigateTo;
import tschuba.ez.booth.ui.util.Routing;
import tschuba.ez.booth.ui.views.BoothSelectionView;

/**
 * A placeholder component that is shown when no booth is selected. It prompts the user to select a booth and provides a button to navigate to the booth selection view.
 */
@Slf4j
public class NoBoothSelectedPlaceholder extends Composite<VerticalLayout> {

  private final Paragraph noBoothSelectedText = new Paragraph();
  private final Button selectBoothButton =
      new Button(LineAwesomeIcon.PERSON_BOOTH_SOLID.create());

  public NoBoothSelectedPlaceholder() {
    VerticalLayout content = getContent();
    content.setAlignItems(FlexComponent.Alignment.CENTER);
    content.add(noBoothSelectedText, selectBoothButton);

    Buttons.disableUntilAfterClick(
        selectBoothButton,
        _ -> {
          try {
            Component currentView = UI.getCurrent().getCurrentView();
            RouteParameters routeParameters =
                Routing.Parameters.builder().returnToView(currentView.getClass()).build();
            NavigateTo.view(BoothSelectionView.class, routeParameters).currentWindow();
          } catch (Exception ex) {
            log.error("Failed to navigate to booth selection view", ex);
          }
        });
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    noBoothSelectedText.setText(getTranslation(Transfer.NOTIFICATION__NO_BOOTH_SELECTED));
    selectBoothButton.setText(getTranslation(Transfer.SELECT_BOOTH_BUTTON__TEXT));
  }
}
