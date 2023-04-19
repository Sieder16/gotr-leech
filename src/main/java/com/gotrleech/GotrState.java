package com.gotrleech;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GotrState {

    private static final int GOTR_WIDGET_ID = 48889857;

    private final Client client;
    private final EventBus eventBus;

    @Getter
    private boolean inGame;

    public void startUp() {
        clearState();
        eventBus.register(this);
    }

    public void shutDown() {
        eventBus.unregister(this);
        clearState();
    }

    private void clearState() {
        inGame = false;
    }

    @Subscribe(priority = 5)
    public void onGameTick(GameTick e) {
        log.debug("inGame: {}", inGame);
        if (client.getGameState() != GameState.LOGGED_IN) return;

        inGame = client.getWidget(GOTR_WIDGET_ID) != null;
    }
}
