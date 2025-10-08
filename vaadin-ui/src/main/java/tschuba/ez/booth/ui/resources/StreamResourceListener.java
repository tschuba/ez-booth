/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.resources;

import java.io.Serializable;

public interface StreamResourceListener extends Serializable {
    void consumed(StreamResourceEvent event);

    void failed(StreamResourceFailedEvent event);
}
