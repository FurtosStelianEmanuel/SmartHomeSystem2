/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Manel
 */
public class MessageFactoryTest {

    MessageFactory messageFactory;

    MessageUtils messageUtilsMock;

    MessageIdentifierGenerator messageIdentifierGeneratorMock;

    public MessageFactoryTest() {
        messageUtilsMock = mock(MessageUtils.class);
        messageIdentifierGeneratorMock = mock(MessageIdentifierGenerator.class);

        messageFactory = new MessageFactory(messageUtilsMock);
    }

    public static class MyCommand extends Command {

        public byte myByte;

        public MyCommand(byte identifier) {
            super(identifier);
        }
    }

    @Test
    public void createReflectiveInstance_typedInstanceReturned() {
        when(messageUtilsMock.getMessageIdentifierGenerator()).thenReturn(messageIdentifierGeneratorMock);
        when(messageIdentifierGeneratorMock.getIdentifier(MyCommand.class)).thenReturn((byte) 69);

        MyCommand command = messageFactory.createReflectiveInstance(MyCommand.class);

        assertEquals((byte) 69, command.identifier);
    }
}
