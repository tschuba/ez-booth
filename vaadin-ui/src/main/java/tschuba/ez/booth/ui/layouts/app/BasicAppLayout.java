package tschuba.ez.booth.ui.layouts.app;

import org.springframework.beans.factory.annotation.Autowired;
import tschuba.ez.booth.data.BoothRepository;

import java.util.List;

public class BasicAppLayout extends CustomAppLayout {
    public BasicAppLayout(@Autowired BoothRepository booths) {
        super(booths, List.of());
    }
}
