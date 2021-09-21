/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.commands.responses;

import messaging.Response;

/**
 *
 * @author Manel
 */
public class TurnOnBuiltInLedCommandResponse extends Response {

    public int pinNumber;

    public TurnOnBuiltInLedCommandResponse(byte identifier) {
        super(identifier);
    }

    public TurnOnBuiltInLedCommandResponse(byte[] rawData) {
        super(rawData[0]);
        pinNumber = Byte.toUnsignedInt(rawData[1]);
    }
}
