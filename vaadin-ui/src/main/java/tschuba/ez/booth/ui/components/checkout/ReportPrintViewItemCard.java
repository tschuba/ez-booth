package tschuba.ez.booth.ui.components.checkout;

import com.vaadin.flow.theme.lumo.LumoUtility;
import tschuba.basarix.data.model.Item;
import tschuba.ez.booth.ui.components.ItemListItem;

public class ReportPrintViewItemCard extends ItemListItem {
    public ReportPrintViewItemCard(Item item) {
        super(item);
        addClassNames(LumoUtility.FlexDirection.COLUMN_REVERSE, LumoUtility.AlignItems.END);
    }
}
