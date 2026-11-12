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

import com.coaldust.block.ModBlocks;
import com.coaldust.block.StainBlock;
import com.coaldust.disease.Disease;
import com.coaldust.disease.Symptoms;
import com.coaldust.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class Syphilis
extends Disease {
    public static final Syphilis INSTANCE = new Syphilis();

    private Syphilis() {
        super("syphilis", 4);
    }

    @Override
    protected int stageDuration(int n) {
        return 24000;
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.SYPHILIS;
    }

    @Override
    protected void applyEffects(ServerPlayer serverPlayer, int n) {
        if (n >= 2) {
            serverPlayer.addEffect(Syphilis.effect((Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0));
        }
        if (n >= 3) {
            serverPlayer.addEffect(Syphilis.effect((Holder<MobEffect>)MobEffects.DIG_SLOWDOWN, 0));
        }
        if (n >= 4) {
            serverPlayer.addEffect(Syphilis.effect((Holder<MobEffect>)MobEffects.POISON, 0));
        }
    }

    @Override
    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
        Symptoms.trail(serverPlayer, compoundTag, "coaldust_syphilis_trail", (StainBlock)((Object)ModBlocks.DIARRHEA_STAIN.get()));
    }
}
