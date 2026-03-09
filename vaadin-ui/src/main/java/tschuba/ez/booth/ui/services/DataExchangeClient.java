/**
 * Copyright (c) 2025-2026 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.DataExchangeServiceGrpc;
import tschuba.ez.booth.proto.ProtoServices;

/**
 * Client for the DataExchangeService to perform data exchange with remote booths.
 */
@Service
@Slf4j
public class DataExchangeClient {

  private final GrpcChannelFactory channelFactory;
  private final DataExchangeServiceGrpc.DataExchangeServiceBlockingStub dataExchangeService;

  @Autowired
  public DataExchangeClient(
      @NonNull GrpcChannelFactory channelFactory,
      @NonNull DataExchangeServiceGrpc.DataExchangeServiceBlockingStub dataExchangeService) {
    this.channelFactory = channelFactory;
    this.dataExchangeService = dataExchangeService;
  }

  /**
   * Exchange data with the target address for the specified booth.
   *
   * @param targetAddress the target address in the format "host:port"
   * @param booth         the booth to exchange data for
   */
  public void exchangeDataWith(@NonNull String targetAddress, @NonNull DataModel.Booth.Key booth) {
    log.info("Starting data exchange with {} for booth {}", targetAddress, booth);
    ProtoServices.ExchangeData localData =
        dataExchangeService.exportData(ProtoMapper.objectToMessage(booth));

    ManagedChannel channel = channelFactory.createChannel(targetAddress);
    DataExchangeServiceGrpc.DataExchangeServiceBlockingStub dataExchangeServiceClient =
        DataExchangeServiceGrpc.newBlockingStub(channel);
    ProtoServices.ExchangeData remoteData = dataExchangeServiceClient.syncData(localData);
    log.debug("Merging received remote data: {}", remoteData);
    Empty _ = dataExchangeService.mergeData(remoteData);
    log.debug("Data exchange completed successfully.");
  }

  /**
   * Export data for the specified booth.
   *
   * @param booth the booth to export data for
   * @return the exported data
   */
  public @NonNull ProtoServices.ExchangeData exportData(@NonNull DataModel.Booth.Key booth) {
    try {
      ProtoServices.ExchangeData exportData =
          dataExchangeService.exportData(ProtoMapper.objectToMessage(booth));
      log.info("Exported data request completed for booth {}", booth);
      return exportData;
    } catch (Exception ex) {
      log.error("Error exporting data for booth {}", booth, ex);
      throw ex;
    }
  }

  /**
   * Merge the given data into the local data store.
   *
   * @param data the data to merge
   */
  public void mergeData(@NonNull ProtoServices.ExchangeData data) {
    try {
      Empty _ = dataExchangeService.mergeData(data);
      log.info("Merge data request completed successfully");
    } catch (Exception ex) {
      log.error("Error merging data", ex);
    }
  }
}
