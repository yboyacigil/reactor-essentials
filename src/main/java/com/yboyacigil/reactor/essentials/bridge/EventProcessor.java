package com.yboyacigil.reactor.essentials.bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventProcessor {

    private final List<EventListener<Event>> listeners;

    private final AtomicBoolean running = new AtomicBoolean(true);

    public EventProcessor() {
        listeners = new ArrayList<>();
    }

    void register(EventListener<Event> listener) {
        listeners.add(listener);
    }

    void process(Event e) {
        listeners.forEach(listener -> listener.onData(e));
    }

    void shutdown() {
        listeners.forEach(EventListener::complete);
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }
}
