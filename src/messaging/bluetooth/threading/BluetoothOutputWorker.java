/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.bluetooth.threading;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.DataSender;
import threading.IOWorker;

/**
 *
 * @author Manel
 */
public class BluetoothOutputWorker extends IOWorker implements DataSender {

    @Override
    public synchronized void run() {
        while (alive) {
            try {
                wait();
                emptyOutputStream();
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(BluetoothOutputWorker.class.getName()).log(Level.SEVERE, null, ex);
                alive = false;
            }
        }

        printTerminationMessage();
    }

    @Override
    public void send(byte[] data) {
        outputBuffer.add(data);

        synchronized (this) {
            notify();
        }
    }

    private void emptyOutputStream() throws IOException, InterruptedException {
        while (!outputBuffer.isEmpty()) {
            outputStream.write((byte[]) outputBuffer.element());
            outputBuffer.remove();

            waitForDataTransfer();
        }
    }

    private void waitForDataTransfer() throws InterruptedException {
        Thread.sleep(sleep);
    }
}
