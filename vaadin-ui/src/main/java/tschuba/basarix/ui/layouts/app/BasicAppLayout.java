package tschuba.basarix.ui.layouts.app;

import org.springframework.beans.factory.annotation.Autowired;
import tschuba.basarix.services.EventService;

import java.util.List;

public class BasicAppLayout extends CustomAppLayout {
    public BasicAppLayout(@Autowired EventService eventService) {
        super(eventService, List.of());
    }
}
