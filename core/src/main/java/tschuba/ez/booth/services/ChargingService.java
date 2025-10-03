package tschuba.ez.booth.services;

import lombok.NonNull;
import tschuba.ez.booth.model.DataModel;

public interface ChargingService {

    @NonNull
    ServiceModel.ChargedFees calculateFees(@NonNull DataModel.Vendor.Key vendor);

    @NonNull
    ServiceModel.Balance.Output calculateBalance(@NonNull ServiceModel.Balance.Input input);
}
