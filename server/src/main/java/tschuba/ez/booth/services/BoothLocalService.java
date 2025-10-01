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
public class BoothLocalService implements BoothService {
    @Override
    public void saveEvent(DataModel.@NonNull Booth event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Stream<DataModel.Booth> getAllEvents() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Optional<DataModel.Booth> getEvent(DataModel.Booth.@NonNull Key event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull DataModel.Booth closeEvent(DataModel.Booth.@NonNull Key event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull DataModel.Booth openEvent(DataModel.Booth.@NonNull Key event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteEvent(DataModel.Booth.@NonNull Key event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
