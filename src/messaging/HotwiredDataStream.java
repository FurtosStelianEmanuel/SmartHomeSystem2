/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

/**
 *
 * @author Manel
 */
public interface HotwiredDataStream {
    
    void onHotwiredResponse(byte[] data);

    void onResponseTimeout();
}
