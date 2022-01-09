/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem;

import java.awt.Color;

/**
 *
 * @author Manel
 */
public interface RgbColorSender {

    void sendColor(Color color);

    Color getPreviousColor();
}
