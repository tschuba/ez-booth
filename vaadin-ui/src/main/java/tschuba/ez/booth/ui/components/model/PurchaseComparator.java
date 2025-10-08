package tschuba.ez.booth.ui.components.model;

import tschuba.basarix.data.model.Purchase;

import java.util.List;

public class PurchaseComparator extends FieldComparator<Purchase, PurchaseComparator.Field> {
    public enum Field {Id, DateTime, Value}

    public PurchaseComparator(List<SortField<Field>> sortFields) {
        super(sortFields);
    }

    @Override
    int compareByField(Purchase purchase, Purchase otherPurchase, SortField<Field> sortField) {
        int result = 0;
        Field field = sortField.getField();
        if (field.equals(Field.Id)) {
            result = purchase.getKey().compareTo(otherPurchase.getKey());
        } else if (field.equals(Field.DateTime)) {
            result = purchase.getDateTime().compareTo(otherPurchase.getDateTime());
        } else if (field.equals(Field.Value)) {
            result = purchase.getValue().compareTo(otherPurchase.getValue());
        }
        if (SortField.Order.Descending.equals(sortField.getOrder())) {
            result *= -1;
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends FieldComparator.Builder<PurchaseComparator, Purchase, PurchaseComparator.Field> {
        private Builder() {
        }

        @Override
        <B extends FieldComparator.Builder<PurchaseComparator, Purchase, Field>> B self() {
            return (B) this;
        }

        @Override
        PurchaseComparator apply(List<SortField<Field>> sortFields) {
            return new PurchaseComparator(sortFields);
        }
    }
}
