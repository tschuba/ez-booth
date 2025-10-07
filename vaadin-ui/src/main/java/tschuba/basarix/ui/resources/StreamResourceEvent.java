package tschuba.basarix.ui.resources;

import com.vaadin.flow.server.StreamRegistration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class StreamResourceEvent implements Serializable {
    private final StreamRegistration registration;
}
