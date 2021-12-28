/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistance;

import bananaconvert.marshaler.exception.SerializationException;
import java.io.FileNotFoundException;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import smarthomesystem.repos.dao.PersistedComponent;

/**
 *
 * @author Manel
 * @param <K>
 */
public abstract class Repository<K extends PersistedComponent> extends InMemoryDataset<K> {

    public abstract Object getSerializedFormat();

    protected abstract void commitRecordsToStorage() throws FileNotFoundException, SerializationException;

    protected abstract void loadRecordsFromStorage();

    protected K readRecord(UUID pk) {
        return records.stream().filter(r -> r.id == pk).findAny().orElse(null);
    }

    protected K readRecord(Predicate<? super K> predicate) {
        return records.stream().filter(predicate).findAny().orElse(null);
    }

    protected Stream<K> readRecords() {
        return records.stream();
    }

    protected void createRecord(K record) {
        records.add(record);
    }

    protected void updateRecord(K record) {
        records.set(getIndexOfRecord(record), record);
    }

    protected void deleteRecords(Predicate<? super K> predicate) {
        records.removeIf(predicate);
    }

    protected int[] parseIntegerArray(String s) {
        String[] splitted = s.replace("[", "").replace("]", "").split(",");
        int[] toReturn = new int[splitted.length];

        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = Integer.parseInt(splitted[i].trim());
        }

        return toReturn;
    }

    private int getIndexOfRecord(K record) {
        K persistedRecord = readRecord(record.id);
        return records.indexOf(persistedRecord);
    }
}
