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
public class MyTestCommand3 extends Command{

    int number;

    public MyTestCommand3(byte[] rawData) {
        super(rawData[0]);
        number = Byte.toUnsignedInt(rawData[1]);
    }
}
