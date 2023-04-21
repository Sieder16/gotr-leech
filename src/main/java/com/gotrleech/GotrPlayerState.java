package com.gotrleech;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * Tracks player state, like their binding necklace charges, their energy, if they're mining, etc.
 */
@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GotrPlayerState extends EventHandler {

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

    @Getter
    private boolean isMining;

    @Getter
    private int bindingNecklaceCharges;

    private int elementalEnergy;
    private int catalyticEnergy;

    @Override
    protected void cleanup() {
        if (client.getLocalPlayer() != null) {
            isMining = MINING_ANIMATIONS.contains(client.getLocalPlayer().getAnimation());
        } else {
            isMining = false;
        }

        bindingNecklaceCharges = client.getVarpValue(BINDING_NECKLACE_VARP_ID);
        elementalEnergy = client.getVarbitValue(ELEMENTAL_ENERGY_VARBIT_ID);
        catalyticEnergy = client.getVarbitValue(CATALYTIC_ENERGY_VARBIT_ID);
    }

    public int getTotalEnergy() {
        return elementalEnergy + catalyticEnergy;
    }

    @Subscribe
    public void onAnimationChanged(final AnimationChanged event) {
        Player local = client.getLocalPlayer();
        if (event.getActor() != local) return;

        isMining = MINING_ANIMATIONS.contains(local.getAnimation());
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == -1) {
            // VarPlayer
            if (event.getVarpId() == BINDING_NECKLACE_VARP_ID) {
                bindingNecklaceCharges = event.getValue();
            }
        } else {
            // Varbit
            if (event.getVarbitId() == ELEMENTAL_ENERGY_VARBIT_ID) {
                elementalEnergy = event.getValue();
            } else if (event.getVarbitId() == CATALYTIC_ENERGY_VARBIT_ID) {
                catalyticEnergy = event.getValue();
            }
        }
    }
}
