/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.virtualdevice;

import annotations.Injectable;
import data.Factory;
import encoding.EncodingAlgorithm;
import messaging.MessageDispatcher;
import messaging.MessageDispatcherWorker;
import messaging.MessageIdentifierGenerator;
import messaging.MessageUtils;
import static org.mockito.Mockito.mock;
import static smarthomesystem.SmartHomeSystem.container;
import threading.ThreadPoolSupervisor;

/**
 *
 * @author Manel
 */
@Injectable
public class VirtualDeviceMessageBrokerFactory extends Factory<VirtualDeviceMessageBroker> {

    @Override
    public VirtualDeviceMessageBroker createNewInstance() {
        return new VirtualDeviceMessageBroker(
                mock(EncodingAlgorithm.class),
                mock(ThreadPoolSupervisor.class),
                mock(Factory.class),
                mock(Factory.class),
                mock(MessageDispatcher.class),
                mock(MessageDispatcherWorker.class),
                mock(MessageUtils.class),
                container.resolveDependencies(MessageIdentifierGenerator.class)
        );
    }
}
