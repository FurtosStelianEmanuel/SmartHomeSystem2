/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threading;

import messaging.MessageDispatcherWorker;
import encoding.EncodingAlgorithm;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Manel
 * @param <T>
 */
public abstract class BackgroundWorker<T extends EncodingAlgorithm> extends Thread {

    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected T encodingAlgorithm;
    protected boolean alive;
    protected List<Byte> inputBuffer;
    protected BlockingQueue<byte[]> outputBuffer;
    protected int sleep;
    protected MessageDispatcherWorker messageDispatcherWorker;

    protected void printTerminationMessage() {
        System.out.println(String.format("%s terminated ", getClass().getName()));
    }

    public void initInputBuffer() {
        inputBuffer = new ArrayList<>();
    }

    public void initOutputBuffer() {
        outputBuffer = new LinkedBlockingQueue<>();
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public void setEncodingAlgorithm(T encodingAlgorithm) {
        this.encodingAlgorithm = encodingAlgorithm;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setMessageDispatcher(MessageDispatcherWorker messageDispatcherWorker) {
        this.messageDispatcherWorker = messageDispatcherWorker;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void terminate() {
        alive = false;
        interrupt();
    }

    public void clearInputBuffer() {
        synchronized (this) {
            inputBuffer.clear();
        }
    }

    @Override
    public void start() {
        super.start();
        alive = true;
    }
}
