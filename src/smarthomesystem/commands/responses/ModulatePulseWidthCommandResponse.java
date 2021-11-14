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
public class ModulatePulseWidthCommandResponse extends Response {

    public int pin;
    public int modulation;

    public ModulatePulseWidthCommandResponse(byte[] rawData) {
        super(rawData[0]);
        pin = Byte.toUnsignedInt(rawData[1]);
        modulation = Byte.toUnsignedInt(rawData[2]);
    }

    @Override
    public String toString() {
        return super.toString(String.format("Pin: %d -> Modulation: %d ", pin, modulation));
    }
}
