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

    public void connectTo(final BrokerConfig config, Class<? extends MessageBroker> brokerType) {
        if (retryPolicy == null) {
            retryPolicy = new RetryConnectionPolicy(0);
        }

        new Thread() {
            @Override
            public void run() {
                connectionListener.onInit();

                while (!tryToConnect(config, brokerType) && retryPolicy.hasAvailableRetries()) {
                    connectionListener.onRetry();
                    retryPolicy.consumeRetryAttempt();
                }

                if (!retryPolicy.hasAvailableRetries()) {
                    connectionListener.onFailure();
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

    private boolean tryToConnect(BrokerConfig config, Class<? extends MessageBroker> brokerType) {
        MessageBroker messageBroker = container.resolveDependencies(brokerType);

        try {
            messageBroker.initConnection(config);
            connectionListener.onSuccess();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ConnectionService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
