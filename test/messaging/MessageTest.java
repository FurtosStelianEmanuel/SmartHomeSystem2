/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import messaging.exceptions.PackingNotImplementedException;
import static org.junit.Assert.*;
import org.junit.Test;
import smarthomesystem.TestUtils;

/**
 *
 * @author Manel
 */
public class MessageTest extends TestUtils {

    @Test
    public void pack_whenFieldTypesAreSupported_fieldsSerializedCorrectly() {
        class CustomMessage extends Message {

            public byte myByte;
            public boolean myBoolean;
            public int myInt;
            public byte[] myByteArray;

            public CustomMessage(byte identifier) {
                super(identifier);
            }
        }
        CustomMessage message = new CustomMessage((byte) 100);
        message.myByte = (byte) 13;
        message.myBoolean = false;
        message.myInt = 69;
        message.myByteArray = new byte[]{(byte) 13, (byte) 17};

        try {
            byte[] result = message.pack();
            assertEquals(6, result.length);
            assertEquals(100, result[0]);
            assertEquals(13, result[1]);
            assertEquals(0, result[2]);
            assertEquals(69, result[3]);
            assertEquals(13, result[4]);
            assertEquals(17, result[5]);
        } catch (IllegalArgumentException | IllegalAccessException | PackingNotImplementedException ex) {
            fail(unexpectedError(ex));
        }
    }

    @Test
    public void pack_whenFieldTypeUnsupportedAndNoCustomPackerImplemented_exceptionThrown() {
        class CustomMessage extends Message {

            public byte myByte;
            public String myUnsupportedString;

            public CustomMessage(byte identifier) {
                super(identifier);
            }
        }

        CustomMessage message = new CustomMessage((byte) 100);
        try {
            message.pack();
            fail(expectedErrorShouldHaveOccured(PackingNotImplementedException.class));
        } catch (IllegalArgumentException | IllegalAccessException | PackingNotImplementedException ex) {
            
        }
    }

    @Test
    public void pack_whenFieldTypeUnsupportedAndCustomPackerImplemented_fieldsSerializedCorrectly() {

        class CustomField {

            byte myByte;

            public CustomField(byte myByte) {
                this.myByte = myByte;
            }
        }

        class CustomMessage extends Message {

            public CustomField customField;

            public CustomMessage(byte identifier) {
                super(identifier);
            }

            @Override
            protected byte[] packCustomFields(String fieldName, Object value) {
                switch (fieldName) {
                    case "customField":
                        CustomField obj = (CustomField) value;
                        return new byte[]{obj.myByte};
                }
                return super.packCustomFields(fieldName, value);
            }
        }

        CustomMessage message = new CustomMessage((byte) 100);
        message.customField = new CustomField((byte) 15);

        try {
            byte[] result = message.pack();
            byte[] expectedResult = new byte[]{(byte) 100, (byte) 15};
            assertArrayEquals(expectedResult, result);
        } catch (IllegalArgumentException | IllegalAccessException | PackingNotImplementedException ex) {
            fail(unexpectedError(ex));
        }
    }
}
