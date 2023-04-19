package com.gotrleech;

import com.google.inject.Provides;
import com.gotrleech.event.GotrStateChanged;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Arrays;

import static net.runelite.api.ItemID.BINDING_NECKLACE;

@Slf4j
@PluginDescriptor(
        name = "Guardians of the Rift Leech"
)
public class GotrLeechPlugin extends Plugin {

    static final String CONFIG_GROUP = "gotrLeech";

    private static final int ENERGY_REQUIRED = 150; // 150 energy required to qualify for points/xp
    private static final int ELEMENTAL_ENERGY_VARBIT_ID = 13686;
    private static final int CATALYTIC_ENERGY_VARBIT_ID = 13685;

    private static final int BINDING_NECKLACE_VARP_ID = 487;

    @Inject
    private Client client;

    @Inject
    private GotrLeechConfig config;

    @Inject
    private ClientThread clientThread;

    @Inject
    private Notifier notifier;

    @Inject
    private GotrState gotrState;

    @Override
    protected void startUp() throws Exception {
        // Start gotr state detection
        clientThread.invoke(() -> {
            gotrState.startUp();
        });

        // TODO: Deposit pool hide entry
    }

    @Override
    protected void shutDown() throws Exception {
        gotrState.shutDown();
    }

    // Run last
    @Subscribe(priority = -1)
    public void onGameTick(GameTick event) {
        if (!gotrState.isInGame()) return;
    }

    @Subscribe
    public void onGotrStateChanged(GotrStateChanged e) {
        log.debug("GotrStateChanged: {}", e.getState());

        switch (e.getState()) {
            case START:
                start();
                break;
            case END:
                gameOver();
                break;
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

        // Start mining overlay
        // Check cells?
    }

    private void end() {

    }

    private void failed() {

    }

    private void gameOver() {
        // Check binding charges
        // Check uncharged cells
        // Check

//        if (client.getVarpValue())

        if (client.getLocalPlayer() == null) return;
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
        if (inventory == null || equipment == null) return;

        int numBindingNecklaces = getNumItemsInItemContainer(inventory, BINDING_NECKLACE);
        numBindingNecklaces += getNumItemsInItemContainer(equipment, BINDING_NECKLACE);

        if (numBindingNecklaces == 0) {
            notifier.notify("You are out of binding necklaces!");
            return;
        }

        int bindingCharges = client.getVarpValue(BINDING_NECKLACE_VARP_ID);
        if (bindingCharges <= 2) { // TODO: Configure per-round remaining charges to have
            notifier.notify("You need more binding necklace charges for the next round!");
            return;
        }

//        if (inventory.getItems())

        // overlay
    }

    private void sixtyPercent() {
        int totalEnergy =
                client.getVarbitValue(ELEMENTAL_ENERGY_VARBIT_ID) + client.getVarbitValue(CATALYTIC_ENERGY_VARBIT_ID);
        int pointsToQualify = ENERGY_REQUIRED - totalEnergy;
        if (pointsToQualify > 0) {
            notifier.notify("You still need " + pointsToQualify + " more points to qualify for GotR rewards!");
        }
        // overlay...
    }

    private int getNumItemsInItemContainer(ItemContainer itemContainer, int itemId) {
        return (int) Arrays.stream(itemContainer.getItems())
                .filter(i -> i.getId() == itemId)
                .count();
    }

    @Provides
    GotrLeechConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GotrLeechConfig.class);
    }
}
