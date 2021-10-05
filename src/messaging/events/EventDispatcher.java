/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.events;

import annotations.Injectable;
import banana.exceptions.UnresolvableDependency;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;
import javassist.Modifier;
import messaging.CommandHandler;
import messaging.Message;
import messaging.events.threading.EventDispatcherWorker;
import org.reflections.Reflections;
import static smarthomesystem.SmartHomeSystem.container;

/**
 *
 * @author Manel
 */
@Injectable
public class EventDispatcher {

    private final Map<Class<? extends Event>, List<Pair<Object, Method>>> eventSubscribers;
    private final Reflections reflections;
    private final EventDispatcherWorker eventDispatcherWorker;

    public EventDispatcher(Reflections reflections, EventDispatcherWorker eventDispatcherWorker) {
        eventSubscribers = new HashMap<>();
        this.reflections = reflections;
        this.eventDispatcherWorker = eventDispatcherWorker;
    }

    public void init() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, UnresolvableDependency {
        Set<Class<? extends EventHandler>> eventHandlers = reflections.getSubTypesOf(EventHandler.class);
        Iterator<Class<? extends EventHandler>> iterator = eventHandlers.iterator();

        while (iterator.hasNext()) {
            EventHandler eventHandlerInstance = container.resolveDependencies(iterator.next());
            List<Pair<Object, Method>> handlers = new ArrayList<>();

            for (Method method : eventHandlerInstance.getClass().getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    Class<Event> eventType = (Class<Event>) method.getParameterTypes()[0];
                    if (eventSubscribers.containsKey(eventType)) {
                        eventSubscribers.get(eventType).add(new Pair(eventHandlerInstance, method));
                    } else {
                        eventSubscribers.put((Class<Event>) method.getParameterTypes()[0], new ArrayList<Pair<Object, Method>>() {
                            {
                                add(new Pair(eventHandlerInstance, method));
                            }
                        });
                    }
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

    public Map<Class<? extends Event>, List<Pair<Object, Method>>> getEventSubscribers() {
        return eventSubscribers;
    }
}
