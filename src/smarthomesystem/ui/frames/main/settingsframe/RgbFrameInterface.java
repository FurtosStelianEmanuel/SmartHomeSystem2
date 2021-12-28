/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.frames.main.settingsframe;

import javax.swing.JComboBox;
import smarthomesystem.ledstrips.RgbStripDetailProjection;

/**
 *
 * @author Manel
 */
public interface RgbFrameInterface {

    void setPinsForColorChannel(JComboBox comboBox, int[] pins);

    void selectStripPreset(Object description);

    void setSelectedStrip(RgbStripDetailProjection selectedStrip);

    void setLedStripsDescriptions(RgbStripDetailProjection[] strips);

    void setPresetSettingsEnabled(boolean enabled);

    void setSaveEnabled(boolean saveEnabled);
}
