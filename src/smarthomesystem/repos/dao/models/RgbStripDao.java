/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.repos.dao.models;

import smarthomesystem.repos.dao.PersistedComponent;

/**
 *
 * @author Manel
 */
public class RgbStripDao extends PersistedComponent {

    public int sequence;
    public int redPin, greenPin, bluePin;
    public String description;
    public boolean isTemporary;
    public boolean isSelected;
}
