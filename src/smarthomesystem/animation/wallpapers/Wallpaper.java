/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.animation.wallpapers;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

/**
 *
 * @author Manel
 */
public abstract class Wallpaper extends JPanel {

    protected Graphics2D painter;
    protected Graphics2D savedState;
    RenderingHints renderingHints = new RenderingHints(
            new HashMap<Key, Object>() {
        {
            put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
    });

    private final Timer animator;

    public Wallpaper() {
        if (this instanceof AnimatedWallpaper) {
            TimerTask animationTask = new TimerTask() {
                @Override
                public void run() {
                    if (!isShowing()) {
                        return;
                    }

                    ((AnimatedWallpaper) (Wallpaper.this)).animate();
                }
            };

            animator = new Timer(true);
            animator.scheduleAtFixedRate(animationTask, 0, 10);
        } else {
            animator = null;
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onSizeSet();
            }
        });
    }

    protected abstract void paintWallpaper();

    protected abstract void onSizeSet();

    protected void paintLayers(Layer[] layers) {
        for (Layer layer : layers) {
            if (!layer.shouldShow()) {
                continue;
            }

            pushState();
            layer.show();
            popState();
        }
    }

    private void pushState() {
        savedState = (Graphics2D) painter.create();
    }

    private void popState() {
        painter = savedState;
    }

    @Override
    protected void paintComponent(Graphics g) {
        painter = (Graphics2D) g;
        painter.setRenderingHints(renderingHints);
        paintWallpaper();
    }
}