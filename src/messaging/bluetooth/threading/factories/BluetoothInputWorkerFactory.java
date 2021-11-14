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
import messaging.events.EventDispatcher;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothInputWorkerFactory extends Factory<BluetoothInputWorker> {

    private final MessageDispatcher messageDispatcher;
    private final EventDispatcher eventDispatcher;

    public BluetoothInputWorkerFactory(MessageDispatcher messageDispatcher, EventDispatcher eventDispatcher) {
        this.messageDispatcher = messageDispatcher;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public BluetoothInputWorker createNewInstance() {
        return new BluetoothInputWorker(messageDispatcher, eventDispatcher);
    }
}
