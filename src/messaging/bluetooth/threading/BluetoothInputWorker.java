/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.bluetooth.threading;

import threading.BackgroundWorker;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.MessageDispatcher;
import smarthomesystem.Main;

/**
 *
 * @author Manel
 */
public class BluetoothInputWorker extends BackgroundWorker {

    public final static int BUFFER_SIZE = 64;
    private final MessageDispatcher messageDispatcher;

    public BluetoothInputWorker(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void run() {
        while (alive) {
            try {
                consumeInputStream();
            } catch (IOException ex) {
                Logger.getLogger(BluetoothInputWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        printTerminationMessage();
    }

    private void consumeInputStream() throws IOException {
        int readValue = inputStream.read();
        if (readValue != -1) {
            inputBuffer.add(readValue);
            byte[] toDispatch;
            if (messageDispatcher.arePartialMessagesEnabled() && inputBuffer.size() < BUFFER_SIZE) {
                toDispatch = new byte[inputBuffer.size()];
                for (int i = 0; i < toDispatch.length; i++) {
                    toDispatch[i] = (byte) (int) inputBuffer.get(i);
                }
                messageDispatcher.queuePartialMessage(toDispatch);
            } else if (inputBuffer.size() == BUFFER_SIZE) {
                toDispatch = new byte[BUFFER_SIZE];
                for (int i = 0; i < toDispatch.length; i++) {
                    toDispatch[i] = (byte) (int) inputBuffer.get(i);
                }
                messageDispatcher.queueMessage(toDispatch);
                inputBuffer.clear();
            }
        }
    }
}
