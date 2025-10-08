package tschuba.ez.booth.ui.components.model;

import tschuba.ez.booth.model.DataModel;

import java.util.List;

public class ItemComparator extends FieldComparator<DataModel.PurchaseItem, ItemComparator.Field> {
    public enum Field {Key, DateTime, Price}

    public ItemComparator(List<SortField<Field>> sortFields) {
        super(sortFields);
    }

    @Override
    int compareByField(DataModel.PurchaseItem item, DataModel.PurchaseItem otherItem, SortField<Field> sortField) {
        int result = 0;
        Field field = sortField.getField();
        if (field.equals(Field.Key)) {
            result = item.key().compareTo(otherItem.key());
        } else if (field.equals(Field.DateTime)) {
            result = item.purchasedOn().compareTo(otherItem.purchasedOn());
        } else if (field.equals(Field.Price)) {
            result = item.price().compareTo(otherItem.price());
        }
        if (SortField.Order.Descending.equals(sortField.getOrder())) {
            result *= -1;
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends FieldComparator.Builder<ItemComparator, DataModel.PurchaseItem, ItemComparator.Field> {
        private Builder() {
        }

        @Override
        <B extends FieldComparator.Builder<ItemComparator, DataModel.PurchaseItem, Field>> B self() {
            return (B) this;
        }

        @Override
        ItemComparator apply(List<SortField<Field>> sortFields) {
            return new ItemComparator(sortFields);
        }
    }
}
