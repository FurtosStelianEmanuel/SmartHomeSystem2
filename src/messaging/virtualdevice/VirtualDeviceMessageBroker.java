/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.virtualdevice;

import data.Factory;
import encoding.EncodingAlgorithm;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.BrokerConfig;
import messaging.DataSender;
import messaging.Message;
import messaging.MessageBroker;
import messaging.MessageDispatcher;
import messaging.MessageDispatcherWorker;
import messaging.MessageIdentifierGenerator;
import messaging.MessageUtils;
import messaging.ResponseListener;
import messaging.bluetooth.BluetoothUtils;
import messaging.commands.ClearOutputBufferCommand;
import messaging.commands.responses.ClearOutputBufferCommandResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.queries.MicroControllerQuery;
import smarthomesystem.queries.results.MicroControllerQueryResult;
import threading.BackgroundWorker;
import threading.ThreadPoolSupervisor;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;

/**
 *
 * @author Manel
 * @param <T>
 */
public class VirtualDeviceMessageBroker<T> extends MessageBroker {

    MessageIdentifierGenerator messageIdentifierGenerator;

    public VirtualDeviceMessageBroker(EncodingAlgorithm encodingAlgorithm,
            ThreadPoolSupervisor threadPoolSupervisor,
            Factory inputWorkerFactory,
            Factory outputWorkerFactory,
            MessageDispatcher messageDispatcher,
            MessageDispatcherWorker messageDispatcherWorker,
            MessageUtils messageUtils,
            MessageIdentifierGenerator messageIdentifierGenerator
    ) {
        super(encodingAlgorithm, threadPoolSupervisor, inputWorkerFactory, outputWorkerFactory, messageDispatcher, messageDispatcherWorker, messageUtils);
        this.messageIdentifierGenerator = messageIdentifierGenerator;
    }

    @Override
    public void initConnection(BrokerConfig config) throws IOException {
        super.initConnection(config);

        inputStream = mock(InputStream.class);
        outputStream = mock(OutputStream.class);

        try {
            Thread.sleep((int) new Random().nextInt(1000));
        } catch (InterruptedException ex) {
            Logger.getLogger(VirtualDeviceMessageBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void startBackgroundWorkers() throws ThreadNotFoundException, ThreadAlreadyStartedException {
        super.startBackgroundWorkers();

        inputWorker = mock(BackgroundWorker.class);
        inputWorker.setInputStream(inputStream);
        inputWorker.setEncodingAlgorithm(encodingAlgorithm);
        inputWorker.initInputBuffer();
        inputWorker.setMessageDispatcher(messageDispatcherWorker);

        outputWorker = mock(VirtualOutputWorker.class);
        outputWorker.setOutputStream(outputStream);
        outputWorker.setEncodingAlgorithm(encodingAlgorithm);
        outputWorker.initOutputBuffer();
        outputWorker.setMessageDispatcher(messageDispatcherWorker);

        try {
            configureMocksForIoOperations();
        } catch (IOException ex) {
            Logger.getLogger(VirtualDeviceMessageBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void configureMocksForIoOperations() throws IOException {
        DataSender dataSender = (DataSender) outputWorker;
        doAnswer(invocation -> {
            processOutputRequest(invocation.getArgument(0));
            return null;
        }).when(dataSender).send(any(byte[].class));

        doAnswer(invocation -> {
            MessageDispatcher actualMessageDispatcher = container.resolveDependencies(MessageDispatcher.class);
            actualMessageDispatcher.queueMessage(invocation.getArgument(0));
            actualMessageDispatcher.dispatchMessages();
            return null;
        }).when(messageDispatcher).queueMessage(any(byte[].class));

        doAnswer(invocation -> {
            container.resolveDependencies(MessageDispatcher.class).addListener(invocation.getArgument(0));
            return null;
        }).when(messageDispatcher).addListener(any(ResponseListener.class));
    }

    private void processOutputRequest(byte[] data) {
        if (data[0] == messageIdentifierGenerator.getIdentifier(ClearOutputBufferCommand.class)) {
            processInput(new ClearOutputBufferCommandResponse());
        } else if (data[0] == messageIdentifierGenerator.getIdentifier(MicroControllerQuery.class)) {
            processInput(new byte[]{
                messageIdentifierGenerator.getIdentifier(MicroControllerQueryResult.class),
                2,
                0
            });
        }
    }

    private void processInput(byte[] data) {
        if (data[0] == messageIdentifierGenerator.getIdentifier(MicroControllerQueryResult.class)) {
            messageDispatcher.queueMessage(data);
        }
    }

    private void processInput(Message message) {
        if (message instanceof ClearOutputBufferCommandResponse) {
            container.resolveDependencies(BluetoothUtils.class).arduinoCommunicationCleared((ClearOutputBufferCommandResponse) message);
        }
    }
}
