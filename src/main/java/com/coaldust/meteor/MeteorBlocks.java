/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.coaldust.meteor;

import com.coaldust.meteor.MeteorClass;
import com.coaldust.meteor.MeteorShape;
import com.coaldust.meteor.MeteorType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class MeteorBlocks {
    public static final TagKey<Block> COMMON_ORES = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"c", (String)"ores"));
    public static final float MOD_ORE_SHARE = 0.3f;
    public static final float VEIN_SHARE = 0.32f;
    private static final int[][] DIRS = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

    public static List<BlockState> findModOres() {
        ArrayList<BlockState> arrayList = new ArrayList<BlockState>();
        for (Holder holder : BuiltInRegistries.BLOCK.getTagOrEmpty(COMMON_ORES)) {
            Block block = (Block)holder.value();
            ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey((Object)block);
            if (resourceLocation == null || "minecraft".equals(resourceLocation.getNamespace())) continue;
            arrayList.add(block.defaultBlockState());
        }
        return arrayList;
    }

    public static BlockState pick(MeteorType meteorType, MeteorClass.Density density, BlockState blockState, RandomSource randomSource, boolean bl, boolean bl2) {
        if (meteorType == MeteorType.ICE) {
            return MeteorBlocks.ice(density, randomSource);
        }
        if (bl2) {
            if (meteorType == MeteorType.LIVING) {
                return randomSource.nextFloat() < 0.35f ? Blocks.SNIFFER_EGG.defaultBlockState() : Blocks.MOSS_BLOCK.defaultBlockState();
            }
            return Blocks.MAGMA_BLOCK.defaultBlockState();
        }
        if (blockState != null) {
            return blockState;
        }
        if (!bl && density.holeChance > 0.0f && randomSource.nextFloat() < density.holeChance) {
            return Blocks.AIR.defaultBlockState();
        }
        if (meteorType == MeteorType.PLAIN && randomSource.nextFloat() < 0.04f) {
            return Blocks.MAGMA_BLOCK.defaultBlockState();
        }
        if (meteorType == MeteorType.LIVING) {
            return MeteorBlocks.ancient(bl, randomSource);
        }
        return MeteorBlocks.rock(density, randomSource);
    }

    public static long key(int n, int n2, int n3) {
        return (long)(n + 512) << 20 | (long)(n2 + 512) << 10 | (long)(n3 + 512);
    }

    public static Map<Long, BlockState> veins(MeteorType meteorType, MeteorClass.Richness richness, MeteorClass.Density density, MeteorShape meteorShape, List<BlockState> list, RandomSource randomSource) {
        int n;
        int n2;
        Object object;
        Object object2;
        int n3;
        int n4;
        int n5;
        HashMap<Long, BlockState> hashMap = new HashMap<Long, BlockState>();
        if (meteorType.oreChance <= 0.0f) {
            return hashMap;
        }
        int n6 = meteorShape.bound();
        ArrayList<int[]> arrayList = new ArrayList<int[]>();
        ArrayList<int[]> arrayList2 = new ArrayList<int[]>();
        for (n5 = -n6; n5 <= n6; ++n5) {
            for (n4 = -n6; n4 <= n6; ++n4) {
                for (n3 = -n6; n3 <= n6; ++n3) {
                    if (!MeteorBlocks.inside(meteorShape, n5, n4, n3) || n5 == 0 && n4 == 0 && n3 == 0) continue;
                    arrayList.add(new int[]{n5, n4, n3});
                    if (!(meteorShape.norm(n5, n4, n3) >= 0.6)) continue;
                    arrayList2.add(new int[]{n5, n4, n3});
                }
            }
        }
        if (arrayList.isEmpty()) {
            return hashMap;
        }
        if (arrayList2.isEmpty()) {
            arrayList2 = arrayList;
        }
        n5 = Math.max(1, Math.round((float)arrayList.size() * Math.min(0.35f, meteorType.oreChance * 0.32f * richness.oreMultiplier)));
        int n7 = n4 = density == MeteorClass.Density.DENSE ? 1 : 0;
        n3 = arrayList.size() < 60 ? 1 : (arrayList.size() < 220 ? 2 + randomSource.nextInt(2) : 4 + randomSource.nextInt(3));
        int[] nArray = (int[])arrayList.get(0);
        for (int[] nArray2 : arrayList) {
            if (nArray2[0] * nArray2[0] + nArray2[1] * nArray2[1] + nArray2[2] * nArray2[2] >= nArray[0] * nArray[0] + nArray[1] * nArray[1] + nArray[2] * nArray[2]) continue;
            nArray = nArray2;
        }
        if (meteorType.rawBlockChance > 0.0f && randomSource.nextFloat() < meteorType.rawBlockChance * richness.oreMultiplier * 4.0f) {
            hashMap.put(MeteorBlocks.key(nArray[0], nArray[1], nArray[2]), MeteorBlocks.rawBlock(randomSource));
        }
        ArrayList arrayList3 = new ArrayList();
        for (int i = 0; i < n3; ++i) {
            int[] nArray3 = null;
            double d = -1.0;
            for (int j = 0; j < 12; ++j) {
                object2 = (int[])arrayList2.get(randomSource.nextInt(arrayList2.size()));
                double d2 = Double.MAX_VALUE;
                object = arrayList3.iterator();
                while (object.hasNext()) {
                    int[] nArray4 = (int[])object.next();
                    d2 = Math.min(d2, MeteorBlocks.dist2((int[])object2, nArray4));
                }
                if (arrayList3.isEmpty()) {
                    d2 = randomSource.nextDouble();
                }
                if (!(d2 > d)) continue;
                d = d2;
                nArray3 = object2;
            }
            arrayList3.add(nArray3);
        }
        ArrayList<BlockState> arrayList4 = new ArrayList<BlockState>();
        for (n2 = 0; n2 < n3; ++n2) {
            BlockState blockState = MeteorBlocks.oreFor(meteorType, n4 != 0, list, randomSource);
            for (n = 0; n < 6 && !arrayList4.isEmpty() && arrayList4.contains(blockState); ++n) {
                blockState = MeteorBlocks.oreFor(meteorType, n4 != 0, list, randomSource);
            }
            arrayList4.add(blockState);
        }
        n2 = Math.max(1, (int)((float)n5 * 0.75f / (float)n3));
        ArrayList<List<int[]>> arrayList5 = new ArrayList<List<int[]>>();
        for (n = 0; n < n3; ++n) {
            arrayList5.add(MeteorBlocks.walk(hashMap, meteorShape, nArray, (int[])arrayList3.get(n), (BlockState)arrayList4.get(n), Math.min(n5, hashMap.size() + n2), randomSource));
        }
        if (n3 >= 4) {
            for (n = 0; n < n3 && hashMap.size() < n5; ++n) {
                List list2 = (List)arrayList5.get(n);
                object2 = (List)arrayList5.get((n + 1) % n3);
                if (list2.size() < 3 || object2.size() < 3) continue;
                int[] nArray5 = (int[])list2.get(list2.size() * 2 / 3);
                int[] nArray6 = (int[])object2.get(object2.size() * 2 / 3);
                MeteorBlocks.walk(hashMap, meteorShape, nArray5, nArray6, (BlockState)arrayList4.get(n), n5, randomSource);
            }
        }
        n = 0;
        while (hashMap.size() < n5 && n++ < 200) {
            int n8 = randomSource.nextInt(n3);
            object2 = (List)arrayList5.get(n8);
            if (object2.isEmpty()) continue;
            int[] nArray7 = (int[])object2.get(randomSource.nextInt(object2.size()));
            int n9 = 2 + randomSource.nextInt(4);
            object = new int[]{nArray7[0] + randomSource.nextInt(2 * n9 + 1) - n9, nArray7[1] + randomSource.nextInt(2 * n9 + 1) - n9, nArray7[2] + randomSource.nextInt(2 * n9 + 1) - n9};
            MeteorBlocks.walk(hashMap, meteorShape, nArray7, (int[])object, (BlockState)arrayList4.get(n8), n5, randomSource);
        }
        return hashMap;
    }

    private static double dist2(int[] nArray, int[] nArray2) {
        double d = nArray[0] - nArray2[0];
        double d2 = nArray[1] - nArray2[1];
        double d3 = nArray[2] - nArray2[2];
        return d * d + d2 * d2 + d3 * d3;
    }

    private static boolean inside(MeteorShape meteorShape, int n, int n2, int n3) {
        return meteorShape.norm(n, n2, n3) <= 1.0 && !meteorShape.isCavity(n, n2, n3);
    }

    private static List<int[]> walk(Map<Long, BlockState> map, MeteorShape meteorShape, int[] nArray, int[] nArray2, BlockState blockState, int n, RandomSource randomSource) {
        ArrayList<int[]> arrayList = new ArrayList<int[]>();
        int n2 = nArray[0];
        int n3 = nArray[1];
        int n4 = nArray[2];
        for (int i = 0; i < 120 && map.size() < n; ++i) {
            int n5;
            int[] nArray3;
            if (MeteorBlocks.inside(meteorShape, n2, n3, n4)) {
                int[] nArray4;
                map.putIfAbsent(MeteorBlocks.key(n2, n3, n4), blockState);
                arrayList.add(new int[]{n2, n3, n4});
                if (randomSource.nextFloat() < 0.3f && MeteorBlocks.inside(meteorShape, n2 + (nArray4 = DIRS[randomSource.nextInt(6)])[0], n3 + nArray4[1], n4 + nArray4[2])) {
                    map.putIfAbsent(MeteorBlocks.key(n2 + nArray4[0], n3 + nArray4[1], n4 + nArray4[2]), blockState);
                }
            }
            int n6 = nArray2[0] - n2;
            int n7 = nArray2[1] - n3;
            int n8 = nArray2[2] - n4;
            if (n6 == 0 && n7 == 0 && n8 == 0) break;
            if (randomSource.nextFloat() < 0.25f) {
                nArray3 = DIRS[randomSource.nextInt(6)];
            } else {
                int[] nArray5;
                int n9;
                int n10;
                n5 = Math.abs(n6);
                int n11 = randomSource.nextInt(n5 + (n10 = Math.abs(n7)) + (n9 = Math.abs(n8)));
                if (n11 < n5) {
                    int[] nArray6 = new int[3];
                    nArray6[0] = Integer.signum(n6);
                    nArray6[1] = 0;
                    nArray5 = nArray6;
                    nArray6[2] = 0;
                } else if (n11 < n5 + n10) {
                    int[] nArray7 = new int[3];
                    nArray7[0] = 0;
                    nArray7[1] = Integer.signum(n7);
                    nArray5 = nArray7;
                    nArray7[2] = 0;
                } else {
                    int[] nArray8 = new int[3];
                    nArray8[0] = 0;
                    nArray8[1] = 0;
                    nArray5 = nArray8;
                    nArray8[2] = Integer.signum(n8);
                }
                nArray3 = nArray5;
            }
            for (n5 = 0; n5 < 8 && !MeteorBlocks.inside(meteorShape, n2 + nArray3[0], n3 + nArray3[1], n4 + nArray3[2]); ++n5) {
                nArray3 = DIRS[randomSource.nextInt(6)];
            }
            if (!MeteorBlocks.inside(meteorShape, n2 + nArray3[0], n3 + nArray3[1], n4 + nArray3[2])) break;
            n2 += nArray3[0];
            n3 += nArray3[1];
            n4 += nArray3[2];
        }
        return arrayList;
    }

    private static BlockState oreFor(MeteorType meteorType, boolean bl, List<BlockState> list, RandomSource randomSource) {
        if (!list.isEmpty() && randomSource.nextFloat() < 0.3f) {
            return list.get(randomSource.nextInt(list.size()));
        }
        return MeteorBlocks.ore(meteorType, bl, randomSource);
    }

    private static BlockState rock(MeteorClass.Density density, RandomSource randomSource) {
        float f = randomSource.nextFloat();
        return switch (density) {
            default -> throw new MatchException(null, null);
            case MeteorClass.Density.POROUS -> {
                if (f < 0.4f) {
                    yield Blocks.TUFF.defaultBlockState();
                }
                if (f < 0.75f) {
                    yield Blocks.BASALT.defaultBlockState();
                }
                if (f < 0.92f) {
                    yield Blocks.SMOOTH_BASALT.defaultBlockState();
                }
                yield Blocks.BLACKSTONE.defaultBlockState();
            }
            case MeteorClass.Density.NORMAL -> {
                if (f < 0.45f) {
                    yield Blocks.BLACKSTONE.defaultBlockState();
                }
                if (f < 0.75f) {
                    yield Blocks.BASALT.defaultBlockState();
                }
                if (f < 0.92f) {
                    yield Blocks.SMOOTH_BASALT.defaultBlockState();
                }
                yield Blocks.OBSIDIAN.defaultBlockState();
            }
            case MeteorClass.Density.DENSE -> f < 0.4f ? Blocks.DEEPSLATE.defaultBlockState() : (f < 0.7f ? Blocks.OBSIDIAN.defaultBlockState() : (f < 0.9f ? Blocks.POLISHED_BLACKSTONE.defaultBlockState() : (f < 0.96f ? Blocks.BLACKSTONE.defaultBlockState() : Blocks.CRYING_OBSIDIAN.defaultBlockState())));
        };
    }

    private static BlockState ancient(boolean bl, RandomSource randomSource) {
        float f = randomSource.nextFloat();
        if (bl) {
            return f < 0.35f ? Blocks.MOSS_BLOCK.defaultBlockState() : (f < 0.6f ? Blocks.MOSSY_STONE_BRICKS.defaultBlockState() : (f < 0.8f ? Blocks.MOSSY_COBBLESTONE.defaultBlockState() : (f < 0.92f ? Blocks.CRACKED_STONE_BRICKS.defaultBlockState() : Blocks.CHISELED_STONE_BRICKS.defaultBlockState())));
        }
        return f < 0.3f ? Blocks.ROOTED_DIRT.defaultBlockState() : (f < 0.55f ? Blocks.MOSS_BLOCK.defaultBlockState() : (f < 0.8f ? Blocks.MOSSY_STONE_BRICKS.defaultBlockState() : Blocks.CRACKED_STONE_BRICKS.defaultBlockState()));
    }

    public static BlockState plant(RandomSource randomSource) {
        float f = randomSource.nextFloat();
        if (f < 0.3f) {
            return Blocks.MOSS_CARPET.defaultBlockState();
        }
        if (f < 0.5f) {
            return Blocks.SHORT_GRASS.defaultBlockState();
        }
        if (f < 0.65f) {
            return Blocks.FERN.defaultBlockState();
        }
        if (f < 0.75f) {
            return Blocks.AZALEA.defaultBlockState();
        }
        if (f < 0.82f) {
            return Blocks.FLOWERING_AZALEA.defaultBlockState();
        }
        if (f < 0.9f) {
            return Blocks.TORCHFLOWER.defaultBlockState();
        }
        return null;
    }

    private static BlockState ice(MeteorClass.Density density, RandomSource randomSource) {
        float f = randomSource.nextFloat();
        return switch (density) {
            default -> throw new MatchException(null, null);
            case MeteorClass.Density.POROUS -> {
                if (f < 0.45f) {
                    yield Blocks.ICE.defaultBlockState();
                }
                if (f < 0.7f) {
                    yield Blocks.PACKED_ICE.defaultBlockState();
                }
                yield MeteorBlocks.rock(density, randomSource);
            }
            case MeteorClass.Density.NORMAL -> {
                if (f < 0.4f) {
                    yield Blocks.PACKED_ICE.defaultBlockState();
                }
                if (f < 0.62f) {
                    yield Blocks.ICE.defaultBlockState();
                }
                if (f < 0.75f) {
                    yield Blocks.BLUE_ICE.defaultBlockState();
                }
                yield MeteorBlocks.rock(density, randomSource);
            }
            case MeteorClass.Density.DENSE -> f < 0.35f ? Blocks.BLUE_ICE.defaultBlockState() : (f < 0.75f ? Blocks.PACKED_ICE.defaultBlockState() : MeteorBlocks.rock(density, randomSource));
        };
    }

    private static BlockState ore(MeteorType meteorType, boolean bl, RandomSource randomSource) {
        int n = randomSource.nextInt(100);
        if (meteorType.hasGems()) {
            if (n < 7) {
                return (bl ? Blocks.DEEPSLATE_DIAMOND_ORE : Blocks.DIAMOND_ORE).defaultBlockState();
            }
            if (n < 12) {
                return (bl ? Blocks.DEEPSLATE_EMERALD_ORE : Blocks.EMERALD_ORE).defaultBlockState();
            }
        }
        if (meteorType.hasRedstone() && n < 30) {
            return (bl ? Blocks.DEEPSLATE_REDSTONE_ORE : Blocks.REDSTONE_ORE).defaultBlockState();
        }
        n = randomSource.nextInt(100);
        if (meteorType == MeteorType.PLAIN) {
            return (n < 50 ? (bl ? Blocks.DEEPSLATE_IRON_ORE : Blocks.IRON_ORE) : (bl ? Blocks.DEEPSLATE_COPPER_ORE : Blocks.COPPER_ORE)).defaultBlockState();
        }
        if (n < 42) {
            return (bl ? Blocks.DEEPSLATE_COPPER_ORE : Blocks.COPPER_ORE).defaultBlockState();
        }
        if (n < 80) {
            return (bl ? Blocks.DEEPSLATE_IRON_ORE : Blocks.IRON_ORE).defaultBlockState();
        }
        if (n < 92) {
            return (bl ? Blocks.DEEPSLATE_LAPIS_ORE : Blocks.LAPIS_ORE).defaultBlockState();
        }
        return (bl ? Blocks.DEEPSLATE_GOLD_ORE : Blocks.GOLD_ORE).defaultBlockState();
    }

    private static BlockState rawBlock(RandomSource randomSource) {
        int n = randomSource.nextInt(100);
        if (n < 45) {
            return Blocks.RAW_IRON_BLOCK.defaultBlockState();
        }
        if (n < 85) {
            return Blocks.RAW_COPPER_BLOCK.defaultBlockState();
        }
        return Blocks.RAW_GOLD_BLOCK.defaultBlockState();
    }

    private MeteorBlocks() {
    }
}
