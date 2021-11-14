/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.bluetooth.threading;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.MessageDispatcher;
import messaging.events.EventDispatcher;
import messaging.events.models.BluetoothConnectionLost;
import threading.IOWorker;

/**
 *
 * @author Manel
 */
public class BluetoothInputWorker extends IOWorker {

    public final static int BUFFER_SIZE = 64;
    private final MessageDispatcher messageDispatcher;
    private final EventDispatcher eventDispatcher;

    public BluetoothInputWorker(MessageDispatcher messageDispatcher, EventDispatcher eventDispatcher) {
        this.messageDispatcher = messageDispatcher;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void run() {
        while (alive) {
            try {
                consumeInputStream();
            } catch (IOException ex) {
                Logger.getLogger(BluetoothInputWorker.class.getName()).log(Level.SEVERE, null, ex);
                alive = false;
            }
        }

        printTerminationMessage();
        eventDispatcher.dispatchEvent(new BluetoothConnectionLost());
    }

    private void consumeInputStream() throws IOException {
        int readValue = inputStream.read();
        if (readValue != -1) {
            inputBuffer.add(readValue);
            byte[] data = getBufferAsArray();
            if (data.length < BUFFER_SIZE) {
                return;
            }

            messageDispatcher.queueMessage(data);
            inputBuffer.clear();
        }
    }

    private byte[] getBufferAsArray() {
        byte[] toReturn = new byte[inputBuffer.size()];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = (byte) (int) inputBuffer.get(i);
        }

        return toReturn;
    }
}
