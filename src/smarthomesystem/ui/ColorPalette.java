/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui;

import annotations.Injectable;
import messaging.events.EventDispatcher;
import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import messaging.events.ColorPalleteChanged;

/**
 *
 * @author Manel
 */
@Injectable
public class ColorPalette {

    private final EventDispatcher eventDispatcher;

    public static Color primaryColor = new Color(21, 21, 21);
    private Color secondaryColor = new Color(48, 27, 63);
    private Color thirdColor = new Color(60, 65, 92);
    private Color fourthColor = new Color(180, 165, 165);

    public ColorPalette(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        setUIColorPallete();
    }

    public void set(Color primaryColor, Color secondaryColor, Color thirdColor, Color fourthColor) {
        ColorPalleteChanged event = new ColorPalleteChanged() {
            {
                setOldPrimaryColor(ColorPalette.this.primaryColor);
                setOldSecondaryColor(ColorPalette.this.secondaryColor);
                setOldThirdColor(ColorPalette.this.thirdColor);
                setOldFourthColor(ColorPalette.this.fourthColor);

                setNewPrimaryColor(primaryColor);
                setNewSecondaryColor(secondaryColor);
                setNewThirdColor(thirdColor);
                setNewFourthColor(fourthColor);
            }
        };
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.thirdColor = thirdColor;
        this.fourthColor = fourthColor;
        setUIColorPallete();
        eventDispatcher.dispatchEvent(event);
    }

    private void setUIColorPallete() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel() {
                @Override
                public UIDefaults getDefaults() {
                    UIDefaults ret = super.getDefaults();
                    ret.put("defaultFont", new Font("Times New Roman", Font.PLAIN, 18));
                    ret.put("control", primaryColor);
                    ret.put("nimbusBase", primaryColor);
                    ret.put("Panel.background", primaryColor);
                    ret.put("nimbusFocus", fourthColor);
                    ret.put("text", fourthColor);
                    ret.put("Table.alternateRowColor", lowerBrightness(secondaryColor, 0.6));
                    ret.put("Table:\"Table.cellRenderer\".background", new ColorUIResource(lowerBrightness(secondaryColor, 0.3)));
                    ret.put("Table[Enabled+Selected].textBackground", secondaryColor);
                    ret.put("TextArea.background", secondaryColor);
                    ret.put("TabbedPane.background", secondaryColor);
                    ret.put("Button.background", secondaryColor);
                    ret.put("ComboBox.background", primaryColor);
                    ret.put("ToggleButton.background", secondaryColor);
                    return ret;
                }
            });
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Color lowerBrightness(Color color, double brightness) {
        return new Color(
                (int) (color.getRed() * brightness),
                (int) (color.getGreen() * brightness),
                (int) (color.getBlue() * brightness)
        );
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public Color getThirdColor() {
        return thirdColor;
    }

    public Color getFourthColor() {
        return fourthColor;
    }
}
