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
public class TurnOffBuiltInLedCommandResponse extends Response {

    public int pinNumber;

    public TurnOffBuiltInLedCommandResponse(byte identifier) {
        super(identifier);
    }

    public TurnOffBuiltInLedCommandResponse(byte[] rawData) {
        super(rawData[0]);
        pinNumber = Byte.toUnsignedInt(rawData[1]);
    }
}
