/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BedPart
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 */
package com.coaldust.meteor.underground;

import com.coaldust.meteor.underground.DarkCities;
import com.coaldust.meteor.underground.StructureDecor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;

public final class UndergroundCity {
    public static final int L1 = 5;
    public static final int L2 = -4;
    public static final int L3 = -13;
    private final int R;
    private final double inner;
    private final RandomSource r;
    private final Plan p = new Plan();
    private final Set<Long> carved = new HashSet<Long>();
    private final List<Object[]> linings = new ArrayList<Object[]>();
    private final int v2;
    private final int v3;
    private int gateTop;

    private UndergroundCity(int n, RandomSource randomSource) {
        this.R = n;
        this.inner = ((double)n - 2.5) * ((double)n - 2.5);
        this.r = randomSource;
        this.v2 = randomSource.nextInt(3);
        this.v3 = randomSource.nextInt(3);
    }

    public static Plan plan(int n, RandomSource randomSource) {
        return UndergroundCity.plan(n, randomSource, -1);
    }

    public static int rollVariant(RandomSource randomSource, boolean bl) {
        if (randomSource.nextInt(100) < 25) {
            return 0;
        }
        DarkCities.Kind[] kindArray = DarkCities.Kind.values();
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (int i = 0; i < kindArray.length; ++i) {
            if (!bl && kindArray[i].big()) continue;
            arrayList.add(i + 1);
            if (!kindArray[i].big()) continue;
            arrayList.add(i + 1);
        }
        return (Integer)arrayList.get(randomSource.nextInt(arrayList.size()));
    }

    public static int rollBig(RandomSource randomSource) {
        DarkCities.Kind[] kindArray = DarkCities.Kind.values();
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (int i = 0; i < kindArray.length; ++i) {
            if (!kindArray[i].big()) continue;
            arrayList.add(i + 1);
        }
        return (Integer)arrayList.get(randomSource.nextInt(arrayList.size()));
    }

    public static int radiusFor(int n) {
        DarkCities.Kind[] kindArray = DarkCities.Kind.values();
        if (n > 0 && n <= kindArray.length && kindArray[n - 1] == DarkCities.Kind.SPIRES) {
            return 56;
        }
        return n > 0 && n <= kindArray.length && kindArray[n - 1].big() ? 30 : 24;
    }

    public static Plan plan(int n, RandomSource randomSource, int n2) {
        int n3;
        int n4;
        DarkCities.Kind[] kindArray = DarkCities.Kind.values();
        if (n2 > 0 && n2 <= kindArray.length) {
            return DarkCities.plan(n, randomSource, kindArray[n2 - 1]);
        }
        int n5 = n4 = n2 == 0 ? 0 : randomSource.nextInt(100);
        if (n4 >= 30 && (n3 = UndergroundCity.rollVariant(randomSource, n >= 29)) > 0) {
            return DarkCities.plan(n, randomSource, kindArray[n3 - 1]);
        }
        UndergroundCity undergroundCity = new UndergroundCity(n, randomSource);
        undergroundCity.body();
        undergroundCity.carveAll();
        undergroundCity.applyLinings();
        undergroundCity.decorateAll();
        Plan plan = undergroundCity.p;
        plan.west2 = (new String[]{"lifespring", "mushrooms", "crystals"})[undergroundCity.v2];
        plan.west3 = (new String[]{"ancestors", "prison", "cellar"})[undergroundCity.v3];
        StructureDecor.Theme theme = StructureDecor.rollTheme(randomSource);
        float f = randomSource.nextFloat();
        if (f < 0.35f) {
            StructureDecor.ruin(plan, randomSource);
            plan.state = "ruined";
        } else if (f < 0.5f) {
            StructureDecor.flood(plan, -13);
            plan.state = "flooded";
        }
        StructureDecor.theme(plan, theme);
        plan.theme = theme.id;
        StructureDecor.cheapen(plan, randomSource);
        StructureDecor.transform(plan, randomSource.nextInt(8));
        return plan;
    }

    private static BlockState s(Block block) {
        return block.defaultBlockState();
    }

    private static BlockState stairs(Block block, Direction direction, boolean bl) {
        return (BlockState)((BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.HALF, (Comparable)(bl ? Half.TOP : Half.BOTTOM));
    }

    private static BlockState slab(Block block, boolean bl) {
        return (BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.SLAB_TYPE, (Comparable)(bl ? SlabType.TOP : SlabType.BOTTOM));
    }

    private static BlockState hangingLantern(boolean bl) {
        return (BlockState)(bl ? Blocks.SOUL_LANTERN : Blocks.LANTERN).defaultBlockState().setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(true));
    }

    private static BlockState litCandle() {
        return (BlockState)Blocks.CANDLE.defaultBlockState().setValue((Property)BlockStateProperties.LIT, (Comparable)Boolean.valueOf(true));
    }

    private BlockState stoneBrick() {
        float f = this.r.nextFloat();
        return f < 0.1f ? UndergroundCity.s(Blocks.CRACKED_STONE_BRICKS) : (f < 0.18f ? UndergroundCity.s(Blocks.MOSSY_STONE_BRICKS) : UndergroundCity.s(Blocks.STONE_BRICKS));
    }

    private BlockState deepBrick() {
        float f = this.r.nextFloat();
        return f < 0.12f ? UndergroundCity.s(Blocks.CRACKED_DEEPSLATE_BRICKS) : (f < 0.45f ? UndergroundCity.s(Blocks.DEEPSLATE_TILES) : UndergroundCity.s(Blocks.DEEPSLATE_BRICKS));
    }

    private BlockState rock(int n) {
        boolean bl;
        float f = this.r.nextFloat();
        boolean bl2 = bl = n < -4;
        if (f < 0.006f) {
            return UndergroundCity.s(bl ? Blocks.DEEPSLATE_IRON_ORE : Blocks.IRON_ORE);
        }
        if (f < 0.012f) {
            return UndergroundCity.s(bl ? Blocks.DEEPSLATE_COPPER_ORE : Blocks.COPPER_ORE);
        }
        if (f < 0.016f) {
            return UndergroundCity.s(bl ? Blocks.DEEPSLATE_COAL_ORE : Blocks.COAL_ORE);
        }
        if (f < 0.0175f) {
            return UndergroundCity.s(bl ? Blocks.DEEPSLATE_GOLD_ORE : Blocks.COAL_ORE);
        }
        if (f < 0.018f) {
            return UndergroundCity.s(bl ? Blocks.DEEPSLATE_DIAMOND_ORE : Blocks.COAL_ORE);
        }
        if (f < 0.02f) {
            return UndergroundCity.s(bl ? Blocks.DEEPSLATE_REDSTONE_ORE : Blocks.LAPIS_ORE);
        }
        return UndergroundCity.s(bl ? Blocks.DEEPSLATE : Blocks.TUFF);
    }

    private boolean inside(int n, int n2, int n3) {
        return (double)(n * n + n2 * n2 + n3 * n3) <= this.inner;
    }

    private void set(int n, int n2, int n3, BlockState blockState) {
        this.p.blocks.put(Plan.key(n, n2, n3), blockState);
    }

    private void air(int n, int n2, int n3) {
        if (!this.inside(n, n2, n3)) {
            return;
        }
        long l = Plan.key(n, n2, n3);
        this.p.blocks.put(l, UndergroundCity.s(Blocks.AIR));
        this.carved.add(l);
    }

    private boolean isCarved(int n, int n2, int n3) {
        return this.carved.contains(Plan.key(n, n2, n3));
    }

    private void hall(int n, int n2, int n3, int n4, int n5, int n6, Supplier<BlockState> supplier, Supplier<BlockState> supplier2) {
        for (int i = n; i <= n4; ++i) {
            for (int j = n2; j <= n5; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    this.air(i, j, k);
                }
            }
        }
        this.linings.add(new Object[]{new int[]{n, n2, n3, n4, n5, n6}, supplier, supplier2});
    }

    private void applyLinings() {
        for (Object[] objectArray : this.linings) {
            int[] nArray = (int[])objectArray[0];
            Supplier supplier = (Supplier)objectArray[1];
            Supplier supplier2 = (Supplier)objectArray[2];
            for (int i = nArray[0] - 1; i <= nArray[3] + 1; ++i) {
                for (int j = nArray[1] - 1; j <= nArray[4] + 1; ++j) {
                    for (int k = nArray[2] - 1; k <= nArray[5] + 1; ++k) {
                        boolean bl;
                        boolean bl2 = bl = i < nArray[0] || i > nArray[3] || j < nArray[1] || j > nArray[4] || k < nArray[2] || k > nArray[5];
                        if (!bl || !this.inside(i, j, k) || this.isCarved(i, j, k)) continue;
                        this.set(i, j, k, j < nArray[1] && supplier2 != null ? (BlockState)supplier2.get() : (BlockState)supplier.get());
                    }
                }
            }
        }
    }

    private void floor(int n, int n2, int n3, int n4, int n5, Supplier<BlockState> supplier) {
        for (int i = n; i <= n3; ++i) {
            for (int j = n2; j <= n4; ++j) {
                if (!this.inside(i, n5 - 1, j) || this.isCarved(i, n5 - 1, j)) continue;
                this.set(i, n5 - 1, j, supplier.get());
            }
        }
    }

    private void cavern(int n, int n2, int n3, int n4, int n5, int n6, Supplier<BlockState> supplier) {
        double d = this.r.nextDouble() * 6.28;
        for (int i = n - n4 - 1; i <= n + n4 + 1; ++i) {
            for (int j = n3 - n5 - 1; j <= n3 + n5 + 1; ++j) {
                double d2 = (double)(i - n) / (double)n4;
                double d3 = (double)(j - n3) / (double)n5;
                double d4 = d2 * d2 + d3 * d3;
                double d5 = 1.0 + 0.1 * Math.sin(Math.atan2(d3, d2) * 5.0 + d);
                if (d4 > d5) continue;
                int n7 = n2 + (int)Math.round((double)n6 * Math.sqrt(Math.max(0.0, 1.0 - d4 / d5)) + 1.0 + (double)this.r.nextInt(2));
                for (int k = n2; k < n7; ++k) {
                    this.air(i, k, j);
                }
                if (!this.inside(i, n2 - 1, j)) continue;
                this.set(i, n2 - 1, j, supplier.get());
            }
        }
    }

    private void line(int n, int n2, int n3, int n4, int n5, int n6, int n7, Supplier<BlockState> supplier, Supplier<BlockState> supplier2) {
        int n8 = Math.min(n, n3);
        int n9 = Math.max(n, n3);
        int n10 = Math.min(n2, n4);
        int n11 = Math.max(n2, n4);
        if (n8 == n9) {
            this.hall(n8 - n6 / 2, n5, n10, n8 + n6 / 2, n5 + n7 - 1, n11, supplier, supplier2);
        } else {
            this.hall(n8, n5, n10 - n6 / 2, n9, n5 + n7 - 1, n10 + n6 / 2, supplier, supplier2);
        }
    }

    private void stairX(int n, int n2, int n3, int n4, int n5, int n6, Supplier<BlockState> supplier) {
        for (int i = 0; i <= n4; ++i) {
            int n7 = n + n2 * i;
            int n8 = n3 - i;
            this.hall(n7, n8, n5, n7, n8 + 3, n6, supplier, supplier);
        }
    }

    private void body() {
        for (int i = -this.R; i <= this.R; ++i) {
            for (int j = -this.R; j <= this.R; ++j) {
                for (int k = -this.R; k <= this.R; ++k) {
                    double d = Math.sqrt(i * i + j * j + k * k);
                    if (d > (double)this.R) continue;
                    if (d > (double)this.R - 1.5) {
                        float f = this.r.nextFloat();
                        this.set(i, j, k, UndergroundCity.s(f < 0.4f ? Blocks.BLACKSTONE : (f < 0.7f ? Blocks.BASALT : (f < 0.92f ? Blocks.SMOOTH_BASALT : Blocks.OBSIDIAN))));
                        continue;
                    }
                    this.set(i, j, k, this.rock(j));
                }
            }
        }
    }

    private void carveAll() {
        double d;
        int n;
        int n2;
        int n3;
        int n4;
        int[][] nArrayArray;
        int n5;
        Supplier<BlockState> supplier = this::stoneBrick;
        Supplier<BlockState> supplier2 = this::deepBrick;
        for (int i = -17; i <= this.R; ++i) {
            for (int j = -4; j <= 4; ++j) {
                for (n5 = -4; n5 <= 4; ++n5) {
                    if (!((double)(j * j + n5 * n5) <= 12.5)) continue;
                    this.air(j, i, n5);
                }
            }
        }
        int[][] nArrayArray2 = nArrayArray = new int[][]{{5, 6}, {-4, 6}, {-13, 4}};
        n5 = nArrayArray2.length;
        for (n4 = 0; n4 < n5; ++n4) {
            int[] nArray = nArrayArray2[n4];
            for (n3 = -8; n3 <= 8; ++n3) {
                for (n2 = -8; n2 <= 8; ++n2) {
                    double d2 = Math.sqrt(n3 * n3 + n2 * n2);
                    if (!(d2 <= 8.4)) continue;
                    for (int i = nArray[0]; i < nArray[0] + nArray[1]; ++i) {
                        this.air(n3, i, n2);
                    }
                }
            }
        }
        this.hall(-11, 5, -21, 11, 11, -11, supplier, supplier);
        this.line(0, -11, 0, -8, 5, 3, 4, supplier, supplier);
        for (n = -12; n <= 12; ++n) {
            if (!this.inside(n, 4, -20)) continue;
            this.air(n, 4, -20);
        }
        for (n = -7; n <= 7; ++n) {
            for (n5 = 8; n5 <= 22; ++n5) {
                double d3 = Math.sqrt(n * n + (n5 - 15) * (n5 - 15));
                if (d3 > 6.6) continue;
                n3 = 9 + (int)Math.round(Math.sqrt(Math.max(0.0, 44.0 - d3 * d3)) * 0.5);
                for (n2 = 5; n2 <= n3; ++n2) {
                    this.air(n, n2, n5);
                }
                if (!(d3 > 2.4) || !(d3 <= 3.5) || Math.abs(n) <= 1 || Math.abs(n5 - 15) <= 1) continue;
                this.air(n, 4, n5);
            }
        }
        this.line(0, 8, 0, 9, 5, 3, 4, supplier, supplier);
        this.hall(-19, 5, -4, -11, 9, 4, supplier, supplier);
        this.line(-11, 0, -8, 0, 5, 3, 4, supplier, supplier);
        n = 5;
        for (n5 = 9; n5 <= this.R + 4 && !((double)((n4 = Math.min(5 + (n5 - 9), 30)) - 1) > (d = Math.sqrt(Math.max(0, this.R * this.R - n5 * n5))) + 1.0); ++n5) {
            for (n2 = -2; n2 <= 2; ++n2) {
                for (int i = n4; i <= n4 + 4; ++i) {
                    long l = Plan.key(n5, i, n2);
                    this.p.blocks.put(l, UndergroundCity.s(Blocks.AIR));
                    this.carved.add(l);
                }
            }
            n = n4;
        }
        this.gateTop = n;
        this.line(5, 0, 9, 0, 5, 5, 5, supplier, supplier);
        this.line(2, -8, 4, -8, 5, 3, 4, supplier, supplier);
        this.stairX(4, 1, 5, 9, -9, -7, supplier);
        this.line(1, -8, 13, -8, -4, 3, 4, supplier, supplier);
        this.cavern(0, -4, -15, 10, 6, 7, () -> this.r.nextFloat() < 0.7f ? UndergroundCity.s(Blocks.POLISHED_ANDESITE) : this.stoneBrick());
        this.line(0, -9, 0, -8, -4, 3, 4, supplier, supplier);
        this.cavern(0, -4, 15, 10, 6, 7, () -> this.r.nextFloat() < 0.6f ? UndergroundCity.s(Blocks.POLISHED_BLACKSTONE_BRICKS) : UndergroundCity.s(Blocks.DEEPSLATE_TILES));
        this.line(0, 8, 0, 9, -4, 3, 4, supplier, supplier);
        this.cavern(-15, -4, 0, 6, 10, 8, () -> UndergroundCity.s(Blocks.MOSS_BLOCK));
        this.line(-9, 0, -8, 0, -4, 3, 4, supplier, supplier);
        this.hall(9, -4, -1, 20, -1, 1, supplier, supplier);
        for (n5 = 0; n5 < 2; ++n5) {
            n4 = 10 + n5 * 5;
            this.hall(n4, -4, 3, n4 + 3, -1, 6, supplier, supplier);
            this.hall(n4, -4, -6, n4 + 3, -1, -3, supplier, supplier);
            this.air(n4 + 1, -4, 2);
            this.air(n4 + 1, -3, 2);
            this.air(n4 + 1, -4, -2);
            this.air(n4 + 1, -3, -2);
            this.p.rooms += 2;
        }
        this.line(3, 8, 5, 8, -4, 3, 4, supplier, supplier);
        this.stairX(5, 1, -4, 9, 7, 9, supplier2);
        this.line(2, 8, 14, 8, -13, 3, 4, supplier2, supplier2);
        this.hall(-6, -13, -16, 6, -10, -10, () -> UndergroundCity.s(Blocks.IRON_BLOCK), () -> UndergroundCity.s(Blocks.GOLD_BLOCK));
        this.line(0, -10, 0, -8, -13, 3, 3, supplier2, supplier2);
        this.hall(-16, -13, -4, -10, -10, 4, supplier2, supplier2);
        this.line(-10, 0, -8, 0, -13, 3, 3, supplier2, supplier2);
        for (n5 = 8; n5 <= 14; ++n5) {
            for (n4 = -1; n4 <= 1; ++n4) {
                for (int i = -13; i < -10; ++i) {
                    this.air(n4, i, n5);
                }
            }
        }
        for (n5 = -10; n5 <= 10; ++n5) {
            for (n4 = 12; n4 <= 14; ++n4) {
                for (int i = -13; i < -10; ++i) {
                    this.air(n5, i, n4);
                }
            }
        }
        this.p.rooms += 12;
    }

    private void decorateAll() {
        this.shaftDecor();
        this.hubDecor(5, 6, false);
        this.hubDecor(-4, 6, false);
        this.hubDecor(-13, 4, true);
        this.greatHall();
        this.rotunda();
        this.library();
        this.gate();
        this.market();
        this.forge();
        switch (this.v2) {
            case 1: {
                this.mushroomGrove();
                break;
            }
            case 2: {
                this.crystalGrotto();
                break;
            }
            default: {
                this.lifespring();
            }
        }
        this.dwellings();
        this.treasury();
        switch (this.v3) {
            case 1: {
                this.prison();
                break;
            }
            case 2: {
                this.cellar();
                break;
            }
            default: {
                this.ancestors();
            }
        }
        this.mines();
        this.stairsDecor();
    }

    private void shaftDecor() {
        int n;
        int n2;
        int n3;
        for (int i = -18; i <= this.R; ++i) {
            for (n3 = -5; n3 <= 5; ++n3) {
                for (n2 = -5; n2 <= 5; ++n2) {
                    double d = Math.sqrt(n3 * n3 + n2 * n2);
                    if (d <= 3.6 || d > 4.6 || !this.inside(n3, i, n2) || this.isCarved(n3, i, n2)) continue;
                    BlockState blockState = i == 12 || i == 3 ? UndergroundCity.s(Blocks.GOLD_BLOCK) : (i == 13 || i == 4 ? UndergroundCity.s(Blocks.LAPIS_BLOCK) : (i % 4 == 0 ? UndergroundCity.s(Blocks.CHISELED_STONE_BRICKS) : this.stoneBrick()));
                    this.set(n3, i, n2, blockState);
                }
            }
        }
        int[] nArray = new int[]{5, -4, -13};
        n3 = nArray.length;
        for (n2 = 0; n2 < n3; ++n2) {
            int n4 = nArray[n2];
            for (int i = -5; i <= 5; ++i) {
                for (int j = -5; j <= 5; ++j) {
                    double d = Math.sqrt(i * i + j * j);
                    if (d > 3.6 && d <= 4.4) {
                        this.set(i, n4, j, UndergroundCity.s(Blocks.STONE_BRICK_WALL));
                    }
                    if (!(d > 3.6) || !(d <= 4.4)) continue;
                    this.set(i, n4 - 1, j, UndergroundCity.s(Blocks.CHISELED_STONE_BRICKS));
                }
            }
        }
        for (n = -4; n <= 4; ++n) {
            for (n3 = -4; n3 <= 4; ++n3) {
                if ((double)(n * n + n3 * n3) > 12.5) continue;
                n2 = (int)Math.floor(Math.sqrt(Math.max(0.0, ((double)this.R - 0.5) * ((double)this.R - 0.5) - (double)(n * n) - (double)(n3 * n3))));
                for (int i = 5; i < n2; ++i) {
                    if (this.inside(n, i, n3)) continue;
                    this.set(n, i, n3, UndergroundCity.s(Blocks.AIR));
                }
                this.set(n, n2, n3, UndergroundCity.s(Blocks.IRON_BARS));
            }
        }
        for (n = -4; n <= 4; ++n) {
            for (n3 = -4; n3 <= 4; ++n3) {
                if ((double)(n * n + n3 * n3) > 12.5) continue;
                for (n2 = -17; n2 <= -15; ++n2) {
                    this.set(n, n2, n3, UndergroundCity.s(Blocks.WATER));
                }
                this.set(n, -18, n3, UndergroundCity.s(Blocks.DEEPSLATE_TILES));
            }
        }
        n = (int)Math.sqrt(Math.max(0.0, ((double)this.R - 2.0) * ((double)this.R - 2.0) - 8.0));
        for (int[] nArray2 : new int[][]{{2, 2}, {-2, -2}, {2, -2}, {-2, 2}}) {
            int n5 = 12 + this.r.nextInt(5);
            for (int i = n; i > n5; --i) {
                this.set(nArray2[0], i, nArray2[1], UndergroundCity.s(Blocks.CHAIN));
            }
            this.set(nArray2[0], n5, nArray2[1], UndergroundCity.hangingLantern(false));
        }
    }

    private void hubDecor(int n, int n2, boolean bl) {
        int n3;
        for (n3 = -8; n3 <= 8; ++n3) {
            for (int i = -8; i <= 8; ++i) {
                double d = Math.sqrt(n3 * n3 + i * i);
                if (d <= 4.4 || d > 8.4 || !this.inside(n3, n - 1, i)) continue;
                BlockState blockState = Math.abs(d - 6.2) < 0.5 ? UndergroundCity.s(bl ? Blocks.GILDED_BLACKSTONE : Blocks.GOLD_BLOCK) : ((n3 + i & 1) == 0 ? UndergroundCity.s(Blocks.DEEPSLATE_TILES) : UndergroundCity.s(Blocks.POLISHED_DEEPSLATE));
                this.set(n3, n - 1, i, blockState);
            }
        }
        for (n3 = 0; n3 < 360; n3 += 45) {
            int n4;
            int n5;
            double d = Math.toRadians((double)n3 + 22.5);
            int n6 = (int)Math.round(Math.cos(d) * 7.6);
            int n7 = (int)Math.round(Math.sin(d) * 7.6);
            for (n5 = n; n5 < n + n2; ++n5) {
                BlockState blockState;
                BlockState blockState2 = n5 == n ? UndergroundCity.s(Blocks.CHISELED_STONE_BRICKS) : (blockState = n5 == n + n2 - 1 ? UndergroundCity.s(Blocks.CHISELED_DEEPSLATE) : UndergroundCity.s(Blocks.POLISHED_DEEPSLATE));
                if (!this.inside(n6, n5, n7)) continue;
                this.set(n6, n5, n7, blockState);
            }
            n5 = (int)Math.round(Math.cos(d) * 6.0);
            if (!this.inside(n5, n + n2 - 1, n4 = (int)Math.round(Math.sin(d) * 6.0))) continue;
            this.set(n5, n + n2 - 1, n4, UndergroundCity.hangingLantern(bl));
        }
    }

    private void greatHall() {
        int n;
        for (n = -11; n <= 11; ++n) {
            for (int i = -21; i <= -11; ++i) {
                BlockState blockState;
                if (!this.isCarved(n, 5, i)) continue;
                BlockState blockState2 = n == -11 || n == 11 || i == -21 || i == -11 ? UndergroundCity.s(Blocks.DEEPSLATE_TILES) : (blockState = i == -16 ? UndergroundCity.s(Blocks.GOLD_BLOCK) : UndergroundCity.s(Blocks.POLISHED_ANDESITE));
                if (this.isCarved(n, 4, i)) continue;
                this.set(n, 4, i, blockState);
            }
        }
        for (n = -8; n <= 8; n += 4) {
            for (int n2 : new int[]{-19, -13}) {
                for (int i = 5; i <= 11 && this.inside(n, i, n2); ++i) {
                    BlockState blockState = i == 5 ? UndergroundCity.s(Blocks.CHISELED_STONE_BRICKS) : (i >= 10 ? UndergroundCity.s(Blocks.CHISELED_DEEPSLATE) : UndergroundCity.s(Blocks.POLISHED_DEEPSLATE));
                    this.set(n, i, n2, blockState);
                }
                if (!this.inside(n, 9, n2 + (n2 < -16 ? 1 : -1))) continue;
                this.set(n, 9, n2 + (n2 < -16 ? 1 : -1), UndergroundCity.s(Blocks.GOLD_BLOCK));
            }
        }
        for (n = -12; n <= 12; ++n) {
            if (!this.isCarved(n, 4, -20)) continue;
            this.set(n, 4, -20, UndergroundCity.s(Blocks.LAVA));
            this.set(n, 5, -20, UndergroundCity.s(Blocks.IRON_BARS));
        }
        for (n = -6; n <= 6; ++n) {
            if (n == 0) continue;
            this.set(n, 5, -16, UndergroundCity.s(Blocks.DARK_OAK_PLANKS));
            this.set(n, 5, -17, UndergroundCity.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
            this.set(n, 5, -15, UndergroundCity.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH, false));
            if (n % 3 != 0) continue;
            this.set(n, 6, -16, UndergroundCity.litCandle());
        }
        this.set(-10, 5, -16, UndergroundCity.s(Blocks.GOLD_BLOCK));
        this.set(-10, 6, -16, UndergroundCity.stairs(Blocks.STONE_BRICK_STAIRS, Direction.WEST, false));
        this.set(-10, 7, -16, UndergroundCity.s(Blocks.CHISELED_QUARTZ_BLOCK));
        this.set(-10, 5, -17, UndergroundCity.s(Blocks.LAPIS_BLOCK));
        this.set(-10, 5, -15, UndergroundCity.s(Blocks.LAPIS_BLOCK));
        this.set(-10, 6, -17, UndergroundCity.s(Blocks.LANTERN));
        this.set(-10, 6, -15, UndergroundCity.s(Blocks.LANTERN));
        this.set(-9, 5, -16, UndergroundCity.stairs(Blocks.STONE_BRICK_STAIRS, Direction.WEST, false));
        this.chest(-10, 5, -19, false);
        for (n = -6; n <= 6; n += 6) {
            if (!this.inside(n, 11, -16)) continue;
            this.set(n, 11, -16, UndergroundCity.s(Blocks.CHAIN));
            this.set(n, 10, -16, UndergroundCity.hangingLantern(false));
        }
    }

    private void rotunda() {
        int n;
        int n2;
        for (n2 = -7; n2 <= 7; ++n2) {
            for (int i = 8; i <= 22; ++i) {
                double d = Math.sqrt(n2 * n2 + (i - 15) * (i - 15));
                if (d > 6.6 || !this.isCarved(n2, 5, i)) continue;
                if (d > 2.4 && d <= 3.5 && Math.abs(n2) > 1 && Math.abs(i - 15) > 1) {
                    this.set(n2, 4, i, UndergroundCity.s(Blocks.LAVA));
                    this.set(n2, 3, i, UndergroundCity.s(Blocks.BLACKSTONE));
                    continue;
                }
                BlockState blockState = Math.abs(d - 4.7) < 0.5 ? UndergroundCity.s(Blocks.EMERALD_BLOCK) : (d <= 2.4 ? UndergroundCity.s(Blocks.GILDED_BLACKSTONE) : UndergroundCity.s(Blocks.POLISHED_BLACKSTONE_BRICKS));
                this.set(n2, 4, i, blockState);
            }
        }
        for (n2 = 0; n2 < 360; n2 += 60) {
            double d = Math.toRadians(n2 + 30);
            int n3 = (int)Math.round(Math.cos(d) * 5.6);
            int n4 = 15 + (int)Math.round(Math.sin(d) * 5.6);
            for (int i = 5; i <= 9; ++i) {
                this.set(n3, i, n4, UndergroundCity.s(Blocks.QUARTZ_PILLAR));
            }
            this.set(n3, 10, n4, UndergroundCity.s(Blocks.CHISELED_QUARTZ_BLOCK));
        }
        this.set(0, 5, 15, UndergroundCity.s(Blocks.CHISELED_QUARTZ_BLOCK));
        this.set(0, 6, 15, UndergroundCity.s(Blocks.EMERALD_BLOCK));
        this.set(0, 7, 15, UndergroundCity.litCandle());
        this.chest(1, 5, 16, true);
        n2 = 12;
        for (n = n2 + 3; n >= n2; --n) {
            if (!this.isCarved(0, n, 15) || this.isCarved(0, n + 1, 15)) continue;
            this.set(0, n, 15, UndergroundCity.s(Blocks.SHROOMLIGHT));
            break;
        }
        this.set(0, 8, 15, UndergroundCity.hangingLantern(false));
        for (n = 9; n <= 11; ++n) {
            if (!this.isCarved(0, n, 15)) continue;
            this.set(0, n, 15, UndergroundCity.s(Blocks.CHAIN));
        }
    }

    private void library() {
        int n;
        int n2;
        for (n2 = -19; n2 <= -11; ++n2) {
            for (n = -4; n <= 4; ++n) {
                this.set(n2, 4, n, (n2 + n & 1) == 0 ? UndergroundCity.s(Blocks.SPRUCE_PLANKS) : UndergroundCity.s(Blocks.DARK_OAK_PLANKS));
            }
        }
        for (n2 = -19; n2 <= -11; ++n2) {
            for (n = 5; n <= 7; ++n) {
                if (this.inside(n2, n, -4)) {
                    this.set(n2, n, -4, n == 6 && n2 % 3 == 0 ? UndergroundCity.s(Blocks.CHISELED_BOOKSHELF) : UndergroundCity.s(Blocks.BOOKSHELF));
                }
                if (!this.inside(n2, n, 4)) continue;
                this.set(n2, n, 4, n == 6 && n2 % 3 == 0 ? UndergroundCity.s(Blocks.CHISELED_BOOKSHELF) : UndergroundCity.s(Blocks.BOOKSHELF));
            }
        }
        for (n2 = -17; n2 <= -13; n2 += 2) {
            for (int n3 : new int[]{-2, 2}) {
                for (int i = 5; i <= 6; ++i) {
                    this.set(n2, i, n3, UndergroundCity.s(Blocks.BOOKSHELF));
                }
            }
        }
        this.set(-18, 5, 0, UndergroundCity.s(Blocks.LECTERN));
        this.set(-14, 5, 0, UndergroundCity.slab(Blocks.SPRUCE_SLAB, true));
        this.set(-13, 5, 0, UndergroundCity.slab(Blocks.SPRUCE_SLAB, true));
        this.set(-14, 6, 0, UndergroundCity.litCandle());
        for (n2 = -18; n2 <= -12; ++n2) {
            if (n2 == -14 || n2 == -13) continue;
            this.set(n2, 5, 1, UndergroundCity.s(Blocks.BLUE_CARPET));
        }
        this.set(-12, 8, 0, UndergroundCity.hangingLantern(false));
        this.set(-16, 8, 0, UndergroundCity.hangingLantern(false));
        this.chest(-19, 5, 0, false);
    }

    private void gate() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        for (n5 = 9; n5 <= this.R + 4 && (n4 = 5 + (n5 - 9)) <= this.gateTop; ++n5) {
            for (n3 = -2; n3 <= 2; ++n3) {
                BlockState blockState = Math.abs(n3) == 2 ? UndergroundCity.s(Blocks.CHISELED_STONE_BRICKS) : UndergroundCity.stairs(Blocks.STONE_BRICK_STAIRS, Direction.EAST, false);
                this.set(n5, n4, n3, blockState);
                this.set(n5, n4 - 1, n3, this.stoneBrick());
            }
            if ((n5 - 9) % 3 != 0) continue;
            this.set(n5, n4 + 4, -2, UndergroundCity.hangingLantern(false));
            this.set(n5, n4 + 4, 2, UndergroundCity.hangingLantern(false));
        }
        n5 = 9 + (this.gateTop - 5) + 1;
        n4 = this.gateTop + 1;
        for (n3 = -4; n3 <= 4; ++n3) {
            for (int i = n4 - 1; i <= n4 + 6; ++i) {
                n2 = Math.abs(n3) >= 3 ? 1 : 0;
                int n6 = n = i >= n4 + 5 ? 1 : 0;
                if (n2 != 0 || n != 0) {
                    this.set(n5, i, n3, i == n4 + 6 ? UndergroundCity.s(Blocks.CHISELED_STONE_BRICKS) : (n2 != 0 && i == n4 + 3 ? UndergroundCity.s(Blocks.GOLD_BLOCK) : this.stoneBrick()));
                    continue;
                }
                if (i >= n4 + 4) {
                    this.set(n5, i, n3, UndergroundCity.s(Blocks.IRON_BARS));
                    continue;
                }
                this.set(n5, i, n3, UndergroundCity.s(Blocks.AIR));
            }
        }
        int[] nArray = new int[]{-4, 4};
        int n7 = nArray.length;
        for (n2 = 0; n2 < n7; ++n2) {
            n = nArray[n2];
            this.set(n5 + 1, n4, n, UndergroundCity.s(Blocks.CHISELED_STONE_BRICKS));
            this.set(n5 + 1, n4 + 1, n, UndergroundCity.s(Blocks.POLISHED_DEEPSLATE));
            this.set(n5 + 1, n4 + 2, n, UndergroundCity.s(Blocks.CHISELED_DEEPSLATE));
            this.set(n5 + 1, n4 + 3, n, UndergroundCity.s(Blocks.LANTERN));
        }
        for (int i = n5 + 1; i <= n5 + 4; ++i) {
            for (n7 = -2; n7 <= 2; ++n7) {
                this.set(i, n4 - 1, n7, this.stoneBrick());
                for (n2 = n4; n2 <= n4 + 4; ++n2) {
                    this.set(i, n2, n7, UndergroundCity.s(Blocks.AIR));
                }
            }
        }
    }

    private void market() {
        int n;
        int n2;
        for (n2 = -1; n2 <= 1; ++n2) {
            for (n = -16; n <= -14; ++n) {
                boolean bl = Math.abs(n2) == 1 || n != -15;
                this.set(n2, -4, n, bl ? UndergroundCity.s(Blocks.STONE_BRICK_WALL) : UndergroundCity.s(Blocks.WATER));
                if (bl) continue;
                this.set(n2, -5, n, UndergroundCity.s(Blocks.WATER));
                this.set(n2, -6, n, UndergroundCity.s(Blocks.WATER));
            }
        }
        for (n2 = -3; n2 <= -2; ++n2) {
            this.set(-1, n2, -16, UndergroundCity.s(Blocks.SPRUCE_FENCE));
            this.set(1, n2, -14, UndergroundCity.s(Blocks.SPRUCE_FENCE));
        }
        this.set(0, -1, -15, UndergroundCity.slab(Blocks.SPRUCE_SLAB, false));
        this.set(0, -2, -15, UndergroundCity.hangingLantern(false));
        for (n2 = 0; n2 < 3; ++n2) {
            this.house(-8 + n2 * 6, -20, Direction.SOUTH);
        }
        Block[] blockArray = new Block[]{Blocks.RED_WOOL, Blocks.YELLOW_WOOL, Blocks.BLUE_WOOL, Blocks.GREEN_WOOL};
        n = 0;
        for (int n3 : new int[]{-7, -3, 3, 7}) {
            int n4 = -12;
            int[][] nArrayArray = new int[][]{{0, 0}, {2, 0}, {0, 1}, {2, 1}};
            int n5 = nArrayArray.length;
            for (int i = 0; i < n5; ++i) {
                int[] nArray = nArrayArray[i];
                for (int j = -4; j <= -3; ++j) {
                    this.set(n3 + nArray[0], j, n4 + nArray[1], UndergroundCity.s(Blocks.SPRUCE_FENCE));
                }
            }
            for (int i = 0; i <= 2; ++i) {
                for (n5 = 0; n5 <= 1; ++n5) {
                    this.set(n3 + i, -2, n4 + n5, UndergroundCity.s(blockArray[n % blockArray.length]));
                }
            }
            this.set(n3 + 1, -4, n4, n % 2 == 0 ? UndergroundCity.s(Blocks.BARREL) : UndergroundCity.s(Blocks.HAY_BLOCK));
            if (n == 1) {
                this.chest(n3 + 1, -4, n4 + 1, false);
            }
            ++n;
        }
    }

    private void house(int n, int n2, Direction direction) {
        int n3;
        int n4;
        int n5 = 4;
        int n6 = 4;
        int n7 = 3;
        for (n4 = n; n4 <= n + n5; ++n4) {
            for (n3 = n2; n3 <= n2 + n6; ++n3) {
                if (this.inside(n4, -4 + n7 + 2, n3)) continue;
                return;
            }
        }
        for (n4 = n; n4 <= n + n5; ++n4) {
            for (n3 = n2; n3 <= n2 + n6; ++n3) {
                boolean bl = n4 == n || n4 == n + n5 || n3 == n2 || n3 == n2 + n6;
                boolean bl2 = !(n4 != n && n4 != n + n5 || n3 != n2 && n3 != n2 + n6);
                this.set(n4, -5, n3, UndergroundCity.s(Blocks.SPRUCE_PLANKS));
                for (int i = -4; i < -4 + n7; ++i) {
                    this.set(n4, i, n3, !bl ? UndergroundCity.s(Blocks.AIR) : (bl2 ? UndergroundCity.s(Blocks.STRIPPED_SPRUCE_LOG) : this.stoneBrick()));
                }
            }
        }
        for (n4 = n2 - 1; n4 <= n2 + n6 + 1; ++n4) {
            for (n3 = 0; n3 <= 2; ++n3) {
                this.set(n - 1 + n3, -4 + n7 + n3, n4, UndergroundCity.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
                this.set(n + n5 + 1 - n3, -4 + n7 + n3, n4, UndergroundCity.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
            }
            this.set(n + 2, -4 + n7 + 2, n4, UndergroundCity.slab(Blocks.SPRUCE_SLAB, false));
        }
        for (n4 = n + 1; n4 <= n + n5 - 1; ++n4) {
            this.set(n4, -4 + n7, n2, this.stoneBrick());
        }
        for (n4 = n + 1; n4 <= n + n5 - 1; ++n4) {
            this.set(n4, -4 + n7, n2 + n6, this.stoneBrick());
        }
        n4 = direction == Direction.SOUTH ? n2 + n6 : n2;
        this.set(n + 2, -4, n4, UndergroundCity.s(Blocks.AIR));
        this.set(n + 2, -3, n4, UndergroundCity.s(Blocks.AIR));
        this.set(n, -3, n2 + 2, UndergroundCity.s(Blocks.IRON_BARS));
        this.set(n + n5, -3, n2 + 2, UndergroundCity.s(Blocks.IRON_BARS));
        this.bed(n + 1, -4, n2 + 1, Direction.SOUTH);
        this.set(n + 3, -4, n2 + 1, UndergroundCity.s(Blocks.BARREL));
        this.set(n + 2, -4, n2 + 2, UndergroundCity.s(Blocks.RED_CARPET));
        this.set(n + 2, -4 + n7 - 1, n2 + 2, UndergroundCity.hangingLantern(false));
        if (this.r.nextFloat() < 0.6f) {
            this.chest(n + 3, -4, n2 + 3, false);
        }
        ++this.p.rooms;
    }

    private void bed(int n, int n2, int n3, Direction direction) {
        this.set(n, n2, n3, (BlockState)((BlockState)Blocks.RED_BED.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.BED_PART, (Comparable)BedPart.FOOT));
        int n4 = n + (direction == Direction.EAST ? 1 : (direction == Direction.WEST ? -1 : 0));
        int n5 = n3 + (direction == Direction.SOUTH ? 1 : (direction == Direction.NORTH ? -1 : 0));
        this.set(n4, n2, n5, (BlockState)((BlockState)Blocks.RED_BED.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.BED_PART, (Comparable)BedPart.HEAD));
    }

    private void forge() {
        int n;
        for (int i = -9; i <= 9; ++i) {
            if (!this.isCarved(i, -4, 18)) continue;
            this.set(i, -5, 18, UndergroundCity.s(Blocks.LAVA));
            this.set(i, -6, 18, UndergroundCity.s(Blocks.BLACKSTONE));
            if (i % 4 != 0) continue;
            this.set(i, -4, 18, UndergroundCity.s(Blocks.IRON_BARS));
        }
        int[] nArray = new int[]{-7, -3, 3, 7};
        for (n = 0; n < nArray.length; ++n) {
            int n2;
            int n3 = nArray[n];
            this.set(n3, -4, 15, UndergroundCity.s(Blocks.BLAST_FURNACE));
            this.set(n3 + 1, -4, 15, UndergroundCity.s(Blocks.ANVIL));
            this.set(n3 - 1, -4, 15, n % 2 == 0 ? UndergroundCity.s(Blocks.LAVA_CAULDRON) : UndergroundCity.s(Blocks.SMITHING_TABLE));
            for (n2 = -4; n2 <= -2; ++n2) {
                this.set(n3 - 1, n2, 13, UndergroundCity.s(Blocks.SPRUCE_FENCE));
            }
            for (n2 = -1; n2 <= 1; ++n2) {
                for (int i = 13; i <= 15; ++i) {
                    this.set(n3 + n2, -1, i, UndergroundCity.s(n % 2 == 0 ? Blocks.WEATHERED_CUT_COPPER : Blocks.CUT_COPPER));
                }
            }
        }
        this.set(-9, -4, 12, UndergroundCity.s(Blocks.COAL_BLOCK));
        this.set(-9, -3, 12, UndergroundCity.s(Blocks.COAL_BLOCK));
        this.set(-8, -4, 12, UndergroundCity.s(Blocks.COAL_BLOCK));
        this.set(9, -4, 12, UndergroundCity.s(Blocks.IRON_BLOCK));
        this.set(8, -4, 12, UndergroundCity.s(Blocks.RAW_IRON_BLOCK));
        this.set(9, -3, 12, UndergroundCity.s(Blocks.COPPER_BLOCK));
        this.set(0, -4, 12, UndergroundCity.s(Blocks.GRINDSTONE));
        this.chest(-1, -4, 12, false);
        this.chest(1, -4, 12, false);
        this.set(0, -4, 16, UndergroundCity.s(Blocks.CAMPFIRE));
        for (n = -3; n <= 2 && this.inside(0, n, 16); ++n) {
            for (int[] nArray2 : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                this.set(nArray2[0], n, 16 + nArray2[1], this.stoneBrick());
            }
            this.set(0, n, 16, UndergroundCity.s(Blocks.AIR));
        }
        for (n = -6; n <= 6; n += 6) {
            if (!this.inside(n, 1, 14)) continue;
            this.set(n, 1, 14, UndergroundCity.hangingLantern(false));
        }
    }

    private void lifespring() {
        int n;
        int n2;
        for (n2 = -21; n2 <= -9; ++n2) {
            for (n = -10; n <= 10; ++n) {
                double d = (double)(n2 + 15) / 4.5;
                double d2 = (double)n / 7.5;
                if (d * d + d2 * d2 > 1.0 || !this.isCarved(n2, -4, n)) continue;
                this.set(n2, -5, n, UndergroundCity.s(Blocks.WATER));
                this.set(n2, -6, n, UndergroundCity.s(Blocks.WATER));
                this.set(n2, -7, n, UndergroundCity.s(Blocks.MOSS_BLOCK));
            }
        }
        for (n2 = -20; n2 <= -10; ++n2) {
            if (!this.isCarved(n2, -4, 0)) continue;
            this.set(n2, -5, 0, UndergroundCity.slab(Blocks.SPRUCE_SLAB, true));
        }
        this.set(-15, -5, 0, UndergroundCity.s(Blocks.MOSS_BLOCK));
        this.set(-15, -4, 0, UndergroundCity.s(Blocks.EMERALD_BLOCK));
        this.set(-15, -3, 0, UndergroundCity.litCandle());
        for (n2 = -21; n2 <= -9; ++n2) {
            block4: for (n = -10; n <= 10; ++n) {
                if (!this.isCarved(n2, -4, n) || this.p.blocks.get(Plan.key(n2, -5, n)) == null) continue;
                BlockState blockState = this.p.blocks.get(Plan.key(n2, -5, n));
                if (blockState.is(Blocks.MOSS_BLOCK)) {
                    float f = this.r.nextFloat();
                    if (f < 0.25f) {
                        this.set(n2, -4, n, UndergroundCity.s(Blocks.MOSS_CARPET));
                    } else if (f < 0.32f) {
                        this.set(n2, -4, n, UndergroundCity.s(Blocks.AZALEA));
                    }
                }
                for (int i = 5; i > -1; --i) {
                    if (!this.isCarved(n2, i, n) || this.isCarved(n2, i + 1, n)) continue;
                    if (this.r.nextFloat() < 0.08f) {
                        this.set(n2, i + 1, n, UndergroundCity.s(Blocks.SHROOMLIGHT));
                        continue block4;
                    }
                    if (!(this.r.nextFloat() < 0.15f)) continue block4;
                    this.set(n2, i + 1, n, UndergroundCity.s(Blocks.MOSS_BLOCK));
                    continue block4;
                }
            }
        }
    }

    private void dwellings() {
        int n;
        for (n = 9; n <= 20; ++n) {
            if (!this.isCarved(n, -4, 0)) continue;
            this.set(n, -5, 0, UndergroundCity.s(Blocks.POLISHED_ANDESITE));
        }
        for (n = 10; n <= 20; n += 3) {
            if (!this.isCarved(n, -1, 0)) continue;
            this.set(n, -1, 0, UndergroundCity.hangingLantern(false));
        }
        for (n = 0; n < 2; ++n) {
            int n2 = 10 + n * 5;
            for (int n3 : new int[]{1, -1}) {
                int n4 = n3 > 0 ? 6 : -6;
                int n5 = n3 > 0 ? 3 : -3;
                this.bed(n2, -4, n4, n3 > 0 ? Direction.NORTH : Direction.SOUTH);
                this.set(n2 + 3, -4, n4, UndergroundCity.s(Blocks.BARREL));
                this.set(n2 + 2, -4, n5, UndergroundCity.s(Blocks.RED_CARPET));
                this.set(n2 + 3, -4, n5, UndergroundCity.s(Blocks.CRAFTING_TABLE));
                this.set(n2 + 2, -1, n3 > 0 ? 5 : -5, UndergroundCity.hangingLantern(false));
                if (!(this.r.nextFloat() < 0.5f)) continue;
                this.chest(n2 + 2, -4, n4, false);
            }
        }
    }

    private void treasury() {
        for (int i = -6; i <= 6; ++i) {
            for (int j = -16; j <= -10; ++j) {
                this.set(i, -14, j, (i + j & 1) == 0 ? UndergroundCity.s(Blocks.GOLD_BLOCK) : UndergroundCity.s(Blocks.GILDED_BLACKSTONE));
            }
        }
        for (int[] nArray : new int[][]{{-6, -16}, {6, -16}, {-6, -10}, {6, -10}}) {
            for (int i = -13; i <= -10; ++i) {
                if (!this.inside(nArray[0], i, nArray[1])) continue;
                this.set(nArray[0], i, nArray[1], UndergroundCity.s(Blocks.EMERALD_BLOCK));
            }
        }
        this.chest(-4, -13, -16, true);
        this.chest(-1, -13, -16, true);
        this.chest(1, -13, -16, true);
        this.chest(4, -13, -16, true);
        this.set(-5, -13, -12, UndergroundCity.s(Blocks.RAW_GOLD_BLOCK));
        this.set(-5, -12, -12, UndergroundCity.s(Blocks.GOLD_BLOCK));
        this.set(-4, -13, -12, UndergroundCity.s(Blocks.RAW_GOLD_BLOCK));
        this.set(5, -13, -12, UndergroundCity.s(Blocks.LAPIS_BLOCK));
        this.set(5, -13, -13, UndergroundCity.s(Blocks.EMERALD_BLOCK));
        this.set(4, -13, -12, UndergroundCity.s(Blocks.IRON_BLOCK));
        this.set(0, -10, -13, UndergroundCity.hangingLantern(true));
        for (int i = -1; i <= 1; ++i) {
            this.set(i, -11, -10, UndergroundCity.s(Blocks.IRON_BARS));
        }
        this.set(-2, -13, -10, UndergroundCity.s(Blocks.CHISELED_DEEPSLATE));
        this.set(2, -13, -10, UndergroundCity.s(Blocks.CHISELED_DEEPSLATE));
    }

    private void ancestors() {
        int n;
        for (n = -16; n <= -10; ++n) {
            for (int i = -4; i <= 4; ++i) {
                this.set(n, -14, i, (n + i & 1) == 0 ? UndergroundCity.s(Blocks.DEEPSLATE_TILES) : UndergroundCity.s(Blocks.POLISHED_DEEPSLATE));
            }
        }
        for (n = -15; n <= -11; n += 2) {
            int[] nArray = new int[]{-5, 5};
            int n2 = nArray.length;
            for (int i = 0; i < n2; ++i) {
                int n3 = nArray[i];
                if (!this.inside(n, -12, n3)) continue;
                this.set(n, -13, n3, UndergroundCity.s(Blocks.BONE_BLOCK));
                this.set(n, -12, n3, UndergroundCity.s(Blocks.SKELETON_SKULL));
                this.set(n, -11, n3, UndergroundCity.s(Blocks.CHISELED_DEEPSLATE));
            }
        }
        for (int i : new int[]{-2, 2}) {
            this.set(-14, -13, i, UndergroundCity.s(Blocks.POLISHED_BLACKSTONE_BRICKS));
            this.set(-13, -13, i, UndergroundCity.s(Blocks.POLISHED_BLACKSTONE_BRICKS));
            this.set(-12, -13, i, UndergroundCity.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
            this.set(-14, -12, i, UndergroundCity.litCandle());
        }
        this.set(-16, -13, 0, UndergroundCity.s(Blocks.CHISELED_DEEPSLATE));
        this.set(-16, -12, 0, UndergroundCity.s(Blocks.SKELETON_SKULL));
        this.set(-11, -11, -3, UndergroundCity.s(Blocks.COBWEB));
        this.set(-15, -11, 3, UndergroundCity.s(Blocks.COBWEB));
        this.set(-13, -10, 0, UndergroundCity.hangingLantern(true));
        this.chest(-16, -13, 2, false);
    }

    private void mines() {
        int n;
        int n2;
        for (n2 = 8; n2 <= 14; ++n2) {
            if (!this.isCarved(0, -13, n2)) continue;
            this.set(0, -13, n2, UndergroundCity.s(Blocks.RAIL));
        }
        for (n2 = -10; n2 <= 10; ++n2) {
            if (!this.isCarved(n2, -13, 13)) continue;
            this.set(n2, -13, 13, UndergroundCity.s(Blocks.RAIL));
        }
        for (n2 = -9; n2 <= 9; n2 += 3) {
            for (int n3 : new int[]{12, 14}) {
                for (int i = -13; i <= -12; ++i) {
                    if (!this.isCarved(n2, i, n3)) continue;
                    this.set(n2, i, n3, UndergroundCity.s(Blocks.SPRUCE_FENCE));
                }
            }
            for (n = 12; n <= 14; ++n) {
                if (!this.isCarved(n2, -11, n)) continue;
                this.set(n2, -11, n, UndergroundCity.s(Blocks.SPRUCE_PLANKS));
            }
            if (this.isCarved(n2, -12, 13)) {
                this.set(n2, -12, 13, UndergroundCity.s(Blocks.AIR));
            }
            if (!this.isCarved(n2 + 1, -11, 13)) continue;
            this.set(n2 + 1, -11, 13, UndergroundCity.hangingLantern(false));
        }
        Block[] blockArray = new Block[]{Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_IRON_ORE};
        for (n = -11; n <= 11; ++n) {
            for (int i : new int[]{11, 15}) {
                for (int j = -13; j <= -11; ++j) {
                    if (!this.inside(n, j, i) || this.isCarved(n, j, i) || !(this.r.nextFloat() < 0.1f)) continue;
                    this.set(n, j, i, UndergroundCity.s(blockArray[this.r.nextInt(blockArray.length)]));
                }
            }
        }
        this.chest(-10, -13, 12, false);
    }

    private void stairsDecor() {
        int n;
        int n2;
        for (n2 = 1; n2 <= 9; ++n2) {
            for (n = -9; n <= -7; ++n) {
                this.set(4 + n2, 5 - n2, n, UndergroundCity.stairs(Blocks.STONE_BRICK_STAIRS, Direction.WEST, false));
            }
        }
        for (n2 = 1; n2 <= 9; ++n2) {
            for (n = 7; n <= 9; ++n) {
                this.set(5 + n2, -4 - n2, n, UndergroundCity.stairs(Blocks.DEEPSLATE_TILE_STAIRS, Direction.WEST, false));
            }
        }
        for (n2 = 1; n2 <= 9; n2 += 3) {
            this.set(4 + n2, 5 - n2 + 3, -8, UndergroundCity.hangingLantern(false));
        }
        for (n2 = 1; n2 <= 9; n2 += 3) {
            this.set(5 + n2, -4 - n2 + 3, 8, UndergroundCity.hangingLantern(true));
        }
    }

    private void mushroomGrove() {
        int n;
        int n2;
        for (int i = -21; i <= -9; ++i) {
            block1: for (n2 = -10; n2 <= 10; ++n2) {
                if (!this.isCarved(i, -4, n2) || this.isCarved(i, -5, n2)) continue;
                this.set(i, -5, n2, this.r.nextFloat() < 0.8f ? UndergroundCity.s(Blocks.MYCELIUM) : UndergroundCity.s(Blocks.PODZOL));
                float f = this.r.nextFloat();
                if (f < 0.1f) {
                    this.set(i, -4, n2, UndergroundCity.s(Blocks.RED_MUSHROOM));
                } else if (f < 0.2f) {
                    this.set(i, -4, n2, UndergroundCity.s(Blocks.BROWN_MUSHROOM));
                }
                for (n = 5; n > -1; --n) {
                    if (!this.isCarved(i, n, n2) || this.isCarved(i, n + 1, n2)) continue;
                    if (!(this.r.nextFloat() < 0.1f)) continue block1;
                    this.set(i, n + 1, n2, UndergroundCity.s(Blocks.SHROOMLIGHT));
                    continue block1;
                }
            }
        }
        int[][] nArrayArray = new int[][]{{-15, -6}, {-17, 2}, {-13, 6}, {-14, -1}};
        for (n2 = 0; n2 < nArrayArray.length; ++n2) {
            int n3;
            int n4 = nArrayArray[n2][0];
            n = nArrayArray[n2][1];
            int n5 = 3 + this.r.nextInt(3);
            boolean bl = (n2 + this.v3) % 2 == 0;
            for (n3 = -4; n3 < -4 + n5; ++n3) {
                if (!this.isCarved(n4, n3, n)) continue;
                this.set(n4, n3, n, UndergroundCity.s(Blocks.MUSHROOM_STEM));
            }
            n3 = -4 + n5;
            int n6 = bl ? 2 : 3;
            for (int i = -n6; i <= n6; ++i) {
                for (int j = -n6; j <= n6; ++j) {
                    int n7;
                    if (i * i + j * j > n6 * n6 + 1) continue;
                    int n8 = n7 = bl && Math.max(Math.abs(i), Math.abs(j)) == n6 ? n3 - 1 : n3;
                    if (!this.isCarved(n4 + i, n7, n + j)) continue;
                    this.set(n4 + i, n7, n + j, UndergroundCity.s(bl ? Blocks.RED_MUSHROOM_BLOCK : Blocks.BROWN_MUSHROOM_BLOCK));
                }
            }
        }
        this.set(-15, -4, 0, UndergroundCity.s(Blocks.CAULDRON));
        this.chest(-19, -4, -2, false);
    }

    private void crystalGrotto() {
        int n;
        int n2;
        for (n2 = -22; n2 <= -8; ++n2) {
            for (n = -11; n <= 11; ++n) {
                for (int i = -5; i <= 6; ++i) {
                    boolean bl;
                    if (this.isCarved(n2, i, n) || !this.inside(n2, i, n)) continue;
                    boolean bl2 = bl = this.isCarved(n2 + 1, i, n) || this.isCarved(n2 - 1, i, n) || this.isCarved(n2, i + 1, n) || this.isCarved(n2, i - 1, n) || this.isCarved(n2, i, n + 1) || this.isCarved(n2, i, n - 1);
                    if (!bl || n2 > -9 && Math.abs(n) < 3) continue;
                    float f = this.r.nextFloat();
                    this.set(n2, i, n, f < 0.45f ? UndergroundCity.s(Blocks.AMETHYST_BLOCK) : (f < 0.8f ? UndergroundCity.s(Blocks.CALCITE) : UndergroundCity.s(Blocks.SMOOTH_BASALT)));
                }
            }
        }
        for (n2 = -21; n2 <= -9; ++n2) {
            for (n = -10; n <= 10; ++n) {
                if (!this.isCarved(n2, -4, n) || this.isCarved(n2, -5, n)) continue;
                double d = (double)(n2 + 15) / 2.5;
                double d2 = (double)n / 3.0;
                if (d * d + d2 * d2 <= 1.0) {
                    this.set(n2, -5, n, UndergroundCity.s(Blocks.WATER));
                    continue;
                }
                float f = this.r.nextFloat();
                if (f < 0.08f) {
                    this.set(n2, -4, n, UndergroundCity.s(Blocks.AMETHYST_CLUSTER));
                    continue;
                }
                if (f < 0.14f) {
                    this.set(n2, -4, n, UndergroundCity.s(Blocks.LARGE_AMETHYST_BUD));
                    continue;
                }
                if (!(f < 0.18f)) continue;
                this.set(n2, -4, n, UndergroundCity.s(Blocks.MEDIUM_AMETHYST_BUD));
            }
        }
        this.set(-15, 2, 0, UndergroundCity.hangingLantern(true));
        this.chest(-19, -4, 3, false);
    }

    private void prison() {
        for (int i = -16; i <= -10; ++i) {
            for (int j = -4; j <= 4; ++j) {
                this.set(i, -14, j, (i * 3 + j) % 4 == 0 ? UndergroundCity.s(Blocks.CRACKED_DEEPSLATE_TILES) : UndergroundCity.s(Blocks.DEEPSLATE_TILES));
            }
        }
        for (int n : new int[]{-1, 1}) {
            int n2 = n * 2;
            for (int i = -16; i <= -10; ++i) {
                int n3 = i == -14 || i == -11 ? 1 : 0;
                for (int j = -13; j <= -10; ++j) {
                    if (i == -13 || i == -10 && j > -11) {
                        this.set(i, j, n2, UndergroundCity.s(Blocks.POLISHED_DEEPSLATE));
                        continue;
                    }
                    if (n3 != 0 && j < -11) continue;
                    this.set(i, j, n2, UndergroundCity.s(Blocks.IRON_BARS));
                }
            }
            for (int n4 : new int[]{-15, -11}) {
                int n5 = n * 4;
                this.set(n4, -10, n5, UndergroundCity.s(Blocks.CHAIN));
                this.set(n4, -11, n5, UndergroundCity.s(Blocks.CHAIN));
                if (this.r.nextFloat() < 0.7f) {
                    this.set(n4, -13, n5, UndergroundCity.s(Blocks.SKELETON_SKULL));
                }
                if (this.r.nextFloat() < 0.6f) {
                    this.set(n4 + 1, -13, n5, UndergroundCity.s(Blocks.HAY_BLOCK));
                }
                this.set(n4 + 1, -10, n * 3, UndergroundCity.s(Blocks.COBWEB));
            }
        }
        this.set(-13, -13, 0, UndergroundCity.s(Blocks.SPRUCE_PLANKS));
        this.set(-13, -12, 0, UndergroundCity.litCandle());
        this.set(-12, -13, 0, UndergroundCity.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
        this.set(-13, -10, 0, UndergroundCity.hangingLantern(true));
        this.chest(-16, -13, 0, false);
    }

    private void cellar() {
        int n;
        for (n = -16; n <= -10; ++n) {
            for (int i = -4; i <= 4; ++i) {
                this.set(n, -14, i, (n + i & 1) == 0 ? UndergroundCity.s(Blocks.SPRUCE_PLANKS) : UndergroundCity.s(Blocks.DARK_OAK_PLANKS));
            }
        }
        for (n = -16; n <= -10; ++n) {
            for (int n2 : new int[]{-4, 4}) {
                for (int i = -13; i <= -13 + (n % 2 == 0 ? 2 : 1); ++i) {
                    this.set(n, i, n2, UndergroundCity.s(Blocks.BARREL));
                }
            }
        }
        for (n = -2; n <= 2; n += 2) {
            this.set(-16, -13, n, UndergroundCity.s(Blocks.CAULDRON));
            this.set(-16, -12, n, UndergroundCity.s(Blocks.SPRUCE_TRAPDOOR));
        }
        this.set(-13, -13, -1, UndergroundCity.s(Blocks.BREWING_STAND));
        this.set(-13, -13, 1, UndergroundCity.s(Blocks.BREWING_STAND));
        this.set(-13, -13, 0, UndergroundCity.s(Blocks.SPRUCE_PLANKS));
        for (n = -15; n <= -11; n += 4) {
            this.set(n, -10, 0, UndergroundCity.hangingLantern(false));
        }
        this.chest(-11, -13, -3, false);
        this.chest(-11, -13, 3, false);
    }

    private void chest(int n, int n2, int n3, boolean bl) {
        if (!this.inside(n, n2, n3)) {
            return;
        }
        this.set(n, n2, n3, UndergroundCity.s(Blocks.CHEST));
        this.p.chests.add(new int[]{n, n2, n3});
        this.p.richChests.add(bl);
    }

    public static final class Plan {
        public final Map<Long, BlockState> blocks = new HashMap<Long, BlockState>();
        public final List<int[]> chests = new ArrayList<int[]>();
        public final List<Boolean> richChests = new ArrayList<Boolean>();
        public int rooms;
        public String theme = "stone";
        public String state = "intact";
        public String west2 = "lifespring";
        public String west3 = "ancestors";

        public static long key(int n, int n2, int n3) {
            return (long)(n + 512) << 40 | (long)(n2 + 512) << 20 | (long)(n3 + 512);
        }

        public static int kx(long l) {
            return (int)(l >> 40 & 0xFFFFFL) - 512;
        }

        public static int ky(long l) {
            return (int)(l >> 20 & 0xFFFFFL) - 512;
        }

        public static int kz(long l) {
            return (int)(l & 0xFFFFFL) - 512;
        }
    }

    public record Result(int levels, int rooms, int chests) {
    }
}
