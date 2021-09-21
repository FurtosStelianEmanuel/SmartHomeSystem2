/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testcommunications;

import java.io.IOException;
import java.util.Arrays;
import messaging.MessageBroker;
import messaging.MessageDispatcher;
import messaging.MessageFactory;
import messaging.ResponseCallback;
import messaging.ResponseListener;
import messaging.commands.TestCommsCommand;
import messaging.commands.responses.TestCommsCommandResponse;

/**
 *
 * @author Manel
 */
public class CommsTester {

    byte[][] dataToSend;
    byte[][] dataReceived;
    private final MessageBroker messageBroker;
    private final MessageFactory messageFactory;
    private final MessageDispatcher messageDispatcher;
    private final int packetCount;
    private final int packetSize;
    private byte startingByte = -128;
    private long startTime;
    private int packetsReceived = 0;

    public CommsTester(MessageBroker messageBroker, MessageFactory messageFactory, MessageDispatcher messageDispatcher, int packetCount, int packetSize) {
        if (packetSize > 63) {
            packetSize = 63; //one byte has to be reserverd for the identifier
        }
        dataToSend = new byte[packetCount][packetSize];
        dataReceived = new byte[packetCount][packetSize];
        this.messageBroker = messageBroker;
        this.messageFactory = messageFactory;
        this.messageDispatcher = messageDispatcher;
        this.packetCount = packetCount;
        this.packetSize = packetSize;

        messageDispatcher.addListener(new ResponseListener(true, new ResponseCallback<TestCommsCommandResponse>(TestCommsCommandResponse.class) {
            @Override
            public void onResponse(TestCommsCommandResponse commandResponse) {

                dataReceived[(packetsReceived++) % packetCount] = commandResponse.data;
                if (packetsReceived == packetCount) {
                    completeDataReceived();
                    reset();
                }
            }
        }));
    }

    public void reset() {
        startTime = System.currentTimeMillis();
        packetsReceived = 0;
        for (int i = 0; i < dataReceived.length; i++) {
            for (int j = 0; j < dataReceived[i].length; j++) {
                dataReceived[i][j] = 0;
            }
        }
    }

    public void generateData() {
        for (int i = 0; i < dataToSend.length; i++) {
            dataToSend[i] = new byte[packetSize];
            for (int j = 0; j < dataToSend[i].length; j++) {
                dataToSend[i][j] = startingByte++;
            }
        }
    }

    public void run() throws IOException, Exception {
        reset();
        generateData();
        TestCommsCommand testCommsCommand = messageFactory.createReflectiveInstance(TestCommsCommand.class);
        for (int i = 0; i < packetCount; i++) {
            testCommsCommand.data = dataToSend[i];
            messageBroker.send(testCommsCommand);
        }
    }

    private void completeDataReceived() {
        boolean errorOccured = false;
        long milis = System.currentTimeMillis() - startTime;

        byte expectedByte = dataReceived[0][0];
        for (int i = 0; i < dataReceived.length; i++) {
            for (int j = 0; j < dataReceived[i].length; j++) {
                if (dataReceived[i][j] != expectedByte) {
                    System.out.println(String.format("Error at position [%d][%d], expected %d, actual %d", i, j, dataToSend[i][j], dataReceived[i][j]));
                    errorOccured = true;
                }
                expectedByte++;
            }
        }
        if (!errorOccured) {
            int dataAmount = packetSize * packetCount * 2;
            System.out.println(String.format("%d bytes transported successfully", dataAmount));

            double seconds = milis / 1000.0;
            long bytesPerSecond = (long) (dataAmount / seconds);
            long bitsPerSecond = bytesPerSecond * 8;
            System.out.println(
                    String.format("Write+read results: \n Bytes/sec : %d \n Bits/sec: %d \n Total time (milis): %d\n Total time(seconds): %f",
                            bytesPerSecond, bitsPerSecond, milis, seconds)
            );
        }
    }
}
