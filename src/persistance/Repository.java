/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistance;

import java.util.UUID;
import java.util.function.Predicate;
import smarthomesystem.repos.dao.PersistedComponent;

/**
 *
 * @author Manel
 * @param <K>
 */
public abstract class Repository<K extends PersistedComponent> extends InMemoryDataset<K> {

    public Repository() {

    }

    protected K readRecord(UUID pk) {
        return records.stream().filter(r -> r.id == pk).findAny().orElse(null);
    }

    protected K readRecord(Predicate<? super K> predicate) {
        return records.stream().filter(predicate).findAny().orElse(null);
    }

    protected void createRecord(K record) {
        records.add(record);
    }

    protected void updateRecord(K record) {
        records.set(getIndexOfRecord(record), record);
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
