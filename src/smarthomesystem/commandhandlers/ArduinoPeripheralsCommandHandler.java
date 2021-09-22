/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.commandhandlers;

import messaging.CommandHandler;
import messaging.MessageBroker;
import messaging.MessageFactory;
import messaging.Response;
import smarthomesystem.commands.DoorOpenedCommand;
import smarthomesystem.commands.responses.DoorOpenedCommandResponse;

/**
 *
 * @author Manel
 */
public class ArduinoPeripheralsCommandHandler extends CommandHandler implements ArduinoPeripheralsCommandHandlerInterface {

    private final MessageBroker broker;
    private final MessageFactory messageFactory;

    public ArduinoPeripheralsCommandHandler(MessageBroker broker, MessageFactory messageFactory) {
        this.broker = broker;
        this.messageFactory = messageFactory;
    }

    @Override
    public Response handle(DoorOpenedCommand command) {
        System.out.println("fac handle la ceva");
        
        return messageFactory.createReflectiveInstance(DoorOpenedCommandResponse.class);
    }
}
