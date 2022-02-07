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
public class SetColorSmoothlyCommand extends Command {

    public int stripType;
    public int argbDataPin;
    public int redPin, greenPin, bluePin;
    public int increment;
    public int currentRed, currentGreen, currentBlue;
    public int targetRed, targetGreen, targetBlue;
    public boolean takeCurrentValuesFromSubRoutine;
    
    public SetColorSmoothlyCommand(byte identifier) {
        super(identifier);
    }

    public SetColorSmoothlyCommand(byte[] rawData) {
        super(rawData[0]);
    }
}
