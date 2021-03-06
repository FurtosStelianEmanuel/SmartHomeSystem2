/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.bluetooth;

import annotations.Injectable;
import messaging.MessageDispatcherWorker;
import threading.ThreadPoolSupervisor;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;
import encoding.EncodingAlgorithm;
import java.io.IOException;
import messaging.MessageBroker;
import messaging.MessageDispatcher;
import messaging.MessageUtils;
import messaging.bluetooth.threading.BluetoothInputWorker;
import messaging.bluetooth.threading.BluetoothOutputWorker;
import messaging.bluetooth.threading.factories.BluetoothInputWorkerFactory;
import messaging.bluetooth.threading.factories.BluetoothOutputWorkerFactory;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothBroker extends MessageBroker<BluetoothConfig, EncodingAlgorithm, BluetoothInputWorkerFactory, BluetoothOutputWorkerFactory, BluetoothInputWorker, BluetoothOutputWorker> {

    private final BluetoothModuleApiWrapper bluetoothWrapper;

    public BluetoothBroker(
            EncodingAlgorithm encodingAlgorithm,
            ThreadPoolSupervisor threadPoolSupervisor,
            BluetoothInputWorkerFactory inputWorkerFactory,
            BluetoothOutputWorkerFactory outputWorkerFactory,
            MessageDispatcherWorker messageDispatcherWorker,
            MessageDispatcher messageDispatcher,
            MessageUtils messageUtils,
            BluetoothModuleApiWrapper bluetoothWrapper
    ) {
        super(encodingAlgorithm, threadPoolSupervisor, inputWorkerFactory, outputWorkerFactory, messageDispatcher, messageDispatcherWorker, messageUtils);
        this.bluetoothWrapper = bluetoothWrapper;
        this.messageDispatcherWorker = messageDispatcherWorker;
    }

    @Override
    public void initConnection(BluetoothConfig config) throws IOException {
        super.initConnection(config);
        bluetoothWrapper.connectToModule(config);

        outputStream = bluetoothWrapper.getOutputStream();
        inputStream = bluetoothWrapper.getInputStream();
    }

    @Override
    public void startBackgroundWorkers() throws ThreadNotFoundException, ThreadAlreadyStartedException {
        super.startBackgroundWorkers();

        inputWorker = inputWorkerFactory.createNewInstance();
        inputWorker.setInputStream(inputStream);
        inputWorker.setEncodingAlgorithm(encodingAlgorithm);
        inputWorker.initInputBuffer();
        inputWorker.setMessageDispatcher(messageDispatcherWorker);
        threadPoolSupervisor.addThread(inputWorker);

        outputWorker = outputWorkerFactory.createNewInstance();
        outputWorker.setOutputStream(outputStream);
        outputWorker.setEncodingAlgorithm(encodingAlgorithm);
        outputWorker.initOutputBuffer();
        outputWorker.setMessageDispatcher(messageDispatcherWorker);
        threadPoolSupervisor.addThread(outputWorker);

        threadPoolSupervisor.startThread(outputWorker);
        threadPoolSupervisor.startThread(inputWorker);

        if (!messageDispatcherWorker.isAlive()) {
            threadPoolSupervisor.startThread(messageDispatcherWorker);
        }
    }

    @Override
    public void closeConnection() throws IOException {
        bluetoothWrapper.disconnect();
        super.closeConnection();
    }

    void clearInputBuffer() {
        inputWorker.clearInputBuffer();
    }
}
