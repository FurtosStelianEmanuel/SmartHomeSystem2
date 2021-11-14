/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * @author Manel
 */
public class MicroControllerDetailProjection {

    public String name;
    public int pwmPins[];
    public int noOfDigitalIOPins = -1;
    public int noOfAnalogInputPins = -1;
    public int rxPins[];
    public int txPins[];
    public int microControllerSignature = -1;
    public int shsVersion = -1;
    public UUID id;
    public boolean isInUse = false;
    public LocalDateTime connectionDate;
}
