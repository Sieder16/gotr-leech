package com.gotrleech;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(GotrLeechPlugin.CONFIG_GROUP)
public interface GotrLeechConfig extends Config {

    @ConfigItem(
            keyName = "notifyOnStart",
            name = "Notify on game start",
            description = "Send a notification when a new game starts",
            position = 0
    )
    default boolean notifyOnStart() {
        return true;
    }
}
