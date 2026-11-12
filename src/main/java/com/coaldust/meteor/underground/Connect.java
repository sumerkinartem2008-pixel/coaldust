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
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.coaldust.meteor.underground;

import com.coaldust.meteor.underground.UndergroundCity;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;

public final class Connect {
    private final UndergroundCity.Plan p;
    private final RandomSource r;
    private final Block floor;
    private final Block stairs;
    private final Map<Long, Integer> comp = new HashMap<Long, Integer>();
    private boolean skipRoofs;
    public static String last = "";
    private static final int[][] H = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private Connect(UndergroundCity.Plan plan, RandomSource randomSource, Block block, Block block2) {
        this.p = plan;
        this.r = randomSource;
        this.floor = block;
        this.stairs = block2;
    }

    public static int run(UndergroundCity.Plan plan, RandomSource randomSource, Block block, Block block2, int n) {
        return Connect.run(plan, randomSource, block, block2, n, false);
    }

    public static int run(UndergroundCity.Plan plan, RandomSource randomSource, Block block, Block block2, int n, boolean bl) {
        Connect connect = new Connect(plan, randomSource, block, block2);
        connect.skipRoofs = bl;
        int n2 = connect.bigComps(n);
        int n3 = 0;
        for (int i = 0; i < 6; ++i) {
            int n4 = connect.connectOnce(n);
            n3 += n4;
            if (n4 == 0) break;
        }
        last = "\u043e\u0431\u043b\u0430\u0441\u0442\u0435\u0439 " + n2 + " -> " + connect.bigComps(n) + ", \u043f\u0440\u043e\u0445\u043e\u0434\u043e\u0432 " + n3;
        return n3;
    }

    private int bigComps(int n) {
        int n2 = 0;
        for (List<int[]> list : this.components()) {
            if (list.size() < n) continue;
            ++n2;
        }
        return n2;
    }

    private static long key(int n, int n2, int n3) {
        return UndergroundCity.Plan.key(n, n2, n3);
    }

    private BlockState at(int n, int n2, int n3) {
        return this.p.blocks.get(Connect.key(n, n2, n3));
    }

    private static boolean passableState(BlockState blockState) {
        if (blockState == null) {
            return false;
        }
        if (blockState.isAir()) {
            return true;
        }
        return blockState.is(Blocks.COBWEB) || blockState.is(Blocks.RED_CARPET) || blockState.is(Blocks.BLACK_CARPET) || blockState.is(Blocks.MOSS_CARPET) || blockState.is(Blocks.RAIL) || blockState.is(Blocks.CANDLE) || blockState.is(Blocks.WITHER_ROSE) || blockState.is(Blocks.DEAD_BUSH) || blockState.is(Blocks.RED_MUSHROOM) || blockState.is(Blocks.BROWN_MUSHROOM) || blockState.is(Blocks.SHORT_GRASS) || blockState.is(Blocks.FERN) || blockState.is(Blocks.SNOW) || blockState.is(Blocks.DARK_OAK_PRESSURE_PLATE) || blockState.is(Blocks.LADDER);
    }

    private static boolean floorState(BlockState blockState) {
        if (blockState == null || blockState.isAir()) {
            return false;
        }
        return !blockState.is(Blocks.WATER) && !blockState.is(Blocks.LAVA) && !blockState.is(Blocks.FIRE) && !blockState.is(Blocks.SOUL_FIRE) && !blockState.is(Blocks.COBWEB) && !blockState.is(Blocks.CHAIN) && !blockState.is(Blocks.LANTERN) && !blockState.is(Blocks.SOUL_LANTERN) && !blockState.is(Blocks.CAMPFIRE) && !blockState.is(Blocks.SOUL_CAMPFIRE) && !Connect.passableState(blockState);
    }

    private boolean pass(int n, int n2, int n3) {
        return Connect.passableState(this.at(n, n2, n3));
    }

    private boolean walk(int n, int n2, int n3) {
        BlockState blockState = this.at(n, n2 - 1, n3);
        return this.pass(n, n2, n3) && this.pass(n, n2 + 1, n3) && (Connect.floorState(blockState) || this.at(n, n2, n3) != null && this.at(n, n2, n3).is(Blocks.LADDER));
    }

    private void neighbours(int n, int n2, int n3, List<long[]> list) {
        list.clear();
        block0: for (int[] nArray : H) {
            int n4 = n + nArray[0];
            int n5 = n3 + nArray[1];
            if (this.walk(n4, n2, n5)) {
                list.add(new long[]{n4, n2, n5});
                continue;
            }
            if (this.pass(n, n2 + 2, n3) && this.walk(n4, n2 + 1, n5)) {
                list.add(new long[]{n4, n2 + 1, n5});
                continue;
            }
            if (!this.pass(n4, n2, n5) || !this.pass(n4, n2 + 1, n5)) continue;
            for (int i = 1; i <= 3; ++i) {
                if (this.walk(n4, n2 - i, n5)) {
                    list.add(new long[]{n4, n2 - i, n5});
                    continue block0;
                }
                if (!this.pass(n4, n2 - i, n5)) continue block0;
            }
        }
        BlockState blockState = this.at(n, n2, n3);
        if (blockState != null && blockState.is(Blocks.LADDER)) {
            if (this.walk(n, n2 + 1, n3) || this.pass(n, n2 + 1, n3) && this.at(n, n2 + 1, n3) != null && this.at(n, n2 + 1, n3).is(Blocks.LADDER)) {
                list.add(new long[]{n, n2 + 1, n3});
            }
            if (this.at(n, n2 - 1, n3) != null && this.at(n, n2 - 1, n3).is(Blocks.LADDER)) {
                list.add(new long[]{n, n2 - 1, n3});
            }
        }
    }

    private List<List<int[]>> components() {
        this.comp.clear();
        ArrayList<List<int[]>> arrayList = new ArrayList<List<int[]>>();
        ArrayList<long[]> arrayList2 = new ArrayList<long[]>();
        ArrayList<Long> arrayList3 = new ArrayList<Long>(this.p.blocks.keySet());
        Iterator iterator = arrayList3.iterator();
        while (iterator.hasNext()) {
            long l = (Long)iterator.next();
            int n = UndergroundCity.Plan.kx(l);
            int n2 = UndergroundCity.Plan.ky(l);
            int n3 = UndergroundCity.Plan.kz(l);
            if (this.comp.containsKey(l) || !this.walk(n, n2, n3)) continue;
            int n4 = arrayList.size();
            ArrayList<int[]> arrayList4 = new ArrayList<int[]>();
            ArrayDeque<int[]> arrayDeque = new ArrayDeque<int[]>();
            arrayDeque.add(new int[]{n, n2, n3});
            this.comp.put(l, n4);
            while (!arrayDeque.isEmpty()) {
                int[] nArray = (int[])arrayDeque.poll();
                arrayList4.add(nArray);
                this.neighbours(nArray[0], nArray[1], nArray[2], arrayList2);
                for (long[] lArray : arrayList2) {
                    long l2 = Connect.key((int)lArray[0], (int)lArray[1], (int)lArray[2]);
                    if (this.comp.containsKey(l2)) continue;
                    this.comp.put(l2, n4);
                    arrayDeque.add(new int[]{(int)lArray[0], (int)lArray[1], (int)lArray[2]});
                }
            }
            arrayList.add(arrayList4);
        }
        return arrayList;
    }

    private int connectOnce(int n3) {
        int n4;
        List<List<int[]>> list = this.components();
        if (list.size() < 2) {
            return 0;
        }
        int n5 = 0;
        for (int i = 1; i < list.size(); ++i) {
            if (list.get(i).size() <= list.get(n5).size()) continue;
            n5 = i;
        }
        List<int[]> list2 = list.get(n5);
        ArrayList<int[]> arrayList = new ArrayList<int[]>();
        int n6 = Math.max(1, list2.size() / 4000);
        for (n4 = 0; n4 < list2.size(); n4 += n6) {
            arrayList.add(list2.get(n4));
        }
        n4 = 0;
        List<Integer> list3 = new ArrayList();
        for (int i = 0; i < list.size(); ++i) {
            if (i == n5 || list.get(i).size() < n3 || this.skipRoofs && !this.roomy(list.get(i), i)) continue;
            list3.add(i);
        }
        list3.sort((n, n2) -> ((List)list.get((int)n2)).size() - ((List)list.get((int)n)).size());
        if (list3.size() > 60) {
            list3 = list3.subList(0, 60);
        }
        Iterator iterator = list3.iterator();
        while (iterator.hasNext()) {
            int n7 = (Integer)iterator.next();
            List<int[]> list4 = list.get(n7);
            int n8 = Math.max(1, list4.size() / 150);
            int[] nArray = null;
            int[] nArray2 = null;
            double d = Double.MAX_VALUE;
            for (int i = 0; i < list4.size(); i += n8) {
                int[] nArray3 = list4.get(i);
                for (int[] nArray4 : arrayList) {
                    double d2;
                    double d3 = Math.sqrt((nArray3[0] - nArray4[0]) * (nArray3[0] - nArray4[0]) + (nArray3[2] - nArray4[2]) * (nArray3[2] - nArray4[2]));
                    double d4 = d3 + (d2 = (double)Math.abs(nArray3[1] - nArray4[1])) + Math.max(0.0, d2 - d3) * 3.0;
                    if (!(d4 < d)) continue;
                    d = d4;
                    nArray = nArray3;
                    nArray2 = nArray4;
                }
            }
            if (nArray == null || d > 160.0) continue;
            this.dig(nArray, nArray2);
            ++n4;
        }
        return n4;
    }

    private boolean roomy(List<int[]> list, int n) {
        int n2;
        if (list.size() >= 40) {
            return true;
        }
        for (int[] object2 : list) {
            boolean bl = true;
            for (n2 = -1; n2 <= 1 && bl; ++n2) {
                for (int i = -1; i <= 1 && bl; ++i) {
                    Integer n3 = this.comp.get(Connect.key(object2[0] + n2, object2[1], object2[2] + i));
                    if (n3 != null && n3 == n) continue;
                    bl = false;
                }
            }
            if (!bl) continue;
            return true;
        }
        int n4 = 0;
        block3: for (int[] nArray : list) {
            for (n2 = 2; n2 <= 6; ++n2) {
                BlockState blockState = this.at(nArray[0], nArray[1] + n2, nArray[2]);
                if (blockState == null || blockState.isAir()) continue;
                ++n4;
                continue block3;
            }
        }
        return n4 * 2 >= list.size();
    }

    private void set(int n, int n2, int n3, BlockState blockState) {
        this.p.blocks.put(Connect.key(n, n2, n3), blockState);
    }

    private void clearCell(int n, int n2, int n3) {
        BlockState blockState = this.at(n, n2, n3);
        if (blockState != null && blockState.is(Blocks.CHEST)) {
            return;
        }
        if (blockState == null) {
            return;
        }
        this.set(n, n2, n3, Blocks.AIR.defaultBlockState());
    }

    private void floorCell(int n, int n2, int n3) {
        BlockState blockState = this.at(n, n2, n3);
        if (blockState == null || Connect.floorState(blockState) && !blockState.is(Blocks.CHEST)) {
            return;
        }
        this.set(n, n2, n3, this.floor.defaultBlockState());
    }

    private void dig(int[] nArray, int[] nArray2) {
        int n = nArray2[0] - nArray[0];
        int n2 = nArray2[2] - nArray[2];
        int n3 = nArray2[1] - nArray[1];
        int n4 = Math.max(Math.abs(n), Math.abs(n2));
        int n5 = nArray[0];
        int n6 = nArray[2];
        int n7 = nArray[1];
        if (Math.abs(n3) > n4) {
            this.spiral(n5, n6, Math.min(n7, nArray2[1]), Math.max(n7, nArray2[1]));
            n7 = nArray2[1];
        }
        int n8 = Math.max(1, Math.max(Math.abs(nArray2[0] - n5), Math.abs(nArray2[2] - n6)));
        int n9 = n7;
        for (int i = 0; i <= n8; ++i) {
            int n10 = n5 + Math.round((float)(nArray2[0] - n5) * (float)i / (float)n8);
            int n11 = n6 + Math.round((float)(nArray2[2] - n6) * (float)i / (float)n8);
            int n12 = n7 + Math.round((float)(nArray2[1] - n7) * (float)i / (float)n8);
            boolean bl = Math.abs(nArray2[0] - n5) >= Math.abs(nArray2[2] - n6);
            for (int j = 0; j <= 1; ++j) {
                Direction direction;
                int n13 = bl ? n10 : n10 + j;
                int n14 = bl ? n11 + j : n11;
                for (int k = 0; k <= 2; ++k) {
                    this.clearCell(n13, n12 + k, n14);
                }
                if (n12 > n9) {
                    this.clearCell(n13, n12 + 3, n14);
                }
                this.floorCell(n13, n12 - 1, n14);
                this.support(n13, n12 - 2, n14);
                if (n12 <= n9) continue;
                Direction direction2 = bl ? (nArray2[0] > n5 ? Direction.EAST : Direction.WEST) : (direction = nArray2[2] > n6 ? Direction.SOUTH : Direction.NORTH);
                if (this.at(n13, n12 - 1, n14) == null) continue;
                this.set(n13, n12 - 1, n14, this.step(direction));
            }
            n9 = n12;
        }
    }

    private void support(int n, int n2, int n3) {
        int n4 = 0;
        while (n4 < 24) {
            BlockState blockState = this.at(n, n2, n3);
            if (blockState == null || !blockState.isAir()) {
                return;
            }
            this.set(n, n2, n3, this.floor.defaultBlockState());
            ++n4;
            --n2;
        }
    }

    private BlockState step(Direction direction) {
        return (BlockState)((BlockState)this.stairs.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.HALF, (Comparable)Half.BOTTOM);
    }

    private void spiral(int n, int n2, int n3, int n4) {
        int n5;
        int[][] nArrayArray = new int[][]{{-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}};
        Direction[] directionArray = new Direction[]{Direction.EAST, Direction.EAST, Direction.SOUTH, Direction.SOUTH, Direction.WEST, Direction.WEST, Direction.NORTH, Direction.NORTH};
        for (n5 = n3 - 1; n5 <= n4 + 3; ++n5) {
            int[][] nArrayArray2 = nArrayArray;
            int n6 = nArrayArray2.length;
            for (int i = 0; i < n6; ++i) {
                int[] nArray = nArrayArray2[i];
                this.clearCell(n + nArray[0], n5, n2 + nArray[1]);
            }
        }
        for (n5 = n3 - 1; n5 <= n4 + 1; ++n5) {
            if (this.at(n, n5, n2) == null) continue;
            this.set(n, n5, n2, this.floor.defaultBlockState());
        }
        n5 = 0;
        int n7 = n3;
        while (n7 <= n4) {
            int[] nArray = nArrayArray[n5 % 8];
            this.set(n + nArray[0], n7 - 1, n2 + nArray[1], this.step(directionArray[n5 % 8]));
            if (this.at(n + nArray[0], n7 - 2, n2 + nArray[1]) != null && !Connect.floorState(this.at(n + nArray[0], n7 - 2, n2 + nArray[1]))) {
                this.set(n + nArray[0], n7 - 2, n2 + nArray[1], this.floor.defaultBlockState());
            }
            ++n7;
            ++n5;
        }
        for (int[] nArray : nArrayArray) {
            this.floorCell(n + nArray[0], n3 - 1, n2 + nArray[1]);
        }
        for (int[] nArray : nArrayArray) {
            if (this.at(n + nArray[0], n4 - 1, n2 + nArray[1]) == null || Connect.floorState(this.at(n + nArray[0], n4 - 1, n2 + nArray[1]))) continue;
            this.set(n + nArray[0], n4 - 1, n2 + nArray[1], this.floor.defaultBlockState());
        }
    }
}
