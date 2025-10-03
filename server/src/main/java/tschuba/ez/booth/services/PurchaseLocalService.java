package tschuba.ez.booth.services;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.EntitiesMapper;
import tschuba.ez.booth.model.Repositories;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Local service implementation for purchase processing.
 * This class provides the core business logic for handling purchases.
 */
@Service
public class PurchaseLocalService implements PurchaseService {

    private final Repositories.Purchase purchaseRepository;

    @Autowired
    public PurchaseLocalService(@NonNull Repositories.Purchase purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public @NonNull DataModel.Purchase checkout(@NonNull ServiceModel.Checkout checkout) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull Optional<DataModel.Purchase> getPurchaseByKey(@NonNull DataModel.Purchase.Key purchase) {
        return purchaseRepository.findById(EntitiesMapper.objectToEntity(purchase)).map(EntitiesMapper::entityToObject);
    }

    @Override
    public @NonNull Stream<DataModel.Purchase> getPurchasesByEvent(@NonNull DataModel.Purchase.Key purchase) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
