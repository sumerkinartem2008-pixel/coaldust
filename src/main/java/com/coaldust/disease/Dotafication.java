/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.player.Player
 */
package com.coaldust.disease;

import com.coaldust.disease.Disease;
import com.coaldust.disease.Symptoms;
import com.coaldust.effect.ModEffects;
import com.coaldust.network.FormSync;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

public class Dotafication
extends Disease {
    public static final Dotafication INSTANCE = new Dotafication();
    public static final int PIG_STAGE = 5;

    private Dotafication() {
        super("dotafication", 5);
    }

    @Override
    protected int stageDuration(int n) {
        return 24000;
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.DOTAFICATION;
    }

    @Override
    protected boolean showIndicator(int n) {
        return n < 5;
    }

    @Override
    protected void onStageChanged(ServerPlayer serverPlayer, int n) {
        if (n >= 5) {
            serverPlayer.removeAllEffects();
        }
        FormSync.sendForm(serverPlayer);
    }

    @Override
    protected void applyEffects(ServerPlayer serverPlayer, int n) {
        if (n >= 5) {
            return;
        }
        serverPlayer.addEffect(Dotafication.effect(ModEffects.OBESITY, 0));
    }

    @Override
    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
        if (n >= 5) {
            return;
        }
        if (n >= 2) {
            Symptoms.vomiting(serverPlayer, compoundTag, "coaldust_dota_vomit", 1200, 2400);
        }
        if (n >= 4 && serverPlayer.getRandom().nextInt(600) == 0) {
            serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getEyeY(), serverPlayer.getZ(), SoundEvents.PIG_AMBIENT, SoundSource.PLAYERS, 1.0f, 0.8f);
        }
    }

    public static boolean isPig(Player player) {
        return INSTANCE.getStageOf(player) >= 5;
    }
}
