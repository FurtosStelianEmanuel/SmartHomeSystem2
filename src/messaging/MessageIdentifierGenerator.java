/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import annotations.Injectable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import messaging.commands.*;
import messaging.commands.responses.*;
import smarthomesystem.commands.*;
import smarthomesystem.commands.responses.*;
import smarthomesystem.queries.*;
import smarthomesystem.queries.results.*;

/**
 *
 * @author Manel
 */
@Injectable
public class MessageIdentifierGenerator {

    private byte currentId = -128;
    private final Map<Class, Byte> identifiers = new HashMap<Class, Byte>() {
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
            put(TurnOffBuiltInLedCommand.class, getNext());
            put(TurnOffBuiltInLedCommandResponse.class, getNext());
            put(DoorOpenedCommand.class, getNext());
            put(DoorOpenedCommandResponse.class, getNext());
            put(ModulatePulseWidthCommand.class, getNext());
            put(GenericCommandResponse.class, getNext());
            put(AnalogValueQuery.class, getNext());
            put(AnalogValueQueryResult.class, getNext());
            put(TransitionStateCommand.class, getNext());
            put(ModulatePulseWidthCommandResponse.class, getNext());
            put(MicroControllerQuery.class, getNext());
            put(MicroControllerQueryResult.class, getNext());
        }
    };
    private final Map<Byte, Class> reversedIdentifiers;

    public MessageIdentifierGenerator() {
        reversedIdentifiers = new HashMap<>();
        for (Entry<Class, Byte> entry : identifiers.entrySet()) {
            reversedIdentifiers.put(entry.getValue(), entry.getKey());
        }
    }

    public Class getTypeFromIdentifier(byte identifier) {
        if (!reversedIdentifiers.containsKey(identifier)) {
            return null;
        }

        return reversedIdentifiers.get(identifier);
    }

    public synchronized byte getNext() {
        return currentId++;
    }

    public byte getIdentifier(Class message) {
        if (!identifiers.containsKey(message)) {
            return 0;
        }

        return identifiers.get(message);
    }

    public boolean identifierExists(byte identifier) {
        return identifiers.containsValue(identifier);
    }
}
