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
import data.PathProvider;
import data.SerializationUtils;
import encoding.EncodingAlgorithm;
import encoding.EncodingUtils;
import encoding.algorithms.HammingEncoder;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import messaging.ConnectionService;
import messaging.MessageBroker;
import messaging.MessageDispatcher;
import messaging.MessageFactory;
import messaging.MessageIdentifierGenerator;
import messaging.MessageUtils;
import messaging.bluetooth.BluetoothBroker;
import messaging.bluetooth.BluetoothModuleApiWrapper;
import messaging.bluetooth.BluetoothUtils;
import messaging.bluetooth.threading.factories.BluetoothInputWorkerFactory;
import messaging.bluetooth.threading.factories.BluetoothOutputWorkerFactory;
import messaging.exceptions.PackingNotImplementedException;
import messaging.MessageDispatcherWorker;
import messaging.events.EventDispatcher;
import messaging.events.threading.EventDispatcherWorker;
import messaging.exceptions.HandlersAlreadyInitializedException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import smarthomesystem.ui.ColorPallete;
import smarthomesystem.ui.ServiceableFrame;
import smarthomesystem.ui.frames.connection.BluetoothConnectionFrame;
import smarthomesystem.ui.frames.connection.ConnectionFrame;
import smarthomesystem.ui.frames.connection.BluetoothConnectingFrame;
import smarthomesystem.ui.frames.main.IndexFrame;
import smarthomesystem.ui.services.FrameService;
import smarthomesystem.ui.services.connection.BluetoothConnectionFrameService;
import smarthomesystem.ui.services.connection.ConnectionFrameService;
import smarthomesystem.ui.services.connection.BluetoothConnectingFrameService;
import smarthomesystem.ui.services.main.IndexFrameService;
import threading.ThreadPoolSupervisor;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;
import threading.factories.EventDispatcherWorkerFactory;
import threading.factories.MessageDispatcherWorkerFactory;

/**
 *
 * @author Manel
 */
public class SmartHomeSystem {

    public static InjectorInterface container;

    public void initSmartHomeSystem() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UnresolvableDependency, HandlersAlreadyInitializedException {
        initDependencyInjection();
        initHandlers();
        initDispatchers();
        mergeFormsAndFormServices();
    }

    public void terminateSmartHomeSystem() throws InterruptedException {
        try {
            ThreadPoolSupervisor threadPoolSupervisor = container.resolveDependencies(ThreadPoolSupervisor.class);
            threadPoolSupervisor.terminateAllThreads();
            Thread.sleep(1000);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | UnresolvableDependency ex) {
            Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.exit(0);
        }
    }

    private void initDependencyInjection() {
        try {
            MessageUtils messageUtils = new MessageUtils(new MessageIdentifierGenerator());
            MessageDispatcherWorkerFactory messageDispatcherWorkerFactory = new MessageDispatcherWorkerFactory();
            MessageDispatcherWorker messageDispatcherWorker = messageDispatcherWorkerFactory.createNewInstance();
            MessageDispatcher messageDispatcher = new MessageDispatcher(messageUtils, messageDispatcherWorker);
            Reflections reflections = new Reflections(
                    getClass().getPackage().getName(),
                    new SubTypesScanner(false)
            );

            EventDispatcherWorkerFactory eventDispatcherWorkerFactory = new EventDispatcherWorkerFactory();
            EventDispatcherWorker eventDispatcherWorker = eventDispatcherWorkerFactory.createNewInstance();
            EventDispatcher eventDispatcher = new EventDispatcher(reflections, eventDispatcherWorker);

            container = new Injector(new HashMap<>(), new HashMap<>(), new ArrayList<>());
            container
                    .addDependency(ColorPallete.class, ColorPallete.class)
                    .addDependency(PathProvider.class, PathProvider.class)
                    .addDependency(ConnectionService.class, ConnectionService.class)
                    .addDependency(SerializationUtils.class, SerializationUtils.class)
                    .addDependency(MessageUtils.class, messageUtils)
                    .addDependency(MessageFactory.class, MessageFactory.class)
                    .addDependency(MessageDispatcherWorkerFactory.class, MessageDispatcherWorkerFactory.class)
                    .addDependency(MessageDispatcherWorker.class, messageDispatcherWorker)
                    .addDependency(MessageDispatcher.class, messageDispatcher)
                    .addDependency(EncodingUtils.class, EncodingUtils.class)
                    .addDependency(EncodingAlgorithm.class, HammingEncoder.class)
                    .addDependency(ThreadPoolSupervisor.class, ThreadPoolSupervisor.class)
                    .addDependency(BluetoothUtils.class, BluetoothUtils.class)
                    .addDependency(BluetoothInputWorkerFactory.class, BluetoothInputWorkerFactory.class)
                    .addDependency(BluetoothOutputWorkerFactory.class, BluetoothOutputWorkerFactory.class)
                    .addDependency(BluetoothModuleApiWrapper.class, BluetoothModuleApiWrapper.class)
                    .addDependency(MessageBroker.class, BluetoothBroker.class)
                    .addDependency(Reflections.class, reflections)
                    .addDependency(EventDispatcher.class, eventDispatcher)
                    .addDependency(EventDispatcherWorker.class, eventDispatcherWorker)
                    .addDependency(ConnectionFrameService.class, ConnectionFrameService.class)
                    .addDependency(ConnectionFrame.class, ConnectionFrame.class)
                    .addDependency(BluetoothConnectionFrameService.class, BluetoothConnectionFrameService.class)
                    .addDependency(BluetoothConnectionFrame.class, BluetoothConnectionFrame.class)
                    .addDependency(BluetoothConnectingFrame.class, BluetoothConnectingFrame.class)
                    .addDependency(BluetoothConnectingFrameService.class, BluetoothConnectingFrameService.class)
                    .addDependency(IndexFrame.class, IndexFrame.class)
                    .addDependency(IndexFrameService.class, IndexFrameService.class);

            container.initialise();
        } catch (InterfaceNotImplemented | ClassNotInjectable | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | UnresolvableDependency ex) {
            Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private void initHandlers() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UnresolvableDependency, HandlersAlreadyInitializedException {
        container.resolveDependencies(MessageDispatcher.class).initHandlers();
        container.resolveDependencies(EventDispatcher.class).init();
    }

    private void initDispatchers() throws InstantiationException, IllegalAccessException, InvocationTargetException, UnresolvableDependency, NoSuchMethodException {
        ThreadPoolSupervisor threadPoolSupervisor = container.resolveDependencies(ThreadPoolSupervisor.class);
        EventDispatcher eventDispatcher = container.resolveDependencies(EventDispatcher.class);
        EventDispatcherWorker eventDispatcherWorker = container.resolveDependencies(EventDispatcherWorker.class);

        eventDispatcherWorker.setSubscribers(eventDispatcher.getEventSubscribers());
        threadPoolSupervisor.addThread(eventDispatcherWorker);
        try {
            threadPoolSupervisor.startThread(eventDispatcherWorker);
        } catch (ThreadNotFoundException | ThreadAlreadyStartedException ex) {
            Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private void mergeFormsAndFormServices() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UnresolvableDependency {
        Reflections reflections = container.resolveDependencies(Reflections.class);
        Set<Class<? extends FrameService>> frameServices = reflections.getSubTypesOf(FrameService.class);
        Iterator iterator = frameServices.iterator();

        while (iterator.hasNext()) {
            FrameService service = container.resolveDependencies((Class<? extends FrameService>) iterator.next());
            ServiceableFrame frame = container.resolveDependencies((Class<? extends ServiceableFrame>) service.getFrameType());

            service.setFrame(frame);
            frame.setService(service);
        }
    }

    private void openConnectionFrame() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UnresolvableDependency {
        ConnectionFrame connectionFrame = container.resolveDependencies(ConnectionFrame.class);

        connectionFrame.setLocationRelativeTo(null);
        connectionFrame.setVisible(true);
    }

    public static void main(String[] args) throws IOException, ThreadNotFoundException, ThreadAlreadyStartedException, IllegalArgumentException, IllegalAccessException, PackingNotImplementedException, InterfaceNotImplemented, ClassNotInjectable, NoSuchMethodException, NoSuchMethodException, NoSuchMethodException, InstantiationException, InvocationTargetException, UnresolvableDependency, ClassNotInjectable, HandlersAlreadyInitializedException {
        SmartHomeSystem smartHomeSystem = new SmartHomeSystem();
        smartHomeSystem.initSmartHomeSystem();

        smartHomeSystem.openConnectionFrame();
    }

    /*
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
        container.resolveDependencies(FormService.class).f = f;
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
     */
}
