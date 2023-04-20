package com.gotrleech.overlay;

import com.gotrleech.GotrLeechConfig;
import com.gotrleech.GotrLeechPlugin;
import com.gotrleech.GotrState;
import com.gotrleech.item.GotrItem;
import com.gotrleech.item.GotrItemManager;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class GotrOverlayPanel extends OverlayPanel {

    private final static String OVERLAY_NAME = "Guardians of the Rift Leech Overlay";

    private final Client client;
    private final GotrLeechPlugin plugin;
    private final GotrState gotrState;

    private final GotrItemManager gotrItemManager;
    private final GotrLeechConfig config;
    private final Color originalBackgroundColor;

    @Inject
    public GotrOverlayPanel(Client client,
                            GotrLeechPlugin plugin,
                            GotrState gotrState,
                            GotrItemManager gotrItemManager,
                            GotrLeechConfig config) {
        super(plugin);

        this.client = client;
        this.plugin = plugin;
        this.gotrState = gotrState;
        this.gotrItemManager = gotrItemManager;
        this.config = config;
        this.originalBackgroundColor = panelComponent.getBackgroundColor();

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, OVERLAY_NAME));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
//        if (!gotrState.isInGame()) return null; TODO: Uncomment
        // TODO: Add config option for overlay and for notifications

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Game Tick: " + client.getTickCount())
                .build());

        renderItem(gotrItemManager.getUnchargedCells(), "Uncharged Cells:");
        renderItem(gotrItemManager.getBindingNecklaces(), "Binding Necklaces:");
        renderBool(plugin.isMining(), "Is Mining:");

        if (gotrItemManager.getUnchargedCells().getCount() <= 0 ||
                gotrItemManager.getBindingNecklaces().getCount() <= 0 ||
                !plugin.isMining()) {
            if (client.getTickCount() % 2 == 0) {
                panelComponent.setBackgroundColor(Color.RED);
            } else {
                panelComponent.setBackgroundColor(originalBackgroundColor);
            }
        } else {
            panelComponent.setBackgroundColor(originalBackgroundColor);
        }
        // TODO: Total energy

//        panelComponent.getChildren().add(LineComponent.builder()
//                .left(text)
//                .right(count > 0 ? String.valueOf(count) : "NONE")
//                .rightColor(count > 0 ? Color.GREEN : Color.RED)
//                .build());

//        if (gotrState.isInRaid()) {
//            panelComponent.getChildren().add(TitleComponent.builder()
//                    .text("In Raid")
//                    .color(Color.GREEN)
//                    .build());
//        } else {
//            panelComponent.getChildren().add(TitleComponent.builder()
//                    .text("NOT in raid")
//                    .color(Color.RED)
//                    .build());
//        }

//        panelComponent.getChildren().add(TitleComponent.builder()
//                .text("Raiders:")
//                .color(raidState.getRaiders().isEmpty() ? Color.RED : Color.GREEN)
//                .build());
//
//        raidState.getRaiders().keySet().forEach(name -> {
//            panelComponent.getChildren().add(LineComponent.builder()
//                    .left(name)
//                    .build());
//        });

        // Add all mistake detectors
//        renderMistakeDetector(mistakeDetectorManager.getClass().getSimpleName(),
//                mistakeDetectorManager.isStarted());
//        for (BaseMistakeDetector mistakeDetector : mistakeDetectorManager.getMistakeDetectors()) {
//            renderMistakeDetector(mistakeDetector.getClass().getSimpleName(), mistakeDetector.isDetectingMistakes());
//        }

        return super.render(graphics);
    }

    private void renderItem(GotrItem item, String text) {
        int count = item.getCount();
        panelComponent.getChildren().add(LineComponent.builder()
                .left(text)
                .right(count > 0 ? String.valueOf(count) : "NONE")
                .rightColor(count > 0 ? Color.GREEN : Color.RED)
                .build());
    }

    private void renderBool(boolean val, String text) {
        panelComponent.getChildren().add(LineComponent.builder()
                .left(text)
                .right(val ? "YES" : "NO")
                .rightColor(val ? Color.GREEN : Color.RED)
                .build());
    }

//    private void renderMistakeDetector(String name, boolean isOn) {
//        panelComponent.getChildren().add(LineComponent.builder()
//                .left(name)
//                .right(isOn ? "ON" : "OFF")
//                .rightColor(isOn ? Color.GREEN : Color.RED)
//                .build());
//    }
}
