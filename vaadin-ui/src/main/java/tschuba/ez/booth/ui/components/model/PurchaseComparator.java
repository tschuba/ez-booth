/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.model;

import java.util.List;
import tschuba.ez.booth.model.DataModel;

public class PurchaseComparator
        extends FieldComparator<DataModel.Purchase, PurchaseComparator.Field> {
    public enum Field {
        Id,
        DateTime,
        Value
    }

    public PurchaseComparator(List<SortField<Field>> sortFields) {
        super(sortFields);
    }

    @Override
    int compareByField(
            DataModel.Purchase purchase,
            DataModel.Purchase otherPurchase,
            SortField<Field> sortField) {
        int result = 0;
        Field field = sortField.getField();
        if (field.equals(Field.Id)) {
            result = purchase.key().compareTo(otherPurchase.key());
        } else if (field.equals(Field.DateTime)) {
            result = purchase.purchasedOn().compareTo(otherPurchase.purchasedOn());
        } else if (field.equals(Field.Value)) {
            result = purchase.value().compareTo(otherPurchase.value());
        }
        if (SortField.Order.Descending.equals(sortField.getOrder())) {
            result *= -1;
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends FieldComparator.Builder<
                    PurchaseComparator, DataModel.Purchase, PurchaseComparator.Field> {
        private Builder() {}

        @Override
        <B extends FieldComparator.Builder<PurchaseComparator, DataModel.Purchase, Field>>
                B self() {
            return (B) this;
        }

        @Override
        PurchaseComparator apply(List<SortField<Field>> sortFields) {
            return new PurchaseComparator(sortFields);
        }
    }
}
