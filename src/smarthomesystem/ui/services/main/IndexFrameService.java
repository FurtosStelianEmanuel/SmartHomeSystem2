/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.main;

import annotations.Injectable;
import java.io.IOException;
import messaging.MessageBroker;
import messaging.MessageDispatcher;
import messaging.MessageFactory;
import messaging.ResponseCallback;
import messaging.ResponseListener;
import messaging.exceptions.PackingNotImplementedException;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.queries.results.DistanceSensorQueryResult;
import smarthomesystem.ui.frames.main.IndexFrame;
import smarthomesystem.ui.services.FrameService;
import smarthomesystem.commands.ModulatePulseWidthCommand;
import smarthomesystem.commands.responses.ModulatePulseWidthCommandResponse;
import smarthomesystem.queries.AnalogValueQuery;
import smarthomesystem.queries.results.AnalogValueQueryResult;

/**
 *
 * @author Manel
 */
@Injectable
public class IndexFrameService extends FrameService<IndexFrame> {

    private final MessageFactory messageFactory;
    private final ResponseListener modulatePulseWidthCommandResponseListener;

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
        modulatePulseWidthCommandResponseListener = new ResponseListener(true, new ResponseCallback<ModulatePulseWidthCommandResponse>(ModulatePulseWidthCommandResponse.class) {
            @Override
            public void onResponse(ModulatePulseWidthCommandResponse response) {
                System.out.println(response);
            }
        });
    }

    public void sendColor() throws IOException, PackingNotImplementedException {
        MessageBroker meinBroker = container.resolveDependencies(MessageBroker.class);
        ModulatePulseWidthCommand command = messageFactory.createReflectiveInstance(ModulatePulseWidthCommand.class);
        command.modulation = frame.jSlider1.getValue();
        command.pin = 6;
        meinBroker.send(command, modulatePulseWidthCommandResponseListener);
    }

    public void getLightAmount() throws IOException, PackingNotImplementedException {
        MessageBroker meinBroker = container.resolveDependencies(MessageBroker.class);
        AnalogValueQuery analogPinQuery = messageFactory.createReflectiveInstance(AnalogValueQuery.class);
        analogPinQuery.pin = 0;
        meinBroker.send(analogPinQuery, new ResponseListener(new ResponseCallback<AnalogValueQueryResult>(AnalogValueQueryResult.class) {
            @Override
            public void onResponse(AnalogValueQueryResult response) {
                System.out.println("lumina la " + response.value);
            }
        }));
    }

    public void sendSetColorCommands() throws IOException, PackingNotImplementedException {
        MessageBroker meinBroker = container.resolveDependencies(MessageBroker.class);
        ModulatePulseWidthCommand command = messageFactory.createReflectiveInstance(ModulatePulseWidthCommand.class);
        command.pin = 6;
        int countis = 2;
        int jump = 5;
        while (countis > 0) {
            for (int i = 0; i <= 255; i += jump) {
                command.modulation = i;
                meinBroker.send(command);
            }
            for (int i = 255; i >= 0; i -= jump) {
                command.modulation = i;
                meinBroker.send(command);
            }
            countis--;
        }
    }
}
