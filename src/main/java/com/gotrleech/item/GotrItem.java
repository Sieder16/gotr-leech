package com.gotrleech.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class GotrItem {

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
        for (int containerId : numItems.keySet()) {
            ItemContainer itemContainer = client.getItemContainer(containerId);
            if (itemContainer == null) continue;

            numItems.put(containerId, itemContainer.count(itemId));
        }
        count = numItems.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Set<Integer> getContainerIds() {
        return numItems.keySet();
    }
}
