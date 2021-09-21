/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encoding;

import encoding.exceptions.EncodingException;

/**
 *
 * @author Manel
 */
public interface EncodingAlgorithm {

    abstract byte[] getDecodedData();

    abstract byte[] getEncodedData();

    abstract void encode(byte[] data) throws EncodingException;

    abstract void decode(byte[] data) throws EncodingException;

    abstract void reset();
}
