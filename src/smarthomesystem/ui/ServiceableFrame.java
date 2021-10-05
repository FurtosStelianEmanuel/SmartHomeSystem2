/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui;

import java.awt.geom.RoundRectangle2D;
import javax.swing.JFrame;
import smarthomesystem.ui.services.FrameService;

/**
 *
 * @author Manel
 * @param <K>
 */
public abstract class ServiceableFrame<K extends FrameService> extends JFrame {

    protected K service;

    public void setService(K service) {
        this.service = service;
    }

    public K getService() {
        return service;
    }

    public final void setRoundCorners() {
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 5, 5));
    }
    
    public void appearInTheCenterOfTheScreen(){
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
