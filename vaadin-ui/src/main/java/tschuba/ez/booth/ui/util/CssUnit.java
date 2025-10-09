/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import static com.vaadin.flow.component.Unit.PIXELS;

import com.vaadin.flow.component.Unit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;

@Getter
public class CssUnit {
    private final float value;
    private final Unit unit;

    private CssUnit(float value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public static CssUnit pixels(float pixel) {
        return cssUnit(pixel, PIXELS);
    }

    public static CssUnit cssUnit(float value, Unit unit) {
        return new CssUnit(value, unit);
    }

    public static String css(float value, Unit unit) {
        return cssUnit(value, unit).css();
    }

    public String css() {
        return "%f %s".formatted(value, unit);
    }

    public void apply(BiConsumer<Float, Unit> consumer) {
        consumer.accept(value, unit);
    }

    public CssUnit calculate(Function<Float, Float> operation) {
        return new CssUnit(operation.apply(this.value), this.unit);
    }
}
