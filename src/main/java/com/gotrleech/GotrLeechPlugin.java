package com.gotrleech;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
        name = "Guardians of the Rift Leech"
)
public class GotrLeechPlugin extends Plugin {

    static final String CONFIG_GROUP = "gotrLeech";

    private static final String GAME_START_MESSAGE = "The rift becomes active!";
    private static final String GAME_END_MESSAGE = "The Great Guardian successfully closed the rift!";
    private static final String GAME_FAILED_MESSAGE = "";
//    private static final String

    @Inject
    private Client client;

    @Inject
    private GotrLeechConfig config;

    @Inject
    private ClientThread clientThread;

    @Inject
    private GotrState gotrState;

    @Override
    protected void startUp() throws Exception {
        // Start gotr state detection
        clientThread.invoke(() -> {
            gotrState.startUp();
        });
    }

    @Override
    protected void shutDown() throws Exception {
        gotrState.shutDown();
    }

    @Provides
    GotrLeechConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GotrLeechConfig.class);
    }
}
