/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import annotations.Injectable;
import banana.exceptions.UnresolvableDependency;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.exceptions.PackingNotImplementedException;
import static smarthomesystem.SmartHomeSystem.container;

/**
 *
 * @author Manel
 */
@Injectable
public class ConnectionService {

    ConnectionListener connectionListener;
    RetryConnectionPolicy retryPolicy;

    public void connectTo(final BrokerConfig config, Class<? extends MessageBroker> brokerType) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UnresolvableDependency {
        if (connectionListener == null) {
            throw new IllegalAccessException();
        }

        if (retryPolicy == null) {
            retryPolicy = new RetryConnectionPolicy(0);
        }

        MessageBroker messageBroker = container.resolveDependencies(brokerType);
        new Thread() {
            @Override
            public void run() {
                try {
                    connectionListener.onInit();
                    messageBroker.initConnection(config);
                    connectionListener.onSuccess();
                } catch (IOException | IllegalArgumentException | IllegalAccessException | PackingNotImplementedException ex) {
                    while (retryPolicy.hasAvailableRetries()) {
                        try {
                            connectionListener.onRetry();
                            messageBroker.initConnection(config);
                            connectionListener.onSuccess();
                            return;
                        } catch (IOException | IllegalArgumentException | IllegalAccessException | PackingNotImplementedException ex1) {
                            Logger.getLogger(ConnectionService.class.getName()).log(Level.SEVERE, null, ex);
                            retryPolicy.connectionAttemptFailed();
                        }
                    }
                    connectionListener.onFailure();
                    Logger.getLogger(ConnectionService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    public void setRetryPolicy(RetryConnectionPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public RetryConnectionPolicy getRetryPolicy() {
        return retryPolicy;
    }

}
