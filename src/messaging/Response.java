/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

/**
 *
 * @author Manel
 */
public class Response extends Message {

    public Response(byte identifier) {
        super(identifier);
    }

    @Override
    public String toString() {
        return String.format("MessageResponse: %s %s", getClass().getName(), serialisedData);
    }

    public String toString(String messageToInclude) {
        return String.format("%s --- %s", getClass().getSimpleName(), messageToInclude);
    }
}
