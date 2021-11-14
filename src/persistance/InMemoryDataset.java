/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistance;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Manel
 * @param <K>
 */
public abstract class InMemoryDataset<K> {

    protected List<K> records;

    public InMemoryDataset() {
        records = new ArrayList<>();
    }
}
