/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc.gifblender;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author Manel
 */
public class BlendOption {

    private final Image firstGif;
    private Image secondGif;
    private final int firstGifCutoutFrame;
    private int secondGifCutoutFrame;
    private final int firstGifTotalFrameCount;

    public BlendOption(URL firstGifPath, URL secondGifPath, int firstGifCutoutFrame, int firstGifTotalCount, int secondGifCutoutFrame) {
        firstGif = new ImageIcon(firstGifPath).getImage();
        secondGif = new ImageIcon(secondGifPath).getImage();
        this.firstGifCutoutFrame = firstGifCutoutFrame;
        this.secondGifCutoutFrame = secondGifCutoutFrame;
        this.firstGifTotalFrameCount = firstGifCutoutFrame;

    }

    public BlendOption(URL firstGifPath, int firstGifCutoutFrame, int firstGifTotalCount) {
        firstGif = new ImageIcon(firstGifPath).getImage();
        this.firstGifCutoutFrame = firstGifCutoutFrame;
        this.firstGifTotalFrameCount = firstGifTotalCount;
    }

    public Image getFirstGif() {
        return firstGif;
    }

    public Image getSecondGif() {
        return secondGif;
    }

    public int getFirstGifCutoutFrame() {
        return firstGifCutoutFrame;
    }

    public int getSecondGifCutoutFrame() {
        return secondGifCutoutFrame;
    }

    public void setSecondGif(URL secondGifPath, int secondGifCutoutFrame) {
        secondGif = new ImageIcon(secondGifPath).getImage();
        this.secondGifCutoutFrame = secondGifCutoutFrame;
    }

    public void setSecondGifCutoutFrame(int secondGifCutoutFrame) {
        this.secondGifCutoutFrame = secondGifCutoutFrame;
    }

    public int getFirstGifTotalFrameCount() {
        return firstGifTotalFrameCount;
    }
}
