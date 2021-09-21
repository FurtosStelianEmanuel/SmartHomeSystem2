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
public class TestCommsCommandResponse extends Response {

    public byte[] data;

    public TestCommsCommandResponse(byte[] rawData) {
        super(rawData[0]);
        data = new byte[rawData.length - 1];
        for (int i = 1; i < rawData.length; i++) {
            data[i - 1] = rawData[i];
        }
    }
}
