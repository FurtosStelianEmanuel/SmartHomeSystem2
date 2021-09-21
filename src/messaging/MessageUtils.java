/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import annotations.Injectable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import misc.Misc;

/**
 *
 * @author Manel
 */
@Injectable
public class MessageUtils {

    private final MessageIdentifierGenerator messageIdentifierGenerator;

    public MessageUtils(MessageIdentifierGenerator messageIdentifierGenerator) {
        this.messageIdentifierGenerator = messageIdentifierGenerator;
    }

    public <T extends Message> T unpack(byte[] serialisedMessage, Class type) {
        T message = (T) new Message(messageIdentifierGenerator.getIdentifier(type)) {
        };
        try {
            Constructor constructor = type.getConstructor(new Class[]{byte[].class});
            message = (T) constructor.newInstance(serialisedMessage);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            if (Misc.LOGGING_GUARD_OUTPUT_BUFFER_CLEARED) {
                Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return (T) message;
    }

    public boolean isUnpackable(byte[] data) {
        for (byte b : data) {
            if (messageIdentifierGenerator.identifierExists(b)) {
                return true;
            }
        }
        return false;
    }

    public MessageIdentifierGenerator getMessageIdentifierGenerator() {
        return messageIdentifierGenerator;
    }
}
