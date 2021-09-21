/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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

    public byte[] pack() throws IllegalArgumentException, IllegalAccessException, PackingNotImplementedException {
        Field[] fields = getClass().getFields();
        serialisedData.clear();

        int customFieldIndex;
        byte[] byteArrayToSerialize;

        for (Field field : fields) {
            switch (field.getType().getTypeName()) {
                case "byte":
                    if (!"identifier".equals(field.getName())) {
                        serialisedData.add(field.getByte(this));
                    }
                    break;
                case "byte[]":
                    byteArrayToSerialize = (byte[]) field.get(this);
                    for (customFieldIndex = 0; customFieldIndex < byteArrayToSerialize.length; customFieldIndex++) {
                        serialisedData.add(byteArrayToSerialize[customFieldIndex]);
                    }
                    break;
                case "boolean":
                    serialisedData.add(field.getBoolean(this) ? (byte) 1 : (byte) 0);
                    break;
                case "int":
                    if (field.getInt(this) > 255 || field.getInt(this) < 0) {
                        checkForCustomImplementation(field);
                    } else {
                        serialisedData.add((byte) field.getInt(this));
                    }
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

    private void checkForCustomImplementation(Field field) throws PackingNotImplementedException, IllegalArgumentException, IllegalAccessException {
        byte[] serialisedCustomFields;
        serialisedCustomFields = packCustomFields(field.getName(), field.get(this));
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
