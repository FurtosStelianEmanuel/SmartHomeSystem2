/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging;

import banana.InjectorInterface;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;
import messaging.events.Event;
import messaging.events.EventDispatcher;
import messaging.events.EventHandler;
import messaging.events.MyEvent;
import messaging.events.MyEvent2;
import messaging.events.MyEvent3;
import messaging.events.threading.EventDispatcherWorker;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.reflections.Reflections;
import smarthomesystem.TestUtils;

/**
 *
 * @author Manel
 */
public class EventDispatcherTest extends TestUtils {

    Reflections reflectionsMock;
    EventDispatcherWorker eventDispatcherWorker;

    EventDispatcher eventDispatcher;
    InjectorInterface containerMock;

    public EventDispatcherTest() {
        reflectionsMock = mock(Reflections.class);
        eventDispatcherWorker = mock(EventDispatcherWorker.class);

        eventDispatcher = new EventDispatcher(reflectionsMock, eventDispatcherWorker);
        containerMock = mock(InjectorInterface.class);
        smarthomesystem.SmartHomeSystem.container = containerMock;
    }

    class MyEventHandler extends EventHandler {

        public void handle(MyEvent myEvent) {

        }

        public void handle(MyEvent2 myEvent2) {

        }

        private void handle(MyEvent3 myEvent3) {

        }
    }

    class MySecondEventHandler extends EventHandler {

        public void handle(MyEvent myEvent) {

        }

        private void handle(MyEvent3 myEvent3) {

        }
    }

    @Test
    public void init_success() throws NoSuchMethodException {
        MyEventHandler myEventHandlerMock = new MyEventHandler();
        MySecondEventHandler mySecondEventHandler = new MySecondEventHandler();
        Set<Class<? extends EventHandler>> eventHandlerDescendants = new HashSet();
        eventHandlerDescendants.add(MyEventHandler.class);
        eventHandlerDescendants.add(MySecondEventHandler.class);

        Map<Class<? extends Event>, List<Pair<? extends EventHandler, Method>>> expected = new HashMap<>();
        expected.put(MyEvent.class, new ArrayList<Pair<? extends EventHandler, Method>>() {
            {
                add(new Pair(myEventHandlerMock, MyEventHandler.class.getMethod("handle", MyEvent.class)));
                add(new Pair(mySecondEventHandler, MySecondEventHandler.class.getMethod("handle", MyEvent.class)));
            }
        });
        expected.put(MyEvent2.class, new ArrayList<Pair<? extends EventHandler, Method>>() {
            {
                add(new Pair(myEventHandlerMock, MyEventHandler.class.getMethod("handle", MyEvent2.class)));
            }
        });

        when(reflectionsMock.getSubTypesOf(EventHandler.class)).thenReturn(eventHandlerDescendants);
        when(containerMock.resolveDependencies(MyEventHandler.class)).thenReturn(myEventHandlerMock);
        when(containerMock.resolveDependencies(MySecondEventHandler.class)).thenReturn(mySecondEventHandler);

        eventDispatcher.init();

        Map<Class<? extends Event>, List<Pair<? extends EventHandler, Method>>> result = eventDispatcher.getEventSubscribers();

        assertEquals(2, result.size());
        assertEventHandlers(expected.get(MyEvent.class), result.get(MyEvent.class));
        assertEventHandlers(expected.get(MyEvent2.class), result.get(MyEvent2.class));
    }

    private void assertEventHandlers(List<Pair<? extends EventHandler, Method>> expected, List<Pair<? extends EventHandler, Method>> actual) {
        assertEquals(expected.size(), actual.size());
        for (Pair<? extends EventHandler, Method> expectedPair : expected) {
            assertTrue(actual.contains(expectedPair));
        }
    }
}
