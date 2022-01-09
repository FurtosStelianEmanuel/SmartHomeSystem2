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
public class StripTransitionedToColorCommand extends Command {

    public int red, green, blue;
    public int redPin, greenPin, bluePin;

    public StripTransitionedToColorCommand(byte identifier) {
        super(identifier);
    }

    public StripTransitionedToColorCommand(byte[] rawData) {
        super(rawData[0]);
    }
}
