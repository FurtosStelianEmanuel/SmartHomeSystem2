/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import threading.BackgroundWorker;
import threading.ThreadPoolSupervisor;
import encoding.EncodingAlgorithm;
import data.Factory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import messaging.exceptions.PackingNotImplementedException;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;

/**
 *
 * @author Manel
 * @param <T>
 * @param <K>
 * @param <IN_FACTORY>
 * @param <OUT_FACTORY>
 * @param <IN_WORKER>
 * @param <OUT_WORKER>
 */
public abstract class MessageBroker<
        T extends BrokerConfig, K extends EncodingAlgorithm, IN_FACTORY extends Factory, OUT_FACTORY extends Factory, IN_WORKER extends BackgroundWorker, OUT_WORKER extends BackgroundWorker> implements BrokerProtocol {

    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected T config;
    protected K encodingAlgorithm;
    protected ThreadPoolSupervisor threadPoolSupervisor;
    protected IN_FACTORY inputWorkerFactory;
    protected OUT_FACTORY outputWorkerFactory;
    protected IN_WORKER inputWorker;
    protected OUT_WORKER outputWorker;
    protected MessageDispatcher messageDispatcher;
    protected MessageDispatcherWorker messageDispatcherWorker;
    protected MessageUtils messageUtils;

    public MessageBroker(
            K encodingAlgorithm,
            ThreadPoolSupervisor threadPoolSupervisor,
            IN_FACTORY inputWorkerFactory,
            OUT_FACTORY outputWorkerFactory,
            MessageDispatcher messageDispatcher,
            MessageDispatcherWorker messageDispatcherWorker,
            MessageUtils messageUtils
    ) {
        this.encodingAlgorithm = encodingAlgorithm;
        this.threadPoolSupervisor = threadPoolSupervisor;
        this.inputWorkerFactory = inputWorkerFactory;
        this.outputWorkerFactory = outputWorkerFactory;
        this.messageDispatcher = messageDispatcher;
        this.messageDispatcherWorker = messageDispatcherWorker;
        this.messageUtils = messageUtils;
    }

    public void initConnection(T config) throws IOException, IllegalArgumentException, IllegalAccessException, PackingNotImplementedException {
        this.config = config;
    }

    public void closeConnection() throws IOException {
        inputStream.close();

        outputStream.flush();
        outputStream.close();

        threadPoolSupervisor.terminateAllThreads();
    }

    @Override
    public void startBackgroundWorkers() throws ThreadNotFoundException, ThreadAlreadyStartedException {
        if (!threadPoolSupervisor.containsWorker(messageDispatcherWorker)) {
            threadPoolSupervisor.addThread(messageDispatcherWorker);
            messageDispatcherWorker.setMessageDispatcher(messageDispatcher);
        }
    }

    @Override
    public void send(Message message, ResponseListener responseListener) throws IOException, IllegalAccessException, PackingNotImplementedException {
        DataSender sender = (DataSender) outputWorker;
        handleNewListener(responseListener);
        sender.send(message.pack());
    }

    @Override
    public void send(Message message) throws IllegalAccessException, PackingNotImplementedException, IOException {
        DataSender sender = (DataSender) outputWorker;
        sender.send(message.pack());
    }

    private void handleNewListener(ResponseListener responseListener) {
        messageDispatcher.addListener(responseListener);
        if (responseListener.hasTimeout()) {
            responseListener.beginCountdown();
        }
    }
}
