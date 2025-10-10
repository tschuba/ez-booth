/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.model;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelGrid<TYPE, F extends Filter<TYPE, C>, C> extends Grid<TYPE> {
  private final Supplier<Stream<TYPE>> itemsSupplier;
  private F filter;

  public ModelGrid(Supplier<Stream<TYPE>> itemsSupplier) {
    this.itemsSupplier = itemsSupplier;

    CallbackDataProvider<TYPE, C> dataProvider =
        DataProvider.fromFilteringCallbacks(this::fetch, this::count);
    ConfigurableFilterDataProvider<TYPE, Void, C> filterDataProvider =
        dataProvider.withConfigurableFilter();
    setItems(filterDataProvider);
  }

  public void setFilter(F filter) {
    this.filter = filter;
  }

  private Stream<TYPE> allItems() {
    return this.itemsSupplier.get();
  }

  private Stream<TYPE> filteredItems(Query<TYPE, C> query) {
    Stream<TYPE> items = allItems();
    return query
        .getFilter()
        .map(criterion -> items.filter(it -> filter.apply(it, criterion)))
        .orElse(items);
  }

  private Stream<TYPE> fetch(Query<TYPE, C> query) {
    Stream<TYPE> filteredItems = filteredItems(query);
    List<TYPE> filteredAndReducedItems =
        filteredItems.skip(query.getOffset()).limit(query.getLimit()).collect(Collectors.toList());
    query.getSortingComparator().ifPresent(filteredAndReducedItems::sort);
    return filteredAndReducedItems.stream();
  }

  private int count(Query<TYPE, C> query) {
    return (int) filteredItems(query).count();
  }
}
