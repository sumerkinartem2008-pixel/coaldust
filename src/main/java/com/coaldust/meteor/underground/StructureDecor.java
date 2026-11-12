/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.coaldust.meteor.underground;

import com.coaldust.meteor.underground.UndergroundCity;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public final class StructureDecor {
    public static final float KEEP_PRECIOUS = 0.06f;
    private static final int[][] N6 = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

    private static BlockState s(Block block) {
        return block.defaultBlockState();
    }

    public static void cheapen(UndergroundCity.Plan plan, RandomSource randomSource) {
        for (Map.Entry<Long, BlockState> entry : plan.blocks.entrySet()) {
            BlockState blockState = entry.getValue();
            BlockState blockState2 = StructureDecor.cheap(blockState, randomSource);
            if (blockState2 == null) continue;
            entry.setValue(blockState2);
        }
    }

    private static BlockState cheap(BlockState blockState, RandomSource randomSource) {
        boolean bl;
        boolean bl2 = bl = randomSource.nextFloat() < 0.06f;
        if (blockState.is(Blocks.GOLD_BLOCK)) {
            return bl ? null : StructureDecor.s(randomSource.nextFloat() < 0.6f ? Blocks.YELLOW_TERRACOTTA : Blocks.WAXED_CUT_COPPER);
        }
        if (blockState.is(Blocks.EMERALD_BLOCK)) {
            return bl ? null : StructureDecor.s(randomSource.nextFloat() < 0.5f ? Blocks.WAXED_OXIDIZED_CUT_COPPER : Blocks.PRISMARINE_BRICKS);
        }
        if (blockState.is(Blocks.DIAMOND_BLOCK)) {
            return bl ? null : StructureDecor.s(Blocks.CALCITE);
        }
        if (blockState.is(Blocks.IRON_BLOCK)) {
            return bl ? null : StructureDecor.s(randomSource.nextFloat() < 0.5f ? Blocks.SMOOTH_STONE : Blocks.POLISHED_ANDESITE);
        }
        if (blockState.is(Blocks.LAPIS_BLOCK)) {
            return bl ? null : StructureDecor.s(Blocks.BLUE_TERRACOTTA);
        }
        if (blockState.is(Blocks.RAW_GOLD_BLOCK)) {
            return randomSource.nextFloat() < 0.15f ? null : StructureDecor.s(Blocks.DEEPSLATE_GOLD_ORE);
        }
        if (blockState.is(Blocks.RAW_IRON_BLOCK)) {
            return randomSource.nextFloat() < 0.15f ? null : StructureDecor.s(Blocks.DEEPSLATE_IRON_ORE);
        }
        if (blockState.is(Blocks.RAW_COPPER_BLOCK)) {
            return randomSource.nextFloat() < 0.25f ? null : StructureDecor.s(Blocks.DEEPSLATE_COPPER_ORE);
        }
        if (blockState.is(Blocks.GILDED_BLACKSTONE)) {
            return randomSource.nextFloat() < 0.25f ? null : StructureDecor.s(Blocks.POLISHED_BLACKSTONE);
        }
        if (blockState.is(Blocks.DIAMOND_ORE) || blockState.is(Blocks.DEEPSLATE_DIAMOND_ORE) || blockState.is(Blocks.EMERALD_ORE) || blockState.is(Blocks.DEEPSLATE_EMERALD_ORE)) {
            return randomSource.nextFloat() < 0.3f ? null : StructureDecor.s(Blocks.DEEPSLATE);
        }
        return null;
    }

    public static Theme rollTheme(RandomSource randomSource) {
        int n = randomSource.nextInt(100);
        return n < 30 ? Theme.STONE : (n < 52 ? Theme.BLACKSTONE : (n < 72 ? Theme.TUFF : (n < 87 ? Theme.SANDSTONE : Theme.MUD)));
    }

    public static void theme(UndergroundCity.Plan plan, Theme theme) {
        HashMap<Block, Block> hashMap = new HashMap<Block, Block>();
        switch (theme.ordinal()) {
            case 0: {
                return;
            }
            case 1: {
                hashMap.put(Blocks.STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
                hashMap.put(Blocks.MOSSY_STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
                hashMap.put(Blocks.CRACKED_STONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
                hashMap.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE);
                hashMap.put(Blocks.STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
                hashMap.put(Blocks.STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
                hashMap.put(Blocks.STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
                hashMap.put(Blocks.POLISHED_ANDESITE, Blocks.POLISHED_BASALT);
                hashMap.put(Blocks.LANTERN, Blocks.SOUL_LANTERN);
                break;
            }
            case 2: {
                hashMap.put(Blocks.STONE_BRICKS, Blocks.TUFF_BRICKS);
                hashMap.put(Blocks.MOSSY_STONE_BRICKS, Blocks.TUFF_BRICKS);
                hashMap.put(Blocks.CRACKED_STONE_BRICKS, Blocks.POLISHED_TUFF);
                hashMap.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_TUFF_BRICKS);
                hashMap.put(Blocks.STONE_BRICK_STAIRS, Blocks.TUFF_BRICK_STAIRS);
                hashMap.put(Blocks.STONE_BRICK_SLAB, Blocks.TUFF_BRICK_SLAB);
                hashMap.put(Blocks.STONE_BRICK_WALL, Blocks.TUFF_BRICK_WALL);
                hashMap.put(Blocks.POLISHED_ANDESITE, Blocks.POLISHED_TUFF);
                break;
            }
            case 3: {
                hashMap.put(Blocks.STONE_BRICKS, Blocks.CUT_SANDSTONE);
                hashMap.put(Blocks.MOSSY_STONE_BRICKS, Blocks.SANDSTONE);
                hashMap.put(Blocks.CRACKED_STONE_BRICKS, Blocks.SANDSTONE);
                hashMap.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_SANDSTONE);
                hashMap.put(Blocks.STONE_BRICK_STAIRS, Blocks.SMOOTH_SANDSTONE_STAIRS);
                hashMap.put(Blocks.STONE_BRICK_SLAB, Blocks.CUT_SANDSTONE_SLAB);
                hashMap.put(Blocks.STONE_BRICK_WALL, Blocks.SANDSTONE_WALL);
                hashMap.put(Blocks.POLISHED_ANDESITE, Blocks.SMOOTH_SANDSTONE);
                hashMap.put(Blocks.TUFF, Blocks.SANDSTONE);
                break;
            }
            case 4: {
                hashMap.put(Blocks.STONE_BRICKS, Blocks.MUD_BRICKS);
                hashMap.put(Blocks.MOSSY_STONE_BRICKS, Blocks.MUD_BRICKS);
                hashMap.put(Blocks.CRACKED_STONE_BRICKS, Blocks.PACKED_MUD);
                hashMap.put(Blocks.CHISELED_STONE_BRICKS, Blocks.PACKED_MUD);
                hashMap.put(Blocks.STONE_BRICK_STAIRS, Blocks.MUD_BRICK_STAIRS);
                hashMap.put(Blocks.STONE_BRICK_SLAB, Blocks.MUD_BRICK_SLAB);
                hashMap.put(Blocks.STONE_BRICK_WALL, Blocks.MUD_BRICK_WALL);
                hashMap.put(Blocks.POLISHED_ANDESITE, Blocks.PACKED_MUD);
                hashMap.put(Blocks.TUFF, Blocks.DRIPSTONE_BLOCK);
            }
        }
        for (Map.Entry<Long, BlockState> entry : plan.blocks.entrySet()) {
            Block block = (Block)hashMap.get(entry.getValue().getBlock());
            if (block == null) continue;
            entry.setValue(block.withPropertiesOf(entry.getValue()));
        }
    }

    private static boolean air(UndergroundCity.Plan plan, int n, int n2, int n3) {
        BlockState blockState = plan.blocks.get(UndergroundCity.Plan.key(n, n2, n3));
        return blockState != null && blockState.isAir();
    }

    private static boolean solid(UndergroundCity.Plan plan, int n, int n2, int n3) {
        BlockState blockState = plan.blocks.get(UndergroundCity.Plan.key(n, n2, n3));
        return blockState != null && !blockState.isAir() && !blockState.is(Blocks.WATER) && !blockState.is(Blocks.LAVA);
    }

    /*
     * WARNING - void declaration
     */
    public static void ruin(UndergroundCity.Plan plan, RandomSource randomSource) {
        void var4_7;
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<Long, BlockState> object : plan.blocks.entrySet()) {
            long l = object.getKey();
            n5 = UndergroundCity.Plan.kx(l);
            n4 = UndergroundCity.Plan.ky(l);
            n3 = UndergroundCity.Plan.kz(l);
            BlockState blockState = object.getValue();
            if (blockState.isAir()) {
                n2 = StructureDecor.solid(plan, n5, n4 + 1, n3);
                n = StructureDecor.solid(plan, n5, n4 - 1, n3);
                float f = randomSource.nextFloat();
                if (n2 != 0 && n == 0 && f < 0.05f) {
                    object.setValue(StructureDecor.s(Blocks.COBWEB));
                    continue;
                }
                if (n != 0 && f < 0.015f) {
                    object.setValue(StructureDecor.s(randomSource.nextFloat() < 0.6f ? Blocks.GRAVEL : Blocks.COBBLED_DEEPSLATE));
                    continue;
                }
                if (n == 0 || !(f < 0.02f)) continue;
                object.setValue(StructureDecor.s(Blocks.COBWEB));
                continue;
            }
            if (blockState.is(Blocks.LANTERN) || blockState.is(Blocks.SOUL_LANTERN) || blockState.is(Blocks.CANDLE)) {
                if (!(randomSource.nextFloat() < 0.55f)) continue;
                object.setValue(StructureDecor.s(Blocks.AIR));
                continue;
            }
            if (blockState.is(Blocks.STONE_BRICKS) && randomSource.nextFloat() < 0.35f) {
                object.setValue(StructureDecor.s(randomSource.nextFloat() < 0.6f ? Blocks.CRACKED_STONE_BRICKS : Blocks.MOSSY_STONE_BRICKS));
                continue;
            }
            if (blockState.is(Blocks.DEEPSLATE_BRICKS) && randomSource.nextFloat() < 0.35f) {
                object.setValue(StructureDecor.s(Blocks.CRACKED_DEEPSLATE_BRICKS));
                continue;
            }
            if (!blockState.is(Blocks.DEEPSLATE_TILES) || !(randomSource.nextFloat() < 0.25f)) continue;
            object.setValue(StructureDecor.s(Blocks.CRACKED_DEEPSLATE_TILES));
        }
        ArrayList arrayList2 = new ArrayList();
        for (Map.Entry<Long, BlockState> entry : plan.blocks.entrySet()) {
            if (!entry.getValue().isAir()) continue;
            arrayList2.add(entry.getKey());
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        boolean bl = false;
        while (var4_7 < 7) {
            long l = (Long)arrayList2.get(randomSource.nextInt(arrayList2.size()));
            n5 = UndergroundCity.Plan.kx(l);
            n4 = UndergroundCity.Plan.ky(l);
            n3 = UndergroundCity.Plan.kz(l);
            while (StructureDecor.air(plan, n5, n4 - 1, n3)) {
                --n4;
            }
            int n6 = 1 + randomSource.nextInt(2);
            for (n2 = -n6; n2 <= n6; ++n2) {
                for (n = -n6; n <= n6; ++n) {
                    int n7 = n6 + 1 - Math.max(Math.abs(n2), Math.abs(n));
                    for (int i = 0; i < n7; ++i) {
                        if ((!StructureDecor.air(plan, n5 + n2, n4 + i, n3 + n) || !StructureDecor.solid(plan, n5 + n2, n4 + i - 1, n3 + n)) && (i <= 0 || !StructureDecor.air(plan, n5 + n2, n4 + i, n3 + n))) continue;
                        plan.blocks.put(UndergroundCity.Plan.key(n5 + n2, n4 + i, n3 + n), StructureDecor.s(randomSource.nextFloat() < 0.5f ? Blocks.COBBLESTONE : (randomSource.nextFloat() < 0.6f ? Blocks.GRAVEL : Blocks.COBBLED_DEEPSLATE)));
                    }
                }
            }
            ++var4_7;
        }
    }

    public static void flood(UndergroundCity.Plan plan, int n) {
        for (Map.Entry<Long, BlockState> entry : plan.blocks.entrySet()) {
            int n2;
            int n3;
            long l = entry.getKey();
            if (UndergroundCity.Plan.ky(l) != n || !entry.getValue().isAir() || (n3 = UndergroundCity.Plan.kx(l)) * n3 + (n2 = UndergroundCity.Plan.kz(l)) * n2 <= 13) continue;
            entry.setValue(StructureDecor.s(Blocks.WATER));
        }
    }

    public static int unfloat(UndergroundCity.Plan plan, int n) {
        int n2;
        int n3;
        Object object;
        HashSet<Long> hashSet = new HashSet<Long>();
        ArrayList<Object> arrayList = new ArrayList<Object>();
        int[][] nArrayArray = new int[18][];
        int n4 = 0;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    int n5 = Math.abs(i) + Math.abs(j) + Math.abs(k);
                    if (n5 != 1 && n5 != 2) continue;
                    nArrayArray[n4++] = new int[]{i, j, k};
                }
            }
        }
        ArrayDeque<Long> arrayDeque = new ArrayDeque<Long>();
        for (Map.Entry<Long, BlockState> entry : plan.blocks.entrySet()) {
            long l = entry.getKey();
            if (entry.getValue().isAir() || hashSet.contains(l)) continue;
            object = new ArrayList();
            hashSet.add(l);
            arrayDeque.add(l);
            boolean bl = false;
            while (!arrayDeque.isEmpty()) {
                long l2 = (Long)arrayDeque.poll();
                if (!bl) {
                    ((ArrayList)object).add(l2);
                    if (((ArrayList)object).size() > n) {
                        bl = true;
                    }
                }
                n3 = UndergroundCity.Plan.kx(l2);
                n2 = UndergroundCity.Plan.ky(l2);
                int n6 = UndergroundCity.Plan.kz(l2);
                for (int[] nArray : nArrayArray) {
                    long l3 = UndergroundCity.Plan.key(n3 + nArray[0], n2 + nArray[1], n6 + nArray[2]);
                    BlockState blockState = plan.blocks.get(l3);
                    if (blockState == null || blockState.isAir() || !hashSet.add(l3)) continue;
                    arrayDeque.add(l3);
                }
            }
            if (bl) continue;
            arrayList.add(object);
        }
        int n7 = 0;
        BlockState blockState = StructureDecor.s(Blocks.AIR);
        for (Map.Entry<Long, BlockState> entry : plan.blocks.entrySet()) {
            int n5;
            object = entry.getValue();
            if (!object.is(Blocks.LANTERN) && !object.is(Blocks.SOUL_LANTERN) || !object.hasProperty((Property)BlockStateProperties.HANGING)) continue;
            long l = entry.getKey();
            int n6 = UndergroundCity.Plan.kx(l);
            n3 = UndergroundCity.Plan.ky(l);
            n2 = UndergroundCity.Plan.kz(l);
            BlockState blockState2 = plan.blocks.get(UndergroundCity.Plan.key(n6, n3 + 1, n2));
            BlockState blockState3 = plan.blocks.get(UndergroundCity.Plan.key(n6, n3 - 1, n2));
            int n8 = blockState2 != null && !blockState2.isAir() ? 1 : 0;
            int n9 = n5 = blockState3 != null && !blockState3.isAir() ? 1 : 0;
            if (((Boolean)object.getValue((Property)BlockStateProperties.HANGING)).booleanValue() && n8 == 0) {
                entry.setValue(n5 != 0 ? (BlockState)object.setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(false)) : blockState);
                continue;
            }
            if (((Boolean)object.getValue((Property)BlockStateProperties.HANGING)).booleanValue() || n5 != 0) continue;
            entry.setValue(n8 != 0 ? (BlockState)object.setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(true)) : blockState);
        }
        for (ArrayList arrayList2 : arrayList) {
            object = arrayList2.iterator();
            while (object.hasNext()) {
                long l = (Long)object.next();
                if (plan.blocks.get(l).is(Blocks.CHEST)) continue;
                plan.blocks.put(l, blockState);
                ++n7;
            }
        }
        return n7;
    }

    public static void transform(UndergroundCity.Plan plan, int n) {
        if (n == 0) {
            return;
        }
        HashMap<Long, BlockState> hashMap = new HashMap<Long, BlockState>(plan.blocks.size() * 2);
        for (Map.Entry<Long, BlockState> object : plan.blocks.entrySet()) {
            long l = object.getKey();
            int[] nArray = StructureDecor.tr(UndergroundCity.Plan.kx(l), UndergroundCity.Plan.kz(l), n);
            hashMap.put(UndergroundCity.Plan.key(nArray[0], UndergroundCity.Plan.ky(l), nArray[1]), StructureDecor.face(object.getValue(), n));
        }
        plan.blocks.clear();
        plan.blocks.putAll(hashMap);
        for (int[] nArray : plan.chests) {
            int[] nArray2 = StructureDecor.tr(nArray[0], nArray[2], n);
            nArray[0] = nArray2[0];
            nArray[2] = nArray2[1];
        }
    }

    private static int[] tr(int n, int n2, int n3) {
        if ((n3 & 1) != 0) {
            n = -n;
        }
        if ((n3 & 2) != 0) {
            n2 = -n2;
        }
        if ((n3 & 4) != 0) {
            int n4 = n;
            n = n2;
            n2 = n4;
        }
        return new int[]{n, n2};
    }

    private static BlockState face(BlockState blockState, int n) {
        if (!blockState.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING)) {
            return blockState;
        }
        Direction direction = (Direction)blockState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        if (direction == null) {
            return blockState;
        }
        if ((n & 1) != 0) {
            Direction direction2 = direction == Direction.EAST ? Direction.WEST : (direction = direction == Direction.WEST ? Direction.EAST : direction);
        }
        if ((n & 2) != 0) {
            Direction direction3 = direction == Direction.NORTH ? Direction.SOUTH : (direction = direction == Direction.SOUTH ? Direction.NORTH : direction);
        }
        if ((n & 4) != 0) {
            direction = switch (direction) {
                case Direction.EAST -> Direction.SOUTH;
                case Direction.SOUTH -> Direction.EAST;
                case Direction.WEST -> Direction.NORTH;
                case Direction.NORTH -> Direction.WEST;
                default -> direction;
            };
        }
        return (BlockState)blockState.setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction);
    }

    private StructureDecor() {
    }

    public static enum Theme {
        STONE("stone"),
        BLACKSTONE("blackstone"),
        TUFF("tuff"),
        SANDSTONE("sandstone"),
        MUD("mud");

        public final String id;

        private Theme(String string2) {
            this.id = string2;
        }
    }
}
