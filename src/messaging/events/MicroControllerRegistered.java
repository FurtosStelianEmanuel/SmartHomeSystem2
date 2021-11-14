/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.events;

import arduino.MicroController;

/**
 *
 * @author Manel
 */
public class MicroControllerRegistered extends Event {

    public MicroController microController;

    public MicroControllerRegistered() {
    }

    public MicroControllerRegistered(MicroController microController) {
        this.microController = microController;
    }
}
