/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.repos.dao.models;

import java.time.LocalDateTime;
import smarthomesystem.repos.dao.PersistedComponent;

/**
 *
 * @author Manel
 */
public class MicroControllerDao extends PersistedComponent {

    public String name;
    public String pwmPins;
    public int noOfDigitalIOPins = -1;
    public int noOfAnalogInputPins = -1;
    public String rxPins;
    public String txPins;
    public int microControllerSignature = -1;
    public int shsVersion = -1;
    public boolean isInUse = false;
    public LocalDateTime connectionDate;
}
