/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.food.FoodData
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 */
package com.coaldust.disease;

import com.coaldust.disease.Disease;
import com.coaldust.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class StomachCancer
extends Disease {
    public static final StomachCancer INSTANCE = new StomachCancer();
    public static final int STAGE_DURATION = 36000;
    public static final float EXTRA_EXHAUSTION = 0.0025f;
    public static final float SATURATION_FACTOR = 0.5f;
    public static final float BAD_FOOD_CHANCE = 0.02f;
    public static final float NAUSEA_MULTIPLIER = 20.0f;
    public static final int VOMIT_INTERVAL_MIN = 600;
    public static final int VOMIT_INTERVAL_MAX = 1800;
    public static final int VOMIT_DURATION = 60;
    public static final int VOMIT_FOOD_COST = 4;
    private static final String KEY_LAST_SATURATION = "coaldust_stomach_last_sat";
    private static final String KEY_NEXT_VOMIT = "coaldust_stomach_next_vomit";
    private static final String KEY_VOMIT_LEFT = "coaldust_stomach_vomit_left";

    private StomachCancer() {
        super("stomach_cancer", 2);
    }

    @Override
    protected int stageDuration(int n) {
        return 36000;
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.STOMACH_CANCER;
    }

    @Override
    protected float spontaneousMultiplier(ServerPlayer serverPlayer) {
        return serverPlayer.hasEffect(MobEffects.CONFUSION) ? 20.0f : 1.0f;
    }

    public void onAte(ServerPlayer serverPlayer, ItemStack itemStack) {
        if (itemStack.is(Items.ROTTEN_FLESH) || itemStack.is(Items.POISONOUS_POTATO)) {
            this.rollInfection(serverPlayer, 0.02f);
        }
    }

    @Override
    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
        serverPlayer.causeFoodExhaustion(0.0025f);
        StomachCancer.limitSaturation(serverPlayer, compoundTag, KEY_LAST_SATURATION, 0.5f);
        if (n < 2) {
            return;
        }
        int n2 = compoundTag.getInt(KEY_VOMIT_LEFT);
        if (n2 > 0) {
            compoundTag.putInt(KEY_VOMIT_LEFT, --n2);
            if (n2 % 2 == 0) {
                StomachCancer.spawnVomit(serverPlayer);
            }
            if (n2 % 20 == 0) {
                serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getEyeY(), serverPlayer.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.0f, 0.5f + serverPlayer.getRandom().nextFloat() * 0.2f);
            }
            return;
        }
        int n3 = compoundTag.getInt(KEY_NEXT_VOMIT);
        if (n3 <= 0) {
            compoundTag.putInt(KEY_NEXT_VOMIT, 600 + serverPlayer.getRandom().nextInt(1201));
            return;
        }
        if (--n3 <= 0) {
            this.startVomit(serverPlayer, compoundTag);
        }
        compoundTag.putInt(KEY_NEXT_VOMIT, n3);
    }

    private void startVomit(ServerPlayer serverPlayer, CompoundTag compoundTag) {
        compoundTag.putInt(KEY_VOMIT_LEFT, 60);
        serverPlayer.addEffect(StomachCancer.effect(ModEffects.VOMITING, 0, 60));
        serverPlayer.addEffect(StomachCancer.effect((Holder<MobEffect>)MobEffects.CONFUSION, 0, 140));
        serverPlayer.addEffect(StomachCancer.effect((Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 2, 60));
        FoodData foodData = serverPlayer.getFoodData();
        foodData.setFoodLevel(Math.max(0, foodData.getFoodLevel() - 4));
        foodData.setSaturation(0.0f);
        compoundTag.putFloat(KEY_LAST_SATURATION, 0.0f);
        serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getEyeY(), serverPlayer.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.2f, 0.4f);
    }

    private static void spawnVomit(ServerPlayer serverPlayer) {
        Level level = serverPlayer.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        level = serverPlayer.getLookAngle();
        double d = serverPlayer.getX() + level.x * 0.4;
        double d2 = serverPlayer.getEyeY() - 0.25;
        double d3 = serverPlayer.getZ() + level.z * 0.4;
        ItemParticleOption itemParticleOption = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack((ItemLike)Items.ROTTEN_FLESH));
        for (int i = 0; i < 4; ++i) {
            double d4 = level.x + (serverPlayer.getRandom().nextDouble() - 0.5) * 0.3;
            double d5 = -0.4 + (serverPlayer.getRandom().nextDouble() - 0.5) * 0.2;
            double d6 = level.z + (serverPlayer.getRandom().nextDouble() - 0.5) * 0.3;
            serverLevel.sendParticles((ParticleOptions)itemParticleOption, d, d2, d3, 0, d4, d5, d6, 0.25);
        }
    }
}
