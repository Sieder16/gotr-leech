package com.gotrleech;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

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

    @ConfigItem(
            keyName = "notifyOnEndForNextGame",
            name = "Notify on game end when missing requirements for next game",
            description = "Send a notification when a game ends and you don't have enough requirements met for the " +
                    "next game, like binding necklaces/charges, uncharged cells, etc.",
            position = 1
    )
    default boolean notifyOnEndForNextGame() {
        return true;
    }

    @ConfigItem(
            keyName = "notifyOnSixtyPercentMessage",
            name = "Notify on 60% message if not enough energy still",
            description = "Send a notification when the 60% game message appears and you still don't have enough " +
                    "energy to qualify for points/rewards.",
            position = 2
    )
    default boolean notifyOnSixtyPercentMessage() {
        return true;
    }

    @ConfigItem(
            keyName = "flashOverlayWhenNotMining",
            name = "Flash overlay when not mining",
            description = "Flash the overlay box when not mining",
            position = 3
    )
    default boolean flashOverlayWhenNotMining() {
        return true;
    }

    @ConfigItem(
            keyName = "minBindingNecklaceChargesRequired",
            name = "Minimum binding necklace charges required",
            description = "The minimum binding necklace charges required to be considered okay for the round. Set to " +
                    "0 if not using binding necklaces. The default is 3 since it takes 3 crafts for 1 full altar run.",
            position = 4
    )
    @Range(min = 0, max = 16)
    default int minBindingNecklaceChargesRequired() {
        return 3;
    }

    @ConfigItem(
            keyName = "minUnchargedCellsRequired",
            name = "Minimum uncharged cells required",
            description = "The minimum uncharged cells required to be considered okay for the round. Set to " +
                    "0 if not using uncharged cells.",
            position = 5
    )
    @Range(min = 0, max = 10)
    default int minUnchargedCellsRequired() {
        return 1;
    }
}
