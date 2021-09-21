/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;
import java.io.IOException;
import messaging.exceptions.PackingNotImplementedException;

/**
 *
 * @author Manel
 */
interface BrokerProtocol {

    void send(Message message, ResponseListener responseListener) throws IOException, IllegalAccessException, PackingNotImplementedException;

    void send(Message message) throws IOException, IllegalAccessException, PackingNotImplementedException;

    void startBackgroundWorkers() throws ThreadNotFoundException, ThreadAlreadyStartedException;
}
