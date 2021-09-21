/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encoding.exceptions;

/**
 *
 * @author Manel
 */
public class UnusablePacketException extends EncodingException{
    
    public UnusablePacketException(byte[] faultyData) {
        super("Packet contains at least 2 erors and cannot be fixed", faultyData);
    }
}
