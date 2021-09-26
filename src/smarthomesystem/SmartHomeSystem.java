/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem;

import banana.Injector;
import banana.InjectorInterface;
import banana.exceptions.ClassNotInjectable;
import banana.exceptions.InterfaceNotImplemented;
import banana.exceptions.UnresolvableDependency;
import encoding.EncodingAlgorithm;
import encoding.EncodingUtils;
import encoding.algorithms.HammingEncoder;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import messaging.BrokerConfig;
import messaging.CommandHandler;
import messaging.MessageBroker;
import messaging.MessageDispatcher;
import messaging.MessageFactory;
import messaging.MessageIdentifierGenerator;
import messaging.MessageUtils;
import messaging.bluetooth.BluetoothBroker;
import messaging.bluetooth.BluetoothConfig;
import messaging.bluetooth.BluetoothModuleApiWrapper;
import messaging.bluetooth.BluetoothUtils;
import messaging.bluetooth.threading.factories.BluetoothInputWorkerFactory;
import messaging.bluetooth.threading.factories.BluetoothOutputWorkerFactory;
import messaging.exceptions.PackingNotImplementedException;
import testcommunications.CommsTester;
import messaging.MessageDispatcherWorker;
import messaging.Query;
import messaging.Response;
import messaging.ResponseCallback;
import messaging.ResponseListener;
import messaging.TimeoutProtocol;
import messaging.bluetooth.threading.BluetoothInputWorker;
import messaging.commands.TurnOffBuiltInLedCommand;
import messaging.commands.TurnOnBuiltInLedCommand;
import messaging.commands.responses.TurnOffBuiltInLedCommandResponse;
import messaging.commands.responses.TurnOnBuiltInLedCommandResponse;
import messaging.exceptions.HandlersAlreadyInitializedException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import smarthomesystem.commandhandlers.ArduinoPeripheralsCommandHandler;
import smarthomesystem.commands.SetSerialSettingsCommand;
import smarthomesystem.commands.responses.SetSerialSettingsCommandResponse;
import smarthomesystem.queries.DistanceSensorQuery;
import smarthomesystem.queries.results.DistanceSensorQueryResult;
import threading.ThreadPoolSupervisor;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;
import threading.factories.MessageDispatcherWorkerFactory;

/**
 *
 * @author Manel
 */
public class SmartHomeSystem {

    public static InjectorInterface container;

    private void initDependencyInjection() {
        try {
            MessageUtils messageUtils = new MessageUtils(new MessageIdentifierGenerator());
            MessageDispatcherWorkerFactory messageDispatcherWorkerFactory = new MessageDispatcherWorkerFactory();
            MessageDispatcherWorker messageDispatcherWorker = messageDispatcherWorkerFactory.createNewInstance();
            MessageDispatcher messageDispatcher = new MessageDispatcher(messageUtils, messageDispatcherWorker);

            container = new Injector(new HashMap<>(), new HashMap<>(), new ArrayList<>());
            container
                    .addDependency(MessageIdentifierGenerator.class, MessageIdentifierGenerator.class)
                    .addDependency(MessageUtils.class, MessageUtils.class)
                    .addDependency(MessageFactory.class, MessageFactory.class)
                    .addDependency(MessageDispatcherWorkerFactory.class, MessageDispatcherWorkerFactory.class)
                    .addDependency(MessageDispatcherWorker.class, messageDispatcherWorker)
                    .addDependency(MessageDispatcher.class, messageDispatcher)
                    .addDependency(EncodingUtils.class, EncodingUtils.class)
                    .addDependency(EncodingAlgorithm.class, HammingEncoder.class)
                    .addDependency(ThreadPoolSupervisor.class, ThreadPoolSupervisor.class)
                    .addDependency(BluetoothInputWorkerFactory.class, BluetoothInputWorkerFactory.class)
                    .addDependency(BluetoothOutputWorkerFactory.class, BluetoothOutputWorkerFactory.class)
                    .addDependency(BluetoothModuleApiWrapper.class, BluetoothModuleApiWrapper.class)
                    .addDependency(BluetoothUtils.class, BluetoothUtils.class)
                    .addDependency(MessageBroker.class, BluetoothBroker.class)
                    .addDependency(Reflections.class, new Reflections(getClass().getPackage().getName(), new SubTypesScanner(false)));

            container.initialise();
        } catch (InterfaceNotImplemented | ClassNotInjectable | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | UnresolvableDependency ex) {
            Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private void initHandlers() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UnresolvableDependency, HandlersAlreadyInitializedException {
        container.resolveDependencies(MessageDispatcher.class).initHandlers();
    }
    
    void initSmartHomeSystem() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UnresolvableDependency, HandlersAlreadyInitializedException {
        try {
            initDependencyInjection();
            initHandlers();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    public static void main(String[] args) throws IOException, ThreadNotFoundException, ThreadAlreadyStartedException, IllegalArgumentException, IllegalAccessException, PackingNotImplementedException, InterfaceNotImplemented, ClassNotInjectable, NoSuchMethodException, NoSuchMethodException, NoSuchMethodException, InstantiationException, InvocationTargetException, UnresolvableDependency, ClassNotInjectable, HandlersAlreadyInitializedException {
        SmartHomeSystem smartHomeSystem = new SmartHomeSystem();
        smartHomeSystem.initSmartHomeSystem();

        MessageBroker broker = container.resolveDependencies(BluetoothBroker.class);
        BrokerConfig config = new BluetoothConfig() {
            {
                setAddress("98D311F85C34");
            }
        };
        broker.initConnection(config);
        broker.startBackgroundWorkers();
        JFrame f = new JFrame();
        f.setLayout(new FlowLayout());
        JButton b = new JButton("Apasa ma");
        CommsTester tester = new CommsTester(
                broker,
                container.resolveDependencies(MessageFactory.class),
                container.resolveDependencies(MessageDispatcher.class),
                16,
                63);
        b.addActionListener(new ActionListener() {
            boolean runQuery = false;
            int count = 4;
            int timeout = 125;

            @Override
            public void actionPerformed(ActionEvent ae) {

                try {
                    MessageFactory messageFactory = container.resolveDependencies(MessageFactory.class);

                    switch (count) {
                        case 0:
                            try {
                                Query query = messageFactory.createReflectiveInstance(DistanceSensorQuery.class);
                                broker.send(query, new ResponseListener(false, new ResponseCallback<DistanceSensorQueryResult>(DistanceSensorQueryResult.class) {
                                    @Override
                                    public void onResponse(DistanceSensorQueryResult commandResponse) {
                                        System.out.println("rezultat query " + commandResponse.distance);
                                    }
                                }, new TimeoutProtocol(1000) {
                                    @Override
                                    public void onTimeout() {
                                        System.out.println("Timeout okkr");
                                    }
                                }
                                ));
                            } catch (IllegalAccessException | PackingNotImplementedException | IOException ex) {
                                Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        case 1:
                            try {
                                for (int i = 0; i < 10; i++) {
                                    tester.run();
                                }
                            } catch (Exception ex) {
                                Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        case 2:
                            SetSerialSettingsCommand command = messageFactory.createReflectiveInstance(SetSerialSettingsCommand.class);
                            command.bufferSize = 64;
                            command.timeout = timeout;
                            timeout += 10;
                            if (timeout > 255) {
                                timeout = 0;
                            }
                            System.out.println("am trimis timeout " + command.timeout + " zis in byte " + (byte) command.timeout);
                            System.out.println("am trimis bufferSize " + command.bufferSize + " zis in byte" + (byte) command.bufferSize);
                            broker.send(command, new ResponseListener(false, new ResponseCallback<SetSerialSettingsCommandResponse>(SetSerialSettingsCommandResponse.class) {
                                @Override
                                public void onResponse(SetSerialSettingsCommandResponse commandResponse) {
                                    System.out.println("timeout " + commandResponse.timeoutSet + " zis in byte " + (byte) commandResponse.timeoutSet);
                                    System.out.println("buffer " + commandResponse.bufferSizeSet + " zis in byte " + (byte) commandResponse.bufferSizeSet);
                                }
                            }, new TimeoutProtocol(1000) {
                                @Override
                                public void onTimeout() {
                                    System.out.println("Timeout okaa");
                                }
                            }));
                            break;
                        case 3:
                            TurnOnBuiltInLedCommand turnOnBuiltInLed = messageFactory.createReflectiveInstance(TurnOnBuiltInLedCommand.class);
                            broker.send(turnOnBuiltInLed, new ResponseListener(new ResponseCallback<TurnOnBuiltInLedCommandResponse>(TurnOnBuiltInLedCommandResponse.class) {
                                @Override
                                public void onResponse(TurnOnBuiltInLedCommandResponse commandResponse) {
                                    System.out.println("A aprins pin " + commandResponse.pinNumber);
                                }
                            }));
                            break;
                        case 4:
                            TurnOffBuiltInLedCommand turnOffBuiltInLed = messageFactory.createReflectiveInstance(TurnOffBuiltInLedCommand.class);
                            broker.send(turnOffBuiltInLed, new ResponseListener(new ResponseCallback<TurnOffBuiltInLedCommandResponse>(TurnOffBuiltInLedCommandResponse.class) {
                                @Override
                                public void onResponse(TurnOffBuiltInLedCommandResponse commandResponse) {
                                    System.out.println("A stins pin " + commandResponse.pinNumber);
                                }
                            }));
                        default:
                            break;
                    }
                    count = (++count) % 5;
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | UnresolvableDependency | PackingNotImplementedException | IOException ex) {
                    Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JButton exit = new JButton("iesi acasa");
        exit.addActionListener((ActionEvent ae) -> {

        });
        f.add(exit);
        f.add(b);
        f.setSize(200, 200);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
