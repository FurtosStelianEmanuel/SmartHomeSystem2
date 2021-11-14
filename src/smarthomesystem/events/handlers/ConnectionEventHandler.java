/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.events.handlers;

import arduino.MicroController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.MessageBroker;
import messaging.MessageFactory;
import messaging.ResponseCallback;
import messaging.ResponseListener;
import messaging.bluetooth.BluetoothUtils;
import messaging.commands.responses.ClearOutputBufferCommandResponse;
import messaging.events.EventDispatcher;
import messaging.events.EventHandler;
import messaging.events.MicroControllerRegistered;
import messaging.events.models.*;
import messaging.exceptions.PackingNotImplementedException;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.events.models.BluetoothConnectionReady;
import smarthomesystem.queries.MicroControllerQuery;
import smarthomesystem.queries.results.MicroControllerQueryResult;
import smarthomesystem.repos.MicroControllerRepository;
import smarthomesystem.ui.frames.connection.ConnectionFrame;
import smarthomesystem.ui.frames.main.IndexFrame;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;

/**
 *
 * @author Manel
 */
public class ConnectionEventHandler extends EventHandler {

    private final BluetoothUtils bluetoothUtils;
    private final EventDispatcher eventDispatcher;
    private final MessageFactory messageFactory;
    private final MicroControllerRepository microControllerRepository;

    public ConnectionEventHandler(
            BluetoothUtils bluetoothUtils,
            EventDispatcher eventDispatcher,
            MessageFactory messageFactory,
            MicroControllerRepository microControllerRepository
            ) {
        this.bluetoothUtils = bluetoothUtils;
        this.eventDispatcher = eventDispatcher;
        this.messageFactory = messageFactory;
        this.microControllerRepository = microControllerRepository;
    }

    public void handle(BluetoothConnectionEstablished event) throws ThreadNotFoundException, ThreadAlreadyStartedException {
        container.resolveDependencies(MessageBroker.class).startBackgroundWorkers();
        bluetoothUtils.clearArduinoCommunication(new ResponseCallback<ClearOutputBufferCommandResponse>(ClearOutputBufferCommandResponse.class) {
            @Override
            public void onResponse(ClearOutputBufferCommandResponse response) {
                try {
                    handleClearOutputBufferCommandResponse(response);
                } catch (IOException | PackingNotImplementedException ex) {
                    Logger.getLogger(ConnectionEventHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void handle(BluetoothConnectionLost event) throws IOException {
        container.resolveDependencies(MessageBroker.class).closeConnection();
        container.resolveDependencies(IndexFrame.class).setVisible(false);
        container.resolveDependencies(ConnectionFrame.class).setVisible(true);
    }

    private void handleClearOutputBufferCommandResponse(ClearOutputBufferCommandResponse response) throws IOException, PackingNotImplementedException {
        MicroControllerQuery microControllerQuery = messageFactory.createReflectiveInstance(MicroControllerQuery.class);
        MessageBroker broker = container.resolveDependencies(MessageBroker.class);
        
        broker.send(microControllerQuery, new ResponseListener(new ResponseCallback<MicroControllerQueryResult>(MicroControllerQueryResult.class) {
            @Override
            public void onResponse(MicroControllerQueryResult response) {
                MicroController microController = microControllerRepository.getMicroControllerBySignature(response.microControllerSignature);
                microController.shsVersion = response.shsVersion;

                eventDispatcher.dispatchEvents(
                        new BluetoothConnectionReady(),
                        new MicroControllerRegistered(microController)
                );
            }
        }));
    }
}
