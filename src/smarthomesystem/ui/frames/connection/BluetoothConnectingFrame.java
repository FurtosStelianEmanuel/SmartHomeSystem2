/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.frames.connection;

import annotations.Injectable;
import misc.gifblender.*;
import smarthomesystem.animation.AnimationListener;
import smarthomesystem.ui.ServiceableFrame;
import smarthomesystem.ui.services.connection.BluetoothConnectingFrameService;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothConnectingFrame extends ServiceableFrame<BluetoothConnectingFrameService> {

    public BlendOption blendOption;
    public BlendListener blendListener;
    AnimationListener animationListener;

    /**
     * Creates new form WirelessConnectingForm
     */
    public BluetoothConnectingFrame() {
        blendOption = new BlendOption(getClass().getResource("/smarthomesystem/ui/resources/connecting-wireless_300x225.gif"), 128, 139);
        blendListener = new BlendListenerAdapter() {
            @Override
            public void onSecondGifAboutToRestart() {
                animationListener.onAnimationTimeout();
            }
        };
        initComponents();
    }

    public void appendToLog(String text) {
        jTextArea1.append(text + "\n");
        jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
    }

    @Override
    public void setVisible(boolean isVisible) {
        if (isVisible) {
            gifBlender.reset();
        } else {
            appendToLog("- - - - - - - - - - - - - - - - ");
        }

        super.setVisible(isVisible);
    }

    public void showConnectedCheckmark(AnimationListener animationListener) {
        this.animationListener = animationListener;
        gifBlender.getBlendOption().setSecondGif(getClass().getResource("/smarthomesystem/ui/resources/wireless_to_checkmark.gif"), 62);
        gifBlender.triggerTransition();
    }

    public void showFailedConnectionCrossmark(AnimationListener animationListener) {
        this.animationListener = animationListener;
        gifBlender.getBlendOption().setSecondGif(getClass().getResource("/smarthomesystem/ui/resources/wireless_to_cross.gif"), 30);
        gifBlender.triggerTransition();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        gifBlender = new misc.gifblender.GifBlender(blendOption, blendListener);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout gifBlenderLayout = new javax.swing.GroupLayout(gifBlender);
        gifBlender.setLayout(gifBlenderLayout);
        gifBlenderLayout.setHorizontalGroup(
            gifBlenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        gifBlenderLayout.setVerticalGroup(
            gifBlenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 216, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gifBlender, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gifBlender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private misc.gifblender.GifBlender gifBlender;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
