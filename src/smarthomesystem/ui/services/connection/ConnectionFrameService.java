/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.connection;

import annotations.Injectable;
import java.awt.event.ActionEvent;
import smarthomesystem.ui.frames.connection.ConnectionFrame;
import smarthomesystem.ui.services.FrameService;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.ui.frames.connection.BluetoothConnectionFrame;

/**
 *
 * @author Manel
 */
@Injectable
public class ConnectionFrameService extends FrameService<ConnectionFrame> {

    public void bluetoothConnectionDesired(ActionEvent evt) {
        BluetoothConnectionFrame bluetoothConnectionFrame = container.resolveDependencies(BluetoothConnectionFrame.class);
        bluetoothConnectionFrame.setLocationRelativeTo(null);
        bluetoothConnectionFrame.setVisible(true);
        frame.setVisible(false);
    }
}
