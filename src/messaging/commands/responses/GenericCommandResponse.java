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
public class GenericCommandResponse extends Response {

    public boolean isValid;

    public GenericCommandResponse(byte identifier) {
        super(identifier);
    }

    public GenericCommandResponse(byte[] rawData) {
        super(rawData[0]);
        isValid = Byte.toUnsignedInt(rawData[1]) == 1;
    }
}
