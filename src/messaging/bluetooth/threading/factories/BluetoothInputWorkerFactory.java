/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.bluetooth.threading.factories;

import annotations.Injectable;
import data.Factory;
import messaging.MessageDispatcher;
import messaging.bluetooth.threading.BluetoothInputWorker;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothInputWorkerFactory extends Factory<BluetoothInputWorker> {

    private final MessageDispatcher messageDispatcher;

    public BluetoothInputWorkerFactory(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public BluetoothInputWorker createNewInstance() {
        return new BluetoothInputWorker(messageDispatcher);
    }
}
