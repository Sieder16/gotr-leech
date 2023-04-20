package com.gotrleech.item;

import com.google.common.collect.ImmutableList;
import com.gotrleech.EventHandler;
import com.gotrleech.GotrState;
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

    private final GotrState gotrState;

    @Inject
    public GotrItemManager(GotrState gotrState) {
        this.gotrState = gotrState;
    }

    @Override
    protected void cleanup() {
        trackedItems.forEach(GotrItem::cleanup);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged e) {
//        if (!gotrState.isInGame()) return; TODO: Uncomment
        if (!trackedContainerIds.contains(e.getContainerId())) return;

        log.debug("Item container changed: {}", e);

        for (GotrItem gotrItem : trackedItems) {
            if (gotrItem.getContainerIds().contains(e.getContainerId())) {
                gotrItem.recalculate(client);
            }
        }
    }
}
