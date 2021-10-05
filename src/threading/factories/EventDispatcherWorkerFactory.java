/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threading.factories;

import data.Factory;
import messaging.events.threading.EventDispatcherWorker;

/**
 *
 * @author Manel
 */
public class EventDispatcherWorkerFactory extends Factory<EventDispatcherWorker>{
    
    @Override
    public EventDispatcherWorker createNewInstance() {
        return new EventDispatcherWorker();
    }
}
