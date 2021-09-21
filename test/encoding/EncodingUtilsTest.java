/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encoding;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Manel
 */
public class EncodingUtilsTest {

    EncodingUtils encodingUtils;

    public EncodingUtilsTest() {
        encodingUtils = new EncodingUtils();
    }

    @Test
    public void convertByteToBooleanArray_success() {
        byte byteValue = (byte) 0b01001001;
        boolean[] expectedBitRepresentation = new boolean[]{
            false, true, false, false, true, false, false, true
        };

        boolean[] result = encodingUtils.convertByteToBooleanArray(byteValue);
        assertArrayEquals(expectedBitRepresentation, result);
    }

    @Test
    public void convertBooleanArrayToByte_success() {
        boolean[] bitRepresentation = new boolean[]{
            false, false, true, true, true, false, true, true
        };
        byte expectedByteValue = 0b00111011;

        byte result = encodingUtils.convertBooleanArrayToByte(bitRepresentation);
        assertEquals(expectedByteValue, result);
    }
}
