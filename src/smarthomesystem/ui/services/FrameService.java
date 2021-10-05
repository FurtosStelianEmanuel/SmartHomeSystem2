/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services;

import java.lang.reflect.ParameterizedType;
import smarthomesystem.ui.ServiceableFrame;

/**
 *
 * @author Manel
 * @param <K>
 */
public abstract class FrameService<K extends ServiceableFrame> {

    protected K frame;

    public Class<K> getFrameType() {
        return (Class<K>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public K getFrame() {
        return frame;
    }

    public void setFrame(K frame) {
        this.frame = frame;
    }
}
