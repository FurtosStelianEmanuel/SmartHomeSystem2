/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.main.settingsframe;

import arduino.MicroControllerDetailProjection;
import bananaconvert.marshaler.exception.SerializationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;
import messaging.MessageBroker;
import messaging.MessageFactory;
import messaging.ResponseCallback;
import messaging.ResponseListener;
import messaging.exceptions.PackingNotImplementedException;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.commands.ModulatePulseWidthCommand;
import smarthomesystem.commands.responses.ModulatePulseWidthCommandResponse;
import smarthomesystem.ledstrips.RgbStripDetailProjection;
import smarthomesystem.repos.MicroControllerRepository;
import smarthomesystem.repos.RgbStripRepository;
import smarthomesystem.ui.frames.main.IndexFrame;
import smarthomesystem.ui.frames.main.SettingsFrame;
import smarthomesystem.ui.frames.main.settingsframe.RgbFrameInterface;

/**
 *
 * @author Manel
 */
public class RgbSettingsService {

    private SettingsFrame frame;
    private RgbFrameInterface rgbInterface;
    private final RgbStripRepository rgbStripRepository;
    private final MicroControllerRepository microControllerRepository;
    private final MessageFactory messageFactory;

    ResponseCallback<ModulatePulseWidthCommandResponse> responseCallbackForModulatePulseWidthCommand = new ResponseCallback<ModulatePulseWidthCommandResponse>(ModulatePulseWidthCommandResponse.class) {
        @Override
        public void onResponse(ModulatePulseWidthCommandResponse response) {
            System.out.println("Receiving " + response.modulation);
        }
    };
    ResponseListener responseListenerForModulatePulseWidthCommand = new ResponseListener(true, responseCallbackForModulatePulseWidthCommand);

    public RgbSettingsService(
            RgbStripRepository rgbStripRepository,
            MicroControllerRepository microControllerRepository,
            MessageFactory messageFactory) {
        this.rgbStripRepository = rgbStripRepository;
        this.microControllerRepository = microControllerRepository;
        this.messageFactory = messageFactory;
    }

    public void tabSelected(SettingsFrame frame) {
        this.frame = frame;
        rgbInterface = (RgbFrameInterface) frame;
        frame.setPresetSettingsEnabled(false);
        setupActiveMicroController();
        setupRgbStrip();
    }

    public void cancelStripChanges() {
        rgbStripRepository.removeTemporaryStrips();
        hideSettingsFrameAndShowIndexFrame();
    }

    public void saveRgbSettings() throws FileNotFoundException, SerializationException {
        RgbStripDetailProjection selectedStrip = getSelectedStrip();

        if (selectedStrip != null) {
            rgbStripRepository.updateStrip(selectedStrip);
            rgbStripRepository.markStripsAsTemporary(false);
        }

        try {
            rgbStripRepository.commitRecordsToStorage();
        } catch (FileNotFoundException | SerializationException ex) {
            throw ex;
        }
    }

    public void saveRgbSettingsAndExit() throws FileNotFoundException, SerializationException {
        RgbStripDetailProjection selectedStrip = getSelectedStrip();

        if (selectedStrip != null) {
            rgbStripRepository.updateStrip(selectedStrip);
            rgbStripRepository.markStripsAsTemporary(false);
        }

        try {
            rgbStripRepository.commitRecordsToStorage();
        } catch (FileNotFoundException | SerializationException ex) {
            throw ex;
        }

        hideSettingsFrameAndShowIndexFrame();
    }

    public void hideSettingsFrameAndShowIndexFrame() {
        frame.dispose();
        container.resolveDependencies(IndexFrame.class).setVisible(true);
    }

    public void addNewStrip() {
        String stripDescription = JOptionPane.showInputDialog(frame, "Led strip description");
        MicroControllerDetailProjection activeMicroController = microControllerRepository.getActiveMicroController();

        rgbStripRepository.addOrUpdateStrip(new RgbStripDetailProjection() {
            {
                sequence = frame.jComboBox2.getItemCount();
                redPin = activeMicroController.pwmPins[0];
                greenPin = activeMicroController.pwmPins[0];
                bluePin = activeMicroController.pwmPins[0];
                description = stripDescription;
                isTemporary = true;
            }
        });

        setupRgbStrip();
    }

    public void removeStrip(String stripDescription) {
        rgbStripRepository.removeStrip(stripDescription);
        frame.jComboBox2.removeItem(stripDescription);
        RgbStripDetailProjection[] strips = rgbStripRepository.getStrips();
        if (strips.length == 0) {
            rgbInterface.setPresetSettingsEnabled(false);
            rgbInterface.setSaveEnabled(true);
        }
    }

    public void setupRgbStrip() {
        RgbStripDetailProjection[] strips = rgbStripRepository.getStrips();

        if (strips.length == 0) {
            rgbInterface.setPresetSettingsEnabled(false);
        } else {
            rgbInterface.setLedStripsDescriptions(strips);
            rgbInterface.setPresetSettingsEnabled(true);
        }
    }

    public void rgbStripChanged(String description) {
        RgbStripDetailProjection selectedStrip = rgbStripRepository.getStripByDescription(description);
        rgbStripRepository.updateStrip(selectedStrip);

        rgbInterface.setSelectedStrip(selectedStrip);
    }

    public void setBrightness(String pin, int brightness) throws IOException, PackingNotImplementedException {
        ModulatePulseWidthCommand modulatePulseWidthCommand = messageFactory.createReflectiveInstance(ModulatePulseWidthCommand.class);
        MessageBroker messageBroker = container.resolveDependencies(MessageBroker.class);

        modulatePulseWidthCommand.modulation = brightness;
        modulatePulseWidthCommand.pin = Integer.valueOf(pin);

        System.out.println("Sending " + brightness);
        messageBroker.send(modulatePulseWidthCommand, responseListenerForModulatePulseWidthCommand);
    }

    private RgbStripDetailProjection getSelectedStrip() {
        RgbStripDetailProjection strip = rgbStripRepository.getStripByDescription((String) frame.jComboBox2.getSelectedItem());

        if (strip == null) {
            return null;
        }

        strip.redPin = Integer.parseInt(frame.jComboBox1.getSelectedItem().toString());
        strip.greenPin = Integer.parseInt(frame.jComboBox4.getSelectedItem().toString());
        strip.bluePin = Integer.parseInt(frame.jComboBox5.getSelectedItem().toString());
        strip.isPrimary = frame.jCheckBox1.isSelected();

        return strip;
    }

    private void setupActiveMicroController() {
        MicroControllerDetailProjection activeMicroController = microControllerRepository.getActiveMicroController();

        rgbInterface.setPinsForColorChannel(frame.jComboBox1, activeMicroController.pwmPins);
        rgbInterface.setPinsForColorChannel(frame.jComboBox4, activeMicroController.pwmPins);
        rgbInterface.setPinsForColorChannel(frame.jComboBox5, activeMicroController.pwmPins);
    }
}
