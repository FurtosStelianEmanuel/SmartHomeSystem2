/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import annotations.Injectable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static smarthomesystem.SmartHomeSystem.container;

/**
 *
 * @author Manel
 */
@Injectable
public class ConnectionService {

    ConnectionListener connectionListener;
    RetryConnectionPolicy retryPolicy;

    public void connectTo(final BrokerConfig config) {
        if (retryPolicy == null) {
            retryPolicy = new RetryConnectionPolicy(0);
        }

        new Thread() {
            @Override
            public void run() {
                connectionListener.onInit();

                while (!tryToConnect(config) && retryPolicy.hasAvailableRetries()) {
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

    private boolean tryToConnect(BrokerConfig config) {
        MessageBroker messageBroker = container.resolveDependencies(MessageBroker.class);

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
