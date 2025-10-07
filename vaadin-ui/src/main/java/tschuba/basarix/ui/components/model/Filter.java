package tschuba.basarix.ui.components.model;

import java.util.function.BiFunction;

public interface Filter<T, C> extends BiFunction<T, C, Boolean> {
}
