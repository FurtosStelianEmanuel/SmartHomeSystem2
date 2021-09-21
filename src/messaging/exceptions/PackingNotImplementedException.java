/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.exceptions;

/**
 *
 * @author Manel
 */
public class PackingNotImplementedException extends Exception {

    public PackingNotImplementedException(String type, String name) {
        super(String.format("Variable %s of type %s could not be serialised into a byte array", name, type));
    }
}
