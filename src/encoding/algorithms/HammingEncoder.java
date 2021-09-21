/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encoding.algorithms;

import annotations.Injectable;
import encoding.EncodingAlgorithm;
import encoding.EncodingUtils;
import encoding.exceptions.IncompletePacketException;
import encoding.exceptions.NotResetException;
import encoding.exceptions.UnusablePacketException;
import java.util.ArrayList;
import java.util.List;

/**
 * A 16 bit HammingCode implementation
 *
 * @author Manel
 */
@Injectable
public class HammingEncoder implements EncodingAlgorithm {

    private byte[] dataToProcess;

    private final boolean[] packet;
    private boolean packetContainedError;
    private boolean parity;

    private final List<Boolean> bitStream;
    private final List<Byte> temporaryBuffer;
    private byte[] decodedData;
    private byte[] encodedData;
    private short dataBitsTransferred = 0;

    private static final short REDUNDANT_BIT_COUNT = 5;
    private static final short PACKET_LENGTH = 16;

    private final EncodingUtils encodingUtils;

    public HammingEncoder(EncodingUtils encodingUtils) {
        packet = new boolean[PACKET_LENGTH];
        bitStream = new ArrayList<>();
        temporaryBuffer = new ArrayList<>();
        this.encodingUtils = encodingUtils;
        encodedData = new byte[0];
        decodedData = new byte[0];
        dataToProcess = new byte[0];
    }

    @Override
    public byte[] getDecodedData() {
        return decodedData;
    }

    @Override
    public byte[] getEncodedData() {
        return encodedData;
    }

    @Override
    public void encode(byte[] data) throws NotResetException {
        if (!algorithmWasReset()) {
            throw new NotResetException();
        }

        dataToProcess = data;

        addDataBitsToEncodedBitStream();

        while (dataBitsTransferred < bitStream.size()) {
            resetPacket();
            buildPacketFromBitStream();
            encodePacketParity();
            storePacketInBuffer();
        }

        publishEncodingResult();
    }

    @Override
    public void decode(byte[] data) throws IncompletePacketException, NotResetException, UnusablePacketException {
        if (data.length % 2 == 1) {
            throw new IncompletePacketException(data, PACKET_LENGTH);
        }
        if (!algorithmWasReset()) {
            throw new NotResetException();
        }

        dataToProcess = data;

        for (short i = 0; i < data.length - 1; i += 2) {
            buildPacketFromBytes(data[i], data[i + 1]);
            correctErrorInPacket();
            calculatePacketParity();
            if (packetContainedError && parity != packet[0]) {
                throw new UnusablePacketException(dataToProcess);
            }
            addDataBitsToDecodedBitStream();
        }

        publishDecodingResult();
    }

    @Override
    public void reset() {
        resetPacket();
        packetContainedError = false;
        parity = false;
        encodedData = new byte[0];
        decodedData = new byte[0];
        dataToProcess = new byte[0];
        dataBitsTransferred = 0;
        temporaryBuffer.clear();
        bitStream.clear();
    }

    private boolean algorithmWasReset() {
        for (int i = 0; i < bitStream.size(); i++) {
            if (bitStream.get(i)) {
                return false;
            }
        }

        return bitStream.isEmpty()
                && temporaryBuffer.isEmpty()
                && !packetContainedError
                && !parity
                && encodedData.length == 0
                && decodedData.length == 0
                && dataToProcess.length == 0
                && dataBitsTransferred == 0;
    }

    private void addDataBitsToEncodedBitStream() {
        for (byte infoByte : dataToProcess) {
            boolean[] bitRepresentation = encodingUtils.convertByteToBooleanArray(infoByte);
            for (short i = 0; i < bitRepresentation.length; i++) {
                bitStream.add(bitRepresentation[i]);
            }
        }
    }

    private void resetPacket() {
        for (short i = 0; i < packet.length; i++) {
            packet[i] = false;
        }
    }

    private void buildPacketFromBitStream() {
        for (short i = 3; i < packet.length; i++) {
            if (i == 4 || i == 8) {
                continue;
            }
            if (dataBitsTransferred < bitStream.size()) {
                packet[i] = bitStream.get(dataBitsTransferred++);
            }
        }
    }

    private void encodePacketParity() {
        packet[1] = packet[5] ^ packet[9] ^ packet[13] ^ packet[3] ^ packet[7] ^ packet[11] ^ packet[15];
        packet[2] = packet[6] ^ packet[10] ^ packet[14] ^ packet[3] ^ packet[7] ^ packet[11] ^ packet[15];
        packet[4] = packet[5] ^ packet[6] ^ packet[7] ^ packet[12] ^ packet[13] ^ packet[14] ^ packet[15];
        packet[8] = packet[9] ^ packet[10] ^ packet[11] ^ packet[12] ^ packet[13] ^ packet[14] ^ packet[15];
        calculatePacketParity();
        packet[0] = parity;
    }

    private void storePacketInBuffer() {
        temporaryBuffer.add(encodingUtils.convertBooleanArrayToByte(new boolean[]{
            packet[0], packet[1], packet[2], packet[3], packet[4], packet[5], packet[6], packet[7]
        }));
        temporaryBuffer.add(encodingUtils.convertBooleanArrayToByte(new boolean[]{
            packet[8], packet[9], packet[10], packet[11], packet[12], packet[13], packet[14], packet[15]
        }));
    }

    private void publishEncodingResult() {
        encodedData = new byte[temporaryBuffer.size()];
        for (int i = 0; i < temporaryBuffer.size(); i++) {
            encodedData[i] = temporaryBuffer.get(i);
        }
    }

    private void buildPacketFromBytes(byte b1, byte b2) {
        boolean[] firstHalf = encodingUtils.convertByteToBooleanArray(b1);
        boolean[] secondHalf = encodingUtils.convertByteToBooleanArray(b2);

        for (short i = 0; i < packet.length; i++) {
            if (i < 8) {
                packet[i] = firstHalf[i];
            } else {
                packet[i] = secondHalf[i - 8];
            }
        }
    }

    private void correctErrorInPacket() {
        packetContainedError = false;
        short errorPosition = pinpointError();

        if (errorPosition != 0) {
            packet[errorPosition] = !packet[errorPosition];
            packetContainedError = true;
        }
    }

    private short pinpointError() {
        short errorPosition = 0;

        for (short i = 1; i < packet.length; i++) {
            if (!packet[i]) {
                continue;
            }
            errorPosition ^= i;
        }

        return errorPosition;
    }

    private void calculatePacketParity() {
        parity = false;

        for (short i = 1; i < packet.length; i++) {
            parity ^= packet[i];
        }
    }

    private void addDataBitsToDecodedBitStream() {
        for (short i = 3; i < packet.length; i++) {
            if (i == 4 || i == 8) {
                continue;
            }
            bitStream.add(packet[i]);
        }
    }

    private void publishDecodingResult() {
        decodedData = new byte[dataToProcess.length - 1 - (REDUNDANT_BIT_COUNT * dataToProcess.length / 2) / Byte.SIZE];

        for (short i = 0; i + 8 < bitStream.size(); i += 8) {
            decodedData[i / 8] = encodingUtils.convertBooleanArrayToByte(new boolean[]{
                bitStream.get(i),
                bitStream.get(i + 1),
                bitStream.get(i + 2),
                bitStream.get(i + 3),
                bitStream.get(i + 4),
                bitStream.get(i + 5),
                bitStream.get(i + 6),
                bitStream.get(i + 7)
            });
        }
    }
}
