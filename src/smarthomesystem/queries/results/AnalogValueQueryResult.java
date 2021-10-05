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
public class AnalogValueQueryResult extends Response {

    public int value;

    public AnalogValueQueryResult(byte identifier) {
        super(identifier);
    }

    public AnalogValueQueryResult(byte[] rawData) {
        super(rawData[0]);
        value = Byte.toUnsignedInt(rawData[1]);
    }
}
