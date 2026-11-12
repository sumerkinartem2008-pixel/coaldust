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
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.coaldust.meteor.underground;

import com.coaldust.meteor.gothic.Gothic;
import com.coaldust.meteor.underground.AncientTrees;
import com.coaldust.meteor.underground.Flora;
import com.coaldust.meteor.underground.UndergroundCity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;

final class Spires {
    private static final int F = -30;
    private static final int CY = -8;
    private static final int RY = 36;
    private static final int TH = 7;
    private static final int TIERS = 16;
    private static final double BASE = 22.0;
    private final Gothic g;
    private final RandomSource r;
    private final int R;
    private final int RX;
    private final double inner;
    private final double SH;
    int rooms;
    private int[] levels;
    private int top;
    private int terraceTier;
    private final List<int[]> walls = new ArrayList<int[]>();
    private final List<Integer> wallSteps = new ArrayList<Integer>();
    private final HashSet<Long> path = new HashSet();
    private final List<double[]> blockedAll = new ArrayList<double[]>();
    private final List<double[]> blockedAt = new ArrayList<double[]>();
    private static final double[] TOWER_A = new double[]{0.3, 1.8707963267948966, 3.441592653589793, 5.0123889803846895};
    private static final double[] PILLAR_A = new double[]{0.7853981633974483, 2.356194490192345, 3.9269908169872414, 5.497787143782138};
    private static final Block[] POTS = new Block[]{Blocks.POTTED_FERN, Blocks.POTTED_RED_MUSHROOM, Blocks.POTTED_FLOWERING_AZALEA, Blocks.POTTED_LILY_OF_THE_VALLEY, Blocks.POTTED_BLUE_ORCHID, Blocks.POTTED_CRIMSON_ROOTS, Blocks.POTTED_AZALEA, Blocks.POTTED_ALLIUM, Blocks.POTTED_CORNFLOWER};
    private static final Block[] BANNERS = new Block[]{Blocks.RED_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLACK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.BLUE_WALL_BANNER};
    private static final Block[][] RUGS = new Block[][]{{Blocks.RED_CARPET, Blocks.BLACK_CARPET}, {Blocks.PURPLE_CARPET, Blocks.GRAY_CARPET}, {Blocks.BROWN_CARPET, Blocks.ORANGE_CARPET}, {Blocks.BLUE_CARPET, Blocks.LIGHT_GRAY_CARPET}};
    private static final int[] BRIDGE_K = new int[]{1, 3, -1, -1};

    Spires(Gothic gothic, RandomSource randomSource, int n, double d) {
        this.g = gothic;
        this.r = randomSource;
        this.R = n;
        this.RX = n - 13;
        this.inner = d;
        this.SH = 1.2333333333333334;
    }

    private boolean inside(int n, int n2, int n3) {
        return (double)(n * n + n2 * n2 + n3 * n3) <= this.inner;
    }

    private boolean cave(int n, int n2, int n3) {
        BlockState blockState = this.g.get(n, n2, n3);
        return this.inside(n, n2, n3) && blockState != null && blockState.isAir();
    }

    private static long key(int n, int n2, int n3) {
        return UndergroundCity.Plan.key(n, n2, n3);
    }

    private boolean onPath(int n, int n2, int n3) {
        return this.path.contains(Spires.key(n, n2, n3));
    }

    private void markPath(int n, int n2, int n3) {
        for (int i = 0; i < 3; ++i) {
            this.path.add(Spires.key(n, n2 + i, n3));
        }
    }

    private static int rx(double d, double d2) {
        return (int)Math.round(Math.cos(d) * d2);
    }

    private static int rz(double d, double d2) {
        return (int)Math.round(Math.sin(d) * d2);
    }

    private static Direction dirOf(double d, double d2) {
        if (Math.abs(d) >= Math.abs(d2)) {
            return d > 0.0 ? Direction.EAST : Direction.WEST;
        }
        return d2 > 0.0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static double angDist(double d, double d2) {
        double d3 = Math.abs(d - d2) % (Math.PI * 2);
        return Math.min(d3, Math.PI * 2 - d3);
    }

    private BlockState glass() {
        Block block = this.g.pal.glass();
        Block block2 = block == Blocks.RED_STAINED_GLASS_PANE ? Blocks.RED_STAINED_GLASS : (block == Blocks.PURPLE_STAINED_GLASS_PANE ? Blocks.PURPLE_STAINED_GLASS : (block == Blocks.CYAN_STAINED_GLASS_PANE ? Blocks.CYAN_STAINED_GLASS : (block == Blocks.GRAY_STAINED_GLASS_PANE ? Blocks.GRAY_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS)));
        return Gothic.s(block2);
    }

    private BlockState warmGlass() {
        return Gothic.s(Blocks.ORANGE_STAINED_GLASS);
    }

    private BlockState hanging(Block block) {
        return (BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(true));
    }

    private BlockState facing(Block block, Direction direction) {
        return (BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction);
    }

    private void bed(int n, int n2, int n3, Direction direction) {
        this.g.set(n, n2, n3, (BlockState)this.facing(Blocks.RED_BED, direction).setValue((Property)BlockStateProperties.BED_PART, (Comparable)BedPart.HEAD));
        this.g.set(n - direction.getStepX(), n2, n3 - direction.getStepZ(), (BlockState)this.facing(Blocks.RED_BED, direction).setValue((Property)BlockStateProperties.BED_PART, (Comparable)BedPart.FOOT));
    }

    private int wallAt(double d, int n, int n2) {
        double d2 = Math.cos(d);
        double d3 = Math.sin(d);
        for (int i = n2; i < this.R; ++i) {
            int n3;
            int n4 = (int)Math.round(d2 * (double)i);
            if (this.cave(n4, n, n3 = (int)Math.round(d3 * (double)i)) && this.cave(n4, n + 3, n3)) continue;
            return i;
        }
        return -1;
    }

    private boolean blocked(double d, int n, double d2, double d3) {
        double d4 = (d2 + 1.0) / Math.max(8.0, d3);
        for (double[] dArray : this.blockedAll) {
            if (!(Spires.angDist(d, dArray[0]) < d4 + dArray[1])) continue;
            return true;
        }
        for (double[] dArray : this.blockedAt) {
            if ((int)dArray[1] != n || !(Spires.angDist(d, dArray[0]) < d4 + dArray[2])) continue;
            return true;
        }
        return false;
    }

    int build() {
        int n = 0;
        while (-30 + 7 * n <= 8) {
            ++n;
        }
        this.levels = new int[n];
        for (int i = 0; i < n; ++i) {
            this.levels[i] = -30 + 7 * i;
        }
        this.top = n - 1;
        this.terraceTier = (this.levels[this.top] - -30) / 7;
        this.cavern();
        this.babel();
        double[] dArray = TOWER_A;
        int n2 = dArray.length;
        for (int i = 0; i < n2; ++i) {
            double d = dArray[i];
            this.blockedAll.add(new double[]{d, 0.06});
        }
        for (int i = 0; i < BRIDGE_K.length; ++i) {
            this.blockedAt.add(new double[]{1.2 + (double)i * Math.PI / 2.0, this.bridgeK(i), 0.04});
        }
        double[] dArray2 = new double[]{1.4, 3.0, 4.55, 6.1};
        for (n2 = 0; n2 < dArray2.length; ++n2) {
            this.blockedAt.add(new double[]{dArray2[n2], n2 % 2 == 0 ? 2.0 : 4.0, 0.08});
        }
        this.measureWalls();
        this.galleries();
        this.terrace();
        this.bridges();
        this.stairTowers();
        this.pillars();
        this.avenues();
        this.gardens();
        this.floatingHouses(dArray2);
        this.hangingGardens();
        this.lights();
        return this.rooms;
    }

    private void cavern() {
        int n;
        int n2;
        double d = this.r.nextDouble() * 6.0;
        int n3 = 30;
        for (n2 = -this.RX - 4; n2 <= this.RX + 4; ++n2) {
            for (n = -this.RX - 4; n <= this.RX + 4; ++n) {
                for (int i = -30; i <= n3; ++i) {
                    double d2 = Math.atan2(n, n2);
                    double d3 = (double)(n2 * n2 + n * n) / (double)(this.RX * this.RX) * (1.0 - 0.08 * Math.sin(d2 * 5.0 + d)) + (double)((i - -8) * (i - -8)) / 1296.0;
                    if (!(d3 <= 1.0) || !this.inside(n2, i, n)) continue;
                    this.g.air(n2, i, n);
                }
            }
        }
        for (n2 = -this.RX - 4; n2 <= this.RX + 4; ++n2) {
            for (n = -this.RX - 4; n <= this.RX + 4; ++n) {
                if (!this.cave(n2, -30, n)) continue;
                this.g.set(n2, -31, n, this.r.nextFloat() < 0.1f ? Gothic.s(Blocks.COBBLED_DEEPSLATE) : this.g.floorTile(n2, n));
            }
        }
    }

    private void measureWalls() {
        for (int i = 0; i <= this.top; ++i) {
            int n = this.levels[i];
            int n2 = (int)(22.0 - this.SH * (double)i) + 3;
            int n3 = this.wallAt(0.0, n, n2);
            if (n3 < 0) {
                n3 = this.RX - 4;
            }
            int n4 = (int)(Math.PI * 2 * (double)n3 / 0.5);
            int[] nArray = new int[n4];
            for (int j = 0; j < n4; ++j) {
                nArray[j] = this.wallAt((double)j * Math.PI * 2.0 / (double)n4, n, Math.max(n2, i == this.top ? 25 : n2));
            }
            this.walls.add(nArray);
            this.wallSteps.add(n4);
        }
    }

    private int wallOf(int n, double d) {
        int[] nArray = this.walls.get(n);
        int n2 = this.wallSteps.get(n);
        return nArray[Math.floorMod((int)Math.round(d / (Math.PI * 2) * (double)n2), n2)];
    }

    private void babel() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6 = 82;
        for (n5 = -30; n5 <= n6 + 14; ++n5) {
            int n7 = Math.min(15, Math.max(0, (n5 - -30) / 7));
            double d = 22.0 - this.SH * (double)n7 + 2.5;
            for (n4 = (int)(-d) - 1; n4 <= (int)d + 1; ++n4) {
                for (int i = (int)(-d) - 1; i <= (int)d + 1; ++i) {
                    if (!((double)(n4 * n4 + i * i) <= d * d) || n5 <= this.R - 8 && !this.inside(n4, n5, i)) continue;
                    this.g.air(n4, n5, i);
                }
            }
        }
        for (n5 = 0; n5 < 16; ++n5) {
            double d = 22.0 - this.SH * (double)n5;
            n3 = -30 + n5 * 7;
            n4 = Math.max(8, (int)Math.round(Math.PI * 2 * d / 4.0));
            for (int i = (int)(-d) - 1; i <= (int)d + 1; ++i) {
                for (n2 = (int)(-d) - 1; n2 <= (int)d + 1; ++n2) {
                    double d2 = Math.sqrt(i * i + n2 * n2);
                    if (d2 > d + 0.4) continue;
                    boolean bl = d2 > d - 0.7;
                    double d3 = (Math.atan2(n2, i) + Math.PI) / (Math.PI * 2) * (double)n4;
                    double d4 = d3 - Math.floor(d3);
                    this.g.set(i, n3 - 1, n2, n5 == 0 ? this.g.floorTile(i, n2) : (bl ? Gothic.s(this.g.pal.polished()) : Gothic.s(Blocks.SPRUCE_PLANKS)));
                    for (int j = n3; j < n3 + 7; ++j) {
                        boolean bl2;
                        int n8 = j - n3;
                        if (!bl) {
                            this.g.air(i, j, n2);
                            continue;
                        }
                        boolean bl3 = d4 > 0.3 && d4 < 0.7 && n8 >= 2 && n8 <= 4;
                        boolean bl4 = bl2 = d4 > 0.2 && d4 < 0.8 && n8 == 5;
                        this.g.set(i, j, n2, bl3 ? this.glass() : (bl2 ? Gothic.s(this.g.pal.chiseled()) : (n8 == 6 || n8 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick())));
                    }
                }
            }
            if (n5 < 15) {
                double d5 = d - this.SH;
                for (int i = (int)(-d) - 1; i <= (int)d + 1; ++i) {
                    for (int j = (int)(-d) - 1; j <= (int)d + 1; ++j) {
                        double d6 = Math.sqrt(i * i + j * j);
                        if (d6 > d + 0.4 || d6 <= d5 + 0.4) continue;
                        this.g.set(i, n3 + 7 - 1, j, this.g.floorTile(i, j));
                        if (!(d6 > d - 0.6) || (i + j & 1) != 0) continue;
                        this.g.set(i, n3 + 7, j, Gothic.s(this.g.pal.wall()));
                    }
                }
            }
            if (d >= 10.5) {
                this.towerRooms(n5, n3, d);
            } else {
                this.towerHall(n5, n3, d);
            }
            if (n5 >= 15) continue;
            int[][] nArrayArray = new int[][]{{4, 4}, {-4, -4}, {4, -4}, {-4, 4}};
            n2 = nArrayArray.length;
            for (int i = 0; i < n2; ++i) {
                int[] nArray = nArrayArray[i];
                if (!(Math.sqrt(nArray[0] * nArray[0] + nArray[1] * nArray[1]) < d - this.SH - 0.8)) continue;
                this.g.set(nArray[0], n3 + 7 - 1, nArray[1], Flora.LUMEN.get());
            }
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int n9;
            for (n9 = 20; n9 <= 23; ++n9) {
                for (n3 = -1; n3 <= 1; ++n3) {
                    n4 = direction.getStepX() * n9 + direction.getClockWise().getStepX() * n3;
                    int n10 = direction.getStepZ() * n9 + direction.getClockWise().getStepZ() * n3;
                    for (n2 = -30; n2 < -27; ++n2) {
                        this.g.air(n4, n2, n10);
                    }
                    this.markPath(n4, -30, n10);
                }
            }
            n9 = direction.getStepX() * 22;
            n3 = direction.getStepZ() * 22;
            for (n4 = -2; n4 <= 2; ++n4) {
                this.g.set(n9 + direction.getClockWise().getStepX() * n4, -27, n3 + direction.getClockWise().getStepZ() * n4, Gothic.s(this.g.pal.chiseled()));
            }
        }
        double d = 22.0 - this.SH * 15.0;
        for (n = (int)(-d) - 1; n <= (int)d + 1; ++n) {
            for (n3 = (int)(-d) - 1; n3 <= (int)d + 1; ++n3) {
                double d7 = Math.sqrt(n * n + n3 * n3);
                if (d7 > d + 0.4) continue;
                this.g.set(n, n6, n3, Gothic.s(this.g.pal.polished()));
                if (!(d7 > d - 0.6) || (n + n3 & 1) != 0) continue;
                this.g.set(n, n6 + 1, n3, Gothic.s(this.g.pal.wall()));
            }
        }
        this.g.spiralStair(0, 0, n6 - 1, -30, 1);
        this.g.spire(0, 0, n6 + 1, 1, 10);
        for (n = 0; n < 16; ++n) {
            double d8 = 22.0 - this.SH * (double)n;
            if (d8 < 5.0) continue;
            int n11 = -30 + n * 7;
            for (n2 = -3; n2 <= 3; ++n2) {
                for (int i = -3; i <= 3; ++i) {
                    if (Math.max(Math.abs(n2), Math.abs(i)) != 3 || !this.g.isAir(n2, n11, i) || this.g.isAir(n2, n11 - 1, i)) continue;
                    boolean bl = false;
                    for (int j = -1; j <= 1 && !bl; ++j) {
                        block21: for (int k = -1; k <= 1 && !bl; ++k) {
                            int n12 = n2 + j;
                            int n13 = i + k;
                            if (Math.max(Math.abs(n12), Math.abs(n13)) > 2) continue;
                            for (int i2 = n11 - 2; i2 <= n11 + 1; ++i2) {
                                BlockState blockState = this.g.get(n12, i2, n13);
                                if (blockState == null || !blockState.is(this.g.pal.stairs())) continue;
                                bl = true;
                                continue block21;
                            }
                        }
                    }
                    if (bl) continue;
                    this.g.set(n2, n11, i, Gothic.s(this.g.pal.wall()));
                }
            }
        }
    }

    private List<int[]> radialCut(double d, double d2, double d3, int n, int n2, boolean bl) {
        int n3;
        ArrayList<int[]> arrayList = new ArrayList<int[]>();
        int n4 = Integer.MIN_VALUE;
        int n5 = 0;
        for (double d4 = d2; d4 <= d3; d4 += 0.25) {
            n3 = Spires.rx(d, d4);
            int n6 = Spires.rz(d, d4);
            if (n3 == n4 && n6 == n5) continue;
            if (n4 != Integer.MIN_VALUE && n3 != n4 && n6 != n5) {
                arrayList.add(new int[]{n4, n6});
            }
            arrayList.add(new int[]{n3, n6});
            n4 = n3;
            n5 = n6;
        }
        for (int[] nArray : arrayList) {
            for (n3 = 0; n3 < n2; ++n3) {
                this.g.air(nArray[0], n + n3, nArray[1]);
            }
            if (bl && this.g.isAir(nArray[0], n - 1, nArray[1]) && this.g.get(nArray[0], n - 1, nArray[1]) != null) {
                this.g.set(nArray[0], n - 1, nArray[1], this.g.floorTile(nArray[0], nArray[1]));
            }
            this.markPath(nArray[0], n, nArray[1]);
        }
        return arrayList;
    }

    private Room roomType(int n, int n2) {
        return switch (n) {
            case 0 -> {
                if (n2 % 2 == 0) {
                    yield Room.STORE;
                }
                yield Room.DINING;
            }
            case 1 -> {
                if (n2 % 3 == 0) {
                    yield Room.CELL;
                }
                if (n2 % 3 == 1) {
                    yield Room.ARMORY;
                }
                yield Room.STORE;
            }
            case 2, 3 -> {
                if (n2 % 4 == 3) {
                    yield Room.STUDY;
                }
                yield Room.DORM;
            }
            case 4 -> {
                if (n2 % 2 == 0) {
                    yield Room.STUDY;
                }
                yield Room.DORM;
            }
            case 5 -> {
                if (n2 % 2 == 0) {
                    yield Room.LIBRARY;
                }
                yield Room.READING;
            }
            case 6 -> {
                if (n2 % 2 == 0) {
                    yield Room.GREENHOUSE;
                }
                yield Room.ALCHEMY;
            }
            case 7 -> {
                if (n2 % 3 == 2) {
                    yield Room.STUDY;
                }
                yield Room.GREENHOUSE;
            }
            case 8 -> {
                if (n2 == 0) {
                    yield Room.THRONE;
                }
                yield Room.CHAPEL;
            }
            default -> n2 % 2 == 0 ? Room.CHAPEL : Room.LIBRARY;
        };
    }

    private void towerRooms(int n, int n2, double d) {
        int n3;
        int n4;
        int n5;
        double d2;
        int n6;
        double d3 = 5.6;
        int n7 = n2 + 7 - 2;
        int n8 = 4 * Math.max(1, (int)Math.round(Math.PI * 2 * d / 12.0 / 4.0));
        double d4 = Math.PI / (double)n8;
        for (n6 = (int)(-d); n6 <= (int)d; ++n6) {
            for (int i = (int)(-d); i <= (int)d; ++i) {
                d2 = Math.sqrt(n6 * n6 + i * i);
                if (!(Math.abs(d2 - d3) < 0.6)) continue;
                for (n5 = n2; n5 <= n7; ++n5) {
                    this.g.set(n6, n5, i, n5 == n7 ? Gothic.s(this.g.pal.polished()) : this.g.brick());
                }
            }
        }
        for (n6 = 0; n6 < n8; ++n6) {
            double d5 = (double)n6 * Math.PI * 2.0 / (double)n8 + d4;
            int n9 = Integer.MIN_VALUE;
            n5 = 0;
            for (double d6 = d3; d6 < d - 0.5; d6 += 0.3) {
                int[][] nArrayArray;
                int[][] nArrayArray2;
                n4 = Spires.rx(d5, d6);
                int n10 = Spires.rz(d5, d6);
                if (n4 == n9 && n10 == n5) continue;
                if (n9 != Integer.MIN_VALUE && n4 != n9 && n10 != n5) {
                    int[][] nArrayArray3 = new int[2][];
                    nArrayArray3[0] = new int[]{n4, n10};
                    nArrayArray2 = nArrayArray3;
                    nArrayArray3[1] = new int[]{n9, n10};
                } else {
                    int[][] nArrayArray4 = new int[1][];
                    nArrayArray2 = nArrayArray4;
                    nArrayArray4[0] = new int[]{n4, n10};
                }
                int[][] nArrayArray5 = nArrayArray = nArrayArray2;
                n3 = nArrayArray5.length;
                for (int i = 0; i < n3; ++i) {
                    int[] nArray = nArrayArray5[i];
                    for (int j = n2; j <= n7; ++j) {
                        this.g.set(nArray[0], j, nArray[1], j == n7 || j == n2 ? Gothic.s(this.g.pal.polished()) : this.g.brick());
                    }
                }
                n9 = n4;
                n5 = n10;
            }
        }
        n6 = n == 0 || n == this.terraceTier ? 1 : 0;
        for (int i = 0; i < n8; ++i) {
            double d7;
            d2 = (double)i * Math.PI * 2.0 / (double)n8 + d4;
            double d8 = d2 + Math.PI / (double)n8;
            Room room = this.roomType(n, i);
            this.radialCut(d8, d3 - 1.0, d3 + 1.1, n2, 2, false);
            this.g.set(Spires.rx(d8, d3), n2 + 2, Spires.rz(d8, d3), Gothic.s(this.g.pal.chiseled()));
            for (n4 = -1; n4 <= 1; n4 += 2) {
                d7 = d8 + (double)n4 * 2.4 / d3;
                int n11 = Spires.rx(d7, d3);
                if (this.onPath(n11, n2, n3 = Spires.rz(d7, d3))) continue;
                this.g.set(n11, n2 + 1, n3, this.glass());
                this.g.set(n11, n2 + 2, n3, this.glass());
            }
            n4 = (n6 != 0 || i % 2 == 0) && room != Room.CELL ? 1 : 0;
            d7 = d8;
            if (n4 != 0) {
                this.radialCut(d7, d - 1.3, d + 1.7, n2, 2, true);
                this.g.set(Spires.rx(d7, d), n2 + 2, Spires.rz(d7, d), Gothic.s(this.g.pal.chiseled()));
                this.g.set(Spires.rx(d7, d), n2 + 3, Spires.rz(d7, d), Gothic.s(this.g.pal.chiseled()));
            }
            this.furnish(room, d2, d2 + Math.PI * 2 / (double)n8, d8, d3, d, n2, n);
            this.sills(d2, d2 + Math.PI * 2 / (double)n8, d, n2);
            this.g.set(Spires.rx(d8, (d3 + d) / 2.0), n2 + 7 - 1, Spires.rz(d8, (d3 + d) / 2.0), Flora.LUMEN.get());
            ++this.rooms;
        }
    }

    private void furnish(Room room, double d, double d2, double d3, double d4, double d5, int n, int n2) {
        int n3;
        int n4;
        double d6;
        double d7 = d5 - 1.5;
        ArrayList<int[]> arrayList = new ArrayList<int[]>();
        for (double d8 = d + 1.4 / d7; d8 <= d2 - 1.4 / d7; d8 += 1.05 / d7) {
            int n5;
            int n6 = Spires.rx(d8, d7);
            if (this.onPath(n6, n, n5 = Spires.rz(d8, d7)) || Spires.angDist(d8, d3) < 1.6 / d7 || !this.g.isAir(n6, n, n5)) continue;
            arrayList.add(new int[]{n6, n5});
        }
        ArrayList<int[]> arrayList2 = new ArrayList<int[]>();
        for (double d9 = d6 = Math.max(d4 + 1.6, 5.5 / (d2 - d)); d9 <= d5 - 2.6; d9 += 1.0) {
            for (double d10 : new double[]{d + 1.2 / d9, d2 - 1.2 / d9}) {
                n4 = Spires.rx(d10, d9);
                if (this.onPath(n4, n, n3 = Spires.rz(d10, d9)) || !this.g.isAir(n4, n, n3)) continue;
                arrayList2.add(new int[]{n4, n3});
            }
        }
        Direction direction = Spires.dirOf(-Math.cos(d3), -Math.sin(d3));
        Direction direction2 = direction.getOpposite();
        double d11 = (d4 + d5) / 2.0;
        int n7 = n + 7 - 2;
        for (double d10 = d + 0.6 / d11; d10 <= d2 - 0.6 / d11; d10 += 0.4 / d11) {
            n4 = Spires.rx(d10, d11);
            if (!this.g.isAir(n4, n7, n3 = Spires.rz(d10, d11))) continue;
            this.g.set(n4, n7, n3, this.log(Blocks.STRIPPED_DARK_OAK_LOG, Spires.dirOf(-Math.sin(d10), Math.cos(d10)).getAxis()));
        }
        int n8 = Spires.rx(d3, d11 + 0.6);
        int n9 = Spires.rz(d3, d11 + 0.6);
        n4 = Spires.rx(d3, d11);
        n3 = Spires.rz(d3, d11);
        int n10 = 0;
        Block block = n2 % 2 == 0 ? Blocks.RED_CARPET : Blocks.BLACK_CARPET;
        switch (room.ordinal()) {
            case 0: {
                for (int[] nArray : arrayList) {
                    this.g.set(nArray[0], n, nArray[1], Gothic.s(this.r.nextFloat() < 0.25f ? Blocks.DECORATED_POT : Blocks.BARREL));
                    if (!(this.r.nextFloat() < 0.6f)) continue;
                    this.g.set(nArray[0], n + 1, nArray[1], Gothic.s(this.r.nextFloat() < 0.5f ? Blocks.BARREL : Blocks.HAY_BLOCK));
                }
                for (int[] nArray : arrayList2) {
                    this.g.set(nArray[0], n, nArray[1], Gothic.s(n10++ % 3 == 0 ? Blocks.HAY_BLOCK : Blocks.BARREL));
                }
                if (!arrayList2.isEmpty()) {
                    Object object = (int[])arrayList2.get(0);
                    this.g.chest((int)object[0], n, (int)object[1], false);
                }
                this.chandelier(n4, n7, n3, 1);
                break;
            }
            case 1: {
                int n11;
                int n12 = Spires.rx(d3, d5 - 1.2);
                int n13 = Spires.rz(d3, d5 - 1.2);
                this.fireplace(n12, n, n13, direction, n7 + 1);
                int n14 = 0;
                double d12 = d11 - 1.0;
                while (d12 <= d11 + 1.0 && d12 <= d5 - 3.0) {
                    int n15;
                    int n16 = Spires.rx(d3, d12);
                    if (!this.onPath(n16, n, n15 = Spires.rz(d3, d12))) {
                        this.g.set(n16, n, n15, Gothic.slab(Blocks.POLISHED_BLACKSTONE_SLAB, true));
                        this.g.set(n16, n + 1, n15, n14 == 1 ? Gothic.s(Blocks.CAKE) : Gothic.candle(1 + this.r.nextInt(3)));
                        Direction direction3 = direction.getClockWise();
                        for (int i = -1; i <= 1; i += 2) {
                            int n17 = n16 + direction3.getStepX() * i;
                            if (!this.g.isAir(n17, n, n11 = n15 + direction3.getStepZ() * i) || this.onPath(n17, n, n11)) continue;
                            this.g.set(n17, n, n11, Gothic.stairs(Blocks.DARK_OAK_STAIRS, i > 0 ? direction3 : direction3.getOpposite(), false));
                        }
                    }
                    d12 += 1.0;
                    ++n14;
                }
                for (int[] nArray : arrayList2) {
                    this.g.set(nArray[0], n, nArray[1], Gothic.s(n10++ % 2 == 0 ? Blocks.BARREL : Blocks.SMOKER));
                }
                this.chandelier(n4, n7, n3, 1);
                break;
            }
            case 2: {
                Block[] blockArray = new Block[]{Blocks.ANVIL, Blocks.SMITHING_TABLE, Blocks.BARREL, Blocks.FLETCHING_TABLE, Blocks.TARGET};
                for (int[] nArray : arrayList) {
                    Block block2 = blockArray[n10++ % blockArray.length];
                    this.g.set(nArray[0], n, nArray[1], block2 == Blocks.ANVIL ? this.facing(block2, direction) : Gothic.s(block2));
                }
                for (int[] nArray : arrayList2) {
                    this.g.set(nArray[0], n, nArray[1], Gothic.s(Blocks.DARK_OAK_FENCE));
                    this.g.set(nArray[0], n + 1, nArray[1], Gothic.s(n10++ % 2 == 0 ? Blocks.SKELETON_SKULL : Blocks.IRON_BARS));
                }
                if (!arrayList.isEmpty()) {
                    Object object = (int[])arrayList.get(arrayList.size() - 1);
                    this.g.chest((int)object[0], n + 0, (int)object[1], true);
                }
                this.chandelier(n4, n7, n3, 1);
                break;
            }
            case 3: {
                int n18;
                int n19;
                double d13 = d11 + 0.8;
                int n20 = Integer.MIN_VALUE;
                int n21 = 0;
                for (double d14 = d; d14 <= d2; d14 += 0.2 / d13) {
                    int[][] nArrayArray;
                    int[][] nArrayArray2;
                    int n22 = Spires.rx(d14, d13);
                    n19 = Spires.rz(d14, d13);
                    if (n20 != Integer.MIN_VALUE && n22 != n20 && n19 != n21) {
                        int[][] nArrayArray3 = new int[2][];
                        nArrayArray3[0] = new int[]{n22, n19};
                        nArrayArray2 = nArrayArray3;
                        nArrayArray3[1] = new int[]{n20, n19};
                    } else {
                        int[][] nArrayArray4 = new int[1][];
                        nArrayArray2 = nArrayArray4;
                        nArrayArray4[0] = new int[]{n22, n19};
                    }
                    for (int[] nArray : nArrayArray = nArrayArray2) {
                        for (n18 = n; n18 <= n7; ++n18) {
                            BlockState blockState = this.g.get(nArray[0], n18, nArray[1]);
                            if (blockState == null || !blockState.isAir() && !blockState.is(Blocks.RED_CARPET) && !blockState.is(Blocks.BLACK_CARPET)) continue;
                            this.g.set(nArray[0], n18, nArray[1], Gothic.s(Blocks.IRON_BARS));
                        }
                    }
                    n20 = n22;
                    n21 = n19;
                }
                for (int[] nArray : arrayList) {
                    float f = this.r.nextFloat();
                    if (f < 0.3f) {
                        for (n19 = n + 2; n19 <= n7; ++n19) {
                            this.g.set(nArray[0], n19, nArray[1], Gothic.s(Blocks.CHAIN));
                        }
                        continue;
                    }
                    if (f < 0.5f) {
                        this.g.set(nArray[0], n, nArray[1], Gothic.s(Blocks.HAY_BLOCK));
                        continue;
                    }
                    if (f < 0.65f) {
                        this.g.set(nArray[0], n, nArray[1], Gothic.s(Blocks.SKELETON_SKULL));
                        continue;
                    }
                    if (!(f < 0.8f)) continue;
                    this.g.set(nArray[0], n + 3, nArray[1], Gothic.s(Blocks.COBWEB));
                }
                if (arrayList2.size() >= 2) {
                    int[] nArray;
                    Object object = (int[])arrayList2.get(0);
                    nArray = (int[])arrayList2.get(1);
                    this.table((int)object[0], n, (int)object[1], true);
                    this.chair(nArray[0], n, nArray[1], Spires.dirOf(nArray[0] - object[0], nArray[1] - object[1]));
                }
                this.chandelier(n4, n7, n3, 0);
                break;
            }
            case 4: {
                int n18;
                int n11;
                for (double d15 = Math.max(d4 + 1.8, 6.5 / (d2 - d)); d15 <= d5 - 2.0; d15 += 2.0) {
                    for (int i = -1; i <= 1; i += 2) {
                        double d16 = i < 0 ? d + 1.3 / d15 : d2 - 1.3 / d15;
                        int n23 = Spires.rx(d16, d15);
                        int n24 = Spires.rz(d16, d15);
                        double d17 = i < 0 ? d + 2.5 / d15 : d2 - 2.5 / d15;
                        int n25 = Spires.rx(d17, d15);
                        n11 = Spires.rz(d17, d15);
                        if (n25 == n23 && n11 == n24) continue;
                        Direction direction4 = Spires.dirOf(n23 - n25, n24 - n11);
                        int n26 = n23 - direction4.getStepX();
                        n18 = n24 - direction4.getStepZ();
                        if (!this.g.isAir(n23, n, n24) || !this.g.isAir(n26, n, n18) || this.onPath(n23, n, n24) || this.onPath(n26, n, n18)) continue;
                        this.bed(n23, n, n24, direction4);
                        this.g.set(n23, n + 2, n24, Gothic.slab(Blocks.DARK_OAK_SLAB, true));
                        this.g.set(n26, n + 2, n18, Gothic.slab(Blocks.DARK_OAK_SLAB, true));
                        int n27 = n26 - direction4.getStepX();
                        int n28 = n18 - direction4.getStepZ();
                        if (!this.g.isAir(n27, n, n28) || this.onPath(n27, n, n28)) continue;
                        if (this.r.nextFloat() < 0.15f) {
                            this.g.chest(n27, n, n28, false);
                            continue;
                        }
                        this.g.set(n27, n, n28, Gothic.s(Blocks.BARREL));
                    }
                }
                for (int[] nArray : arrayList) {
                    if (n10++ % 2 == 0) {
                        this.g.set(nArray[0], n, nArray[1], Gothic.s(Blocks.DARK_OAK_PLANKS));
                        this.g.set(nArray[0], n + 1, nArray[1], Gothic.candle(1 + this.r.nextInt(3)));
                        continue;
                    }
                    this.g.set(nArray[0], n, nArray[1], Gothic.s(Blocks.BARREL));
                }
                this.chandelier(n4, n7, n3, 0);
                break;
            }
            case 5: {
                Object object;
                for (int[] nArray : arrayList) {
                    this.g.set(nArray[0], n, nArray[1], Gothic.s(Blocks.BOOKSHELF));
                    this.g.set(nArray[0], n + 1, nArray[1], Gothic.s(n10++ % 3 == 0 ? Blocks.SKELETON_SKULL : Blocks.BOOKSHELF));
                }
                if (arrayList2.size() >= 2) {
                    int[] nArray;
                    object = (int[])arrayList2.get(0);
                    nArray = (int[])arrayList2.get(1);
                    this.g.set((int)object[0], n, (int)object[1], Gothic.stairs(Blocks.DARK_OAK_STAIRS, Spires.dirOf((double)(object[0] - nArray[0]), (double)(object[1] - nArray[1])), true));
                    this.g.set((int)object[0], n + 1, (int)object[1], Gothic.candle(3));
                    this.chair(nArray[0], n, nArray[1], Spires.dirOf(nArray[0] - object[0], nArray[1] - object[1]));
                }
                if (arrayList2.size() >= 3) {
                    object = (int[])arrayList2.get(2);
                    this.g.set((int)object[0], n, (int)object[1], Gothic.s(Blocks.CARTOGRAPHY_TABLE));
                }
                if (arrayList2.size() >= 4) {
                    object = (int[])arrayList2.get(3);
                    this.g.chest((int)object[0], n, (int)object[1], n2 > 3);
                }
                this.g.set(n8, n, n9, Gothic.s(Blocks.ENCHANTING_TABLE));
                this.chandelier(n4, n7, n3, 0);
                break;
            }
            case 6: {
                int n29;
                for (int[] nArray : arrayList) {
                    for (n29 = n; n29 < n + 4; ++n29) {
                        this.g.set(nArray[0], n29, nArray[1], Gothic.s((nArray[0] + n29) % 3 == 0 ? Blocks.CHISELED_BOOKSHELF : Blocks.BOOKSHELF));
                    }
                }
                for (int[] nArray : arrayList2) {
                    for (n29 = n; n29 < n + 2; ++n29) {
                        this.g.set(nArray[0], n29, nArray[1], Gothic.s(Blocks.BOOKSHELF));
                    }
                    if (!(this.r.nextFloat() < 0.5f)) continue;
                    this.g.set(nArray[0], n + 2, nArray[1], this.r.nextBoolean() ? Gothic.candle(1 + this.r.nextInt(3)) : Gothic.s(Blocks.SKELETON_SKULL));
                }
                this.g.set(n8, n, n9, this.facing(Blocks.LECTERN, direction));
                this.chandelier(n4, n7, n3, 0);
                break;
            }
            case 7: {
                for (int[] nArray : arrayList) {
                    for (int i = n; i < n + 2; ++i) {
                        this.g.set(nArray[0], i, nArray[1], Gothic.s(Blocks.BOOKSHELF));
                    }
                }
                int n30 = 0;
                while (n30 + 1 < arrayList2.size()) {
                    int[] nArray;
                    nArray = (int[])arrayList2.get(n30);
                    int[] nArray2 = (int[])arrayList2.get(n30 + 1);
                    this.table(nArray[0], n, nArray[1], true);
                    this.chair(nArray2[0], n, nArray2[1], Spires.dirOf(nArray2[0] - nArray[0], nArray2[1] - nArray[1]));
                    n30 += 2;
                }
                this.g.set(n8, n, n9, Gothic.s(Blocks.POTTED_WITHER_ROSE));
                this.chandelier(n4, n7, n3, 0);
                break;
            }
            case 8: {
                for (int[] nArray : arrayList) {
                    this.g.set(nArray[0], n - 1, nArray[1], Gothic.s(Blocks.MOSS_BLOCK));
                    float f = this.r.nextFloat();
                    this.g.set(nArray[0], n, nArray[1], f < 0.25f ? Flora.SHRUB.get() : (f < 0.45f ? Flora.BERRY.get() : (f < 0.6f ? Flora.FERN.get() : (f < 0.75f ? Gothic.s(Blocks.FLOWERING_AZALEA) : Flora.GRASS.get()))));
                }
                for (int[] nArray : arrayList2) {
                    this.g.set(nArray[0], n - 1, nArray[1], Gothic.s(Blocks.MOSS_BLOCK));
                    this.g.set(nArray[0], n, nArray[1], n10++ % 2 == 0 ? Flora.FERN.get() : Flora.GRASS.get());
                }
                this.g.set(n8, n, n9, Gothic.s(Blocks.WATER_CAULDRON));
                this.g.set(n4, n7 - 1, n3, Gothic.s(Blocks.SPORE_BLOSSOM));
                for (double d18 = d11 - 1.0; d18 <= d11 + 1.2; d18 += 0.5) {
                    for (double d19 = d3 - 1.5 / d18; d19 <= d3 + 1.5 / d18; d19 += 0.4 / d18) {
                        int n31;
                        int n32 = Spires.rx(d19, d18);
                        if (!this.g.isAir(n32, n, n31 = Spires.rz(d19, d18)) || this.onPath(n32, n, n31) || !(this.r.nextFloat() < 0.4f)) continue;
                        this.g.set(n32, n, n31, Gothic.s(Blocks.MOSS_CARPET));
                    }
                }
                break;
            }
            case 9: {
                for (int[] nArray : arrayList) {
                    int n33 = n10++ % 4;
                    this.g.set(nArray[0], n, nArray[1], n33 == 0 ? Gothic.s(Blocks.BREWING_STAND) : (n33 == 1 ? Gothic.s(Blocks.WATER_CAULDRON) : (n33 == 2 ? Gothic.slab(Blocks.POLISHED_BLACKSTONE_SLAB, true) : Gothic.s(Blocks.LAVA_CAULDRON))));
                    if (n33 != 2) continue;
                    this.g.set(nArray[0], n + 1, nArray[1], Gothic.s(Blocks.AMETHYST_CLUSTER));
                }
                for (int[] nArray : arrayList2) {
                    this.g.set(nArray[0], n, nArray[1], Gothic.s(Blocks.BOOKSHELF));
                    this.g.set(nArray[0], n + 1, nArray[1], this.pot());
                }
                this.g.set(n8, n, n9, Gothic.s(Blocks.CRYING_OBSIDIAN));
                this.g.set(n8, n + 1, n9, Gothic.s(Blocks.BREWING_STAND));
                this.chandelier(n4, n7, n3, 0);
                break;
            }
            case 10: {
                int n34;
                int n35 = Spires.rx(d3, d5 - 1.6);
                int n36 = Spires.rz(d3, d5 - 1.6);
                this.g.set(n35, n, n36, Gothic.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, direction2, false));
                Direction direction5 = direction.getClockWise();
                for (n34 = -1; n34 <= 1; n34 += 2) {
                    this.g.set(n35 + direction5.getStepX() * n34, n, n36 + direction5.getStepZ() * n34, Gothic.s(Blocks.POLISHED_BLACKSTONE_WALL));
                    this.g.set(n35 + direction5.getStepX() * n34 * 2, n, n36 + direction5.getStepZ() * n34 * 2, Gothic.s(this.g.pal.polished()));
                    this.g.set(n35 + direction5.getStepX() * n34 * 2, n + 1, n36 + direction5.getStepZ() * n34 * 2, this.g.lantern(false));
                }
                n34 = n35 + direction2.getStepX();
                int n37 = n36 + direction2.getStepZ();
                for (int i = n; i <= n + 3; ++i) {
                    this.g.set(n34, i, n37, i == n + 3 ? Gothic.s(Blocks.CHISELED_POLISHED_BLACKSTONE) : Gothic.s(Blocks.GILDED_BLACKSTONE));
                }
                this.g.set(n34, n + 4, n37, Gothic.s(Blocks.WITHER_SKELETON_SKULL));
                for (double d20 = d4 + 1.2; d20 <= d5 - 2.4; d20 += 0.4) {
                    int n38;
                    int n39 = Spires.rx(d3, d20);
                    if (!this.g.isAir(n39, n, n38 = Spires.rz(d3, d20))) continue;
                    this.g.set(n39, n, n38, Gothic.s(Blocks.RED_CARPET));
                }
                for (int[] nArray : arrayList2) {
                    this.g.set(nArray[0], n, nArray[1], Gothic.s(this.g.pal.polished()));
                    this.g.set(nArray[0], n + 1, nArray[1], Gothic.s(Blocks.SKELETON_SKULL));
                }
                this.chandelier(n4, n7, n3, 0);
                break;
            }
            case 11: {
                int n40 = Spires.rx(d3, d5 - 1.6);
                int n41 = Spires.rz(d3, d5 - 1.6);
                this.g.set(n40, n, n41, Gothic.s(this.g.pal.chiseled()));
                this.g.set(n40, n + 1, n41, Gothic.candle(4));
                for (int[] nArray : arrayList) {
                    this.g.set(nArray[0], n, nArray[1], Gothic.candle(1 + this.r.nextInt(4)));
                }
                for (double d21 = d11 - 0.8; d21 <= d11 + 0.4; d21 += 1.2) {
                    for (int i = -1; i <= 1; i += 2) {
                        int n42;
                        int n43 = Spires.rx(d3 + (double)i * 1.6 / d21, d21);
                        if (!this.g.isAir(n43, n, n42 = Spires.rz(d3 + (double)i * 1.6 / d21, d21)) || this.onPath(n43, n, n42)) continue;
                        this.g.set(n43, n, n42, Gothic.stairs(Blocks.DARK_OAK_STAIRS, direction, false));
                    }
                }
                if (!arrayList2.isEmpty()) {
                    int[] nArray = (int[])arrayList2.get(arrayList2.size() - 1);
                    this.g.set(nArray[0], n, nArray[1], Gothic.s(this.g.pal.polished()));
                    this.g.set(nArray[0], n + 1, nArray[1], Gothic.slab(this.g.pal.slab(), false));
                }
                this.g.set(Spires.rx(d3, d5 - 0.5), n + 2, Spires.rz(d3, d5 - 0.5), Gothic.s(Blocks.PURPLE_STAINED_GLASS));
                this.g.set(Spires.rx(d3, d5 - 0.5), n + 3, Spires.rz(d3, d5 - 0.5), Gothic.s(Blocks.PURPLE_STAINED_GLASS));
                this.chandelier(n4, n7, n3, 0);
            }
        }
        if (room != Room.GREENHOUSE && room != Room.THRONE && room != Room.DINING) {
            for (double d22 = d4 + 1.0; d22 <= d4 + 2.6; d22 += 0.4) {
                int n44;
                int n45 = Spires.rx(d3, d22);
                if (!this.g.isAir(n45, n, n44 = Spires.rz(d3, d22)) || this.g.isAir(n45, n - 1, n44)) continue;
                this.g.set(n45, n, n44, Gothic.s(block));
            }
        }
        for (int i = -1; i <= 1; i += 2) {
            int n46;
            double d23 = d3 + (double)i * 2.4 / (d5 - 1.0);
            int n47 = Spires.rx(d23, d5 - 1.0);
            if (this.onPath(n47, n + 2, n46 = Spires.rz(d23, d5 - 1.0))) continue;
            this.banner(n47, n + 2, n46, direction);
        }
        if (room == Room.STORE || room == Room.CELL || room == Room.LIBRARY || room == Room.ARMORY) {
            for (double d24 = d + 0.5 / d5; d24 <= d2 - 0.5 / d5; d24 += 0.7 / d5) {
                int n48;
                int n49 = Spires.rx(d24, d5 - 1.2);
                if (!this.g.isAir(n49, n7, n48 = Spires.rz(d24, d5 - 1.2)) || !(this.r.nextFloat() < 0.35f)) continue;
                this.g.set(n49, n7, n48, Gothic.s(Blocks.COBWEB));
            }
        }
    }

    private void sills(double d, double d2, double d3, int n) {
        double d4 = d3 - 1.3;
        int n2 = Integer.MIN_VALUE;
        int n3 = 0;
        for (double d5 = d + 0.8 / d3; d5 <= d2 - 0.8 / d3; d5 += 0.3 / d3) {
            int n4;
            int n5 = Spires.rx(d5, d4);
            int n6 = Spires.rz(d5, d4);
            if (n5 == n2 && n6 == n3) continue;
            n2 = n5;
            n3 = n6;
            int n7 = Spires.rx(d5, d3);
            BlockState blockState = this.g.get(n7, n + 2, n4 = Spires.rz(d5, d3));
            if (blockState == null || !blockState.is(this.glass().getBlock()) || !this.g.isAir(n5, n + 1, n6) || this.onPath(n5, n, n6) || !this.g.isAir(n5, n, n6) && !this.g.get(n5, n, n6).is(Blocks.BARREL)) continue;
            this.g.set(n5, n + 1, n6, Gothic.slab(this.g.pal.slab(), true));
            float f = this.r.nextFloat();
            if (!this.g.isAir(n5, n + 2, n6)) continue;
            this.g.set(n5, n + 2, n6, f < 0.45f ? Gothic.candle(1 + this.r.nextInt(3)) : (f < 0.6f ? Gothic.s(Blocks.SKELETON_SKULL) : (f < 0.75f ? this.pot() : Gothic.s(Blocks.AIR))));
        }
    }

    private void towerHall(int n, int n2, double d) {
        double d22;
        for (double d22 : new double[]{0.6 + (double)n, 0.6 + (double)n + Math.PI}) {
            this.radialCut(d22, d - 1.3, d + 1.7, n2, 2, true);
            this.g.set(Spires.rx(d22, d), n2 + 2, Spires.rz(d22, d), Gothic.s(this.g.pal.chiseled()));
            this.g.set(Spires.rx(d22, d), n2 + 3, Spires.rz(d22, d), Gothic.s(this.g.pal.chiseled()));
        }
        if (d < 5.0) {
            return;
        }
        double d3 = d - 1.4;
        int n3 = 0;
        d22 = 0.0;
        while (d22 < Math.PI * 2) {
            int n4;
            int n5 = Spires.rx(d22, d3);
            if (!this.onPath(n5, n2, n4 = Spires.rz(d22, d3)) && this.g.isAir(n5, n2, n4) && Math.max(Math.abs(n5), Math.abs(n4)) > 3) {
                switch ((n3 + n) % 5) {
                    case 0: {
                        this.g.set(n5, n2, n4, Gothic.s(Blocks.BOOKSHELF));
                        this.g.set(n5, n2 + 1, n4, Gothic.s(Blocks.BOOKSHELF));
                        break;
                    }
                    case 1: {
                        this.g.set(n5, n2, n4, Gothic.s(Blocks.SPRUCE_FENCE));
                        this.g.set(n5, n2 + 1, n4, Gothic.candle(1 + n3 % 3));
                        break;
                    }
                    case 2: {
                        this.g.set(n5, n2, n4, Gothic.s(this.g.pal.polished()));
                        this.g.set(n5, n2 + 1, n4, Gothic.s(Blocks.SKELETON_SKULL));
                        break;
                    }
                    case 3: {
                        if (!(this.r.nextFloat() < 0.25f)) break;
                        this.g.chest(n5, n2, n4, true);
                        break;
                    }
                }
            }
            d22 += 1.1 / d3;
            ++n3;
        }
        ++this.rooms;
    }

    private void galleries() {
        for (int i = 1; i <= this.top; ++i) {
            int n;
            int n2;
            int n3;
            double d;
            int n4;
            int n5 = this.levels[i];
            int n6 = this.wallSteps.get(i);
            int[] nArray = this.walls.get(i);
            int n7 = i == this.top ? 5 : 3;
            for (n4 = 0; n4 < n6; ++n4) {
                d = (double)n4 * Math.PI * 2.0 / (double)n6;
                n3 = nArray[n4];
                if (n3 < 0) continue;
                for (n2 = 1; n2 <= n7; ++n2) {
                    int n8;
                    n = Spires.rx(d, n3 - n2);
                    if (!this.inside(n, n5 + 2, n8 = Spires.rz(d, n3 - n2))) continue;
                    if (this.g.isAir(n, n5 - 1, n8)) {
                        this.g.set(n, n5 - 1, n8, this.g.floorTile(n, n8));
                        if (this.g.isAir(n, n5 - 2, n8)) {
                            this.g.set(n, n5 - 2, n8, Gothic.s(this.g.pal.polished()));
                        }
                    }
                    for (int j = n5; j < n5 + 3; ++j) {
                        if (this.cave(n, j, n8)) continue;
                        this.g.air(n, j, n8);
                    }
                    this.markPath(n, n5, n8);
                }
                n2 = Spires.rx(d, n3);
                if (!this.g.isAir(n2, n5 - 1, n = Spires.rz(d, n3)) || !this.inside(n2, n5 - 1, n)) continue;
                this.g.set(n2, n5 - 1, n, this.g.floorTile(n2, n));
            }
            for (n4 = 0; n4 < n6; ++n4) {
                d = (double)n4 * Math.PI * 2.0 / (double)n6;
                n3 = nArray[n4];
                if (n3 < 0 || this.onPath(n2 = Spires.rx(d, n3 - n7 - 1), n5, n = Spires.rz(d, n3 - n7 - 1)) || !this.inside(n2, n5, n) || !this.g.isAir(n2, n5 - 1, n) || !this.g.isAir(n2, n5, n)) continue;
                this.g.set(n2, n5 - 1, n, Gothic.s(this.g.pal.polished()));
                this.g.set(n2, n5, n, Gothic.s(this.g.pal.wall()));
            }
            n4 = nArray[0] > 0 ? nArray[0] : this.RX - 4;
            int n9 = Math.max(12, (int)(Math.PI * 2 * (double)n4 / 7.6));
            House[] houseArray = new House[]{House.SMITHY, House.FARM, House.APOTHECARY, House.TRADE};
            int[] nArray2 = new int[houseArray.length];
            for (n2 = 0; n2 < houseArray.length; ++n2) {
                nArray2[n2] = n2 * n9 / houseArray.length + this.r.nextInt(Math.max(1, n9 / 8));
            }
            ArrayList<House> arrayList = new ArrayList<House>();
            for (n = 0; n < n9; ++n) {
                int n10;
                int n11;
                House house;
                double d2 = (double)n * Math.PI * 2.0 / (double)n9 + (double)i * 0.21;
                int n12 = this.wallOf(i, d2);
                if (i == this.top) {
                    house = switch (n % 6) {
                        case 0 -> House.SHRINE;
                        case 1 -> House.TAVERN;
                        case 2 -> House.OASIS;
                        case 3 -> House.LIBRARY;
                        case 4 -> House.ALCHEMY;
                        default -> House.HOME;
                    };
                } else {
                    house = this.r.nextFloat() < 0.15f ? House.LIBRARY : House.HOME;
                    for (n11 = 0; n11 < houseArray.length; ++n11) {
                        if (n != nArray2[n11]) continue;
                        house = houseArray[n11];
                    }
                    if (!arrayList.isEmpty() && house == House.HOME) {
                        house = (House)((Object)arrayList.remove(0));
                    }
                }
                int n13 = n11 = house == House.HOME ? 3 : 4;
                if (n12 < 0 || this.blocked(d2, i, n11 + 1, n12)) {
                    if (house == House.HOME || house == House.LIBRARY || i == this.top) continue;
                    arrayList.add(house);
                    continue;
                }
                int n14 = this.wallOf(i, d2 - ((double)n11 + 0.5) / (double)n12);
                int n15 = this.wallOf(i, d2 + ((double)n11 + 0.5) / (double)n12);
                if (n14 < 0 || n15 < 0 || Math.abs(n14 - n12) > 3 || Math.abs(n15 - n12) > 3) {
                    int n16;
                    if (house != House.HOME && house != House.LIBRARY && i != this.top) {
                        arrayList.add(house);
                    }
                    if (this.g.isAir(n10 = Spires.rx(d2, n12 - 1), n5 + 3, n16 = Spires.rz(d2, n12 - 1))) continue;
                    this.g.set(n10, n5 + 2, n16, this.g.lantern(true));
                    continue;
                }
                n10 = Math.max(n12, Math.max(n14, n15));
                this.house(Spires.rx(d2, n10), n5, Spires.rz(d2, n10), Spires.dirOf(Math.cos(d2), Math.sin(d2)), house);
            }
        }
    }

    private House pickHouse(int n) {
        float f = this.r.nextFloat();
        if (n <= 2) {
            return f < 0.3f ? House.HOME : (f < 0.5f ? House.SMITHY : (f < 0.7f ? House.FARM : (f < 0.88f ? House.TRADE : House.APOTHECARY)));
        }
        return f < 0.42f ? House.HOME : (f < 0.58f ? House.APOTHECARY : (f < 0.72f ? House.LIBRARY : (f < 0.86f ? House.TRADE : House.SMITHY)));
    }

    private void house(int n, int n2, int n3, Direction direction, House house) {
        boolean bl;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9 = house == House.HOME ? 5 : 7;
        int n10 = house == House.HOME ? 5 : 6;
        int n11 = n9 / 2;
        Direction direction2 = direction.getClockWise();
        int n12 = direction2.getStepX();
        int n13 = direction2.getStepZ();
        int n14 = direction.getStepX();
        int n15 = direction.getStepZ();
        for (int i = -n11; i <= n11; ++i) {
            for (n8 = 1; n8 <= n10; ++n8) {
                n7 = n + n12 * i + n14 * n8;
                n6 = n3 + n13 * i + n15 * n8;
                if (this.inside(n7, n2 + 4, n6)) continue;
                return;
            }
        }
        BlockState blockState = house == House.TRADE || house == House.LIBRARY || house == House.TAVERN ? Flora.PLANKS.get() : (house == House.SHRINE || house == House.ALCHEMY ? this.g.floorTile(n, n3) : Gothic.s(Blocks.SPRUCE_PLANKS));
        for (n8 = -n11; n8 <= n11; ++n8) {
            for (n7 = 1; n7 <= n10; ++n7) {
                n6 = n + n12 * n8 + n14 * n7;
                n5 = n3 + n13 * n8 + n15 * n7;
                this.g.set(n6, n2 - 1, n5, house == House.OASIS ? Gothic.s(Blocks.MOSS_BLOCK) : blockState);
                if (this.g.isAir(n6, n2 - 2, n5) && this.g.get(n6, n2 - 2, n5) != null) {
                    this.g.set(n6, n2 - 2, n5, this.g.brick());
                }
                for (n4 = n2; n4 < n2 + 4; ++n4) {
                    this.g.air(n6, n4, n5);
                }
                this.g.set(n6, n2 + 4, n5, Gothic.s(this.g.pal.polished()));
                if (!this.cave(n6, n2 + 5, n5)) continue;
                this.g.set(n6, n2 + 5, n5, this.g.brick());
            }
        }
        for (n8 = -n11 - 1; n8 <= n11 + 1; ++n8) {
            for (n7 = 1; n7 <= n10 + 1; ++n7) {
                if (Math.abs(n8) <= n11 && n7 <= n10) continue;
                n6 = n + n12 * n8 + n14 * n7;
                n5 = n3 + n13 * n8 + n15 * n7;
                for (n4 = n2 - 1; n4 <= n2 + 4; ++n4) {
                    BlockState blockState2 = this.g.get(n6, n4, n5);
                    if (blockState2 == null || !blockState2.isAir() || n7 == 1 && this.onPath(n6, n4, n5) && n4 >= n2 && n4 < n2 + 2) continue;
                    bl = (n4 == n2 + 1 || n4 == n2 + 2) && n7 == n10 / 2 + 1 && Math.abs(n8) == n11 + 1;
                    this.g.set(n6, n4, n5, bl ? this.warmGlass() : this.g.brick());
                }
            }
        }
        n8 = house == House.TRADE || house == House.OASIS || house == House.SHRINE || house == House.TAVERN ? 1 : 0;
        for (n7 = -n11; n7 <= n11; ++n7) {
            for (n6 = n2; n6 < n2 + 4; ++n6) {
                boolean bl2;
                n5 = n + n12 * n7;
                n4 = n3 + n13 * n7;
                boolean bl3 = (n8 != 0 ? Math.abs(n7) <= 1 : n7 == 0) && n6 < n2 + (n8 != 0 ? 3 : 2) ? true : (bl2 = false);
                if (bl2) {
                    this.g.air(n5, n6, n4);
                    if (n6 != n2 || !this.g.isAir(n5, n2 - 1, n4)) continue;
                    this.g.set(n5, n2 - 1, n4, this.g.floorTile(n5, n4));
                    continue;
                }
                boolean bl4 = bl = n8 == 0 && Math.abs(n7) == n11 - 1 && (n6 == n2 + 1 || n6 == n2 + 2);
                BlockState blockState3 = bl ? this.warmGlass() : (n6 == n2 + 3 || Math.abs(n7) <= 1 && n6 == n2 + 2 ? Gothic.s(this.g.pal.chiseled()) : (Math.abs(n7) == n11 ? Gothic.s(this.g.pal.polished()) : this.g.brick()));
                this.g.set(n5, n6, n4, blockState3);
            }
        }
        block10: for (n7 = 1; n7 <= 5; ++n7) {
            for (n6 = n8 != 0 ? -1 : 0; n6 <= (n8 != 0 ? 1 : 0); ++n6) {
                n5 = n + n12 * n6 - n14 * n7;
                n4 = n3 + n13 * n6 - n15 * n7;
                if (!this.inside(n5, n2 + 2, n4)) continue;
                boolean bl5 = this.onPath(n5, n2, n4);
                for (int i = n2; i < n2 + 3; ++i) {
                    this.g.air(n5, i, n4);
                }
                BlockState blockState4 = this.g.get(n5, n2 - 1, n4);
                if (blockState4 == null || blockState4.isAir()) {
                    this.g.set(n5, n2 - 1, n4, this.g.floorTile(n5, n4));
                }
                this.markPath(n5, n2, n4);
                if (bl5 && n6 == 0) continue block10;
            }
        }
        n7 = n - n14;
        n6 = n3 - n15;
        for (n5 = -n11; n5 <= n11; ++n5) {
            n4 = n7 + n12 * n5;
            int n16 = n6 + n13 * n5;
            if (!this.g.isAir(n4, n2 + 4, n16) || this.g.get(n4, n2 + 4, n16) == null) continue;
            this.g.set(n4, n2 + 4, n16, Gothic.stairs(this.g.pal.stairs(), direction, true));
        }
        if (this.g.get(n7 + n12 * (n11 - 1), n2 + 4, n6 + n13 * (n11 - 1)) != null && !this.g.isAir(n7 + n12 * (n11 - 1), n2 + 4, n6 + n13 * (n11 - 1))) {
            this.g.set(n7 + n12 * (n11 - 1), n2 + 3, n6 + n13 * (n11 - 1), this.g.lantern(true));
        }
        this.g.set(n + n14 * ((n10 + 1) / 2), n2 + 4, n3 + n15 * ((n10 + 1) / 2), Flora.LUMEN.get());
        this.interior(house, n, n2, n3, direction, direction2, n11, n10);
        ++this.rooms;
    }

    private int[] at(int n, int n2, Direction direction, Direction direction2, int n3, int n4) {
        return new int[]{n + direction2.getStepX() * n3 + direction.getStepX() * n4, n2 + direction2.getStepZ() * n3 + direction.getStepZ() * n4};
    }

    private void put(int n, int n2, int n3, Direction direction, Direction direction2, int n4, int n5, BlockState blockState) {
        int[] nArray = this.at(n, n3, direction, direction2, n4, n5);
        this.g.set(nArray[0], n2, nArray[1], blockState);
    }

    private BlockState trapdoor(Block block, Direction direction, boolean bl, boolean bl2) {
        return (BlockState)((BlockState)((BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.HALF, (Comparable)(bl ? Half.TOP : Half.BOTTOM))).setValue((Property)BlockStateProperties.OPEN, (Comparable)Boolean.valueOf(bl2));
    }

    private BlockState log(Block block, Direction.Axis axis) {
        return (BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.AXIS, (Comparable)axis);
    }

    private BlockState crop(Block block) {
        return block == Blocks.BEETROOTS ? (BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.AGE_3, (Comparable)Integer.valueOf(2 + this.r.nextInt(2))) : (BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.AGE_7, (Comparable)Integer.valueOf(4 + this.r.nextInt(4)));
    }

    private BlockState pot() {
        return Gothic.s(POTS[this.r.nextInt(POTS.length)]);
    }

    private void table(int n, int n2, int n3, boolean bl) {
        this.g.set(n, n2, n3, Gothic.s(Blocks.DARK_OAK_FENCE));
        this.g.set(n, n2 + 1, n3, bl ? Gothic.candle(1 + this.r.nextInt(3)) : Gothic.s(Blocks.DARK_OAK_PRESSURE_PLATE));
    }

    private void chair(int n, int n2, int n3, Direction direction) {
        this.g.set(n, n2, n3, Gothic.stairs(Blocks.SPRUCE_STAIRS, direction, false));
    }

    private void chandelier(int n, int n2, int n3, int n4) {
        for (int i = 1; i <= n4; ++i) {
            this.g.set(n, n2 - i, n3, Gothic.s(Blocks.CHAIN));
        }
        this.g.set(n, n2 - n4 - 1, n3, this.g.lantern(true));
    }

    private void fireplace(int n, int n2, int n3, Direction direction, int n4) {
        int n5;
        Direction direction2 = direction.getClockWise();
        this.g.set(n, n2, n3, Gothic.s(Blocks.CAMPFIRE));
        for (n5 = -1; n5 <= 1; n5 += 2) {
            this.g.set(n + direction2.getStepX() * n5, n2, n3 + direction2.getStepZ() * n5, Gothic.s(this.g.pal.polished()));
            this.g.set(n + direction2.getStepX() * n5, n2 + 1, n3 + direction2.getStepZ() * n5, Gothic.s(this.g.pal.polished()));
        }
        this.g.set(n, n2 + 1, n3, Gothic.stairs(this.g.pal.stairs(), direction.getOpposite(), true));
        for (n5 = n2 + 2; n5 < n4; ++n5) {
            this.g.set(n, n5, n3, this.g.brick());
        }
    }

    private void rug(int n, int n2, int n3, int n4, int n5) {
        Block[] blockArray = RUGS[this.r.nextInt(RUGS.length)];
        for (int i = Math.min(n, n3); i <= Math.max(n, n3); ++i) {
            for (int j = Math.min(n2, n4); j <= Math.max(n2, n4); ++j) {
                BlockState blockState = this.g.get(i, n5, j);
                BlockState blockState2 = this.g.get(i, n5 - 1, j);
                if (blockState == null || !blockState.isAir() || blockState2 == null || blockState2.isAir() || blockState2.is(Blocks.WATER) || blockState2.is(Blocks.FARMLAND)) continue;
                boolean bl = i == Math.min(n, n3) || i == Math.max(n, n3) || j == Math.min(n2, n4) || j == Math.max(n2, n4);
                this.g.set(i, n5, j, Gothic.s(bl ? blockArray[1] : blockArray[0]));
            }
        }
    }

    private void banner(int n, int n2, int n3, Direction direction) {
        if (this.g.isAir(n, n2, n3) && this.g.get(n, n2, n3) != null) {
            this.g.set(n, n2, n3, this.facing(BANNERS[this.r.nextInt(BANNERS.length)], direction));
        }
    }

    private void shelf(int n, int n2, int n3, Direction direction) {
        if (!this.g.isAir(n, n2, n3)) {
            return;
        }
        this.g.set(n, n2, n3, this.trapdoor(Blocks.SPRUCE_TRAPDOOR, direction, true, false));
        if (this.g.isAir(n, n2 + 1, n3)) {
            float f = this.r.nextFloat();
            this.g.set(n, n2 + 1, n3, f < 0.45f ? this.pot() : (f < 0.8f ? Gothic.candle(1 + this.r.nextInt(3)) : Gothic.s(Blocks.DECORATED_POT)));
        }
    }

    private void interior(House house, int n, int n2, int n3, Direction direction, Direction direction2, int n4, int n5) {
        int[] nArray;
        int n6;
        Direction direction3 = direction.getOpposite();
        int n7 = this.r.nextInt(2);
        Direction.Axis axis = direction2.getAxis();
        for (int i = 2; i <= n5; i += 2) {
            for (n6 = -n4; n6 <= n4; ++n6) {
                nArray = this.at(n, n3, direction, direction2, n6, i);
                if (!this.g.isAir(nArray[0], n2 + 3, nArray[1])) continue;
                this.g.set(nArray[0], n2 + 3, nArray[1], this.log(Blocks.STRIPPED_DARK_OAK_LOG, axis));
            }
        }
        int[] nArray2 = this.at(n, n3, direction, direction2, 0, n5);
        switch (house.ordinal()) {
            case 0: {
                int[] nArray3;
                if (n7 == 0) {
                    nArray3 = this.at(n, n3, direction, direction2, -(n4 - 1), n5);
                    this.bed(nArray3[0], n2, nArray3[1], direction);
                    this.put(n, n2, n3, direction, direction2, -n4, n5, Gothic.s(Blocks.BARREL));
                    this.put(n, n2 + 1, n3, direction, direction2, -n4, n5, this.pot());
                    nArray = this.at(n, n3, direction, direction2, n4, n5);
                    this.fireplace(nArray[0], n2, nArray[1], direction2.getOpposite(), n2 + 4);
                    this.chair(this.at(n, n3, direction, direction2, n4 - 1, n5 - 2)[0], n2, this.at(n, n3, direction, direction2, n4 - 1, n5 - 2)[1], direction);
                    this.put(n, n2, n3, direction, direction2, n4, 2, Gothic.s(Blocks.BARREL));
                    this.put(n, n2 + 1, n3, direction, direction2, n4, 2, Gothic.s(Blocks.BARREL));
                    this.put(n, n2, n3, direction, direction2, -n4, 1, Gothic.s(Blocks.POTTED_FERN));
                    if (this.r.nextFloat() < 0.45f) {
                        int[] nArray4 = this.at(n, n3, direction, direction2, -n4, n5 - 2);
                        this.g.chest(nArray4[0], n2, nArray4[1], false);
                    }
                } else {
                    this.put(n, n2, n3, direction, direction2, -n4, n5, this.facing(Blocks.SMOKER, direction3));
                    this.put(n, n2, n3, direction, direction2, -n4 + 1, n5, Gothic.s(Blocks.WATER_CAULDRON));
                    this.put(n, n2, n3, direction, direction2, n4, n5, Gothic.s(Blocks.BARREL));
                    this.put(n, n2 + 1, n3, direction, direction2, n4, n5, Gothic.s(Blocks.DECORATED_POT));
                    nArray3 = this.at(n, n3, direction, direction2, 1, 3);
                    this.table(nArray3[0], n2, nArray3[1], false);
                    nArray = this.at(n, n3, direction, direction2, 1, 2);
                    int[] nArray5 = this.at(n, n3, direction, direction2, 1, 4);
                    this.chair(nArray[0], n2, nArray[1], direction3);
                    this.chair(nArray5[0], n2, nArray5[1], direction);
                    int[] nArray6 = this.at(n, n3, direction, direction2, -n4, 3);
                    this.bed(nArray6[0], n2, nArray6[1], direction3.getClockWise() == direction2 ? direction3 : direction3);
                    this.put(n, n2, n3, direction, direction2, -n4, 1, Gothic.s(Blocks.CRAFTING_TABLE));
                }
                nArray3 = this.at(n, n3, direction, direction2, n4, 1);
                this.shelf(nArray3[0], n2 + 2, nArray3[1], direction2.getOpposite());
                this.rug(this.at(n, n3, direction, direction2, -1, 1)[0], this.at(n, n3, direction, direction2, -1, 1)[1], this.at(n, n3, direction, direction2, 0, 2)[0], this.at(n, n3, direction, direction2, 0, 2)[1], n2);
                break;
            }
            case 1: {
                int n8;
                int[] nArray7 = this.at(n, n3, direction, direction2, 0, n5);
                this.g.set(nArray7[0], n2, nArray7[1], Gothic.s(Blocks.LAVA_CAULDRON));
                this.g.set(nArray7[0], n2 + 1, nArray7[1], Gothic.s(Blocks.IRON_BARS));
                for (n8 = n2 + 2; n8 < n2 + 4; ++n8) {
                    this.g.set(nArray7[0], n8, nArray7[1], Gothic.s(this.g.pal.polished()));
                }
                this.put(n, n2, n3, direction, direction2, -1, n5, this.facing(Blocks.BLAST_FURNACE, direction3));
                this.put(n, n2, n3, direction, direction2, 1, n5, this.facing(Blocks.BLAST_FURNACE, direction3));
                this.put(n, n2, n3, direction, direction2, -1, n5 - 2, this.facing(Blocks.ANVIL, direction2));
                this.put(n, n2, n3, direction, direction2, -3, n5, Gothic.s(Blocks.WATER_CAULDRON));
                this.put(n, n2, n3, direction, direction2, -3, n5 - 1, Gothic.s(Blocks.COAL_BLOCK));
                this.put(n, n2 + 1, n3, direction, direction2, -3, n5 - 1, Gothic.s(Blocks.CHAIN));
                this.put(n, n2, n3, direction, direction2, 3, n5, Gothic.s(Blocks.SMITHING_TABLE));
                this.put(n, n2, n3, direction, direction2, 3, n5 - 1, Gothic.s(Blocks.STONECUTTER));
                this.put(n, n2, n3, direction, direction2, 3, n5 - 2, Gothic.s(Blocks.BARREL));
                this.put(n, n2 + 1, n3, direction, direction2, 3, n5 - 2, Gothic.s(Blocks.BARREL));
                for (n8 = 1; n8 <= 3; ++n8) {
                    this.put(n, n2, n3, direction, direction2, -3, n8, Gothic.s(Blocks.SPRUCE_FENCE));
                    this.put(n, n2 + 1, n3, direction, direction2, -3, n8, n8 == 2 ? Gothic.s(Blocks.SKELETON_SKULL) : Gothic.s(Blocks.IRON_BARS));
                }
                this.put(n, n2, n3, direction, direction2, 3, 1, Gothic.s(Blocks.FLETCHING_TABLE));
                if (!(this.r.nextFloat() < 0.6f)) break;
                int[] nArray8 = this.at(n, n3, direction, direction2, 3, 3);
                this.g.chest(nArray8[0], n2, nArray8[1], false);
                break;
            }
            case 2: {
                for (n6 = -3; n6 <= 3; ++n6) {
                    for (int i = 2; i <= n5 - 1; ++i) {
                        if (n6 == 0) continue;
                        int[] nArray9 = this.at(n, n3, direction, direction2, n6, i);
                        if (Math.abs(n6) == 3 && i == n5 - 1) {
                            this.g.set(nArray9[0], n2 - 1, nArray9[1], Gothic.s(Blocks.WATER));
                            continue;
                        }
                        this.g.set(nArray9[0], n2 - 1, nArray9[1], Gothic.s(Blocks.FARMLAND));
                        Block block = Math.abs(n6) == 1 ? (i % 2 == 0 ? Blocks.WHEAT : Blocks.CARROTS) : (Math.abs(n6) == 2 ? Blocks.POTATOES : Blocks.BEETROOTS);
                        this.g.set(nArray9[0], n2, nArray9[1], this.crop(block));
                    }
                }
                this.put(n, n2, n3, direction, direction2, -2, n5, Gothic.s(Blocks.COMPOSTER));
                this.put(n, n2, n3, direction, direction2, -1, n5, Gothic.s(Blocks.HAY_BLOCK));
                this.put(n, n2 + 1, n3, direction, direction2, -1, n5, Gothic.s(Blocks.HAY_BLOCK));
                this.put(n, n2, n3, direction, direction2, 1, n5, Gothic.s(Blocks.BARREL));
                this.put(n, n2, n3, direction, direction2, 2, n5, Gothic.s(Blocks.PUMPKIN));
                this.put(n, n2, n3, direction, direction2, 0, n5, Gothic.s(Blocks.SPRUCE_FENCE));
                this.put(n, n2 + 1, n3, direction, direction2, 0, n5, Gothic.s(Blocks.HAY_BLOCK));
                this.put(n, n2 + 2, n3, direction, direction2, 0, n5, this.facing(Blocks.CARVED_PUMPKIN, direction3));
                for (int n9 : new int[]{2, n5 - 1}) {
                    for (int n10 : new int[]{-2, 2}) {
                        int[] nArray10 = this.at(n, n3, direction, direction2, n10, n9);
                        this.g.set(nArray10[0], n2 + 4, nArray10[1], Flora.LUMEN.get());
                    }
                }
                this.put(n, n2, n3, direction, direction2, -3, 1, Gothic.s(Blocks.BARREL));
                this.put(n, n2, n3, direction, direction2, 3, 1, Gothic.s(Blocks.CHEST));
                break;
            }
            case 3: 
            case 9: {
                for (n6 = -2; n6 <= 2; ++n6) {
                    this.put(n, n2, n3, direction, direction2, n6, n5 - 1, n6 == 0 ? Gothic.s(Blocks.BREWING_STAND) : Gothic.slab(Blocks.SPRUCE_SLAB, true));
                }
                this.put(n, n2, n3, direction, direction2, -3, n5, Gothic.s(Blocks.WATER_CAULDRON));
                this.put(n, n2, n3, direction, direction2, 3, n5, Gothic.s(Blocks.CAULDRON));
                for (n6 = -2; n6 <= 2; ++n6) {
                    nArray = this.at(n, n3, direction, direction2, n6, n5);
                    this.shelf(nArray[0], n2 + 1, nArray[1], direction3);
                }
                for (n6 = 2; n6 <= n5 - 2; ++n6) {
                    this.put(n, n2, n3, direction, direction2, -3, n6, Gothic.s(n6 % 2 == 0 ? Blocks.BOOKSHELF : Blocks.BARREL));
                    this.put(n, n2 + 1, n3, direction, direction2, -3, n6, this.pot());
                    this.put(n, n2, n3, direction, direction2, 3, n6, Gothic.s(n6 % 2 == 0 ? Blocks.BARREL : Blocks.CHISELED_BOOKSHELF));
                    this.put(n, n2 + 1, n3, direction, direction2, 3, n6, house == House.ALCHEMY ? Gothic.s(Blocks.AMETHYST_CLUSTER) : Gothic.candle(2));
                }
                this.put(n, n2 + 3, n3, direction, direction2, 0, 2, Gothic.s(Blocks.SPORE_BLOSSOM));
                this.put(n, n2 + 2, n3, direction, direction2, -2, 2, Gothic.s(Blocks.HANGING_ROOTS));
                if (this.r.nextFloat() < 0.6f) {
                    int[] nArray11 = this.at(n, n3, direction, direction2, 3, 1);
                    this.g.chest(nArray11[0], n2, nArray11[1], house == House.ALCHEMY);
                }
                this.rug(this.at(n, n3, direction, direction2, -1, 1)[0], this.at(n, n3, direction, direction2, -1, 1)[1], this.at(n, n3, direction, direction2, 1, 3)[0], this.at(n, n3, direction, direction2, 1, 3)[1], n2);
                break;
            }
            case 4: {
                for (n6 = -3; n6 <= 2; ++n6) {
                    this.put(n, n2, n3, direction, direction2, n6, 3, n6 == -3 ? Gothic.s(Blocks.BARREL) : this.log(Blocks.STRIPPED_SPRUCE_LOG, direction2.getAxis()));
                }
                this.put(n, n2 + 1, n3, direction, direction2, -1, 3, Gothic.s(Blocks.BELL));
                this.put(n, n2 + 1, n3, direction, direction2, 1, 3, Gothic.s(Blocks.DECORATED_POT));
                this.put(n, n2 + 1, n3, direction, direction2, 2, 3, this.g.lantern(false));
                Block[] blockArray = new Block[]{Blocks.PUMPKIN, Blocks.MELON, Blocks.HAY_BLOCK, Blocks.BARREL, Blocks.DRIED_KELP_BLOCK, Blocks.DECORATED_POT, Blocks.BEEHIVE};
                for (int i = -3; i <= 3; ++i) {
                    this.put(n, n2, n3, direction, direction2, i, n5, Gothic.s(blockArray[Math.floorMod(i, blockArray.length)]));
                    if (!this.r.nextBoolean()) continue;
                    this.put(n, n2 + 1, n3, direction, direction2, i, n5, Gothic.s(Blocks.BARREL));
                }
                this.put(n, n2, n3, direction, direction2, -3, n5 - 1, Gothic.s(Blocks.LOOM));
                this.put(n, n2, n3, direction, direction2, 3, n5 - 1, Gothic.s(Blocks.CARTOGRAPHY_TABLE));
                if (this.r.nextFloat() < 0.7f) {
                    int[] nArray12 = this.at(n, n3, direction, direction2, -3, n5 - 2);
                    this.g.chest(nArray12[0], n2, nArray12[1], false);
                }
                this.rug(this.at(n, n3, direction, direction2, -2, 1)[0], this.at(n, n3, direction, direction2, -2, 1)[1], this.at(n, n3, direction, direction2, 2, 2)[0], this.at(n, n3, direction, direction2, 2, 2)[1], n2);
                for (int i = -3; i <= 3; i += 6) {
                    int[] nArray13 = this.at(n, n3, direction, direction2, i, 1);
                    this.banner(nArray13[0], n2 + 2, nArray13[1], i < 0 ? direction2 : direction2.getOpposite());
                }
                break;
            }
            case 5: {
                for (n6 = -n4; n6 <= n4; ++n6) {
                    for (int i = n2; i < n2 + 3; ++i) {
                        this.put(n, i, n3, direction, direction2, n6, n5, Gothic.s((n6 + i) % 3 == 0 ? Blocks.CHISELED_BOOKSHELF : Blocks.BOOKSHELF));
                    }
                }
                for (n6 = 2; n6 < n5; ++n6) {
                    for (int i = n2; i < n2 + 2; ++i) {
                        this.put(n, i, n3, direction, direction2, -n4, n6, Gothic.s(Blocks.BOOKSHELF));
                        if (n6 <= 2) continue;
                        this.put(n, i, n3, direction, direction2, n4, n6, Gothic.s(Blocks.BOOKSHELF));
                    }
                }
                if (n4 >= 3) {
                    this.put(n, n2, n3, direction, direction2, 0, n5 - 2, Gothic.s(Blocks.ENCHANTING_TABLE));
                    this.put(n, n2, n3, direction, direction2, 0, n5 - 3, Gothic.s(Blocks.RED_CARPET));
                } else {
                    this.put(n, n2, n3, direction, direction2, 0, n5 - 2, this.facing(Blocks.LECTERN, direction3));
                }
                int[] nArray14 = this.at(n, n3, direction, direction2, n4, 2);
                this.table(nArray14[0], n2, nArray14[1], true);
                int[] nArray15 = this.at(n, n3, direction, direction2, n4 - 1, 2);
                this.chair(nArray15[0], n2, nArray15[1], direction2.getOpposite());
                this.chandelier(nArray2[0] - direction.getStepX() * 2, n2 + 3, nArray2[1] - direction.getStepZ() * 2, 0);
                this.put(n, n2 + 3, n3, direction, direction2, -n4, 2, Flora.LUMEN.get());
                break;
            }
            case 8: {
                for (n6 = -n4 + 1; n6 <= n4 - 1; ++n6) {
                    this.put(n, n2, n3, direction, direction2, n6, n5 - 1, n6 == n4 - 1 ? Gothic.s(Blocks.BARREL) : this.log(Blocks.STRIPPED_DARK_OAK_LOG, direction2.getAxis()));
                }
                for (n6 = -n4; n6 <= n4; ++n6) {
                    this.put(n, n2, n3, direction, direction2, n6, n5, Gothic.s(Blocks.BARREL));
                    this.put(n, n2 + 1, n3, direction, direction2, n6, n5, n6 % 2 == 0 ? Gothic.s(Blocks.BARREL) : Gothic.s(Blocks.DECORATED_POT));
                }
                this.put(n, n2 + 1, n3, direction, direction2, 0, n5 - 1, Gothic.s(Blocks.CAKE));
                this.put(n, n2 + 1, n3, direction, direction2, -1, n5 - 1, Gothic.candle(3));
                for (n6 = -1; n6 <= 1; n6 += 2) {
                    nArray = this.at(n, n3, direction, direction2, n6 * 2, 2);
                    this.table(nArray[0], n2, nArray[1], n6 > 0);
                    int[] nArray16 = this.at(n, n3, direction, direction2, n6 * 2, 1);
                    int[] nArray17 = this.at(n, n3, direction, direction2, n6 * 2, 3);
                    int[] nArray18 = this.at(n, n3, direction, direction2, n6 * 3, 2);
                    this.chair(nArray16[0], n2, nArray16[1], direction3);
                    this.chair(nArray17[0], n2, nArray17[1], direction);
                    this.chair(nArray18[0], n2, nArray18[1], n6 > 0 ? direction2 : direction2.getOpposite());
                }
                this.chandelier(n + direction.getStepX() * 2, n2 + 3, n3 + direction.getStepZ() * 2, 0);
                for (n6 = -n4; n6 <= n4; n6 += 2 * n4) {
                    nArray = this.at(n, n3, direction, direction2, n6, n5 - 2);
                    this.banner(nArray[0], n2 + 2, nArray[1], n6 < 0 ? direction2 : direction2.getOpposite());
                }
                break;
            }
            case 6: {
                this.put(n, n2, n3, direction, direction2, 0, n5, Gothic.s(this.g.pal.chiseled()));
                this.put(n, n2 + 1, n3, direction, direction2, 0, n5, Gothic.s(this.g.pal.trim()));
                this.put(n, n2 + 2, n3, direction, direction2, 0, n5, Gothic.s(Blocks.WITHER_SKELETON_SKULL));
                for (int n11 : new int[]{-2, -1, 1, 2}) {
                    this.put(n, n2, n3, direction, direction2, n11, n5, Gothic.candle(1 + this.r.nextInt(4)));
                }
                for (int n11 : new int[]{-3, 3}) {
                    this.put(n, n2, n3, direction, direction2, n11, n5, Gothic.s(this.g.pal.polished()));
                    this.put(n, n2 + 1, n3, direction, direction2, n11, n5, this.g.lantern(false));
                }
                for (int n11 : new int[]{2, 3}) {
                    for (int n12 : new int[]{-2, -1, 1, 2}) {
                        this.put(n, n2, n3, direction, direction2, n12, n11, Gothic.stairs(Blocks.DARK_OAK_STAIRS, direction3, false));
                    }
                }
                for (int n11 : new int[]{-3, 3}) {
                    int[] nArray19 = this.at(n, n3, direction, direction2, n11, n5 - 2);
                    this.banner(nArray19[0], n2 + 2, nArray19[1], n11 < 0 ? direction2 : direction2.getOpposite());
                }
                for (int i = 1; i <= n5; ++i) {
                    int[] nArray20 = this.at(n, n3, direction, direction2, 0, i);
                    if (!this.g.isAir(nArray20[0], n2, nArray20[1])) continue;
                    this.g.set(nArray20[0], n2, nArray20[1], Gothic.s(Blocks.PURPLE_CARPET));
                }
                if (!(this.r.nextFloat() < 0.5f)) break;
                int[] nArray21 = this.at(n, n3, direction, direction2, -3, 1);
                this.g.chest(nArray21[0], n2, nArray21[1], true);
                break;
            }
            case 7: {
                int[] nArray22 = this.at(n, n3, direction, direction2, 0, n5 - 1);
                this.g.set(nArray22[0], n2 - 1, nArray22[1], Gothic.s(Blocks.WATER));
                this.put(n, n2 - 1, n3, direction, direction2, 1, n5 - 1, Gothic.s(Blocks.WATER));
                this.put(n, n2 - 1, n3, direction, direction2, -1, n5 - 1, Gothic.s(Blocks.WATER));
                this.put(n, n2, n3, direction, direction2, 1, n5 - 1, Gothic.s(Blocks.LILY_PAD));
                nArray = this.at(n, n3, direction, direction2, -3, n5);
                this.ancientBush(nArray[0], n2, nArray[1]);
                int[] nArray23 = this.at(n, n3, direction, direction2, 3, n5);
                this.ancientBush(nArray23[0], n2, nArray23[1]);
                Block[] blockArray = new Block[]{Blocks.LILY_OF_THE_VALLEY, Blocks.FERN, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.FLOWERING_AZALEA};
                for (int i = -3; i <= 3; ++i) {
                    for (int j = 1; j <= 3; ++j) {
                        if (i == 0 || this.r.nextFloat() < 0.3f) continue;
                        float f = this.r.nextFloat();
                        this.put(n, n2, n3, direction, direction2, i, j, f < 0.25f ? Flora.GRASS.get() : (f < 0.4f ? Flora.FERN.get() : (f < 0.5f ? Flora.BERRY.get() : (f < 0.58f ? Flora.SHRUB.get() : Gothic.s(blockArray[this.r.nextInt(blockArray.length)])))));
                    }
                }
                this.put(n, n2 + 3, n3, direction, direction2, 0, n5 - 1, Gothic.s(Blocks.SPORE_BLOSSOM));
                this.put(n, n2 + 3, n3, direction, direction2, -2, 2, Gothic.s(Blocks.SPORE_BLOSSOM));
                this.put(n, n2 + 4, n3, direction, direction2, 0, 2, Flora.LUMEN.get());
                this.chair(this.at(n, n3, direction, direction2, 2, 2)[0], n2, this.at(n, n3, direction, direction2, 2, 2)[1], direction);
            }
        }
    }

    private void terrace() {
        int n;
        double d;
        int n2;
        int n3;
        int n4 = this.levels[this.top];
        int n5 = (n4 - -30) / 7;
        double d2 = 22.0 - this.SH * (double)n5;
        double d3 = 23.0;
        for (n3 = (int)(-d3) - 1; n3 <= (int)d3 + 1; ++n3) {
            for (int i = (int)(-d3) - 1; i <= (int)d3 + 1; ++i) {
                int n6;
                double d4 = Math.sqrt(n3 * n3 + i * i);
                if (d4 <= d2 + 0.4 || d4 > d3 + 0.4 || !this.inside(n3, n4, i)) continue;
                double d5 = Math.atan2(i, n3);
                n2 = 0;
                double[] dArray = PILLAR_A;
                int n7 = dArray.length;
                for (n6 = 0; n6 < n7; ++n6) {
                    d = dArray[n6];
                    if (!(Spires.angDist(d5, d) < 0.45)) continue;
                    n2 = 1;
                }
                this.g.set(n3, n4 - 1, i, n2 != 0 && d4 > d2 + 2.5 ? Gothic.s(this.r.nextFloat() < 0.2f ? Blocks.ROOTED_DIRT : Blocks.MOSS_BLOCK) : this.g.floorTile(n3, i));
                for (n = n4; n < n4 + 3; ++n) {
                    if (this.g.get(n3, n, i) != null && this.g.get(n3, n, i).isAir() && !(d4 > d2 + 1.0)) continue;
                    this.g.air(n3, n, i);
                }
                this.g.set(n3, n4 - 2, i, Gothic.s(this.g.pal.polished()));
                n = 0;
                for (n7 = 0; n7 < 8; ++n7) {
                    if (!(Spires.angDist(d5, (double)n7 * Math.PI / 4.0 + 0.39269908169872414) < 1.2 / d4)) continue;
                    n = 1;
                }
                if (n != 0) {
                    n7 = 1 + (int)Math.round((d3 - d4) * 0.8);
                    for (n6 = 3; n6 <= n7 + 2; ++n6) {
                        if (!this.g.isAir(n3, n4 - n6, i)) continue;
                        this.g.set(n3, n4 - n6, i, n6 == n7 + 2 ? Gothic.s(this.g.pal.chiseled()) : this.g.brick());
                    }
                }
                if (!(d4 > d3 - 0.6) || (n3 + i & 1) != 0) continue;
                this.g.set(n3, n4, i, Gothic.s(this.g.pal.wall()));
            }
        }
        for (n3 = -1; n3 <= 1; n3 += 2) {
            this.chapel(n3, n4);
        }
        for (n3 = -1; n3 <= 1; n3 += 2) {
            this.crystalAltar(0, n4, n3 * 19);
        }
        for (double d6 : PILLAR_A) {
            int n8 = Spires.rx(d6, 19.5);
            n2 = Spires.rz(d6, 19.5);
            for (n = -1; n <= 1; ++n) {
                for (int i = -1; i <= 1; ++i) {
                    if (Math.abs(n) + Math.abs(i) > 1) continue;
                    this.g.set(n8 + n, n4 - 1, n2 + i, Gothic.s(Blocks.WATER));
                }
            }
            this.g.set(n8, n4 - 2, n2, Gothic.s(Blocks.CLAY));
            this.ancientTree(Spires.rx(d6 + 0.22, 20.5), n4, Spires.rz(d6 + 0.22, 20.5), 8 + this.r.nextInt(3), 3);
            for (n = 0; n < 26; ++n) {
                int n9;
                double d7 = d6 + (this.r.nextDouble() - 0.5) * 0.8;
                int n10 = Spires.rx(d7, d = d2 + 3.0 + this.r.nextDouble() * (d3 - d2 - 4.0));
                BlockState blockState = this.g.get(n10, n4 - 1, n9 = Spires.rz(d7, d));
                if (blockState == null || !blockState.is(Blocks.MOSS_BLOCK) || !this.g.isAir(n10, n4, n9)) continue;
                float f = this.r.nextFloat();
                this.g.set(n10, n4, n9, f < 0.25f ? Flora.GRASS.get() : (f < 0.42f ? Flora.FERN.get() : (f < 0.56f ? Flora.SHRUB.get() : (f < 0.68f ? Flora.BERRY.get() : Gothic.s(this.r.nextBoolean() ? Blocks.LILY_OF_THE_VALLEY : Blocks.BLUE_ORCHID)))));
            }
            this.g.set(Spires.rx(d6 - 0.2, 21.0), n4, Spires.rz(d6 - 0.2, 21.0), Flora.BLOSSOM.get());
        }
        this.rooms += 4;
    }

    private void crystalAltar(int n, int n2, int n3) {
        int n4;
        for (n4 = n - 3; n4 <= n + 3; ++n4) {
            for (int i = n3 - 3; i <= n3 + 3; ++i) {
                int n5 = Math.max(Math.abs(n4 - n), Math.abs(i - n3));
                if (n5 == 3) {
                    this.g.set(n4, n2, i, Gothic.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Spires.dirOf(n - n4, n3 - i), false));
                } else {
                    this.g.set(n4, n2, i, Gothic.s(Blocks.POLISHED_BLACKSTONE_BRICKS));
                    if (n5 <= 1) {
                        this.g.set(n4, n2 + 1, i, n5 == 1 ? Gothic.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Spires.dirOf(n4 - n, i - n3).getOpposite(), false) : Gothic.s(Blocks.CHISELED_POLISHED_BLACKSTONE));
                    }
                }
                if (n5 != 2 || n4 != n && i != n3) continue;
                this.g.set(n4, n2 + 1, i, Gothic.candle(2 + this.r.nextInt(3)));
            }
        }
        for (n4 = n2 + 2; n4 <= n2 + 4; ++n4) {
            this.g.set(n, n4, n3, n4 == n2 + 4 ? Gothic.s(Blocks.CHISELED_POLISHED_BLACKSTONE) : Gothic.s(Blocks.POLISHED_BLACKSTONE_WALL));
        }
        this.g.set(n, n2 + 5, n3, Gothic.s(Blocks.CRYING_OBSIDIAN));
        for (n4 = n2 + 6; n4 <= n2 + 8; ++n4) {
            this.g.set(n, n4, n3, Gothic.s(n4 == n2 + 7 ? Blocks.AMETHYST_BLOCK : Blocks.MAGENTA_STAINED_GLASS));
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            this.g.set(n + direction.getStepX(), n2 + 7, n3 + direction.getStepZ(), Gothic.s(Blocks.MAGENTA_STAINED_GLASS));
        }
        this.g.set(n, n2 + 9, n3, Gothic.s(Blocks.AMETHYST_CLUSTER));
        for (Object object : (Object)new int[][]{{-2, -2}, {2, -2}, {-2, 2}, {2, 2}}) {
            this.g.set(n + object[0], n2 + 1, n3 + object[1], Gothic.s(Blocks.POLISHED_BLACKSTONE_WALL));
            this.g.set(n + object[0], n2 + 2, n3 + object[1], this.g.lantern(false));
        }
    }

    private void chapel(int n, int n2) {
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8 = n > 0 ? 16 : -23;
        int n9 = n > 0 ? 23 : -16;
        int n10 = -4;
        int n11 = 4;
        int n12 = 5;
        for (n7 = n8; n7 <= n9; ++n7) {
            for (n6 = n10; n6 <= n11; ++n6) {
                n5 = n7 == n8 || n7 == n9 || n6 == n10 || n6 == n11 ? 1 : 0;
                this.g.set(n7, n2 - 1, n6, Gothic.s(this.g.pal.polished()));
                for (n4 = n2; n4 < n2 + n12; ++n4) {
                    boolean bl;
                    if (n5 == 0) {
                        this.g.air(n7, n4, n6);
                        continue;
                    }
                    n3 = !(n7 != n8 && n7 != n9 || n6 != n10 && n6 != n11) ? 1 : 0;
                    boolean bl2 = bl = n3 == 0 && (n6 == n10 || n6 == n11) && (n7 - n8) % 2 == 1 && n4 >= n2 + 1 && n4 <= n2 + 3;
                    this.g.set(n7, n4, n6, n3 != 0 ? Gothic.s(this.g.pal.polished()) : (bl ? Gothic.s(Blocks.PURPLE_STAINED_GLASS) : (n4 == n2 + n12 - 1 ? Gothic.s(this.g.pal.chiseled()) : this.g.brick())));
                }
            }
        }
        this.g.gable(n8, n10, n9, n11, n2 + n12, true);
        n7 = n > 0 ? n8 : n9;
        n6 = n > 0 ? n9 : n8;
        for (n5 = -1; n5 <= 1; ++n5) {
            for (n4 = n2; n4 < n2 + 3; ++n4) {
                this.g.air(n7, n4, n5);
            }
        }
        this.g.set(n7, n2 + 3, 0, Gothic.s(this.g.pal.chiseled()));
        for (n5 = n2 + 1; n5 <= n2 + 3; ++n5) {
            this.g.set(n6, n5, 0, Gothic.s(Blocks.PURPLE_STAINED_GLASS));
        }
        n5 = n6 - n;
        this.g.set(n5, n2, 0, Gothic.s(this.g.pal.chiseled()));
        this.g.set(n5, n2 + 1, 0, Gothic.s(this.g.pal.trim()));
        this.g.set(n5, n2, -2, Gothic.candle(4));
        this.g.set(n5, n2, 2, Gothic.candle(4));
        Direction direction = n > 0 ? Direction.WEST : Direction.EAST;
        for (n3 = n8 + 2; n3 <= n9 - 3; ++n3) {
            if ((n3 & 1) != 0) continue;
            for (int n13 : new int[]{-3, -2, 2, 3}) {
                if (n3 == n5) continue;
                this.g.set(n3, n2, n13, Gothic.stairs(Blocks.DARK_OAK_STAIRS, direction, false));
            }
        }
        this.g.set((n8 + n9) / 2, n2 + n12 - 1, 0, Flora.LUMEN.get());
        this.g.chainLantern((n8 + n9) / 2 + n * 2, n2 + n12 - 2, 0, 1);
        if (this.r.nextFloat() < 0.6f) {
            this.g.chest(n6 - n, n2, 3, true);
        }
    }

    private int bridgeK(int n) {
        return BRIDGE_K[n] < 0 ? this.top : Math.min(BRIDGE_K[n], this.top - 1);
    }

    private void bridges() {
        for (int i = 0; i < BRIDGE_K.length; ++i) {
            this.bridge(this.bridgeK(i), 1.2 + (double)i * Math.PI / 2.0);
        }
    }

    private void bridge(int n, double d) {
        double d2;
        int n2 = this.levels[n];
        int n3 = this.wallOf(n, d);
        if (n3 < 0) {
            return;
        }
        if (n == this.top) {
            d2 = 22.4;
        } else {
            double d3 = 22.0 - this.SH * (double)n;
            d2 = d3 + this.SH / 2.0;
            this.radialCut(d, d3 - 1.3, d3 + this.SH + 0.4, n2, 2, true);
            this.g.set(Spires.rx(d, d3), n2 + 2, Spires.rz(d, d3), Gothic.s(this.g.pal.chiseled()));
            this.g.set(Spires.rx(d, d3), n2 + 3, Spires.rz(d, d3), Gothic.s(this.g.pal.chiseled()));
        }
        this.stoneBridge(Spires.rx(d, d2), n2, Spires.rz(d, d2), Spires.rx(d, n3 - 1), Spires.rz(d, n3 - 1));
    }

    private void stoneBridge(int n, int n2, int n3, int n4, int n5) {
        int n6 = n4 - n;
        int n7 = n5 - n3;
        int n8 = Math.max(Math.abs(n6), Math.abs(n7));
        if (n8 == 0) {
            return;
        }
        boolean bl = Math.abs(n6) >= Math.abs(n7);
        for (int i = 0; i <= n8; ++i) {
            int n9;
            int n10 = n + Math.round((float)n6 * (float)i / (float)n8);
            int n11 = n3 + Math.round((float)n7 * (float)i / (float)n8);
            for (n9 = -2; n9 <= 2; ++n9) {
                int n12;
                int n13 = bl ? n10 : n10 + n9;
                int n14 = bl ? n11 + n9 : n11;
                this.g.set(n13, n2 - 1, n14, Math.abs(n9) <= 1 ? this.g.floorTile(n13, n14) : Gothic.s(this.g.pal.polished()));
                this.g.set(n13, n2 - 2, n14, Math.abs(n9) == 2 || i % 6 == 0 ? Gothic.s(this.g.pal.chiseled()) : Gothic.slab(this.g.pal.slab(), true));
                if (Math.abs(n9) <= 1) {
                    for (n12 = 0; n12 < 3; ++n12) {
                        this.g.air(n13, n2 + n12, n14);
                    }
                    this.markPath(n13, n2, n14);
                    continue;
                }
                if (i > 0 && i < n8) {
                    this.g.set(n13, n2, n14, i % 6 == 0 ? Gothic.s(this.g.pal.chiseled()) : Gothic.s(this.g.pal.wall()));
                    if (i % 6 != 0) continue;
                    this.g.set(n13, n2 + 1, n14, this.g.lantern(false));
                    continue;
                }
                for (n12 = 0; n12 < 3; ++n12) {
                    this.g.air(n13, n2 + n12, n14);
                }
            }
            if (i % 8 != 4) continue;
            for (n9 = 1; n9 <= 2; ++n9) {
                this.g.set(n10, n2 - 2 - n9, n11, Gothic.s(Blocks.CHAIN));
            }
            this.g.set(n10, n2 - 5, n11, this.g.lantern(true));
        }
        ++this.rooms;
    }

    private void stairTowers() {
        int n = this.levels[this.top];
        for (double d : TOWER_A) {
            int n2;
            int n3;
            int n4;
            int n5;
            int n6;
            int n7 = this.wallAt(d, -29, 25);
            if (n7 < 0) continue;
            int n8 = Spires.rx(d, n7 - 6);
            int n9 = Spires.rz(d, n7 - 6);
            Direction direction = Spires.dirOf(Math.cos(d), Math.sin(d));
            int n10 = n + 5;
            for (n6 = n8 - 4; n6 <= n8 + 4; ++n6) {
                for (n5 = n9 - 4; n5 <= n9 + 4; ++n5) {
                    n4 = Math.abs(n6 - n8) == 4 || Math.abs(n5 - n9) == 4 ? 1 : 0;
                    n3 = Math.abs(n6 - n8) == 4 && Math.abs(n5 - n9) == 4 ? 1 : 0;
                    this.g.set(n6, -31, n5, Gothic.s(this.g.pal.polished()));
                    for (n2 = -30; n2 <= n10; ++n2) {
                        boolean bl;
                        if (n4 == 0) {
                            this.g.air(n6, n2, n5);
                            continue;
                        }
                        int n11 = Math.floorMod(n2 - -30, 7);
                        boolean bl2 = bl = n3 == 0 && (Math.abs(n6 - n8) == 0 || Math.abs(n5 - n9) == 0) && n11 >= 3 && n11 <= 4;
                        this.g.set(n6, n2, n5, n3 != 0 ? Gothic.s(this.g.pal.polished()) : (bl ? this.glass() : (n11 == 6 ? Gothic.s(this.g.pal.chiseled()) : this.g.brick())));
                    }
                }
            }
            for (n6 = n8 - 4; n6 <= n8 + 4; ++n6) {
                for (n5 = n9 - 4; n5 <= n9 + 4; ++n5) {
                    this.g.set(n6, n10 + 1, n5, Gothic.s(this.g.pal.polished()));
                }
            }
            this.g.spire(n8, n9, n10 + 2, 3, 8);
            for (n6 = 0; n6 <= this.top; ++n6) {
                n5 = this.levels[n6];
                for (n4 = n8 - 3; n4 <= n8 + 3; ++n4) {
                    for (n3 = n9 - 3; n3 <= n9 + 3; ++n3) {
                        if (Math.max(Math.abs(n4 - n8), Math.abs(n3 - n9)) != 3) continue;
                        this.g.set(n4, n5 - 1, n3, this.g.floorTile(n4, n3));
                    }
                }
            }
            this.g.spiralStair(n8, n9, n, -30, 1);
            for (n6 = n8 - 5; n6 <= n8 + 5; ++n6) {
                for (n5 = n9 - 5; n5 <= n9 + 5; ++n5) {
                    this.path.add(Spires.key(n6, -30, n5));
                }
            }
            for (n6 = 0; n6 <= this.top; ++n6) {
                this.g.set(n8 + 3, this.levels[n6] + 4, n9 + 3, Flora.LUMEN.get());
            }
            Direction direction2 = direction.getOpposite();
            for (n5 = -30; n5 < -27; ++n5) {
                for (n4 = -1; n4 <= 1; ++n4) {
                    this.g.air(n8 + direction2.getStepX() * 4 + direction2.getClockWise().getStepX() * n4, n5, n9 + direction2.getStepZ() * 4 + direction2.getClockWise().getStepZ() * n4);
                }
            }
            for (n5 = -1; n5 <= 1; ++n5) {
                this.markPath(n8 + direction2.getStepX() * 4 + direction2.getClockWise().getStepX() * n5, -30, n9 + direction2.getStepZ() * 4 + direction2.getClockWise().getStepZ() * n5);
            }
            for (n5 = 1; n5 <= this.top; ++n5) {
                n4 = this.levels[n5];
                n3 = n8 + direction.getStepX() * 4;
                n2 = n9 + direction.getStepZ() * 4;
                this.g.air(n3, n4, n2);
                this.g.air(n3, n4 + 1, n2);
                this.g.set(n3, n4 + 2, n2, Gothic.s(this.g.pal.chiseled()));
                this.walkway(n3 + direction.getStepX(), n2 + direction.getStepZ(), direction, n4, 18);
            }
            ++this.rooms;
        }
    }

    private void walkway(int n, int n2, Direction direction, int n3, int n4) {
        int n5;
        int n6;
        Direction direction2 = direction.getClockWise();
        for (int i = 0; i < n4 && this.inside(n6 = n + direction.getStepX() * i, n3 + 2, n5 = n2 + direction.getStepZ() * i); ++i) {
            boolean bl = this.onPath(n6, n3, n5) && i > 0;
            for (int j = -2; j <= 2; ++j) {
                int n7 = n6 + direction2.getStepX() * j;
                int n8 = n5 + direction2.getStepZ() * j;
                if (Math.abs(j) <= 1) {
                    BlockState blockState = this.g.get(n7, n3 - 1, n8);
                    if (blockState == null || blockState.isAir()) {
                        this.g.set(n7, n3 - 1, n8, this.g.floorTile(n7, n8));
                    }
                    for (int k = 0; k < 3; ++k) {
                        if (this.g.get(n7, n3 + k, n8) == null) continue;
                        this.g.air(n7, n3 + k, n8);
                    }
                    this.markPath(n7, n3, n8);
                } else if (!bl && this.g.isAir(n7, n3, n8) && this.g.get(n7, n3, n8) != null && !this.onPath(n7, n3, n8)) {
                    if (this.g.isAir(n7, n3 - 1, n8)) {
                        this.g.set(n7, n3 - 1, n8, Gothic.s(this.g.pal.polished()));
                    }
                    this.g.set(n7, n3, n8, Gothic.s(this.g.pal.wall()));
                }
                if (Math.abs(j) > 1 || i % 4 != 2 || !this.g.isAir(n7, n3 - 2, n8) || this.g.get(n7, n3 - 2, n8) == null) continue;
                this.g.set(n7, n3 - 2, n8, Gothic.stairs(this.g.pal.stairs(), direction.getOpposite(), true));
            }
            if (bl) break;
        }
    }

    private void pillars() {
        for (double d : PILLAR_A) {
            int n;
            int n2;
            int n3;
            int n4 = this.wallAt(d, -29, 25);
            if (n4 < 0) continue;
            int n5 = Spires.rx(d, Math.min(30, n4 - 6));
            int n6 = Spires.rz(d, Math.min(30, n4 - 6));
            for (n3 = -30; n3 < 30 && this.cave(n5, n3 + 1, n6); ++n3) {
            }
            if (n3 - -30 < 10) continue;
            for (n2 = -30; n2 <= n3; ++n2) {
                n = n3 - n2;
                int n7 = n2 < -28 ? 2 : (n < 2 ? 3 : (n < 4 ? 2 : 1));
                for (int i = n5 - n7; i <= n5 + n7; ++i) {
                    for (int j = n6 - n7; j <= n6 + n7; ++j) {
                        boolean bl;
                        boolean bl2 = bl = (n2 - -30) % 7 == 6;
                        BlockState blockState = n2 < -28 ? Gothic.s(this.g.pal.polished()) : (n < 4 ? (n7 == 3 && (i == n5 - 3 || i == n5 + 3 || j == n6 - 3 || j == n6 + 3) ? Gothic.stairs(this.g.pal.stairs(), Spires.dirOf(i - n5, j - n6).getOpposite(), true) : Gothic.s(this.g.pal.chiseled())) : (bl ? Gothic.s(this.g.pal.chiseled()) : (i == n5 != (j == n6) ? Gothic.s(this.g.pal.polished()) : this.g.brick())));
                        this.g.set(i, n2, j, blockState);
                    }
                }
                if ((n2 - -30) % 7 == 6 && n2 + 1 < n3 - 4) {
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        this.g.set(n5 + direction.getStepX() * 2, n2, n6 + direction.getStepZ() * 2, Gothic.s(this.g.pal.polished()));
                    }
                }
                if ((n2 - -30) % 7 != 5 || n2 <= -28 || n2 + 2 >= n3 - 4) continue;
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    this.g.set(n5 + direction.getStepX() * 2, n2, n6 + direction.getStepZ() * 2, this.g.lantern(true));
                }
            }
            for (n2 = n5 - 3; n2 <= n5 + 3; ++n2) {
                for (n = n6 - 3; n <= n6 + 3; ++n) {
                    this.path.add(Spires.key(n2, -30, n));
                }
            }
        }
    }

    private void avenues() {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int n;
            int n2;
            Direction direction2 = direction.getClockWise();
            for (int i = 24; i < this.RX && this.cave(n2 = direction.getStepX() * i, -30, n = direction.getStepZ() * i); ++i) {
                for (int j = -2; j <= 2; ++j) {
                    int n3;
                    int n4 = n2 + direction2.getStepX() * j;
                    if (!this.cave(n4, -30, n3 = n + direction2.getStepZ() * j)) continue;
                    this.g.set(n4, -31, n3, Math.abs(j) == 2 ? Gothic.s(this.g.pal.polished()) : Gothic.s(this.g.pal.floor()));
                    this.markPath(n4, -30, n3);
                }
                if (i % 6 != 0) continue;
                for (int n5 : new int[]{-3, 3}) {
                    int n6;
                    int n7 = n2 + direction2.getStepX() * n5;
                    if (!this.cave(n7, -26, n6 = n + direction2.getStepZ() * n5)) continue;
                    this.g.set(n7, -30, n6, Gothic.s(this.g.pal.chiseled()));
                    for (int j = 1; j <= 3; ++j) {
                        this.g.set(n7, -30 + j, n6, Gothic.s(this.g.pal.wall()));
                    }
                    this.g.set(n7, -26, n6, this.g.lantern(false));
                    this.path.add(Spires.key(n7, -30, n6));
                }
            }
        }
    }

    private void gardens() {
        int n;
        double d;
        int n2;
        int n3;
        int n4 = 16;
        Garden[] gardenArray = new Garden[]{Garden.GROVE, Garden.FLOWERS, Garden.MUSHROOMS, Garden.CROPS, Garden.MOSS};
        int n5 = 720;
        for (n3 = 0; n3 < n5; ++n3) {
            double d2 = (double)n3 * Math.PI * 2.0 / (double)n5;
            int n6 = this.wallAt(d2, -29, 25);
            if (n6 < 0) continue;
            n2 = (int)(d2 / (Math.PI * 2) * (double)n4);
            d = (double)n2 * Math.PI * 2.0 / (double)n4;
            Garden garden = gardenArray[n2 % gardenArray.length];
            n = Spires.angDist(d2, d) < 0.6 / (double)n6 ? 1 : 0;
            for (double d3 = (double)(n6 - 11); d3 < (double)n6; d3 += 0.5) {
                BlockState blockState;
                int n7;
                int n8 = Spires.rx(d2, d3);
                if (!this.cave(n8, -30, n7 = Spires.rz(d2, d3)) || this.path.contains(Spires.key(n8, -30, n7)) || (blockState = this.g.get(n8, -31, n7)) == null || blockState.isAir()) continue;
                if (d3 < (double)(n6 - 9)) {
                    this.g.set(n8, -31, n7, Gothic.s(this.g.pal.polished()));
                    continue;
                }
                if (n != 0 && d3 > (double)n6 - 8.5 && d3 < (double)n6 - 1.5) {
                    this.g.set(n8, -31, n7, Gothic.s(this.g.pal.polished()));
                    if (!(Math.abs(d3 - (double)(n6 - 5)) > 1.0)) continue;
                    this.g.set(n8, -30, n7, Gothic.s(this.g.pal.wall()));
                    continue;
                }
                this.soil(n8, n7, garden, d3, n6);
                if (Math.floorMod(n8, 4) != 0 || Math.floorMod(n7, 4) != 0) continue;
                this.g.set(n8, -31, n7, Flora.LUMEN.get());
                BlockState blockState2 = this.g.get(n8, -30, n7);
                if (blockState2 == null || blockState2.isAir() || blockState2.is(Blocks.WATER)) continue;
                this.g.air(n8, -30, n7);
            }
        }
        for (n3 = 0; n3 < n4; ++n3) {
            int n9;
            Garden garden = gardenArray[n3 % gardenArray.length];
            double d4 = ((double)n3 + 0.5) * Math.PI * 2.0 / (double)n4;
            n2 = this.wallAt(d4, -29, 25);
            if (n2 < 0 || (d = Math.max((double)(n2 - 5), 28.5)) > (double)n2 - 2.5 || this.path.contains(Spires.key(n9 = Spires.rx(d4, d), -30, n = Spires.rz(d4, d))) || this.blocked(d4, 0, 4.0, n2)) continue;
            switch (garden.ordinal()) {
                case 0: {
                    this.ancientTree(n9, -30, n, 6 + this.r.nextInt(3), 2);
                    break;
                }
                case 2: {
                    this.giantMushroom(n9, -30, n, this.r.nextBoolean());
                    break;
                }
                case 3: {
                    this.g.set(n9, -31, n, Gothic.s(Blocks.WATER));
                    this.g.set(n9, -30, n, Gothic.s(Blocks.AIR));
                    break;
                }
                case 4: {
                    this.ancientBush(n9, -30, n);
                    break;
                }
                case 1: {
                    this.g.set(n9, -31, n, Flora.LUMEN.get());
                    this.g.set(n9, -30, n, Gothic.s(Blocks.FLOWERING_AZALEA));
                }
            }
            ++this.rooms;
        }
    }

    private void soil(int n, int n2, Garden garden, double d, int n3) {
        float f = this.r.nextFloat();
        switch (garden.ordinal()) {
            case 0: {
                this.g.set(n, -31, n2, Gothic.s(f < 0.15f ? Blocks.ROOTED_DIRT : Blocks.MOSS_BLOCK));
                if (f < 0.04f) {
                    this.g.set(n, -31, n2, Flora.LUMEN.get());
                    break;
                }
                if (f > 0.9f) {
                    this.g.set(n, -30, n2, Flora.SHRUB.get());
                    break;
                }
                if (f > 0.82f) {
                    this.g.set(n, -30, n2, Flora.BERRY.get());
                    break;
                }
                if (f > 0.65f) {
                    this.g.set(n, -30, n2, Flora.FERN.get());
                    break;
                }
                if (!(f > 0.4f)) break;
                this.g.set(n, -30, n2, Flora.GRASS.get());
                break;
            }
            case 1: {
                this.g.set(n, -31, n2, Gothic.s(Blocks.GRASS_BLOCK));
                if (f < 0.05f) {
                    this.g.set(n, -31, n2, Flora.LUMEN.get());
                    break;
                }
                if (f > 0.85f) {
                    this.g.set(n, -30, n2, Flora.GRASS.get());
                    break;
                }
                if (f > 0.78f) {
                    this.g.set(n, -30, n2, Flora.SHRUB.get());
                    break;
                }
                if (!(f > 0.3f)) break;
                Block[] blockArray = new Block[]{Blocks.POPPY, Blocks.CORNFLOWER, Blocks.ALLIUM, Blocks.LILY_OF_THE_VALLEY, Blocks.OXEYE_DAISY, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.WHITE_TULIP, Blocks.BLUE_ORCHID};
                this.g.set(n, -30, n2, Gothic.s(blockArray[this.r.nextInt(blockArray.length)]));
                break;
            }
            case 2: {
                this.g.set(n, -31, n2, Gothic.s(f < 0.5f ? Blocks.MYCELIUM : Blocks.PODZOL));
                if (f > 0.7f) {
                    this.g.set(n, -30, n2, Gothic.s(this.r.nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM));
                    break;
                }
                if (!(f < 0.04f)) break;
                this.g.set(n, -31, n2, Flora.LUMEN.get());
                break;
            }
            case 3: {
                int n4 = (int)Math.round(d) - (n3 - 9);
                if (n4 % 4 == 2) {
                    this.g.set(n, -31, n2, Gothic.s(Blocks.WATER));
                    return;
                }
                this.g.set(n, -31, n2, Gothic.s(Blocks.FARMLAND));
                Block[] blockArray = new Block[]{Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS};
                this.g.set(n, -30, n2, Gothic.s(blockArray[Math.floorMod(n4 / 4, blockArray.length)]));
                break;
            }
            case 4: {
                this.g.set(n, -31, n2, Gothic.s(Blocks.MOSS_BLOCK));
                if (f > 0.88f) {
                    this.g.set(n, -30, n2, Gothic.s(this.r.nextBoolean() ? Blocks.AZALEA : Blocks.FLOWERING_AZALEA));
                    break;
                }
                if (f > 0.76f) {
                    this.g.set(n, -30, n2, Flora.BERRY.get());
                    break;
                }
                if (f > 0.6f) {
                    this.g.set(n, -30, n2, Flora.FERN.get());
                    break;
                }
                if (f > 0.4f) {
                    this.g.set(n, -30, n2, Gothic.s(Blocks.MOSS_CARPET));
                    break;
                }
                if (!(f < 0.05f)) break;
                this.g.set(n, -31, n2, Flora.LUMEN.get());
            }
        }
    }

    private void ancientTree(int n, int n2, int n3, int n4, int n5) {
        AncientTrees.grow(new AncientTrees.Setter(){

            @Override
            public void set(int n, int n2, int n3, BlockState blockState) {
                if (Spires.this.g.get(n, n2, n3) != null) {
                    Spires.this.g.set(n, n2, n3, blockState);
                }
            }

            @Override
            public boolean free(int n, int n2, int n3) {
                BlockState blockState = Spires.this.g.get(n, n2, n3);
                return blockState != null && (blockState.isAir() || blockState.is(Blocks.MOSS_CARPET) || blockState.is(Blocks.FERN)) && Spires.this.inside(n, n2, n3) && !Spires.this.onPath(n, n2, n3);
            }
        }, this.r, n, n2, n3, AncientTrees.random(this.r), Math.max(1.0, Math.min(3.0, (double)(n4 - 5) * 0.6 + (double)n5 * 0.3)));
    }

    private void ancientBush(int n, int n2, int n3) {
        this.g.set(n, n2 - 1, n3, Gothic.s(Blocks.MOSS_BLOCK));
        this.g.set(n, n2, n3, Flora.LOG.get());
        this.g.set(n, n2 + 1, n3, Flora.BLOSSOM.get());
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            boolean bl;
            int n4;
            int n5 = n + direction.getStepX();
            if (!this.g.isAir(n5, n2, n4 = n3 + direction.getStepZ()) || this.g.get(n5, n2, n4) == null) continue;
            BlockState blockState = this.g.get(n5, n2 - 1, n4);
            boolean bl2 = bl = blockState != null && (blockState.is(Blocks.MOSS_BLOCK) || blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.ROOTED_DIRT));
            this.g.set(n5, n2, n4, bl && this.r.nextBoolean() ? (this.r.nextBoolean() ? Flora.SHRUB.get() : Flora.BERRY.get()) : Flora.LEAVES.get());
        }
    }

    private void giantMushroom(int n, int n2, int n3, boolean bl) {
        int n4;
        int n5 = 5 + this.r.nextInt(3);
        for (n4 = 0; n4 < n5; ++n4) {
            this.g.set(n, n2 + n4, n3, Gothic.s(Blocks.MUSHROOM_STEM));
        }
        n4 = bl ? 2 : 3;
        for (int i = -n4; i <= n4; ++i) {
            for (int j = -n4; j <= n4; ++j) {
                if (Math.abs(i) == n4 && Math.abs(j) == n4) continue;
                this.g.set(n + i, n2 + n5, n3 + j, Gothic.s(bl ? Blocks.RED_MUSHROOM_BLOCK : Blocks.BROWN_MUSHROOM_BLOCK));
                if (!bl || Math.abs(i) != n4 && Math.abs(j) != n4) continue;
                this.g.set(n + i, n2 + n5 - 1, n3 + j, Gothic.s(Blocks.RED_MUSHROOM_BLOCK));
            }
        }
        this.g.set(n, n2 + n5 - 1, n3, Flora.LUMEN.get());
    }

    private void floatingHouses(double[] dArray) {
        for (int i = 0; i < dArray.length; ++i) {
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
            int n12;
            int n13;
            int n14 = n13 = i % 2 == 0 ? 2 : 4;
            if (n13 >= this.top) continue;
            double d = dArray[i];
            int n15 = this.levels[n13];
            int n16 = this.wallOf(n13, d);
            if (n16 < 0) continue;
            double d2 = n16 - 12;
            int n17 = Spires.rx(d, d2);
            int n18 = Spires.rz(d, d2);
            Direction direction = Spires.dirOf(Math.cos(d), Math.sin(d));
            for (n12 = n17 - 4; n12 <= n17 + 4; ++n12) {
                for (n11 = n18 - 4; n11 <= n18 + 4; ++n11) {
                    n10 = Math.abs(n12 - n17) == 4 || Math.abs(n11 - n18) == 4 ? 1 : 0;
                    this.g.set(n12, n15 - 1, n11, n10 != 0 ? Gothic.s(this.g.pal.polished()) : Gothic.s(Blocks.SPRUCE_PLANKS));
                    n9 = Math.max(Math.abs(n12 - n17), Math.abs(n11 - n18));
                    for (n8 = 2; n8 <= 5 - n9 + 1 && n9 <= 4; ++n8) {
                        this.g.set(n12, n15 - n8, n11, this.g.brick());
                    }
                    int n19 = n8 = (n12 - n17) * direction.getStepX() + (n11 - n18) * direction.getStepZ() == 4 && Math.abs((n12 - n17) * direction.getStepZ() - (n11 - n18) * direction.getStepX()) <= 1 ? 1 : 0;
                    if (n10 == 0 || n8 != 0 || (n12 + n11 & 1) != 0) continue;
                    this.g.set(n12, n15, n11, Gothic.s(Blocks.SPRUCE_FENCE));
                }
            }
            this.g.chainLantern(n17, n15 - 6, n18, 2);
            for (n12 = n17 - 2; n12 <= n17 + 2; ++n12) {
                for (n11 = n18 - 2; n11 <= n18 + 2; ++n11) {
                    n10 = Math.abs(n12 - n17) == 2 || Math.abs(n11 - n18) == 2 ? 1 : 0;
                    n9 = Math.abs(n12 - n17) == 2 && Math.abs(n11 - n18) == 2 ? 1 : 0;
                    for (n8 = n15; n8 < n15 + 4; ++n8) {
                        if (n10 == 0) {
                            this.g.air(n12, n8, n11);
                            continue;
                        }
                        boolean bl = n9 == 0 && (n12 == n17 || n11 == n18) && n8 == n15 + 1;
                        n7 = n8 == n15 + 3 || n8 == n15 ? 1 : 0;
                        int n20 = n6 = n9 == 0 && !bl && Math.abs(n12 - n17 + (n11 - n18)) == 1 && n8 == n15 + 2 ? 1 : 0;
                        this.g.set(n12, n8, n11, n9 != 0 ? Gothic.s(Blocks.DARK_OAK_LOG) : (bl ? this.warmGlass() : (n7 != 0 ? Gothic.s(Blocks.STRIPPED_DARK_OAK_LOG) : (n6 != 0 ? Gothic.s(Blocks.DARK_OAK_PLANKS) : Flora.PLANKS.get()))));
                    }
                }
            }
            this.g.gable(n17 - 2, n18 - 2, n17 + 2, n18 + 2, n15 + 4, direction.getAxis() == Direction.Axis.X);
            n12 = n17 - direction.getStepX() * 2 + direction.getClockWise().getStepX() * 2;
            n11 = n18 - direction.getStepZ() * 2 + direction.getClockWise().getStepZ() * 2;
            for (n10 = n15; n10 <= n15 + 7; ++n10) {
                this.g.set(n12, n10, n11, n10 == n15 + 7 ? Gothic.s(Blocks.CAMPFIRE) : Gothic.s(Blocks.COBBLED_DEEPSLATE));
            }
            n10 = n17 + direction.getStepX() * 2;
            n9 = n18 + direction.getStepZ() * 2;
            this.g.air(n10, n15, n9);
            this.g.air(n10, n15 + 1, n9);
            int[] nArray = new int[]{n17 - direction.getStepX(), n18 - direction.getStepZ()};
            Direction direction2 = direction.getClockWise();
            this.bed(nArray[0] + direction2.getStepX(), n15, nArray[1] + direction2.getStepZ(), direction.getOpposite());
            this.g.set(nArray[0] - direction2.getStepX(), n15, nArray[1] - direction2.getStepZ(), Gothic.s(Blocks.BARREL));
            this.g.set(n17, n15 + 3, n18, Flora.LUMEN.get());
            if (this.r.nextFloat() < 0.5f) {
                this.g.chest(n17 + direction2.getStepX(), n15, n18 + direction2.getStepZ(), true);
            }
            for (int[] nArray2 : new int[][]{{-4, -4}, {4, -4}, {-4, 4}, {4, 4}}) {
                n5 = n17 + nArray2[0];
                n4 = n18 + nArray2[1];
                for (n3 = n15; n3 < 30 && this.g.isAir(n5, n3, n4) && this.g.get(n5, n3, n4) != null; ++n3) {
                }
                if (n3 >= 30) continue;
                for (n2 = n15; n2 < n3; ++n2) {
                    if (!this.g.isAir(n5, n2, n4)) continue;
                    this.g.set(n5, n2, n4, Gothic.s(Blocks.CHAIN));
                }
            }
            n7 = n17 + direction.getStepX() * 5;
            n6 = n18 + direction.getStepZ() * 5;
            for (int j = 0; j < 16 && this.inside(n = n7 + direction.getStepX() * j, n15, n5 = n6 + direction.getStepZ() * j) && (!this.onPath(n, n15, n5) || j <= 0); ++j) {
                for (n4 = -1; n4 <= 1; ++n4) {
                    n3 = n + direction2.getStepX() * n4;
                    n2 = n5 + direction2.getStepZ() * n4;
                    if (n4 == 0) {
                        this.g.set(n3, n15 - 1, n2, Gothic.s(Blocks.SPRUCE_PLANKS));
                        for (int k = 0; k < 3; ++k) {
                            this.g.air(n3, n15 + k, n2);
                        }
                        this.markPath(n3, n15, n2);
                        continue;
                    }
                    if (!this.g.isAir(n3, n15, n2) || this.g.get(n3, n15, n2) == null) continue;
                    this.g.set(n3, n15 - 1, n2, Gothic.slab(Blocks.SPRUCE_SLAB, true));
                    this.g.set(n3, n15, n2, Gothic.s(Blocks.SPRUCE_FENCE));
                }
            }
            ++this.rooms;
        }
    }

    private void hangingGardens() {
        for (int i = 0; i < 260; ++i) {
            int n;
            int n2;
            int n3;
            int n4;
            double d = this.r.nextDouble() * Math.PI * 2.0;
            double d2 = 17.0 + this.r.nextDouble() * 20.0;
            int n5 = Spires.rx(d, d2);
            int n6 = Spires.rz(d, d2);
            for (n4 = -8; n4 < 30 && this.cave(n5, n4, n6); ++n4) {
            }
            if (n4 >= 30 || !this.inside(n5, n4, n6) || !this.cave(n5, n3 = n4 - 1, n6)) continue;
            float f = this.r.nextFloat();
            if (f < 0.4f) {
                n2 = 3 + this.r.nextInt(8);
                for (n = 0; n < n2; ++n) {
                    if (this.cave(n5, n3 - n, n6) && this.cave(n5, n3 - n - 2, n6)) continue;
                    n2 = n;
                    break;
                }
                for (n = 0; n < n2; ++n) {
                    this.g.set(n5, n3 - n, n6, Gothic.s(n == n2 - 1 ? Blocks.WEEPING_VINES : Blocks.WEEPING_VINES_PLANT));
                }
                continue;
            }
            if (f < 0.55f) {
                this.g.set(n5, n3, n6, Gothic.s(Blocks.SPORE_BLOSSOM));
                continue;
            }
            if (f < 0.7f) {
                this.g.set(n5, n3, n6, Gothic.s(Blocks.HANGING_ROOTS));
                continue;
            }
            if (f < 0.85f) {
                for (n2 = -1; n2 <= 1; ++n2) {
                    for (n = -1; n <= 1; ++n) {
                        if (!this.cave(n5 + n2, n3, n6 + n) || this.cave(n5 + n2, n3 + 1, n6 + n)) continue;
                        this.g.set(n5 + n2, n3, n6 + n, this.r.nextBoolean() ? Flora.BLOSSOM.get() : Flora.LEAVES.get());
                    }
                }
                this.g.set(n5, n3, n6, Flora.LUMEN.get());
                continue;
            }
            if (!this.cave(n5, n3 - 6, n6)) continue;
            n2 = 3 + this.r.nextInt(5);
            for (n = 0; n < n2; ++n) {
                this.g.set(n5, n3 - n, n6, Gothic.s(Blocks.CHAIN));
            }
            this.g.set(n5, n3 - n2, n6, this.g.lantern(true));
        }
    }

    private void lights() {
        for (int i = 0; i < 160; ++i) {
            int n;
            int n2 = -this.RX + this.r.nextInt(2 * this.RX + 1);
            int n3 = -this.RX + this.r.nextInt(2 * this.RX + 1);
            for (n = -8; n < 30 && this.cave(n2, n, n3); ++n) {
            }
            if (n >= 30 || !this.inside(n2, n, n3) || n <= -6) continue;
            this.g.set(n2, n, n3, this.r.nextFloat() < 0.7f ? Flora.LUMEN.get() : Gothic.s(Blocks.OCHRE_FROGLIGHT));
        }
        this.g.chest(-3, -30, 26, true);
    }

    private static enum Room {
        STORE,
        DINING,
        ARMORY,
        CELL,
        DORM,
        STUDY,
        LIBRARY,
        READING,
        GREENHOUSE,
        ALCHEMY,
        THRONE,
        CHAPEL;

    }

    private static enum House {
        HOME,
        SMITHY,
        FARM,
        APOTHECARY,
        TRADE,
        LIBRARY,
        SHRINE,
        OASIS,
        TAVERN,
        ALCHEMY;

    }

    private static enum Garden {
        GROVE,
        FLOWERS,
        MUSHROOMS,
        CROPS,
        MOSS;

    }
}
