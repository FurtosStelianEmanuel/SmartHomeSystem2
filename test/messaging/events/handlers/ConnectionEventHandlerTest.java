/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.events.handlers;

import banana.InjectorInterface;
import messaging.MessageBroker;
import messaging.MessageFactory;
import messaging.ResponseCallback;
import messaging.bluetooth.BluetoothUtils;
import messaging.events.EventDispatcher;
import messaging.events.models.BluetoothConnectionEstablished;
import static org.junit.Assert.fail;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import smarthomesystem.TestUtils;
import smarthomesystem.events.handlers.ConnectionEventHandler;
import smarthomesystem.repos.MicroControllerRepository;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;

/**
 *
 * @author Manel
 */
public class ConnectionEventHandlerTest extends TestUtils {

    BluetoothUtils bluetoothUtilsMock;
    EventDispatcher eventDispatcherMock;
    MicroControllerRepository microControllerRepositoryMock;
    MessageFactory messageFactoryMock;

    ConnectionEventHandler eventHandler;

    InjectorInterface containerMock;

    public ConnectionEventHandlerTest() {
        bluetoothUtilsMock = mock(BluetoothUtils.class);
        eventDispatcherMock = mock(EventDispatcher.class);
        microControllerRepositoryMock = mock(MicroControllerRepository.class);
        messageFactoryMock = mock(MessageFactory.class);

        eventHandler = new ConnectionEventHandler(bluetoothUtilsMock, eventDispatcherMock, messageFactoryMock, microControllerRepositoryMock);

        containerMock = mock(InjectorInterface.class);
        smarthomesystem.SmartHomeSystem.container = containerMock;
    }

    @Test
    public void bluetoothConnectionEstablished_success() {
        BluetoothConnectionEstablished event = new BluetoothConnectionEstablished();

        MessageBroker messageBroker = mock(MessageBroker.class);

        when(containerMock.resolveDependencies(MessageBroker.class)).thenReturn(messageBroker);

        try {
            eventHandler.handle(event);
        } catch (ThreadNotFoundException | ThreadAlreadyStartedException ex) {
            fail(unexpectedError(ex));
        } finally {
            verify(containerMock).resolveDependencies(MessageBroker.class);
            verify(bluetoothUtilsMock).clearArduinoCommunication(any(ResponseCallback.class));
        }
    }
}
