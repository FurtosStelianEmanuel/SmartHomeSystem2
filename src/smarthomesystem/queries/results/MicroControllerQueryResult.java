/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.queries.results;

import messaging.Response;

/**
 *
 * @author Manel
 */
public class MicroControllerQueryResult extends Response {

    public int microControllerSignature = -1;
    public int shsVersion = -1;

    public MicroControllerQueryResult(byte[] rawData) {
        super(rawData[0]);
        microControllerSignature = Byte.toUnsignedInt(rawData[1]);
        shsVersion = Byte.toUnsignedInt(rawData[2]);
    }
}
