/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.exceptions.PackingNotImplementedException;

/**
 *
 * @author Manel
 */
public abstract class Message {

    public byte identifier;
    private static final byte[] UNIMPLEMENTED_CUSTOM_FIELD_PACKER = new byte[0];
    List<Byte> serialisedData;

    public Message(byte identifier) {
        this.identifier = identifier;
        serialisedData = new ArrayList<>();
    }

    public byte[] pack() throws PackingNotImplementedException {
        Field[] fields = getClass().getFields();
        serialisedData.clear();

        for (Field field : fields) {
            switch (field.getType().getTypeName()) {
                case "byte":
                    if (!"identifier".equals(field.getName())) {
                        parseByte(field);
                    }
                    break;
                case "byte[]":
                    parseByteArray(field);
                    break;
                case "boolean":
                    parseBoolean(field);
                    break;
                case "int":
                    parseInt(field);
                    break;
                default:
                    checkForCustomImplementation(field);
            }
        }

        serialisedData.add(0, identifier);
        byte[] packedData = new byte[serialisedData.size()];
        for (int i = 0; i < serialisedData.size(); i++) {
            packedData[i] = serialisedData.get(i);
        }

        return packedData;
    }

    private void parseByte(Field field) {
        try {
            byte value = field.getByte(this);
            serialisedData.add(value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void parseByteArray(Field field) {
        byte[] byteArrayToSerialize = new byte[0];

        try {
            byteArrayToSerialize = (byte[]) field.get(this);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < byteArrayToSerialize.length; i++) {
            serialisedData.add(byteArrayToSerialize[i]);
        }
    }

    private void parseBoolean(Field field) {
        try {
            serialisedData.add(field.getBoolean(this) ? (byte) 1 : (byte) 0);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void parseInt(Field field) throws PackingNotImplementedException {
        int value = 0;

        try {
            value = field.getInt(this);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (value > 255 || value < 0) {
            checkForCustomImplementation(field);
        } else {
            serialisedData.add((byte) value);
        }
    }

    private void checkForCustomImplementation(Field field) throws PackingNotImplementedException {
        byte[] serialisedCustomFields;
        Object value = null;

        try {
            value = field.get(this);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }

        serialisedCustomFields = packCustomFields(field.getName(), value);
        if (serialisedCustomFields == UNIMPLEMENTED_CUSTOM_FIELD_PACKER) {
            throw new PackingNotImplementedException(field.getType().getTypeName(), field.getName());
        }
        for (int customFieldIndex = 0; customFieldIndex < serialisedCustomFields.length; customFieldIndex++) {
            serialisedData.add(serialisedCustomFields[customFieldIndex]);
        }
    }

    protected byte[] packCustomFields(String fieldName, Object value) {
        return UNIMPLEMENTED_CUSTOM_FIELD_PACKER;
    }
}
