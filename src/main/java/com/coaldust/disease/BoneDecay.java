/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.player.Player
 */
package com.coaldust.disease;

import com.coaldust.disease.Disease;
import com.coaldust.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class BoneDecay
extends Disease {
    public static final BoneDecay INSTANCE = new BoneDecay();

    private BoneDecay() {
        super("bone_decay", 4);
    }

    @Override
    protected int stageDuration(int n) {
        return 24000;
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.BONE_DECAY;
    }

    @Override
    protected void applyEffects(ServerPlayer serverPlayer, int n) {
        serverPlayer.addEffect(BoneDecay.effect((Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0));
        if (n >= 2) {
            serverPlayer.addEffect(BoneDecay.effect((Holder<MobEffect>)MobEffects.DIG_SLOWDOWN, 0));
        }
        if (n >= 3) {
            serverPlayer.addEffect(BoneDecay.effect(ModEffects.FRAGILITY, n >= 4 ? 1 : 0));
        }
    }

    public static float damageMultiplier(Player player) {
        int n = INSTANCE.getStageOf(player);
        return n >= 4 ? 4.0f : (n >= 3 ? 2.0f : 1.0f);
    }
}
