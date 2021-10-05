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
public class RetryConnectionPolicy {

    private int retryCount;
    private int initialRetryCount;

    public RetryConnectionPolicy(int retryCount) {
        this.retryCount = retryCount;
        this.initialRetryCount = retryCount;
    }

    public boolean hasAvailableRetries() {
        return retryCount > 0;
    }

    public void consumeRetryAttempt() {
        retryCount--;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        initialRetryCount = retryCount;
    }

    public int getInitialRetryCount() {
        return initialRetryCount;
    }
}
