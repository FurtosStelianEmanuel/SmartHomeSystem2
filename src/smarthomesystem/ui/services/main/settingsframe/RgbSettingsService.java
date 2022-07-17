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
import smarthomesystem.ui.services.main.DataHandlingException;
import smarthomesystem.ui.services.main.DataInteractionService;
import smarthomesystem.ui.services.main.TabbedFrameService;

/**
 *
 * @author Manel
 */
public class RgbSettingsService implements TabbedFrameService, DataInteractionService {

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

    @Override
    public void selectTab(SettingsFrame frame) {
        this.frame = frame;
        rgbInterface = (RgbFrameInterface) frame;
        frame.setPresetSettingsEnabled(false);
        setupActiveMicroController();
        setupRgbStrips();
    }

    @Override
    public void cancel() {
        rgbStripRepository.removeTemporaryStrips();
        hideSettingsFrameAndShowIndexFrame();
    }

    @Override
    public void save() throws DataHandlingException {
        RgbStripDetailProjection selectedStrip = getSelectedStrip();

        if (selectedStrip != null) {
            rgbStripRepository.updateStrip(selectedStrip);
            rgbStripRepository.markStripsAsTemporary(false);
        }

        try {
            rgbStripRepository.commitRecordsToStorage();
        } catch (FileNotFoundException | SerializationException ex) {
            throw new DataHandlingException(ex);
        }
    }

    @Override
    public void saveAndExit() throws DataHandlingException {
        RgbStripDetailProjection selectedStrip = getSelectedStrip();

        if (selectedStrip != null) {
            rgbStripRepository.updateStrip(selectedStrip);
            rgbStripRepository.markStripsAsTemporary(false);
        }

        try {
            rgbStripRepository.commitRecordsToStorage();
        } catch (FileNotFoundException | SerializationException ex) {
            throw new DataHandlingException(ex);
        }

        hideSettingsFrameAndShowIndexFrame();
    }

    public void hideSettingsFrameAndShowIndexFrame() {
        frame.dispose();
        container.resolveDependencies(IndexFrame.class).setVisible(true);
    }

    @Override
    public void add() {
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

        setupRgbStrips();
    }

    @Override
    public void delete(String stripDescription) {
        rgbStripRepository.removeStrip(stripDescription);
        frame.jComboBox2.removeItem(stripDescription);
        RgbStripDetailProjection[] strips = rgbStripRepository.getStrips();
        if (strips.length == 0) {
            rgbInterface.setPresetSettingsEnabled(false);
            rgbInterface.setSaveEnabled(true);
        }
    }

    public void setupRgbStrips() {
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

    @Override
    public void edit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
