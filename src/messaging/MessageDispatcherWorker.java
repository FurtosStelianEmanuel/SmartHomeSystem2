/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import java.util.logging.Level;
import java.util.logging.Logger;
import misc.Misc;
import threading.BackgroundWorker;

/**
 *
 * @author Manel
 */
public class MessageDispatcherWorker extends BackgroundWorker {

    public MessageDispatcher messageDispatcher;

    public void setMessageDispatcher(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public synchronized void run() {
        while (alive) {
            try {
                wait();
                messageDispatcher.dispatchMessages();
            } catch (InterruptedException | ClassCastException ex) {
                if (Misc.LOGGING_GUARD_OUTPUT_BUFFER_CLEARED) {
                    Logger.getLogger(MessageDispatcherWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                Logger.getLogger(MessageDispatcherWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        printTerminationMessage();
    }
}
