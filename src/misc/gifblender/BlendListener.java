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
public interface BlendListener {

    void onFirstGifShowing();

    void onTransitionToSecondGif();

    void onSecondGifShowing();

    void onSecondGifAboutToRestart();
}
