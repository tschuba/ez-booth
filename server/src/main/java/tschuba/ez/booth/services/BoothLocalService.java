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
    public void save(DataModel.@NonNull Booth booth) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Stream<DataModel.Booth> getAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Optional<DataModel.Booth> get(@NonNull DataModel.Booth.Key booth) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull DataModel.Booth close(@NonNull DataModel.Booth.Key booth) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull DataModel.Booth open(@NonNull DataModel.Booth.Key booth) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(@NonNull DataModel.Booth.Key booth) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
