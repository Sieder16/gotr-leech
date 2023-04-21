package com.gotrleech;

import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Base class for any class that should listen for events
 */
public abstract class EventHandler {

    @Inject
    protected Client client;
    @Inject
    protected EventBus eventBus;

    public void startup() {
        cleanup();
        eventBus.register(this);
    }

    public void shutdown() {
        eventBus.unregister(this);
        cleanup();
    }

    /**
     * Called during startup and shutdown, to allow subclasses to cleanup/reset any state.
     */
    protected abstract void cleanup();
}
