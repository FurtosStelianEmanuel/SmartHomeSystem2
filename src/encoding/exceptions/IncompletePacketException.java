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
public class IncompletePacketException extends EncodingException{
    
    public IncompletePacketException(byte[] data, int packetSize){
        super(String.format("The provided data cannot form a complete %d bit packet", packetSize), data);
    }
}
