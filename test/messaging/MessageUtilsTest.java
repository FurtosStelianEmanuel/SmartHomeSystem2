/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Manel
 */
public class MessageUtilsTest {

    MessageUtils messageUtils;

    MessageIdentifierGenerator messageIdentifierGeneratorMock;

    public MessageUtilsTest() {
        messageIdentifierGeneratorMock = mock(MessageIdentifierGenerator.class);

        messageUtils = new MessageUtils(messageIdentifierGeneratorMock);
    }

    public static class MyCommand extends Command {

        public byte myByte;

        public MyCommand(byte[] rawData) {
            super(rawData[0]);
            myByte = rawData[1];
        }
    }

    public static class MyQuery extends Query {

        public byte skip;
        public byte take;

        public MyQuery(byte[] rawData) {
            super(rawData[0]);
            skip = rawData[1];
            take = rawData[2];
        }
    }

    public static class MyResult extends Response {

        public byte calculationResult;

        public MyResult(byte[] rawData) {
            super(rawData[0]);
            calculationResult = rawData[1];
        }
    }

    @Test
    public void unpack_commandDeserializedCorrectly() {
        MyCommand unpackedCommand = messageUtils.unpack(new byte[]{(byte) 15, (byte) 10}, MyCommand.class);

        assertEquals(15, unpackedCommand.identifier);
        assertEquals(10, unpackedCommand.myByte);
    }

    @Test
    public void unpack_queryDeserializedCorrectly() {
        MyQuery unpackedQuery = messageUtils.unpack(new byte[]{(byte) 10, (byte) 12, (byte) 13}, MyQuery.class);

        assertEquals(10, unpackedQuery.identifier);
        assertEquals(12, unpackedQuery.skip);
        assertEquals(13, unpackedQuery.take);
    }

    @Test
    public void unpack_resultDeserializedCorrectly() {
        MyResult unpackedResult = messageUtils.unpack(new byte[]{(byte) 10, (byte) 12}, MyResult.class);

        assertEquals(10, unpackedResult.identifier);
        assertEquals(12, unpackedResult.calculationResult);
    }

    @Test
    public void isUnpackable_whenRawDataContainsAnIdentifier_returnTrue() {
        when(messageIdentifierGeneratorMock.identifierExists((byte) 12)).thenReturn(true);

        boolean isUnpackable = messageUtils.isUnpackable(new byte[]{(byte) 5, (byte) 5, (byte) 5, (byte) 12});

        assertTrue(isUnpackable);
    }

    @Test
    public void isUnpackable_whenRawDataDoesNotContainAnIdentifier_returnFalse() {
        boolean isUnpackable = messageUtils.isUnpackable(new byte[]{(byte) 5, (byte) 5, (byte) 5, (byte) 12});

        assertFalse(isUnpackable);
    }
}
