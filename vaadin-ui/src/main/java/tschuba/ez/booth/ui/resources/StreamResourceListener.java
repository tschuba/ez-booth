package tschuba.ez.booth.ui.resources;

import java.io.Serializable;

public interface StreamResourceListener extends Serializable {
    void consumed(StreamResourceEvent event);

    void failed(StreamResourceFailedEvent event);
}
