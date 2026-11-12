/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.coaldust.disease;

import com.coaldust.block.DeathFlowerBlock;
import com.coaldust.block.ModBlocks;
import com.coaldust.disease.Disease;
import com.coaldust.disease.Symptoms;
import com.coaldust.effect.ModEffects;
import com.coaldust.network.FormSync;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class DeathFlower
extends Disease {
    public static final DeathFlower INSTANCE = new DeathFlower();
    public static final int DEATH_STAGE = 5;

    private DeathFlower() {
        super("death_flower", 5);
    }

    @Override
    protected int stageDuration(int n) {
        return 24000;
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.DEATH_FLOWER;
    }

    @Override
    protected boolean showIndicator(int n) {
        return n < 5;
    }

    @Override
    protected void onStageChanged(ServerPlayer serverPlayer, int n) {
        FormSync.sendForm(serverPlayer);
        if (n >= 5 && serverPlayer.isAlive()) {
            Level level = serverPlayer.level();
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                DeathFlower.bloom(serverLevel, serverPlayer.blockPosition(), serverPlayer);
            }
            this.hurt(serverPlayer, 1000.0f);
        }
    }

    @Override
    protected void applyEffects(ServerPlayer serverPlayer, int n) {
        if (n >= 5) {
            this.hurt(serverPlayer, 1000.0f);
            return;
        }
        if (n >= 2) {
            serverPlayer.addEffect(DeathFlower.effect((Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0));
        }
        if (n >= 2) {
            Symptoms.contagion(this, serverPlayer, 4.0, (float)(n - 1) / 1200.0f);
        }
        if (n >= 4) {
            serverPlayer.addEffect(DeathFlower.effect((Holder<MobEffect>)MobEffects.POISON, 0));
        }
    }

    @Override
    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
        if (n >= 3) {
            Symptoms.dizziness(serverPlayer, compoundTag, "coaldust_death_flower_dizzy");
        }
    }

    public static void bloom(ServerLevel serverLevel, BlockPos blockPos, ServerPlayer serverPlayer) {
        BlockState blockState = ((DeathFlowerBlock)((Object)ModBlocks.DEATH_FLOWER.get())).defaultBlockState();
        int n = 0;
        for (int i = -2; i <= 2; ++i) {
            block1: for (int j = -2; j <= 2; ++j) {
                if (i * i + j * j > 5 || (i != 0 || j != 0) && serverPlayer.getRandom().nextFloat() < 0.35f) continue;
                for (int k = 2; k >= -3; --k) {
                    BlockPos blockPos2 = blockPos.offset(i, k, j);
                    BlockState blockState2 = serverLevel.getBlockState(blockPos2);
                    if (!blockState2.isAir() && !blockState2.canBeReplaced() || !blockState.canSurvive((LevelReader)serverLevel, blockPos2)) continue;
                    serverLevel.setBlock(blockPos2, blockState, 3);
                    ++n;
                    continue block1;
                }
            }
        }
        serverLevel.sendParticles((ParticleOptions)ParticleTypes.SOUL, (double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 30, 1.0, 0.6, 1.0, 0.02);
        serverLevel.sendParticles((ParticleOptions)ParticleTypes.ASH, (double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 80, 1.5, 1.0, 1.5, 0.0);
        serverLevel.playSound(null, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.4f, 1.6f);
    }

    public static boolean hasUlcers(ServerPlayer serverPlayer) {
        int n = INSTANCE.getStage(serverPlayer);
        return n >= 1 && n < 5;
    }
}
