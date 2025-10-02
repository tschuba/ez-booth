package tschuba.ez.booth.services;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import tschuba.ez.booth.DataModel;
import tschuba.ez.booth.proto.ModelMocks;
import tschuba.ez.booth.proto.ProtoMapper;
import tschuba.ez.booth.proto.ProtoModel;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BoothGrpcService}.
 */
class BoothGrpcServiceTest {

    private static final RuntimeException TEST_EXCEPTION = new RuntimeException("Test exception");

    private BoothLocalService localServiceMock;
    private BoothGrpcService grpcService;
    private ModelMocks modelMocks;

    @BeforeEach
    void setUp() {
        localServiceMock = mock(BoothLocalService.class);
        grpcService = new BoothGrpcService(localServiceMock);
        modelMocks = ModelMocks.newInstance();
    }

    @Test
    void testConstructorShouldThrowIfArgIsNull() {
        assertThatNullPointerException().isThrownBy(() -> new BoothGrpcService(null));
    }

    @Test
    void testSaveShouldDelegateToLocalServiceAndComplete() {
        StreamObserver<Empty> observerMock = mock(StreamObserver.class);

        try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
            grpcService.save(modelMocks.messages.booth, observerMock);
        }

        verify(localServiceMock).save(modelMocks.objects.booth);
        assertCompletion(observerMock);
    }

    @Test
    void testSaveShouldNotifyOnError() {
        StreamObserver<Empty> observerMock = mock(StreamObserver.class);

        doThrow(TEST_EXCEPTION).when(localServiceMock).save(modelMocks.objects.booth);

        try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
            grpcService.save(modelMocks.messages.booth, observerMock);
        }

        verify(localServiceMock).save(modelMocks.objects.booth);
        assertError(observerMock);
    }

    @Test
    void testGetAllShouldDelegateToLocalServiceAndComplete() {
        Stream<DataModel.Booth> allBoothsStream = Stream.of(modelMocks.objects.booth);
        when(localServiceMock.getAll()).thenReturn(allBoothsStream);

        try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
            StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);
            grpcService.getAll(Empty.getDefaultInstance(), observerMock);

            verify(localServiceMock).getAll();
            verify(observerMock).onNext(modelMocks.messages.booth);
            assertCompletion(observerMock);
        }
    }

    @Test
    void testGetAllShouldNotifyOnError() {
        when(localServiceMock.getAll()).thenThrow(TEST_EXCEPTION);

        StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);
        grpcService.getAll(Empty.getDefaultInstance(), observerMock);

        assertError(observerMock);
    }

    @Test
    void testGetShouldDelegateToLocalServiceAndComplete() {
        StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);

        when(localServiceMock.get(modelMocks.objects.boothKey)).thenReturn(Optional.of(modelMocks.objects.booth));

        try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
            grpcService.get(modelMocks.messages.boothKey, observerMock);
        }

        verify(localServiceMock).get(modelMocks.objects.boothKey);
        verify(observerMock).onNext(modelMocks.messages.booth);
        assertCompletion(observerMock);
    }

    @Test
    void testGetShouldNotifyOnError() {
        ProtoModel.BoothKey requestMock = mock(ProtoModel.BoothKey.class);
        StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);
        DataModel.Booth.Key boothKeyMock = mock(DataModel.Booth.Key.class);

        when(localServiceMock.get(boothKeyMock)).thenThrow(TEST_EXCEPTION);

        try (MockedStatic<ProtoMapper> protoMapperMockedStatic = mockStatic(ProtoMapper.class)) {
            protoMapperMockedStatic.when(() -> ProtoMapper.messageToObject(requestMock)).thenReturn(boothKeyMock);

            grpcService.get(requestMock, observerMock);
        }

        assertError(observerMock);
    }

    @Test
    void testCloseShouldDelegateToLocalServiceAndComplete() {
        StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);

        when(localServiceMock.close(modelMocks.objects.boothKey)).thenReturn(modelMocks.objects.booth);

        try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
            grpcService.close(modelMocks.messages.boothKey, observerMock);
        }

        verify(localServiceMock).close(modelMocks.objects.boothKey);
        verify(observerMock).onNext(modelMocks.messages.booth);
        assertCompletion(observerMock);
    }

    @Test
    void testCloseShouldNotifyOnError() {
        StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);

        when(localServiceMock.close(modelMocks.objects.boothKey)).thenThrow(TEST_EXCEPTION);

        try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
            grpcService.close(modelMocks.messages.boothKey, observerMock);
        }

        assertError(observerMock);
    }

    @Test
    void testOpenShouldDelegateToLocalServiceAndComplete() {
        StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);

        when(localServiceMock.open(modelMocks.objects.boothKey)).thenReturn(modelMocks.objects.booth);

        try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
            grpcService.open(modelMocks.messages.boothKey, observerMock);
        }

        verify(localServiceMock).open(modelMocks.objects.boothKey);
        verify(observerMock).onNext(modelMocks.messages.booth);
        assertCompletion(observerMock);
    }

    @Test
    void testOpenShouldNotifyOnError() {
        StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);

        when(localServiceMock.open(modelMocks.objects.boothKey)).thenThrow(TEST_EXCEPTION);

        try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
            grpcService.open(modelMocks.messages.boothKey, observerMock);
        }

        assertError(observerMock);
    }

    private static <T> void assertCompletion(StreamObserver<T> observerMock) {
        verify(observerMock).onCompleted();
        verify(observerMock, never()).onError(any(Throwable.class));
    }

    private static <T> void assertError(StreamObserver<T> observerMock) {
        verify(observerMock).onError(TEST_EXCEPTION);
        verify(observerMock, never()).onCompleted();
    }
}
