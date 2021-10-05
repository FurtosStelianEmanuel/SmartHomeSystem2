/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.main;

import annotations.Injectable;
import banana.exceptions.UnresolvableDependency;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import messaging.MessageDispatcher;
import messaging.MessageFactory;
import messaging.ResponseCallback;
import messaging.ResponseListener;
import messaging.bluetooth.BluetoothBroker;
import messaging.exceptions.PackingNotImplementedException;
import smarthomesystem.queries.results.DistanceSensorQueryResult;
import smarthomesystem.ui.frames.main.IndexFrame;
import smarthomesystem.ui.services.FrameService;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.commands.ModulatePulseWidthCommand;
import smarthomesystem.queries.AnalogValueQuery;
import smarthomesystem.queries.results.AnalogValueQueryResult;

/**
 *
 * @author Manel
 */
@Injectable
public class IndexFrameService extends FrameService<IndexFrame> {

    private final MessageFactory messageFactory;

    int count = 0;

    private final ResponseListener distanceSensorQueryResult = new ResponseListener(true, new ResponseCallback<DistanceSensorQueryResult>(DistanceSensorQueryResult.class) {
        @Override
        public void onResponse(DistanceSensorQueryResult response) {
            System.out.println("result " + response.distance + count);
        }
    });

    public IndexFrameService(MessageFactory messageFactory, MessageDispatcher messageDispatcher) {
        this.messageFactory = messageFactory;
        messageDispatcher.addListener(distanceSensorQueryResult);
    }

    public void sendColor() throws IOException, IllegalAccessException, PackingNotImplementedException, NoSuchMethodException, InvocationTargetException, InstantiationException, UnresolvableDependency {
        BluetoothBroker meinBroker = container.resolveDependencies(BluetoothBroker.class);
        ModulatePulseWidthCommand command = messageFactory.createReflectiveInstance(ModulatePulseWidthCommand.class);
        command.modulation = frame.jSlider1.getValue();
        command.pin = 6;
        meinBroker.send(command);
    }

    public void getLightAmount() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, UnresolvableDependency, PackingNotImplementedException, IOException {
        BluetoothBroker meinBroker = container.resolveDependencies(BluetoothBroker.class);
        AnalogValueQuery analogPinQuery = messageFactory.createReflectiveInstance(AnalogValueQuery.class);
        analogPinQuery.pin = 0;
        meinBroker.send(analogPinQuery, new ResponseListener(new ResponseCallback<AnalogValueQueryResult>(AnalogValueQueryResult.class) {
            @Override
            public void onResponse(AnalogValueQueryResult response) {
                System.out.println("lumina la " + response.value);
            }
        }));
    }
}
