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
public class ModulatePulseWidthCommand extends Command{
    
    public int pin;
    public int modulation;
    
    public ModulatePulseWidthCommand(byte identifier) {
        super(identifier);
    }
}
