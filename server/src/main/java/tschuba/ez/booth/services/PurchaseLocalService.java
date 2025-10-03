package tschuba.ez.booth.services;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Local service implementation for purchase processing.
 * This class provides the core business logic for handling purchases.
 */
@Service
public class PurchaseLocalService implements PurchaseService {
    @Override
    public @NonNull DataModel.Purchase checkout(ServiceModel.@NonNull Checkout checkout) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Optional<DataModel.Purchase> getPurchaseByKey(DataModel.Purchase.@NonNull Key purchase) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Stream<DataModel.Purchase> getPurchasesByEvent(DataModel.Purchase.@NonNull Key purchase) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
