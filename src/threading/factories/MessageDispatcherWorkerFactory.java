/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threading.factories;

import annotations.Injectable;
import messaging.MessageDispatcherWorker;
import data.Factory;

/**
 *
 * @author Manel
 */
@Injectable
public class MessageDispatcherWorkerFactory extends Factory<MessageDispatcherWorker> {

    @Override
    public MessageDispatcherWorker createNewInstance() {
        return new MessageDispatcherWorker();
    }
}
