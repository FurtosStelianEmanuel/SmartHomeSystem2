/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.commandhandlers;

import messaging.CommandHandler;
import messaging.MessageBroker;
import messaging.MessageFactory;
import messaging.events.EventDispatcher;
import smarthomesystem.commands.DoorOpenedCommand;
import smarthomesystem.events.models.DoorOpened;

/**
 *
 * @author Manel
 */
public class ArduinoPeripheralsCommandHandler extends CommandHandler implements ArduinoPeripheralsCommandHandlerInterface {

    private final MessageBroker broker;
    private final MessageFactory messageFactory;
    private final EventDispatcher eventDispatcher;

    public ArduinoPeripheralsCommandHandler(MessageBroker broker, MessageFactory messageFactory, EventDispatcher eventDispatcher) {
        this.broker = broker;
        this.messageFactory = messageFactory;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void handle(DoorOpenedCommand command) {
        eventDispatcher.dispatchEvent(new DoorOpened());
    }
}
