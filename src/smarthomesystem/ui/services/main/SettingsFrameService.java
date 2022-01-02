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

    public SettingsFrameService() {
        rgbSettingsService = container.resolveDependencies(RgbSettingsService.class);
        microControllerSettingsService = container.resolveDependencies(MicroControllerSettingsService.class);
    }

    public void frameOpened() {
        frame.jTabbedPane1.addChangeListener((ChangeEvent e) -> {
            JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
            switch (tabbedPane.getSelectedIndex()) {
                case 0:
                    rgbSettingsService.tabSelected(frame);
                    break;
                case 1:
                    break;
                case 2:
                    microControllerSettingsService.tabSelected(frame);
                    break;
            }
        });

        rgbSettingsService.tabSelected(frame);
    }

    public RgbSettingsService getRgbSettingsService() {
        return rgbSettingsService;
    }

    public MicroControllerSettingsService getMicroControllerSettingsService() {
        return microControllerSettingsService;
    }
}
