/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.bluetooth.threading.factories;

import annotations.Injectable;
import data.Factory;
import messaging.bluetooth.threading.BluetoothOutputWorker;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothOutputWorkerFactory extends Factory<BluetoothOutputWorker> {

    @Override
    public BluetoothOutputWorker createNewInstance() {
        return new BluetoothOutputWorker();
    }
}
