/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.events.handlers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.MessageBroker;
import messaging.MessageFactory;
import messaging.ResponseCallback;
import messaging.ResponseListener;
import messaging.events.EventHandler;
import messaging.exceptions.PackingNotImplementedException;
import smarthomesystem.commands.SetSerialSettingsCommand;
import smarthomesystem.events.models.*;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.commands.responses.SetSerialSettingsCommandResponse;
import smarthomesystem.ui.frames.main.IndexFrame;

/**
 *
 * @author Manel
 */
public class IndexFrameEventHandler extends EventHandler {

    private final MessageFactory messageFactory;

    public IndexFrameEventHandler(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public void handle(IndexFrameOpened event) {
        SetSerialSettingsCommand setSerialSettings = messageFactory.createReflectiveInstance(SetSerialSettingsCommand.class);
        setSerialSettings.bufferSize = 8;
        setSerialSettings.timeout = 5;

        try {
            MessageBroker messageBroker = container.resolveDependencies(MessageBroker.class);
            messageBroker.send(setSerialSettings, new ResponseListener(new ResponseCallback<SetSerialSettingsCommandResponse>(SetSerialSettingsCommandResponse.class) {
                @Override
                public void onResponse(SetSerialSettingsCommandResponse response) {
                    System.out.println("Set buffer " + response.bufferSizeSet + " timeout " + response.timeoutSet);
                }
            }));
        } catch (IOException | PackingNotImplementedException ex) {
            Logger.getLogger(IndexFrameEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void handle(IndexFrameClosed event) {
        System.out.println("s a inchis");
    }

    public void handle(BluetoothConnectionReady event) {
        IndexFrame indexFrame = container.resolveDependencies(IndexFrame.class);
        indexFrame.appearInTheCenterOfTheScreen();
    }
}
