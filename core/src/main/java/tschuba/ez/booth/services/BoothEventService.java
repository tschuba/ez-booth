package tschuba.ez.booth.services;

import lombok.NonNull;
import tschuba.ez.booth.DataModel;

import java.util.Optional;
import java.util.stream.Stream;

public interface BoothEventService {
    void saveEvent(@NonNull DataModel.BoothEvent event);

    @NonNull
    Stream<DataModel.BoothEvent> getAllEvents();

    @NonNull
    Optional<DataModel.BoothEvent> getEvent(@NonNull DataModel.BoothEvent.Key event);

    @NonNull
    DataModel.BoothEvent closeEvent(@NonNull DataModel.BoothEvent.Key event);

    @NonNull
    DataModel.BoothEvent openEvent(@NonNull DataModel.BoothEvent.Key event);

    void deleteEvent(@NonNull DataModel.BoothEvent.Key event);
}
