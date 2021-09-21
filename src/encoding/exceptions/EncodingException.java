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
public class EncodingException extends Exception {

    protected byte[] faultyData;

    public EncodingException(String message, byte[] faultyData) {
        super(message);
        this.faultyData = faultyData;
    }

    public EncodingException(byte[] faultyData) {
        this.faultyData = faultyData;
    }

    public byte[] getFaultyData() {
        return faultyData;
    }
}
