/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.bluetooth;

import annotations.Injectable;
import banana.exceptions.UnresolvableDependency;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.MessageDispatcher;
import messaging.MessageFactory;
import messaging.commands.ClearOutputBufferCommand;
import messaging.commands.responses.ClearOutputBufferCommandResponse;
import messaging.exceptions.PackingNotImplementedException;
import messaging.HotwiredDataStreamAdapter;
import messaging.exceptions.CannotUnpackByteArrayException;
import static smarthomesystem.SmartHomeSystem.container;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothUtils {

    private final MessageFactory messageFactory;
    private final MessageDispatcher messageDispatcher;

    private BluetoothBroker bluetoothBroker;

    public BluetoothUtils(MessageFactory messageFactory, MessageDispatcher messageDispatcher) {
        this.messageFactory = messageFactory;
        this.messageDispatcher = messageDispatcher;
    }

    public void clearArduinoCommunication() {
        try {
            bluetoothBroker = container.resolveDependencies(BluetoothBroker.class);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | UnresolvableDependency ex) {
            Logger.getLogger(BluetoothUtils.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        messageDispatcher.setHotwiredDataStream(new HotwiredDataStreamAdapter(10000) {
            @Override
            public void onHotwiredResponse(byte[] data) {
                try {
                    ClearOutputBufferCommandResponse clearOutputBufferCommandResponse = new ClearOutputBufferCommandResponse(data);
                    if (clearOutputBufferCommandResponse.hasBadBytes()) {
                        Logger.getLogger(BluetoothUtils.class.getName()).log(Level.INFO, String.format("Bluetooth buffer needs to be cleared"));
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
        });

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
