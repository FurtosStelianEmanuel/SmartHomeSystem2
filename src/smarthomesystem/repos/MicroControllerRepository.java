/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.repos;

import annotations.Injectable;
import arduino.MicroController;
import arduino.MicroControllerDetailProjection;
import data.DateTimeService;
import persistance.Repository;
import smarthomesystem.repos.dao.models.MicroControllerDao;
import java.util.Arrays;
import java.util.UUID;

/**
 *
 * @author Manel
 */
@Injectable
public class MicroControllerRepository extends Repository<MicroControllerDao> {

    private final DateTimeService dateTimeService;

    public MicroControllerRepository(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void addMicroControllers(MicroController[] microControllers) {
        for (MicroController microController : microControllers) {
            createRecord(mapToMicroControllerDao(microController));
        }
    }

    public MicroController getMicroControllerBySignature(int microControllerSignature) {
        MicroControllerDao microControllerDao = readRecord(mc -> mc.microControllerSignature == microControllerSignature);
        if (microControllerDao == null) {
            microControllerDao = readRecord(mc -> mc.microControllerSignature == 0);
        }

        return mapToMicroController(microControllerDao);
    }

    public MicroControllerDetailProjection getMicroControllerDetailProjection(UUID id) {
        MicroControllerDao microControllerDao = readRecord(id);

        return mapToMicroControllerDetailProjection(microControllerDao);
    }

    public void activateMicroController(MicroController microController) {
        MicroControllerDao microControllerDao = readRecord(mc -> mc.microControllerSignature == microController.microControllerSignature);
        microControllerDao.isInUse = true;
        microControllerDao.connectionDate = dateTimeService.getCurrentDateTime();

        updateRecord(microControllerDao);
    }

    public MicroControllerDetailProjection getActiveMicroController() {
        return mapToMicroControllerDetailProjection(readRecord(mc -> mc.isInUse));
    }

    @Override
    protected void commitRecordsToStorage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object mapToSerializedFormat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void loadRecordsFromStorage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private MicroControllerDao mapToMicroControllerDao(MicroController microController) {
        return new MicroControllerDao() {
            {
                microControllerSignature = microController.microControllerSignature;
                name = microController.name;
                noOfAnalogInputPins = microController.noOfAnalogInputPins;
                noOfDigitalIOPins = microController.noOfDigitalIOPins;
                pwmPins = Arrays.toString(microController.pwmPins);
                rxPins = Arrays.toString(microController.rxPins);
                shsVersion = microController.shsVersion;
                txPins = Arrays.toString(microController.txPins);
                id = microController.id;
            }
        };
    }

    private MicroController mapToMicroController(MicroControllerDao microControllerDao) {
        return new MicroController() {
            {
                microControllerSignature = microControllerDao.microControllerSignature;
                name = microControllerDao.name;
                noOfAnalogInputPins = microControllerDao.noOfAnalogInputPins;
                noOfDigitalIOPins = microControllerDao.noOfDigitalIOPins;
                pwmPins = parseIntegerArray(microControllerDao.pwmPins);
                rxPins = parseIntegerArray(microControllerDao.rxPins);
                shsVersion = microControllerDao.shsVersion;
                txPins = parseIntegerArray(microControllerDao.txPins);
                id = microControllerDao.id;
            }
        };
    }

    private MicroControllerDetailProjection mapToMicroControllerDetailProjection(MicroControllerDao microControllerDao) {
        return new MicroControllerDetailProjection() {
            {
                microControllerSignature = microControllerDao.microControllerSignature;
                name = microControllerDao.name;
                noOfAnalogInputPins = microControllerDao.noOfAnalogInputPins;
                noOfDigitalIOPins = microControllerDao.noOfDigitalIOPins;
                pwmPins = parseIntegerArray(microControllerDao.pwmPins);
                rxPins = parseIntegerArray(microControllerDao.rxPins);
                shsVersion = microControllerDao.shsVersion;
                txPins = parseIntegerArray(microControllerDao.txPins);
                id = microControllerDao.id;
                isInUse = microControllerDao.isInUse;
                connectionDate = microControllerDao.connectionDate;
            }
        };
    }
}