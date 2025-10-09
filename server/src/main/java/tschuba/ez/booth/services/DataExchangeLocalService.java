/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;

@Service
public class DataExchangeLocalService implements DataExchangeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeLocalService.class);

    private final DataService dataService;

    public DataExchangeLocalService(@NonNull DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public @NonNull ServiceModel.ExchangeData exchangeData(ServiceModel.ExchangeData dataReceived) {

        LOGGER.debug("Importing remote data: {}", dataReceived);
        dataService.merge(dataReceived);
        LOGGER.debug("Remote data imported successfully.");

        DataModel.Booth.Key booth = dataReceived.booth().key();
        LOGGER.debug("Exporting updated data for booth: {}", booth);
        ServiceModel.ExchangeData exportData = dataService.export(booth);
        LOGGER.debug("Export data: {}", exportData);
        return exportData;
    }
}
