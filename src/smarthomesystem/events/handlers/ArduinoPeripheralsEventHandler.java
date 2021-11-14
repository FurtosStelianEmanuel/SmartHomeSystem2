/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.events.handlers;

import messaging.events.EventHandler;
import messaging.events.MicroControllerRegistered;
import smarthomesystem.events.models.*;
import smarthomesystem.repos.MicroControllerRepository;

/**
 *
 * @author Manel
 */
public class ArduinoPeripheralsEventHandler extends EventHandler {

    private final MicroControllerRepository microControllerRepository;

    public ArduinoPeripheralsEventHandler(MicroControllerRepository microControllerRepository) {
        this.microControllerRepository = microControllerRepository;
    }

    public void handle(DoorOpened event) {
        System.out.println("facutam handle si la event");
    }

    public void handle(MicroControllerRegistered event) {
        microControllerRepository.activateMicroController(event.microController);
    }
}
