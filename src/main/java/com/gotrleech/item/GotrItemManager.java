package com.gotrleech.item;

import com.google.common.collect.ImmutableList;
import com.gotrleech.EventHandler;
import com.gotrleech.GotrGameState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tracks items for the player like how many of them they have in their inventory/equipment containers
 */
@Slf4j
@Singleton
public class GotrItemManager extends EventHandler {

    @Getter
    private final GotrItem unchargedCells = new GotrItem(ItemID.UNCHARGED_CELL,
            InventoryID.INVENTORY.getId());

    @Getter
    private final GotrItem guardianFragments = new GotrItem(ItemID.GUARDIAN_FRAGMENTS,
            InventoryID.INVENTORY.getId());

    @Getter
    private final GotrItem bindingNecklaces = new GotrItem(ItemID.BINDING_NECKLACE,
            InventoryID.INVENTORY.getId(),
            InventoryID.EQUIPMENT.getId());

    private final List<GotrItem> trackedItems = ImmutableList.of(unchargedCells, guardianFragments, bindingNecklaces);
    private final Set<Integer> trackedContainerIds = trackedItems.stream()
            .map(GotrItem::getContainerIds)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

    private final GotrGameState gotrGameState;

    @Inject
    public GotrItemManager(GotrGameState gotrGameState) {
        this.gotrGameState = gotrGameState;
    }

    @Override
    protected void cleanup() {
        trackedItems.forEach(item -> item.recalculate(client));
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (!trackedContainerIds.contains(event.getContainerId())) return;

        for (GotrItem gotrItem : trackedItems) {
            if (gotrItem.getContainerIds().contains(event.getContainerId())) {
                gotrItem.recalculate(client);
            }
        }
    }
}
