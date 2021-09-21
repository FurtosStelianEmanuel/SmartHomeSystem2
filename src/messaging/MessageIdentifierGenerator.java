/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import annotations.Injectable;
import java.util.HashMap;
import java.util.Map;
import messaging.commands.ClearOutputBufferCommand;
import messaging.commands.TestCommsCommand;
import messaging.commands.TurnOffBuiltInLedCommand;
import messaging.commands.TurnOnBuiltInLedCommand;
import messaging.commands.responses.ClearOutputBufferCommandResponse;
import messaging.commands.responses.TestCommsCommandResponse;
import messaging.commands.responses.TurnOffBuiltInLedCommandResponse;
import messaging.commands.responses.TurnOnBuiltInLedCommandResponse;
import smarthomesystem.commands.SetSerialSettingsCommand;
import smarthomesystem.commands.responses.SetSerialSettingsCommandResponse;
import smarthomesystem.queries.DistanceSensorQuery;
import smarthomesystem.queries.results.DistanceSensorQueryResult;

/**
 *
 * @author Manel
 */
@Injectable
public class MessageIdentifierGenerator {

    private byte currentId = -128;
    private final Map<Class, Byte> IDENTIFIERS = new HashMap<Class, Byte>() {
        {
            put(ClearOutputBufferCommand.class, getNext());
            put(ClearOutputBufferCommandResponse.class, getNext());
            put(DistanceSensorQuery.class, getNext());
            put(DistanceSensorQueryResult.class, getNext());
            put(TestCommsCommand.class, getNext());
            put(TestCommsCommandResponse.class, getNext());
            put(SetSerialSettingsCommand.class, getNext());
            put(SetSerialSettingsCommandResponse.class, getNext());
            put(TurnOnBuiltInLedCommand.class, getNext());
            put(TurnOnBuiltInLedCommandResponse.class, getNext());
            put(TurnOffBuiltInLedCommand.class,getNext());
            put(TurnOffBuiltInLedCommandResponse.class,getNext());
        }
    };

    public synchronized byte getNext() {
        return currentId++;
    }

    public byte getIdentifier(Class message) {
        if (!IDENTIFIERS.containsKey(message)) {
            return 0;
        }

        return IDENTIFIERS.get(message);
    }

    public boolean identifierExists(byte identifier) {
        return IDENTIFIERS.containsValue(identifier);
    }
}
