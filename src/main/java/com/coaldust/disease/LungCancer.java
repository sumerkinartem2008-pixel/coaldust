/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.player.Player
 */
package com.coaldust.disease;

import com.coaldust.block.CoalDustBlock;
import com.coaldust.disease.Disease;
import com.coaldust.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class LungCancer
extends Disease {
    public static final LungCancer INSTANCE = new LungCancer();
    public static final int DUST_EXPOSURE_TO_START = 400;
    public static final int[] STAGE_DURATION = new int[]{24000, 24000};
    public static final int DUST_SPEEDUP = 10;
    private static final String KEY_EXPOSURE = "coaldust_exposure";

    private LungCancer() {
        super("lung_cancer", 3);
    }

    @Override
    protected int stageDuration(int n) {
        return STAGE_DURATION[n - 1];
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.LUNG_CANCER;
    }

    @Override
    protected int progressRate(ServerPlayer serverPlayer) {
        int n = CoalDustBlock.densityAround((Player)serverPlayer);
        return n < 0 ? 1 : 10 * (n + 1);
    }

    @Override
    protected boolean tryStart(ServerPlayer serverPlayer, CompoundTag compoundTag) {
        int n = CoalDustBlock.densityAround((Player)serverPlayer);
        if (n >= 0) {
            int n2 = compoundTag.getInt(KEY_EXPOSURE) + n + 1;
            compoundTag.putInt(KEY_EXPOSURE, n2);
            if (n2 >= 400) {
                compoundTag.putInt(KEY_EXPOSURE, 0);
                return true;
            }
        }
        return super.tryStart(serverPlayer, compoundTag);
    }

    @Override
    protected void applyEffects(ServerPlayer serverPlayer, int n) {
        serverPlayer.addEffect(LungCancer.effect((Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, n >= 2 ? 1 : 0));
        serverPlayer.addEffect(LungCancer.effect((Holder<MobEffect>)MobEffects.WEAKNESS, n >= 3 ? 1 : 0));
        if (n >= 3) {
            serverPlayer.addEffect(LungCancer.effect((Holder<MobEffect>)MobEffects.BLINDNESS, 0));
        }
    }

    @Override
    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
        if (n == 2 && serverPlayer.tickCount % 100 == 0) {
            this.hurt(serverPlayer, 1.0f);
        } else if (n >= 3 && serverPlayer.tickCount % 20 == 0) {
            this.hurt(serverPlayer, 1.0f);
        }
    }
}
