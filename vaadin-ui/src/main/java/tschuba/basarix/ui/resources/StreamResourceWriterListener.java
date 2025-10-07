package tschuba.basarix.ui.resources;

import java.io.Serializable;

public interface StreamResourceWriterListener extends Serializable {
    void accepted();

    void failed(StreamResourceWriterFailedEvent event);
}
