/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.components.model;

import java.util.function.BiFunction;

public interface Filter<T, C> extends BiFunction<T, C, Boolean> {}
