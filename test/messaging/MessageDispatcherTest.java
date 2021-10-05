/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import messaging.commands.MyTestCommand;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.junit.Test;
import static org.mockito.Mockito.when;
import smarthomesystem.TestUtils;

/**
 *
 * @author Manel
 */
public class MessageDispatcherTest extends TestUtils {

    MessageDispatcher messageDispatcher;

    MessageUtils messageUtilsMock;
    MessageDispatcherWorker messageDispatcherWorkerMock;

    MessageIdentifierGenerator messageIdentifierGeneratorMock;

    public MessageDispatcherTest() {
        messageUtilsMock = mock(MessageUtils.class);
        messageDispatcherWorkerMock = mock(MessageDispatcherWorker.class);
        messageIdentifierGeneratorMock = mock(MessageIdentifierGenerator.class);

        messageDispatcher = new MessageDispatcher(messageUtilsMock, messageDispatcherWorkerMock);
    }

    @Test
    public void dispatchMessages_when_persistentListenerPresent_messageSent_listenerNotRemoved_listenerResponseReceivedSetToTrue() {
        class MyMessage extends Response {

            public MyMessage(byte[] rawData) {
                super(rawData[0]);
            }
        }

        byte[] messageToDispatch = new byte[]{69};
        MyMessage expectedDeserializedMessage = new MyMessage(messageToDispatch);
        ResponseListener myListener = mock(ResponseListener.class);
        ResponseCallback myCallback = mock(ResponseCallback.class);
        List<ResponseListener> listeners = Arrays.asList(myListener);

        messageDispatcher.setListeners(listeners);
        messageDispatcher.setMessageToDispatch(messageToDispatch);

        when(myListener.getIdentifier()).thenReturn((byte) 69);
        when(myListener.getCallback()).thenReturn(myCallback);
        when(myListener.isPersistent()).thenReturn(true);
        when(myCallback.getType()).thenReturn(MyMessage.class);
        when(messageUtilsMock.unpack(messageToDispatch, MyMessage.class)).thenReturn(expectedDeserializedMessage);

        messageDispatcher.dispatchMessages();

        verify(myCallback).onResponse(expectedDeserializedMessage);
        verify(myListener).setResponseReceived(true);
        assertEquals(1, messageDispatcher.getListeners().size());
    }

    @Test
    public void dispatchMessages_when_responseArrivesAfterTimeout_responseArrivedAfterTimeout_called() {
        class MyMessage extends Response {

            public MyMessage(byte[] rawData) {
                super(rawData[0]);
            }
        }

        byte[] messageToDispatch = new byte[]{69};
        ResponseListener myListener = mock(ResponseListener.class);
        List<ResponseListener> listeners = Arrays.asList(myListener);

        messageDispatcher.setListeners(listeners);
        messageDispatcher.setMessageToDispatch(messageToDispatch);

        when(myListener.timeoutOccured()).thenReturn(true);
        when(myListener.getIdentifier()).thenReturn((byte) 69);
        when(myListener.isPersistent()).thenReturn(true);

        messageDispatcher.dispatchMessages();

        verify(myListener).responseArrivedAfterTimeout(messageToDispatch);

        assertEquals(1, messageDispatcher.getListeners().size());
    }

    @Test
    public void dispatchMessages_when_nonPersistentListenerPresent_messageSent_listenerRemoved() {
        class MyMessage extends Response {

            public MyMessage(byte[] rawData) {
                super(rawData[0]);
            }
        }

        byte[] messageToDispatch = new byte[]{69};
        MyMessage expectedDeserializedMessage = new MyMessage(messageToDispatch);
        ResponseListener myListener = mock(ResponseListener.class);
        ResponseCallback myCallback = mock(ResponseCallback.class);
        List<ResponseListener> listeners = new ArrayList<>();
        listeners.add(myListener);

        messageDispatcher.setListeners(listeners);
        messageDispatcher.setMessageToDispatch(messageToDispatch);

        when(myListener.getIdentifier()).thenReturn((byte) 69);
        when(myListener.getCallback()).thenReturn(myCallback);
        when(myListener.isPersistent()).thenReturn(false);
        when(myCallback.getType()).thenReturn(MyMessage.class);
        when(messageUtilsMock.unpack(messageToDispatch, MyMessage.class)).thenReturn(expectedDeserializedMessage);

        messageDispatcher.dispatchMessages();

        verify(myCallback).onResponse(expectedDeserializedMessage);
        assertEquals(0, messageDispatcher.getListeners().size());
    }

    @Test
    public void addListener_identifierFromCallbackIsSetOnListener_listenerAdded() {
        class MyMessage extends Response {

            public MyMessage(byte[] rawData) {
                super(rawData[0]);
            }
        }

        ResponseListener myListener = mock(ResponseListener.class);
        ResponseCallback myCallback = mock(ResponseCallback.class);

        when(myListener.getCallback()).thenReturn(myCallback);
        when(myListener.isPersistent()).thenReturn(false);
        when(myCallback.getType()).thenReturn(MyMessage.class);

        when(messageUtilsMock.getMessageIdentifierGenerator()).thenReturn(messageIdentifierGeneratorMock);
        when(messageIdentifierGeneratorMock.getIdentifier(MyMessage.class)).thenReturn((byte) 69);

        messageDispatcher.addListener(myListener);

        verify(myListener).setIdentifier((byte) 69);
        assertEquals(1, messageDispatcher.getListeners().size());
    }

    public static class MyCommandHandler extends CommandHandler {

        public void handle(MyTestCommand command) {
        }
    }

    @Test
    public void callHandlers_commandReceived_commandDispatchedToHandlers() throws NoSuchMethodException {
        MyCommandHandler myCommandHandler = mock(MyCommandHandler.class);
        byte[] byteRepresentation = new byte[]{(byte) 13, (byte) 12};

        MyTestCommand myCommand = new MyTestCommand(byteRepresentation);
        Method myHandleMethod = myCommandHandler.getClass().getMethod("handle", MyTestCommand.class);

        Map<Class<? extends Message>, Pair<CommandHandler, Method>> commandHandlers = new HashMap<>();
        commandHandlers.put(MyTestCommand.class, new Pair(myCommandHandler, myHandleMethod));

        when(messageUtilsMock.unpack(byteRepresentation)).thenReturn(myCommand);

        messageDispatcher.setCommandHandlers(commandHandlers);
        messageDispatcher.setMessageToDispatch(byteRepresentation);
        messageDispatcher.dispatchMessages();

        verify(myCommandHandler).handle(myCommand);
    }
}
