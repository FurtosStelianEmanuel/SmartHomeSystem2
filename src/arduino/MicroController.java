/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino;

import java.util.Arrays;
import java.util.UUID;

/**
 *
 * @author Manel
 */
public class MicroController {

    public String name;
    public int pwmPins[];
    public int noOfDigitalIOPins = -1;
    public int noOfAnalogInputPins = -1;
    public int rxPins[];
    public int txPins[];
    public int microControllerSignature = -1;
    public int shsVersion = -1;
    public UUID id;

    @Override
    public String toString() {
        return String.format(""
                + "Name: %s \n"
                + "Pwm pins: %s\n"
                + "NoOfDigitalIOPins: %d\n"
                + "NoOfAnalogInputPins: %d\n"
                + "RxPins: %s\n"
                + "TxPins: %s\n"
                + "MicroControllerSignature: %d\n"
                + "ShsVersion: %d\n",
                name, Arrays.toString(pwmPins), noOfDigitalIOPins, noOfAnalogInputPins, Arrays.toString(rxPins), Arrays.toString(txPins), microControllerSignature, shsVersion);
    }
}
