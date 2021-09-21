/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encoding;

import smarthomesystem.TestUtils;
import encoding.exceptions.EncodingException;
import encoding.exceptions.IncompletePacketException;
import encoding.exceptions.NotResetException;
import encoding.exceptions.UnusablePacketException;
import encoding.algorithms.HammingEncoder;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Manel
 */
public class HammingEncoderTest extends TestUtils {

    EncodingUtils encodingUtilsMock;
    EncodingAlgorithm algorithm;

    public HammingEncoderTest() {
        encodingUtilsMock = mock(EncodingUtils.class);
        algorithm = new HammingEncoder(encodingUtilsMock);
    }

    //<editor-fold desc="decode tests" defaultstate="collapsed">
    @Test
    public void decode_whenPacketsHaveNoErrors_resultsDecodedCorrectly() {
        byte[] expectedDecodedData = new byte[]{(byte) 0b11010011, (byte) 0b10010010, (byte) 0b11100001, (byte) 0b00001111};
        byte[] encodedData = new byte[]{(byte) 0b11110101, (byte) 0b10011100, (byte) 0b11010001, (byte) 0b10111000, (byte) 0b01000100, (byte) 0b00011110};

        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[0])).thenReturn(new boolean[]{
            true, true, true, true, false, true, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[1])).thenReturn(new boolean[]{
            true, false, false, true, true, true, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[2])).thenReturn(new boolean[]{
            true, true, false, true, false, false, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[3])).thenReturn(new boolean[]{
            true, false, true, true, true, false, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[4])).thenReturn(new boolean[]{
            false, true, false, false, false, true, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[5])).thenReturn(new boolean[]{
            false, false, false, true, true, true, true, false
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, false, true, false, false, true, true
        })).thenReturn(expectedDecodedData[0]);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, false, false, true, false, false, true, false
        })).thenReturn(expectedDecodedData[1]);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, true, false, false, false, false, true
        })).thenReturn(expectedDecodedData[2]);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, false, false, false, true, true, true, true
        })).thenReturn(expectedDecodedData[3]);

        try {
            algorithm.decode(encodedData);
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            byte[] result = algorithm.getDecodedData();
            assertEquals(4, result.length);
            assertEquals(expectedDecodedData[0], result[0]);
            assertEquals(expectedDecodedData[1], result[1]);
            assertEquals(expectedDecodedData[2], result[2]);
            assertEquals(expectedDecodedData[3], result[3]);
        }
    }

    @Test
    public void decode_whenPacketHasADataBitError_resultsDecodedCorrectly() {
        byte[] expectedDecodedData = new byte[]{(byte) 0b11010011, (byte) 0b10010010, (byte) 0b11100001, (byte) 0b00001111};
        byte[] encodedData = new byte[]{
            (byte) (0b11110101 & 0b11111110), (byte) 0b10011100,
            (byte) 0b11010001, (byte) 0b10111000 | 0b01000000,
            (byte) 0b01000100, (byte) (0b00011110 & 0b11101111)
        };

        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[0])).thenReturn(new boolean[]{
            true, true, true, true, false, true, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[1])).thenReturn(new boolean[]{
            true, false, false, true, true, true, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[2])).thenReturn(new boolean[]{
            true, true, false, true, false, false, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[3])).thenReturn(new boolean[]{
            true, true, true, true, true, false, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[4])).thenReturn(new boolean[]{
            false, true, false, false, false, true, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[5])).thenReturn(new boolean[]{
            false, false, false, false, true, true, true, false
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, false, true, false, false, true, true
        })).thenReturn(expectedDecodedData[0]);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, false, false, true, false, false, true, false
        })).thenReturn(expectedDecodedData[1]);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, true, false, false, false, false, true
        })).thenReturn(expectedDecodedData[2]);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, false, false, false, true, true, true, true
        })).thenReturn(expectedDecodedData[3]);

        try {
            algorithm.decode(encodedData);
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            byte[] result = algorithm.getDecodedData();
            assertEquals(4, result.length);
            assertEquals(expectedDecodedData[0], result[0]);
            assertEquals(expectedDecodedData[1], result[1]);
            assertEquals(expectedDecodedData[2], result[2]);
            assertEquals(expectedDecodedData[3], result[3]);
        }
    }

    @Test
    public void decode_whenParityBitFlippedAndOtherPacketsBitsFlipped_resultsDecodedCorrectly() {
        byte[] expectedDecodedData = new byte[]{(byte) 0b11010011, (byte) 0b10010010, (byte) 0b11100001, (byte) 0b00001111};
        byte[] encodedData = new byte[]{
            (byte) (0b11110101 & 0b01111111), (byte) 0b10011100,
            (byte) 0b11010001, (byte) 0b10111000 | 0b01000000,
            (byte) 0b01000100, (byte) (0b00011110 & 0b11101111)
        };

        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[0])).thenReturn(new boolean[]{
            false, true, true, true, false, true, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[1])).thenReturn(new boolean[]{
            true, false, false, true, true, true, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[2])).thenReturn(new boolean[]{
            true, true, false, true, false, false, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[3])).thenReturn(new boolean[]{
            true, true, true, true, true, false, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[4])).thenReturn(new boolean[]{
            false, true, false, false, false, true, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[5])).thenReturn(new boolean[]{
            false, false, false, false, true, true, true, false
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, false, true, false, false, true, true
        })).thenReturn(expectedDecodedData[0]);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, false, false, true, false, false, true, false
        })).thenReturn(expectedDecodedData[1]);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, true, false, false, false, false, true
        })).thenReturn(expectedDecodedData[2]);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, false, false, false, true, true, true, true
        })).thenReturn(expectedDecodedData[3]);

        try {
            algorithm.decode(encodedData);
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            byte[] result = algorithm.getDecodedData();
            assertEquals(4, result.length);
            assertEquals(expectedDecodedData[0], result[0]);
            assertEquals(expectedDecodedData[1], result[1]);
            assertEquals(expectedDecodedData[2], result[2]);
            assertEquals(expectedDecodedData[3], result[3]);
        }
    }

    @Test
    public void decode_when2BytePacketProvided1DataByteRetrieved() {
        byte expectedDecodedData = (byte) 0b11010011;
        byte[] encodedData = new byte[]{
            (byte) 0b11110101, (byte) 0b10011100
        };

        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[0])).thenReturn(new boolean[]{
            true, true, true, true, false, true, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[1])).thenReturn(new boolean[]{
            true, false, false, true, true, true, false, false
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, false, true, false, false, true, true
        })).thenReturn(expectedDecodedData);

        try {
            algorithm.decode(encodedData);
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            byte[] result = algorithm.getDecodedData();
            assertEquals(1, result.length);
            assertEquals(expectedDecodedData, result[0]);
        }
    }

    @Test
    public void decode_when2DataBitsFlippedInPacket_unusablePacketExceptionThrown() {
        byte[] encodedData = new byte[]{
            (byte) (0b11110101 & 0b11111010), (byte) 0b10011100
        };

        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[0])).thenReturn(new boolean[]{
            true, true, true, true, false, false, false, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[1])).thenReturn(new boolean[]{
            true, false, false, true, true, true, false, false
        });

        try {
            algorithm.decode(encodedData);
            fail(expectedErrorShouldHaveOccured(UnusablePacketException.class));
        } catch (EncodingException ex) {
            assertEquals(UnusablePacketException.class.getName(), ex.getClass().getName());
            assertArrayEquals(encodedData, ex.getFaultyData());
        }
    }

    @Test
    public void decode_whenProvidedByteArrayHasInsufficientBytesToFormCompletePacket_incompletePacketExceptionThrown() {
        byte[] encodedData = new byte[]{
            (byte) 0b11110101, (byte) 0b10011100,
            (byte) 0b11110000
        };

        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[0])).thenReturn(new boolean[]{
            true, true, true, true, false, true, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[1])).thenReturn(new boolean[]{
            true, false, false, true, true, true, false, false
        });

        try {
            algorithm.decode(encodedData);
            fail(expectedErrorShouldHaveOccured(IncompletePacketException.class));
        } catch (EncodingException ex) {
            assertEquals(IncompletePacketException.class.getName(), ex.getClass().getName());
            assertArrayEquals(encodedData, ex.getFaultyData());
        }
    }

    @Test
    public void decode_whenDecodingCalledTwiceWithoutReset_notResetExceptionThrown() {
        byte[] encodedData = new byte[]{
            (byte) 0b11110101, (byte) 0b10011100
        };

        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[0])).thenReturn(new boolean[]{
            true, true, true, true, false, true, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData[1])).thenReturn(new boolean[]{
            true, false, false, true, true, true, false, false
        });

        try {
            algorithm.decode(encodedData);
            algorithm.decode(new byte[]{(byte) 0b11110000, (byte) 0b00110011});
            fail(expectedErrorShouldHaveOccured(NotResetException.class));
        } catch (EncodingException ex) {
            assertEquals(NotResetException.class.getName(), ex.getClass().getName());
            assertNull(ex.getFaultyData());
        }
    }

    @Test
    public void decode_whenDecodeCalledTwiceWithReset_resultsDecodedCorrectly() {
        byte expectedData1 = (byte) 0b11011001;
        byte expectedData2 = (byte) 0b11010001;

        byte[] encodedData1 = new byte[]{
            (byte) 0b00011101, (byte) 0b01001000
        };
        byte[] encodedData2 = new byte[]{
            (byte) 0b11011101, (byte) 0b10001000
        };

        when(encodingUtilsMock.convertByteToBooleanArray(encodedData1[0])).thenReturn(new boolean[]{
            false, false, false, true, true, true, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData1[1])).thenReturn(new boolean[]{
            false, true, false, false, true, false, false, false
        });

        when(encodingUtilsMock.convertByteToBooleanArray(encodedData2[0])).thenReturn(new boolean[]{
            true, true, false, true, true, true, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(encodedData2[1])).thenReturn(new boolean[]{
            true, false, false, false, true, false, false, false
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, false, true, true, false, false, true
        })).thenReturn(expectedData1);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, false, true, false, false, false, true
        })).thenReturn(expectedData2);

        byte[] firstResult, secondResult;
        firstResult = secondResult = new byte[0];

        try {
            algorithm.decode(encodedData1);
            firstResult = algorithm.getDecodedData();
            algorithm.reset();
            algorithm.decode(encodedData2);
            secondResult = algorithm.getDecodedData();
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            assertEquals(1, firstResult.length);
            assertEquals(1, secondResult.length);
            assertEquals(expectedData1, firstResult[0]);
            assertEquals(expectedData2, secondResult[0]);
        }
    }

    //</editor-fold>
    //<editor-fold desc="encode tests" defaultstate="collapsed">
    @Test
    public void encode_when1ByteToEncode_1PacketCreatedAndEncodedCorrectly() {

        byte[] bytesToEncode = new byte[]{(byte) 0b00111001};
        byte[] expectedEncodedPacket = new byte[]{(byte) 0b10001011, (byte) 0b01001000};

        when(encodingUtilsMock.convertByteToBooleanArray(bytesToEncode[0])).thenReturn(new boolean[]{
            false, false, true, true, true, false, false, true
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, false, false, false, true, false, true, true
        })).thenReturn((byte) 0b10001011);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, true, false, false, true, false, false, false
        })).thenReturn((byte) 0b01001000);

        try {
            algorithm.encode(bytesToEncode);
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            byte[] result = algorithm.getEncodedData();
            assertEquals(2, result.length);
            assertArrayEquals(expectedEncodedPacket, result);
        }
    }

    @Test
    public void encode_when2BytesToEncode_2PacketsCreatedAndEncodedCorrectly() {

        byte[] bytesToEncode = new byte[]{(byte) 0b00111001, (byte) 0b00111111};
        byte[] expectedEncodedPacket = new byte[]{(byte) 0b01100011, (byte) 0b11001001, (byte) 0b00111111, (byte) 0b11000000};

        when(encodingUtilsMock.convertByteToBooleanArray(bytesToEncode[0])).thenReturn(new boolean[]{
            false, false, true, true, true, false, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(bytesToEncode[1])).thenReturn(new boolean[]{
            false, false, true, true, true, true, true, true
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, true, true, false, false, false, true, true
        })).thenReturn((byte) 0b01100011);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, false, false, true, false, false, true
        })).thenReturn((byte) 0b11001001);

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, false, true, true, true, true, true, true
        })).thenReturn((byte) 0b00111111);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, false, false, false, false, false, false
        })).thenReturn((byte) 0b11000000);

        try {
            algorithm.encode(bytesToEncode);
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            byte[] result = algorithm.getEncodedData();
            assertEquals(4, result.length);
            assertArrayEquals(expectedEncodedPacket, result);
        }
    }

    @Test
    public void encode_when4BytesToEncode_3PacketsCreatedAndEncodedCorrectly() {

        byte[] bytesToEncode = new byte[]{(byte) 0b00111001, (byte) 0b00111111, (byte) 0b10101010, (byte) 0b11110001};
        byte[] expectedEncodedPacket = new byte[]{(byte) 0b01100011, (byte) 0b11001001, (byte) 0b00111111, (byte) 0b01101010, (byte) 0b01111011, (byte) 0b11100010};

        when(encodingUtilsMock.convertByteToBooleanArray(bytesToEncode[0])).thenReturn(new boolean[]{
            false, false, true, true, true, false, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(bytesToEncode[1])).thenReturn(new boolean[]{
            false, false, true, true, true, true, true, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(bytesToEncode[2])).thenReturn(new boolean[]{
            true, false, true, false, true, false, true, false
        });
        when(encodingUtilsMock.convertByteToBooleanArray(bytesToEncode[3])).thenReturn(new boolean[]{
            true, true, true, true, false, false, false, true
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, true, true, false, false, false, true, true
        })).thenReturn((byte) 0b1100011);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, false, false, true, false, false, true
        })).thenReturn((byte) 0b11001001);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, false, true, true, true, true, true, true
        })).thenReturn((byte) 0b00111111);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, true, true, false, true, false, true, false
        })).thenReturn((byte) 0b01101010);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, true, true, true, true, false, true, true
        })).thenReturn((byte) 0b01111011);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, true, false, false, false, true, false
        })).thenReturn((byte) 0b11100010);

        try {
            algorithm.encode(bytesToEncode);
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            byte[] result = algorithm.getEncodedData();
            assertEquals(6, result.length);
            assertArrayEquals(expectedEncodedPacket, result);
        }
    }

    @Test
    public void encode_when1ByteToEncode_and2ConsecutiveCallsWithResetInBetween_packetsCreatedAndEncodedCorrectly() {
        byte[] firstByteToEncode = new byte[]{(byte) 0b00101001};
        byte[] secondByteToEncode = new byte[]{(byte) 0b11110010};

        byte[] firstResult = new byte[0];
        byte[] secondResult = new byte[0];

        byte[] expectedFirstResult = new byte[]{(byte) 0b11100010, (byte) 0b01001000};
        byte[] expectedSecondResult = new byte[]{(byte) 0b10011111, (byte) 0b10010000};

        when(encodingUtilsMock.convertByteToBooleanArray(firstByteToEncode[0])).thenReturn(new boolean[]{
            false, false, true, false, true, false, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(secondByteToEncode[0])).thenReturn(new boolean[]{
            true, true, true, true, false, false, true, false
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, true, false, false, false, true, false
        })).thenReturn((byte) 0b11100010);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, true, false, false, true, false, false, false
        })).thenReturn((byte) 0b01001000);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, false, false, true, true, true, true, true
        })).thenReturn((byte) 0b10011111);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, false, false, true, false, false, false, false
        })).thenReturn((byte) 0b10010000);

        try {
            algorithm.encode(firstByteToEncode);
            firstResult = algorithm.getEncodedData();
            algorithm.reset();
            algorithm.encode(secondByteToEncode);
            secondResult = algorithm.getEncodedData();
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            assertEquals(2, firstResult.length);
            assertEquals(2, secondResult.length);

            assertArrayEquals(expectedFirstResult, firstResult);
            assertArrayEquals(expectedSecondResult, secondResult);
        }
    }

    @Test
    public void encode_when1ByteToEncode_and2ConsecutiveCallsNoResetInBetween_notResetExceptionThrown() {
        byte[] firstByteToEncode = new byte[]{(byte) 0b00101001};
        byte[] secondByteToEncode = new byte[]{(byte) 0b11110010};

        byte[] firstResult = new byte[0];
        byte[] secondResult = new byte[0];

        byte[] expectedFirstResult = new byte[]{(byte) 0b11100010, (byte) 0b01001000};
        byte[] expectedSecondResult = new byte[0];

        when(encodingUtilsMock.convertByteToBooleanArray(firstByteToEncode[0])).thenReturn(new boolean[]{
            false, false, true, false, true, false, false, true
        });
        when(encodingUtilsMock.convertByteToBooleanArray(secondByteToEncode[0])).thenReturn(new boolean[]{
            true, true, true, true, false, false, true, false
        });

        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, true, true, false, false, false, true, false
        })).thenReturn((byte) 0b11100010);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            false, true, false, false, true, false, false, false
        })).thenReturn((byte) 0b01001000);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, false, false, true, true, true, true, true
        })).thenReturn((byte) 0b10011111);
        when(encodingUtilsMock.convertBooleanArrayToByte(new boolean[]{
            true, false, false, true, false, false, false, false
        })).thenReturn((byte) 0b10010000);

        try {
            algorithm.encode(firstByteToEncode);
            firstResult = algorithm.getEncodedData();
            algorithm.encode(secondByteToEncode);
            fail(expectedErrorShouldHaveOccured(NotResetException.class));
        } catch (EncodingException ex) {
            assertEquals(NotResetException.class.getName(), ex.getClass().getName());
            assertNull(ex.getFaultyData());
        } finally {
            assertEquals(2, firstResult.length);
            assertEquals(0, secondResult.length);

            assertArrayEquals(expectedFirstResult, firstResult);
            assertArrayEquals(expectedSecondResult, secondResult);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Integration tests with EncodingUtils" defaultstate="collapsed">
    @Test
    public void encode_decode_decodedOutputMatchesInitialInput_1Byte() {
        EncodingUtils encodingUtils = new EncodingUtils();
        EncodingAlgorithm hammingEncoder = new HammingEncoder(encodingUtils);

        byte[] dataToEncode = new byte[]{(byte) 0b00100101};
        byte[] encodedData;
        byte[] decodedData = new byte[0];

        try {
            hammingEncoder.encode(dataToEncode);
            encodedData = hammingEncoder.getEncodedData();

            hammingEncoder.reset();

            hammingEncoder.decode(encodedData);
            decodedData = hammingEncoder.getDecodedData();
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            assertArrayEquals(dataToEncode, decodedData);
        }
    }

    @Test
    public void encode_decode_decodedOutputMatchesInitialInput_2Bytes() {
        EncodingUtils encodingUtils = new EncodingUtils();
        EncodingAlgorithm hammingEncoder = new HammingEncoder(encodingUtils);

        byte[] dataToEncode = new byte[]{(byte) 0b00100101, (byte) 0b01010101};
        byte[] encodedData;
        byte[] decodedData = new byte[0];

        try {
            hammingEncoder.encode(dataToEncode);
            encodedData = hammingEncoder.getEncodedData();

            hammingEncoder.reset();

            hammingEncoder.decode(encodedData);
            decodedData = hammingEncoder.getDecodedData();
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            assertArrayEquals(dataToEncode, decodedData);
        }
    }

    @Test
    public void encode_decode_decodedOutputMatchesInitialInput_4Bytes() {
        EncodingUtils encodingUtils = new EncodingUtils();
        EncodingAlgorithm hammingEncoder = new HammingEncoder(encodingUtils);

        byte[] dataToEncode = new byte[]{(byte) 0b00100101, (byte) 0b01010101, (byte) 0b11001100, (byte) 0b10001100};
        byte[] encodedData;
        byte[] decodedData = new byte[0];

        try {
            hammingEncoder.encode(dataToEncode);
            encodedData = hammingEncoder.getEncodedData();

            hammingEncoder.reset();

            hammingEncoder.decode(encodedData);
            decodedData = hammingEncoder.getDecodedData();
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            assertArrayEquals(dataToEncode, decodedData);
        }
    }

    @Test
    public void encode_decode_decodedOutputMatchesInitialInput_16Bytes() {
        EncodingUtils encodingUtils = new EncodingUtils();
        EncodingAlgorithm hammingEncoder = new HammingEncoder(encodingUtils);

        byte[] dataToEncode = new byte[]{
            (byte) 0b00100101,
            (byte) 0b01010101,
            (byte) 0b11001100,
            (byte) 0b10001100,
            (byte) 0b00100101,
            (byte) 0b11001010,
            (byte) 0b11000100,
            (byte) 0b00100010,
            (byte) 0b00101010,
            (byte) 0b11010100,
            (byte) 0b00010000,
            (byte) 0b00100001,
            (byte) 0b11100001,
            (byte) 0b00101001,
            (byte) 0b00101101,
            (byte) 0b00111101
        };
        byte[] encodedData;
        byte[] decodedData = new byte[0];

        try {
            hammingEncoder.encode(dataToEncode);
            encodedData = hammingEncoder.getEncodedData();

            hammingEncoder.reset();

            hammingEncoder.decode(encodedData);
            decodedData = hammingEncoder.getDecodedData();
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            assertArrayEquals(dataToEncode, decodedData);
        }
    }

    @Test
    public void encode_decode_decodedOutputMatchesInitialInput_17Bytes() {
        EncodingUtils encodingUtils = new EncodingUtils();
        EncodingAlgorithm hammingEncoder = new HammingEncoder(encodingUtils);

        byte[] dataToEncode = new byte[]{
            (byte) 0b00100101,
            (byte) 0b01010101,
            (byte) 0b11001100,
            (byte) 0b10001100,
            (byte) 0b00100101,
            (byte) 0b11001010,
            (byte) 0b11000100,
            (byte) 0b00100010,
            (byte) 0b00101010,
            (byte) 0b11010100,
            (byte) 0b00010000,
            (byte) 0b00100001,
            (byte) 0b11100001,
            (byte) 0b00101001,
            (byte) 0b00101101,
            (byte) 0b00111101,
            (byte) 0b00111101
        };
        byte[] encodedData;
        byte[] decodedData = new byte[0];

        try {
            hammingEncoder.encode(dataToEncode);
            encodedData = hammingEncoder.getEncodedData();

            hammingEncoder.reset();

            hammingEncoder.decode(encodedData);
            decodedData = hammingEncoder.getDecodedData();
        } catch (EncodingException ex) {
            fail(unexpectedError(ex));
        } finally {
            assertArrayEquals(dataToEncode, decodedData);
        }
    }
    //</editor-fold>
}
