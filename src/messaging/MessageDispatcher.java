/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import banana.exceptions.UnresolvableDependency;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javassist.Modifier;
import messaging.exceptions.HandlersAlreadyInitializedException;
import org.reflections.Reflections;
import static smarthomesystem.SmartHomeSystem.container;

/**
 *
 * @author Manel
 */
public class MessageDispatcher {

    private final MessageUtils messageUtils;
    private List<ResponseListener> listeners;
    private List<ResponseListener> listenersPendingDeletion;
    private byte[] messageToDispatch;
    private final MessageDispatcherWorker messageDispatcherWorker;
    private Map<Class<? extends Message>, Pair<CommandHandler, Method>> commandHandlers;
    private HotwiredDataStreamAdapter hotwiredDataStreamAdapter;

    public MessageDispatcher(MessageUtils messageUtils, MessageDispatcherWorker messageDispatcherWorker) {
        this.messageUtils = messageUtils;
        listeners = new ArrayList<>();
        listenersPendingDeletion = new ArrayList<>();
        this.messageDispatcherWorker = messageDispatcherWorker;
        commandHandlers = new HashMap<>();
    }

    public void setHotwiredDataStream(HotwiredDataStreamAdapter hotwiredDataStreamAdapter) {
        if (hotwiredDataStreamAdapter == null) {
            return;
        }

        this.hotwiredDataStreamAdapter = hotwiredDataStreamAdapter;
        this.hotwiredDataStreamAdapter.setEnabled(true);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (hotwiredDataStreamAdapter.isEnabled()) {
                    hotwiredDataStreamAdapter.onResponseTimeout();
                }
            }
        }, hotwiredDataStreamAdapter.getTimeout());
    }

    public void queueMessage(byte[] data) {
        setMessageToDispatch(data);

        synchronized (messageDispatcherWorker) {
            messageDispatcherWorker.notify();
        }
    }

    public void dispatchMessages() {
        if (messageToDispatch == null) {
            return;
        }

        if (!isHotwired()) {
            regularDispatch();
        } else {
            hotwiredDataStreamAdapter.onHotwiredResponse(messageToDispatch);
        }
    }

    private void regularDispatch() {
        try {
            callHandlers();
            callResponseListeners();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(MessageDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addListener(ResponseListener responseListener) {
        responseListener.setIdentifier(messageUtils.getMessageIdentifierGenerator().getIdentifier(responseListener.getCallback().getType()));
        listeners.add(responseListener);
    }

    public void initHandlers() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UnresolvableDependency, HandlersAlreadyInitializedException {
        if (!commandHandlers.isEmpty()) {
            throw new HandlersAlreadyInitializedException();
        }

        Reflections reflections = container.resolveDependencies(Reflections.class);
        Set<Class<? extends CommandHandler>> commandHandlerDescendants = reflections.getSubTypesOf(CommandHandler.class);
        Iterator<Class<? extends CommandHandler>> iterator = commandHandlerDescendants.iterator();
        while (iterator.hasNext()) {
            Class<? extends CommandHandler> next = iterator.next();
            CommandHandler commandHandler = container.resolveDependencies(next);
            for (Method method : next.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    commandHandlers.put((Class<Message>) method.getParameterTypes()[0], new Pair(commandHandler, method));
                }
            }
        }
    }

    private void callResponseListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            ResponseListener listener = listeners.get(i);
            if (listener.getIdentifier() == messageToDispatch[0]) {
                callWaitingListener(listener, messageToDispatch);
                determineListenerDeletion(listener);
            }
        }

        cleanupListeners();
    }

    private void callHandlers() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Message unpacked = messageUtils.unpack(messageToDispatch);
        if (unpacked == null) {
            return;
        }

        if (commandHandlers.containsKey(unpacked.getClass())) {
            Pair<CommandHandler, Method> handler = commandHandlers.get(unpacked.getClass());
            handler.getValue().invoke(handler.getKey(), unpacked);
        }
    }

    private void callWaitingListener(ResponseListener listener, byte[] rawData) {
        if (!listener.timeoutOccured()) {
            listener.getCallback().onResponse(messageUtils.unpack(rawData, listener.getCallback().getType()));
            listener.setResponseReceived(true);
        } else {
            listener.responseArrivedAfterTimeout(rawData);
        }
    }

    private void determineListenerDeletion(ResponseListener listener) {
        if (!listener.isPersistent()) {
            listenersPendingDeletion.add(listener);
        }
    }

    private void cleanupListeners() {
        for (ResponseListener listener : listenersPendingDeletion) {
            listeners.remove(listener);
        }
    }

    public List<ResponseListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<ResponseListener> listeners) {
        this.listeners = listeners;
    }

    public void setListenersPendingDeletion(List<ResponseListener> listenersPendingDeletion) {
        this.listenersPendingDeletion = listenersPendingDeletion;
    }

    public List<ResponseListener> getListenersPendingDeletion() {
        return listenersPendingDeletion;
    }

    public byte[] getMessageToDispatch() {
        return messageToDispatch;
    }

    public void setMessageToDispatch(byte[] messageToDispatch) {
        this.messageToDispatch = messageToDispatch;
    }

    public void setCommandHandlers(Map<Class<? extends Message>, Pair<CommandHandler, Method>> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    public boolean isHotwired() {
        return hotwiredDataStreamAdapter != null && hotwiredDataStreamAdapter.isEnabled();
    }

    public void disableHotWire() {
        hotwiredDataStreamAdapter.setEnabled(false);
    }
}
