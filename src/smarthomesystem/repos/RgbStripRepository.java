/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.repos;

import annotations.Injectable;
import bananaconvert.marshaler.exception.DeserializationException;
import bananaconvert.marshaler.exception.SerializationException;
import smarthomesystem.repos.dao.models.RgbStripSerializedFileFormat;
import data.Serializer;
import java.io.FileNotFoundException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import persistance.Repository;
import smarthomesystem.ledstrips.RgbStripDetailProjection;
import smarthomesystem.repos.dao.models.RgbStripDao;

/**
 *
 * @author Manel
 */
@Injectable
public class RgbStripRepository extends Repository<RgbStripDao> {

    private final Serializer serializer;

    public RgbStripRepository(Serializer serializer) {
        this.serializer = serializer;
        loadRecordsFromStorage();
    }

    @Override
    public void commitRecordsToStorage() throws FileNotFoundException, SerializationException {
        serializer.updateRepository(this);
    }

    @Override
    public RgbStripSerializedFileFormat mapToSerializedFormat() {
        return new RgbStripSerializedFileFormat(records);
    }

    public void addOrUpdateStrip(RgbStripDetailProjection rgbStripDetail) {
        rgbStripDetail.id = UUID.randomUUID();
        createRecord(mapToRgbStripDao(rgbStripDetail));
    }

    public RgbStripDetailProjection[] getStrips() {
        return readRecords().map(s -> mapToRgbStripDetailProjection(s)).toArray(RgbStripDetailProjection[]::new);
    }

    public RgbStripDetailProjection getSelectedStrip() {
        return mapToRgbStripDetailProjection(readRecord(s -> s.isSelected));
    }

    public RgbStripDetailProjection getStripByDescription(String description) {
        return mapToRgbStripDetailProjection(readRecord(s -> s.description.equals(description)));
    }

    public void updateStrip(RgbStripDetailProjection stripDetail) {
        updateRecord(mapToRgbStripDao(stripDetail));
    }

    public void removeTemporaryStrips() {
        deleteRecords(s -> s.isTemporary);
    }

    public void removeStrip(String description) {
        deleteRecords(s -> s.description.equals(description));
    }

    public void markStripsAsTemporary(boolean temporary) {
        records.forEach(r -> r.isTemporary = temporary);
    }

    @Override
    protected final void loadRecordsFromStorage() {
        try {
            RgbStripSerializedFileFormat deserialized = serializer.loadRepository(this);

            records = deserialized.records;
        } catch (FileNotFoundException | DeserializationException ex) {
            Logger.getLogger(RgbStripRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private RgbStripDetailProjection mapToRgbStripDetailProjection(RgbStripDao rgbStripDao) {
        if (rgbStripDao == null) {
            return null;
        }

        return new RgbStripDetailProjection() {
            {
                redPin = rgbStripDao.redPin;
                greenPin = rgbStripDao.greenPin;
                bluePin = rgbStripDao.bluePin;
                id = rgbStripDao.id;
                sequence = rgbStripDao.sequence;
                description = rgbStripDao.description;
                isTemporary = rgbStripDao.isTemporary;
                isPrimary = rgbStripDao.isSelected;
            }
        };
    }

    private RgbStripDao mapToRgbStripDao(RgbStripDetailProjection rgbStripDetailProjection) {
        return new RgbStripDao() {
            {
                redPin = rgbStripDetailProjection.redPin;
                greenPin = rgbStripDetailProjection.greenPin;
                bluePin = rgbStripDetailProjection.bluePin;
                id = rgbStripDetailProjection.id;
                sequence = rgbStripDetailProjection.sequence;
                description = rgbStripDetailProjection.description;
                isTemporary = rgbStripDetailProjection.isTemporary;
                isSelected = rgbStripDetailProjection.isPrimary;
            }
        };
    }
}