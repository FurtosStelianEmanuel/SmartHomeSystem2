/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui;

import annotations.Injectable;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import smarthomesystem.ui.frames.connection.ConnectionFrame;

/**
 *
 * @author Manel
 */
@Injectable
public class ColorPallete {

    Color c1 = new Color(21, 21, 21);
    Color c2 = new Color(48, 27, 63);
    Color c3 = new Color(60, 65, 92);
    Color c4 = new Color(180, 165, 165);

    public ColorPallete() {
        setUIColorPallete();
    }

    public Color getC1() {
        return c1;
    }

    public Color getC2() {
        return c2;
    }

    public Color getC3() {
        return c3;
    }

    public Color getC4() {
        return c4;
    }
    
    public void set(Color c1, Color c2, Color c3, Color c4) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
        setUIColorPallete();
    }

    private void setUIColorPallete() {
        UIManager.put("control", c1);
        UIManager.put("info", c1);
        UIManager.put("nimbusBase", c1);
        UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
        UIManager.put("nimbusDisabledText", c1);
        UIManager.put("nimbusFocus", c4);
        UIManager.put("nimbusGreen", new Color(176, 179, 50));
        UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
        UIManager.put("nimbusLightBackground", c2);
        UIManager.put("nimbusOrange", new Color(191, 98, 4));
        UIManager.put("nimbusRed", new Color(169, 46, 34));
        UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
        UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
        UIManager.put("Button.background", c2);
        UIManager.put("text", c4);
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ConnectionFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
