package com.gotrleech.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class GotrItem {

    // TODO: Global charges (binding necklaces?)

    @Getter
    private final int itemId;
    @Getter
    private int count;

    private final Map<Integer, Integer> numItems; // containerId -> count

    public GotrItem(int itemId, Integer... containerIds) {
        this.itemId = itemId;
        this.numItems = Arrays.stream(containerIds).collect(Collectors.toMap(Function.identity(), i -> 0));
    }

    public void recalculate(Client client) {
        log.debug("Recalculating for item: {}", itemId);

        for (int containerId : numItems.keySet()) {
            ItemContainer itemContainer = client.getItemContainer(containerId);
            if (itemContainer == null) continue;

            numItems.put(containerId, getNumItemsInItemContainer(itemContainer));
        }
        count = numItems.values().stream().mapToInt(Integer::intValue).sum();

        log.debug("Found {} total items for item {}", getCount(), itemId);
    }

    public void cleanup() {
        // Reset values back to 0
        for (Map.Entry<Integer, Integer> entry : new HashSet<>(numItems.entrySet())) {
            numItems.put(entry.getKey(), 0);
        }
        count = 0;
    }

    public Set<Integer> getContainerIds() {
        return numItems.keySet();
    }

    private int getNumItemsInItemContainer(ItemContainer itemContainer) {
        return Arrays.stream(itemContainer.getItems())
                .filter(i -> i.getId() == itemId)
                .mapToInt(Item::getQuantity)
                .sum();
    }
}
