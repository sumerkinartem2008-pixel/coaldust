/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.food.FoodData
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.coaldust.disease;

import com.coaldust.block.StainBlock;
import com.coaldust.disease.Disease;
import com.coaldust.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public final class Symptoms {
    public static final int VOMIT_DURATION = 60;

    public static void trail(ServerPlayer serverPlayer, CompoundTag compoundTag, String string, StainBlock stainBlock) {
        BlockPos blockPos;
        if (serverPlayer.tickCount % 30 != 0 || !serverPlayer.onGround() || serverPlayer.isSpectator()) {
            return;
        }
        double d = compoundTag.getDouble(string + "_x");
        double d2 = compoundTag.getDouble(string + "_z");
        double d3 = serverPlayer.getX() - d;
        double d4 = serverPlayer.getZ() - d2;
        compoundTag.putDouble(string + "_x", serverPlayer.getX());
        compoundTag.putDouble(string + "_z", serverPlayer.getZ());
        if (d3 * d3 + d4 * d4 < 0.25 && serverPlayer.getRandom().nextFloat() < 0.8f) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)serverPlayer.level();
        BlockState blockState = serverLevel.getBlockState(blockPos = serverPlayer.blockPosition());
        if (!blockState.isAir() && !(blockState.getBlock() instanceof StainBlock)) {
            return;
        }
        BlockState blockState2 = stainBlock.defaultBlockState();
        if (!blockState2.canSurvive((LevelReader)serverLevel, blockPos)) {
            return;
        }
        stainBlock.place(serverLevel, blockPos);
    }

    public static void vomiting(ServerPlayer serverPlayer, CompoundTag compoundTag, String string, int n, int n2) {
        String string2 = string + "_left";
        String string3 = string + "_next";
        int n3 = compoundTag.getInt(string2);
        if (n3 > 0) {
            compoundTag.putInt(string2, --n3);
            if (n3 % 2 == 0) {
                Symptoms.spawnVomit(serverPlayer);
            }
            if (n3 % 20 == 0) {
                serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getEyeY(), serverPlayer.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.0f, 0.5f + serverPlayer.getRandom().nextFloat() * 0.2f);
            }
            return;
        }
        int n4 = compoundTag.getInt(string3);
        if (n4 <= 0) {
            compoundTag.putInt(string3, n + serverPlayer.getRandom().nextInt(n2 - n + 1));
            return;
        }
        if (--n4 <= 0) {
            compoundTag.putInt(string2, 60);
            serverPlayer.addEffect(new MobEffectInstance(ModEffects.VOMITING, 60, 0, false, false, true));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 140, 0, false, false, true));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false, true));
            FoodData foodData = serverPlayer.getFoodData();
            foodData.setFoodLevel(Math.max(0, foodData.getFoodLevel() - 3));
            foodData.setSaturation(0.0f);
            serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getEyeY(), serverPlayer.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.2f, 0.4f);
        }
        compoundTag.putInt(string3, n4);
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

    public static void dizziness(ServerPlayer serverPlayer, CompoundTag compoundTag, String string) {
        int n = compoundTag.getInt(string);
        if (n <= 0) {
            compoundTag.putInt(string, 1200 + serverPlayer.getRandom().nextInt(2401));
            return;
        }
        if (--n <= 0) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, true));
            serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getEyeY(), serverPlayer.getZ(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0f, 0.8f);
        }
        compoundTag.putInt(string, n);
    }

    public static void contagion(Disease disease, ServerPlayer serverPlayer, double d, float f) {
        Level level = serverPlayer.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        for (ServerPlayer serverPlayer2 : serverLevel.players()) {
            if (serverPlayer2 == serverPlayer || serverPlayer2.distanceToSqr(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ()) > d * d) continue;
            disease.rollInfection(serverPlayer2, f);
        }
    }

    public static void hunger(ServerPlayer serverPlayer) {
        serverPlayer.causeFoodExhaustion(0.005f);
    }

    private Symptoms() {
    }
}
