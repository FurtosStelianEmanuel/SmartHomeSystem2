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
public abstract class HotwiredDataStreamAdapter implements HotwiredDataStream {

    private int timeout;
    private boolean enabled;

    public HotwiredDataStreamAdapter(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
