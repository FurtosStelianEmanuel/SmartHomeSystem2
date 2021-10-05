/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.events.threading;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import messaging.events.Event;
import messaging.exceptions.NoHandlerForEventException;
import threading.BackgroundWorker;

/**
 *
 * @author Manel
 */
public class EventDispatcherWorker extends BackgroundWorker {

    private BlockingQueue<Event> eventsToDispatch;
    private Map<Class<? extends Event>, List<Pair<Object, Method>>> subscribers;

    public EventDispatcherWorker() {
        eventsToDispatch = new LinkedBlockingQueue<>();
    }

    @Override
    public synchronized void run() {
        while (alive) {
            try {
                wait();
                dequeueEvents();
            } catch (InterruptedException | NoHandlerForEventException ex) {
                Logger.getLogger(EventDispatcherWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        printTerminationMessage();
    }

    public void enqueueEvent(Event event) {
        eventsToDispatch.add(event);
    }

    private void dequeueEvents() throws NoHandlerForEventException {
        if (eventsToDispatch.isEmpty()) {
            return;
        }

        while (!eventsToDispatch.isEmpty()) {
            Event eventToDispatch = eventsToDispatch.remove();

            if (subscribers.containsKey(eventToDispatch.getClass())) {
                List<Pair<Object, Method>> handlers = subscribers.get(eventToDispatch.getClass());
                for (Pair<Object, Method> handler : handlers) {
                    try {
                        handler.getValue().invoke(handler.getKey(), eventToDispatch);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(EventDispatcherWorker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                throw new NoHandlerForEventException();
            }
        }
    }

    public void setEventsToDispatch(BlockingQueue<Event> eventsToDispatch) {
        this.eventsToDispatch = eventsToDispatch;
    }

    public void setSubscribers(Map<Class<? extends Event>, List<Pair<Object, Method>>> subscribers) {
        this.subscribers = subscribers;
    }
}
