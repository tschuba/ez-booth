/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.services;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class DataExchangeClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeClient.class);

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
    LOGGER.info("Starting data exchange with {} for booth {}", targetAddress, booth);
    ProtoServices.ExchangeData localData =
        dataExchangeService.exportData(ProtoMapper.objectToMessage(booth));

    ManagedChannel channel = channelFactory.createChannel(targetAddress);
    DataExchangeServiceGrpc.DataExchangeServiceBlockingStub dataExchangeServiceClient =
        DataExchangeServiceGrpc.newBlockingStub(channel);
    ProtoServices.ExchangeData remoteData = dataExchangeServiceClient.syncData(localData);

    LOGGER.debug("Merging received remote data: {}", remoteData);
    Empty _ = dataExchangeService.mergeData(remoteData);
    LOGGER.debug("Data exchange completed successfully.");
  }
}
