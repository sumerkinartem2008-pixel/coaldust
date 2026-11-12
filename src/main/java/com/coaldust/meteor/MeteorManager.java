/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 */
package com.coaldust.meteor;

import com.coaldust.meteor.BuildJob;
import com.coaldust.meteor.Crater;
import com.coaldust.meteor.MeteorBlocks;
import com.coaldust.meteor.MeteorClass;
import com.coaldust.meteor.MeteorShape;
import com.coaldust.meteor.MeteorType;
import com.coaldust.meteor.MeteorVisual;
import com.coaldust.meteor.ancient.AncientStructures;
import com.coaldust.meteor.underground.UndergroundCity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class MeteorManager {
    public static final int NATURAL_CHECK_INTERVAL = 1200;
    public static final float NATURAL_CHANCE = 0.033333335f;
    public static final int FALL_TICKS = 50;
    public static final int FALL_TICKS_BIG = 90;
    public static final int MAX_ACTIVE = 8;
    private static final double VIEW_RANGE_SQR = 102400.0;
    private static final double MESSAGE_RANGE_SQR = 25600.0;
    private static final List<Falling> ACTIVE = new ArrayList<Falling>();
    private static final List<BuildJob> JOBS = new ArrayList<BuildJob>();
    private static final ConcurrentLinkedQueue<Runnable> READY = new ConcurrentLinkedQueue();

    static boolean isBig(MeteorType meteorType, double d) {
        return meteorType == MeteorType.ANCIENT || meteorType == MeteorType.UNDERGROUND || d >= 8.0;
    }

    public static float craterPower(double d, MeteorClass.Density density) {
        double d2 = 1.0 + Math.max(0.0, d - 1.5) * 1.6;
        return (float)Math.min(10.0, d2 * (double)density.craterMultiplier);
    }

    public static boolean launchRitual(ServerLevel serverLevel, BlockPos blockPos) {
        RandomSource randomSource = serverLevel.getRandom();
        MeteorType.Size size = randomSource.nextBoolean() ? MeteorType.Size.SMALL : MeteorType.Size.MEDIUM;
        int n = randomSource.nextInt(100);
        MeteorType meteorType = n < 50 ? MeteorType.resourceFor(size, randomSource) : (n < 70 ? MeteorType.PLAIN : (n < 85 ? MeteorType.ICE : MeteorType.LIVING));
        double d = meteorType.rollRadius(randomSource, size);
        double d2 = randomSource.nextDouble() * Math.PI * 2.0;
        double d3 = 18.0 + randomSource.nextDouble() * 12.0;
        int n2 = blockPos.getX() + (int)Math.round(Math.cos(d2) * d3);
        int n3 = blockPos.getZ() + (int)Math.round(Math.sin(d2) * d3);
        int n4 = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, n2, n3);
        return MeteorManager.launch(serverLevel, new BlockPos(n2, n4, n3), meteorType, d);
    }

    static float[] facing(double d, double d2, double d3) {
        double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
        if (d4 < 1.0E-6) {
            return new float[]{0.0f, 90.0f};
        }
        return new float[]{(float)Math.toDegrees(Math.atan2(-d, d3)), (float)Math.toDegrees(Math.asin(-d2 / d4))};
    }

    public static boolean launch(ServerLevel serverLevel, BlockPos blockPos, MeteorType meteorType, double d) {
        return MeteorManager.launch(serverLevel, blockPos, meteorType, d, -1);
    }

    public static boolean launch(ServerLevel serverLevel, BlockPos blockPos, MeteorType meteorType, double d, int n) {
        if (ACTIVE.size() >= 8) {
            return false;
        }
        RandomSource randomSource = serverLevel.getRandom();
        double d2 = randomSource.nextDouble() * Math.PI * 2.0;
        boolean bl = MeteorManager.isBig(meteorType, d);
        double d3 = bl ? 110.0 : 70.0;
        double d4 = (double)blockPos.getX() + 0.5 + Math.cos(d2) * d3;
        double d5 = (double)blockPos.getZ() + 0.5 + Math.sin(d2) * d3;
        double d6 = blockPos.getY() + (bl ? 160 : 110);
        Falling falling = new Falling(serverLevel, blockPos.immutable(), meteorType, d, d4, d6, d5);
        falling.variant = n;
        float[] fArray = MeteorManager.facing((double)blockPos.getX() + 0.5 - d4, (double)blockPos.getY() + 0.5 - d6, (double)blockPos.getZ() + 0.5 - d5);
        falling.visual = MeteorVisual.spawn(serverLevel, meteorType, d, falling.fallTicks, d4, d6, d5, fArray[0], fArray[1]);
        ACTIVE.add(falling);
        serverLevel.playSound(null, d4, d6, d5, SoundEvents.BLAZE_SHOOT, SoundSource.AMBIENT, 12.0f, 0.5f);
        return true;
    }

    public static void onLevelTick(LevelTickEvent.Post post) {
        Object object;
        ServerLevel serverLevel;
        Object object2 = post.getLevel();
        if (object2 instanceof ServerLevel) {
            serverLevel = (ServerLevel)object2;
        } else {
            return;
        }
        while ((object2 = READY.poll()) != null) {
            object2.run();
        }
        object2 = JOBS.iterator();
        while (object2.hasNext()) {
            object = (BuildJob)object2.next();
            if (((BuildJob)object).level != serverLevel || !((BuildJob)object).tick()) continue;
            object2.remove();
        }
        object = ACTIVE.iterator();
        while (object.hasNext()) {
            Falling falling = (Falling)object.next();
            if (falling.level != serverLevel) continue;
            ++falling.age;
            if (falling.age >= falling.fallTicks) {
                object.remove();
                MeteorVisual.remove(falling.visual);
                MeteorManager.impact(falling);
                continue;
            }
            MeteorManager.flight(falling);
        }
        if (serverLevel.dimension() == Level.OVERWORLD && serverLevel.getGameTime() % 1200L == 0L) {
            MeteorManager.tryNatural(serverLevel);
        }
    }

    private static void tryNatural(ServerLevel serverLevel) {
        List list = serverLevel.players();
        if (list.isEmpty()) {
            return;
        }
        RandomSource randomSource = serverLevel.getRandom();
        if (randomSource.nextFloat() >= 0.033333335f) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)list.get(randomSource.nextInt(list.size()));
        double d = Math.toRadians((double)serverPlayer.getYRot() + (randomSource.nextDouble() - 0.5) * 60.0);
        double d2 = 40.0 + randomSource.nextDouble() * 40.0;
        int n = (int)Math.floor(serverPlayer.getX() - Math.sin(d) * d2);
        int n2 = (int)Math.floor(serverPlayer.getZ() + Math.cos(d) * d2);
        BlockPos blockPos = new BlockPos(n, (int)serverPlayer.getY(), n2);
        if (!serverLevel.isLoaded(blockPos)) {
            return;
        }
        int n3 = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, n, n2);
        MeteorType.Size size = MeteorType.Size.rollNatural(randomSource);
        MeteorType meteorType = MeteorType.rollNatural(randomSource, size);
        double d3 = meteorType.rollRadius(randomSource, size);
        if (meteorType == MeteorType.ANCIENT) {
            d3 = Math.max(d3, 2.8);
        }
        int n4 = -1;
        if (meteorType == MeteorType.UNDERGROUND) {
            n4 = size == MeteorType.Size.TITANIC ? UndergroundCity.rollBig(randomSource) : UndergroundCity.rollVariant(randomSource, false);
            d3 = UndergroundCity.radiusFor(n4);
        }
        MeteorManager.launch(serverLevel, new BlockPos(n, n3, n2), meteorType, d3, n4);
    }

    private static void flight(Falling falling) {
        double d = (double)falling.age / (double)falling.fallTicks;
        double d2 = (double)falling.impact.getX() + 0.5;
        double d3 = (double)falling.impact.getY() + 0.5;
        double d4 = (double)falling.impact.getZ() + 0.5;
        double d5 = falling.sx + (d2 - falling.sx) * d;
        double d6 = falling.sy + (d3 - falling.sy) * d;
        double d7 = falling.sz + (d4 - falling.sz) * d;
        float[] fArray = MeteorManager.facing(d2 - falling.sx, d3 - falling.sy, d4 - falling.sz);
        MeteorVisual.move(falling.visual, d5, d6, d7, fArray[0], fArray[1]);
        int n = MeteorManager.isBig(falling.type, falling.radius) ? 9 : (int)Math.max(1L, Math.min(10L, Math.round(falling.radius)));
        double d8 = 0.3 * (double)n;
        double d9 = d2 - falling.sx;
        double d10 = d3 - falling.sy;
        double d11 = d4 - falling.sz;
        double d12 = Math.sqrt(d9 * d9 + d10 * d10 + d11 * d11);
        double d13 = d11 /= d12;
        double d14 = -(d9 /= d12);
        double d15 = Math.sqrt(d13 * d13 + d14 * d14);
        double d16 = (d10 /= d12) * (d14 /= d15);
        double d17 = d11 * (d13 /= d15) - d9 * d14;
        double d18 = -d10 * d13;
        block7: for (ServerPlayer serverPlayer : falling.level.players()) {
            if (serverPlayer.distanceToSqr(d5, d6, d7) > 102400.0) continue;
            ServerLevel serverLevel = falling.level;
            switch (falling.type) {
                case ICE: {
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.SNOWFLAKE, true, d5, d6, d7, 14 * n, d8, d8, d8, 0.05);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.CLOUD, true, d5, d6, d7, 4 * n, d8, d8, d8, 0.02);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.CLOUD, true, d5 - d9 * 3.0, d6 - d10 * 3.0, d7 - d11 * 3.0, 6 * n, 0.6, 0.6, 0.6, 0.01);
                    continue block7;
                }
                case PLAIN: {
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.EXPLOSION, true, d5, d6, d7, Math.max(1, n / 2), d8, d8, d8, 0.0);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.LARGE_SMOKE, true, d5, d6, d7, 10 * n, d8, d8, d8, 0.03);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.ASH, true, d5 - d9 * 3.0, d6 - d10 * 3.0, d7 - d11 * 3.0, 20 * n, 1.0, 1.0, 1.0, 0.0);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.FLAME, true, d5, d6, d7, 2 * n, d8 * 0.6, d8 * 0.6, d8 * 0.6, 0.01);
                    continue block7;
                }
                case LIVING: {
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.EXPLOSION, true, d5, d6, d7, Math.max(1, n / 2), d8, d8, d8, 0.0);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.HAPPY_VILLAGER, true, d5, d6, d7, 12 * n, d8 * 1.5, d8 * 1.5, d8 * 1.5, 0.0);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.CHERRY_LEAVES, true, d5 - d9 * 2.0, d6 - d10 * 2.0, d7 - d11 * 2.0, 10 * n, 1.2, 1.2, 1.2, 0.0);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.SPORE_BLOSSOM_AIR, true, d5, d6, d7, 8 * n, d8 * 2.0, d8 * 2.0, d8 * 2.0, 0.0);
                    continue block7;
                }
                case ANCIENT: {
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.EXPLOSION, true, d5, d6, d7, 3, d8, d8, d8, 0.0);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.FLAME, true, d5, d6, d7, 30, d8, d8, d8, 0.02);
                    for (int i = 0; i < 3; ++i) {
                        double d19 = (double)falling.age * 0.55 + (double)i * Math.PI * 2.0 / 3.0;
                        double d20 = 2.5 + (double)n * 0.3;
                        double d21 = d5 + (d13 * Math.cos(d19) + d16 * Math.sin(d19)) * d20;
                        double d22 = d6 + d17 * Math.sin(d19) * d20;
                        double d23 = d7 + (d14 * Math.cos(d19) + d18 * Math.sin(d19)) * d20;
                        serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.END_ROD, true, d21, d22, d23, 4, 0.1, 0.1, 0.1, 0.0);
                    }
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.ENCHANT, true, d5, d6, d7, 40, d8 * 1.5, d8 * 1.5, d8 * 1.5, 0.5);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.LARGE_SMOKE, true, d5 - d9 * 4.0, d6 - d10 * 4.0, d7 - d11 * 4.0, 20, 1.0, 1.0, 1.0, 0.01);
                    continue block7;
                }
                case UNDERGROUND: {
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.EXPLOSION, true, d5, d6, d7, 6, d8, d8, d8, 0.0);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, true, d5, d6, d7, 90, d8 * 1.2, d8 * 1.2, d8 * 1.2, 0.04);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.SOUL, true, d5, d6, d7, 15, d8, d8, d8, 0.02);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.LARGE_SMOKE, true, d5 - d9 * 5.0, d6 - d10 * 5.0, d7 - d11 * 5.0, 50, 2.0, 2.0, 2.0, 0.01);
                    continue block7;
                }
            }
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.EXPLOSION, true, d5, d6, d7, n, d8, d8, d8, 0.0);
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.FLAME, true, d5, d6, d7, 10 * n, d8 * 1.3, d8 * 1.3, d8 * 1.3, 0.03);
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.LAVA, true, d5, d6, d7, 2 * n, d8, d8, d8, 0.0);
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.ELECTRIC_SPARK, true, d5, d6, d7, 6 * n, d8, d8, d8, 0.3);
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.LARGE_SMOKE, true, d5 - d9 * 2.0, d6 - d10 * 2.0 + 1.0, d7 - d11 * 2.0, 4 * n, 0.5, 0.8, 0.5, 0.01);
        }
        if (falling.age % 10 == 0) {
            falling.level.playSound(null, d5, d6, d7, falling.type == MeteorType.ICE ? SoundEvents.GLASS_BREAK : SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT, 10.0f, falling.type == MeteorType.ICE ? 0.6f : 0.4f);
        }
    }

    private static void impactFx(ServerLevel serverLevel, BlockPos blockPos, MeteorType meteorType, double d) {
        double d2 = (double)blockPos.getX() + 0.5;
        double d3 = (double)blockPos.getY() + 1.0;
        double d4 = (double)blockPos.getZ() + 0.5;
        double d5 = 0.5 + Math.min(d, 24.0) * 0.06;
        block12: for (ServerPlayer serverPlayer : serverLevel.players()) {
            int n;
            if (serverPlayer.distanceToSqr(d2, d3, d4) > 102400.0) continue;
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.FLASH, true, d2, d3 + 2.0, d4, 1, 0.0, 0.0, 0.0, 0.0);
            SimpleParticleType simpleParticleType = switch (meteorType) {
                case MeteorType.ICE -> ParticleTypes.SNOWFLAKE;
                case MeteorType.LIVING -> ParticleTypes.HAPPY_VILLAGER;
                case MeteorType.ANCIENT -> ParticleTypes.END_ROD;
                case MeteorType.UNDERGROUND -> ParticleTypes.SOUL_FIRE_FLAME;
                default -> ParticleTypes.CLOUD;
            };
            for (n = 0; n < 48; ++n) {
                double d6 = (double)n * Math.PI * 2.0 / 48.0;
                serverLevel.sendParticles(serverPlayer, (ParticleOptions)simpleParticleType, true, d2, d3, d4, 0, Math.cos(d6), 0.02, Math.sin(d6), d5);
                serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.CLOUD, true, d2, d3, d4, 0, Math.cos(d6), 0.05, Math.sin(d6), d5 * 0.7);
            }
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.LARGE_SMOKE, true, d2, d3 + 1.0, d4, 40, d * 0.6, 2.0, d * 0.6, 0.06);
            switch (meteorType) {
                case ICE: {
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.SNOWFLAKE, true, d2, d3 + 2.0, d4, 120, d, 3.0, d, 0.1);
                    continue block12;
                }
                case LIVING: {
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.SPORE_BLOSSOM_AIR, true, d2, d3 + 1.0, d4, 80, d * 1.5, 2.0, d * 1.5, 0.0);
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.CHERRY_LEAVES, true, d2, d3 + 3.0, d4, 60, d, 2.0, d, 0.0);
                    continue block12;
                }
                case ANCIENT: {
                    for (n = 0; n < 40; n += 2) {
                        serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.END_ROD, true, d2, d3 + (double)n, d4, 3, 0.2, 0.5, 0.2, 0.01);
                    }
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.ENCHANT, true, d2, d3 + 2.0, d4, 200, d * 0.6, 3.0, d * 0.6, 1.0);
                    continue block12;
                }
                case UNDERGROUND: {
                    serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.SOUL, true, d2, d3 + 1.0, d4, 80, d * 0.6, 3.0, d * 0.6, 0.05);
                    continue block12;
                }
            }
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.LAVA, true, d2, d3 + 1.0, d4, 20 + (int)d * 4, d, 0.5, d, 0.0);
        }
        if (meteorType == MeteorType.ANCIENT) {
            serverLevel.playSound(null, d2, d3, d4, SoundEvents.BEACON_ACTIVATE, SoundSource.AMBIENT, 6.0f, 0.6f);
        }
        if (meteorType == MeteorType.LIVING) {
            serverLevel.playSound(null, d2, d3, d4, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 4.0f, 0.8f);
        }
        if (meteorType == MeteorType.ICE) {
            serverLevel.playSound(null, d2, d3, d4, SoundEvents.GLASS_BREAK, SoundSource.AMBIENT, 6.0f, 0.5f);
        }
    }

    private static void impact(Falling falling) {
        int n;
        ServerLevel serverLevel = falling.level;
        BlockPos blockPos = falling.impact;
        MeteorManager.impactFx(serverLevel, blockPos, falling.type, falling.radius);
        if (falling.type == MeteorType.ANCIENT) {
            MeteorManager.impactAncient(falling);
            return;
        }
        if (falling.type == MeteorType.UNDERGROUND) {
            MeteorManager.impactUnderground(falling);
            return;
        }
        double d = Crater.radiusFor(falling.radius) * Math.sqrt(falling.density.craterMultiplier);
        int n4 = Crater.carve(serverLevel, blockPos, d, Crater.depthFor(falling.radius, d), falling.type, serverLevel.getRandom());
        serverLevel.explode(null, (double)blockPos.getX() + 0.5, (double)n4 + 1.0, (double)blockPos.getZ() + 0.5, MeteorManager.craterPower(falling.radius, falling.density), Level.ExplosionInteraction.NONE);
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), n4 + (int)Math.round(falling.radius * 0.35), blockPos.getZ());
        int n5 = falling.shape.bound();
        double d2 = 1.0 - 0.9 / falling.radius;
        RandomSource randomSource = serverLevel.getRandom();
        List<BlockState> list = MeteorBlocks.findModOres();
        Map<Long, BlockState> map = MeteorBlocks.veins(falling.type, falling.richness, falling.density, falling.shape, list, randomSource);
        for (n = -n5; n <= n5; ++n) {
            for (int i = -n5; i <= n5; ++i) {
                for (int j = -n5; j <= n5; ++j) {
                    BlockPos blockPos3;
                    double d3 = falling.shape.norm(n, i, j);
                    if (d3 > 1.0 || !serverLevel.isLoaded(blockPos3 = blockPos2.offset(n, i, j)) || serverLevel.getBlockEntity(blockPos3) != null || serverLevel.getBlockState(blockPos3).is(Blocks.BEDROCK)) continue;
                    if (falling.shape.isCavity(n, i, j)) {
                        serverLevel.setBlock(blockPos3, Blocks.AIR.defaultBlockState(), 18);
                        continue;
                    }
                    boolean bl = d3 > d2;
                    boolean bl2 = n == 0 && i == 0 && j == 0;
                    serverLevel.setBlock(blockPos3, MeteorBlocks.pick(falling.type, falling.density, map.get(MeteorBlocks.key(n, i, j)), randomSource, bl, bl2), 18);
                }
            }
        }
        if (falling.type == MeteorType.LIVING) {
            MeteorManager.livingGarden(serverLevel, blockPos2, falling, randomSource);
        }
        n = falling.shape.bound();
        BlockPos blockPos4 = blockPos2;
        Crater.aftermath(serverLevel, blockPos, d, n4, falling.type, falling.radius, randomSource, (n2, n3) -> {
            int n4 = n2 - blockPos4.getX();
            int n5 = n3 - blockPos4.getZ();
            if (Math.abs(n4) > n || Math.abs(n5) > n) {
                return false;
            }
            for (int i = -n; i <= n; ++i) {
                if (!(falling.shape.norm(n4, i, n5) <= 1.15)) continue;
                return true;
            }
            return false;
        });
        MutableComponent mutableComponent = Component.translatable((String)"message.coaldust.meteor", (Object[])new Object[]{Component.translatable((String)("meteor.coaldust.type." + falling.type.id)), Component.translatable((String)("meteor.coaldust.richness." + falling.richness.id)), Component.translatable((String)("meteor.coaldust.density." + falling.density.id)), Component.translatable((String)("meteor.coaldust.shape." + falling.shape.kind.id)), (int)Math.round(falling.radius * 2.0)});
        for (ServerPlayer serverPlayer : serverLevel.players()) {
            if (!(serverPlayer.distanceToSqr((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()) <= 25600.0)) continue;
            serverPlayer.displayClientMessage((Component)mutableComponent, true);
        }
    }

    private static void impactUnderground(Falling falling) {
        ServerLevel serverLevel = falling.level;
        BlockPos blockPos = falling.impact;
        int n = falling.variant >= 0 ? falling.variant : UndergroundCity.rollVariant(serverLevel.getRandom(), falling.radius >= 29.0);
        int n2 = UndergroundCity.radiusFor(n);
        Crater.carve(serverLevel, blockPos, Math.min((double)n2 * 1.3, 42.0), 3.0, falling.type, serverLevel.getRandom());
        serverLevel.explode(null, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 6.0f, Level.ExplosionInteraction.NONE);
        BlockPos blockPos2 = blockPos.below((int)Math.round((double)n2 * 0.6));
        long l = serverLevel.getRandom().nextLong();
        for (ServerPlayer serverPlayer : serverLevel.players()) {
            if (!(serverPlayer.distanceToSqr((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()) <= 25600.0)) continue;
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"message.coaldust.meteor_building"), true);
        }
        CompletableFuture.supplyAsync(() -> {
            UndergroundCity.Plan plan = UndergroundCity.plan(n2, RandomSource.create((long)l), n);
            return new Object[]{plan, new BuildJob(serverLevel, blockPos2, plan)};
        }).whenComplete((objectArray, throwable) -> READY.add(() -> {
            if (throwable != null || objectArray == null) {
                MeteorManager.fail(serverLevel, throwable);
                return;
            }
            UndergroundCity.Plan plan = (UndergroundCity.Plan)objectArray[0];
            JOBS.add((BuildJob)objectArray[1]);
            MutableComponent mutableComponent = Component.translatable((String)"message.coaldust.meteor_underground", (Object[])new Object[]{3, plan.rooms, Component.translatable((String)("meteor.coaldust.city_theme." + plan.theme)), Component.translatable((String)("meteor.coaldust.city_state." + plan.state))});
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (serverPlayer.distanceToSqr((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()) > 102400.0) continue;
                serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.LARGE_SMOKE, true, (double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 4), (double)blockPos.getZ() + 0.5, 120, (double)n2 * 0.8, 3.0, (double)n2 * 0.8, 0.05);
                if (!(serverPlayer.distanceToSqr((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()) <= 25600.0)) continue;
                serverPlayer.displayClientMessage((Component)mutableComponent, true);
            }
        }));
    }

    private static void fail(ServerLevel serverLevel, Throwable throwable) {
        Throwable throwable2;
        for (throwable2 = throwable; throwable2 != null && throwable2.getCause() != null && throwable2 instanceof CompletionException; throwable2 = throwable2.getCause()) {
        }
        Object object = "";
        if (throwable2 != null) {
            throwable2.printStackTrace();
            for (StackTraceElement stackTraceElement : throwable2.getStackTrace()) {
                if (!stackTraceElement.getClassName().startsWith("com.coaldust")) continue;
                object = " @ " + stackTraceElement.getClassName().substring(stackTraceElement.getClassName().lastIndexOf(46) + 1) + ":" + stackTraceElement.getLineNumber();
                break;
            }
        }
        String string = "[Coal Dust] \u041f\u043e\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u043c\u0435\u0442\u0435\u043e\u0440\u0438\u0442\u0430 \u043d\u0435 \u0443\u0434\u0430\u043b\u0430\u0441\u044c: " + (String)(throwable2 == null ? "\u043f\u0443\u0441\u0442\u043e\u0439 \u043f\u043b\u0430\u043d" : throwable2.getClass().getSimpleName() + ": " + throwable2.getMessage()) + (String)object;
        System.err.println(string);
        for (ServerPlayer serverPlayer : serverLevel.players()) {
            serverPlayer.sendSystemMessage((Component)Component.literal((String)string));
        }
    }

    private static void impactAncient(Falling falling) {
        ServerLevel serverLevel = falling.level;
        BlockPos blockPos = falling.impact;
        AncientStructures.Kind kind = falling.variant >= 0 && falling.variant / 10 < AncientStructures.Kind.values().length ? AncientStructures.Kind.values()[falling.variant / 10] : AncientStructures.Kind.forRadius(falling.radius);
        int n = falling.variant >= 0 ? falling.variant % 10 : -1;
        Crater.carve(serverLevel, blockPos, kind.radius + 6, 1.0, falling.type, serverLevel.getRandom());
        serverLevel.explode(null, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 4.0f, Level.ExplosionInteraction.NONE);
        long l = serverLevel.getRandom().nextLong();
        CompletableFuture.supplyAsync(() -> {
            AncientStructures.Result result = AncientStructures.plan(kind, RandomSource.create((long)l), n);
            return new Object[]{result, new BuildJob(serverLevel, blockPos, result.plan())};
        }).whenComplete((objectArray, throwable) -> READY.add(() -> {
            if (throwable != null || objectArray == null) {
                MeteorManager.fail(serverLevel, throwable);
                return;
            }
            AncientStructures.Result result = (AncientStructures.Result)objectArray[0];
            JOBS.add((BuildJob)objectArray[1]);
            MutableComponent mutableComponent = Component.translatable((String)"message.coaldust.meteor_ancient", (Object[])new Object[]{Component.translatable((String)("meteor.coaldust.ancient." + result.variantId())), Component.translatable((String)("meteor.coaldust.style." + result.paletteId()))});
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (serverPlayer.distanceToSqr((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()) > 102400.0) continue;
                serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.LARGE_SMOKE, true, (double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 2), (double)blockPos.getZ() + 0.5, 80, (double)kind.radius * 0.7, 2.0, (double)kind.radius * 0.7, 0.05);
                if (!(serverPlayer.distanceToSqr((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()) <= 25600.0)) continue;
                serverPlayer.displayClientMessage((Component)mutableComponent, true);
            }
        }));
    }

    private static void livingGarden(ServerLevel serverLevel, BlockPos blockPos, Falling falling, RandomSource randomSource) {
        BlockPos blockPos2;
        int n = falling.shape.bound();
        for (int i = -n; i <= n; ++i) {
            block1: for (int j = -n; j <= n; ++j) {
                for (int k = n; k >= -n; --k) {
                    BlockPos blockPos3;
                    if (falling.shape.norm(i, k, j) > 1.0 || serverLevel.getBlockState(blockPos3 = blockPos.offset(i, k, j)).isAir()) continue;
                    if (!serverLevel.getBlockState(blockPos3.above()).isAir()) continue block1;
                    serverLevel.setBlock(blockPos3, Blocks.MOSS_BLOCK.defaultBlockState(), 3);
                    continue block1;
                }
            }
        }
        if (falling.radius < 2.2) {
            return;
        }
        double d = 0.62;
        ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
        for (int i = -n; i <= n; ++i) {
            for (int j = -n; j <= n; ++j) {
                for (int k = -n; k <= n; ++k) {
                    if (falling.shape.norm(i, j, k) >= d || !serverLevel.isLoaded(blockPos2 = blockPos.offset(i, j, k))) continue;
                    serverLevel.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                    arrayList.add(blockPos2);
                }
            }
        }
        HashSet hashSet = new HashSet(arrayList);
        for (BlockPos blockPos4 : arrayList) {
            blockPos2 = blockPos4.below();
            BlockPos blockPos5 = blockPos4.above();
            if (!hashSet.contains(blockPos2)) {
                BlockState blockState;
                serverLevel.setBlock(blockPos2, randomSource.nextFloat() < 0.2f ? Blocks.ROOTED_DIRT.defaultBlockState() : Blocks.MOSS_BLOCK.defaultBlockState(), 3);
                if (!(randomSource.nextFloat() < 0.7f) || (blockState = MeteorBlocks.plant(randomSource)) == null || !blockState.canSurvive((LevelReader)serverLevel, blockPos4)) continue;
                serverLevel.setBlock(blockPos4, blockState, 3);
                continue;
            }
            if (hashSet.contains(blockPos5)) continue;
            float f = randomSource.nextFloat();
            if (f < 0.1f) {
                serverLevel.setBlock(blockPos5, Blocks.SHROOMLIGHT.defaultBlockState(), 3);
                continue;
            }
            if (f < 0.25f) {
                serverLevel.setBlock(blockPos4, Blocks.HANGING_ROOTS.defaultBlockState(), 3);
                continue;
            }
            if (f < 0.33f) {
                serverLevel.setBlock(blockPos4, Blocks.SPORE_BLOSSOM.defaultBlockState(), 3);
                continue;
            }
            if (!(f < 0.55f)) continue;
            serverLevel.setBlock(blockPos5, Blocks.MOSS_BLOCK.defaultBlockState(), 3);
        }
        int n2 = 0;
        while (falling.shape.norm(0.0, n2, 0.0) < d) {
            ++n2;
        }
        for (int i = n2; i <= n && falling.shape.norm(0.0, i, 0.0) <= 1.0; ++i) {
            serverLevel.setBlock(blockPos.offset(0, i, 0), Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private MeteorManager() {
    }

    private static final class Falling {
        final ServerLevel level;
        final BlockPos impact;
        final MeteorType type;
        final double radius;
        final MeteorShape shape;
        final MeteorClass.Richness richness;
        final MeteorClass.Density density;
        final double sx;
        final double sy;
        final double sz;
        final int fallTicks;
        int variant = -1;
        int age;
        MeteorVisual.Visual visual;

        Falling(ServerLevel serverLevel, BlockPos blockPos, MeteorType meteorType, double d, double d2, double d3, double d4) {
            this.level = serverLevel;
            this.impact = blockPos;
            this.type = meteorType;
            this.radius = d;
            RandomSource randomSource = serverLevel.getRandom();
            this.shape = MeteorShape.roll(randomSource, d);
            this.richness = MeteorClass.Richness.roll(randomSource);
            this.density = MeteorClass.Density.roll(randomSource);
            this.sx = d2;
            this.sy = d3;
            this.sz = d4;
            this.fallTicks = MeteorManager.isBig(meteorType, d) ? 90 : 50;
        }
    }
}
