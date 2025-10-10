/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.services;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.*;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import tschuba.ez.booth.model.DataModel;
import tschuba.ez.booth.model.ModelMocks;
import tschuba.ez.booth.model.ProtoMapper;
import tschuba.ez.booth.proto.ProtoModel;

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
      grpcService.saveBooth(modelMocks.messages.booth, observerMock);
    }

    verify(localServiceMock).saveBooth(modelMocks.objects.booth);
    verify(observerMock).onNext(any(Empty.class));
    assertCompletion(observerMock);
  }

  @Test
  void testSaveShouldNotifyOnError() {
    StreamObserver<Empty> observerMock = mock(StreamObserver.class);

    doThrow(TEST_EXCEPTION).when(localServiceMock).saveBooth(modelMocks.objects.booth);

    try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
      grpcService.saveBooth(modelMocks.messages.booth, observerMock);
    }

    verify(localServiceMock).saveBooth(modelMocks.objects.booth);
    assertError(observerMock);
  }

  @Test
  void testGetAllShouldDelegateToLocalServiceAndComplete() {
    Stream<DataModel.Booth> allBoothsStream = Stream.of(modelMocks.objects.booth);
    when(localServiceMock.findAll()).thenReturn(allBoothsStream);

    try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
      StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);
      grpcService.getAllBooths(Empty.getDefaultInstance(), observerMock);

      verify(localServiceMock).findAll();
      verify(observerMock).onNext(modelMocks.messages.booth);
      assertCompletion(observerMock);
    }
  }

  @Test
  void testGetAllShouldNotifyOnError() {
    when(localServiceMock.findAll()).thenThrow(TEST_EXCEPTION);

    StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);
    grpcService.getAllBooths(Empty.getDefaultInstance(), observerMock);

    assertError(observerMock);
  }

  @Test
  void testGetShouldDelegateToLocalServiceAndComplete() {
    StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);

    when(localServiceMock.findById(modelMocks.objects.boothKey))
        .thenReturn(Optional.of(modelMocks.objects.booth));

    try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
      grpcService.getBooth(modelMocks.messages.boothKey, observerMock);
    }

    verify(localServiceMock).findById(modelMocks.objects.boothKey);
    verify(observerMock).onNext(modelMocks.messages.booth);
    assertCompletion(observerMock);
  }

  @Test
  void testGetShouldNotifyOnError() {
    ProtoModel.BoothKey requestMock = mock(ProtoModel.BoothKey.class);
    StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);
    DataModel.Booth.Key boothKeyMock = mock(DataModel.Booth.Key.class);

    when(localServiceMock.findById(boothKeyMock)).thenThrow(TEST_EXCEPTION);

    try (MockedStatic<ProtoMapper> protoMapperMockedStatic = mockStatic(ProtoMapper.class)) {
      protoMapperMockedStatic
          .when(() -> ProtoMapper.messageToObject(requestMock))
          .thenReturn(boothKeyMock);

      grpcService.getBooth(requestMock, observerMock);
    }

    assertError(observerMock);
  }

  @Test
  void testCloseShouldDelegateToLocalServiceAndComplete() {
    StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);

    when(localServiceMock.close(modelMocks.objects.boothKey)).thenReturn(modelMocks.objects.booth);

    try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
      grpcService.closeBooth(modelMocks.messages.boothKey, observerMock);
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
      grpcService.closeBooth(modelMocks.messages.boothKey, observerMock);
    }

    assertError(observerMock);
  }

  @Test
  void testOpenShouldDelegateToLocalServiceAndComplete() {
    StreamObserver<ProtoModel.Booth> observerMock = mock(StreamObserver.class);

    when(localServiceMock.open(modelMocks.objects.boothKey)).thenReturn(modelMocks.objects.booth);

    try (MockedStatic<ProtoMapper> _ = modelMocks.mapper()) {
      grpcService.openBooth(modelMocks.messages.boothKey, observerMock);
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
      grpcService.openBooth(modelMocks.messages.boothKey, observerMock);
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
