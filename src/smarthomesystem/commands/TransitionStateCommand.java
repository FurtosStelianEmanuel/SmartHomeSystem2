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
public class TransitionStateCommand extends Command {

    public int desiredState;

    public TransitionStateCommand(byte identifier) {
        super(identifier);
    }
}
