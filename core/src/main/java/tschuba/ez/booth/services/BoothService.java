package tschuba.ez.booth.services;

import lombok.NonNull;
import tschuba.ez.booth.DataModel;

import java.util.Optional;
import java.util.stream.Stream;

public interface BoothService {
    void saveEvent(@NonNull DataModel.Booth event);

    @NonNull
    Stream<DataModel.Booth> getAllEvents();

    @NonNull
    Optional<DataModel.Booth> getEvent(@NonNull DataModel.Booth.Key event);

    @NonNull
    DataModel.Booth closeEvent(@NonNull DataModel.Booth.Key event);

    @NonNull
    DataModel.Booth openEvent(@NonNull DataModel.Booth.Key event);

    void deleteEvent(@NonNull DataModel.Booth.Key event);
}
