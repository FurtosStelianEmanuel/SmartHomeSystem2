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
public abstract class TimeoutProtocol {

    private final int timeout;

    public abstract void onTimeout();

    public TimeoutProtocol(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }
}
