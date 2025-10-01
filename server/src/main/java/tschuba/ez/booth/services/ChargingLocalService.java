package tschuba.ez.booth.services;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.DataModel;

/**
 * Local service implementation for charging calculations.
 * This class provides the core business logic for calculating fees and balances.
 */
@Service
public class ChargingLocalService implements ChargingService {
    @Override
    @NonNull
    public ServiceModel.ChargedFees calculateFees(DataModel.Vendor.@NonNull Key vendor) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @NonNull
    public ServiceModel.Balance.Output calculateBalance(@NonNull ServiceModel.Balance.Input input) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
