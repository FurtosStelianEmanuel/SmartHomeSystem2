/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.bluetooth;

import annotations.Injectable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.MessageDispatcher;
import messaging.MessageFactory;
import messaging.ResponseCallback;
import messaging.ResponseListener;
import messaging.TimeoutProtocol;
import messaging.commands.ClearOutputBufferCommand;
import messaging.commands.responses.ClearOutputBufferCommandResponse;
import messaging.exceptions.BufferNotClearedException;
import messaging.exceptions.PackingNotImplementedException;
import misc.Misc;
import messaging.HotwiredDataStream;
import messaging.HotwiredDataStreamAdapter;
import messaging.bluetooth.threading.BluetoothInputWorker;
import messaging.exceptions.CannotUnpackByteArrayException;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothUtils {

    private MessageFactory messageFactory;
    private BluetoothBroker bluetoothBroker;
    private MessageDispatcher messageDispatcher;

    public void setup(
            MessageFactory messageFactory,
            BluetoothBroker bluetoothBroker,
            MessageDispatcher messageDispatcher
    ) {
        this.messageDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.bluetoothBroker = bluetoothBroker;
    }

    public void clearArduinoCommunication() {
        HotwiredDataStreamAdapter hotwiredDataStreamAdapter = new HotwiredDataStreamAdapter(100000) {
            @Override
            public void onHotwiredResponse(byte[] data) {
                try {
                    ClearOutputBufferCommandResponse clearOutputBufferCommandResponse = new ClearOutputBufferCommandResponse(data);
                    if (clearOutputBufferCommandResponse.hasBadBytes()) {
                        Logger.getLogger(BluetoothUtils.class.getName()).log(Level.INFO, String.format("Bluetooth buffer cleared"));
                    }
                    
                    messageDispatcher.disableHotWire();
                    bluetoothBroker.clearInputBuffer();
                } catch (CannotUnpackByteArrayException ex) {
                    Logger.getLogger(BluetoothUtils.class.getName()).log(Level.SEVERE, null, ex);
                    fatalErrorOccured();
                }
            }

            @Override
            public void onResponseTimeout() {
                fatalErrorOccured();
            }
        };
        messageDispatcher.setHotwiredDataStream(hotwiredDataStreamAdapter);

        try {
            bluetoothBroker.send(messageFactory.createReflectiveInstance(ClearOutputBufferCommand.class));
        } catch (IllegalAccessException | PackingNotImplementedException | IOException ex) {
            Logger.getLogger(BluetoothUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fatalErrorOccured() {
        try {
            bluetoothBroker.closeConnection();
            Thread.sleep(1000);
        } catch (IOException | InterruptedException ex1) {
            Logger.getLogger(BluetoothUtils.class.getName()).log(Level.SEVERE, null, ex1);
        }
        System.exit(0);
    }
}
