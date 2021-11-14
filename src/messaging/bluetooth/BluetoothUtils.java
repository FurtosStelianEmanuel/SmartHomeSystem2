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
import messaging.commands.ClearOutputBufferCommand;
import messaging.commands.responses.ClearOutputBufferCommandResponse;
import messaging.exceptions.PackingNotImplementedException;
import messaging.HotwiredDataStreamAdapter;
import messaging.MessageBroker;
import messaging.ResponseCallback;
import messaging.exceptions.CannotUnpackByteArrayException;
import smarthomesystem.SmartHomeSystem;
import static smarthomesystem.SmartHomeSystem.container;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothUtils {

    private final MessageFactory messageFactory;
    private final MessageDispatcher messageDispatcher;

    private MessageBroker messageBroker;
    private ResponseCallback responseCallback;

    public BluetoothUtils(MessageFactory messageFactory, MessageDispatcher messageDispatcher) {
        this.messageFactory = messageFactory;
        this.messageDispatcher = messageDispatcher;
    }

    public void clearArduinoCommunication(ResponseCallback responseCallback) {
        messageBroker = container.resolveDependencies(MessageBroker.class);
        this.responseCallback = responseCallback;

        messageDispatcher.setHotwiredDataStream(new HotwiredDataStreamAdapter(10000) {
            @Override
            public void onHotwiredResponse(byte[] data) {
                try {
                    arduinoCommunicationCleared(new ClearOutputBufferCommandResponse(data));
                } catch (CannotUnpackByteArrayException ex) {
                    Logger.getLogger(BluetoothUtils.class.getName()).log(Level.SEVERE, null, ex);
                    container.resolveDependencies(smarthomesystem.SmartHomeSystem.class).terminateSmartHomeSystem();
                }
            }

            @Override
            public void onResponseTimeout() {
                container.resolveDependencies(SmartHomeSystem.class).terminateSmartHomeSystem();
            }
        });

        try {
            Thread.sleep(100);
            messageBroker.send(messageFactory.createReflectiveInstance(ClearOutputBufferCommand.class));
        } catch (IOException | PackingNotImplementedException | InterruptedException ex) {
            Logger.getLogger(BluetoothUtils.class.getName()).log(Level.SEVERE, null, ex);
            container.resolveDependencies(SmartHomeSystem.class).terminateSmartHomeSystem();
        }
    }

    public void arduinoCommunicationCleared(ClearOutputBufferCommandResponse response) {
        messageDispatcher.disableHotWire();
        responseCallback.onResponse(response);

        if (response.hasBadBytes()) {
            ((BluetoothBroker) messageBroker).clearInputBuffer();
            Logger.getLogger(BluetoothUtils.class.getName()).log(Level.INFO, String.format("Bluetooth buffer has been cleared"));
        }
    }
}
