/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.lightsensors;

import java.util.UUID;

/**
 *
 * @author Manel
 * @param <K>
 */
public class LightSensorDetail<K extends SpecificValues> {

    public UUID id;
    public String name;
    public String type;
    public K specificValues;
}
