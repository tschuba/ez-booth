package tschuba.ez.booth.services;

import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.ChargingServiceGrpc;
import tschuba.ez.booth.proto.ProtoModel;
import tschuba.ez.booth.proto.ProtoServices;

/**
 * gRPC service implementation for charging calculations.
 * This class handles incoming gRPC requests and delegates the operations to the local service layer.
 */
@GrpcService
public class ChargingGrpcService extends ChargingServiceGrpc.ChargingServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChargingGrpcService.class);

    private final ChargingLocalService localService;

    @Autowired
    ChargingGrpcService(@NonNull ChargingLocalService localService) {
        this.localService = localService;
    }

    @Override
    public void calculateFees(ProtoModel.VendorKey request, StreamObserver<ProtoServices.ChargedFees> responseObserver) {
        try {
            DataModel.Vendor.Key vendor = ProtoMapper.VENDOR_KEY.messageToObject(request);
            ServiceModel.ChargedFees chargedFees = localService.calculateFees(vendor);

            ProtoServices.ChargedFees chargedFeesMsg = ProtoMapper.CHARGED_FEES.objectToMessage(chargedFees);
            responseObserver.onNext(chargedFeesMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error calculating fees for vendor: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void calculateBalance(ProtoServices.BalanceInput request, StreamObserver<ProtoServices.SalesBalance> responseObserver) {
        try {
            ServiceModel.Balance.Input input = ProtoMapper.BALANCE_INPUT.messageToObject(request);
            ServiceModel.Balance.Output output = localService.calculateBalance(input);

            ProtoServices.SalesBalance balanceMsg = ProtoMapper.BALANCE_OUTPUT.objectToMessage(output);
            responseObserver.onNext(balanceMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error calculating balance: {}", request, ex);
            responseObserver.onError(ex);
        }
    }
}
