/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JColorChooser;

/**
 *
 * @author Manel
 */
public class Misc {

    public static final int CLEAR_BUFFER_NR_CONFIRMATION_BYTES = 10;
    public static final String EMPTY_MAC_ADDRESS = "000000000000";
    public static final String VIRTUAL_DEVICE_NAME = "VirtualDevice";

    public static long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static Color chooseColor(Component component, String message, Color color) {
        return JColorChooser.showDialog(component, message, color);
    }
}