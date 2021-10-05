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
import messaging.commands.responses.ClearOutputBufferCommandResponse;
import messaging.exceptions.CannotUnpackByteArrayException;

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

    public <T extends Message> T unpack(byte[] serializedMessage, Class type) {
        T message = (T) new Message(messageIdentifierGenerator.getIdentifier(type)) {
        };
        try {
            Constructor constructor = type.getConstructor(new Class[]{byte[].class});
            message = (T) constructor.newInstance(serializedMessage);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }

        return (T) message;
    }

    public <T extends Message> T unpack(byte[] serializedMessage) {
        Class<Message> c = messageIdentifierGenerator.getTypeFromIdentifier(serializedMessage[0]);
        if (c == null) {
            return null;
        }

        return unpack(serializedMessage, c);
    }

    public boolean isUnpackable(byte[] data) {
        for (byte b : data) {
            if (messageIdentifierGenerator.identifierExists(b)) {
                return true;
            }
        }

        return false;
    }

    public ClearOutputBufferCommandResponse getClearOutputBufferCommandResponseFromBadPacket(byte[] badPacket) throws CannotUnpackByteArrayException {
        return new ClearOutputBufferCommandResponse(badPacket);
    }

    public MessageIdentifierGenerator getMessageIdentifierGenerator() {
        return messageIdentifierGenerator;
    }
}
