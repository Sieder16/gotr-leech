package com.gotrleech;

import com.google.inject.Provides;
import com.gotrleech.event.GotrGameStateChanged;
import com.gotrleech.item.GotrItemManager;
import com.gotrleech.overlay.GotrOverlayPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
        name = "Guardians of the Rift Leech"
)
public class GotrLeechPlugin extends Plugin {

    static final String CONFIG_GROUP = "gotrLeech";

    @Inject
    private Client client;

    @Inject
    private GotrLeechConfig config;

    @Inject
    private ClientThread clientThread;

    @Inject
    private Notifier notifier;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GotrOverlayPanel overlayPanel;

    @Inject
    private GotrGameState gotrGameState;

    @Inject
    private GotrPlayerState gotrPlayerState;

    @Inject
    private GotrItemManager gotrItemManager;

    @Override
    protected void startUp() throws Exception {
        // Start gotr state detection
        clientThread.invoke(() -> {
            gotrGameState.startup();
            gotrPlayerState.startup();
            gotrItemManager.startup();
        });

        overlayManager.add(overlayPanel);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlayPanel);

        clientThread.invoke(() -> {
            gotrItemManager.shutdown();
            gotrPlayerState.shutdown();
            gotrGameState.shutdown();
        });
    }

    @Subscribe
    public void onGotrGameStateChanged(GotrGameStateChanged event) {
        switch (event.getState()) {
            case START:
                start();
                break;
            case END:
            case FAILED:
                gameOver();
                break;
            case SIXTY_PERCENT:
                sixtyPercent();
                break;
            default:
                break;
        }
    }

    private void start() {
        if (config.notifyOnStart()) {
            notifier.notify("A new Guardians of the Rift game has started!");
        }
    }

    private void gameOver() {
        if (!config.notifyOnEndForNextGame()) return;

        if (gotrItemManager.getBindingNecklaces().getCount() < 1) {
            notifier.notify("You are out of binding necklaces!");
            return;
        }

        int bindingCharges = gotrPlayerState.getBindingNecklaceCharges();
        if (bindingCharges < config.minBindingNecklaceChargesRequired()) {
            notifier.notify("You need more binding necklace charges for the next round!");
            return;
        }

        if (gotrItemManager.getUnchargedCells().getCount() < config.minUnchargedCellsRequired()) {
            notifier.notify("You need more uncharged cells for the next round!");
            return;
        }
    }

    private void sixtyPercent() {
        if (!config.notifyOnSixtyPercentMessage()) return;

        int requiredEnergy = config.highPointRequirement() ? 300 : 150;
        int pointsToQualify = requiredEnergy - gotrPlayerState.getTotalEnergy();
        if (pointsToQualify > 0) {
            notifier.notify("You still need " + pointsToQualify + " more points to qualify for GotR rewards!");
            return;
        }
    }

    @Provides
    GotrLeechConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GotrLeechConfig.class);
    }
}
