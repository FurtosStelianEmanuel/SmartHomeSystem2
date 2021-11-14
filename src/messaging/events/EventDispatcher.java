/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.events;

import annotations.Injectable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;
import javassist.Modifier;
import messaging.events.threading.EventDispatcherWorker;
import org.reflections.Reflections;
import static smarthomesystem.SmartHomeSystem.container;

/**
 *
 * @author Manel
 */
@Injectable
public class EventDispatcher {

    private final Map<Class<? extends Event>, List<Pair<? extends EventHandler, Method>>> eventSubscribers;
    private final Reflections reflections;
    private final EventDispatcherWorker eventDispatcherWorker;

    public EventDispatcher(Reflections reflections, EventDispatcherWorker eventDispatcherWorker) {
        this.reflections = reflections;
        this.eventDispatcherWorker = eventDispatcherWorker;
        eventSubscribers = new HashMap<>();
    }

    public void init() {
        Set<Class<? extends EventHandler>> eventHandlers = reflections.getSubTypesOf(EventHandler.class);
        Iterator<Class<? extends EventHandler>> iterator = eventHandlers.iterator();

        while (iterator.hasNext()) {
            EventHandler eventHandlerInstance = container.resolveDependencies(iterator.next());

            for (Method method : eventHandlerInstance.getClass().getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    processSubscriber(eventHandlerInstance, method);
                }
            }
        }
    }

    public void dispatchEvent(Event event) {
        synchronized (eventDispatcherWorker) {
            eventDispatcherWorker.enqueueEvent(event);
            eventDispatcherWorker.notify();
        }
    }

    public void dispatchEvents(Event... events) {
        if (events.length == 0) {
            return;
        }

        synchronized (eventDispatcherWorker) {
            for (Event event : events) {
                eventDispatcherWorker.enqueueEvent(event);
            }

            eventDispatcherWorker.notify();
        }
    }

    public Map<Class<? extends Event>, List<Pair<? extends EventHandler, Method>>> getEventSubscribers() {
        return eventSubscribers;
    }

    private void processSubscriber(EventHandler eventHandlerInstance, Method method) {
        Class<Event> eventType = (Class<Event>) method.getParameterTypes()[0];
        if (eventSubscribers.containsKey(eventType)) {
            eventSubscribers.get(eventType).add(new Pair(eventHandlerInstance, method));
        } else {
            eventSubscribers.put(eventType, new ArrayList<Pair<? extends EventHandler, Method>>() {
                {
                    add(new Pair(eventHandlerInstance, method));
                }
            });
        }
    }
}
