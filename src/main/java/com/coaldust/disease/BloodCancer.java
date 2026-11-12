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
import com.coaldust.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class BloodCancer
extends Disease {
    public static final BloodCancer INSTANCE = new BloodCancer();
    public static final int STAGE_DURATION = 36000;
    public static final float SATURATION_FACTOR = 0.5f;
    private static final String KEY_LAST_SATURATION = "coaldust_blood_cancer_last_sat";

    private BloodCancer() {
        super("blood_cancer", 2);
    }

    @Override
    protected int stageDuration(int n) {
        return 36000;
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.BLOOD_CANCER;
    }

    @Override
    protected void applyEffects(ServerPlayer serverPlayer, int n) {
        serverPlayer.addEffect(BloodCancer.effect((Holder<MobEffect>)MobEffects.HUNGER, 0));
        serverPlayer.addEffect(BloodCancer.effect((Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0));
    }

    @Override
    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
        BloodCancer.limitSaturation(serverPlayer, compoundTag, KEY_LAST_SATURATION, n >= 2 ? 0.5f : 1.0f);
    }
}
