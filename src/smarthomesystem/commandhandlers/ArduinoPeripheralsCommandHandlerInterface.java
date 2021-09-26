/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.commandhandlers;

import smarthomesystem.commands.DoorOpenedCommand;

/**
 *
 * @author Manel
 */
public interface ArduinoPeripheralsCommandHandlerInterface {
    void handle(DoorOpenedCommand command);
}
