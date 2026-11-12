/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 */
package com.coaldust.meteor;

import com.coaldust.meteor.MeteorType;
import com.coaldust.meteor.underground.AncientTrees;
import com.coaldust.meteor.underground.Flora;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class Crater {
    private static final int FLAGS = 18;

    private static BlockState s(Block block) {
        return block.defaultBlockState();
    }

    public static int carve(ServerLevel serverLevel, BlockPos blockPos, double d, double d2, MeteorType meteorType, RandomSource randomSource) {
        int n = blockPos.getY();
        double d3 = randomSource.nextDouble() * 6.28;
        double d4 = randomSource.nextDouble() * 6.28;
        int n2 = (int)Math.ceil(d * 1.55);
        for (int i = -n2; i <= n2; ++i) {
            for (int j = -n2; j <= n2; ++j) {
                int n3;
                BlockState blockState;
                int n4;
                int n5;
                BlockPos blockPos2;
                double d5;
                double d6;
                double d7 = Math.sqrt(i * i + j * j);
                double d8 = d7 / (d6 = d * (1.0 + 0.09 * Math.sin((d5 = Math.atan2(j, i)) * 5.0 + d3) + 0.05 * Math.sin(d5 * 11.0 + d4)));
                if (d8 > 1.5 || !serverLevel.isLoaded(blockPos2 = new BlockPos(n5 = blockPos.getX() + i, n, n4 = blockPos.getZ() + j)) || (blockState = serverLevel.getBlockState(new BlockPos(n5, n3 = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, n5, n4) - 1, n4))).is(Blocks.WATER) || blockState.is(Blocks.LAVA) || blockState.is(Blocks.ICE)) continue;
                if (d8 < 1.0) {
                    Crater.bowl(serverLevel, n5, n4, n, n3, d8, d2, meteorType, randomSource);
                    continue;
                }
                Crater.rim(serverLevel, n5, n4, n3, d8, d2, meteorType, randomSource);
            }
        }
        Crater.ejecta(serverLevel, blockPos, d, meteorType, randomSource);
        return n - (int)Math.round(d2);
    }

    private static void bowl(ServerLevel serverLevel, int n, int n2, int n3, int n4, double d, double d2, MeteorType meteorType, RandomSource randomSource) {
        int n5 = n3 - (int)Math.round(d2 * (1.0 - d * d));
        int n6 = Math.max(n4, n3) + (d < 0.8 ? 8 : 3);
        for (int i = n5 + 1; i <= n6; ++i) {
            Crater.clear(serverLevel, new BlockPos(n, i, n2));
        }
        BlockPos blockPos = new BlockPos(n, n5, n2);
        if (Crater.replaceable(serverLevel, blockPos)) {
            serverLevel.setBlock(blockPos, Crater.scorch(meteorType, randomSource, true), 18);
        }
        BlockPos blockPos2 = blockPos.below();
        if (randomSource.nextFloat() < 0.5f && Crater.replaceable(serverLevel, blockPos2)) {
            serverLevel.setBlock(blockPos2, Crater.scorch(meteorType, randomSource, false), 18);
        }
        BlockPos blockPos3 = blockPos.above();
        float f = randomSource.nextFloat();
        switch (meteorType) {
            case ICE: {
                if (!(f < 0.25f)) break;
                serverLevel.setBlock(blockPos3, Crater.s(Blocks.SNOW), 18);
                break;
            }
            case LIVING: {
                if (!(f < 0.12f)) break;
                serverLevel.setBlock(blockPos3, Crater.s(Blocks.MOSS_CARPET), 18);
                break;
            }
            case UNDERGROUND: {
                if (!(f < 0.04f) || !serverLevel.getBlockState(blockPos).is(Blocks.SOUL_SOIL)) break;
                serverLevel.setBlock(blockPos3, Crater.s(Blocks.SOUL_FIRE), 18);
                break;
            }
            case ANCIENT: {
                break;
            }
            default: {
                if (!(f < 0.035f)) break;
                serverLevel.setBlock(blockPos3, Crater.s(Blocks.FIRE), 18);
            }
        }
    }

    private static void rim(ServerLevel serverLevel, int n, int n2, int n3, double d, double d2, MeteorType meteorType, RandomSource randomSource) {
        BlockPos blockPos;
        double d3 = (d - 1.0) / 0.5;
        int n4 = (int)Math.round(Math.max(1.0, d2 * 0.45) * Math.pow(1.0 - d3, 1.6) + ((double)randomSource.nextFloat() - 0.5) * 0.8);
        if (n4 <= 0) {
            if ((double)randomSource.nextFloat() < (double)0.35f * (1.0 - d3)) {
                Crater.scorchTop(serverLevel, n, n3, n2, meteorType, randomSource);
            }
            return;
        }
        BlockPos blockPos2 = new BlockPos(n, n3, n2);
        BlockState blockState = serverLevel.getBlockState(blockPos2.below());
        BlockState blockState2 = blockState.isAir() || serverLevel.getBlockEntity(blockPos2.below()) != null ? Crater.s(Blocks.COARSE_DIRT) : Crater.soil(blockState);
        for (int i = 1; i <= n4 && (Crater.replaceable(serverLevel, blockPos = new BlockPos(n, n3 + i, n2)) || serverLevel.getBlockState(blockPos).isAir()); ++i) {
            serverLevel.setBlock(blockPos, i == n4 && randomSource.nextFloat() < 0.6f ? Crater.scorch(meteorType, randomSource, false) : blockState2, 18);
        }
    }

    private static void scorchTop(ServerLevel serverLevel, int n, int n2, int n3, MeteorType meteorType, RandomSource randomSource) {
        BlockPos blockPos = new BlockPos(n, n2, n3);
        if (Crater.replaceable(serverLevel, blockPos) && !serverLevel.getBlockState(blockPos).isAir()) {
            serverLevel.setBlock(blockPos, Crater.scorch(meteorType, randomSource, false), 18);
        }
    }

    private static void ejecta(ServerLevel serverLevel, BlockPos blockPos, double d, MeteorType meteorType, RandomSource randomSource) {
        int n = (int)(d * 5.0);
        for (int i = 0; i < n; ++i) {
            BlockPos blockPos2;
            int n2;
            BlockPos blockPos3;
            double d2 = randomSource.nextDouble() * Math.PI * 2.0;
            double d3 = d * (1.5 + randomSource.nextDouble() * 1.3);
            int n3 = blockPos.getX() + (int)Math.round(Math.cos(d2) * d3);
            int n4 = blockPos.getZ() + (int)Math.round(Math.sin(d2) * d3);
            BlockPos blockPos4 = new BlockPos(n3, blockPos.getY(), n4);
            if (!serverLevel.isLoaded(blockPos4) || !serverLevel.getBlockState((blockPos3 = new BlockPos(n3, n2 = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, n3, n4), n4)).below()).isFaceSturdy((BlockGetter)serverLevel, blockPos3.below(), Direction.UP) || serverLevel.getBlockState(blockPos3.below()).is(Blocks.WATER) || serverLevel.getBlockState(blockPos3.below()).is(Blocks.ICE) || !serverLevel.getBlockState(blockPos3).isAir() && !serverLevel.getBlockState(blockPos3).canBeReplaced()) continue;
            serverLevel.setBlock(blockPos3, Crater.debris(meteorType, randomSource), 18);
            if (!(randomSource.nextFloat() < 0.25f) || !serverLevel.getBlockState(blockPos2 = blockPos3.above()).isAir()) continue;
            serverLevel.setBlock(blockPos2, Crater.debris(meteorType, randomSource), 18);
        }
    }

    private static void clear(ServerLevel serverLevel, BlockPos blockPos) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        if (blockState.isAir() || blockState.is(Blocks.BEDROCK) || serverLevel.getBlockEntity(blockPos) != null) {
            return;
        }
        serverLevel.setBlock(blockPos, Crater.s(Blocks.AIR), 18);
    }

    private static boolean replaceable(ServerLevel serverLevel, BlockPos blockPos) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        return !blockState.is(Blocks.BEDROCK) && serverLevel.getBlockEntity(blockPos) == null;
    }

    private static BlockState soil(BlockState blockState) {
        if (blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.DIRT) || blockState.is(Blocks.PODZOL) || blockState.is(Blocks.MYCELIUM) || blockState.is(Blocks.ROOTED_DIRT)) {
            return Crater.s(Blocks.COARSE_DIRT);
        }
        if (blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA)) {
            return Crater.s(Blocks.GRAVEL);
        }
        if (!blockState.canBeReplaced()) {
            return blockState.getBlock().defaultBlockState();
        }
        return Crater.s(Blocks.COARSE_DIRT);
    }

    private static BlockState scorch(MeteorType meteorType, RandomSource randomSource, boolean bl) {
        float f = randomSource.nextFloat();
        return switch (meteorType) {
            case MeteorType.ICE -> {
                if (f < 0.4f) {
                    yield Crater.s(Blocks.PACKED_ICE);
                }
                if (f < 0.75f) {
                    yield Crater.s(Blocks.SNOW_BLOCK);
                }
                if (f < 0.9f) {
                    yield Crater.s(Blocks.ICE);
                }
                yield Crater.s(Blocks.GRAVEL);
            }
            case MeteorType.LIVING -> {
                if (f < 0.5f) {
                    yield Crater.s(Blocks.MOSS_BLOCK);
                }
                if (f < 0.8f) {
                    yield Crater.s(Blocks.ROOTED_DIRT);
                }
                yield Crater.s(Blocks.COARSE_DIRT);
            }
            case MeteorType.ANCIENT -> {
                if (f < 0.35f) {
                    yield Crater.s(Blocks.CALCITE);
                }
                if (f < 0.65f) {
                    yield Crater.s(Blocks.SMOOTH_BASALT);
                }
                if (f < 0.85f) {
                    yield Crater.s(Blocks.COARSE_DIRT);
                }
                yield Crater.s(Blocks.GRAVEL);
            }
            case MeteorType.UNDERGROUND -> {
                if (f < 0.4f) {
                    yield Crater.s(Blocks.SOUL_SOIL);
                }
                if (f < 0.65f) {
                    yield Crater.s(Blocks.SOUL_SAND);
                }
                if (f < 0.85f) {
                    yield Crater.s(Blocks.BASALT);
                }
                yield Crater.s(Blocks.BLACKSTONE);
            }
            default -> bl && f < 0.06f ? Crater.s(Blocks.MAGMA_BLOCK) : (f < 0.35f ? Crater.s(Blocks.COARSE_DIRT) : (f < 0.6f ? Crater.s(Blocks.BLACKSTONE) : (f < 0.8f ? Crater.s(Blocks.BASALT) : (f < 0.92f ? Crater.s(Blocks.GRAVEL) : Crater.s(Blocks.TUFF)))));
        };
    }

    private static BlockState debris(MeteorType meteorType, RandomSource randomSource) {
        float f = randomSource.nextFloat();
        return switch (meteorType) {
            case MeteorType.ICE -> {
                if (f < 0.6f) {
                    yield Crater.s(Blocks.PACKED_ICE);
                }
                yield Crater.s(Blocks.BLUE_ICE);
            }
            case MeteorType.LIVING -> {
                if (f < 0.5f) {
                    yield Crater.s(Blocks.MOSSY_COBBLESTONE);
                }
                yield Crater.s(Blocks.MOSS_BLOCK);
            }
            case MeteorType.ANCIENT -> {
                if (f < 0.5f) {
                    yield Crater.s(Blocks.CRACKED_STONE_BRICKS);
                }
                yield Crater.s(Blocks.COBBLESTONE);
            }
            case MeteorType.UNDERGROUND -> {
                if (f < 0.5f) {
                    yield Crater.s(Blocks.BLACKSTONE);
                }
                yield Crater.s(Blocks.BASALT);
            }
            default -> f < 0.35f ? Crater.s(Blocks.BLACKSTONE) : (f < 0.6f ? Crater.s(Blocks.BASALT) : (f < 0.75f ? Crater.s(Blocks.COBBLESTONE) : (f < 0.9f ? Crater.s(Blocks.GRAVEL) : Crater.s(Blocks.OBSIDIAN))));
        };
    }

    public static double radiusFor(double d) {
        if (d < 2.0) {
            return 1.0 + d * 1.2;
        }
        return 2.5 + d * 1.9;
    }

    public static double depthFor(double d, double d2) {
        return Math.max(0.6, d2 * 0.42 * Math.min(1.0, Math.max(0.15, (d - 0.8) / 2.5)));
    }

    public static void aftermath(ServerLevel serverLevel, BlockPos blockPos, double d, int n, MeteorType meteorType, double d2, RandomSource randomSource, Footprint footprint) {
        int n2;
        Spots spots = new Spots();
        for (n2 = 0; n2 < 40 && (footprint.onMeteor(blockPos.getX() + n2, blockPos.getZ()) || footprint.onMeteor(blockPos.getX() - n2, blockPos.getZ()) || footprint.onMeteor(blockPos.getX(), blockPos.getZ() + n2) || footprint.onMeteor(blockPos.getX(), blockPos.getZ() - n2)); ++n2) {
        }
        spots.add(blockPos.getX(), blockPos.getZ(), Math.max(1, n2 - 2));
        switch (meteorType) {
            case ICE: {
                Crater.ice(serverLevel, blockPos, d, d2, randomSource, footprint, spots);
                break;
            }
            case SMALL_RESOURCE: 
            case MEDIUM_RESOURCE: 
            case LARGE_RESOURCE: {
                Crater.oreMounds(serverLevel, blockPos, d, meteorType, d2, randomSource, footprint, spots);
                break;
            }
            case LIVING: {
                Crater.living(serverLevel, blockPos, d, d2, n2, randomSource, footprint, spots);
                break;
            }
            case PLAIN: {
                Crater.ash(serverLevel, blockPos, d, d2, randomSource, footprint);
                break;
            }
        }
    }

    private static int surface(ServerLevel serverLevel, int n, int n2) {
        return serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, n, n2);
    }

    private static boolean free(ServerLevel serverLevel, BlockPos blockPos) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        return (blockState.isAir() || blockState.canBeReplaced()) && serverLevel.getBlockEntity(blockPos) == null;
    }

    private static boolean land(ServerLevel serverLevel, int n, int n2, int n3) {
        BlockState blockState = serverLevel.getBlockState(new BlockPos(n, n2 - 1, n3));
        return !blockState.isAir() && !blockState.is(Blocks.WATER) && !blockState.is(Blocks.LAVA) && !blockState.is(Blocks.ICE);
    }

    private static void put(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        if (serverLevel.isLoaded(blockPos) && serverLevel.getBlockEntity(blockPos) == null && !serverLevel.getBlockState(blockPos).is(Blocks.BEDROCK)) {
            serverLevel.setBlock(blockPos, blockState, 18);
        }
    }

    private static void ice(ServerLevel serverLevel, BlockPos blockPos, double d, double d2, RandomSource randomSource, Footprint footprint, Spots spots) {
        int n;
        int n2;
        int n3;
        int n4 = (int)Math.ceil(d * (1.5 + d2 * 0.05));
        for (n3 = -n4; n3 <= n4; ++n3) {
            for (n2 = -n4; n2 <= n4; ++n2) {
                double d3 = Math.sqrt(n3 * n3 + n2 * n2);
                if (d3 > (double)n4 || (double)randomSource.nextFloat() > 1.15 - d3 / (double)n4) continue;
                int n5 = blockPos.getX() + n3;
                int n6 = blockPos.getZ() + n2;
                if (!serverLevel.isLoaded(new BlockPos(n5, blockPos.getY(), n6)) || footprint.onMeteor(n5, n6)) continue;
                n = Crater.surface(serverLevel, n5, n6);
                BlockPos blockPos2 = new BlockPos(n5, n - 1, n6);
                BlockPos blockPos3 = new BlockPos(n5, n, n6);
                BlockState blockState = serverLevel.getBlockState(blockPos2);
                if (blockState.is(Blocks.WATER)) {
                    if (!(d3 < d * 1.2)) continue;
                    Crater.put(serverLevel, blockPos2, Crater.s(Blocks.ICE));
                    continue;
                }
                if (d3 < d * 0.7 && d2 >= 2.5 && randomSource.nextFloat() < 0.5f) {
                    Crater.put(serverLevel, blockPos2, Crater.s(Blocks.PACKED_ICE));
                    continue;
                }
                if (!Crater.free(serverLevel, blockPos3) || blockState.isAir()) continue;
                Crater.put(serverLevel, blockPos3, Crater.s(Blocks.SNOW));
            }
        }
        if (d2 < 2.5) {
            return;
        }
        n3 = (int)Math.round(d2 * 0.8);
        n2 = 0;
        for (int i = 0; n2 < n3 && i < n3 * 8; ++i) {
            int n7;
            double d4 = randomSource.nextDouble() * Math.PI * 2.0;
            double d5 = d * (0.8 + randomSource.nextDouble() * 0.7);
            int n8 = blockPos.getX() + (int)Math.round(Math.cos(d4) * d5);
            int n9 = blockPos.getZ() + (int)Math.round(Math.sin(d4) * d5);
            double d6 = 0.8 + d2 * 0.05;
            if (footprint.onMeteor(n8, n9) || !spots.free(n8, n9, d6) || !Crater.land(serverLevel, n8, n7 = Crater.surface(serverLevel, n8, n9), n9)) continue;
            int n10 = 2 + randomSource.nextInt(3) + (int)(d2 * 0.5);
            Crater.spike(serverLevel, n8, n7, n9, n10, d6, randomSource);
            spots.add(n8, n9, d6);
            ++n2;
        }
        if (d2 >= 8.0) {
            for (n2 = 0; n2 < 12; ++n2) {
                int n11;
                int n12;
                double d7 = randomSource.nextDouble() * Math.PI * 2.0;
                double d8 = d * (1.0 + randomSource.nextDouble() * 0.3);
                n = blockPos.getX() + (int)Math.round(Math.cos(d7) * d8);
                if (footprint.onMeteor(n, n12 = blockPos.getZ() + (int)Math.round(Math.sin(d7) * d8)) || !spots.free(n, n12, 2.0) || !Crater.land(serverLevel, n, n11 = Crater.surface(serverLevel, n, n12), n12)) continue;
                Crater.spike(serverLevel, n, n11, n12, (int)(d2 * 1.3), 1.6 + d2 * 0.06, randomSource);
                spots.add(n, n12, 2.0);
                break;
            }
        }
    }

    private static void spike(ServerLevel serverLevel, int n, int n2, int n3, int n4, double d, RandomSource randomSource) {
        for (int i = 0; i < n4; ++i) {
            double d2 = d * Math.pow(1.0 - (double)i / (double)n4, 0.8);
            int n5 = (int)Math.ceil(d2);
            for (int j = -n5; j <= n5; ++j) {
                for (int k = -n5; k <= n5; ++k) {
                    BlockPos blockPos;
                    if (!((double)(j * j + k * k) <= d2 * d2 + 0.3) || !Crater.free(serverLevel, blockPos = new BlockPos(n + j, n2 + i, n3 + k))) continue;
                    Crater.put(serverLevel, blockPos, Crater.s(randomSource.nextFloat() < 0.75f ? Blocks.PACKED_ICE : Blocks.BLUE_ICE));
                }
            }
        }
    }

    private static void oreMounds(ServerLevel serverLevel, BlockPos blockPos, double d, MeteorType meteorType, double d2, RandomSource randomSource, Footprint footprint, Spots spots) {
        int n = Math.max(1, (int)Math.round(d2 * 0.9));
        float f = meteorType == MeteorType.LARGE_RESOURCE ? 0.09f : (meteorType == MeteorType.MEDIUM_RESOURCE ? 0.06f : 0.035f);
        int n2 = 0;
        for (int i = 0; n2 < n && i < n * 10; ++i) {
            double d3 = randomSource.nextDouble() * Math.PI * 2.0;
            double d4 = d * (1.05 + randomSource.nextDouble() * 0.5);
            int n3 = blockPos.getX() + (int)Math.round(Math.cos(d3) * d4);
            int n4 = blockPos.getZ() + (int)Math.round(Math.sin(d3) * d4);
            double d5 = Math.min(5.0, 1.5 + d2 * 0.35 + randomSource.nextDouble() * 0.5);
            if (footprint.onMeteor(n3, n4) || !spots.free(n3, n4, d5) || !Crater.land(serverLevel, n3, Crater.surface(serverLevel, n3, n4), n4)) continue;
            spots.add(n3, n4, d5);
            ++n2;
            int n5 = (int)Math.ceil(d5);
            for (int j = -n5; j <= n5; ++j) {
                for (int k = -n5; k <= n5; ++k) {
                    double d6 = Math.sqrt(j * j + k * k);
                    if (d6 > d5) continue;
                    int n6 = n3 + j;
                    int n7 = n4 + k;
                    if (!serverLevel.isLoaded(new BlockPos(n6, blockPos.getY(), n7)) || footprint.onMeteor(n6, n7)) continue;
                    int n8 = (int)Math.round(d5 * 0.8 * Math.sqrt(1.0 - d6 / d5) + (double)randomSource.nextFloat() * 0.6);
                    int n9 = Crater.surface(serverLevel, n6, n7);
                    if (!Crater.land(serverLevel, n6, n9, n7)) continue;
                    for (int i2 = 0; i2 < n8; ++i2) {
                        BlockPos blockPos2 = new BlockPos(n6, n9 + i2, n7);
                        if (!Crater.free(serverLevel, blockPos2)) continue;
                        float f2 = randomSource.nextFloat();
                        Crater.put(serverLevel, blockPos2, f2 < f ? Crater.oreBlock(meteorType, randomSource) : Crater.s(f2 < 0.45f ? Blocks.STONE : (f2 < 0.65f ? Blocks.COBBLESTONE : (f2 < 0.8f ? Blocks.GRAVEL : (f2 < 0.9f ? Blocks.TUFF : Blocks.ANDESITE)))));
                    }
                }
            }
        }
    }

    private static BlockState oreBlock(MeteorType meteorType, RandomSource randomSource) {
        int n = randomSource.nextInt(100);
        if (meteorType == MeteorType.LARGE_RESOURCE) {
            if (n < 3) {
                return Crater.s(Blocks.DIAMOND_ORE);
            }
            if (n < 5) {
                return Crater.s(Blocks.EMERALD_ORE);
            }
            if (n < 15) {
                return Crater.s(Blocks.LAPIS_ORE);
            }
            if (n < 25) {
                return Crater.s(Blocks.GOLD_ORE);
            }
        }
        if (meteorType != MeteorType.SMALL_RESOURCE && n < 40) {
            return Crater.s(n % 2 == 0 ? Blocks.REDSTONE_ORE : Blocks.GOLD_ORE);
        }
        return Crater.s(n < 55 ? Blocks.COAL_ORE : (n < 80 ? Blocks.IRON_ORE : Blocks.COPPER_ORE));
    }

    private static void living(ServerLevel serverLevel, BlockPos blockPos, double d, double d2, int n, RandomSource randomSource, Footprint footprint, Spots spots) {
        double d3;
        int n2;
        int n3;
        int n4 = (int)Math.ceil(d * (1.3 + d2 * 0.06));
        for (n3 = -n4; n3 <= n4; ++n3) {
            for (int i = -n4; i <= n4; ++i) {
                double d4 = Math.sqrt(n3 * n3 + i * i);
                if (d4 > (double)n4 || (double)randomSource.nextFloat() > 0.78 - d4 / (double)n4 * 0.45) continue;
                n2 = blockPos.getX() + n3;
                int n5 = blockPos.getZ() + i;
                if (!serverLevel.isLoaded(new BlockPos(n2, blockPos.getY(), n5)) || footprint.onMeteor(n2, n5)) continue;
                int n6 = Crater.surface(serverLevel, n2, n5);
                BlockPos blockPos2 = new BlockPos(n2, n6 - 1, n5);
                BlockPos blockPos3 = new BlockPos(n2, n6, n5);
                BlockState blockState = serverLevel.getBlockState(blockPos2);
                if (blockState.is(Blocks.WATER) || blockState.isAir() || !blockState.is(Blocks.DIRT) && !blockState.is(Blocks.GRASS_BLOCK) && !blockState.is(Blocks.COARSE_DIRT) && !blockState.is(Blocks.PODZOL) && !blockState.is(Blocks.GRAVEL) && !blockState.is(Blocks.STONE) && !blockState.is(Blocks.SAND) && !blockState.is(Blocks.MOSS_BLOCK)) continue;
                Crater.put(serverLevel, blockPos2, randomSource.nextFloat() < 0.2f ? Crater.s(Blocks.MOSS_BLOCK) : Crater.s(Blocks.GRASS_BLOCK));
                if (!Crater.free(serverLevel, blockPos3)) continue;
                float f = randomSource.nextFloat();
                d3 = 1.0 - d4 / (double)n4;
                if (f < 0.12f) {
                    Crater.put(serverLevel, blockPos3, Crater.s(Blocks.SHORT_GRASS));
                    continue;
                }
                if ((double)f < (double)0.12f + (double)0.2f * d3 + (double)0.06f) {
                    Crater.put(serverLevel, blockPos3, Flora.GRASS.get());
                    continue;
                }
                if (f < 0.42f) {
                    Crater.put(serverLevel, blockPos3, randomSource.nextFloat() < 0.5f ? Flora.FERN.get() : Crater.s(Blocks.FERN));
                    continue;
                }
                if (f < 0.48f) {
                    Crater.put(serverLevel, blockPos3, Crater.s(Crater.flower(randomSource)));
                    continue;
                }
                if (f < 0.53f) {
                    Crater.put(serverLevel, blockPos3, Flora.SHRUB.get());
                    continue;
                }
                if (f < 0.56f) {
                    Crater.put(serverLevel, blockPos3, Flora.BERRY.get());
                    continue;
                }
                if (!(f < 0.58f) || !(d2 >= 4.0)) continue;
                Crater.put(serverLevel, blockPos3, Crater.s(Blocks.TORCHFLOWER));
            }
        }
        if (d2 >= 5.5) {
            n3 = (int)Math.round(Math.min(7.0, 1.0 + (d2 - 5.5) * 0.6));
            double d5 = Math.min(3.0, 1.0 + (d2 - 5.5) / 5.0);
            int n7 = 0;
            for (n2 = 0; n7 < n3 && n2 < n3 * 20; ++n2) {
                int n8;
                double d6 = randomSource.nextDouble() * Math.PI * 2.0;
                double d7 = (double)(n + 3) + randomSource.nextDouble() * Math.max(3.0, (double)n4 * 0.6 - (double)n);
                int n9 = blockPos.getX() + (int)Math.round(Math.cos(d6) * d7);
                int n10 = blockPos.getZ() + (int)Math.round(Math.sin(d6) * d7);
                d3 = 3.0 + d5 * 1.5;
                if (footprint.onMeteor(n9, n10) || !spots.free(n9, n10, d3) || !Crater.land(serverLevel, n9, n8 = Crater.surface(serverLevel, n9, n10), n10)) continue;
                Crater.treeAt(serverLevel, n9, n8, n10, d5, randomSource);
                spots.add(n9, n10, d3);
                ++n7;
            }
        }
        if (d2 < 11.0) {
            return;
        }
        double d8 = Math.min(6.0, 2.0 + d2 * 0.4);
        for (int i = 0; i < 16; ++i) {
            int n11;
            int n12;
            int n13;
            double d9 = randomSource.nextDouble() * Math.PI * 2.0;
            double d10 = (double)n + d8 - 0.5;
            int n14 = blockPos.getX() + (int)Math.round(Math.cos(d9) * d10);
            if (!spots.free(n14, n13 = blockPos.getZ() + (int)Math.round(Math.sin(d9) * d10), d8 - 3.0) && i < 15) continue;
            int n15 = Crater.surface(serverLevel, n14, n13);
            int n16 = (int)Math.ceil(d8) + 2;
            for (n12 = -n16; n12 <= n16; ++n12) {
                for (n11 = -n16; n11 <= n16; ++n11) {
                    BlockPos blockPos4;
                    int n17;
                    double d11 = Math.sqrt(n12 * n12 + n11 * n11);
                    int n18 = n14 + n12;
                    int n19 = n13 + n11;
                    if (footprint.onMeteor(n18, n19)) continue;
                    if (d11 <= d8) {
                        for (n17 = n15; n17 <= n15 + 3; ++n17) {
                            Crater.put(serverLevel, new BlockPos(n18, n17, n19), Crater.s(Blocks.AIR));
                        }
                        Crater.put(serverLevel, new BlockPos(n18, n15 - 1, n19), Crater.s(Blocks.WATER));
                        Crater.put(serverLevel, new BlockPos(n18, n15 - 2, n19), Crater.s(d11 < d8 - 1.5 ? Blocks.WATER : Blocks.CLAY));
                        Crater.put(serverLevel, new BlockPos(n18, n15 - 3, n19), Crater.s(Blocks.CLAY));
                        if (!(randomSource.nextFloat() < 0.16f)) continue;
                        Crater.put(serverLevel, new BlockPos(n18, n15, n19), Crater.s(Blocks.LILY_PAD));
                        continue;
                    }
                    if (d11 <= d8 + 1.5) {
                        n17 = randomSource.nextFloat() < 0.45f ? 1 : 0;
                        Crater.put(serverLevel, new BlockPos(n18, n15 - 1, n19), Crater.s(n17 != 0 ? Blocks.SAND : Blocks.MOSS_BLOCK));
                        for (int j = n15; j <= n15 + 2; ++j) {
                            blockPos4 = new BlockPos(n18, j, n19);
                            if (serverLevel.getBlockState(blockPos4).isAir() || !Crater.free(serverLevel, blockPos4)) continue;
                            Crater.put(serverLevel, blockPos4, Crater.s(Blocks.AIR));
                        }
                        float f = randomSource.nextFloat();
                        blockPos4 = new BlockPos(n18, n15, n19);
                        if (n17 != 0 && f < 0.35f) {
                            int n20 = 1 + randomSource.nextInt(3);
                            for (int j = 0; j < n20; ++j) {
                                Crater.put(serverLevel, new BlockPos(n18, n15 + j, n19), Crater.s(Blocks.SUGAR_CANE));
                            }
                            continue;
                        }
                        if (n17 == 0 && f < 0.3f) {
                            Crater.put(serverLevel, blockPos4, Flora.FERN.get());
                            continue;
                        }
                        if (n17 != 0 || !(f < 0.5f)) continue;
                        Crater.put(serverLevel, blockPos4, Flora.GRASS.get());
                        continue;
                    }
                    if (!(d11 <= d8 + 4.0)) continue;
                    n17 = Crater.surface(serverLevel, n18, n19);
                    BlockPos blockPos5 = new BlockPos(n18, n17 - 1, n19);
                    blockPos4 = new BlockPos(n18, n17, n19);
                    if (!Crater.land(serverLevel, n18, n17, n19) || !Crater.free(serverLevel, blockPos4)) continue;
                    Crater.put(serverLevel, blockPos5, Crater.s(randomSource.nextFloat() < 0.4f ? Blocks.MOSS_BLOCK : Blocks.GRASS_BLOCK));
                    float f = randomSource.nextFloat();
                    if (f < 0.18f) {
                        Crater.put(serverLevel, blockPos4, Flora.SHRUB.get());
                        continue;
                    }
                    if (f < 0.32f) {
                        Crater.put(serverLevel, blockPos4, Flora.BERRY.get());
                        continue;
                    }
                    if (f < 0.55f) {
                        Crater.put(serverLevel, blockPos4, Flora.GRASS.get());
                        continue;
                    }
                    if (f < 0.65f) {
                        Crater.put(serverLevel, blockPos4, Crater.s(Crater.flower(randomSource)));
                        continue;
                    }
                    if (!(f < 0.72f)) continue;
                    Crater.put(serverLevel, blockPos4, Crater.s(Blocks.FLOWERING_AZALEA));
                }
            }
            spots.add(n14, n13, d8 + 1.5);
            n12 = 2;
            n11 = 0;
            for (int j = 0; n11 < n12 && j < n12 * 10; ++j) {
                int n21;
                double d12 = randomSource.nextDouble() * Math.PI * 2.0;
                double d13 = d8 + 3.0 + randomSource.nextDouble() * 4.0;
                int n22 = n14 + (int)Math.round(Math.cos(d12) * d13);
                int n23 = n13 + (int)Math.round(Math.sin(d12) * d13);
                double d14 = 2.5 + d2 * 0.15;
                if (footprint.onMeteor(n22, n23) || !spots.free(n22, n23, d14) || !Crater.land(serverLevel, n22, n21 = Crater.surface(serverLevel, n22, n23), n23)) continue;
                Crater.ancientTree(serverLevel, n22, n21, n23, 5 + randomSource.nextInt(3) + (int)(d2 * 0.4), d14, randomSource);
                spots.add(n22, n23, d14);
                ++n11;
            }
            break;
        }
    }

    private static Block flower(RandomSource randomSource) {
        Block[] blockArray = new Block[]{Blocks.POPPY, Blocks.DANDELION, Blocks.CORNFLOWER, Blocks.AZURE_BLUET, Blocks.ALLIUM, Blocks.OXEYE_DAISY};
        return blockArray[randomSource.nextInt(blockArray.length)];
    }

    private static void treeAt(final ServerLevel serverLevel, int n, int n2, int n3, double d, RandomSource randomSource) {
        AncientTrees.grow(new AncientTrees.Setter(){

            @Override
            public void set(int n, int n2, int n3, BlockState blockState) {
                BlockPos blockPos = new BlockPos(n, n2, n3);
                if (serverLevel.isLoaded(blockPos)) {
                    Crater.put(serverLevel, blockPos, blockState);
                }
            }

            @Override
            public boolean free(int n, int n2, int n3) {
                BlockPos blockPos = new BlockPos(n, n2, n3);
                return serverLevel.isLoaded(blockPos) && Crater.free(serverLevel, blockPos);
            }
        }, randomSource, n, n2, n3, AncientTrees.random(randomSource), d);
    }

    private static void ancientTree(final ServerLevel serverLevel, int n, int n2, int n3, int n4, double d, RandomSource randomSource) {
        AncientTrees.grow(new AncientTrees.Setter(){

            @Override
            public void set(int n, int n2, int n3, BlockState blockState) {
                BlockPos blockPos = new BlockPos(n, n2, n3);
                if (serverLevel.isLoaded(blockPos)) {
                    Crater.put(serverLevel, blockPos, blockState);
                }
            }

            @Override
            public boolean free(int n, int n2, int n3) {
                BlockPos blockPos = new BlockPos(n, n2, n3);
                return serverLevel.isLoaded(blockPos) && Crater.free(serverLevel, blockPos);
            }
        }, randomSource, n, n2, n3, AncientTrees.random(randomSource), Math.max(1.0, Math.min(3.0, (double)(n4 - 5) * 0.5 + d * 0.3)));
    }

    private static void ash(ServerLevel serverLevel, BlockPos blockPos, double d, double d2, RandomSource randomSource, Footprint footprint) {
        if (d2 < 2.0) {
            return;
        }
        int n = (int)Math.ceil(d * 1.6);
        for (int i = -n; i <= n; ++i) {
            for (int j = -n; j <= n; ++j) {
                int n2;
                int n3;
                double d3 = Math.sqrt(i * i + j * j);
                if (d3 > (double)n || (double)randomSource.nextFloat() > 0.7 - d3 / (double)n * 0.5 || footprint.onMeteor(n3 = blockPos.getX() + i, n2 = blockPos.getZ() + j)) continue;
                int n4 = Crater.surface(serverLevel, n3, n2);
                BlockPos blockPos2 = new BlockPos(n3, n4 - 1, n2);
                if (!Crater.land(serverLevel, n3, n4, n2)) continue;
                float f = randomSource.nextFloat();
                Crater.put(serverLevel, blockPos2, Crater.s(f < 0.3f ? Blocks.BASALT : (f < 0.55f ? Blocks.BLACKSTONE : (f < 0.75f ? Blocks.COARSE_DIRT : (f < 0.9f ? Blocks.TUFF : Blocks.GRAVEL)))));
                if (!(f > 0.985f)) continue;
                Crater.put(serverLevel, blockPos2.above(), Crater.s(Blocks.FIRE));
            }
        }
    }

    private Crater() {
    }

    private static final class Spots {
        final List<double[]> list = new ArrayList<double[]>();

        private Spots() {
        }

        boolean free(double d, double d2, double d3) {
            for (double[] dArray : this.list) {
                if (!(Math.hypot(dArray[0] - d, dArray[1] - d2) < dArray[2] + d3 + 2.0)) continue;
                return false;
            }
            return true;
        }

        void add(double d, double d2, double d3) {
            this.list.add(new double[]{d, d2, d3});
        }
    }

    public static interface Footprint {
        public boolean onMeteor(int var1, int var2);
    }
}
