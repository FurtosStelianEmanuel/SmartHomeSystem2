/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.events;

import java.awt.Color;

/**
 *
 * @author Manel
 */
public class ColorPalleteChanged extends Event {

    Color oldPrimaryColor, oldSecondaryColor, oldThirdColor, oldFourthColor;
    Color newPrimaryColor, newSecondaryColor, newThirdColor, newFourthColor;

    public ColorPalleteChanged() {
    }

    public void setOldPrimaryColor(Color oldPrimaryColor) {
        this.oldPrimaryColor = oldPrimaryColor;
    }

    public void setOldSecondaryColor(Color oldSecondaryColor) {
        this.oldSecondaryColor = oldSecondaryColor;
    }

    public void setOldThirdColor(Color oldThirdColor) {
        this.oldThirdColor = oldThirdColor;
    }

    public void setOldFourthColor(Color oldFourthColor) {
        this.oldFourthColor = oldFourthColor;
    }

    public void setNewPrimaryColor(Color newPrimaryColor) {
        this.newPrimaryColor = newPrimaryColor;
    }

    public void setNewSecondaryColor(Color newSecondaryColor) {
        this.newSecondaryColor = newSecondaryColor;
    }

    public void setNewThirdColor(Color newThirdColor) {
        this.newThirdColor = newThirdColor;
    }

    public void setNewFourthColor(Color newFourthColor) {
        this.newFourthColor = newFourthColor;
    }

    public Color getOldPrimaryColor() {
        return oldPrimaryColor;
    }

    public Color getOldSecondaryColor() {
        return oldSecondaryColor;
    }

    public Color getOldThirdColor() {
        return oldThirdColor;
    }

    public Color getOldFourthColor() {
        return oldFourthColor;
    }

    public Color getNewPrimaryColor() {
        return newPrimaryColor;
    }

    public Color getNewSecondaryColor() {
        return newSecondaryColor;
    }

    public Color getNewThirdColor() {
        return newThirdColor;
    }

    public Color getNewFourthColor() {
        return newFourthColor;
    }

}
