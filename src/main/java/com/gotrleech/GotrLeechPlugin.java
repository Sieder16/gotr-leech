package com.gotrleech;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import com.gotrleech.event.GotrStateChanged;
import com.gotrleech.item.GotrItemManager;
import com.gotrleech.overlay.GotrOverlayPanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Set;

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

    private static final Set<Integer> MINING_ANIMATIONS = ImmutableSet.of(
            AnimationID.MINING_BRONZE_PICKAXE,
            AnimationID.MINING_IRON_PICKAXE,
            AnimationID.MINING_STEEL_PICKAXE,
            AnimationID.MINING_BLACK_PICKAXE,
            AnimationID.MINING_MITHRIL_PICKAXE,
            AnimationID.MINING_ADAMANT_PICKAXE,
            AnimationID.MINING_RUNE_PICKAXE,
            AnimationID.MINING_GILDED_PICKAXE,
            AnimationID.MINING_DRAGON_PICKAXE,
            AnimationID.MINING_DRAGON_PICKAXE_UPGRADED,
            AnimationID.MINING_DRAGON_PICKAXE_OR,
            AnimationID.MINING_DRAGON_PICKAXE_OR_TRAILBLAZER,
            AnimationID.MINING_INFERNAL_PICKAXE,
            AnimationID.MINING_3A_PICKAXE,
            AnimationID.MINING_CRYSTAL_PICKAXE,
            AnimationID.MINING_TRAILBLAZER_PICKAXE,
            AnimationID.MINING_TRAILBLAZER_PICKAXE_2,
            AnimationID.MINING_TRAILBLAZER_PICKAXE_3);

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
    private GotrState gotrState;

    @Inject
    private GotrItemManager gotrItemManager;

    @Getter
    private boolean isMining;

    @Override
    protected void startUp() throws Exception {
        isMining = false;

        // Start gotr state detection
        clientThread.invoke(() -> {
            gotrState.startup();
            gotrItemManager.startup();
        });

        overlayManager.add(overlayPanel);

        // TODO: Deposit pool hide entry
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlayPanel);

        gotrItemManager.shutdown();
        gotrState.shutdown();

        isMining = false;
    }

    // Run last
    @Subscribe(priority = -1)
    public void onGameTick(GameTick event) {
        if (!gotrState.isInGame()) return;

//        checkUnchargedCells();
    }

//    private void checkUnchargedCells() {
//        if (hasUnchargedCells != null) return; // Already calculated
//
//        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
//        if (inventory == null) return;
//
//        recalculateHasUnchargedCells(inventory);
//    }

//    private void recalculateHasUnchargedCells(ItemContainer itemContainer) {
//        int numUnchargedCells = getNumItemsInItemContainer(itemContainer, ItemID.UNCHARGED_CELL);
//        hasUnchargedCells = numUnchargedCells > 0;
//    }

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

//    @Subscribe
//    public void onItemContainerChanged(ItemContainerChanged e) {
//        if (!gotrState.isInGame() || e.getContainerId() != InventoryID.INVENTORY.getId()) return;
//
//        recalculateHasUnchargedCells(e.getItemContainer());
//    }

    private void start() {
        if (config.notifyOnStart()) {
            notifier.notify("A new Guardians of the Rift game has started!");
        }

        // Start mining overlay
        // Check cells?

//        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
//        if (inventory == null) return;
//
//        int numUnchargedCells = getNumItemsInItemContainer(inventory, ItemID.UNCHARGED_CELL);
//        if (numUnchargedCells < 1) {
//
//        }
    }

    private void end() {

    }

    private void failed() {

    }

    private void gameOver() {
        // Check binding charges
        // Check uncharged cells
        // Check

//        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
//        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
//        if (inventory == null || equipment == null) return;
//
//        int numBindingNecklaces = getNumItemsInItemContainer(inventory, ItemID.BINDING_NECKLACE);
//        numBindingNecklaces += getNumItemsInItemContainer(equipment, ItemID.BINDING_NECKLACE);

        if (gotrItemManager.getBindingNecklaces().getCount() == 0) {
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

    @Subscribe
    public void onAnimationChanged(final AnimationChanged event) {
        Player local = client.getLocalPlayer();

        if (event.getActor() != local) {
            return;
        }

        isMining = MINING_ANIMATIONS.contains(local.getAnimation());
    }

    @Provides
    GotrLeechConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GotrLeechConfig.class);
    }
}
