/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encoding;

import annotations.Injectable;

/**
 *
 * @author Manel
 */
@Injectable
public class EncodingUtils {

    public final byte[] BITMASKS = new byte[]{
        (byte) 0b10000000,
        (byte) 0b01000000,
        (byte) 0b00100000,
        (byte) 0b00010000,
        (byte) 0b00001000,
        (byte) 0b00000100,
        (byte) 0b00000010,
        (byte) 0b00000001
    };

    public boolean[] convertByteToBooleanArray(byte toConvert) {
        return new boolean[]{
            (toConvert & BITMASKS[0]) != 0,
            (toConvert & BITMASKS[1]) != 0,
            (toConvert & BITMASKS[2]) != 0,
            (toConvert & BITMASKS[3]) != 0,
            (toConvert & BITMASKS[4]) != 0,
            (toConvert & BITMASKS[5]) != 0,
            (toConvert & BITMASKS[6]) != 0,
            (toConvert & BITMASKS[7]) != 0
        };
    }

    public byte convertBooleanArrayToByte(boolean[] bits) {
        byte result = 0;
        int index = 8 - bits.length;
        for (boolean bit : bits) {
            if (bit) {
                result |= (byte) (1 << (7 - index));
            }
            index++;
        }

        return result;
    }
}
