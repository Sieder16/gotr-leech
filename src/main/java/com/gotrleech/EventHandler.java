package com.gotrleech;

import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;

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

    protected abstract void cleanup();
}
