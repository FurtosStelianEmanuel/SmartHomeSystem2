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
            put(ClearOutputBufferCommand.class, getNext());         //0
            put(ClearOutputBufferCommandResponse.class, getNext()); //1
            put(DistanceSensorQuery.class, getNext());              //2
            put(DistanceSensorQueryResult.class, getNext());        //3
            put(TestCommsCommand.class, getNext());                 //4
            put(TestCommsCommandResponse.class, getNext());         //5
            put(SetSerialSettingsCommand.class, getNext());         //6
            put(SetSerialSettingsCommandResponse.class, getNext()); //7
            put(TurnOnBuiltInLedCommand.class, getNext());          //8
            put(TurnOnBuiltInLedCommandResponse.class, getNext());  //9
            put(TurnOffBuiltInLedCommand.class, getNext());         //10
            put(TurnOffBuiltInLedCommandResponse.class, getNext()); //11
            put(DoorOpenedCommand.class, getNext());                //12
            put(DoorClosedCommand.class, getNext());                //13
            put(ModulatePulseWidthCommand.class, getNext());        //14
            put(GenericCommandResponse.class, getNext());           //15
            put(AnalogValueQuery.class, getNext());                 //16
            put(AnalogValueQueryResult.class, getNext());           //17
            put(TransitionStateCommand.class, getNext());           //18
            put(ModulatePulseWidthCommandResponse.class, getNext());//19
            put(MicroControllerQuery.class, getNext());             //20
            put(MicroControllerQueryResult.class, getNext());       //21
            put(SetRgbStripColorCommand.class, getNext());          //22
            put(SetColorSmoothlyCommand.class, getNext());          //23
            put(StripTransitionedToColorCommand.class, getNext());  //24
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
