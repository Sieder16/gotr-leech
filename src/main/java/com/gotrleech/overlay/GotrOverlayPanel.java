package com.gotrleech.overlay;

import com.gotrleech.GotrGameState;
import com.gotrleech.GotrLeechConfig;
import com.gotrleech.GotrLeechPlugin;
import com.gotrleech.GotrPlayerState;
import com.gotrleech.item.GotrItemManager;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class GotrOverlayPanel extends OverlayPanel {

    private final static String OVERLAY_NAME = "Guardians of the Rift Leech Overlay";

    private final Client client;
    private final GotrLeechPlugin plugin;
    private final GotrGameState gotrGameState;
    private final GotrPlayerState gotrPlayerState;
    private final GotrItemManager gotrItemManager;
    private final GotrLeechConfig config;
    private final Color originalBackgroundColor;

    @Inject
    public GotrOverlayPanel(Client client,
                            GotrLeechPlugin plugin,
                            GotrGameState gotrGameState,
                            GotrPlayerState gotrPlayerState,
                            GotrItemManager gotrItemManager,
                            GotrLeechConfig config) {
        super(plugin);

        this.client = client;
        this.plugin = plugin;
        this.gotrGameState = gotrGameState;
        this.gotrPlayerState = gotrPlayerState;
        this.gotrItemManager = gotrItemManager;
        this.config = config;
        this.originalBackgroundColor = panelComponent.getBackgroundColor();

        setPosition(OverlayPosition.BOTTOM_LEFT);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, OVERLAY_NAME));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!gotrGameState.isInGame()) return null;

        renderBool(gotrPlayerState.isMining(), "Is Mining");

        renderInt(gotrPlayerState.getTotalEnergy(), "Total Energy", GotrLeechPlugin.ENERGY_REQUIRED);
        panelComponent.getChildren().add(LineComponent.builder().build());

        if (config.minBindingNecklaceChargesRequired() > 0) {
            renderInt(gotrPlayerState.getBindingNecklaceCharges(), "Binding Necklace Charges",
                    config.minBindingNecklaceChargesRequired());
            renderInt(gotrItemManager.getBindingNecklaces().getCount(), "Binding Necklaces", 1);
        }

        if (config.minUnchargedCellsRequired() > 0) {
            renderInt(gotrItemManager.getUnchargedCells().getCount(), "Uncharged Cells",
                    config.minUnchargedCellsRequired());
        }

        if (shouldFlash()) {
            flashOverlay();
        } else {
            panelComponent.setBackgroundColor(originalBackgroundColor);
        }

        panelComponent.setPreferredSize(new Dimension(175, 0));

        return super.render(graphics);
    }

    private void renderInt(int val, String label, int minVal) {
        panelComponent.getChildren().add(LineComponent.builder()
                .left(label + ":")
                .right(String.valueOf(val))
                .rightColor(chooseColor(val >= minVal))
                .build());
    }

    private void renderBool(boolean val, String label) {
        panelComponent.getChildren().add(LineComponent.builder()
                .left(label + ":")
                .right(val ? "YES" : "NO")
                .rightColor(chooseColor(val))
                .build());
    }

    private Color chooseColor(boolean val) {
        return val ? Color.GREEN : Color.RED;
    }

    private boolean shouldFlash() {
        return config.flashOverlayWhenNotMining() && !gotrPlayerState.isMining();
    }

    private void flashOverlay() {
        if (client.getTickCount() % 2 == 0) {
            panelComponent.setBackgroundColor(new Color(255, 0, 0, originalBackgroundColor.getAlpha()));
        } else {
            panelComponent.setBackgroundColor(originalBackgroundColor);
        }
    }
}
