/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.connection;

import annotations.Injectable;
import data.PathProvider;
import data.SerializationUtils;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.swing.table.DefaultTableModel;
import misc.Misc;
import smarthomesystem.ui.frames.connection.BluetoothConnectionFrame;
import smarthomesystem.ui.services.FrameService;

/**
 *
 * @author Manel
 */
@Injectable
public class BluetoothConnectionFrameService extends FrameService<BluetoothConnectionFrame> {

    private final SerializationUtils serializationUtils;
    private final PathProvider pathProvider;

    private final String bluetoothDevicesFileName = "bluetoothDevices.shs";
    private final String selectedDeviceFileName = "selectedDevice.shs";
    private final boolean LOAD_VIRTUAL_DEVICE = true;

    public BluetoothConnectionFrameService(SerializationUtils serializationUtils, PathProvider pathProvider) {
        this.serializationUtils = serializationUtils;
        this.pathProvider = pathProvider;
    }

    public void loadDevices() {
        List<Pair<String, String>> deserializedDevices = null;
        Pair<String, String> deserializedSelectedDevice = null;

        try {
            deserializedDevices = serializationUtils.deserialize(Paths.get(serializationUtils.serializationDirectory, bluetoothDevicesFileName).toString());
            deserializedSelectedDevice = serializationUtils.deserialize(Paths.get(serializationUtils.serializationDirectory, selectedDeviceFileName).toString());

            checkVirtualDevice(deserializedDevices);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BluetoothConnectionFrameService.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean shouldInitiateSearch = deserializedDevices == null;

        if (shouldInitiateSearch) {
            searchDevices();
        } else {
            frame.addDevicesToTable(deserializedDevices);

            if (deserializedSelectedDevice != null) {
                handleDeserializedSelectedDevice(deserializedDevices, deserializedSelectedDevice);
            }
        }
    }

    public void reloadDevices() {
        frame.clearDevices();
        searchDevices();
    }

    public void saveDevices() throws IOException {
        DefaultTableModel tableContent = (DefaultTableModel) frame.jTable1.getModel();
        List<Pair<String, String>> devices = new ArrayList<>();

        for (int i = 0; i < tableContent.getRowCount(); i++) {
            String macAddress = (String) tableContent.getValueAt(i, 0);
            String deviceName = (String) tableContent.getValueAt(i, 1);

            if (macAddress.equals(Misc.EMPTY_MAC_ADDRESS) && deviceName.equals(Misc.VIRTUAL_DEVICE_NAME)) {
                continue;
            }

            devices.add(new Pair(macAddress, deviceName));
        }

        int selectedRow = frame.jTable1.getSelectedRow();

        serializationUtils.serialize(devices, Paths.get(pathProvider.getCurrentWorkingDirectory(), "serialized").toString(), bluetoothDevicesFileName);
        serializationUtils.serialize(new Pair((String) tableContent.getValueAt(selectedRow, 0), (String) tableContent.getValueAt(selectedRow, 1)), Paths.get(pathProvider.getCurrentWorkingDirectory(), "serialized").toString(), selectedDeviceFileName);
    }

    private void checkVirtualDevice(List<Pair<String, String>> deserializedDevices) {
        if (!LOAD_VIRTUAL_DEVICE) {
            return;
        }

        deserializedDevices.add(new Pair(Misc.EMPTY_MAC_ADDRESS, Misc.VIRTUAL_DEVICE_NAME));
    }

    private void searchDevices() {
        try {
            frame.setLoadingState(true);
            frame.jButton2.setEnabled(false);

            LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
                @Override
                public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                    String name;

                    try {
                        name = btDevice.getFriendlyName(true);
                    } catch (IOException ex) {
                        Logger.getLogger(BluetoothConnectionFrameService.class.getName()).log(Level.SEVERE, null, ex);
                        name = ex.getMessage();
                    }

                    frame.addDeviceToTable(new Pair(btDevice.getBluetoothAddress(), name));
                }

                @Override
                public void inquiryCompleted(int discType) {
                    frame.setLoadingState(false);
                }

                @Override
                public void serviceSearchCompleted(int transID, int respCode) {
                }

                @Override
                public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                }
            });
        } catch (BluetoothStateException ex) {
            Logger.getLogger(BluetoothConnectionFrameService.class.getName()).log(Level.WARNING, ex.getClass().getName(), ex);
        }
    }

    private void handleDeserializedSelectedDevice(List<Pair<String, String>> deserializedDevices, Pair<String, String> deserializedSelectedDevice) {
        for (int i = 0; i < deserializedDevices.size(); i++) {
            Pair<String, String> deserializedDevice = deserializedDevices.get(i);
            if (deserializedDevice.getKey().equals(deserializedSelectedDevice.getKey())) {
                frame.selectDevice(i);
                break;
            }
        }
    }
}
