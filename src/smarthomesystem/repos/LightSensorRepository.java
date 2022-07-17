/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.repos;

import bananaconvert.BananaConvert;
import bananaconvert.marshaler.exception.SerializationException;
import java.io.FileNotFoundException;
import persistance.Repository;
import smarthomesystem.lightsensors.LightSensorDetail;
import smarthomesystem.repos.dao.models.LightSensorDao;

/**
 *
 * @author Manel
 */
public class LightSensorRepository extends Repository<LightSensorDao> {

    private final BananaConvert bananaConvert;

    public LightSensorRepository(BananaConvert bananaConvert) {
        this.bananaConvert = bananaConvert;
    }

    @Override
    public Object mapToSerializedFormat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void commitRecordsToStorage() throws FileNotFoundException, SerializationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void loadRecordsFromStorage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addLightSensor(LightSensorDetail lightSensor) {
        createRecord(mapToLightSensorDao(lightSensor));
    }

    private LightSensorDao mapToLightSensorDao(LightSensorDetail lightSensor) {
        String specificValues;
        try {
            specificValues = bananaConvert.serializeToJson(lightSensor.specificValues);
        } catch (SerializationException ex) {
            specificValues = "";
        }

        return new LightSensorDao(lightSensor.id, lightSensor.name, lightSensor.type, specificValues);
    }
}
