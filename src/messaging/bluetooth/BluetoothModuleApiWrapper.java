/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.bluetooth;

import annotations.Injectable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothModuleApiWrapper {

    StreamConnection streamConnection;

    public BluetoothModuleApiWrapper() {

    }

    public void connectToModule(BluetoothConfig config) throws IOException {
        streamConnection = (StreamConnection) Connector.open("btspp://" + config.getAddress() + ":1;authenticate=false;encrypt=false;master=false");
    }

    public InputStream getInputStream() throws IOException {
        return streamConnection.openInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return streamConnection.openOutputStream();
    }

    public void disconnect() throws IOException {
        streamConnection.close();
    }
}
