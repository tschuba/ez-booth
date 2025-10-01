package tschuba.ez.booth.services;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.DataModel;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Local service implementation for booth events.
 * This class provides the core business logic for managing booth events.
 */
@Service
public class BoothEventLocalService implements BoothEventService {
    @Override
    public void saveEvent(DataModel.@NonNull BoothEvent event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Stream<DataModel.BoothEvent> getAllEvents() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Optional<DataModel.BoothEvent> getEvent(DataModel.BoothEvent.@NonNull Key event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull DataModel.BoothEvent closeEvent(DataModel.BoothEvent.@NonNull Key event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull DataModel.BoothEvent openEvent(DataModel.BoothEvent.@NonNull Key event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteEvent(DataModel.BoothEvent.@NonNull Key event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
