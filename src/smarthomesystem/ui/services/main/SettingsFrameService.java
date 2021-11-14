/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.main;

import annotations.Injectable;
import arduino.MicroControllerDetailProjection;
import java.io.IOException;
import messaging.MessageBroker;
import messaging.MessageFactory;
import messaging.exceptions.PackingNotImplementedException;
import smarthomesystem.commands.ModulatePulseWidthCommand;
import smarthomesystem.repos.MicroControllerRepository;
import smarthomesystem.ui.frames.main.SettingsFrame;
import smarthomesystem.ui.services.FrameService;
import static smarthomesystem.SmartHomeSystem.container;

/**
 *
 * @author Manel
 */
@Injectable
public class SettingsFrameService extends FrameService<SettingsFrame> {

    private final MicroControllerRepository microControllerRepository;
    private final MessageFactory messageFactory;

    public SettingsFrameService(MicroControllerRepository microControllerRepository, MessageFactory messageFactory) {
        this.microControllerRepository = microControllerRepository;
        this.messageFactory = messageFactory;
    }

    public void frameOpened() {
        MicroControllerDetailProjection activeMicroController = microControllerRepository.getActiveMicroController();
        frame.setComboBoxPins(frame.jComboBox1, activeMicroController.pwmPins);
        frame.setComboBoxPins(frame.jComboBox4, activeMicroController.pwmPins);
        frame.setComboBoxPins(frame.jComboBox5, activeMicroController.pwmPins);
    }

    public void testBrightness(String pin, int brightness) throws IOException, PackingNotImplementedException {
        ModulatePulseWidthCommand modulatePulseWidthCommand = messageFactory.createReflectiveInstance(ModulatePulseWidthCommand.class);
        MessageBroker messageBroker = container.resolveDependencies(MessageBroker.class);

        modulatePulseWidthCommand.modulation = brightness;
        modulatePulseWidthCommand.pin = Integer.valueOf(pin);

        messageBroker.send(modulatePulseWidthCommand);
    }
}
