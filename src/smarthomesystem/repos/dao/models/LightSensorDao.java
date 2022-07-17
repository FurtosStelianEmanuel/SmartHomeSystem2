/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.repos.dao.models;

import java.util.UUID;
import smarthomesystem.repos.dao.PersistedComponent;

/**
 *
 * @author Manel
 */
public class LightSensorDao extends PersistedComponent {

    public String name;
    public String type;
    public String specificValues;

    public LightSensorDao() {
    }

    public LightSensorDao(UUID id, String name, String type, String specificValues) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.specificValues = specificValues;
    }
}
