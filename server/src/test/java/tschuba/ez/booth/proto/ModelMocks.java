package tschuba.ez.booth.proto;

import org.mockito.MockedStatic;
import tschuba.ez.booth.DataModel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * Mocks for data and proto models, as well as the ProtoMapper static methods.
 */
public class ModelMocks {

    public final ObjectMocks objects = new ObjectMocks();
    public final MessageMocks messages = new MessageMocks();

    private ModelMocks() {
    }

    /**
     * @return a new ModelMocks instance
     */
    public static ModelMocks newInstance() {
        return new ModelMocks();
    }

    /**
     * @return a MockedStatic for ProtoMapper with predefined behaviors
     */
    public MockedStatic<ProtoMapper> mapper() {
        MockedStatic<ProtoMapper> protoMapperMockedStatic = mockStatic(ProtoMapper.class);

        protoMapperMockedStatic.when(() -> ProtoMapper.messageToObject(messages.booth)).thenReturn(objects.booth);
        protoMapperMockedStatic.when(() -> ProtoMapper.messageToObject(messages.boothKey)).thenReturn(objects.boothKey);
        protoMapperMockedStatic.when(() -> ProtoMapper.objectToMessage(objects.booth)).thenReturn(messages.booth);
        protoMapperMockedStatic.when(() -> ProtoMapper.objectToMessage(objects.boothKey)).thenReturn(messages.boothKey);

        return protoMapperMockedStatic;
    }

    /**
     * Mocks for {@link DataModel} objects.
     */
    public static class ObjectMocks {
        private ObjectMocks() {
        }

        public final DataModel.Booth.Key boothKey = mock(DataModel.Booth.Key.class);
        public final DataModel.Booth booth = mock(DataModel.Booth.class);
    }

    /**
     * Mocks for {@link ProtoModel} messages.
     */
    public static class MessageMocks {
        private MessageMocks() {
        }

        public final ProtoModel.BoothKey boothKey = mock(ProtoModel.BoothKey.class);
        public final ProtoModel.Booth booth = mock(ProtoModel.Booth.class);
    }
}
