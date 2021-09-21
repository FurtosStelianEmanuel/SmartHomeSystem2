/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.commands.responses;

import messaging.Response;
import messaging.exceptions.CannotUnpackByteArrayException;
import misc.Misc;

/**
 *
 * @author Manel
 */
public class ClearOutputBufferCommandResponse extends Response {

    byte startByte;
    byte[] confirmationBytes;
    byte endByte;

    public ClearOutputBufferCommandResponse(byte[] rawData) throws CannotUnpackByteArrayException {
        super((byte) -127);
        confirmationBytes = new byte[Misc.CLEAR_BUFFER_NR_CONFIRMATION_BYTES];
        if (!(rawData.length >= Misc.CLEAR_BUFFER_NR_CONFIRMATION_BYTES + 2)) {
            throw new CannotUnpackByteArrayException();
        }
        if (rawData.length >= Misc.CLEAR_BUFFER_NR_CONFIRMATION_BYTES + 2) {
            for (int i = 0; i < rawData.length; i++) {
                if (rawData[i] == identifier) {
                    startByte = rawData[i];
                    if (i < rawData.length - 1) {
                        i++;
                    }
                    boolean allConfirmationBytesArrived = true;
                    for (int j = i, count = 0; j < i + Misc.CLEAR_BUFFER_NR_CONFIRMATION_BYTES && j < rawData.length; j++, count++) {
                        confirmationBytes[count] = rawData[j];
                        if (confirmationBytes[count] != -56) {
                            allConfirmationBytesArrived = false;
                        }
                    }
                    if (i + 10 < rawData.length) {
                        endByte = rawData[i + 10];
                    }

                    if (startByte == endByte && startByte == identifier && allConfirmationBytesArrived) {
                        return;
                    }
                }
            }
        }
        throw new CannotUnpackByteArrayException();
    }
}
