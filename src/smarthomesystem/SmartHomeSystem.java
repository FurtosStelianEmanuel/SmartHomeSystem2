/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem;

import arduino.MicroControllerFactory;
import banana.Injector;
import banana.InjectorInterface;
import banana.exceptions.ClassNotInjectable;
import banana.exceptions.InterfaceNotImplemented;
import banana.exceptions.UnresolvableDependency;
import data.DateTimeService;
import data.PathProvider;
import data.Serializer;
import encoding.EncodingAlgorithm;
import encoding.EncodingUtils;
import encoding.algorithms.HammingEncoder;
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
import messaging.MessageDispatcherWorker;
import messaging.events.EventDispatcher;
import messaging.events.threading.EventDispatcherWorker;
import messaging.exceptions.HandlersAlreadyInitializedException;
import messaging.virtualdevice.VirtualDeviceMessageBrokerFactory;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import smarthomesystem.repos.MicroControllerRepository;
import smarthomesystem.repos.RgbStripRepository;
import smarthomesystem.ui.ColorPalette;
import smarthomesystem.ui.ServiceableFrame;
import smarthomesystem.ui.frames.connection.*;
import smarthomesystem.ui.frames.main.*;
import smarthomesystem.ui.services.FrameService;
import smarthomesystem.ui.services.main.SettingsFrameService;
import smarthomesystem.ui.services.connection.*;
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

    public void initSmartHomeSystem() throws HandlersAlreadyInitializedException {
        initDependencyInjection();
        initHandlers();
        initDispatchers();
        mergeFormsAndFormServices();
    }

    public void terminateSmartHomeSystem() {
        ThreadPoolSupervisor threadPoolSupervisor = container.resolveDependencies(ThreadPoolSupervisor.class);
        threadPoolSupervisor.terminateAllThreads();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.exit(0);
    }

    private void initDependencyInjection() {
        try {
            MessageUtils messageUtils = new MessageUtils(new MessageIdentifierGenerator());
            MessageDispatcherWorkerFactory messageDispatcherWorkerFactory = new MessageDispatcherWorkerFactory();
            MessageDispatcherWorker messageDispatcherWorker = messageDispatcherWorkerFactory.createNewInstance();
            Reflections reflections = new Reflections(
                    getClass().getPackage().getName(),
                    new SubTypesScanner(false)
            );
            MessageDispatcher messageDispatcher = new MessageDispatcher(reflections, messageUtils, messageDispatcherWorker);
            EventDispatcherWorkerFactory eventDispatcherWorkerFactory = new EventDispatcherWorkerFactory();
            EventDispatcherWorker eventDispatcherWorker = eventDispatcherWorkerFactory.createNewInstance();
            EventDispatcher eventDispatcher = new EventDispatcher(reflections, eventDispatcherWorker);

            container = new Injector(new HashMap<>(), new HashMap<>(), new ArrayList<>());
            container
                    .addDependency(VirtualDeviceMessageBrokerFactory.class, VirtualDeviceMessageBrokerFactory.class)
                    .addDependency(DateTimeService.class, DateTimeService.class)
                    .addDependency(MicroControllerRepository.class, MicroControllerRepository.class)
                    .addDependency(ColorPalette.class, ColorPalette.class)
                    .addDependency(PathProvider.class, PathProvider.class)
                    .addDependency(ConnectionService.class, ConnectionService.class)
                    .addDependency(Serializer.class, Serializer.class)
                    .addDependency(RgbStripRepository.class, RgbStripRepository.class)
                    .addDependency(Reflections.class, reflections)
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
                    .addDependency(EventDispatcher.class, eventDispatcher)
                    .addDependency(EventDispatcherWorker.class, eventDispatcherWorker)
                    .addDependency(ConnectionFrameService.class, ConnectionFrameService.class)
                    .addDependency(ConnectionFrame.class, ConnectionFrame.class)
                    .addDependency(BluetoothConnectionFrameService.class, BluetoothConnectionFrameService.class)
                    .addDependency(BluetoothConnectionFrame.class, BluetoothConnectionFrame.class)
                    .addDependency(BluetoothConnectingFrame.class, BluetoothConnectingFrame.class)
                    .addDependency(BluetoothConnectingFrameService.class, BluetoothConnectingFrameService.class)
                    .addDependency(IndexFrame.class, IndexFrame.class)
                    .addDependency(IndexFrameService.class, IndexFrameService.class)
                    .addDependency(SettingsFrame.class, SettingsFrame.class)
                    .addDependency(SettingsFrameService.class, SettingsFrameService.class)
                    .addDependency(MicroControllerFactory.class, MicroControllerFactory.class);

            container.initialise();
        } catch (InterfaceNotImplemented | ClassNotInjectable | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | UnresolvableDependency ex) {
            Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
            terminateSmartHomeSystem();
        }
    }

    private void initHandlers() throws HandlersAlreadyInitializedException {
        container.resolveDependencies(MessageDispatcher.class).initHandlers();
        container.resolveDependencies(EventDispatcher.class).init();
    }

    private void initDispatchers() {
        ThreadPoolSupervisor threadPoolSupervisor = container.resolveDependencies(ThreadPoolSupervisor.class);
        EventDispatcher eventDispatcher = container.resolveDependencies(EventDispatcher.class);
        EventDispatcherWorker eventDispatcherWorker = container.resolveDependencies(EventDispatcherWorker.class);

        eventDispatcherWorker.setSubscribers(eventDispatcher.getEventSubscribers());
        threadPoolSupervisor.addThread(eventDispatcherWorker);
        try {
            threadPoolSupervisor.startThread(eventDispatcherWorker);
        } catch (ThreadNotFoundException | ThreadAlreadyStartedException ex) {
            Logger.getLogger(SmartHomeSystem.class.getName()).log(Level.SEVERE, null, ex);
            terminateSmartHomeSystem();
        }
    }

    private void mergeFormsAndFormServices() {
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

    private void openConnectionFrame() {
        ConnectionFrame connectionFrame = container.resolveDependencies(ConnectionFrame.class);

        connectionFrame.setLocationRelativeTo(null);
        connectionFrame.setVisible(true);
    }

    public static void main(String[] args) throws HandlersAlreadyInitializedException {
        SmartHomeSystem smartHomeSystem = new SmartHomeSystem();
        smartHomeSystem.initSmartHomeSystem();

        smartHomeSystem.openConnectionFrame();
    }
}