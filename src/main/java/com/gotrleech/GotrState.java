package com.gotrleech;

import com.gotrleech.event.GotrStateChanged;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GotrState extends EventHandler {

    private static final int GOTR_WIDGET_GROUP_ID = 746;
    private static final int GOTR_WIDGET_CHILD_GAME_ID = 1;

    public enum State {
        START("Creatures from the Abyss will attack in"),
        END("The Great Guardian successfully closed the rift"),
        FAILED("The Great Guardian was defeated"),
        SIXTY_PERCENT("The rift burns intensely"),
        ;

        @Getter
        private final String gameMessage;

        State(String gameMessage) {
            this.gameMessage = gameMessage;
        }
    }

    @Getter
    private boolean inGame;

//    public void startUp() {
//        clearState();
//        eventBus.register(this);
//    }
//
//    public void shutDown() {
//        eventBus.unregister(this);
//        clearState();
//    }

    @Override
    protected void cleanup() {
        inGame = false;
    }

    @Subscribe(priority = 5)
    public void onGameTick(GameTick e) {
        if (client.getGameState() != GameState.LOGGED_IN) return;

        inGame = client.getWidget(GOTR_WIDGET_GROUP_ID, GOTR_WIDGET_CHILD_GAME_ID) != null;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!inGame) return;

        if (event.getType() != ChatMessageType.GAMEMESSAGE) return;

        String message = Text.removeTags(event.getMessage());
        if (message == null) return;

        log.debug("Game Message: {}", message);

        for (State state : State.values()) {
            if (message.startsWith(state.getGameMessage())) {
                eventBus.post(new GotrStateChanged(state));
                return;
            }
        }
    }
}
