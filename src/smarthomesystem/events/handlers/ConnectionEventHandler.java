/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.events.handlers;

import banana.exceptions.UnresolvableDependency;
import java.lang.reflect.InvocationTargetException;
import messaging.bluetooth.BluetoothBroker;
import messaging.bluetooth.BluetoothUtils;
import messaging.events.EventHandler;
import messaging.events.models.*;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.ui.frames.main.IndexFrame;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;

/**
 *
 * @author Manel
 */
public class ConnectionEventHandler extends EventHandler {

    private final BluetoothUtils bluetoothUtils;

    public ConnectionEventHandler(BluetoothUtils bluetoothUtils) {
        this.bluetoothUtils = bluetoothUtils;
    }

    public void handle(BluetoothConnectionEstablished event) throws ThreadNotFoundException, ThreadAlreadyStartedException {
        container.resolveDependencies(IndexFrame.class).setVisible(true);
        container.resolveDependencies(BluetoothBroker.class).startBackgroundWorkers();
        bluetoothUtils.clearArduinoCommunication();
    }
}
