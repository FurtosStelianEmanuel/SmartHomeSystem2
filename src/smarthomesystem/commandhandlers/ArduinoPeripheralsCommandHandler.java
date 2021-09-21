/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.commandhandlers;

import messaging.CommandHandler;
import messaging.MessageBroker;
import smarthomesystem.commands.DoorOpenedCommand;

/**
 *
 * @author Manel
 */
public class ArduinoPeripheralsCommandHandler extends CommandHandler implements ArduinoPeripheralsCommandHandlerInterface {

    private final MessageBroker broker;

    public ArduinoPeripheralsCommandHandler(MessageBroker broker) {
        this.broker = broker;
    }

    @Override
    public void handle(DoorOpenedCommand command) {
        System.out.println("fac handle la ceva");
    }
}
