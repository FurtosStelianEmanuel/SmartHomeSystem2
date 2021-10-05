/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.queries;

import messaging.Query;

/**
 *
 * @author Manel
 */
public class AnalogValueQuery extends Query{
    
    public int pin;
    
    public AnalogValueQuery(byte identifier) {
        super(identifier);
    }
}
