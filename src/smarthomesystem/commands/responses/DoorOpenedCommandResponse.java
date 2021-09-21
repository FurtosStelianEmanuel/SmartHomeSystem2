/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.commands.responses;

import messaging.Response;

/**
 *
 * @author Manel
 */
public class DoorOpenedCommandResponse extends Response {

    public DoorOpenedCommandResponse(byte identifier) {
        super(identifier);
    }

    public DoorOpenedCommandResponse(byte[] rawData) {
        super(rawData[0]);
    }
}
