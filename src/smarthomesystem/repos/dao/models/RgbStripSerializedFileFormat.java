/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.ArrayList;
import java.util.List;
import smarthomesystem.repos.dao.models.RgbStripDao;

/**
 *
 * @author Manel
 */
public class RgbStripSerializedFileFormat {

    public List<RgbStripDao> records;

    public RgbStripSerializedFileFormat() {
        this.records = new ArrayList<>();
    }

    public RgbStripSerializedFileFormat(List<RgbStripDao> records) {
        this.records = records;
    }
}
