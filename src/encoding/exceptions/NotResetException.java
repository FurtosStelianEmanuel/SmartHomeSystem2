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
public class NotResetException extends EncodingException {

    public NotResetException() {
        super("Algorithm should be reset after before or after encoding/decoding", null);
    }
}
