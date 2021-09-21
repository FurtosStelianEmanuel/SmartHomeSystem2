/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manel
 */
public class ResponseListener {

    private byte identifier;
    private final ResponseCallback responseCallback;
    private final boolean persistent;
    private final TimeoutProtocol timeoutProtocol;
    private boolean receivedResponse = false;
    private boolean timeoutOccured = false;

    public ResponseListener(ResponseCallback responseCallback){
        persistent = false;
        this.responseCallback = responseCallback;
        timeoutProtocol = null;
    }
    
    public ResponseListener(boolean persistent, ResponseCallback responseCallback) {
        this.persistent = persistent;
        this.responseCallback = responseCallback;
        timeoutProtocol = null;
    }

    public ResponseListener(boolean persistent, ResponseCallback responseCallback, TimeoutProtocol timeoutProtocol) {
        this.persistent = persistent;
        this.responseCallback = responseCallback;
        this.timeoutProtocol = timeoutProtocol;
    }

    public byte getIdentifier() {
        return identifier;
    }

    public ResponseCallback getCallback() {
        return responseCallback;
    }

    public boolean receivedResponse() {
        return receivedResponse;
    }

    public void setResponseReceived(boolean receivedResponse) {
        this.receivedResponse = receivedResponse;
    }

    public boolean timeoutOccured() {
        return timeoutOccured;
    }

    public void setTimeoutOccured(boolean timeoutOccured) {
        this.timeoutOccured = timeoutOccured;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public boolean hasTimeout() {
        return timeoutProtocol != null;
    }

    public void setIdentifier(byte identifier) {
        this.identifier = identifier;
    }

    public void beginCountdown() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!receivedResponse()) {
                    timeoutProtocol.onTimeout();
                    setTimeoutOccured(true);
                }
            }
        }, timeoutProtocol.getTimeout());
    }

    public void responseArrivedAfterTimeout(byte[] rawData) {
        Logger.getLogger(ResponseListener.class.getName()).log(Level.WARNING, String.format("Response arrived after timeout for %s data packet: %s", getCallback().getType().getName(), Arrays.toString(rawData)));
    }
}
