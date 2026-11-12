/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BedPart
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.coaldust.meteor.underground;

import com.coaldust.meteor.gothic.Gothic;
import com.coaldust.meteor.underground.Connect;
import com.coaldust.meteor.underground.Spires;
import com.coaldust.meteor.underground.StructureDecor;
import com.coaldust.meteor.underground.UndergroundCity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public final class DarkCities {
    private final int R;
    private final double inner;
    private final RandomSource r;
    private final UndergroundCity.Plan p = new UndergroundCity.Plan();
    private final Gothic g;
    private int rooms;
    private final HashSet<Long> ledge = new HashSet();
    private static final int CANAL = 15;
    private double cph;
    private int cbot;

    private DarkCities(int n, RandomSource randomSource, Gothic.Pal pal) {
        this.R = n;
        this.inner = ((double)n - 2.5) * ((double)n - 2.5);
        this.r = randomSource;
        this.g = new Gothic(this.p, randomSource, pal);
        this.g.decay = 0.2f + randomSource.nextFloat() * 0.3f;
    }

    public static UndergroundCity.Plan plan(int n, RandomSource randomSource, Kind kind) {
        Gothic.Pal pal = switch (kind.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                if (randomSource.nextBoolean()) {
                    yield Gothic.BONE;
                }
                yield Gothic.OBSIDIAN;
            }
            case 1 -> {
                if (randomSource.nextBoolean()) {
                    yield Gothic.SCULK;
                }
                yield Gothic.OBSIDIAN;
            }
            case 2 -> Gothic.ASH;
            case 3 -> {
                if (randomSource.nextBoolean()) {
                    yield Gothic.BLOOD;
                }
                yield Gothic.OBSIDIAN;
            }
            case 4 -> {
                if (randomSource.nextBoolean()) {
                    yield Gothic.OBSIDIAN;
                }
                yield Gothic.DWARF_DEEP;
            }
            case 5 -> {
                if (randomSource.nextBoolean()) {
                    yield Gothic.DWARF;
                }
                yield Gothic.ASH;
            }
            case 6 -> randomSource.nextBoolean() ? Gothic.BONE : Gothic.OBSIDIAN;
        };
        DarkCities darkCities = new DarkCities(n, randomSource, pal);
        darkCities.body(kind);
        switch (kind.ordinal()) {
            case 0: {
                darkCities.necropolis();
                break;
            }
            case 1: {
                darkCities.abyss();
                break;
            }
            case 2: {
                darkCities.nest();
                break;
            }
            case 3: {
                darkCities.forge();
                break;
            }
            case 4: {
                darkCities.metropolis();
                break;
            }
            case 5: {
                darkCities.canyon();
                break;
            }
            case 6: {
                darkCities.rooms += new Spires(darkCities.g, randomSource, n, darkCities.inner).build();
            }
        }
        if (kind != Kind.SPIRES) {
            Connect.run(darkCities.p, randomSource, pal.floor(), pal.stairs(), 6);
        }
        StructureDecor.unfloat(darkCities.p, 60);
        darkCities.p.theme = kind.id;
        darkCities.p.state = "cursed";
        darkCities.p.rooms = darkCities.rooms;
        StructureDecor.cheapen(darkCities.p, randomSource);
        StructureDecor.transform(darkCities.p, randomSource.nextInt(8));
        return darkCities.p;
    }

    private boolean onLedge(int n, int n2, int n3) {
        return this.ledge.contains(UndergroundCity.Plan.key(n, n2, n3));
    }

    private boolean inside(int n, int n2, int n3) {
        return (double)(n * n + n2 * n2 + n3 * n3) <= this.inner;
    }

    private void carve(int n, int n2, int n3) {
        if (this.inside(n, n2, n3)) {
            this.g.air(n, n2, n3);
        }
    }

    private boolean cave(int n, int n2, int n3) {
        return this.inside(n, n2, n3) && this.g.get(n, n2, n3) != null && this.g.get(n, n2, n3).isAir();
    }

    private void carveBox(int n, int n2, int n3, int n4, int n5, int n6) {
        for (int i = n; i <= n4; ++i) {
            for (int j = n2; j <= n5; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    this.carve(i, j, k);
                }
            }
        }
    }

    private void body(Kind kind) {
        for (int i = -this.R; i <= this.R; ++i) {
            for (int j = -this.R; j <= this.R; ++j) {
                for (int k = -this.R; k <= this.R; ++k) {
                    double d = Math.sqrt(i * i + j * j + k * k);
                    if (d > (double)this.R) continue;
                    float f = this.r.nextFloat();
                    BlockState blockState = d > (double)this.R - 1.5 ? Gothic.s(f < 0.45f ? Blocks.BLACKSTONE : (f < 0.75f ? Blocks.BASALT : (f < 0.93f ? Blocks.OBSIDIAN : Blocks.CRYING_OBSIDIAN))) : (kind == Kind.NEST ? Gothic.s(f < 0.5f ? Blocks.TUFF : (f < 0.8f ? Blocks.DEEPSLATE : (f < 0.97f ? Blocks.CALCITE : Blocks.BONE_BLOCK))) : (kind == Kind.FORGE ? Gothic.s(f < 0.45f ? Blocks.BLACKSTONE : (f < 0.75f ? Blocks.BASALT : (f < 0.96f ? Blocks.SMOOTH_BASALT : Blocks.MAGMA_BLOCK))) : Gothic.s(f < 0.6f ? Blocks.DEEPSLATE : (f < 0.85f ? Blocks.BLACKSTONE : (f < 0.993f ? Blocks.TUFF : (j < -8 ? Blocks.DEEPSLATE_COAL_ORE : Blocks.COAL_ORE))))));
                    this.g.set(i, j, k, blockState);
                }
            }
        }
    }

    private void entrance(int n, int n2, int n3) {
        int n4;
        int n5;
        int n6;
        int n7;
        int n8 = (int)Math.floor(Math.sqrt(Math.max(0, this.R * this.R - n * n - n2 * n2)));
        for (n7 = n - 3; n7 <= n + 3; ++n7) {
            for (int i = n2 - 3; i <= n2 + 3; ++i) {
                for (n6 = n3; n6 <= n8 + 1; ++n6) {
                    int n9 = n5 = Math.abs(n7 - n) == 3 || Math.abs(i - n2) == 3 ? 1 : 0;
                    if (n5 != 0) {
                        if (n6 <= n3 - 1 || !this.inside(n7, n6, i)) continue;
                        this.g.set(n7, n6, i, this.g.brick());
                        continue;
                    }
                    this.g.air(n7, n6, i);
                }
            }
        }
        this.g.spiralStair(n, n2, n8 - 1, n3, 1);
        for (n7 = n - 3; n7 <= n + 3; ++n7) {
            int[] nArray = new int[]{n2 - 3, n2 + 3};
            n6 = nArray.length;
            for (n5 = 0; n5 < n6; ++n5) {
                n4 = nArray[n5];
                this.g.set(n7, n8 + 1, n4, Gothic.s(this.g.pal.polished()));
            }
        }
        for (n7 = n2 - 3; n7 <= n2 + 3; ++n7) {
            int[] nArray = new int[]{n - 3, n + 3};
            n6 = nArray.length;
            for (n5 = 0; n5 < n6; ++n5) {
                n4 = nArray[n5];
                this.g.set(n4, n8 + 1, n7, Gothic.s(this.g.pal.polished()));
            }
        }
        for (int[] nArray : new int[][]{{n - 3, n2 - 3}, {n + 3, n2 - 3}, {n - 3, n2 + 3}, {n + 3, n2 + 3}}) {
            this.g.set(nArray[0], n8 + 2, nArray[1], Gothic.s(this.g.pal.wall()));
            this.g.set(nArray[0], n8 + 3, nArray[1], Gothic.s(Blocks.WITHER_SKELETON_SKULL));
        }
    }

    private void townHouse(int n, int n2, int n3, int n4, int n5, int n6, boolean bl) {
        int n7;
        int n8;
        int n9;
        int n10 = n4 / 2;
        int n11 = n5 / 2;
        int n12 = bl ? n - n10 : n - n11;
        int n13 = bl ? n + n10 : n + n11;
        int n14 = bl ? n3 - n11 : n3 - n10;
        int n15 = bl ? n3 + n11 : n3 + n10;
        int n16 = n6 * 4;
        for (n9 = n12; n9 <= n13; ++n9) {
            for (n8 = n14; n8 <= n15; ++n8) {
                n7 = n9 == n12 || n9 == n13 || n8 == n14 || n8 == n15 ? 1 : 0;
                boolean bl2 = !(n9 != n12 && n9 != n13 || n8 != n14 && n8 != n15);
                this.g.set(n9, n2 - 1, n8, Gothic.s(this.g.pal.floor()));
                for (int i = n2; i < n2 + n16; ++i) {
                    boolean bl3;
                    int n17 = (i - n2) % 4;
                    if (n7 == 0) {
                        this.g.set(n9, i, n8, n17 == 0 && i > n2 ? Gothic.s(Blocks.SPRUCE_PLANKS) : Gothic.s(Blocks.AIR));
                        continue;
                    }
                    boolean bl4 = bl3 = n17 == 2 && !bl2 && (n9 + n8 & 1) == 0;
                    this.g.set(n9, i, n8, bl2 ? Gothic.s(this.g.pal.polished()) : (bl3 ? Gothic.s(this.r.nextFloat() < 0.7f ? Blocks.ORANGE_STAINED_GLASS_PANE : this.g.pal.glass()) : (n17 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick())));
                }
            }
        }
        this.g.gable(n12, n14, n13, n15, n2 + n16, bl);
        n9 = bl ? n : n13;
        n8 = bl ? n15 : n3;
        this.g.air(n9, n2, n8);
        this.g.air(n9, n2 + 1, n8);
        this.g.set(n9 + (bl ? 0 : 1), n2 + 2, n8 + (bl ? 1 : 0), Blocks.LANTERN.defaultBlockState());
        for (n7 = 0; n7 < n6; ++n7) {
            this.g.set(n, n2 + n7 * 4 + 3, n3, Gothic.s(Blocks.SHROOMLIGHT));
            if (n7 != 0) continue;
            this.g.set(n12 + 1, n2, n14 + 1, Gothic.s(Blocks.BARREL));
        }
        if (this.r.nextFloat() < 0.35f) {
            this.g.chest(n13 - 1, n2, n15 - 1, false);
        }
        ++this.rooms;
    }

    private void stoneBridge(int n, int n2, int n3, int n4, int n5, int n6) {
        int n7 = n4 - n;
        int n8 = n5 - n3;
        int n9 = Math.max(Math.abs(n7), Math.abs(n8));
        if (n9 == 0) {
            return;
        }
        boolean bl = Math.abs(n7) >= Math.abs(n8);
        for (int i = 0; i <= n9; ++i) {
            int n10;
            int n11 = n + Math.round((float)n7 * (float)i / (float)n9);
            int n12 = n3 + Math.round((float)n8 * (float)i / (float)n9);
            for (n10 = -n6 - 1; n10 <= n6 + 1; ++n10) {
                int n13 = bl ? n11 : n11 + n10;
                int n14 = bl ? n12 + n10 : n12;
                this.g.set(n13, n2 - 1, n14, Math.abs(n10) <= n6 ? this.g.floorTile(n13, n14) : Gothic.s(this.g.pal.polished()));
                this.g.set(n13, n2 - 2, n14, Math.abs(n10) == n6 + 1 || i % 6 == 0 ? Gothic.s(this.g.pal.chiseled()) : Gothic.slab(this.g.pal.slab(), true));
                if (Math.abs(n10) <= n6) {
                    for (int j = 0; j < 3; ++j) {
                        this.g.air(n13, n2 + j, n14);
                    }
                    continue;
                }
                this.g.set(n13, n2, n14, i % 6 == 0 ? Gothic.s(this.g.pal.chiseled()) : Gothic.s(this.g.pal.wall()));
                if (i % 6 != 0) continue;
                this.g.set(n13, n2 + 1, n14, Blocks.LANTERN.defaultBlockState());
            }
            if (i % 12 == 6) {
                for (n10 = 1; n10 <= 3; ++n10) {
                    this.g.set(n11, n2 - 2 - n10, n12, Gothic.s(Blocks.CHAIN));
                }
            }
            if (i % 12 != 6) continue;
            this.g.set(n11, n2 - 6, n12, (BlockState)Blocks.LANTERN.defaultBlockState().setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(true)));
        }
    }

    private void ceilingLights(int n, int n2, int n3, int n4) {
        for (int i = 0; i < n4; ++i) {
            int n5;
            int n6 = -n + this.r.nextInt(2 * n + 1);
            int n7 = -n + this.r.nextInt(2 * n + 1);
            for (n5 = n2; n5 < n3 && this.g.isAir(n6, n5, n7) && this.g.get(n6, n5, n7) != null; ++n5) {
            }
            if (n5 >= n3 || n5 <= n2 + 2) continue;
            if (this.r.nextFloat() < 0.6f) {
                this.g.set(n6, n5, n7, Gothic.s(this.r.nextFloat() < 0.6f ? Blocks.SHROOMLIGHT : Blocks.GLOWSTONE));
                continue;
            }
            int n8 = 2 + this.r.nextInt(6);
            for (int j = 1; j <= n8; ++j) {
                if (!this.g.isAir(n6, n5 - j, n7)) continue;
                this.g.set(n6, n5 - j, n7, Gothic.s(j == n8 ? Blocks.DRIPSTONE_BLOCK : Blocks.DRIPSTONE_BLOCK));
            }
        }
    }

    private void cavern(int n, int n2, int n3, int n4) {
        int n5;
        int n6;
        double d = this.r.nextDouble() * 6.0;
        for (n6 = -n - 2; n6 <= n + 2; ++n6) {
            for (n5 = -n - 2; n5 <= n + 2; ++n5) {
                for (int i = n4; i <= n3 + n2 + 2; ++i) {
                    double d2 = Math.atan2(n5, n6);
                    double d3 = (double)(n6 * n6 + n5 * n5) / (double)(n * n) * (1.0 - 0.08 * Math.sin(d2 * 5.0 + d)) + (double)((i - n3) * (i - n3)) / (double)(n2 * n2);
                    if (!(d3 <= 1.0)) continue;
                    this.carve(n6, i, n5);
                }
            }
        }
        for (n6 = -n - 2; n6 <= n + 2; ++n6) {
            for (n5 = -n - 2; n5 <= n + 2; ++n5) {
                if (this.g.isAir(n6, n4 - 1, n5) || !this.g.isAir(n6, n4, n5) || this.g.get(n6, n4, n5) == null) continue;
                this.g.set(n6, n4 - 1, n5, Gothic.s(Blocks.DEEPSLATE_TILES));
            }
        }
    }

    private void metropolis() {
        int n;
        int n2;
        int n3;
        int[][] nArrayArray;
        int n4;
        int n5 = -16;
        int n6 = -8;
        int n7 = this.R - 3;
        int n8 = 21;
        this.cavern(n7, n8, n6, n5);
        for (int i = -n7; i <= n7; ++i) {
            for (int j = -n7; j <= n7; ++j) {
                if (!this.g.isAir(i, n5, j) || this.g.get(i, n5, j) == null) continue;
                int n9 = n4 = Math.floorMod(i, 10) <= 1 || Math.floorMod(j, 10) <= 1 ? 1 : 0;
                this.g.set(i, n5 - 1, j, n4 != 0 ? Gothic.s(this.r.nextFloat() < 0.85f ? this.g.pal.floor() : Blocks.COBBLED_DEEPSLATE) : Gothic.s(Blocks.DEEPSLATE_TILES));
                if (n4 == 0 || Math.floorMod(i, 10) != 0 || Math.floorMod(j, 10) != 5) continue;
                this.g.set(i, n5, j, Gothic.s(this.g.pal.wall()));
                this.g.set(i, n5 + 1, j, Gothic.s(this.g.pal.wall()));
                this.g.set(i, n5 + 2, j, Blocks.LANTERN.defaultBlockState());
            }
        }
        int[][] nArrayArray2 = nArrayArray = new int[][]{{-12, -10}, {11, -12}, {-10, 12}, {13, 9}, {0, 0}};
        n4 = nArrayArray2.length;
        for (n3 = 0; n3 < n4; ++n3) {
            int n10;
            int[] nArray = nArrayArray2[n3];
            for (n10 = n5; n10 <= n6 + n8; ++n10) {
                double d = (double)(n10 - n5) / (double)(n6 + n8 - n5);
                double d2 = 3.2 + Math.pow(d, 3.0) * 7.0;
                boolean bl = false;
                for (int i = nArray[0] - 11; i <= nArray[0] + 11; ++i) {
                    for (int j = nArray[1] - 11; j <= nArray[1] + 11; ++j) {
                        boolean bl2;
                        double d3 = Math.sqrt((i - nArray[0]) * (i - nArray[0]) + (j - nArray[1]) * (j - nArray[1]));
                        if (d3 > d2 || !this.inside(i, n10, j)) continue;
                        bl = true;
                        boolean bl3 = d3 > d2 - 1.2;
                        boolean bl4 = bl2 = bl3 && d < 0.6 && (n10 - n5) % 5 == 2 && (Math.abs(i - nArray[0]) <= 0 || Math.abs(j - nArray[1]) <= 0);
                        this.g.set(i, n10, j, bl2 ? Gothic.s(Blocks.ORANGE_STAINED_GLASS_PANE) : (bl3 ? ((n10 - n5) % 5 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick()) : (d < 0.6 ? Gothic.s(Blocks.AIR) : this.g.brick())));
                    }
                }
                if (!bl) break;
            }
            for (n10 = n5 + 4; n10 < n5 + 18; n10 += 5) {
                this.g.set(nArray[0], n10, nArray[1], Gothic.s(Blocks.SHROOMLIGHT));
            }
        }
        this.g.spiralStair(0, 0, 2, n5, 1);
        for (n2 = n5; n2 <= n5 + 2; ++n2) {
            for (n4 = 2; n4 <= 3; ++n4) {
                this.g.air(n4, n2, 0);
            }
        }
        n2 = 3;
        for (int[] nArray : nArrayArray) {
            if (nArray[0] == 0 && nArray[1] == 0) continue;
            this.stoneBridge(0, n2, 0, nArray[0], nArray[1], 1);
        }
        this.stoneBridge(-12, n2, -10, -12, -n7 + 1, 1);
        this.stoneBridge(13, n2, 9, n7 - 1, 9, 1);
        int[][] nArrayArray3 = nArrayArray;
        n3 = nArrayArray3.length;
        for (n = 0; n < n3; ++n) {
            int[] nArray = nArrayArray3[n];
            for (int i = nArray[0] - 2; i <= nArray[0] + 2; ++i) {
                for (int j = nArray[1] - 2; j <= nArray[1] + 2; ++j) {
                    this.g.set(i, n2 - 1, j, Gothic.s(this.g.pal.floor()));
                    for (int k = 0; k < 3; ++k) {
                        this.g.air(i, n2 + k, j);
                    }
                }
            }
        }
        this.metroInfrastructure(n5, n7, nArrayArray, n2);
        for (int i = -n7; i <= n7; i += 10) {
            for (n3 = -n7; n3 <= n7; n3 += 10) {
                n = i + 5;
                int n11 = n3 + 5;
                boolean bl = false;
                for (int[] nArray : nArrayArray) {
                    if (Math.abs(n - nArray[0]) >= 7 || Math.abs(n11 - nArray[1]) >= 7) continue;
                    bl = true;
                }
                if (Math.abs(n) < 11 && Math.abs(n11) < 11 || Math.abs(n11 - 15) < 5 || bl || !this.cave(n, n5, n11) || !this.cave(n + 4, n5 + 7, n11 + 4) || !this.cave(n - 4, n5 + 7, n11 - 4) || !this.cave(n + 4, n5, n11 - 4) || !this.cave(n - 4, n5, n11 + 4)) continue;
                int n12 = this.cave(n + 4, n5 + 12, n11 + 4) && this.cave(n - 4, n5 + 12, n11 - 4) ? 3 : 1;
                float f = this.r.nextFloat();
                if (f < 0.12f) {
                    this.domeHall(n, n5, n11);
                    continue;
                }
                if (f < 0.24f) {
                    this.garden(n, n5, n11);
                    continue;
                }
                int n13 = Math.min(n12, 2 + this.r.nextInt(2));
                if (this.r.nextBoolean()) {
                    this.townHouse(n, n5, n11 - 2, 7, 3, n13, true);
                    this.townHouse(n, n5, n11 + 2, 7, 3, Math.max(1, n13 - 1 + this.r.nextInt(2)), true);
                    continue;
                }
                this.townHouse(n, n5, n11, 7, 7, n13, this.r.nextBoolean());
            }
        }
        this.ceilingLights(n7, n5 + 10, n6 + n8 + 3, 160);
        this.entrance(-12, -n7 + 4, n2);
        this.g.chest(1, n5, 2, true);
    }

    private void metroInfrastructure(int n, int n2, int[][] nArray, int n3) {
        int n4;
        int n5;
        int n6;
        int n7;
        int n82;
        int n9;
        int n10;
        for (n10 = -n2; n10 <= n2; ++n10) {
            for (int i = -2; i <= 2; ++i) {
                int[][] nArrayArray = new int[][]{{n10, i}, {i, n10}};
                n9 = nArrayArray.length;
                for (int j = 0; j < n9; ++j) {
                    int[] nArray2 = nArrayArray[j];
                    n82 = nArray2[0];
                    n7 = nArray2[1];
                    if (!this.cave(n82, n, n7)) continue;
                    this.g.set(n82, n - 1, n7, Math.abs(i) == 2 ? Gothic.s(this.g.pal.polished()) : Gothic.s(this.g.pal.floor()));
                }
                if (this.cave(n10, n, -1) && Math.abs(n10) > 6) {
                    this.g.set(n10, n, -1, Gothic.s(Blocks.RAIL));
                }
                if (!this.cave(-1, n, n10) || Math.abs(n10) <= 6) continue;
                this.g.set(-1, n, n10, Gothic.s(Blocks.RAIL));
            }
        }
        for (n10 = -n2; n10 <= n2; n10 += 6) {
            int[][] nArrayArray = new int[][]{{n10, 3}, {n10, -3}, {3, n10}, {-3, n10}};
            int n11 = nArrayArray.length;
            for (n9 = 0; n9 < n11; ++n9) {
                int[] nArray3 = nArrayArray[n9];
                int n12 = nArray3[0];
                n82 = nArray3[1];
                if (!this.cave(n12, n, n82) || !this.cave(n12, n + 4, n82) || Math.abs(n12) < 9 && Math.abs(n82) < 9) continue;
                this.g.set(n12, n, n82, Gothic.s(this.g.pal.chiseled()));
                for (n7 = 1; n7 <= 3; ++n7) {
                    this.g.set(n12, n + n7, n82, Gothic.s(this.g.pal.wall()));
                }
                this.g.set(n12, n + 4, n82, Gothic.s(Blocks.LANTERN));
            }
        }
        for (n10 = -10; n10 <= 10; ++n10) {
            for (int i = -10; i <= 10; ++i) {
                double d = Math.sqrt(n10 * n10 + i * i);
                if (d > 10.0 || !this.cave(n10, n, i)) continue;
                this.g.set(n10, n - 1, i, (int)d % 3 == 0 ? Gothic.s(this.g.pal.polished()) : Gothic.s(this.g.pal.floor()));
            }
        }
        for (n10 = 0; n10 < 4; ++n10) {
            int n13 = (n10 % 2 == 0 ? 1 : -1) * 7;
            int n14 = (n10 < 2 ? 1 : -1) * 7;
            for (n9 = n13 - 1; n9 <= n13 + 1; ++n9) {
                for (int i = n14 - 1; i <= n14 + 1; ++i) {
                    boolean bl = n9 == n13 && i == n14;
                    this.g.set(n9, n - 1, i, bl ? Gothic.s(this.g.pal.chiseled()) : Gothic.s(Blocks.WATER));
                    this.g.set(n9, n - 2, i, Gothic.s(this.g.pal.polished()));
                    if (bl) continue;
                    this.g.set(n9, n, i, Gothic.s(Blocks.AIR));
                }
            }
            for (n9 = n13 - 2; n9 <= n13 + 2; ++n9) {
                for (int i = n14 - 2; i <= n14 + 2; ++i) {
                    if (Math.abs(n9 - n13) != 2 && Math.abs(i - n14) != 2) continue;
                    this.g.set(n9, n, i, Gothic.slab(this.g.pal.slab(), false));
                }
            }
            this.g.set(n13, n, n14, Gothic.s(this.g.pal.wall()));
            this.g.set(n13, n + 1, n14, Gothic.s(this.g.pal.wall()));
            this.g.set(n13, n + 2, n14, Gothic.s(Blocks.SHROOMLIGHT));
        }
        for (n10 = 0; n10 < 8; ++n10) {
            int n15;
            double d = (double)n10 * Math.PI / 4.0 + 0.39269908169872414;
            n9 = (int)Math.round(Math.cos(d) * 5.0);
            if (!this.cave(n9, n, n15 = (int)Math.round(Math.sin(d) * 5.0))) continue;
            this.g.set(n9, n, n15, Gothic.stairs(this.g.pal.stairs(), DarkCities.dirOf(n9, n15), false));
        }
        int[][] nArrayArray = new int[][]{{9, 4}, {9, -5}, {-9, 4}, {-9, -5}, {4, 9}, {-5, 9}, {4, -9}, {-5, -9}};
        Block[] blockArray = new Block[]{Blocks.RED_WOOL, Blocks.ORANGE_WOOL, Blocks.YELLOW_WOOL, Blocks.PURPLE_WOOL};
        for (n6 = 0; n6 < nArrayArray.length; ++n6) {
            n9 = nArrayArray[n6][0];
            int n16 = nArrayArray[n6][1];
            if (!this.cave(n9, n + 3, n16)) continue;
            int[][] nArrayArray2 = new int[][]{{0, 0}, {2, 0}, {0, 2}, {2, 2}};
            n82 = nArrayArray2.length;
            for (n7 = 0; n7 < n82; ++n7) {
                int[] nArray4 = nArrayArray2[n7];
                for (n5 = 0; n5 < 3; ++n5) {
                    this.g.set(n9 + nArray4[0], n + n5, n16 + nArray4[1], Gothic.s(Blocks.SPRUCE_FENCE));
                }
            }
            for (int i = n9; i <= n9 + 2; ++i) {
                for (n82 = n16; n82 <= n16 + 2; ++n82) {
                    this.g.set(i, n + 3, n82, Gothic.s(blockArray[n6 % blockArray.length]));
                }
            }
            this.g.set(n9 + 1, n, n16 + 1, Gothic.s(n6 % 2 == 0 ? Blocks.BARREL : Blocks.CRAFTING_TABLE));
            this.g.set(n9 + 1, n, n16, Gothic.s(Blocks.SPRUCE_SLAB));
            if (n6 % 3 != 0) continue;
            this.g.chest(n9 + 1, n, n16 + 2, false);
        }
        for (n6 = -n2; n6 <= n2; ++n6) {
            for (n9 = 14; n9 <= 16; ++n9) {
                if (!this.cave(n6, n, n9)) continue;
                this.g.set(n6, n - 1, n9, Gothic.s(Blocks.WATER));
                this.g.set(n6, n - 2, n9, Gothic.s(Blocks.WATER));
                this.g.set(n6, n - 3, n9, Gothic.s(this.g.pal.polished()));
            }
        }
        for (n6 = -n2; n6 <= n2; ++n6) {
            for (int n82 : new int[]{13, 17}) {
                if (!this.cave(n6, n, n82)) continue;
                this.g.set(n6, n - 1, n82, Gothic.s(this.g.pal.polished()));
                this.g.set(n6, n, n82, Math.floorMod(n6, 10) == 5 ? Gothic.s(Blocks.AIR) : Gothic.s(this.g.pal.wall()));
                if (Math.floorMod(n6, 10) != 0) continue;
                this.g.set(n6, n + 1, n82, Blocks.LANTERN.defaultBlockState());
            }
        }
        for (n6 = -n2; n6 <= n2; ++n6) {
            if (Math.floorMod(n6, 10) < 4 || Math.floorMod(n6, 10) > 6) continue;
            for (int i = 14; i <= 16; ++i) {
                if (!this.cave(n6, n, i)) continue;
                this.g.set(n6, n, i, Gothic.slab(this.g.pal.slab(), false));
            }
        }
        block25: for (n6 = -n2; n6 <= n2; ++n6) {
            if (!this.cave(n6, n, 15) || this.cave(n6 - 1, n, 15)) continue;
            for (int i = n + 2; i < n + 14; ++i) {
                if (this.cave(n6 - 1, i, 15)) continue;
                this.g.set(n6 - 1, i, 15, Gothic.s(Blocks.WATER));
                break block25;
            }
            break;
        }
        int[][] nArray5 = nArray;
        int n17 = nArray5.length;
        for (n4 = 0; n4 < n17; ++n4) {
            int[] nArray6 = nArray5[n4];
            if (nArray6[0] == 0 && nArray6[1] == 0) continue;
            this.g.spiralStair(nArray6[0], nArray6[1], n3 - 1, n, 1);
            for (n82 = n; n82 <= n + 2; ++n82) {
                for (n7 = 2; n7 <= 4; ++n7) {
                    if (!this.inside(nArray6[0] + n7, n82, nArray6[1])) continue;
                    this.g.air(nArray6[0] + n7, n82, nArray6[1]);
                }
            }
            for (n82 = n; n82 <= n + 2; ++n82) {
                this.g.set(nArray6[0] + 5, n82, nArray6[1], Gothic.s(Blocks.AIR));
            }
            this.g.set(nArray6[0] + 4, n + 3, nArray6[1], Gothic.s(this.g.pal.chiseled()));
        }
        for (int i = -n2; i <= n2; i += 20) {
            for (n17 = -n2; n17 <= n2; n17 += 20) {
                n4 = i + 10;
                int n18 = n17 + 10;
                if (Math.abs(n4) < 12 && Math.abs(n18) < 12 || Math.abs(n18 - 15) < 4 || !this.cave(n4, n, n18) || !this.cave(n4, n + 3, n18)) continue;
                for (n82 = -1; n82 <= 1; ++n82) {
                    for (n7 = -1; n7 <= 1; ++n7) {
                        boolean bl = n82 == 0 && n7 == 0;
                        this.g.set(n4 + n82, n, n18 + n7, bl ? Gothic.s(Blocks.AIR) : Gothic.slab(this.g.pal.slab(), false));
                        this.g.set(n4 + n82, n - 1, n18 + n7, bl ? Gothic.s(Blocks.WATER) : Gothic.s(this.g.pal.polished()));
                    }
                }
                this.g.set(n4, n - 2, n18, Gothic.s(Blocks.WATER));
                this.g.set(n4 - 1, n + 1, n18 - 1, Gothic.s(Blocks.SPRUCE_FENCE));
                this.g.set(n4 + 1, n + 1, n18 + 1, Gothic.s(Blocks.SPRUCE_FENCE));
                this.g.set(n4 - 1, n + 2, n18 - 1, Gothic.s(Blocks.SPRUCE_FENCE));
                this.g.set(n4 + 1, n + 2, n18 + 1, Gothic.s(Blocks.SPRUCE_FENCE));
                for (n82 = -1; n82 <= 1; ++n82) {
                    for (n7 = -1; n7 <= 1; ++n7) {
                        this.g.set(n4 + n82, n + 3, n18 + n7, Gothic.slab(Blocks.SPRUCE_SLAB, false));
                    }
                }
            }
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            n4 = n2 - 5;
            int n19 = direction.getStepX() * n4;
            n82 = direction.getStepZ() * n4;
            while (n4 > 8 && !this.cave(n19, n + 7, n82)) {
                n19 = direction.getStepX() * --n4;
                n82 = direction.getStepZ() * n4;
            }
            Direction direction2 = direction.getClockWise();
            for (int i = -4; i <= 4; ++i) {
                for (n5 = 0; n5 <= 7; ++n5) {
                    boolean bl;
                    int n20;
                    int n21 = n19 + direction2.getStepX() * i;
                    if (!this.cave(n21, n + n5, n20 = n82 + direction2.getStepZ() * i)) continue;
                    boolean bl2 = Math.abs(i) <= 2 && n5 <= 4 + (Math.abs(i) < 2 ? 1 : 0) ? true : (bl = false);
                    if (bl || Math.abs(i) < 3 && n5 < 5) continue;
                    this.g.set(n21, n + n5, n20, n5 == 7 ? Gothic.s(this.g.pal.chiseled()) : this.g.brick());
                }
            }
            this.g.set(n19 + direction2.getStepX() * 3, n + 8, n82 + direction2.getStepZ() * 3, Gothic.s(Blocks.CAMPFIRE));
            this.g.set(n19 - direction2.getStepX() * 3, n + 8, n82 - direction2.getStepZ() * 3, Gothic.s(Blocks.CAMPFIRE));
        }
    }

    private void garden(int n, int n2, int n3) {
        for (int i = n - 3; i <= n + 3; ++i) {
            for (int j = n3 - 3; j <= n3 + 3; ++j) {
                if (!this.cave(i, n2, j)) continue;
                int n4 = i == n || j == n3 ? 1 : 0;
                this.g.set(i, n2 - 1, j, n4 != 0 ? Gothic.s(Blocks.GRAVEL) : Gothic.s(Blocks.MOSS_BLOCK));
                if (n4 != 0) continue;
                float f = this.r.nextFloat();
                if (f < 0.25f) {
                    this.g.set(i, n2, j, Gothic.s(Blocks.AZALEA));
                    continue;
                }
                if (f < 0.35f) {
                    this.g.set(i, n2, j, Gothic.s(Blocks.FLOWERING_AZALEA));
                    continue;
                }
                if (f < 0.45f) {
                    this.g.set(i, n2, j, Gothic.s(this.r.nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM));
                    continue;
                }
                if (!(f < 0.6f)) continue;
                this.g.set(i, n2, j, Gothic.s(Blocks.MOSS_CARPET));
            }
        }
        for (int[] nArray : new int[][]{{n - 3, n3 - 3}, {n + 3, n3 + 3}, {n - 3, n3 + 3}, {n + 3, n3 - 3}}) {
            if (!this.cave(nArray[0], n2, nArray[1])) continue;
            this.g.set(nArray[0], n2, nArray[1], Gothic.s(this.g.pal.wall()));
            this.g.set(nArray[0], n2 + 1, nArray[1], Gothic.s(Blocks.SHROOMLIGHT));
        }
        this.g.set(n + 1, n2, n3 + 1, Gothic.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
    }

    private void domeHall(int n, int n2, int n3) {
        int n4;
        int n5;
        int n6 = 4;
        int n7 = 5;
        for (n5 = n - n6; n5 <= n + n6; ++n5) {
            for (n4 = n3 - n6; n4 <= n3 + n6; ++n4) {
                double d = Math.sqrt((n5 - n) * (n5 - n) + (n4 - n3) * (n4 - n3));
                if (d > (double)n6 + 0.4) continue;
                this.g.set(n5, n2 - 1, n4, this.g.floorTile(n5, n4));
                for (int i = n2; i < n2 + n7; ++i) {
                    this.g.set(n5, i, n4, d > (double)n6 - 0.6 ? (i - n2 == 2 ? Gothic.s(Blocks.ORANGE_STAINED_GLASS_PANE) : this.g.brick()) : Gothic.s(Blocks.AIR));
                }
            }
        }
        for (n5 = 0; n5 <= n6; ++n5) {
            for (n4 = n - n6; n4 <= n + n6; ++n4) {
                for (int i = n3 - n6; i <= n3 + n6; ++i) {
                    double d;
                    double d2 = Math.sqrt((n4 - n) * (n4 - n) + (i - n3) * (i - n3));
                    if (!(d2 <= (d = Math.sqrt(Math.max(0.0, ((double)n6 + 0.4) * ((double)n6 + 0.4) - (double)(n5 * n5))))) || !(d2 > d - 1.2)) continue;
                    this.g.set(n4, n2 + n7 + n5, i, (n4 + i + n5) % 3 == 0 ? Gothic.s(Blocks.GLOWSTONE) : Gothic.s(this.g.pal.roof()));
                }
            }
        }
        this.g.altar(n, n2, n3);
        ++this.rooms;
    }

    private double cHalf(int n, int n2) {
        return 6.5 + (double)(n2 - this.cbot) * 0.3 + 1.5 * Math.sin((double)n * 0.18 + this.cph);
    }

    private double cMid(int n) {
        return 2.5 * Math.sin((double)n * 0.11 + this.cph * 2.0);
    }

    private void canyon() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10;
        int n11;
        int n122;
        int n13;
        int n142;
        int n15;
        int n16;
        int n17 = -15;
        int n18 = this.R + 2;
        int n19 = (int)Math.round((double)this.R * 0.6) - 2;
        this.cbot = n17;
        this.cph = this.r.nextDouble() * 6.0;
        for (n16 = -this.R; n16 <= this.R; ++n16) {
            for (n15 = n17; n15 <= n18; ++n15) {
                double d = this.cHalf(n16, n15);
                double d2 = this.cMid(n16);
                for (n142 = -this.R; n142 <= this.R; ++n142) {
                    if (!(Math.abs((double)n142 - d2) <= d) || !this.inside(n16, n15, n142) && (n15 <= n19 || Math.abs(n16) >= this.R - 4)) continue;
                    this.g.air(n16, n15, n142);
                }
            }
        }
        for (n16 = -this.R; n16 <= this.R; ++n16) {
            for (n15 = -this.R; n15 <= this.R; ++n15) {
                if (!this.cave(n16, n17, n15)) continue;
                if (Math.abs((double)n15 - this.cMid(n16)) <= 2.5) {
                    this.g.set(n16, n17 - 1, n15, Gothic.s(Blocks.WATER));
                    this.g.set(n16, n17 - 2, n15, Gothic.s(Blocks.GRAVEL));
                    continue;
                }
                this.g.set(n16, n17 - 1, n15, Math.abs((double)n15 - this.cMid(n16)) <= 3.5 ? Gothic.s(this.g.pal.polished()) : this.g.floorTile(n16, n15));
            }
        }
        int[] nArray = new int[]{n17 + 6, n17 + 12, n17 + 18, n17 + 24};
        for (n15 = 0; n15 < nArray.length; ++n15) {
            int n20 = nArray[n15];
            for (int n142 : new int[]{-1, 1}) {
                for (n13 = -this.R + 2; n13 <= this.R - 2; ++n13) {
                    n122 = (int)Math.round(this.cMid(n13) + (double)n142 * (this.cHalf(n13, n20) + 1.0));
                    if (!this.inside(n13, n20, n122 + n142 * 6)) continue;
                    for (n11 = 0; n11 < 3; ++n11) {
                        n10 = n122 + n142 * n11;
                        this.g.set(n13, n20 - 1, n10, this.g.floorTile(n13, n10));
                        for (n9 = n20; n9 < n20 + 4; ++n9) {
                            this.g.air(n13, n9, n10);
                        }
                        this.g.set(n13, n20 + 4, n10, n11 == 0 ? Gothic.slab(this.g.pal.slab(), true) : Gothic.s(this.g.pal.polished()));
                    }
                    n11 = n122 - n142;
                    if (this.cave(n13, n20, n11) && this.cave(n13, n20 - 1, n11)) {
                        this.g.set(n13, n20 - 1, n11, Gothic.s(this.g.pal.polished()));
                        this.g.set(n13, n20, n11, n13 % 5 == 0 ? Gothic.s(this.g.pal.chiseled()) : Gothic.s(this.g.pal.wall()));
                        if (n13 % 5 == 0) {
                            this.g.set(n13, n20 + 1, n11, Blocks.LANTERN.defaultBlockState());
                        }
                        if (n13 % 5 == 0) {
                            this.g.set(n13, n20 - 2, n11, Gothic.stairs(this.g.pal.stairs(), n142 > 0 ? Direction.NORTH : Direction.SOUTH, true));
                        }
                    }
                    if (Math.floorMod(n13 + n15 * 3, 7) != 0) continue;
                    this.rockHouse(n13, n20, n122 + n142 * 2, n142 > 0 ? Direction.SOUTH : Direction.NORTH, 5, 5);
                }
            }
        }
        for (n15 = 0; n15 < nArray.length; ++n15) {
            int[] nArray2;
            int n21 = nArray[n15];
            for (int n142 : new int[]{-this.R + 9 + n15 * 7, this.R - 9 - n15 * 6}) {
                if (Math.abs(n142) > this.R - 6) continue;
                this.stoneBridge(n142, n21, (int)Math.round(this.cMid(n142) - this.cHalf(n142, n21) - 2.0), n142, (int)Math.round(this.cMid(n142) + this.cHalf(n142, n21) + 2.0), 1);
            }
            if (n15 == 0) {
                int[] nArray3 = new int[1];
                nArray2 = nArray3;
                nArray3[0] = n17;
            } else {
                int[] nArray4 = new int[1];
                nArray2 = nArray4;
                nArray4[0] = nArray[n15 - 1];
            }
            int[] nArray5 = nArray2;
            int[] nArray6 = new int[]{-1, 1};
            int n22 = nArray6.length;
            for (n142 = 0; n142 < n22; ++n142) {
                n13 = nArray6[n142];
                n122 = -6 + n15 * 4 * n13;
                n11 = nArray5[0];
                for (n10 = 0; n10 < n21 - n11; ++n10) {
                    n9 = n122 + n13 * n10;
                    n8 = n11 + n10;
                    n7 = (int)Math.round(this.cMid(n9) + (double)n13 * (this.cHalf(n9, n8) + 2.0));
                    for (n6 = 0; n6 < 2; ++n6) {
                        if (!this.inside(n9, n8, n7 + n13 * n6)) continue;
                        this.g.set(n9, n8 - 1, n7 + n13 * n6, Gothic.stairs(this.g.pal.stairs(), n13 > 0 ? Direction.EAST : Direction.WEST, false));
                        for (n5 = 0; n5 <= 3; ++n5) {
                            this.g.air(n9, n8 + n5, n7 + n13 * n6);
                        }
                    }
                }
            }
        }
        for (int n23 : new int[]{-14, 4, 16}) {
            int[] nArray7 = new int[]{-1, 1};
            n142 = nArray7.length;
            for (n13 = 0; n13 < n142; ++n13) {
                n122 = nArray7[n13];
                if (this.r.nextFloat() < 0.35f) continue;
                n11 = (int)Math.round(this.cMid(n23) + (double)n122 * (this.cHalf(n23, n17) - 1.0));
                n10 = nArray[3] + 6 - n17;
                if (!this.cave(n23, n17, n11)) continue;
                for (n9 = n23 - 2; n9 <= n23 + 2; ++n9) {
                    for (n8 = n11 - 2; n8 <= n11 + 2; ++n8) {
                        n7 = Math.abs(n9 - n23) == 2 || Math.abs(n8 - n11) == 2 ? 1 : 0;
                        for (n6 = n17; n6 < n17 + n10; ++n6) {
                            if (!this.inside(n9, n6, n8) && n6 < n19) continue;
                            if (n7 == 0) {
                                this.g.set(n9, n6, n8, (n6 - n17) % 6 == 5 && (n9 != n23 + 1 || n8 != n11) ? Gothic.s(Blocks.SPRUCE_PLANKS) : Gothic.s(Blocks.AIR));
                                continue;
                            }
                            int n24 = n5 = (n6 - n17) % 6 == 2 && (n9 == n23 || n8 == n11) ? 1 : 0;
                            this.g.set(n9, n6, n8, n5 != 0 ? Gothic.s(Blocks.ORANGE_STAINED_GLASS_PANE) : ((n6 - n17) % 6 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick()));
                        }
                    }
                }
                for (n9 = n17; n9 < n17 + n10 - 1; ++n9) {
                    this.g.set(n23 + 1, n9, n11, (BlockState)Blocks.LADDER.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)Direction.WEST));
                }
                int[] nArray8 = nArray;
                n8 = nArray8.length;
                for (n7 = 0; n7 < n8; ++n7) {
                    for (n5 = n6 = nArray8[n7]; n5 < n6 + 2; ++n5) {
                        this.g.air(n23, n5, n11 + n122 * 2);
                        this.g.air(n23, n5, n11 - n122 * 2);
                    }
                }
                for (n9 = n17; n9 < n17 + 2; ++n9) {
                    this.g.air(n23 - 2, n9, n11);
                }
                this.g.spire(n23, n11, n17 + n10, 2, 8);
                ++this.rooms;
            }
        }
        for (n4 = -this.R + 6; n4 <= this.R - 6; n4 += 9) {
            int n25;
            n3 = n4 / 9 % 2 == 0 ? 1 : -1;
            n2 = (int)Math.round(this.cMid(n4) + (double)n3 * 4.5);
            if (!this.cave(n4, n17, n2) || !this.cave(n4, n17 + 5, n2)) continue;
            for (n25 = -2; n25 <= 2; ++n25) {
                for (int i = 0; i <= 2; ++i) {
                    n142 = n4 + n25;
                    n13 = n2 + n3 * i;
                    if (!this.cave(n142, n17, n13)) continue;
                    this.g.set(n142, n17, n13, Gothic.s(Blocks.SPRUCE_PLANKS));
                    if (Math.abs(n25) == 2 && i != 1) {
                        for (n122 = n17 + 1; n122 <= n17 + 3; ++n122) {
                            this.g.set(n142, n122, n13, Gothic.s(Blocks.STRIPPED_SPRUCE_LOG));
                        }
                    } else if (Math.abs(n25) == 2 || i == 2) {
                        for (n122 = n17 + 1; n122 <= n17 + 3; ++n122) {
                            this.g.set(n142, n122, n13, n122 == n17 + 2 && n25 == 0 ? Gothic.s(Blocks.ORANGE_STAINED_GLASS_PANE) : Gothic.s(Blocks.SPRUCE_PLANKS));
                        }
                    }
                    this.g.set(n142, n17 + 4, n13, Gothic.slab(this.g.pal.roofSlab(), false));
                }
            }
            this.g.air(n4, n17 + 1, n2);
            this.g.air(n4, n17 + 2, n2);
            this.g.set(n4, n17 + 3, n2 + n3, Gothic.s(Blocks.SHROOMLIGHT));
            this.g.set(n4 + 1, n17 + 1, n2 + n3, Gothic.s(Blocks.BARREL));
            for (n25 = 1; n25 <= 2; ++n25) {
                this.g.set(n4, n17 - 1, n2 - n3 * n25, Gothic.s(Blocks.SPRUCE_PLANKS));
            }
            ++this.rooms;
        }
        n4 = -this.R / 2;
        n3 = (int)Math.round(this.cMid(n4));
        for (n2 = -3; n2 <= 3; ++n2) {
            for (int i = -3; i <= 3; ++i) {
                double d = Math.sqrt(n2 * n2 + i * i);
                if (d > 3.4) continue;
                this.g.set(n4 + n2, n17 + i, n3, d > 2.4 || n2 == 0 || i == 0 ? Gothic.s(Blocks.STRIPPED_SPRUCE_LOG) : Gothic.s(Blocks.AIR));
            }
        }
        for (n2 = this.R - 6; n2 > 0 && this.cave(n2, n17, (int)Math.round(this.cMid(n2))); ++n2) {
        }
        int n26 = (int)Math.round(this.cMid(--n2));
        for (n = n26 - 7; n <= n26 + 7; ++n) {
            for (n142 = n17; n142 <= n17 + 16; ++n142) {
                if (!this.inside(n2 + 1, n142, n)) continue;
                this.g.set(n2 + 1, n142, n, Math.abs(n - n26) % 3 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick());
            }
        }
        for (n = n2 + 1; n <= n2 + 9; ++n) {
            for (n142 = n26 - 4; n142 <= n26 + 4; ++n142) {
                for (n13 = n17; n13 <= n17 + 8; ++n13) {
                    if (!this.inside(n, n13, n142)) continue;
                    this.g.air(n, n13, n142);
                }
            }
        }
        for (n = n2 + 1; n <= n2 + 9; ++n) {
            for (n142 = n26 - 4; n142 <= n26 + 4; ++n142) {
                if (!this.inside(n, n17 - 1, n142)) continue;
                this.g.set(n, n17 - 1, n142, this.g.floorTile(n, n142));
            }
        }
        this.g.altar(Math.min(n2 + 8, this.R - 4), n17, n26);
        for (int n122 : new int[]{n26 - 3, n26 + 3}) {
            this.g.pillar(n2 + 4, n122, n17, 8);
        }
        this.g.chest(n2 + 7, n17, n26 + 2, true);
        this.g.rose(n26, n17 + 12, n2 + 1, Direction.Axis.Z, 3);
        this.ceilingLights(this.R - 2, n17, n18, 50);
    }

    private void rockHouse(int n, int n2, int n3, Direction direction, int n4, int n5) {
        int n6;
        int n7;
        int n8;
        int n9;
        int n10;
        int n11;
        Direction direction2 = direction.getClockWise();
        int n12 = n4 / 2;
        for (n11 = -n12; n11 <= n12; ++n11) {
            for (n10 = 1; n10 <= n5; ++n10) {
                n9 = n + direction2.getStepX() * n11 + direction.getStepX() * n10;
                if (!this.inside(n9, n2 + 3, n8 = n3 + direction2.getStepZ() * n11 + direction.getStepZ() * n10)) continue;
                this.g.set(n9, n2 - 1, n8, Gothic.s(Blocks.SPRUCE_PLANKS));
                for (n7 = n2; n7 < n2 + 4; ++n7) {
                    this.g.air(n9, n7, n8);
                }
                this.g.set(n9, n2 + 4, n8, Gothic.s(this.g.pal.polished()));
            }
        }
        for (n11 = -n12 - 1; n11 <= n12 + 1; ++n11) {
            for (n10 = 1; n10 <= n5 + 1; ++n10) {
                if (Math.abs(n11) <= n12 && n10 <= n5) continue;
                n9 = n + direction2.getStepX() * n11 + direction.getStepX() * n10;
                n8 = n3 + direction2.getStepZ() * n11 + direction.getStepZ() * n10;
                for (n7 = n2 - 1; n7 <= n2 + 4; ++n7) {
                    if (!this.cave(n9, n7, n8) || this.onLedge(n9, n7, n8)) continue;
                    this.g.set(n9, n7, n8, (n7 == n2 + 1 || n7 == n2 + 2) && n10 == n5 / 2 + 1 && Math.abs(n11) == n12 + 1 ? Gothic.s(Blocks.ORANGE_STAINED_GLASS_PANE) : this.g.brick());
                }
            }
        }
        for (n11 = -n12; n11 <= n12; ++n11) {
            for (n10 = 1; n10 <= n5; ++n10) {
                n9 = n + direction2.getStepX() * n11 + direction.getStepX() * n10;
                if (this.cave(n9, n2 - 2, n8 = n3 + direction2.getStepZ() * n11 + direction.getStepZ() * n10) && this.g.isAir(n9, n2 - 2, n8)) {
                    this.g.set(n9, n2 - 2, n8, this.g.brick());
                }
                if (!this.cave(n9, n2 + 5, n8)) continue;
                this.g.set(n9, n2 + 5, n8, Gothic.s(this.g.pal.polished()));
            }
        }
        for (n11 = -n12; n11 <= n12; ++n11) {
            for (n10 = n2; n10 < n2 + 4; ++n10) {
                n9 = n + direction2.getStepX() * n11;
                n8 = n3 + direction2.getStepZ() * n11;
                n7 = n11 == 0 && n10 < n2 + 2 ? 1 : 0;
                int n13 = n6 = Math.abs(n11) == n12 - 1 && n10 == n2 + 1 || Math.abs(n11) == 1 && n10 == n2 + 2 ? 1 : 0;
                if (n7 != 0) {
                    this.g.air(n9, n10, n8);
                    if (n10 != n2 || !this.g.isAir(n9, n2 - 1, n8)) continue;
                    this.g.set(n9, n2 - 1, n8, this.g.floorTile(n9, n8));
                    continue;
                }
                if (this.onLedge(n9, n10, n8)) continue;
                this.g.set(n9, n10, n8, n6 != 0 ? Gothic.s(Blocks.ORANGE_STAINED_GLASS_PANE) : (n10 == n2 + 3 ? Gothic.s(this.g.pal.chiseled()) : (Math.abs(n11) == n12 ? Gothic.s(this.g.pal.polished()) : this.g.brick())));
            }
        }
        n11 = n - direction.getStepX();
        n10 = n3 - direction.getStepZ();
        for (n9 = -n12; n9 <= n12; ++n9) {
            this.g.set(n11 + direction2.getStepX() * n9, n2 + 4, n10 + direction2.getStepZ() * n9, Gothic.stairs(this.g.pal.stairs(), direction, true));
        }
        this.g.set(n11, n2 + 3, n10, (BlockState)Blocks.LANTERN.defaultBlockState().setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(true)));
        n9 = n + direction.getStepX() * (n5 / 2 + 1);
        n8 = n3 + direction.getStepZ() * (n5 / 2 + 1);
        this.g.set(n9, n2 + 3, n8, Gothic.s(Blocks.SHROOMLIGHT));
        this.g.set(n + direction.getStepX() * n5 + direction2.getStepX() * (n12 - 1), n2, n3 + direction.getStepZ() * n5 + direction2.getStepZ() * (n12 - 1), Gothic.s(Blocks.BARREL));
        this.g.set(n + direction.getStepX() * n5 - direction2.getStepX() * (n12 - 1), n2, n3 + direction.getStepZ() * n5 - direction2.getStepZ() * (n12 - 1), Gothic.s(Blocks.CRAFTING_TABLE));
        this.g.set(n9 + direction2.getStepX(), n2, n8 + direction2.getStepZ(), Gothic.s(Blocks.SPRUCE_FENCE));
        this.g.set(n9 + direction2.getStepX(), n2 + 1, n8 + direction2.getStepZ(), Gothic.candle(2));
        if (n5 >= 5) {
            n7 = n + direction.getStepX() * (n5 - 1) - direction2.getStepX() * (n12 - 1);
            n6 = n3 + direction.getStepZ() * (n5 - 1) - direction2.getStepZ() * (n12 - 1);
            this.g.set(n7, n2, n6, (BlockState)((BlockState)Blocks.RED_BED.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.BED_PART, (Comparable)BedPart.HEAD));
            this.g.set(n7 - direction.getStepX(), n2, n6 - direction.getStepZ(), (BlockState)((BlockState)Blocks.RED_BED.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.BED_PART, (Comparable)BedPart.FOOT));
        }
        if (this.r.nextFloat() < 0.3f) {
            this.g.chest(n + direction.getStepX() * n5 + direction2.getStepX() * (n12 - 1), n2 + 1, n3 + direction.getStepZ() * n5 + direction2.getStepZ() * (n12 - 1), false);
        }
        ++this.rooms;
    }

    private static Direction dirOf(double d, double d2) {
        if (Math.abs(d) >= Math.abs(d2)) {
            return d > 0.0 ? Direction.EAST : Direction.WEST;
        }
        return d2 > 0.0 ? Direction.SOUTH : Direction.NORTH;
    }

    private void wallGalleries(int[] nArray, double d) {
        this.wallGalleries(nArray, d, 6);
    }

    private void wallGalleries(int[] nArray, double d, int n) {
        for (int n2 : nArray) {
            int n3;
            int n4;
            int n5;
            double d2;
            int n6;
            int n7 = 0;
            int n8 = n;
            while ((double)n8 < d) {
                if (!this.cave(n8, n2, 0) || !this.cave(n8, n2 + 3, 0)) {
                    n7 = n8;
                    break;
                }
                ++n8;
            }
            if (n7 == 0) {
                n7 = (int)(d * 0.8);
            }
            n8 = (int)(Math.PI * 2 * (double)n7 / 0.5);
            int[] nArray2 = new int[n8];
            for (n6 = 0; n6 < n8; ++n6) {
                d2 = (double)n6 * Math.PI * 2.0 / (double)n8;
                nArray2[n6] = this.wallAt(Math.cos(d2), Math.sin(d2), n2, n, d);
            }
            for (n6 = 0; n6 < n8; ++n6) {
                d2 = (double)n6 * Math.PI * 2.0 / (double)n8;
                double d3 = Math.cos(d2);
                double d4 = Math.sin(d2);
                int n9 = nArray2[n6];
                if (n9 < 0) continue;
                for (n5 = 0; n5 <= 3; ++n5) {
                    n4 = (int)Math.round(d3 * (double)(n9 - n5));
                    if (!this.inside(n4, n2 + 2, n3 = (int)Math.round(d4 * (double)(n9 - n5)))) continue;
                    if (n5 == 0) {
                        if (!this.g.isAir(n4, n2 - 1, n3) || !this.inside(n4, n2 - 1, n3)) continue;
                        this.g.set(n4, n2 - 1, n3, this.g.floorTile(n4, n3));
                        continue;
                    }
                    if (this.g.isAir(n4, n2 - 1, n3)) {
                        this.g.set(n4, n2 - 1, n3, this.g.floorTile(n4, n3));
                        if (this.g.isAir(n4, n2 - 2, n3)) {
                            this.g.set(n4, n2 - 2, n3, Gothic.s(this.g.pal.polished()));
                        }
                    }
                    for (int i = n2; i < n2 + 3; ++i) {
                        if (!this.cave(n4, i, n3)) {
                            this.g.air(n4, i, n3);
                        }
                        this.ledge.add(UndergroundCity.Plan.key(n4, i, n3));
                    }
                }
            }
            for (n6 = 0; n6 < n8; ++n6) {
                int n10;
                int n11;
                d2 = (double)n6 * Math.PI * 2.0 / (double)n8;
                int n12 = nArray2[n6];
                if (n12 < 0 || this.onLedge(n11 = (int)Math.round(Math.cos(d2) * (double)(n12 - 4)), n2, n10 = (int)Math.round(Math.sin(d2) * (double)(n12 - 4))) || !this.inside(n11, n2, n10) || !this.g.isAir(n11, n2 - 1, n10) || !this.g.isAir(n11, n2, n10)) continue;
                this.g.set(n11, n2 - 1, n10, Gothic.s(this.g.pal.polished()));
                this.g.set(n11, n2, n10, Gothic.s(this.g.pal.wall()));
            }
            n6 = Math.max(10, (int)(Math.PI * 2 * (double)n7 / 7.0));
            for (int i = 0; i < n6; ++i) {
                BlockState blockState;
                int n13;
                int n14;
                int n15;
                double d5 = (double)i * Math.PI * 2.0 / (double)n6 + (double)n2 * 0.13;
                double d6 = Math.cos(d5);
                double d7 = Math.sin(d5);
                n5 = nArray2[Math.floorMod((int)Math.round(d5 / (Math.PI * 2) * (double)n8), n8)];
                if (n5 < 0) continue;
                n4 = (int)Math.round(d6 * (double)n5);
                n3 = (int)Math.round(d7 * (double)n5);
                if (this.r.nextFloat() < 0.9f) {
                    this.rockHouse(n4, n2, n3, DarkCities.dirOf(d6, d7), 5, 5);
                    Direction direction = DarkCities.dirOf(d6, d7).getOpposite();
                    for (var24_30 = 1; var24_30 <= 4 && this.inside(n15 = n4 + direction.getStepX() * var24_30, n2 + 2, n14 = n3 + direction.getStepZ() * var24_30); ++var24_30) {
                        n13 = this.onLedge(n15, n2, n14);
                        this.g.air(n15, n2, n14);
                        this.g.air(n15, n2 + 1, n14);
                        blockState = this.g.get(n15, n2 - 1, n14);
                        if (blockState == null || blockState.isAir()) {
                            this.g.set(n15, n2 - 1, n14, this.g.floorTile(n15, n14));
                        }
                        if (n13 == 0) {
                            continue;
                        }
                        break;
                    }
                } else {
                    int n16 = (int)Math.round(d6 * (double)(n5 - 1));
                    var24_30 = (int)Math.round(d7 * (double)(n5 - 1));
                    this.g.set(n16, n2 + 2, var24_30, (BlockState)Blocks.LANTERN.defaultBlockState().setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(true)));
                }
                double d8 = d5 + Math.PI / (double)n6;
                n15 = nArray2[Math.floorMod((int)Math.round(d8 / (Math.PI * 2) * (double)n8), n8)];
                if (n15 <= 0 || (blockState = this.g.get(n14 = (int)Math.round(Math.cos(d8) * (double)(n15 - 4)), n2, n13 = (int)Math.round(Math.sin(d8) * (double)(n15 - 4)))) == null || blockState.isAir()) continue;
                this.g.set(n14, n2 + 1, n13, Blocks.LANTERN.defaultBlockState());
            }
        }
    }

    private int wallAt(double d, double d2, int n, int n2, double d3) {
        int n3 = n2;
        while ((double)n3 < d3) {
            int n4;
            int n5 = (int)Math.round(d * (double)n3);
            if (!this.cave(n5, n, n4 = (int)Math.round(d2 * (double)n3)) || !this.cave(n5, n + 3, n4)) {
                return n3;
            }
            ++n3;
        }
        return -1;
    }

    private void necropolis() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10 = -2;
        int n11 = 14;
        this.carveBox(-9, n10, -9, 9, n10 + n11, 9);
        for (n9 = -9; n9 <= 9; ++n9) {
            for (n8 = -9; n8 <= 9; ++n8) {
                this.g.set(n9, n10 - 1, n8, this.g.floorTile(n9, n8));
            }
        }
        for (n9 = -10; n9 <= 10; ++n9) {
            for (n8 = -10; n8 <= 10; ++n8) {
                for (int i = n10; i <= n10 + n11 + 1; ++i) {
                    if (Math.abs(n9) != 10 && Math.abs(n8) != 10 && i != n10 + n11 + 1 || !this.inside(n9, i, n8)) continue;
                    this.g.set(n9, i, n8, this.g.brick());
                }
            }
        }
        for (n9 = -6; n9 <= 6; n9 += 4) {
            for (n8 = -6; n8 <= 6; n8 += 4) {
                if (Math.abs(n9) < 3 && Math.abs(n8) < 3) continue;
                this.g.pillar(n9, n8, n10, n11);
                for (Object object2 : Direction.Plane.HORIZONTAL) {
                    for (n7 = 1; n7 <= 2; ++n7) {
                        n6 = n9 + object2.getStepX() * n7;
                        n5 = n8 + object2.getStepZ() * n7;
                        if (Math.abs(n6) > 9 || Math.abs(n5) > 9) continue;
                        this.g.set(n6, n10 + n11 - 3 + n7, n5, Gothic.stairs(this.g.pal.stairs(), object2.getOpposite(), true));
                    }
                }
            }
        }
        for (n9 = -2; n9 <= 2; ++n9) {
            for (n8 = -2; n8 <= 2; ++n8) {
                this.g.set(n9, n10, n8, Gothic.s(this.g.pal.polished()));
            }
        }
        this.g.altar(0, n10 + 1, 0);
        this.g.sarcophagus(-6, n10, -1, false);
        this.g.sarcophagus(6, n10, -1, false);
        Object object = new int[][]{{-4, -4}, {4, -4}, {-4, 4}, {4, 4}, {0, -6}, {0, 6}};
        n8 = ((int[][])object).length;
        for (n4 = 0; n4 < n8; ++n4) {
            Object object2;
            object2 = object[n4];
            this.g.chainLantern((int)object2[0], n10 + n11, (int)object2[1], 3 + this.r.nextInt(4));
        }
        this.g.rose(0, n10 + 9, -10, Direction.Axis.X, 3);
        this.g.rose(0, n10 + 9, 10, Direction.Axis.X, 3);
        this.g.chest(0, n10, -8, true);
        ++this.rooms;
        object = Direction.Plane.HORIZONTAL.iterator();
        while (object.hasNext()) {
            Direction direction = (Direction)object.next();
            n4 = direction.getStepX();
            int n12 = direction.getStepZ();
            n7 = -n12;
            n6 = n4;
            for (n5 = 10; n5 <= 19; ++n5) {
                int n13;
                for (n13 = -1; n13 <= 1; ++n13) {
                    for (n3 = n10; n3 <= n10 + 4; ++n3) {
                        this.carve(n4 * n5 + n7 * n13, n3, n12 * n5 + n6 * n13);
                    }
                }
                for (n13 = -1; n13 <= 1; ++n13) {
                    if (!this.inside(n4 * n5 + n7 * n13, n10 - 1, n12 * n5 + n6 * n13)) continue;
                    this.g.set(n4 * n5 + n7 * n13, n10 - 1, n12 * n5 + n6 * n13, this.g.floorTile(n5, n13));
                }
                for (n13 = -1; n13 <= 1; ++n13) {
                    if (!this.inside(n4 * n5 + n7 * n13, n10 + 5, n12 * n5 + n6 * n13)) continue;
                    this.g.set(n4 * n5 + n7 * n13, n10 + 5, n12 * n5 + n6 * n13, n13 == 0 ? Gothic.s(this.g.pal.polished()) : Gothic.stairs(this.g.pal.stairs(), n13 < 0 ? DarkCities.dirOf(n7, n6) : DarkCities.dirOf(-n7, -n6), true));
                }
                if (n5 % 2 == 0) {
                    int[] nArray = new int[]{-1, 1};
                    n3 = nArray.length;
                    for (n2 = 0; n2 < n3; ++n2) {
                        int n14 = nArray[n2];
                        int n15 = n4 * n5 + n7 * 2 * n14;
                        int n16 = n12 * n5 + n6 * 2 * n14;
                        if (!this.inside(n15 + n7 * n14, n10 + 2, n16 + n6 * n14)) continue;
                        for (int i = n10; i <= n10 + 3; ++i) {
                            this.g.set(n15, i, n16, i == n10 + 1 || i == n10 + 2 ? Gothic.s(Blocks.AIR) : Gothic.s(this.g.pal.polished()));
                        }
                        this.g.set(n15 + n7 * n14, n10 + 1, n16 + n6 * n14, Gothic.s(Blocks.BONE_BLOCK));
                        float f = this.r.nextFloat();
                        this.g.set(n15, n10 + 1, n16, f < 0.4f ? Gothic.s(Blocks.SKELETON_SKULL) : (f < 0.7f ? Gothic.candle(1 + this.r.nextInt(3)) : Gothic.s(Blocks.COBWEB)));
                    }
                }
                if (n5 % 4 != 1) continue;
                this.g.chainLantern(n4 * n5, n10 + 4, n12 * n5, 1);
            }
            n5 = n4 * 19;
            int n17 = n12 * 19;
            if (this.inside(n5, n10, n17)) {
                this.g.set(n5, n10, n17, Gothic.s(this.g.pal.chiseled()));
                this.g.set(n5, n10 + 1, n17, Gothic.candle(4));
                if (this.r.nextBoolean()) {
                    this.g.chest(n5 - n7, n10, n17 - n6, false);
                }
            }
            this.rooms += 6;
        }
        int n18 = -12;
        for (n = -16; n <= 16; ++n) {
            for (n4 = -16; n4 <= 16; ++n4) {
                double d = Math.sqrt(n * n + n4 * n4);
                if (d < 11.5 || d > 14.5) continue;
                for (int i = n18; i <= n18 + 4; ++i) {
                    this.carve(n, i, n4);
                }
                if (this.inside(n, n18 - 1, n4)) {
                    this.g.set(n, n18 - 1, n4, Gothic.s(this.g.pal.floorAlt()));
                }
                double d2 = 11.0;
                double d3 = 15.0;
                for (n2 = n18; n2 <= n18 + 4; ++n2) {
                    for (double d4 : new double[]{d2, d3}) {
                        int n19;
                        int n20 = (int)Math.round((double)n * d4 / d);
                        if (!this.inside(n20, n2, n19 = (int)Math.round((double)n4 * d4 / d)) || this.g.isAir(n20, n2, n19)) continue;
                        this.g.set(n20, n2, n19, this.r.nextFloat() < 0.45f ? Gothic.s(Blocks.BONE_BLOCK) : (this.r.nextFloat() < 0.15f ? Gothic.s(Blocks.SKELETON_SKULL) : this.g.brick()));
                    }
                }
            }
        }
        for (n = 0; n < 360; n += 45) {
            double d = Math.toRadians(n);
            this.g.chainLantern((int)Math.round(Math.cos(d) * 13.0), n18 + 4, (int)Math.round(Math.sin(d) * 13.0), 1);
        }
        this.g.chest(13, n18, 0, true);
        this.g.chest(-13, n18, 0, false);
        for (int[] nArray : new int[][]{{8, 8}, {-8, -8}}) {
            int n21 = Integer.signum(nArray[0]);
            n5 = Integer.signum(nArray[1]);
            for (int i = 0; i <= n10 - n18 + 1; ++i) {
                n3 = nArray[0] + n21 * Math.min(i, 6);
                n2 = nArray[1] + (i > 6 ? n5 * (i - 6) : 0);
                for (int j = n10 - i; j <= n10 - i + 4; ++j) {
                    this.carve(n3, j, n2);
                }
                this.g.set(n3, n10 - i - 1, n2, this.g.brick());
            }
        }
        this.rooms += 4;
        this.entrance(15, 15, n10);
        this.carveBox(10, n10, 13, 15, n10 + 3, 14);
        this.g.cobwebs(-19, n18, -19, 19, n10 + n11, 19, 0.04f + this.g.decay * 0.08f);
    }

    private static Direction dirOf(int n, int n2) {
        if (n > 0) {
            return Direction.EAST;
        }
        if (n < 0) {
            return Direction.WEST;
        }
        return n2 > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private void abyss() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6 = -17;
        int n7 = 6;
        for (int i = -n7; i <= n7; ++i) {
            for (int j = -n7; j <= n7; ++j) {
                if (i * i + j * j > n7 * n7 + 2) continue;
                for (int k = n6; k <= this.R + 1; ++k) {
                    if (!((double)k <= Math.sqrt(Math.max(0, this.R * this.R - i * i - j * j)) + 1.0)) continue;
                    this.g.air(i, k, j);
                }
            }
        }
        double d = 0.0;
        double d2 = 1.2;
        double d3 = Math.sqrt((double)(this.R * this.R) - 64.0) - 1.0;
        int n8 = 0;
        int n9 = (int)d3;
        while (n9 > n6) {
            int n10;
            int n11;
            int n12;
            n9 = (int)Math.floor(d3 - d * 2.6);
            for (n12 = 0; n12 < 2; ++n12) {
                double d4 = (double)n7 + 0.5 + (double)n12;
                n11 = (int)Math.round(Math.cos(d) * d4);
                n5 = (int)Math.round(Math.sin(d) * d4);
                for (n4 = 0; n4 <= 3; ++n4) {
                    this.carve(n11, n9 + n4, n5);
                }
                this.g.set(n11, n9 - 1, n5, n12 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick());
            }
            n12 = (int)Math.round(Math.cos(d) * ((double)n7 - 0.3));
            if (this.g.isAir(n12, n9, n10 = (int)Math.round(Math.sin(d) * ((double)n7 - 0.3))) && this.g.isAir(n12, n9 - 1, n10)) {
                this.g.set(n12, n9 - 1, n10, Gothic.s(this.g.pal.polished()));
                if ((int)(d * 10.0) % 3 == 0) {
                    this.g.set(n12, n9, n10, Gothic.s(this.g.pal.wall()));
                }
            }
            if (d >= d2 && n8 < 12) {
                d2 += 2.0943951023931953;
                int n13 = (int)Math.round(Math.cos(d) * (double)(n7 + 4));
                n11 = (int)Math.round(Math.sin(d) * (double)(n7 + 4));
                this.carveBox(n13 - 1, n9, n11 - 1, n13 + 1, n9 + 3, n11 + 1);
                for (n5 = n13 - 1; n5 <= n13 + 1; ++n5) {
                    for (n4 = n11 - 1; n4 <= n11 + 1; ++n4) {
                        this.g.set(n5, n9 - 1, n4, Gothic.s(this.g.pal.floor()));
                    }
                }
                n5 = (int)Math.round(Math.cos(d) * ((double)n7 + 5.5));
                n4 = (int)Math.round(Math.sin(d) * ((double)n7 + 5.5));
                this.g.set(n5, n9, n4, Gothic.s(this.g.pal.chiseled()));
                this.g.set(n5, n9 + 1, n4, n8 % 3 == 0 ? Gothic.s(Blocks.WITHER_SKELETON_SKULL) : Gothic.candle(3));
                this.g.set(n13, n9 + 3, n11, this.g.lantern(true));
                if (n8 % 2 == 1) {
                    this.g.chest(n13, n9, n11, n8 > 6);
                }
                ++n8;
                ++this.rooms;
            }
            d += 0.08;
        }
        for (int[] nArray : new int[][]{{2, 0}, {-8, 1}}) {
            n5 = nArray[0];
            n4 = nArray[1] == 0 ? 1 : 0;
            for (n3 = -n7 - 2; n3 <= n7 + 2; ++n3) {
                n2 = n4 != 0 ? n3 : 0;
                int n14 = n4 != 0 ? 0 : n3;
                this.g.set(n2, n5, n14, Gothic.s(Blocks.OBSIDIAN));
                for (int i = 1; i <= 3; ++i) {
                    this.carve(n2, n5 + i, n14);
                }
                for (int n15 : new int[]{-1, 1}) {
                    int n16 = n4 != 0 ? n2 : n15;
                    int n17 = n4 != 0 ? n15 : n14;
                    this.g.set(n16, n5, n17, Gothic.s(Blocks.CRYING_OBSIDIAN));
                    this.g.set(n16, n5 + 1, n17, Gothic.s(Blocks.IRON_BARS));
                }
            }
        }
        for (n = 0; n < 6; ++n) {
            double d5 = (double)n * Math.PI / 3.0;
            int n18 = (int)Math.round(Math.cos(d5) * 3.0);
            n5 = (int)Math.round(Math.sin(d5) * 3.0);
            n4 = (int)Math.sqrt(Math.max(0.0, ((double)this.R - 2.0) * ((double)this.R - 2.0) - (double)(n18 * n18) - (double)(n5 * n5)));
            n3 = 8 + this.r.nextInt(16);
            for (n2 = 0; n2 < n3; ++n2) {
                this.g.set(n18, n4 - n2, n5, Gothic.s(Blocks.CHAIN));
            }
            this.g.set(n18, n4 - n3, n5, this.g.lantern(true));
        }
        for (n = -n7 - 3; n <= n7 + 3; ++n) {
            for (int i = -n7 - 3; i <= n7 + 3; ++i) {
                double d6 = Math.sqrt(n * n + i * i);
                if (d6 > (double)n7 + 0.5) continue;
                this.g.set(n, n6 - 1, i, d6 < 2.5 ? Gothic.s(this.g.pal.polished()) : (d6 < 4.5 ? Gothic.s(Blocks.SOUL_SOIL) : Gothic.s(Blocks.BLACKSTONE)));
                if (!(d6 >= 2.5) || !(d6 < 4.5) || !(this.r.nextFloat() < 0.4f)) continue;
                this.g.set(n, n6, i, Gothic.s(Blocks.SOUL_FIRE));
            }
        }
        this.g.altar(0, n6, 0);
        this.g.chest(1, n6, 1, true);
        for (n = -this.R; n <= this.R; ++n) {
            for (int i = -this.R; i <= this.R; ++i) {
                for (int j = n6; j < n6 + 8; ++j) {
                    if (!this.g.solid(n, j, i) || n * n + i * i > (n7 + 3) * (n7 + 3) || !((double)this.r.nextFloat() < (double)0.35f * (1.0 - (double)(j - n6) / 8.0))) continue;
                    this.g.set(n, j, i, Gothic.s(Blocks.CRYING_OBSIDIAN));
                }
            }
        }
        this.rooms += 2;
    }

    private void nest() {
        int n;
        double d;
        int n2;
        int n3 = 9;
        ArrayList<int[]> arrayList = new ArrayList<int[]>();
        for (n2 = 0; n2 < n3; ++n2) {
            double d2 = (this.r.nextDouble() - 0.5) * 6.0;
            d = this.R - 4;
            double d3 = (this.r.nextDouble() - 0.5) * 6.0;
            if (n2 > 0) {
                int[] nArray = (int[])arrayList.get(this.r.nextInt(arrayList.size()));
                d2 = nArray[0];
                d = nArray[1];
                d3 = nArray[2];
            }
            double d4 = this.r.nextDouble() * Math.PI * 2.0;
            double d5 = -0.5;
            int n4 = 30 + this.r.nextInt(25);
            for (int i = 0; i < n4; ++i) {
                d5 = Math.max(-0.9, Math.min(0.4, d5 + (this.r.nextDouble() - 0.5) * 0.3));
                d2 += Math.cos(d4 += (this.r.nextDouble() - 0.5) * 0.6) * Math.cos(d5);
                d3 += Math.sin(d4) * Math.cos(d5);
                d += Math.sin(d5);
                double d6 = 1.6 + 0.6 * Math.sin((double)i * 0.4);
                if (d2 * d2 + d * d + d3 * d3 > (double)((this.R - 5) * (this.R - 5))) {
                    d4 += 2.199114857512855;
                    continue;
                }
                this.sphere((int)d2, (int)d, (int)d3, d6);
                if (i % 14 != 13) continue;
                arrayList.add(new int[]{(int)d2, (int)d, (int)d3});
            }
            if (!arrayList.isEmpty()) continue;
            arrayList.add(new int[]{(int)d2, (int)d, (int)d3});
        }
        for (n2 = this.R - 6; n2 <= this.R + 1; ++n2) {
            this.sphere(0, n2, 0, 2.2);
        }
        n2 = 0;
        for (int[] object : arrayList) {
            d = 3.5 + this.r.nextDouble() * 2.0;
            this.sphere(object[0], object[1], object[2], d);
            int n5 = this.floorBelow(object[0], object[1], object[2]);
            if (n2 % 3 == 0) {
                this.eggs(object[0], n5, object[2]);
            } else if (n2 % 3 == 1) {
                this.g.set(object[0], n5, object[2], Gothic.s(Blocks.SKELETON_SKULL));
                this.g.set(object[0] + 1, n5, object[2], Gothic.s(Blocks.BONE_BLOCK));
                this.g.chest(object[0] - 1, n5, object[2], n2 > 6);
                this.g.set(object[0], n5, object[2] + 1, this.g.lantern(false));
            } else {
                for (n = 0; n < 4; ++n) {
                    this.g.set(object[0] + this.r.nextInt(5) - 2, n5, object[2] + this.r.nextInt(5) - 2, Gothic.s(Blocks.BONE_BLOCK));
                }
            }
            ++n2;
            ++this.rooms;
        }
        for (Map.Entry entry : new ArrayList<Map.Entry<Long, BlockState>>(this.p.blocks.entrySet())) {
            int n6;
            long l;
            int n7;
            int n4;
            if (!((BlockState)entry.getValue()).isAir() || (n4 = (this.g.solid((n7 = UndergroundCity.Plan.kx(l = ((Long)entry.getKey()).longValue())) + 1, n = UndergroundCity.Plan.ky(l), n6 = UndergroundCity.Plan.kz(l)) ? 1 : 0) + (this.g.solid(n7 - 1, n, n6) ? 1 : 0) + (this.g.solid(n7, n + 1, n6) ? 1 : 0) + (this.g.solid(n7, n, n6 + 1) ? 1 : 0) + (this.g.solid(n7, n, n6 - 1) ? 1 : 0)) < 2 || !(this.r.nextFloat() < 0.22f)) continue;
            entry.setValue(Gothic.s(Blocks.COBWEB));
        }
    }

    private void sphere(int n, int n2, int n3, double d) {
        int n4 = (int)Math.ceil(d);
        for (int i = -n4; i <= n4; ++i) {
            for (int j = -n4; j <= n4; ++j) {
                for (int k = -n4; k <= n4; ++k) {
                    if (!((double)(i * i + j * j + k * k) <= d * d)) continue;
                    this.carve(n + i, n2 + j, n3 + k);
                }
            }
        }
    }

    private int floorBelow(int n, int n2, int n3) {
        while (n2 > -this.R && this.g.isAir(n, n2 - 1, n3) && this.g.get(n, n2 - 1, n3) != null) {
            --n2;
        }
        return n2;
    }

    private void eggs(int n, int n2, int n3) {
        for (int i = 0; i < 6; ++i) {
            int n4 = n + this.r.nextInt(5) - 2;
            int n5 = n3 + this.r.nextInt(5) - 2;
            int n6 = this.floorBelow(n4, n2 + 2, n5);
            this.g.set(n4, n6, n5, Gothic.s(Blocks.WHITE_WOOL));
            if (!this.r.nextBoolean()) continue;
            this.g.set(n4, n6 + 1, n5, Gothic.s(Blocks.COBWEB));
        }
    }

    private void forge() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10 = 19;
        int n11 = 13;
        int n12 = -3;
        for (n9 = -n10; n9 <= n10; ++n9) {
            for (n8 = -n10; n8 <= n10; ++n8) {
                for (n7 = n12 - n11; n7 <= n12 + n11; ++n7) {
                    double d = (double)n9 * (double)n9 / (double)(n10 * n10) + (double)(n7 - n12) * (double)(n7 - n12) / (double)(n11 * n11) + (double)n8 * (double)n8 / (double)(n10 * n10);
                    if (!(d <= 1.0)) continue;
                    this.carve(n9, n7, n8);
                }
            }
        }
        n9 = -11;
        for (n8 = -n10; n8 <= n10; ++n8) {
            for (n7 = -n10; n7 <= n10; ++n7) {
                for (int i = n12 - n11; i <= n9; ++i) {
                    if (!this.g.isAir(n8, i, n7) || this.g.get(n8, i, n7) == null) continue;
                    this.g.set(n8, i, n7, Gothic.s(Blocks.LAVA));
                }
            }
        }
        n8 = -4;
        int[][] nArrayArray = new int[][]{{-6, -6}, {6, -6}, {-6, 6}, {6, 6}, {0, 0}};
        int n13 = nArrayArray.length;
        for (n6 = 0; n6 < n13; ++n6) {
            int[] nArray = nArrayArray[n6];
            for (n5 = n12 - n11; n5 < n8; ++n5) {
                for (n4 = nArray[0] - 1; n4 <= nArray[0] + 1; ++n4) {
                    for (n3 = nArray[1] - 1; n3 <= nArray[1] + 1; ++n3) {
                        if (!this.inside(n4, n5, n3)) continue;
                        this.g.set(n4, n5, n3, Gothic.s(this.r.nextFloat() < 0.8f ? Blocks.POLISHED_BASALT : Blocks.MAGMA_BLOCK));
                    }
                }
            }
        }
        for (n2 = -9; n2 <= 9; ++n2) {
            for (n13 = -9; n13 <= 9; ++n13) {
                this.g.set(n2, n8 - 1, n13, Gothic.s(this.g.pal.polished()));
                this.g.set(n2, n8 - 2, n13, Gothic.s(this.g.pal.brick()));
            }
        }
        this.g.hall(-6, -5, 6, 5, n8, 7, 4);
        this.g.windowRow(-6, 6, -5, n8 + 2, 3, 4, Direction.Axis.X);
        this.g.windowRow(-6, 6, 5, n8 + 2, 3, 4, Direction.Axis.X);
        this.g.gable(-6, -5, 6, 5, n8 + 8, true);
        this.g.doorway(6, n8, 0, Direction.Axis.Z, 4);
        this.g.doorway(-6, n8, 0, Direction.Axis.Z, 4);
        this.g.tower(-8, -7, n8, 2, 16, true);
        this.g.tower(8, 7, n8, 2, 12, false);
        this.g.set(-3, n8, -3, Gothic.s(Blocks.ANVIL));
        this.g.set(-2, n8, -3, Gothic.s(Blocks.BLAST_FURNACE));
        this.g.set(-1, n8, -3, Gothic.s(Blocks.SMITHING_TABLE));
        this.g.set(2, n8, -3, Gothic.s(Blocks.LAVA_CAULDRON));
        this.g.set(3, n8, -3, Gothic.s(Blocks.LAVA_CAULDRON));
        this.g.altar(3, n8, 2);
        this.g.chest(-4, n8, 3, true);
        this.g.chest(4, n8, -3, false);
        for (n2 = -4; n2 <= 4; n2 += 4) {
            this.g.chainLantern(n2, n8 + 6, 0, 2);
        }
        for (int n14 : new int[]{-1, 1}) {
            for (n5 = 10; n5 <= n10; ++n5) {
                n4 = n14 * n5;
                for (n3 = -1; n3 <= 1; ++n3) {
                    this.g.set(n4, n8 - 1, n3, n3 == 0 ? Gothic.s(this.g.pal.polished()) : Gothic.slab(this.g.pal.slab(), true));
                    for (n = 0; n <= 3; ++n) {
                        this.carve(n4, n8 + n, n3);
                    }
                }
                this.g.set(n4, n8, -2, Gothic.s(Blocks.IRON_BARS));
                this.g.set(n4, n8, 2, Gothic.s(Blocks.IRON_BARS));
                if (n5 % 4 != 0) continue;
                n3 = n12 + n11 - 1;
                for (n = n8 + 1; n <= n3; ++n) {
                    this.g.set(n4, n, -2, Gothic.s(Blocks.CHAIN));
                    this.g.set(n4, n, 2, Gothic.s(Blocks.CHAIN));
                }
            }
        }
        for (int i = 0; i < 4; ++i) {
            n13 = i % 2 == 0 ? -13 : 13;
            n6 = i < 2 ? -12 : 12;
            int n15 = n12 + n11 - 2;
            for (n5 = n8 + 3; n5 <= n15; ++n5) {
                this.g.set(n13, n5, n6, Gothic.s(Blocks.CHAIN));
            }
            for (n5 = -1; n5 <= 1; ++n5) {
                for (n4 = -1; n4 <= 1; ++n4) {
                    for (n3 = n8 - 1; n3 <= n8 + 2; ++n3) {
                        int n16 = n = Math.abs(n5) == 1 || Math.abs(n4) == 1 ? 1 : 0;
                        if (n3 == n8 - 1 || n3 == n8 + 2) {
                            this.g.set(n13 + n5, n3, n6 + n4, Gothic.s(this.g.pal.polished()));
                            continue;
                        }
                        if (n == 0) continue;
                        this.g.set(n13 + n5, n3, n6 + n4, Gothic.s(Blocks.IRON_BARS));
                    }
                }
            }
            this.g.set(n13, n8, n6, Gothic.s(Blocks.SKELETON_SKULL));
        }
        this.entrance(16, 0, n8);
        this.rooms += 6;
    }

    public static enum Kind {
        NECROPOLIS("necropolis"),
        ABYSS("abyss"),
        NEST("nest"),
        FORGE("forge"),
        METROPOLIS("metropolis"),
        CANYON("canyon"),
        SPIRES("spires");

        public final String id;

        public boolean big() {
            return this == METROPOLIS || this == CANYON || this == SPIRES;
        }

        private Kind(String string2) {
            this.id = string2;
        }
    }
}
