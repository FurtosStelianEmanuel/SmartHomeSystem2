/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.connection;

import annotations.Injectable;
import banana.exceptions.UnresolvableDependency;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import messaging.ConnectionListener;
import messaging.ConnectionService;
import messaging.RetryConnectionPolicy;
import messaging.bluetooth.BluetoothBroker;
import messaging.bluetooth.BluetoothConfig;
import messaging.events.EventDispatcher;
import messaging.events.models.BluetoothConnectionEstablished;
import smarthomesystem.ui.frames.connection.BluetoothConnectingFrame;
import smarthomesystem.ui.services.FrameService;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.ui.frames.connection.ConnectionFrame;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothConnectingFrameService extends FrameService<BluetoothConnectingFrame> {

    private final ConnectionService connectionService;
    private final EventDispatcher eventDispatcher;

    public BluetoothConnectingFrameService(ConnectionService connectionService, EventDispatcher eventDispatcher) {
        this.connectionService = connectionService;
        this.eventDispatcher = eventDispatcher;
    }

    public void tryToConnect(Pair<String, String> addressAndName) {
        BluetoothConfig bluetoothConfig = new BluetoothConfig();
        bluetoothConfig.setAddress(addressAndName.getKey());
        bluetoothConfig.setName(addressAndName.getValue());

        connectionService.setConnectionListener(new ConnectionListener() {
            @Override
            public void onInit() {
                frame.appearInTheCenterOfTheScreen();
                frame.appendToLog(String.format("Trying to connect to %s", bluetoothConfig.getName()));
            }

            @Override
            public void onRetry() {
                int initialRetryCount = connectionService.getRetryPolicy().getInitialRetryCount();
                frame.appendToLog(String.format("Retrying to connect to %s, attempt %d/%d", bluetoothConfig.getName(), initialRetryCount - connectionService.getRetryPolicy().getRetryCount() + 1, initialRetryCount));
            }

            @Override
            public void onSuccess() {
                frame.appendToLog(String.format("Connected to %s", bluetoothConfig.getName()));
                frame.showConnectedCheckmark(() -> {
                    frame.setVisible(false);
                    eventDispatcher.dispatchEvent(new BluetoothConnectionEstablished());
                });
            }

            @Override
            public void onFailure() {
                frame.appendToLog(String.format("Failed to connect to %s", bluetoothConfig.getName()));
                frame.showFailedConnectionCrossmark(() -> {
                    frame.setVisible(false);

                    container.resolveDependencies(ConnectionFrame.class).setVisible(true);
                });
            }
        });

        connectionService.setRetryPolicy(new RetryConnectionPolicy(3));

        connectionService.connectTo(bluetoothConfig, BluetoothBroker.class);
    }

}
