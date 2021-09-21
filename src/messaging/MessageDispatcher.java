/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Manel
 */
public class MessageDispatcher {

    private final MessageUtils messageUtils;
    private List<ResponseListener> listeners;
    private List<ResponseListener> listenersPendingDeletion;
    private byte[] messageToDispatch;
    private byte[] partialMessageToDispatch;
    private boolean partialMessagesEnabled = false;
    private final MessageDispatcherWorker messageDispatcherWorker;

    public MessageDispatcher(MessageUtils messageUtils, MessageDispatcherWorker messageDispatcherWorker) {
        this.messageUtils = messageUtils;
        listeners = new ArrayList<>();
        listenersPendingDeletion = new ArrayList<>();
        this.messageDispatcherWorker = messageDispatcherWorker;
    }

    public void queueMessage(byte[] data) {
        setMessageToDispatch(data);

        synchronized (messageDispatcherWorker) {
            messageDispatcherWorker.notify();
        }
    }

    public void queuePartialMessage(byte[] data) {
        setPartialMessageToDispatch(data);

        synchronized (messageDispatcherWorker) {
            messageDispatcherWorker.notify();
        }
    }

    public void dispatchMessages() {
        if (arePartialMessagesEnabled() && partialMessageToDispatch != null) {
            callPartialMessageListeners();
        }
        if (messageToDispatch != null) {
            callResponseListeners();
        }
    }

    public void addListener(ResponseListener responseListener) {
        responseListener.setIdentifier(messageUtils.getMessageIdentifierGenerator().getIdentifier(responseListener.getCallback().getType()));
        listeners.add(responseListener);
    }

    private void callResponseListeners() {
        for (ResponseListener listener : listeners) {
            if (listener.getIdentifier() == messageToDispatch[0]) {
                callWaitingListener(listener, messageToDispatch);
                determineListenerDeletion(listener);
            }
        }
        cleanupListeners();
    }

    private void callPartialMessageListeners() {
        for (ResponseListener listener : listeners) {
            if (messageUtils.isUnpackable(partialMessageToDispatch)) {
                callWaitingListener(listener, partialMessageToDispatch);
                determineListenerDeletion(listener);
            }
        }
        cleanupListeners();
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

    public boolean arePartialMessagesEnabled() {
        return partialMessagesEnabled;
    }

    public void setPartialMessagesEnabled(boolean partialMessagesEnabled) {
        this.partialMessagesEnabled = partialMessagesEnabled;
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

    public byte[] getPartialMessageToDispatch() {
        return partialMessageToDispatch;
    }

    public void setMessageToDispatch(byte[] messageToDispatch) {
        this.messageToDispatch = messageToDispatch;
    }

    public void setPartialMessageToDispatch(byte[] partialMessageToDispatch) {
        this.partialMessageToDispatch = partialMessageToDispatch;
    }
}
