/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import annotations.Injectable;
import data.Factory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manel
 */
@Injectable
public class MessageFactory extends Factory<Message> {

    private final MessageUtils messageUtils;

    public MessageFactory(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    @Override
    public Message createNewInstance() {
        return null;
    }

    @Override
    public <K> K createReflectiveInstance(Class<K> classReference) {
        byte identifier = messageUtils.getMessageIdentifierGenerator().getIdentifier(classReference);
        Message message = null;
        
        try {
            Constructor constructor = classReference.getConstructor(new Class[]{byte.class});
            message = (Message) constructor.newInstance(identifier);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(MessageFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return (K) message;
    }
}
