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
import messaging.bluetooth.threading.BluetoothInputWorker;
import messaging.commands.ClearOutputBufferCommand;
import messaging.commands.responses.ClearOutputBufferCommandResponse;
import messaging.exceptions.BufferNotClearedException;
import messaging.exceptions.PackingNotImplementedException;
import misc.Misc;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothUtils {

    private MessageFactory messageFactory;
    private BluetoothBroker bluetoothBroker;
    private BluetoothInputWorker inputWorker;
    private MessageDispatcher messageDispatcher;

    public void setup(
            MessageFactory messageFactory,
            BluetoothBroker bluetoothBroker,
            BluetoothInputWorker inputWorker,
            MessageDispatcher messageDispatcher
    ) {
        this.messageDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.bluetoothBroker = bluetoothBroker;
        this.inputWorker = inputWorker;
    }

    public void clearArduinoCommunication() {
        try {
            Misc.LOGGING_GUARD_OUTPUT_BUFFER_CLEARED = false;
            bluetoothBroker.send(messageFactory.createReflectiveInstance(ClearOutputBufferCommand.class), new ResponseListener(
                    false,
                    new ResponseCallback<ClearOutputBufferCommandResponse>(ClearOutputBufferCommandResponse.class) {
                @Override
                public void onResponse(ClearOutputBufferCommandResponse commandResponse) {
                    inputWorker.clearInputBuffer();
                    messageDispatcher.setPartialMessagesEnabled(false);
                    Misc.LOGGING_GUARD_OUTPUT_BUFFER_CLEARED = true;
                }
            }, new TimeoutProtocol(10000) {
                @Override
                public void onTimeout() {
                    if (!Misc.LOGGING_GUARD_OUTPUT_BUFFER_CLEARED) {
                        try {
                            throw new BufferNotClearedException();
                        } catch (BufferNotClearedException ex) {
                            Logger.getLogger(BluetoothBroker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        fatalErrorOccured();
                    }
                }
            }));
        } catch (IOException | IllegalAccessException | PackingNotImplementedException ex) {
            Logger.getLogger(BluetoothUtils.class.getName()).log(Level.SEVERE, null, ex);
            fatalErrorOccured();
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
