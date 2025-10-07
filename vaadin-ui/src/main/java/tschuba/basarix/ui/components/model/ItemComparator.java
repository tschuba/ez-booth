package tschuba.basarix.ui.components.model;

import tschuba.basarix.data.model.Item;

import java.util.List;

public class ItemComparator extends FieldComparator<Item, ItemComparator.Field> {
    public enum Field {Key, DateTime, Price}

    public ItemComparator(List<SortField<Field>> sortFields) {
        super(sortFields);
    }

    @Override
    int compareByField(Item item, Item otherItem, SortField<Field> sortField) {
        int result = 0;
        Field field = sortField.getField();
        if (field.equals(Field.Key)) {
            result = item.getKey().compareTo(otherItem.getKey());
        } else if (field.equals(Field.DateTime)) {
            result = item.getDateTime().compareTo(otherItem.getDateTime());
        } else if (field.equals(Field.Price)) {
            result = item.getPrice().compareTo(otherItem.getPrice());
        }
        if (SortField.Order.Descending.equals(sortField.getOrder())) {
            result *= -1;
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends FieldComparator.Builder<ItemComparator, Item, ItemComparator.Field> {
        private Builder() {
        }

        @Override
        <B extends FieldComparator.Builder<ItemComparator, Item, Field>> B self() {
            return (B) this;
        }

        @Override
        ItemComparator apply(List<SortField<Field>> sortFields) {
            return new ItemComparator(sortFields);
        }
    }
}
