package tschuba.ez.booth.services;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import tschuba.ez.booth.DataModel;
import tschuba.ez.booth.proto.BoothServiceGrpc;
import tschuba.ez.booth.proto.ProtoMapper;
import tschuba.ez.booth.proto.ProtoModel;

import java.util.stream.Stream;

/**
 * gRPC service implementation for booth events.
 * This class handles incoming gRPC requests and delegates the operations to the local service layer.
 */
@GrpcService
public class BoothGrpcService extends BoothServiceGrpc.BoothServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoothGrpcService.class);

    private final BoothLocalService localService;

    @Autowired
    BoothGrpcService(@NonNull BoothLocalService localService) {
        this.localService = localService;
    }

    @Override
    public void save(ProtoModel.Booth request, StreamObserver<Empty> responseObserver) {
        try {
            DataModel.Booth booth = ProtoMapper.messageToObject(request);
            localService.save(booth);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error saving booth: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void getAll(Empty request, StreamObserver<ProtoModel.Booth> responseObserver) {
        try {
            Stream<DataModel.Booth> allEvents = localService.getAll();
            allEvents.forEach(booth -> {
                ProtoModel.Booth boothMsg = ProtoMapper.objectToMessage(booth);
                responseObserver.onNext(boothMsg);
            });
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error getting all events", ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void get(ProtoModel.BoothKey request, StreamObserver<ProtoModel.Booth> responseObserver) {
        try {
            DataModel.Booth.Key eventKey = ProtoMapper.messageToObject(request);
            localService.get(eventKey).ifPresent(booth -> {
                ProtoModel.Booth boothMsg = ProtoMapper.objectToMessage(booth);
                responseObserver.onNext(boothMsg);
            });
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error getting booth: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void close(ProtoModel.BoothKey request, StreamObserver<ProtoModel.Booth> responseObserver) {
        try {
            DataModel.Booth.Key key = ProtoMapper.messageToObject(request);
            DataModel.Booth booth = localService.close(key);
            ProtoModel.Booth boothMsg = ProtoMapper.objectToMessage(booth);
            responseObserver.onNext(boothMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error closing booth: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void open(ProtoModel.BoothKey request, StreamObserver<ProtoModel.Booth> responseObserver) {
        try {
            DataModel.Booth.Key event = ProtoMapper.messageToObject(request);
            DataModel.Booth booth = localService.open(event);
            ProtoModel.Booth boothMsg = ProtoMapper.objectToMessage(booth);
            responseObserver.onNext(boothMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error opening booth: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void delete(ProtoModel.BoothKey request, StreamObserver<Empty> responseObserver) {
        try {
            DataModel.Booth.Key key = ProtoMapper.messageToObject(request);
            localService.delete(key);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error deleting booth: {}", request, ex);
            responseObserver.onError(ex);
        }
    }
}
