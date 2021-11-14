/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import threading.ThreadPoolSupervisor;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;
import encoding.EncodingAlgorithm;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.bluetooth.BluetoothBroker;
import messaging.bluetooth.BluetoothConfig;
import messaging.bluetooth.BluetoothModuleApiWrapper;
import messaging.bluetooth.threading.BluetoothInputWorker;
import messaging.bluetooth.threading.BluetoothOutputWorker;
import messaging.bluetooth.threading.factories.BluetoothInputWorkerFactory;
import messaging.bluetooth.threading.factories.BluetoothOutputWorkerFactory;
import messaging.exceptions.PackingNotImplementedException;
import org.junit.Test;
import smarthomesystem.TestUtils;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Manel
 */
public class BluetoothBrokerTest extends TestUtils {

    BluetoothBroker bluetoothBroker;

    EncodingAlgorithm encodingAlgorithmMock;
    ThreadPoolSupervisor threadPoolSupervisorMock;
    BluetoothInputWorkerFactory inputWorkerFactoryMock;
    BluetoothOutputWorkerFactory outputWorkerFactoryMock;
    MessageDispatcherWorker messageDispatcherWorkerMock;
    MessageDispatcher messageDispatcherMock;
    MessageUtils messageUtilsMock;
    BluetoothModuleApiWrapper bluetoothModuleApiWrapperMock;

    MessageIdentifierGenerator messageIdentifierGeneratorMock;

    public BluetoothBrokerTest() {
        encodingAlgorithmMock = mock(EncodingAlgorithm.class);
        threadPoolSupervisorMock = mock(ThreadPoolSupervisor.class);
        inputWorkerFactoryMock = mock(BluetoothInputWorkerFactory.class);
        outputWorkerFactoryMock = mock(BluetoothOutputWorkerFactory.class);
        messageDispatcherWorkerMock = mock(MessageDispatcherWorker.class);
        messageUtilsMock = mock(MessageUtils.class);
        bluetoothModuleApiWrapperMock = mock(BluetoothModuleApiWrapper.class);
        messageIdentifierGeneratorMock = mock(MessageIdentifierGenerator.class);
        messageDispatcherMock = mock(MessageDispatcher.class);

        bluetoothBroker = new BluetoothBroker(
                encodingAlgorithmMock,
                threadPoolSupervisorMock,
                inputWorkerFactoryMock,
                outputWorkerFactoryMock,
                messageDispatcherWorkerMock,
                messageDispatcherMock,
                messageUtilsMock,
                bluetoothModuleApiWrapperMock
        );
    }

    @Test
    public void initConnection_success() throws IOException {
        String macAddress = "Module-MAC-Address";
        BluetoothConfig config = new BluetoothConfig() {
            {
                setAddress(macAddress);
            }
        };
        InputStream inputStreamMock = mock(InputStream.class);
        OutputStream outputStreamMock = mock(OutputStream.class);

        try {
            when(bluetoothModuleApiWrapperMock.getInputStream()).thenReturn(inputStreamMock);
            when(bluetoothModuleApiWrapperMock.getOutputStream()).thenReturn(outputStreamMock);
            bluetoothBroker.initConnection(config);

        } catch (IOException | IllegalArgumentException ex) {
            fail(unexpectedError(ex));
        } finally {
            assertEquals(macAddress, bluetoothBroker.config.getAddress());
            verify(bluetoothModuleApiWrapperMock).connectToModule(config);
            verify(bluetoothModuleApiWrapperMock).getInputStream();
            verify(bluetoothModuleApiWrapperMock).getOutputStream();

            assertEquals(inputStreamMock, bluetoothBroker.inputStream);
            assertEquals(outputStreamMock, bluetoothBroker.outputStream);
        }
    }

    @Test
    public void startBackgroundWorkers_success() throws ThreadNotFoundException, ThreadAlreadyStartedException, IllegalArgumentException, IllegalAccessException, PackingNotImplementedException {
        BluetoothInputWorker inputWorkerMock = mock(BluetoothInputWorker.class);
        BluetoothOutputWorker outputWorkerMock = mock(BluetoothOutputWorker.class);

        when(inputWorkerFactoryMock.createNewInstance()).thenReturn(inputWorkerMock);
        when(outputWorkerFactoryMock.createNewInstance()).thenReturn(outputWorkerMock);
        when(messageUtilsMock.getMessageIdentifierGenerator()).thenReturn(messageIdentifierGeneratorMock);

        bluetoothBroker.inputStream = mock(InputStream.class);
        bluetoothBroker.outputStream = mock(OutputStream.class);

        try {
            bluetoothBroker.startBackgroundWorkers();
        } catch (ThreadNotFoundException | ThreadAlreadyStartedException ex) {
            fail(unexpectedError(ex));
        } finally {
            verify(threadPoolSupervisorMock).addThread(messageDispatcherWorkerMock);
            verify(messageDispatcherWorkerMock).setMessageDispatcher(messageDispatcherMock);

            verify(inputWorkerMock).setInputStream(any(InputStream.class));
            verify(inputWorkerMock).setEncodingAlgorithm(any(EncodingAlgorithm.class));
            verify(inputWorkerMock).initInputBuffer();
            verify(inputWorkerMock).setMessageDispatcher(messageDispatcherWorkerMock);
            verify(threadPoolSupervisorMock).addThread(inputWorkerMock);

            verify(outputWorkerMock).setOutputStream(any(OutputStream.class));
            verify(outputWorkerMock).setEncodingAlgorithm(any(EncodingAlgorithm.class));
            verify(outputWorkerMock).initOutputBuffer();
            verify(outputWorkerMock).setMessageDispatcher(messageDispatcherWorkerMock);
            verify(threadPoolSupervisorMock).addThread(outputWorkerMock);

            verify(threadPoolSupervisorMock).startThread(outputWorkerMock);
            verify(threadPoolSupervisorMock).startThread(inputWorkerMock);
            verify(threadPoolSupervisorMock).startThread(messageDispatcherWorkerMock);
        }
    }

    @Test
    public void closeConnection_success() throws IOException {
        InputStream inputStreamMock = mock(InputStream.class);
        OutputStream outputStreamMock = mock(OutputStream.class);

        bluetoothBroker.inputStream = inputStreamMock;
        bluetoothBroker.outputStream = outputStreamMock;

        try {
            bluetoothBroker.closeConnection();
        } catch (IOException ex) {
            fail(unexpectedError(ex));
        } finally {
            verify(bluetoothModuleApiWrapperMock).disconnect();
            verify(inputStreamMock).close();
            verify(outputStreamMock).close();
            verify(outputStreamMock).flush();
            verify(threadPoolSupervisorMock).terminateIOWorkers();
            verify(threadPoolSupervisorMock).removeIOWorkers();
        }
    }

    @Test
    public void send_withResponseListener_success() throws IllegalArgumentException, IllegalAccessException, PackingNotImplementedException {
        Message messageMock = mock(Message.class);
        ResponseListener responseListenerMock = mock(ResponseListener.class);
        BluetoothOutputWorker outputWorkerMock = mock(BluetoothOutputWorker.class);

        byte[] packedMessageForm = new byte[]{0};

        when(messageIdentifierGeneratorMock.getIdentifier(any(Class.class))).thenReturn((byte) 0);
        when(messageUtilsMock.getMessageIdentifierGenerator()).thenReturn(messageIdentifierGeneratorMock);
        when(messageMock.pack()).thenReturn(packedMessageForm);

        bluetoothBroker.outputWorker = outputWorkerMock;

        try {
            bluetoothBroker.send(messageMock, responseListenerMock);
        } catch (IOException | PackingNotImplementedException ex) {
            fail(unexpectedError(ex));
        } finally {
            verify(messageMock).pack();
            verify(outputWorkerMock).send(packedMessageForm);
        }
    }

    @Test
    public void send_withResponseListenerThatHasTimeout_responseListenerBeginCountdownCalled() throws IllegalArgumentException, IllegalAccessException, PackingNotImplementedException {
        Message messageMock = mock(Message.class);
        ResponseListener responseListenerMock = mock(ResponseListener.class);
        BluetoothOutputWorker outputWorkerMock = mock(BluetoothOutputWorker.class);

        byte[] packedMessageForm = new byte[]{0};

        when(messageIdentifierGeneratorMock.getIdentifier(any(Class.class))).thenReturn((byte) 0);
        when(messageUtilsMock.getMessageIdentifierGenerator()).thenReturn(messageIdentifierGeneratorMock);
        when(responseListenerMock.hasTimeout()).thenReturn(true);
        when(messageMock.pack()).thenReturn(packedMessageForm);

        bluetoothBroker.outputWorker = outputWorkerMock;

        try {
            bluetoothBroker.send(messageMock, responseListenerMock);
        } catch (IOException | PackingNotImplementedException ex) {
            fail(unexpectedError(ex));
        } finally {
            verify(messageMock).pack();
            verify(outputWorkerMock).send(packedMessageForm);
            verify(responseListenerMock).beginCountdown();
        }
    }

    @Test
    public void send_withoutResponseListener_success() throws IllegalArgumentException, IllegalAccessException, PackingNotImplementedException {
        Message messageMock = mock(Message.class);
        BluetoothOutputWorker outputWorkerMock = mock(BluetoothOutputWorker.class);

        byte[] packedMessageForm = new byte[]{0};

        when(messageIdentifierGeneratorMock.getIdentifier(any(Class.class))).thenReturn((byte) 0);
        when(messageUtilsMock.getMessageIdentifierGenerator()).thenReturn(messageIdentifierGeneratorMock);
        when(messageMock.pack()).thenReturn(packedMessageForm);

        bluetoothBroker.outputWorker = outputWorkerMock;

        try {
            bluetoothBroker.send(messageMock);
        } catch (IOException ex) {
            Logger.getLogger(BluetoothBrokerTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            verify(messageMock).pack();
            verify(outputWorkerMock).send(packedMessageForm);
        }
    }
}
