/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.commands;

import messaging.Command;

/**
 *
 * @author Manel
 */
public class DoorClosedCommand extends Command {

    public DoorClosedCommand(byte identifier) {
        super(identifier);
    }

    public DoorClosedCommand(byte[] rawData) {
        super(rawData[0]);
    }
}
