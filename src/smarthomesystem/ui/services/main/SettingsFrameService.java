/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.ui.services.main;

import annotations.Injectable;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import smarthomesystem.ui.frames.main.SettingsFrame;
import smarthomesystem.ui.services.FrameService;
import static smarthomesystem.SmartHomeSystem.container;
import smarthomesystem.ui.services.main.settingsframe.LightSensorService;
import smarthomesystem.ui.services.main.settingsframe.MicroControllerSettingsService;
import smarthomesystem.ui.services.main.settingsframe.RgbSettingsService;

/**
 *
 * @author Manel
 */
@Injectable
public class SettingsFrameService extends FrameService<SettingsFrame> {

    private final RgbSettingsService rgbSettingsService;
    private final MicroControllerSettingsService microControllerSettingsService;
    private final LightSensorService lightSensorService;

    public SettingsFrameService() {
        rgbSettingsService = container.resolveDependencies(RgbSettingsService.class);
        microControllerSettingsService = container.resolveDependencies(MicroControllerSettingsService.class);
        lightSensorService = container.resolveDependencies(LightSensorService.class);
    }

    public void frameOpened() {
        frame.jTabbedPane1.addChangeListener((ChangeEvent e) -> {
            JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
            switch (tabbedPane.getSelectedIndex()) {
                case 0:
                    rgbSettingsService.selectTab(frame);
                    break;
                case 1:
                    break;
                case 2:
                    microControllerSettingsService.selectTab(frame);
                    break;
                case 3:
                    lightSensorService.selectTab(frame);
                    break;
            }
        });

        rgbSettingsService.selectTab(frame);
    }

    public <K> K getSubService(Class<K> type) {
        if (type == RgbSettingsService.class) {
            return (K) rgbSettingsService;
        } else if (type == MicroControllerSettingsService.class) {
            return (K) microControllerSettingsService;
        } else if (type == LightSensorService.class) {
            return (K) lightSensorService;
        }

        return null;
    }
}
