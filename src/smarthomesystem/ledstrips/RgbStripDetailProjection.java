/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ledstrips;

import java.util.UUID;

/**
 *
 * @author Manel
 */
public class RgbStripDetailProjection {

    public int sequence;
    public int redPin, greenPin, bluePin;
    public UUID id;
    public String description;
    public boolean isTemporary;
    public boolean isSelected;
}
