package tschuba.ez.booth.services;

import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

import java.util.Optional;
import java.util.stream.Stream;

public interface BoothService {
    void save(@NonNull DataModel.Booth event);

    @NonNull
    Stream<DataModel.Booth> getAll();

    @NonNull
    Optional<DataModel.Booth> get(@NonNull DataModel.Booth.Key event);

    @NonNull
    DataModel.Booth close(@NonNull DataModel.Booth.Key event);

    @NonNull
    DataModel.Booth open(@NonNull DataModel.Booth.Key event);

    void delete(@NonNull DataModel.Booth.Key event);
}
