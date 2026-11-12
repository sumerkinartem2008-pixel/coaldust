/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 */
package com.coaldust.disease;

import com.coaldust.disease.Disease;
import com.coaldust.disease.Symptoms;
import com.coaldust.effect.ModEffects;
import com.coaldust.network.FormSync;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class Plague
extends Disease {
    public static final Plague INSTANCE = new Plague();
    public static final float CONTAGION = 8.3333335E-4f;

    private Plague() {
        super("plague", 4);
    }

    @Override
    protected int stageDuration(int n) {
        return 24000;
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.PLAGUE;
    }

    @Override
    protected void onStageChanged(ServerPlayer serverPlayer, int n) {
        FormSync.sendForm(serverPlayer);
    }

    @Override
    protected void applyEffects(ServerPlayer serverPlayer, int n) {
        if (n >= 3) {
            serverPlayer.addEffect(Plague.effect((Holder<MobEffect>)MobEffects.POISON, 0));
        }
        if (n >= 4) {
            serverPlayer.addEffect(Plague.effect(ModEffects.WASTING, 0));
        }
        Symptoms.contagion(this, serverPlayer, 4.0, (float)n * 8.3333335E-4f);
    }

    @Override
    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
        Symptoms.hunger(serverPlayer);
        if (n >= 4 && serverPlayer.tickCount % 120 == 0) {
            this.hurt(serverPlayer, 1.0f);
        }
    }

    public static boolean hasTumors(ServerPlayer serverPlayer) {
        return INSTANCE.getStage(serverPlayer) >= 2;
    }
}
