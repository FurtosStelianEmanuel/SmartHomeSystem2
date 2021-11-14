/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc.gifblender;

/**
 *
 * @author Manel
 */
public abstract class BlendListenerAdapter implements BlendListener {

    @Override
    public void onFirstGifShowing() {
    }

    @Override
    public void onTransitionToSecondGif() {
    }

    @Override
    public void onSecondGifShowing() {
    }

    @Override
    public void onSecondGifAboutToRestart() {
    }
}
