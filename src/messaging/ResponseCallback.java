/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

/**
 *
 * @author Manel
 * @param <T>
 */
public abstract class ResponseCallback<T extends Response> {

    private final Class type;

    public ResponseCallback(Class type) {
        this.type = type;
    }

    public abstract void onResponse(T response);

    public Class getType() {
        return type;
    }
}
