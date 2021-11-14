/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc.gifblender;

import java.awt.Graphics;
import static misc.gifblender.BlendState.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 *
 * @author Manel
 */
public class GifBlender extends JPanel {

    private BlendOption blendOption;
    private BlendState state;
    private Graphics2D g2;
    private long frameCount;
    private final BlendListener blendListener;
    private boolean secondGifWasShown = false;

    public GifBlender() {
        this.blendOption = null;
        this.blendListener = null;
    }

    public GifBlender(BlendOption blendOption, BlendListener blendListener) {
        this.blendOption = blendOption;
        this.blendListener = blendListener;
        state = ShowingFirstGif;
    }

    public GifBlender(BlendOption blendOption) {
        this.blendOption = blendOption;
        blendListener = null;
        state = ShowingFirstGif;
    }

    public void setBlendOption(BlendOption blendOption) {
        this.blendOption = blendOption;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (blendOption == null) {
            g.fillRect(0, 0, getWidth(), getHeight());
            return;
        }
        super.paintComponent(g);

        g2 = (Graphics2D) g;
        initGraphicsQuality();

        switch (state) {
            case ShowingFirstGif:
                drawGif(blendOption.getFirstGif());
                break;
            case InTransitionToSecondGif:
                if (frameCount != 0) {
                    drawGif(blendOption.getFirstGif());
                } else {
                    drawGif(blendOption.getSecondGif());
                    state = ShowingSecondGif;
                    frameCount = 0;
                }
                break;
            case ShowingSecondGif:
                drawGif(blendOption.getSecondGif());
                break;
        }

        iterateFrameCount();
        checkListenerEvents();
    }

    public void triggerTransition() {
        if (state == ShowingSecondGif) {
            return;
        }

        state = InTransitionToSecondGif;
        repaint();
    }

    public BlendOption getBlendOption() {
        return blendOption;
    }

    public void reset() {
        state = ShowingFirstGif;
        frameCount = 0;
        secondGifWasShown = false;
        
        blendOption.getFirstGif().flush();
        if (blendOption.getSecondGif() != null) {
            blendOption.getSecondGif().flush();
        }
    }

    private void checkListenerEvents() {
        checkSecondGifRestart();

        if (frameCount != 0 || blendListener == null) {
            return;
        }

        switch (state) {
            case ShowingFirstGif:
                blendListener.onFirstGifShowing();
                break;
            case InTransitionToSecondGif:
                blendListener.onTransitionToSecondGif();
                break;
            case ShowingSecondGif:
                if (!secondGifWasShown) {
                    blendListener.onSecondGifShowing();
                    secondGifWasShown = true;
                }
                break;
        }
    }

    private void iterateFrameCount() {
        frameCount++;

        switch (state) {
            case ShowingFirstGif:
                frameCount %= blendOption.getFirstGifTotalFrameCount();
                break;
            case InTransitionToSecondGif:
                frameCount %= blendOption.getFirstGifCutoutFrame();
                break;
            default:
                frameCount %= blendOption.getSecondGifCutoutFrame();
                break;
        }
    }

    private void checkSecondGifRestart() {
        if (!secondGifWasShown) {
            return;
        }

        if (frameCount == blendOption.getSecondGifCutoutFrame() - 1) {
            blendListener.onSecondGifAboutToRestart();
        }
    }

    private void initGraphicsQuality() {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void drawGif(Image gif) {
        g2.translate(getWidth() / 2 - gif.getWidth(this) / 2, 0);
        g2.drawImage(gif, 0, 0, this);
    }
}
