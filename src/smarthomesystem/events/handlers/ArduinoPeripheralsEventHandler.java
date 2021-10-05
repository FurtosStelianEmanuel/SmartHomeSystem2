/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.events.handlers;

import messaging.events.EventHandler;
import smarthomesystem.events.models.*;

/**
 *
 * @author Manel
 */
public class ArduinoPeripheralsEventHandler extends EventHandler {


    public ArduinoPeripheralsEventHandler() {
    }

    public void handle(DoorOpened event) {
        System.out.println("facutam handle si la event");
    }
}
