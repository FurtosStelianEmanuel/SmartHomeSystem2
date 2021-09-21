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
public class SetSerialSettingsCommandResponse extends Response {

    public int bufferSizeSet;
    public int timeoutSet;

    public SetSerialSettingsCommandResponse(byte[] rawData) {
        super(rawData[0]);
        bufferSizeSet = Byte.toUnsignedInt(rawData[1]);
        timeoutSet = Byte.toUnsignedInt(rawData[2]);
    }
}
