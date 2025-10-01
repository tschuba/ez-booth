package tschuba.ez.booth.services;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import tschuba.ez.booth.DataModel;
import tschuba.ez.booth.proto.BoothEventServiceGrpc;
import tschuba.ez.booth.proto.ProtoMapper;
import tschuba.ez.booth.proto.ProtoModel;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * gRPC service implementation for booth events.
 * This class handles incoming gRPC requests and delegates the operations to the local service layer.
 */
@GrpcService
public class BoothEventGrpcService extends BoothEventServiceGrpc.BoothEventServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoothEventGrpcService.class);

    private final BoothEventLocalService localService;

    @Autowired
    public BoothEventGrpcService(BoothEventLocalService localService) {
        this.localService = localService;
    }

    @Override
    public void saveEvent(ProtoModel.BoothEvent request, StreamObserver<Empty> responseObserver) {
        try {
            DataModel.BoothEvent eventObj = ProtoMapper.EVENT.messageToObject(request);
            localService.saveEvent(eventObj);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error saving event: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void getAllEvents(Empty request, StreamObserver<ProtoModel.BoothEvent> responseObserver) {
        try {
            Stream<DataModel.BoothEvent> allEvents = localService.getAllEvents();
            allEvents.forEach(eventObj -> {
                ProtoModel.BoothEvent eventMsg = ProtoMapper.EVENT.objectToMessage(eventObj);
                responseObserver.onNext(eventMsg);
            });
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error getting all events", ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void getEvent(ProtoModel.BoothEventKey request, StreamObserver<ProtoModel.BoothEvent> responseObserver) {
        try {
            DataModel.BoothEvent.Key eventKey = ProtoMapper.EVENT_KEY.messageToObject(request);
            Optional<DataModel.BoothEvent> event = localService.getEvent(eventKey);
            event.ifPresent(eventObj -> {
                ProtoModel.BoothEvent eventMsg = ProtoMapper.EVENT.objectToMessage(eventObj);
                responseObserver.onNext(eventMsg);
            });
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error getting event: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void closeEvent(ProtoModel.BoothEventKey request, StreamObserver<ProtoModel.BoothEvent> responseObserver) {
        try {
            DataModel.BoothEvent.Key key = ProtoMapper.EVENT_KEY.messageToObject(request);
            DataModel.BoothEvent eventObj = localService.closeEvent(key);
            ProtoModel.BoothEvent eventMsg = ProtoMapper.EVENT.objectToMessage(eventObj);
            responseObserver.onNext(eventMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error closing event: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void openEvent(ProtoModel.BoothEventKey request, StreamObserver<ProtoModel.BoothEvent> responseObserver) {
        try {
            DataModel.BoothEvent.Key event = ProtoMapper.EVENT_KEY.messageToObject(request);
            DataModel.BoothEvent eventObj = localService.openEvent(event);
            ProtoModel.BoothEvent eventMsg = ProtoMapper.EVENT.objectToMessage(eventObj);
            responseObserver.onNext(eventMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error opening event: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void deleteEvent(ProtoModel.BoothEventKey request, StreamObserver<Empty> responseObserver) {
        try {
            DataModel.BoothEvent.Key key = ProtoMapper.EVENT_KEY.messageToObject(request);
            localService.deleteEvent(key);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error deleting event: {}", request, ex);
            responseObserver.onError(ex);
        }
    }
}
