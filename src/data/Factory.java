/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author Manel
 * @param <T>
 */
public abstract class Factory<T> {

    public abstract T createNewInstance();

    public <K> K createReflectiveInstance(Class<K> classReference) {
        throw new UnsupportedOperationException();
    }
}
