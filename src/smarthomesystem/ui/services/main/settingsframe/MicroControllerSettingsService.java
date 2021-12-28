/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.main.settingsframe;

import arduino.MicroControllerConstants;
import arduino.MicroControllerDetailProjection;
import data.DateTimeService;
import java.io.IOException;
import java.util.Arrays;
import messaging.Command;
import messaging.MessageBroker;
import messaging.MessageFactory;
import messaging.ResponseCallback;
import messaging.ResponseListener;
import messaging.commands.TurnOffBuiltInLedCommand;
import messaging.commands.TurnOnBuiltInLedCommand;
import messaging.commands.responses.GenericCommandResponse;
import messaging.exceptions.PackingNotImplementedException;
import smarthomesystem.commands.TransitionStateCommand;
import smarthomesystem.repos.MicroControllerRepository;
import smarthomesystem.ui.frames.main.SettingsFrame;
import smarthomesystem.ui.services.main.settingsframe.constants.SettingsFrameConstants;

/**
 *
 * @author Manel
 */
public class MicroControllerSettingsService {

    private SettingsFrame frame;
    private final MicroControllerRepository microControllerRepository;
    private final DateTimeService dateTimeService;
    private final MessageFactory messageFactory;
    private final MessageBroker messageBroker;

    public MicroControllerSettingsService(
            MicroControllerRepository microControllerRepository,
            DateTimeService dateTimeService,
            MessageFactory messageFactory,
            MessageBroker messageBroker) {
        this.microControllerRepository = microControllerRepository;
        this.dateTimeService = dateTimeService;
        this.messageFactory = messageFactory;
        this.messageBroker = messageBroker;
    }

    public void loadMicroController() {
        MicroControllerDetailProjection activeMicroController = microControllerRepository.getActiveMicroController();

        frame.microControllerName.setText(activeMicroController.name);
        frame.noOfAnalogInputPins.setText(Integer.toString(activeMicroController.noOfAnalogInputPins));
        frame.noOfDigitalIoPins.setText(Integer.toString(activeMicroController.noOfDigitalIOPins));
        frame.pwmPins.setText(Arrays.toString(activeMicroController.pwmPins));
        frame.rxPins.setText(Arrays.toString(activeMicroController.rxPins));
        frame.txPins.setText(Arrays.toString(activeMicroController.txPins));
        frame.signature.setText(Integer.toString(activeMicroController.microControllerSignature));
        frame.connectionDate.setText(dateTimeService.getStringFromDateTime(activeMicroController.connectionDate));
    }

    public void tabSelected(SettingsFrame frame) {
        this.frame = frame;
        loadMicroController();
    }

    public void saveChanges() throws IOException, PackingNotImplementedException {
        Command setStatusPinStateCommand = calculateStatusPinState();
        Command setMicroControllerStateCommand = calculateMicroControllerState();

        messageBroker.send(setStatusPinStateCommand);
        messageBroker.send(setMicroControllerStateCommand, new ResponseListener(new ResponseCallback<GenericCommandResponse>(GenericCommandResponse.class) {
            @Override
            public void onResponse(GenericCommandResponse response) {
                System.out.println(response.isValid);
            }
        }));
    }

    private Command calculateStatusPinState() {
        if (frame.jToggleButton1.isSelected()) {
            return messageFactory.createReflectiveInstance(TurnOnBuiltInLedCommand.class);
        }

        return messageFactory.createReflectiveInstance(TurnOffBuiltInLedCommand.class);
    }

    private Command calculateMicroControllerState() {
        TransitionStateCommand transitionStateCommand = messageFactory.createReflectiveInstance(TransitionStateCommand.class);

        transitionStateCommand.desiredState = frame.jComboBox3.getSelectedItem().toString().equals(SettingsFrameConstants.GIVE_NO_RESPONSE_STRING)
                ? MicroControllerConstants.GIVE_NO_RESPONSE_IDENTIFIER : MicroControllerConstants.GIVE_RESPONSE_IDENTIFIER;

        return transitionStateCommand;
    }
}
