/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.main.settingsframe;

import smarthomesystem.ui.frames.main.SettingsFrame;
import smarthomesystem.ui.services.main.DataHandlingException;
import smarthomesystem.ui.services.main.DataInteractionService;
import smarthomesystem.ui.services.main.TabbedFrameService;

/**
 *
 * @author Manel
 */
public class LightSensorService implements TabbedFrameService, DataInteractionService {

    @Override
    public void selectTab(SettingsFrame settingsFrame) {
        System.out.println("am fost deschis");
    }

    @Override
    public void save() throws DataHandlingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveAndExit() throws DataHandlingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void edit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(String identifier) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
