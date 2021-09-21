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
public class DistanceSensorQueryResult extends Response {

    public final int distance;

    public DistanceSensorQueryResult(byte[] rawData) {
        super(rawData[0]);
        distance = Byte.toUnsignedInt(rawData[1]);
    }
}
