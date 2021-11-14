/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino;

import annotations.Injectable;
import java.util.UUID;
import smarthomesystem.repos.MicroControllerRepository;

/**
 *
 * @author Manel
 */
@Injectable
public class MicroControllerFactory {

    private final MicroControllerRepository microControllerRepository;

    public MicroControllerFactory(MicroControllerRepository microControllerRepository) {
        this.microControllerRepository = microControllerRepository;
        microControllerRepository.addMicroControllers(new MicroController[]{
            unknownMicroController,
            arduinoUno
        });
    }

    private final MicroController unknownMicroController = new MicroController() {
        {
            name = "Unknown microcontroller, using settings from Arduino Uno";
            noOfAnalogInputPins = 5;
            noOfDigitalIOPins = 13;
            pwmPins = new int[]{3, 5, 6, 10, 11};
            rxPins = new int[]{0};
            txPins = new int[]{1};
            microControllerSignature = 0;
            id = UUID.fromString("f2cd6781-f4fa-49a5-9746-f480fed84226");
        }
    };

    private final MicroController arduinoUno = new MicroController() {
        {
            name = "Arduino Uno";
            noOfAnalogInputPins = 5;
            noOfDigitalIOPins = 13;
            pwmPins = new int[]{3, 5, 6, 10, 11};
            rxPins = new int[]{0};
            txPins = new int[]{1};
            microControllerSignature = 1;
            id = UUID.fromString("ecb35e8c-32fc-4162-8144-5607cc22c9ca");
        }
    };
}
