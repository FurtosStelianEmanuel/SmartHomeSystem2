/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.commands;

import messaging.Command;

/**
 *
 * @author Manel
 */
public class TestCommsCommand extends Command{
    
    public byte[] data;
    
    public TestCommsCommand(byte identifier) {
        super(identifier);
        data = new byte[63];
    }
}
