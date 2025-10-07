package tschuba.basarix.ui.components.model;

import com.vaadin.flow.component.UI;

import java.util.*;

import static tschuba.basarix.ui.i18n.TranslationKeys.FieldComparator.NO_FIELDS__EXCEPTION;
import static tschuba.basarix.ui.i18n.TranslationKeys.FieldComparator.NO_NULL_FIELDS__EXCEPTION;

public abstract class FieldComparator<T, F> implements Comparator<T> {
    private final List<SortField<F>> fields;

    public FieldComparator(List<SortField<F>> fields) {
        this.fields = fields;
    }

    abstract int compareByField(T item, T otherItem, SortField<F> field);

    @Override
    public int compare(T item1, T item2) {

        for (SortField<F> field : fields) {
            int result = compareByField(item1, item2, field);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public static abstract class Builder<C, T, F> {
        private final List<SortField<F>> fields = new ArrayList<>();

        abstract <B extends Builder<C, T, F>> B self();

        abstract C apply(List<SortField<F>> fields);

        public <B extends Builder<C, T, F>> B ascending(F field) {
            return add(field, SortField.Order.Ascending);
        }

        public <B extends Builder<C, T, F>> B descending(F field) {
            return add(field, SortField.Order.Descending);
        }

        public <B extends Builder<C, T, F>> B add(F field, SortField.Order order) {
            return add(new SortField<>(field, order));
        }

        public <B extends Builder<C, T, F>> B add(SortField<F> field) {
            fields.add(field);
            return self();
        }

        public C build() {
            if (fields.isEmpty()) {
                String message = UI.getCurrent().getTranslation(NO_FIELDS__EXCEPTION);
                throw new IllegalStateException(message);
            }
            Optional<SortField<F>> anyNullField = fields.stream().filter(Objects::isNull).findAny();
            if (anyNullField.isPresent()) {
                String message = UI.getCurrent().getTranslation(NO_NULL_FIELDS__EXCEPTION);
                throw new IllegalStateException(message);
            }
            return apply(fields);
        }
    }
}
