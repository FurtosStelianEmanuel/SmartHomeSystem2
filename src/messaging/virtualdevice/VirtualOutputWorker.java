/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.virtualdevice;

import java.io.IOException;
import messaging.DataSender;
import threading.BackgroundWorker;

/**
 *
 * @author Manel
 */
public class VirtualOutputWorker extends BackgroundWorker implements DataSender{

    @Override
    public void send(byte[] data) throws IOException {
        throw new UnsupportedOperationException("Method implementation should never be called, it should always be mocked");
    }
}
