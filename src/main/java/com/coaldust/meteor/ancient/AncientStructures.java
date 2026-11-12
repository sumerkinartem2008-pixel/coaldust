/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BedPart
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.coaldust.meteor.ancient;

import com.coaldust.meteor.gothic.Gothic;
import com.coaldust.meteor.underground.Connect;
import com.coaldust.meteor.underground.StructureDecor;
import com.coaldust.meteor.underground.UndergroundCity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public final class AncientStructures {
    public static final String[][] VARIANTS = new String[][]{{"shrine", "gallows"}, {"temple", "warlock_tower", "monastery"}, {"city", "vampire_castle", "cursed_village"}, {"colossal_city", "cathedral"}, {"dwarf_town", "dwarf_hold", "deep_kingdom"}};
    private final UndergroundCity.Plan plan = new UndergroundCity.Plan();
    private final RandomSource r;
    private final Gothic g;
    private final Gothic w;
    private final int R;
    private double ph1;
    private double ph2;
    private static final Direction[] H4 = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static final String[] DWARF_FACE = new String[]{"h.............h", "hh...........hh", ".hh..HHHHH..hh.", "..hhHHHGHHHhh..", "...HHHHGHHHH...", "...HHHHGHHHH...", "..GGGGGGGGGGG..", "..#SS#####SS#..", "..#EE##N##EE#..", "..###NNNNN###..", "..MMMMM.MMMMM..", "..BBBBB.BBBBB..", "...BBBBBBBBB...", "...BBBBGBBBB...", "....BBBGBBB....", ".....BBGBB.....", "......BGB......", ".......G......."};

    private AncientStructures(RandomSource randomSource, Gothic.Pal pal, int n) {
        this.r = randomSource;
        this.R = n;
        this.g = new Gothic(this.plan, randomSource, pal);
        this.g.decay = 0.08f + randomSource.nextFloat() * 0.3f;
        this.w = new Gothic(this.plan, randomSource, new Gothic.Pal("wood", Blocks.DARK_OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_FENCE, pal.roof(), pal.roofStairs(), pal.roofSlab(), Blocks.BROWN_STAINED_GLASS_PANE, Blocks.SPRUCE_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.STRIPPED_SPRUCE_LOG, pal.soul()));
        this.w.decay = this.g.decay;
    }

    public static Result plan(Kind kind, RandomSource randomSource) {
        return AncientStructures.plan(kind, randomSource, -1);
    }

    public static Result plan(Kind kind, RandomSource randomSource, int n) {
        boolean bl;
        String[] stringArray = VARIANTS[kind.ordinal()];
        String string = stringArray[n >= 0 && n < stringArray.length ? n : randomSource.nextInt(stringArray.length)];
        boolean bl2 = bl = string.startsWith("dwarf") || string.equals("deep_kingdom");
        Gothic.Pal pal = bl ? (randomSource.nextBoolean() ? Gothic.DWARF : Gothic.DWARF_DEEP) : Gothic.roll(randomSource);
        AncientStructures ancientStructures = new AncientStructures(randomSource, pal, kind.radius);
        ancientStructures.body();
        switch (string) {
            case "shrine": {
                ancientStructures.shrine();
                break;
            }
            case "gallows": {
                ancientStructures.gallowsHill();
                break;
            }
            case "temple": {
                ancientStructures.temple();
                break;
            }
            case "warlock_tower": {
                ancientStructures.warlockTower();
                break;
            }
            case "monastery": {
                ancientStructures.monastery();
                break;
            }
            case "city": {
                ancientStructures.necropolis();
                break;
            }
            case "vampire_castle": {
                ancientStructures.vampireCastle();
                break;
            }
            case "cursed_village": {
                ancientStructures.cursedVillage();
                break;
            }
            case "cathedral": {
                ancientStructures.cathedral();
                break;
            }
            case "dwarf_town": {
                ancientStructures.dwarfTown();
                break;
            }
            case "dwarf_hold": {
                ancientStructures.dwarfHold();
                break;
            }
            case "deep_kingdom": {
                ancientStructures.deepKingdom();
                break;
            }
            default: {
                ancientStructures.citadel();
            }
        }
        if (bl) {
            ancientStructures.g.decay *= 0.4f;
        }
        if (!bl) {
            ancientStructures.weather();
        } else {
            Connect.run(ancientStructures.plan, randomSource, pal.floor(), pal.stairs(), 6);
        }
        StructureDecor.cheapen(ancientStructures.plan, randomSource);
        StructureDecor.transform(ancientStructures.plan, randomSource.nextInt(8));
        return new Result(ancientStructures.plan, kind, pal.id(), string);
    }

    private double edge(int n, int n2) {
        double d = Math.atan2(n2, n);
        return (double)this.R * (1.0 + 0.07 * Math.sin(d * 5.0 + this.ph1) + 0.05 * Math.sin(d * 13.0 + this.ph2));
    }

    private boolean onDisc(int n, int n2) {
        return Math.sqrt(n * n + n2 * n2) <= this.edge(n, n2) - 0.5;
    }

    private void body() {
        int n;
        int n2;
        double d;
        double d2;
        int n3;
        int n4;
        this.ph1 = this.r.nextDouble() * 6.28;
        this.ph2 = this.r.nextDouble() * 6.28;
        int n5 = Math.max(4, this.R / 2);
        int n6 = this.R + 30;
        for (n4 = -this.R - 3; n4 <= this.R + 3; ++n4) {
            for (n3 = -this.R - 3; n3 <= this.R + 3; ++n3) {
                float f;
                d2 = Math.sqrt(n4 * n4 + n3 * n3);
                if (d2 > (d = this.edge(n4, n3))) continue;
                double d3 = d2 / d;
                n2 = (int)Math.round((double)n5 * Math.sqrt(Math.max(0.0, 1.0 - d3 * d3)) + 1.0 + (double)this.r.nextInt(2));
                for (int i = -n2; i <= -2; ++i) {
                    f = this.r.nextFloat();
                    this.g.set(n4, i, n3, Gothic.s(i > -3 && d3 > 0.85 ? Blocks.BLACKSTONE : (f < 0.35f ? Blocks.DEEPSLATE : (f < 0.6f ? Blocks.BLACKSTONE : (f < 0.8f ? Blocks.BASALT : (f < 0.95f ? Blocks.TUFF : Blocks.MAGMA_BLOCK))))));
                }
                f = this.r.nextFloat();
                BlockState blockState = d3 > 0.92 ? (f < 0.5f ? Gothic.s(Blocks.BLACKSTONE) : Gothic.s(Blocks.COBBLED_DEEPSLATE)) : (f < 0.1f + this.g.decay * 0.3f ? Gothic.s(Blocks.COBBLED_DEEPSLATE) : (f < 0.16f ? Gothic.s(Blocks.SOUL_SOIL) : ((n4 * 7 + n3 * 3 & 3) == 0 ? Gothic.s(this.g.pal.floorAlt()) : Gothic.s(this.g.pal.floor()))));
                this.g.set(n4, -1, n3, blockState);
                for (n = 0; n < n6; ++n) {
                    this.g.air(n4, n, n3);
                }
            }
        }
        n4 = 6 + this.R / 3;
        for (n3 = 0; n3 < n4; ++n3) {
            d2 = this.r.nextDouble() * Math.PI * 2.0;
            d = (double)this.R * 0.95;
            int n7 = (int)Math.round(Math.cos(d2) * d);
            int n8 = (int)Math.round(Math.sin(d2) * d);
            n2 = 3 + this.r.nextInt(this.R / 3 + 2);
            for (int i = 0; i < n2; ++i) {
                int n9 = n7 + (int)Math.round(Math.cos(d2) * (double)i * 0.5);
                n = n8 + (int)Math.round(Math.sin(d2) * (double)i * 0.5);
                int n10 = -2 + (int)Math.round((double)i * 0.8);
                this.g.set(n9, n10, n, Gothic.s(i > n2 - 2 ? Blocks.CRYING_OBSIDIAN : Blocks.OBSIDIAN));
                if (i >= n2 / 2) continue;
                this.g.set(n9, n10 - 1, n, Gothic.s(Blocks.OBSIDIAN));
            }
        }
    }

    private void soil(int n, int n2, int n3, int n4) {
        for (int i = n; i <= n3; ++i) {
            for (int j = n2; j <= n4; ++j) {
                if (!this.onDisc(i, j)) continue;
                this.g.set(i, -1, j, this.r.nextFloat() < 0.6f ? Gothic.s(Blocks.PODZOL) : Gothic.s(Blocks.COARSE_DIRT));
            }
        }
    }

    private void graveyard(int n, int n2, int n3, int n4) {
        int n5;
        this.soil(n, n2, n3, n4);
        for (n5 = n + 1; n5 < n3; n5 += 2) {
            for (int i = n2 + 1; i < n4; i += 3) {
                if (!this.onDisc(n5, i) || !(this.r.nextFloat() < 0.8f)) continue;
                this.g.grave(n5, 0, i, Direction.SOUTH);
            }
        }
        for (n5 = n; n5 <= n3; ++n5) {
            for (int n6 : new int[]{n2, n4}) {
                if (!this.onDisc(n5, n6) || !(this.r.nextFloat() < 0.8f)) continue;
                this.g.set(n5, 0, n6, Gothic.s(Blocks.IRON_BARS));
            }
        }
        for (n5 = n2; n5 <= n4; ++n5) {
            for (int n6 : new int[]{n, n3}) {
                if (!this.onDisc(n6, n5) || !(this.r.nextFloat() < 0.8f)) continue;
                this.g.set(n6, 0, n5, Gothic.s(Blocks.IRON_BARS));
            }
        }
        if (this.r.nextBoolean()) {
            this.g.deadTree(n + 1 + this.r.nextInt(Math.max(1, n3 - n - 1)), 0, n4 - 1, 5 + this.r.nextInt(4));
        }
    }

    private void lampPost(int n, int n2) {
        if (!this.onDisc(n, n2)) {
            return;
        }
        this.g.set(n, 0, n2, Gothic.s(this.g.pal.polished()));
        for (int i = 1; i <= 3; ++i) {
            this.g.set(n, i, n2, Gothic.s(this.g.pal.wall()));
        }
        this.g.set(n, 4, n2, Gothic.s(this.g.pal.chiseled()));
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!(this.r.nextFloat() < 0.6f)) continue;
            this.g.set(n + direction.getStepX(), 4, n2 + direction.getStepZ(), Gothic.stairs(this.g.pal.stairs(), direction.getOpposite(), true));
            this.g.set(n + direction.getStepX(), 3, n2 + direction.getStepZ(), this.g.lantern(true));
        }
    }

    private int chapel(int n, int n2, int n3, int n4, int n5, boolean bl, boolean bl2, boolean bl3) {
        int n6;
        int n7;
        int n8;
        int n9;
        int n10;
        int n112;
        int n12;
        int n13;
        int n14 = n3 / 2;
        int n15 = n4 / 2;
        int n16 = bl ? n - n14 : n - n15;
        int n17 = bl ? n + n14 : n + n15;
        int n18 = bl ? n2 - n15 : n2 - n14;
        int n19 = bl ? n2 + n15 : n2 + n14;
        this.g.hall(n16, n18, n17, n19, 0, n5, 4);
        for (n13 = (bl ? n16 : n18) + 2; n13 <= (bl ? n17 : n19) - 2; n13 += 4) {
            if (bl) {
                this.g.buttress(n13, n18, Direction.NORTH, 0, n5);
                this.g.buttress(n13, n19, Direction.SOUTH, 0, n5);
                this.g.window(n13 + 2 <= n17 - 1 ? n13 + 2 : n13 - 2, 2, n18, Direction.Axis.X, n5 - 4, false);
                this.g.window(n13 + 2 <= n17 - 1 ? n13 + 2 : n13 - 2, 2, n19, Direction.Axis.X, n5 - 4, false);
                continue;
            }
            this.g.buttress(n16, n13, Direction.WEST, 0, n5);
            this.g.buttress(n17, n13, Direction.EAST, 0, n5);
            this.g.window(n16, 2, n13 + 2 <= n19 - 1 ? n13 + 2 : n13 - 2, Direction.Axis.Z, n5 - 4, false);
            this.g.window(n17, 2, n13 + 2 <= n19 - 1 ? n13 + 2 : n13 - 2, Direction.Axis.Z, n5 - 4, false);
        }
        n13 = this.g.gable(n16, n18, n17, n19, n5 + 1, bl);
        int n20 = bl ? (bl2 ? n16 : n17) : (n12 = n);
        int n21 = bl ? n2 : (bl2 ? n18 : n19);
        Direction.Axis axis = bl ? Direction.Axis.Z : Direction.Axis.X;
        this.g.doorway(n12, 0, n21, axis, 4);
        if (n4 >= 7) {
            this.g.rose(bl ? n2 : n, n5 + 1 + Math.max(2, n15 / 2), bl ? n12 : n21, axis, Math.min(n15 - 1, 3));
        }
        Direction direction = bl ? (bl2 ? Direction.WEST : Direction.EAST) : (bl2 ? Direction.NORTH : Direction.SOUTH);
        for (int n112 : new int[]{-1, 1}) {
            n10 = bl ? n12 + direction.getStepX() : n + n112 * n15;
            n9 = bl ? n2 + n112 * n15 : n21 + direction.getStepZ();
            this.g.gargoyle(n10, n5, n9, direction);
        }
        for (n8 = -1; n8 <= 1; ++n8) {
            n7 = bl ? n12 + direction.getStepX() : n + n8;
            n6 = bl ? n2 + n8 : n21 + direction.getStepZ();
            this.g.set(n7, 0, n6, Gothic.stairs(this.g.pal.stairs(), direction.getOpposite(), false));
        }
        int n22 = bl ? (bl2 ? n17 - 2 : n16 + 2) : (n8 = n);
        n7 = bl ? n2 : (bl2 ? n19 - 2 : n18 + 2);
        for (n6 = (bl ? n16 : n18) + 1; n6 <= (bl ? n17 : n19) - 1; ++n6) {
            n112 = bl ? n6 : n;
            n10 = bl ? n2 : n6;
            this.g.set(n112, 0, n10, Gothic.s(this.g.pal.soul() ? Blocks.BLACK_CARPET : Blocks.RED_CARPET));
            int n23 = n9 = Math.abs(bl ? n6 - n8 : n6 - n7) < 4 ? 1 : 0;
            if (n9 != 0 || n6 % 2 != 0) continue;
            for (int n24 : new int[]{-1, 1}) {
                for (int i = 2; i < n15; ++i) {
                    int n25;
                    int n26 = bl ? n6 : n + n24 * i;
                    int n27 = n25 = bl ? n2 + n24 * i : n6;
                    this.g.set(n26, 0, n25, Gothic.stairs(Blocks.DARK_OAK_STAIRS, bl ? (bl2 ? Direction.WEST : Direction.EAST) : (bl2 ? Direction.NORTH : Direction.SOUTH), false));
                }
            }
        }
        this.g.altar(n8, 0, n7);
        for (n6 = (bl ? n16 : n18) + 3; n6 <= (bl ? n17 : n19) - 3; n6 += 5) {
            n112 = bl ? n6 : n;
            n10 = bl ? n2 : n6;
            this.g.chainLantern(n112, n5 + 1, n10, 2 + this.r.nextInt(2));
        }
        this.g.chest(bl ? n8 : n + n15 - 1, 0, bl ? n2 + n15 - 1 : n7, false);
        if (bl3) {
            this.cryptBelow(n8, n7, bl);
        }
        this.g.cobwebs(n16 + 1, 0, n18 + 1, n17 - 1, n5 + 3, n19 - 1, 0.04f + this.g.decay * 0.1f);
        return n13;
    }

    private void cryptBelow(int n, int n2, boolean bl) {
        int n3;
        int n4;
        int n5;
        int n6;
        int n7 = -6;
        int n8 = n;
        int n9 = n2;
        for (n6 = n8 - 4; n6 <= n8 + 4; ++n6) {
            for (n5 = n9 - 3; n5 <= n9 + 3; ++n5) {
                n4 = Math.abs(n6 - n8) == 4 || Math.abs(n5 - n9) == 3 ? 1 : 0;
                this.g.set(n6, n7 - 1, n5, Gothic.s(this.g.pal.floor()));
                for (n3 = n7; n3 <= n7 + 3; ++n3) {
                    this.g.set(n6, n3, n5, n4 != 0 ? this.g.brick() : Gothic.s(Blocks.AIR));
                }
                this.g.set(n6, n7 + 4, n5, Gothic.s(this.g.pal.polished()));
            }
        }
        for (n6 = 0; n6 <= 4; ++n6) {
            n5 = bl ? n8 + 3 - n6 : n8 + 2;
            n4 = bl ? n9 + 2 : n9 + 3 - n6;
            for (n3 = -n6; n3 <= -n6 + 3; ++n3) {
                this.g.air(n5, n3, n4);
            }
            this.g.set(n5, -n6 - 1, n4, Gothic.stairs(this.g.pal.stairs(), bl ? Direction.EAST : Direction.SOUTH, false));
        }
        this.g.sarcophagus(n8 - 2, n7, n9 - 2, true);
        this.g.sarcophagus(n8 - 2, n7, n9 + 2, true);
        this.g.chest(n8 - 3, n7, n9, true);
        this.g.set(n8 + 3, n7, n9 - 2, Gothic.s(Blocks.SOUL_CAMPFIRE));
        this.g.chainLantern(n8, n7 + 3, n9, 1);
        this.g.cobwebs(n8 - 3, n7, n9 - 2, n8 + 3, n7 + 3, n9 + 2, 0.15f);
    }

    private void mausoleum(int n, int n2, boolean bl, boolean bl2) {
        int n3;
        int n4;
        int n5;
        int n6 = 7 + 2 * this.r.nextInt(2);
        int n7 = 5;
        int n8 = 4 + this.r.nextInt(2);
        int n9 = n6 / 2;
        int n10 = n7 / 2;
        int n11 = bl ? n - n9 : n - n10;
        int n12 = bl ? n + n9 : n + n10;
        int n13 = bl ? n2 - n10 : n2 - n9;
        int n14 = bl ? n2 + n10 : n2 + n9;
        this.g.hall(n11, n13, n12, n14, 0, n8, 3);
        this.g.gable(n11, n13, n12, n14, n8 + 1, bl);
        int n15 = bl ? (bl2 ? n11 : n12) : (n5 = n);
        int n16 = bl ? n2 : (bl2 ? n13 : n14);
        this.g.doorway(n5, 0, n16, bl ? Direction.Axis.Z : Direction.Axis.X, 3);
        if (bl) {
            this.g.window(n, 1, n13, Direction.Axis.X, 2, false);
            this.g.window(n, 1, n14, Direction.Axis.X, 2, false);
        } else {
            this.g.window(n11, 1, n2, Direction.Axis.Z, 2, false);
            this.g.window(n12, 1, n2, Direction.Axis.Z, 2, false);
        }
        int n17 = bl ? (bl2 ? n12 - 2 : n11 + 2) : (n4 = n - 1);
        int n18 = bl ? n2 - 1 : (n3 = bl2 ? n14 - 2 : n13 + 2);
        if (this.r.nextBoolean()) {
            this.g.sarcophagus(n4, 0, n3, !bl);
        } else {
            this.g.set(n4, 0, n3, Gothic.s(Blocks.BARREL));
            this.g.set(n4, 1, n3, Gothic.candle(2));
        }
        this.g.chainLantern(n, n8 - 1, n2, 1);
        if (this.r.nextFloat() < 0.6f) {
            this.g.chest(bl ? n : n + n10 - 1, 0, bl ? n2 + n10 - 1 : n2, false);
        }
        this.g.cobwebs(n11 + 1, 0, n13 + 1, n12 - 1, n8, n14 - 1, 0.08f);
    }

    private void ringWall(int n, int n2, int n3, boolean bl) {
        int n4;
        int n5;
        for (n5 = -n - 1; n5 <= n + 1; ++n5) {
            for (int i = -n - 1; i <= n + 1; ++i) {
                double d = Math.sqrt(n5 * n5 + i * i);
                if (d < (double)n - 0.5 || d > (double)n + 0.5 || !this.onDisc(n5, i)) continue;
                n4 = n5 > 0 && Math.abs(i) <= 1 ? 1 : 0;
                for (int j = 0; j < n2; ++j) {
                    this.g.set(n5, j, i, n4 != 0 && j < 4 ? Gothic.s(Blocks.AIR) : this.g.brick());
                }
                this.g.set(n5, n2, i, Gothic.s(this.g.pal.polished()));
                if ((n5 + i & 1) == 0) {
                    this.g.set(n5, n2 + 1, i, this.g.brick());
                }
                if (n4 == 0) continue;
                this.g.set(n5, 4, i, Gothic.s(this.g.pal.chiseled()));
            }
        }
        for (n5 = 0; n5 < n3; ++n5) {
            double d = Math.PI * 2 * ((double)n5 + 0.5) / (double)n3;
            int n6 = (int)Math.round(Math.cos(d) * (double)n);
            n4 = (int)Math.round(Math.sin(d) * (double)n);
            this.g.tower(n6, n4, 0, 2, n2 + 4 + this.r.nextInt(3), bl && this.r.nextFloat() < 0.6f);
        }
        for (n5 = -1; n5 <= 1; ++n5) {
            this.g.set(n, 3, n5, Gothic.s(Blocks.IRON_BARS));
        }
    }

    private void street(int n, int n2, int n3, int n4, int n5) {
        for (int i = Math.min(n, n3) - (n == n3 ? n5 : 0); i <= Math.max(n, n3) + (n == n3 ? n5 : 0); ++i) {
            for (int j = Math.min(n2, n4) - (n2 == n4 ? n5 : 0); j <= Math.max(n2, n4) + (n2 == n4 ? n5 : 0); ++j) {
                if (!this.onDisc(i, j)) continue;
                this.g.set(i, -1, j, this.r.nextFloat() < 0.8f ? Gothic.s(Blocks.POLISHED_BLACKSTONE_BRICKS) : Gothic.s(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS));
            }
        }
    }

    private void shrine() {
        int n;
        int n2;
        int n3;
        for (n3 = 0; n3 < 3; ++n3) {
            int n4 = 3 - n3;
            for (n2 = -n4; n2 <= n4; ++n2) {
                for (int i = -n4; i <= n4; ++i) {
                    boolean bl;
                    boolean bl2 = bl = Math.abs(n2) == n4 || Math.abs(i) == n4;
                    if (bl) {
                        Direction direction = Math.abs(n2) == n4 ? (n2 > 0 ? Direction.WEST : Direction.EAST) : (i > 0 ? Direction.NORTH : Direction.SOUTH);
                        this.g.set(n2, n3, i, Math.abs(n2) == n4 && Math.abs(i) == n4 ? Gothic.s(this.g.pal.chiseled()) : Gothic.stairs(this.g.pal.stairs(), direction, false));
                        continue;
                    }
                    this.g.set(n2, n3, i, Gothic.s(this.g.pal.polished()));
                }
            }
        }
        this.g.altar(0, 3, 0);
        n3 = 6;
        int[][] nArrayArray = new int[n3][];
        for (n2 = 0; n2 < n3; ++n2) {
            double d = Math.PI * 2 * (double)n2 / (double)n3;
            int n5 = (int)Math.round(Math.cos(d) * 6.5);
            int n6 = (int)Math.round(Math.sin(d) * 6.5);
            int n7 = 5 + this.r.nextInt(3);
            for (n = 0; n < n7; ++n) {
                this.g.set(n5, n, n6, n == 0 ? Gothic.s(this.g.pal.chiseled()) : (n == n7 - 1 ? Gothic.s(this.g.pal.trim()) : this.g.brick()));
            }
            this.g.set(n5, n7, n6, Gothic.s(this.g.pal.wall()));
            this.g.set(n5, n7 + 1, n6, this.r.nextBoolean() ? Gothic.s(Blocks.WITHER_SKELETON_SKULL) : this.g.lantern(false));
            nArrayArray[n2] = new int[]{n5, n6, n7};
        }
        for (n2 = 0; n2 < n3; ++n2) {
            int[] nArray = nArrayArray[n2];
            int[] nArray2 = nArrayArray[(n2 + 1) % n3];
            for (int i = 1; i < 6; ++i) {
                double d = (double)i / 6.0;
                n = (int)Math.round((double)nArray[0] + (double)(nArray2[0] - nArray[0]) * d);
                int n8 = (int)Math.round((double)nArray[1] + (double)(nArray2[1] - nArray[1]) * d);
                int n9 = (int)Math.round((double)(Math.min(nArray[2], nArray2[2]) - 1) - Math.sin(d * Math.PI) * 2.0);
                this.g.set(n, n9, n8, Gothic.s(Blocks.IRON_BARS));
            }
        }
        for (int[] nArray : new int[][]{{4, 0}, {-4, 0}, {0, 4}, {0, -4}}) {
            this.g.set(nArray[0], -1, nArray[1], Gothic.s(Blocks.SOUL_SOIL));
            this.g.set(nArray[0], 0, nArray[1], Gothic.s(Blocks.SOUL_FIRE));
        }
        this.cryptBelow(0, 0, true);
        this.g.deadTree(-7, 0, 5, 5 + this.r.nextInt(3));
        this.g.deadTree(6, 0, -6, 4 + this.r.nextInt(3));
        for (int i = 0; i < 4; ++i) {
            int n10;
            int n11 = -8 + this.r.nextInt(16);
            if (!this.onDisc(n11, n10 = -8 + this.r.nextInt(16)) || Math.abs(n11) <= 4 || Math.abs(n10) <= 4) continue;
            this.g.grave(n11, 0, n10, Direction.SOUTH);
        }
        this.g.chest(2, 0, 4, false);
    }

    private void temple() {
        int n = this.chapel(1, -1, 17, 9, 8, true, true, true);
        this.g.tower(11, -1, 0, 2, 13, true);
        for (int n2 : new int[]{-1, 1}) {
            int n3 = -1 + n2 * 5;
            for (int i = 0; i < 12; ++i) {
                for (int j = -9; j <= -7; ++j) {
                    for (int k = n3 - 1; k <= n3 + 1; ++k) {
                        this.g.set(j, i, k, j == -8 && k == n3 && i > 0 ? Gothic.s(Blocks.AIR) : this.g.brick());
                    }
                }
            }
            this.g.spire(-8, n3, 12, 1, 7);
            this.g.gargoyle(-10, 10, n3, Direction.WEST);
        }
        this.graveyard(-8, 6, 8, 12);
        this.street(-14, -1, -10, -1, 1);
        for (int i = 0; i < 3; ++i) {
            this.g.deadTree(-12 + this.r.nextInt(6), 0, -8 - this.r.nextInt(4), 5 + this.r.nextInt(4));
        }
        this.lampPost(-11, 2);
        this.lampPost(-11, -4);
    }

    private void necropolis() {
        int n;
        int[][] nArrayArray;
        this.ringWall(this.R - 2, 5, 4, true);
        this.street(-this.R + 3, 0, this.R - 2, 0, 1);
        this.street(0, -this.R + 3, 0, this.R - 3, 1);
        this.chapel(-1, 0, 15, 9, 7, true, false, true);
        this.g.tower(-10, 0, 0, 2, 12, true);
        for (int[] nArray : nArrayArray = new int[][]{{-8, -11}, {6, -11}, {-8, 11}, {6, 11}, {12, -6}, {12, 6}}) {
            if (!this.onDisc(nArray[0] + 3, nArray[1] + 3) || !this.onDisc(nArray[0] - 3, nArray[1] - 3)) continue;
            if (this.r.nextFloat() < 0.75f) {
                this.mausoleum(nArray[0], nArray[1], this.r.nextBoolean(), nArray[1] > 0);
                continue;
            }
            this.graveyard(nArray[0] - 3, nArray[1] - 2, nArray[0] + 3, nArray[1] + 2);
        }
        for (n = 0; n < 6; ++n) {
            this.lampPost(-12 + n * 5, n % 2 == 0 ? 2 : -2);
        }
        for (n = 0; n < 5; ++n) {
            int n2;
            int n3 = -this.R + 4 + this.r.nextInt(2 * this.R - 8);
            if (!this.onDisc(n3, n2 = -this.R + 4 + this.r.nextInt(2 * this.R - 8)) || !this.g.isAir(n3, 0, n2) || !this.g.isAir(n3, 3, n2)) continue;
            this.g.deadTree(n3, 0, n2, 4 + this.r.nextInt(4));
        }
    }

    private void citadel() {
        int n;
        double d;
        int n2;
        int n3;
        int n4 = 13;
        for (n3 = -n4; n3 <= n4; ++n3) {
            for (n2 = -n4; n2 <= n4; ++n2) {
                d = Math.sqrt(n3 * n3 + n2 * n2);
                if (d > (double)n4) continue;
                for (n = -1; n <= 2; ++n) {
                    this.g.set(n3, n, n2, n == 2 ? this.g.floorTile(n3, n2) : Gothic.s(this.g.pal.brick()));
                }
            }
        }
        for (n3 = 0; n3 < 3; ++n3) {
            for (n2 = -2; n2 <= 2; ++n2) {
                this.g.set(n4 + 1 + n3, 2 - n3, n2, Gothic.stairs(this.g.pal.stairs(), Direction.WEST, false));
            }
        }
        this.ringWall(this.R - 2, 6, 6, true);
        for (n3 = -n4; n3 <= n4; ++n3) {
            for (n2 = -n4; n2 <= n4; ++n2) {
                d = Math.sqrt(n3 * n3 + n2 * n2);
                if (d < (double)n4 - 0.5 || d > (double)n4 + 0.5 || n3 > 0 && Math.abs(n2) <= 2) continue;
                for (n = 3; n < 7; ++n) {
                    this.g.set(n3, n, n2, this.g.brick());
                }
                this.g.set(n3, 7, n2, Gothic.s(this.g.pal.polished()));
                if ((n3 + n2 & 1) != 0) continue;
                this.g.set(n3, 8, n2, this.g.brick());
            }
        }
        this.shiftY(3, () -> {
            this.chapel(-1, 0, 19, 9, 11, true, false, false);
            this.chapel(-1, 0, 15, 7, 9, false, true, false);
            this.g.tower(-1, 0, 12, 3, 8, true);
            this.g.tower(-11, -6, 0, 2, 16, true);
        });
        n3 = 7;
        for (n2 = 0; n2 < n3; ++n2) {
            d = Math.PI * 2 * ((double)n2 + 0.3) / (double)n3;
            n = (int)Math.round(Math.cos(d) * 18.5);
            int n5 = (int)Math.round(Math.sin(d) * 18.5);
            if (n > 14 && Math.abs(n5) < 5) continue;
            if (this.r.nextFloat() < 0.7f) {
                this.mausoleum(n, n5, Math.abs(Math.sin(d)) > 0.7, this.r.nextBoolean());
                continue;
            }
            this.graveyard(n - 2, n5 - 2, n + 2, n5 + 2);
        }
        for (n2 = 0; n2 < 8; ++n2) {
            d = Math.PI * 2 * (double)n2 / 8.0;
            this.lampPost((int)Math.round(Math.cos(d) * 15.5), (int)Math.round(Math.sin(d) * 15.5));
        }
        this.cryptBelow(-6, 0, true);
        this.g.chest(-5, -6, 1, true);
    }

    private void shiftY(int n, Runnable runnable) {
        HashMap<Long, BlockState> hashMap = new HashMap<Long, BlockState>(this.plan.blocks);
        int n2 = this.plan.chests.size();
        this.plan.blocks.clear();
        runnable.run();
        HashMap<Long, BlockState> hashMap2 = new HashMap<Long, BlockState>(this.plan.blocks);
        this.plan.blocks.clear();
        this.plan.blocks.putAll(hashMap);
        for (Map.Entry entry : hashMap2.entrySet()) {
            long l = (Long)entry.getKey();
            this.plan.blocks.put(UndergroundCity.Plan.key(UndergroundCity.Plan.kx(l), UndergroundCity.Plan.ky(l) + n, UndergroundCity.Plan.kz(l)), (BlockState)entry.getValue());
        }
        for (int i = n2; i < this.plan.chests.size(); ++i) {
            int[] nArray = this.plan.chests.get(i);
            nArray[1] = nArray[1] + n;
        }
    }

    private void bed(int n, int n2, int n3, Direction direction, Block block) {
        this.g.set(n, n2, n3, (BlockState)((BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.BED_PART, (Comparable)BedPart.FOOT));
        this.g.set(n + direction.getStepX(), n2, n3 + direction.getStepZ(), (BlockState)((BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.BED_PART, (Comparable)BedPart.HEAD));
    }

    private void cage(int n, int n2, int n3, int n4) {
        int n5;
        for (n5 = 0; n5 < n4; ++n5) {
            this.g.set(n, n2 - n5, n3, Gothic.s(Blocks.CHAIN));
        }
        n5 = n2 - n4;
        this.g.set(n, n5, n3, Gothic.s(Blocks.IRON_BARS));
        this.g.set(n, n5 - 1, n3, Gothic.s(Blocks.COBWEB));
        this.g.set(n, n5 - 2, n3, Gothic.s(Blocks.SKELETON_SKULL));
        for (Direction direction : H4) {
            this.g.set(n + direction.getStepX(), n5 - 1, n3 + direction.getStepZ(), Gothic.s(Blocks.IRON_BARS));
            this.g.set(n + direction.getStepX(), n5 - 2, n3 + direction.getStepZ(), Gothic.s(Blocks.IRON_BARS));
        }
        this.g.set(n, n5 - 3, n3, Gothic.s(Blocks.IRON_BARS));
    }

    private void house(int n, int n2, int n3, int n4, boolean bl, Direction direction) {
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10;
        int n11 = n3 / 2;
        int n12 = n4 / 2;
        int n13 = 4;
        int n14 = bl ? n - n11 : n - n12;
        int n15 = bl ? n + n11 : n + n12;
        int n16 = bl ? n2 - n12 : n2 - n11;
        int n17 = bl ? n2 + n12 : n2 + n11;
        for (n10 = n14; n10 <= n15; ++n10) {
            for (n9 = n16; n9 <= n17; ++n9) {
                n8 = n10 == n14 || n10 == n15 ? 1 : 0;
                n7 = n9 == n16 || n9 == n17 ? 1 : 0;
                this.g.set(n10, -1, n9, Gothic.s(Blocks.COBBLED_DEEPSLATE));
                for (n6 = 0; n6 < n13; ++n6) {
                    if (n8 == 0 && n7 == 0) {
                        this.g.air(n10, n6, n9);
                        continue;
                    }
                    int n18 = n8 != 0 && n7 != 0 || (n8 != 0 ? (n9 - n16) % 3 == 0 : (n10 - n14) % 3 == 0) ? 1 : (n5 = 0);
                    BlockState blockState = n6 == 0 ? Gothic.s(Blocks.COBBLED_DEEPSLATE) : (n5 != 0 || n6 == n13 - 1 ? Gothic.s(Blocks.STRIPPED_DARK_OAK_LOG) : this.w.brick());
                    this.g.set(n10, n6, n9, blockState);
                }
            }
        }
        for (n10 = (bl ? n14 : n16) + 2; n10 < (bl ? n15 : n17); n10 += 3) {
            if (bl) {
                this.g.set(n10, 2, n16, Gothic.s(Blocks.BROWN_STAINED_GLASS_PANE));
                this.g.set(n10, 2, n17, Gothic.s(Blocks.BROWN_STAINED_GLASS_PANE));
                continue;
            }
            this.g.set(n14, 2, n10, Gothic.s(Blocks.BROWN_STAINED_GLASS_PANE));
            this.g.set(n15, 2, n10, Gothic.s(Blocks.BROWN_STAINED_GLASS_PANE));
        }
        int n19 = direction == Direction.EAST ? n15 : (n10 = direction == Direction.WEST ? n14 : n);
        n9 = direction == Direction.SOUTH ? n17 : (direction == Direction.NORTH ? n16 : n2);
        this.g.air(n10, 1, n9);
        this.g.air(n10, 2, n9);
        this.g.set(n10, 0, n9, Gothic.s(Blocks.DARK_OAK_PLANKS));
        this.g.set(n10 + direction.getStepX(), 0, n9 + direction.getStepZ(), Gothic.stairs(Blocks.COBBLED_DEEPSLATE_STAIRS, direction.getOpposite(), false));
        this.g.set(n10 + direction.getStepX(), 3, n9 + direction.getStepZ(), this.g.lantern(true));
        this.w.gable(n14, n16, n15, n17, n13, bl);
        n8 = bl ? n15 - 1 : n14 + 1;
        n7 = bl ? n16 + 1 : n17 - 1;
        for (n6 = 0; n6 < n13 + n12 + 2; ++n6) {
            this.g.set(n8, n6, n7, Gothic.s(Blocks.COBBLED_DEEPSLATE));
        }
        this.g.set(n8, n13 + n12 + 2, n7, Gothic.s(Blocks.CAMPFIRE));
        n6 = bl ? n14 + 1 : n14 + 1;
        n5 = bl ? n16 + 1 : n16 + 1;
        this.g.set(n8, 1, n7, Gothic.s(Blocks.FURNACE));
        this.bed(bl ? n15 - 1 : n14 + 1, 0, bl ? n17 - 1 : n17 - 2, bl ? Direction.WEST : Direction.SOUTH, Blocks.BLACK_BED);
        this.g.set(n6, 0, n5, Gothic.s(Blocks.BARREL));
        this.g.set(n, 0, n2, Gothic.s(Blocks.DARK_OAK_FENCE));
        this.g.set(n, 1, n2, Gothic.s(Blocks.DARK_OAK_PRESSURE_PLATE));
        this.g.set(n + (bl ? 1 : 0), 0, n2 + (bl ? 0 : 1), Gothic.stairs(Blocks.SPRUCE_STAIRS, bl ? Direction.WEST : Direction.NORTH, false));
        this.g.chainLantern(n, n13 - 1, n2, 0);
        if (this.r.nextFloat() < 0.6f) {
            this.g.chest(bl ? n14 + 1 : n15 - 1, 0, bl ? n17 - 1 : n16 + 1, false);
        }
        this.g.cobwebs(n14 + 1, 0, n16 + 1, n15 - 1, n13, n17 - 1, 0.1f + this.g.decay * 0.2f);
    }

    private void scarecrow(int n, int n2) {
        this.g.set(n, 0, n2, Gothic.s(Blocks.DARK_OAK_FENCE));
        this.g.set(n, 1, n2, Gothic.s(Blocks.DARK_OAK_FENCE));
        this.g.set(n, 2, n2, Gothic.s(Blocks.HAY_BLOCK));
        this.g.set(n, 3, n2, (BlockState)Blocks.CARVED_PUMPKIN.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)Direction.EAST));
        this.g.set(n, 2, n2 - 1, Gothic.s(Blocks.DARK_OAK_FENCE));
        this.g.set(n, 2, n2 + 1, Gothic.s(Blocks.DARK_OAK_FENCE));
    }

    private void deadField(int n, int n2, int n3, int n4) {
        for (int i = n; i <= n3; ++i) {
            for (int j = n2; j <= n4; ++j) {
                boolean bl;
                if (!this.onDisc(i, j)) continue;
                boolean bl2 = bl = (j - n2) % 3 == 2;
                this.g.set(i, -1, j, bl ? Gothic.s(Blocks.WATER) : Gothic.s(this.r.nextFloat() < 0.7f ? Blocks.COARSE_DIRT : Blocks.PODZOL));
                if (bl || !(this.r.nextFloat() < 0.35f)) continue;
                this.g.set(i, 0, j, Gothic.s(this.r.nextFloat() < 0.75f ? Blocks.DEAD_BUSH : Blocks.WITHER_ROSE));
            }
        }
        this.scarecrow((n + n3) / 2, (n2 + n4) / 2 + ((n4 - n2) % 3 == 2 ? 1 : 0));
    }

    private void well(int n, int n2) {
        int n3;
        int n4;
        for (int i = n - 1; i <= n + 1; ++i) {
            for (n4 = n2 - 1; n4 <= n2 + 1; ++n4) {
                n3 = i == n && n4 == n2 ? 1 : 0;
                for (int j = -6; j <= -1; ++j) {
                    this.g.set(i, j, n4, n3 != 0 ? (j < -3 ? Gothic.s(Blocks.WATER) : Gothic.s(Blocks.AIR)) : this.g.brick());
                }
                this.g.set(i, 0, n4, n3 != 0 ? Gothic.s(Blocks.AIR) : Gothic.s(this.g.pal.slab()));
            }
        }
        int[][] nArrayArray = new int[][]{{n - 1, n2 - 1}, {n + 1, n2 + 1}};
        n4 = nArrayArray.length;
        for (n3 = 0; n3 < n4; ++n3) {
            int[] nArray = nArrayArray[n3];
            for (int i = 1; i <= 2; ++i) {
                this.g.set(nArray[0], i, nArray[1], Gothic.s(Blocks.DARK_OAK_FENCE));
            }
        }
        for (int i = n - 1; i <= n + 1; ++i) {
            for (n4 = n2 - 1; n4 <= n2 + 1; ++n4) {
                this.g.set(i, 3, n4, Gothic.slab(this.g.pal.roofSlab(), false));
            }
        }
        this.g.set(n, 2, n2, Gothic.s(Blocks.CHAIN));
        this.g.set(n, 1, n2, Gothic.s(Blocks.CHAIN));
    }

    private void arcade(int n, int n2, int n3, int n4, int n5, Direction direction) {
        boolean bl = n2 == n4;
        int n6 = bl ? Math.min(n, n3) : Math.min(n2, n4);
        int n7 = bl ? Math.max(n, n3) : Math.max(n2, n4);
        for (int i = n6; i <= n7; ++i) {
            int n8;
            int n9 = bl ? i : n;
            int n10 = bl ? n2 : i;
            boolean bl2 = (i - n6) % 3 == 0;
            for (n8 = 0; n8 < n5; ++n8) {
                if (bl2) {
                    this.g.set(n9, n8, n10, n8 == 0 || n8 == n5 - 1 ? Gothic.s(this.g.pal.chiseled()) : Gothic.s(this.g.pal.polished()));
                    continue;
                }
                if (n8 == n5 - 1) {
                    this.g.set(n9, n8, n10, Gothic.s(this.g.pal.brick()));
                    continue;
                }
                if (n8 != n5 - 2) continue;
                this.g.set(n9, n8, n10, Gothic.stairs(this.g.pal.stairs(), bl ? ((i - n6) % 3 == 1 ? Direction.EAST : Direction.WEST) : ((i - n6) % 3 == 1 ? Direction.SOUTH : Direction.NORTH), true));
            }
            for (n8 = 0; n8 <= 2; ++n8) {
                int n11 = n9 + direction.getStepX() * n8;
                int n12 = n10 + direction.getStepZ() * n8;
                this.g.set(n11, n5, n12, n8 == 0 ? Gothic.s(this.g.pal.polished()) : Gothic.slab(this.g.pal.roofSlab(), false));
                this.g.set(n11, -1, n12, this.g.floorTile(n11, n12));
            }
            if ((i - n6) % 6 != 3) continue;
            this.g.chainLantern(n9 + direction.getStepX(), n5 - 1, n10 + direction.getStepZ(), 0);
        }
    }

    private void flyingButtress(int n, int n2, Direction direction, int n3) {
        int n4;
        int n5 = n + direction.getStepX() * 4;
        int n6 = n2 + direction.getStepZ() * 4;
        int n7 = n3 - 3;
        for (n4 = 0; n4 < n7; ++n4) {
            this.g.set(n5, n4, n6, n4 % 5 == 0 ? Gothic.s(this.g.pal.chiseled()) : this.g.brick());
        }
        this.g.set(n5, n7, n6, Gothic.s(this.g.pal.wall()));
        this.g.set(n5, n7 + 1, n6, Gothic.s(this.g.pal.wall()));
        this.g.set(n5, n7 + 2, n6, Gothic.s(Blocks.IRON_BARS));
        for (n4 = 1; n4 <= 3; ++n4) {
            int n8 = n + direction.getStepX() * (4 - n4);
            int n9 = n2 + direction.getStepZ() * (4 - n4);
            int n10 = n7 - 2 + n4;
            this.g.set(n8, n10, n9, Gothic.stairs(this.g.pal.stairs(), direction.getOpposite(), false));
            this.g.set(n8, n10 - 1, n9, Gothic.stairs(this.g.pal.stairs(), direction, true));
        }
    }

    private void gallowsHill() {
        int n;
        int n2;
        int n3;
        for (n3 = -8; n3 <= 8; ++n3) {
            for (n2 = -8; n2 <= 8; ++n2) {
                double d = Math.sqrt(n3 * n3 + n2 * n2);
                if (d > 7.5 || !this.onDisc(n3, n2)) continue;
                int n4 = (int)Math.round(2.2 * (1.0 - d / 7.5));
                for (n = -1; n < n4; ++n) {
                    this.g.set(n3, n, n2, Gothic.s(Blocks.COARSE_DIRT));
                }
                this.g.set(n3, n4 - 1 < -1 ? -1 : n4 - 1, n2, Gothic.s(this.r.nextFloat() < 0.6f ? Blocks.PODZOL : Blocks.COARSE_DIRT));
            }
        }
        n3 = 2;
        for (n2 = -3; n2 <= 3; ++n2) {
            for (int i = -1; i <= 1; ++i) {
                this.g.set(n2, n3 + 1, i, n2 == 0 && i == 0 ? Gothic.s(Blocks.DARK_OAK_TRAPDOOR) : Gothic.s(Blocks.DARK_OAK_PLANKS));
                if (n2 != -3 && n2 != 3 || i == 0) continue;
                for (int j = 0; j <= n3; ++j) {
                    this.g.set(n2, j, i, Gothic.s(Blocks.DARK_OAK_LOG));
                }
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            this.g.set(4 + n2, n3 - n2 + 1, 0, Gothic.stairs(Blocks.DARK_OAK_STAIRS, Direction.WEST, false));
        }
        for (n2 = n3 + 2; n2 <= n3 + 7; ++n2) {
            this.g.set(-3, n2, 0, Gothic.s(Blocks.DARK_OAK_LOG));
            this.g.set(3, n2, 0, Gothic.s(Blocks.DARK_OAK_LOG));
        }
        for (n2 = -3; n2 <= 3; ++n2) {
            this.g.set(n2, n3 + 8, 0, Gothic.s(Blocks.STRIPPED_DARK_OAK_LOG));
        }
        this.g.set(-2, n3 + 7, 0, Gothic.stairs(Blocks.DARK_OAK_STAIRS, Direction.EAST, true));
        this.g.set(2, n3 + 7, 0, Gothic.stairs(Blocks.DARK_OAK_STAIRS, Direction.WEST, true));
        this.g.set(-1, n3 + 7, 0, Gothic.s(Blocks.CHAIN));
        this.g.set(-1, n3 + 6, 0, Gothic.s(Blocks.CHAIN));
        this.g.set(-1, n3 + 5, 0, Gothic.s(Blocks.SKELETON_SKULL));
        this.g.set(1, n3 + 7, 0, Gothic.s(Blocks.CHAIN));
        this.g.set(1, n3 + 6, 0, Gothic.s(Blocks.CHAIN));
        for (int[] nArray : new int[][]{{-6, -4}, {6, 4}, {-5, 5}}) {
            if (!this.onDisc(nArray[0], nArray[1])) continue;
            for (n = 0; n < 7; ++n) {
                this.g.set(nArray[0], n, nArray[1], Gothic.s(Blocks.DARK_OAK_FENCE));
            }
            this.g.set(nArray[0], 7, nArray[1], Gothic.s(Blocks.DARK_OAK_LOG));
            this.g.set(nArray[0] + 1, 7, nArray[1], Gothic.s(Blocks.DARK_OAK_FENCE));
            this.cage(nArray[0] + 1, 6, nArray[1], 1);
        }
        this.g.set(0, n3, 4, Gothic.s(Blocks.DARK_OAK_FENCE));
        this.g.set(-1, n3, 4, Gothic.s(Blocks.DARK_OAK_FENCE));
        this.g.set(1, n3, 4, Gothic.s(Blocks.DARK_OAK_FENCE));
        this.g.set(-1, n3 + 1, 4, Gothic.slab(Blocks.DARK_OAK_SLAB, false));
        this.g.set(0, n3 + 1, 4, Gothic.slab(Blocks.DARK_OAK_SLAB, false));
        this.g.set(1, n3 + 1, 4, Gothic.slab(Blocks.DARK_OAK_SLAB, false));
        this.g.set(-4, 1, -5, Gothic.s(this.g.pal.soul() ? Blocks.SOUL_CAMPFIRE : Blocks.CAMPFIRE));
        this.g.set(-6, 1, -5, Gothic.s(Blocks.STRIPPED_DARK_OAK_LOG));
        this.g.set(-2, 1, -5, Gothic.s(Blocks.STRIPPED_DARK_OAK_LOG));
        if (this.onDisc(7, -6) && this.onDisc(10, -6)) {
            this.house(8, -6, 5, 5, true, Direction.WEST);
        }
        for (int i = 0; i < 8; ++i) {
            int n5;
            double d = this.r.nextDouble() * Math.PI * 2.0;
            double d2 = 6.0 + this.r.nextDouble() * 2.5;
            int n6 = (int)Math.round(Math.cos(d) * d2);
            if (!this.onDisc(n6, n5 = (int)Math.round(Math.sin(d) * d2)) || !this.g.isAir(n6, 1, n5)) continue;
            this.g.grave(n6, 0, n5, Direction.SOUTH);
        }
        this.g.deadTree(-7, 0, 2, 6 + this.r.nextInt(3));
        this.g.deadTree(5, 0, -3, 5 + this.r.nextInt(3));
        this.lampPost(4, -2);
        this.g.chest(-2, n3 + 2, 1, false);
    }

    private void warlockTower() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        double d;
        int n7;
        int n8;
        int n9 = 5;
        int n10 = 6;
        int n11 = 4;
        int n12 = n10 * n11;
        for (n8 = -n9 - 1; n8 <= n9 + 1; ++n8) {
            for (n7 = -n9 - 1; n7 <= n9 + 1; ++n7) {
                d = Math.sqrt(n8 * n8 + n7 * n7);
                if (d > (double)n9 + 1.4 || !(d > (double)n9 + 0.4)) continue;
                this.g.set(n8, 0, n7, d > (double)n9 + 0.9 ? Gothic.stairs(this.g.pal.stairs(), AncientStructures.d(n8, n7).getOpposite(), false) : Gothic.s(this.g.pal.polished()));
            }
        }
        for (n8 = -n9; n8 <= n9; ++n8) {
            for (n7 = -n9; n7 <= n9; ++n7) {
                d = Math.sqrt(n8 * n8 + n7 * n7);
                if (d > (double)n9 + 0.4) continue;
                boolean bl = d > (double)n9 - 0.6;
                for (int i = 0; i < n12; ++i) {
                    if (bl) {
                        n6 = i % n10;
                        int n13 = n5 = n6 >= 2 && n6 <= 3 && (Math.abs(n8) <= 0 || Math.abs(n7) <= 0) && i > 0 ? 1 : 0;
                        this.g.set(n8, i, n7, n5 != 0 ? Gothic.s(this.g.pal.glass()) : (n6 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick()));
                        continue;
                    }
                    this.g.set(n8, i, n7, i % n10 == 0 && i > 0 ? Gothic.s(this.g.pal.floor()) : Gothic.s(Blocks.AIR));
                }
                if (bl) continue;
                this.g.set(n8, -1, n7, this.g.floorTile(n8, n7));
            }
        }
        for (n8 = 0; n8 < n11 - 1; ++n8) {
            double d2 = (double)n8 * 1.9;
            for (int i = 0; i < n10; ++i) {
                double d3 = d2 + (double)i * 0.42;
                n6 = (int)Math.round(Math.cos(d3) * ((double)n9 - 1.4));
                n5 = (int)Math.round(Math.sin(d3) * ((double)n9 - 1.4));
                int n14 = n8 * n10 + i;
                double d4 = d3 + 1.5707963267948966;
                Direction direction = AncientStructures.d((int)Math.round(Math.cos(d4) * 3.0), (int)Math.round(Math.sin(d4) * 3.0));
                this.g.set(n6, n14, n5, Gothic.stairs(this.g.pal.stairs(), direction, false));
                for (int j = n14 + 1; j <= n14 + 3; ++j) {
                    if (j % n10 == 0) {
                        this.g.air(n6, j, n5);
                        continue;
                    }
                    this.g.air(n6, j, n5);
                }
            }
        }
        this.g.chest(-2, 0, -3, false);
        this.g.set(2, 0, 2, Gothic.s(Blocks.CRAFTING_TABLE));
        this.g.set(0, 0, 0, Gothic.s(this.g.pal.soul() ? Blocks.BLACK_CARPET : Blocks.RED_CARPET));
        this.g.chainLantern(0, n10 - 1, 0, 1);
        n8 = n10;
        for (n4 = -n9 + 1; n4 <= n9 - 1; ++n4) {
            for (int i = -n9 + 1; i <= n9 - 1; ++i) {
                double d5 = Math.sqrt(n4 * n4 + i * i);
                if (!(d5 > (double)n9 - 1.6) || !(d5 <= (double)n9 - 0.6) || !this.g.isAir(n4, n8, i) || !this.g.isAir(n4, n8 + 1, i) || !(this.r.nextFloat() < 0.8f)) continue;
                this.g.set(n4, n8, i, Gothic.s(Blocks.BOOKSHELF));
                this.g.set(n4, n8 + 1, i, Gothic.s(this.r.nextFloat() < 0.3f ? Blocks.CHISELED_BOOKSHELF : Blocks.BOOKSHELF));
            }
        }
        this.g.set(0, n8, 0, Gothic.s(Blocks.LECTERN));
        this.g.set(1, n8, 1, Gothic.s(Blocks.DARK_OAK_FENCE));
        this.g.set(1, n8 + 1, 1, Gothic.candle(3));
        this.g.chainLantern(0, n8 + n10 - 1, 0, 1);
        n4 = n10 * 2;
        this.g.set(2, n4, 0, Gothic.s(Blocks.BREWING_STAND));
        this.g.set(-2, n4, 1, Gothic.s(Blocks.BREWING_STAND));
        this.g.set(0, n4, 2, Gothic.s(Blocks.CAULDRON));
        this.g.set(0, n4, -2, Gothic.s(Blocks.WATER_CAULDRON));
        this.g.set(-2, n4, -2, Gothic.s(Blocks.BARREL));
        this.g.set(-2, n4 + 1, -2, Gothic.s(Blocks.BARREL));
        this.g.set(2, n4, -2, Gothic.s(Blocks.SKELETON_SKULL));
        this.g.chest(-3, n4, 0, true);
        this.g.chainLantern(0, n4 + n10 - 1, 0, 1);
        int n15 = n10 * 3;
        this.g.set(0, n15 - 1, 0, Gothic.s(Blocks.CRYING_OBSIDIAN));
        this.g.set(0, n15, 0, Gothic.s(Blocks.WITHER_SKELETON_SKULL));
        for (n3 = 0; n3 < 8; ++n3) {
            double d6 = (double)n3 * Math.PI / 4.0;
            n6 = (int)Math.round(Math.cos(d6) * 2.5);
            n5 = (int)Math.round(Math.sin(d6) * 2.5);
            this.g.set(n6, n15, n5, Gothic.candle(1 + n3 % 4));
            this.g.set(n6, n15 - 1, n5, Gothic.s(this.g.pal.trim()));
        }
        for (n3 = -n9 - 2; n3 <= n9 + 2; ++n3) {
            for (int i = -n9 - 2; i <= n9 + 2; ++i) {
                double d7 = Math.sqrt(n3 * n3 + i * i);
                if (d7 <= (double)n9 + 0.4 || d7 > (double)n9 + 2.4) continue;
                this.g.set(n3, n15 - 1, i, Gothic.slab(this.g.pal.slab(), true));
                if (d7 > (double)n9 + 1.4) {
                    this.g.set(n3, n15, i, Gothic.s(Blocks.IRON_BARS));
                }
                if ((n3 * 3 + i & 7) != 0) continue;
                this.g.set(n3, n15 - 2, i, Gothic.stairs(this.g.pal.stairs(), AncientStructures.d(n3, i).getOpposite(), true));
            }
        }
        for (n3 = 0; n3 < 4; ++n3) {
            this.g.air(n3 == 0 ? n9 : (n3 == 1 ? -n9 : 0), n15 + 1, n3 == 2 ? n9 : (n3 == 3 ? -n9 : 0));
        }
        for (n3 = 0; n3 <= n9 + 1; ++n3) {
            double d8 = (double)n9 + 1.5 - (double)n3;
            for (n6 = -n9 - 2; n6 <= n9 + 2; ++n6) {
                for (n5 = -n9 - 2; n5 <= n9 + 2; ++n5) {
                    double d9 = Math.sqrt(n6 * n6 + n5 * n5);
                    if (d9 > d8 || d9 < d8 - 1.1) continue;
                    this.g.set(n6, n12 + n3, n5, n3 == 0 ? Gothic.slab(this.g.pal.roofSlab(), false) : Gothic.s(this.g.pal.roof()));
                }
            }
        }
        for (n3 = 0; n3 < 5; ++n3) {
            this.g.set(0, n12 + n9 + 2 + n3, 0, n3 < 2 ? Gothic.s(this.g.pal.wall()) : Gothic.s(Blocks.IRON_BARS));
        }
        this.g.set(0, n12 + n9 + 7, 0, Gothic.s(Blocks.LIGHTNING_ROD));
        Direction[] directionArray = H4;
        int n16 = directionArray.length;
        for (n2 = 0; n2 < n16; ++n2) {
            Direction direction = directionArray[n2];
            this.g.gargoyle(direction.getStepX() * (n9 + 1), n12 - 1, direction.getStepZ() * (n9 + 1), direction);
        }
        for (n = 0; n <= 2; ++n) {
            this.g.air(n9, n, 0);
        }
        this.g.set(n9, 3, 0, Gothic.s(this.g.pal.chiseled()));
        this.g.set(n9 + 1, 3, 0, this.g.lantern(true));
        for (n = -12; n <= -7; ++n) {
            for (n16 = 4; n16 <= 9; ++n16) {
                if (!this.onDisc(n, n16)) continue;
                this.g.set(n, -1, n16, Gothic.s(Blocks.SOUL_SOIL));
                if (!(this.r.nextFloat() < 0.45f)) continue;
                this.g.set(n, 0, n16, Gothic.s(Blocks.WITHER_ROSE));
            }
        }
        for (n = 0; n < 12; ++n) {
            double d10 = (double)n * Math.PI / 6.0;
            int n17 = 8 + (int)Math.round(Math.cos(d10) * 3.0);
            if (!this.onDisc(n17, n5 = -7 + (int)Math.round(Math.sin(d10) * 3.0))) continue;
            this.g.set(n17, -1, n5, Gothic.s(Blocks.SOUL_SOIL));
            this.g.set(n17, 0, n5, n % 3 == 0 ? Gothic.s(Blocks.SOUL_FIRE) : Gothic.s(Blocks.BLACK_CARPET));
        }
        if (this.onDisc(8, -7)) {
            this.g.set(8, 0, -7, Gothic.s(this.g.pal.chiseled()));
            this.g.set(8, 1, -7, Gothic.s(Blocks.SKELETON_SKULL));
        }
        if (this.onDisc(-9, -8) && this.onDisc(-6, -5)) {
            this.house(-8, -7, 5, 5, false, Direction.SOUTH);
        }
        this.g.deadTree(8, 0, 7, 6);
        this.lampPost(n9 + 4, 3);
        this.lampPost(n9 + 4, -3);
        for (n = 0; n < 4; ++n) {
            int n18 = -6 + this.r.nextInt(13);
            if (!this.onDisc(n18, n2 = 7 + this.r.nextInt(5)) || n18 <= -6) continue;
            this.g.grave(n18, 0, n2, Direction.NORTH);
        }
    }

    private static Direction d(int n, int n2) {
        if (Math.abs(n) >= Math.abs(n2)) {
            return n >= 0 ? Direction.EAST : Direction.WEST;
        }
        return n2 >= 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private void monastery() {
        int n;
        int[][] nArrayArray;
        int n2 = 9;
        this.chapel(0, -n2 - 5, 17, 9, 8, true, false, true);
        int[][] nArrayArray2 = nArrayArray = new int[][]{{n2 + 3, 0, 0}, {-n2 - 3, 0, 0}, {0, n2 + 3, 1}};
        int n3 = nArrayArray2.length;
        for (int i = 0; i < n3; ++i) {
            int n4;
            int n5;
            int[] nArray = nArrayArray2[i];
            boolean bl = nArray[2] == 1;
            int n6 = bl ? 2 * n2 + 5 : 2 * n2 - 1;
            int n7 = 5;
            int n8 = n6 / 2;
            int n9 = n7 / 2;
            int n10 = bl ? nArray[0] - n8 : nArray[0] - n9;
            int n11 = bl ? nArray[0] + n8 : nArray[0] + n9;
            int n12 = bl ? nArray[1] - n9 : nArray[1] - n8;
            int n13 = bl ? nArray[1] + n9 : nArray[1] + n8;
            this.g.hall(n10, n12, n11, n13, 0, 5, 3);
            this.g.gable(n10, n12, n11, n13, 6, bl);
            for (n5 = (bl ? n10 : n12) + 1; n5 < (bl ? n11 : n13) - 1; n5 += 3) {
                int n14;
                n4 = bl ? n5 : (nArray[0] > 0 ? n11 - 1 : n10 + 1);
                int n15 = n14 = bl ? n13 - 1 : n5;
                this.bed(n4, 0, n14, bl ? Direction.NORTH : (nArray[0] > 0 ? Direction.WEST : Direction.EAST), Blocks.BLACK_BED);
                int n16 = bl ? n5 + 1 : n4;
                int n17 = bl ? n14 : n5 + 1;
                this.g.set(n16, 0, n17, Gothic.s(Blocks.SPRUCE_FENCE));
                this.g.set(n16, 1, n17, Gothic.candle(1));
                if (bl) {
                    this.g.window(n5, 2, n13, Direction.Axis.X, 2, false);
                    continue;
                }
                this.g.window(nArray[0] > 0 ? n11 : n10, 2, n5, Direction.Axis.Z, 2, false);
            }
            n5 = bl ? nArray[0] : (nArray[0] > 0 ? n10 : n11);
            n4 = bl ? n12 : nArray[1];
            this.g.doorway(n5, 0, n4, bl ? Direction.Axis.X : Direction.Axis.Z, 3);
            this.g.cobwebs(n10 + 1, 0, n12 + 1, n11 - 1, 5, n13 - 1, 0.07f);
            this.g.chest(bl ? n10 + 1 : nArray[0], 0, bl ? nArray[1] : n12 + 1, false);
        }
        for (n = -n2 + 1; n <= n2 - 1; ++n) {
            this.g.set(n, 0, n2 + 3, Gothic.s(Blocks.DARK_OAK_PLANKS));
            if (n % 4 != 0) continue;
            this.g.set(n, 1, n2 + 3, Gothic.candle(2));
        }
        this.arcade(-n2, -n2, n2, -n2, 5, Direction.SOUTH);
        this.arcade(-n2, n2, n2, n2, 5, Direction.NORTH);
        this.arcade(-n2, -n2, -n2, n2, 5, Direction.EAST);
        this.arcade(n2, -n2, n2, n2, 5, Direction.WEST);
        for (n = -n2 + 3; n <= n2 - 3; ++n) {
            for (n3 = -n2 + 3; n3 <= n2 - 3; ++n3) {
                this.g.set(n, -1, n3, this.r.nextFloat() < 0.5f ? Gothic.s(Blocks.PODZOL) : Gothic.s(Blocks.COARSE_DIRT));
            }
        }
        this.well(0, 0);
        this.g.deadTree(-4, 0, 3, 7);
        for (n = 2; n <= 5; n += 2) {
            for (n3 = -5; n3 <= -2; n3 += 3) {
                this.g.grave(n, 0, n3, Direction.SOUTH);
            }
        }
        this.g.tower(n2 + 3, -n2 - 5, 0, 2, 15, true);
        for (n = 9; n < 13; ++n) {
            this.g.air(n2 + 3, n, -n2 - 5);
        }
        this.g.set(n2 + 3, 12, -n2 - 5, Gothic.s(Blocks.BELL));
        this.lampPost(n2 + 6, n2 + 7);
        this.lampPost(-n2 - 6, n2 + 7);
    }

    private void vampireCastle() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10 = 14;
        int n11 = 8;
        for (int i = -n10; i <= n10; ++i) {
            for (n9 = -n10; n9 <= n10; ++n9) {
                int n12;
                int n13 = n8 = Math.abs(i) == n10 || Math.abs(n9) == n10 ? 1 : 0;
                if (n8 == 0) continue;
                n7 = i == n10 && Math.abs(n9) <= 1 ? 1 : 0;
                for (n12 = 0; n12 < n11; ++n12) {
                    this.g.set(i, n12, n9, n7 != 0 && n12 < 5 ? Gothic.s(Blocks.AIR) : (n12 % 4 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick()));
                }
                this.g.set(i, n11, n9, Gothic.s(this.g.pal.polished()));
                if ((i + n9 & 1) == 0) {
                    this.g.set(i, n11 + 1, n9, this.g.brick());
                }
                n12 = i - Integer.signum(i) * (Math.abs(i) == n10 ? 1 : 0);
                n6 = n9 - Integer.signum(n9) * (Math.abs(n9) == n10 ? 1 : 0);
                if (n7 != 0) continue;
                this.g.set(n12, n11 - 1, n6, Gothic.slab(this.g.pal.slab(), true));
            }
        }
        int[] nArray = new int[]{-1, 1};
        n9 = nArray.length;
        for (n8 = 0; n8 < n9; ++n8) {
            n7 = nArray[n8];
            int[] nArray2 = new int[]{-1, 1};
            n6 = nArray2.length;
            for (n5 = 0; n5 < n6; ++n5) {
                n4 = nArray2[n5];
                this.g.tower(n7 * n10, n4 * n10, 0, 3, n11 + 7, true);
            }
        }
        this.g.tower(n10, -4, 0, 2, n11 + 5, false);
        this.g.tower(n10, 4, 0, 2, n11 + 5, false);
        for (n3 = -1; n3 <= 1; ++n3) {
            this.g.set(n10, 4, n3, Gothic.s(Blocks.IRON_BARS));
            this.g.set(n10, 3, n3, Gothic.s(Blocks.IRON_BARS));
            this.g.set(n10, 5, n3, Gothic.s(this.g.pal.chiseled()));
        }
        for (n3 = -2; n3 <= 2; n3 += 4) {
            this.g.gargoyle(n10 + 1, n11 - 1, n3, Direction.EAST);
        }
        for (n3 = 1; n3 <= 3; ++n3) {
            for (n9 = -1; n9 <= 1; ++n9) {
                this.g.set(n10 + n3, 0, n9, Gothic.s(Blocks.DARK_OAK_PLANKS));
            }
        }
        n3 = -11;
        n9 = 1;
        n8 = -5;
        n7 = 5;
        int n14 = 6;
        for (n6 = n3; n6 <= n9; ++n6) {
            for (n5 = n8; n5 <= n7; ++n5) {
                n4 = n6 == n3 || n6 == n9 || n5 == n8 || n5 == n7 ? 1 : 0;
                n2 = !(n6 != n3 && n6 != n9 || n5 != n8 && n5 != n7) ? 1 : 0;
                for (n = 0; n < n14 * 3; ++n) {
                    if (n4 != 0) {
                        this.g.set(n6, n, n5, n2 != 0 || n % n14 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick());
                        continue;
                    }
                    this.g.set(n6, n, n5, n % n14 == 0 && n > 0 ? Gothic.s(Blocks.DARK_OAK_PLANKS) : Gothic.s(Blocks.AIR));
                }
                if (n4 != 0) continue;
                this.g.set(n6, -1, n5, this.g.floorTile(n6, n5));
            }
        }
        for (n6 = 0; n6 < 3; ++n6) {
            for (n5 = n3 + 2; n5 < n9; n5 += 3) {
                this.g.window(n5, n6 * n14 + 2, n8, Direction.Axis.X, 3, false);
                this.g.window(n5, n6 * n14 + 2, n7, Direction.Axis.X, 3, false);
            }
        }
        this.g.gable(n3, n8, n9, n7, n14 * 3, true);
        this.g.tower(n3, n8, 0, 2, n14 * 3 + 6, true);
        this.g.tower(n3, n7, 0, 2, n14 * 3 + 6, true);
        this.g.doorway(n9, 0, 0, Direction.Axis.Z, 4);
        for (n6 = -1; n6 <= 1; ++n6) {
            this.g.set(n9 + 1, 0, n6, Gothic.stairs(this.g.pal.stairs(), Direction.WEST, false));
        }
        for (n6 = 0; n6 < 2; ++n6) {
            for (n5 = 0; n5 < n14; ++n5) {
                n4 = n9 - 2 - n5;
                n2 = n6 * n14 + n5;
                this.g.set(n4, n2, n8 + 1, Gothic.stairs(Blocks.DARK_OAK_STAIRS, Direction.WEST, false));
                for (n = n2 + 1; n <= n2 + 3; ++n) {
                    this.g.air(n4, n, n8 + 1);
                }
            }
        }
        for (n6 = n3 + 2; n6 <= n9 - 1; ++n6) {
            this.g.set(n6, 0, 0, Gothic.s(Blocks.RED_CARPET));
        }
        this.g.set(n3 + 1, 0, 0, Gothic.s(this.g.pal.chiseled()));
        this.g.set(n3 + 1, 1, 0, Gothic.stairs(this.g.pal.stairs(), Direction.EAST, false));
        this.g.set(n3 + 1, 2, 0, Gothic.s(Blocks.WITHER_SKELETON_SKULL));
        this.g.set(n3 + 1, 0, -1, Gothic.s(this.g.pal.trim()));
        this.g.set(n3 + 1, 0, 1, Gothic.s(this.g.pal.trim()));
        this.g.set(n3 + 1, 1, -1, Gothic.candle(3));
        this.g.set(n3 + 1, 1, 1, Gothic.candle(3));
        for (n6 = n3 + 3; n6 <= n9 - 2; ++n6) {
            this.g.set(n6, 0, 2, Gothic.s(Blocks.DARK_OAK_PLANKS));
            this.g.set(n6, 0, 3, Gothic.stairs(Blocks.DARK_OAK_STAIRS, Direction.NORTH, false));
            if (n6 % 2 != 0) continue;
            this.g.set(n6, 1, 2, Gothic.candle(1 + this.r.nextInt(3)));
        }
        this.g.chainLantern(-6, n14 - 1, 0, 1);
        this.g.chainLantern(-3, n14 - 1, 0, 1);
        this.g.sarcophagus(n3 + 2, n14, -1, true);
        this.bed(n3 + 2, n14, 3, Direction.EAST, Blocks.RED_BED);
        this.g.set(n9 - 1, n14, 3, Gothic.s(Blocks.BARREL));
        this.g.set(n9 - 1, n14 + 1, 3, Gothic.s(Blocks.BARREL));
        this.g.chest(n9 - 2, n14, 3, true);
        this.g.chainLantern(-5, n14 * 2 - 1, 1, 1);
        for (n6 = n3 + 2; n6 <= n9 - 2; n6 += 2) {
            this.g.set(n6, n14 * 2, n7 - 1, Gothic.s(Blocks.ANVIL));
            this.g.set(n6 + 1, n14 * 2, n7 - 1, Gothic.s(Blocks.SMITHING_TABLE));
        }
        this.g.chest(n3 + 2, n14 * 2, n7 - 1, false);
        this.g.chainLantern(-5, n14 * 3 - 1, 0, 1);
        n6 = -6;
        for (n5 = n3 + 1; n5 <= n9 - 1; ++n5) {
            for (n4 = n8 + 1; n4 <= n7 - 1; ++n4) {
                this.g.set(n5, n6 - 1, n4, Gothic.s(this.g.pal.floor()));
                for (n2 = n6; n2 < n6 + 4; ++n2) {
                    this.g.air(n5, n2, n4);
                }
                this.g.set(n5, n6 + 4, n4, Gothic.s(this.g.pal.polished()));
            }
        }
        for (n5 = n3 + 1; n5 <= n9 - 1; ++n5) {
            n4 = (n5 - n3) % 3 == 2 ? 1 : 0;
            for (n2 = n6; n2 < n6 + 3; ++n2) {
                if (n4 != 0 && n2 < n6 + 2) continue;
                this.g.set(n5, n2, -2, Gothic.s(Blocks.IRON_BARS));
                this.g.set(n5, n2, 2, Gothic.s(Blocks.IRON_BARS));
            }
            if ((n5 - n3) % 3 == 0) {
                this.g.set(n5, n6, -2, this.g.brick());
                this.g.set(n5, n6, 2, this.g.brick());
                for (n2 = n6; n2 < n6 + 4; ++n2) {
                    this.g.set(n5, n2, -3, this.g.brick());
                    this.g.set(n5, n2, -4, this.g.brick());
                    this.g.set(n5, n2, 3, this.g.brick());
                    this.g.set(n5, n2, 4, this.g.brick());
                }
            }
            if ((n5 - n3) % 3 != 1) continue;
            this.g.set(n5, n6, -4, Gothic.s(Blocks.SKELETON_SKULL));
            this.g.set(n5, n6 + 3, 4, Gothic.s(Blocks.CHAIN));
            this.g.set(n5, n6 + 2, 4, Gothic.s(Blocks.CHAIN));
        }
        for (n5 = 0; n5 <= 5; ++n5) {
            n4 = n9 - 1 - n5;
            for (n2 = -n5; n2 <= -n5 + 3; ++n2) {
                this.g.air(n4, n2, n7 - 1);
            }
            this.g.set(n4, -n5 - 1, n7 - 1, Gothic.stairs(this.g.pal.stairs(), Direction.EAST, false));
        }
        this.g.chest(n3 + 1, n6, 0, true);
        this.g.chainLantern(-4, n6 + 3, 0, 0);
        for (n5 = 5; n5 <= 9; ++n5) {
            for (n4 = -2; n4 <= 2; ++n4) {
                n2 = Math.abs(n5 - 7) == 2 || Math.abs(n4) == 2 ? 1 : 0;
                this.g.set(n5, 0, n4, n2 != 0 ? Gothic.s(this.g.pal.polished()) : Gothic.s(Blocks.AIR));
                if (n2 != 0) continue;
                this.g.set(n5, -1, n4, Gothic.s(Blocks.WATER));
            }
        }
        for (n5 = 0; n5 <= 3; ++n5) {
            this.g.set(7, n5, 0, n5 == 3 ? Gothic.s(this.g.pal.trim()) : Gothic.s(this.g.pal.wall()));
        }
        this.g.set(7, 4, 0, Gothic.s(Blocks.RED_STAINED_GLASS));
        this.g.set(7, 5, 0, Gothic.s(Blocks.WITHER_SKELETON_SKULL));
        this.well(5, 9);
        this.cage(9, 6, -9, 2);
        for (n5 = 0; n5 <= 6; ++n5) {
            this.g.set(10, n5, -9, Gothic.s(Blocks.DARK_OAK_FENCE));
        }
        this.g.set(10, 7, -9, Gothic.s(Blocks.DARK_OAK_LOG));
        this.g.set(9, 7, -9, Gothic.s(Blocks.DARK_OAK_FENCE));
        for (n5 = 4; n5 <= 11; ++n5) {
            this.g.set(n5, 0, n10 - 3, n5 % 3 == 0 ? Gothic.s(Blocks.DARK_OAK_FENCE) : Gothic.s(Blocks.HAY_BLOCK));
            this.g.set(n5, 3, n10 - 3, Gothic.s(Blocks.DARK_OAK_FENCE));
            for (n4 = n10 - 3; n4 < n10; ++n4) {
                this.g.set(n5, 4, n4, Gothic.slab(this.g.pal.roofSlab(), false));
            }
        }
        this.g.deadTree(-4, 0, 10, 7);
        this.g.deadTree(-4, 0, -10, 6);
        for (n5 = 0; n5 < 4; ++n5) {
            this.lampPost(3 + n5 * 3, n5 % 2 == 0 ? -5 : 5);
        }
        for (n5 = 0; n5 < 6; ++n5) {
            n4 = -n10 - 2 - this.r.nextInt(3);
            if (!this.onDisc(n4, n2 = -10 + this.r.nextInt(20))) continue;
            this.g.grave(n4, 0, n2, Direction.WEST);
        }
    }

    private void cursedVillage() {
        int n;
        int[][] nArrayArray;
        for (int i = -4; i <= 4; ++i) {
            for (int j = -4; j <= 4; ++j) {
                this.g.set(i, -1, j, this.r.nextFloat() < 0.75f ? Gothic.s(Blocks.COBBLED_DEEPSLATE) : Gothic.s(Blocks.GRAVEL));
            }
        }
        this.well(0, 0);
        this.street(-this.R + 2, 0, this.R - 2, 0, 1);
        this.street(0, -this.R + 2, 0, this.R - 2, 1);
        for (int[] nArray : nArrayArray = new int[][]{{-9, -6, 1}, {-9, 6, 1}, {8, -7, 0}, {8, 7, 0}, {-3, -11, 1}, {4, 12, 0}, {-14, 0, 0}}) {
            boolean bl = nArray[2] == 1;
            int n2 = 7 + 2 * this.r.nextInt(2);
            int n3 = 5;
            if (!this.onDisc(nArray[0] + (bl ? n2 / 2 : n3 / 2) + 1, nArray[1] + (bl ? n3 / 2 : n2 / 2) + 1) || !this.onDisc(nArray[0] - (bl ? n2 / 2 : n3 / 2) - 1, nArray[1] - (bl ? n3 / 2 : n2 / 2) - 1)) continue;
            Direction direction = bl ? (nArray[1] > 0 ? Direction.NORTH : Direction.SOUTH) : (nArray[0] > 0 ? Direction.WEST : Direction.EAST);
            this.house(nArray[0], nArray[1], n2, n3, bl, direction);
        }
        this.chapel(-6, 13, 11, 7, 6, true, false, false);
        this.g.tower(-12, 13, 0, 2, 11, true);
        for (n = 0; n <= 5; ++n) {
            this.g.set(5, n, 4, Gothic.s(Blocks.DARK_OAK_LOG));
        }
        for (n = 5; n <= 7; ++n) {
            this.g.set(n, 6, 4, Gothic.s(Blocks.DARK_OAK_LOG));
        }
        this.g.set(7, 5, 4, Gothic.s(Blocks.CHAIN));
        this.g.set(7, 4, 4, Gothic.s(Blocks.CHAIN));
        this.deadField(10, -14, 16, -10);
        this.deadField(10, 10, 15, 14);
        for (n = 9; n <= 17; ++n) {
            if (!this.onDisc(n, -15)) continue;
            this.g.set(n, 0, -15, Gothic.s(Blocks.DARK_OAK_FENCE));
        }
        for (n = 0; n < 6; ++n) {
            this.lampPost(-12 + n * 5, n % 2 == 0 ? 2 : -2);
        }
        for (n = 0; n < 6; ++n) {
            int n4;
            int n5 = -this.R + 3 + this.r.nextInt(2 * this.R - 6);
            if (!this.onDisc(n5, n4 = -this.R + 3 + this.r.nextInt(2 * this.R - 6)) || !this.g.isAir(n5, 0, n4) || !this.g.isAir(n5, 2, n4) || !this.g.isAir(n5 + 1, 3, n4)) continue;
            this.g.deadTree(n5, 0, n4, 4 + this.r.nextInt(4));
        }
        for (int[] nArray : new int[][]{{3, -3}, {-3, 3}, {3, 3}}) {
            this.g.set(nArray[0], 0, nArray[1], Gothic.s(Blocks.BARREL));
            if (!this.r.nextBoolean()) continue;
            this.g.set(nArray[0], 1, nArray[1], Gothic.s(Blocks.SKELETON_SKULL));
        }
        this.graveyard(-17, -10, -12, -4);
    }

    private void cathedral() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6 = -19;
        int n7 = 13;
        int n8 = 13;
        int n9 = 15;
        this.chapel(-3, 0, n7 - n6 + 1, n8, n9, true, true, true);
        this.chapel(5, 0, 27, 9, n9 - 2, false, true, false);
        for (n5 = n7; n5 <= n7 + 7; ++n5) {
            for (n4 = -7; n4 <= 7; ++n4) {
                double d = Math.sqrt((n5 - n7) * (n5 - n7) + n4 * n4);
                if (d > 6.4) continue;
                n3 = d > 5.4 ? 1 : 0;
                for (int i = 0; i < n9; ++i) {
                    if (n3 != 0) {
                        this.g.set(n5, i, n4, i >= 3 && i <= n9 - 4 && Math.abs(n4) % 3 == 0 ? Gothic.s(this.g.pal.glass()) : this.g.brick());
                        continue;
                    }
                    if (n5 <= n7) continue;
                    this.g.air(n5, i, n4);
                }
                if (n3 != 0) continue;
                this.g.set(n5, -1, n4, this.g.floorTile(n5, n4));
            }
        }
        for (n5 = 0; n5 <= 6; ++n5) {
            for (n4 = n7; n4 <= n7 + 7; ++n4) {
                for (int i = -7; i <= 7; ++i) {
                    double d;
                    double d2 = Math.sqrt((n4 - n7) * (n4 - n7) + i * i);
                    if (!(d2 <= (d = 6.4 - (double)n5)) || !(d2 > d - 1.1)) continue;
                    this.g.set(n4, n9 + n5, i, Gothic.s(this.g.pal.roof()));
                }
            }
        }
        this.g.altar(n7 + 3, 0, 0);
        this.g.set(n7 + 3, 3, 0, Gothic.s(Blocks.CHAIN));
        for (n5 = 0; n5 < 5; ++n5) {
            for (n4 = 0; n4 < 4 + n5 % 3 * 2; ++n4) {
                this.g.set(3 + n5, n4, -12, Gothic.s(Blocks.POLISHED_DEEPSLATE_WALL));
            }
        }
        for (int n10 : new int[]{-1, 1}) {
            int n11;
            int n12;
            n3 = n10 * 8;
            n2 = n6 - 1;
            for (int i = n2 - 2; i <= n2 + 2; ++i) {
                for (n12 = n3 - 2; n12 <= n3 + 2; ++n12) {
                    n11 = Math.abs(i - n2) == 2 || Math.abs(n12 - n3) == 2 ? 1 : 0;
                    for (int j = 0; j < 26; ++j) {
                        if (n11 != 0) {
                            boolean bl;
                            boolean bl2 = bl = !(j % 7 != 4 && j % 7 != 5 || i != n2 && n12 != n3);
                            this.g.set(i, j, n12, bl ? Gothic.s(this.g.pal.glass()) : (j % 7 == 0 ? Gothic.s(this.g.pal.polished()) : this.g.brick()));
                            continue;
                        }
                        this.g.set(i, j, n12, j % 7 == 0 && j > 0 ? Gothic.s(this.g.pal.floor()) : Gothic.s(Blocks.AIR));
                    }
                }
            }
            this.g.spire(n2, n3, 26, 2, 14);
            Direction[] directionArray = H4;
            n12 = directionArray.length;
            for (n11 = 0; n11 < n12; ++n11) {
                Direction direction = directionArray[n11];
                this.g.gargoyle(n2 + direction.getStepX() * 3, 24, n3 + direction.getStepZ() * 3, direction);
            }
            this.g.chainLantern(n2, 24, n3, 3);
        }
        this.g.tower(5, 0, n9 + 4, 3, 7, true);
        for (n = n6 + 3; n <= n7 - 2; n += 4) {
            if (Math.abs(n - 5) <= 5) continue;
            this.flyingButtress(n, -n8 / 2 - 2, Direction.NORTH, n9);
            this.flyingButtress(n, n8 / 2 + 2, Direction.SOUTH, n9);
        }
        n = -22;
        n4 = -12;
        int n13 = -16;
        int n14 = -4;
        this.arcade(n13, n, n14, n, 5, Direction.SOUTH);
        this.arcade(n13, n, n13, n4, 5, Direction.EAST);
        this.arcade(n14, n, n14, n4, 5, Direction.WEST);
        for (n3 = n13 + 3; n3 <= n14 - 3; ++n3) {
            for (n2 = n + 3; n2 <= n4 - 1; ++n2) {
                if (!this.onDisc(n3, n2)) continue;
                this.g.set(n3, -1, n2, Gothic.s(Blocks.PODZOL));
            }
        }
        this.well(-10, -17);
        if (this.onDisc(-8, 21) && this.onDisc(-8, 15)) {
            this.house(-8, 18, 9, 7, true, Direction.NORTH);
        }
        this.graveyard(4, 13, 16, 21);
        for (n3 = n6 - 12; n3 <= n6 - 4; n3 += 3) {
            this.lampPost(n3, -3);
            this.lampPost(n3, 3);
        }
        this.street(n6 - 14, 0, n6 - 2, 0, 1);
        for (n3 = 0; n3 < 5; ++n3) {
            int n15;
            n2 = -this.R + 4 + this.r.nextInt(2 * this.R - 8);
            if (!this.onDisc(n2, n15 = -this.R + 4 + this.r.nextInt(2 * this.R - 8)) || !this.g.isAir(n2, 0, n15) || !this.g.isAir(n2, 4, n15) || !this.g.isAir(n2 + 1, 2, n15)) continue;
            this.g.deadTree(n2, 0, n15, 5 + this.r.nextInt(4));
        }
    }

    private BlockState faceBlock(char c, boolean bl) {
        boolean bl2 = this.g.pal == Gothic.DWARF_DEEP;
        return switch (c) {
            case 'h' -> Gothic.s(Blocks.BONE_BLOCK);
            case 'H' -> Gothic.s(this.g.pal.polished());
            case 'G' -> Gothic.s(this.g.pal.trim());
            case 'S' -> Gothic.s(this.g.pal.chiseled());
            case 'E' -> Gothic.s(bl ? Blocks.MAGMA_BLOCK : Blocks.SHROOMLIGHT);
            case 'N' -> Gothic.s(bl ? Blocks.BRICKS : this.g.pal.polished());
            case 'M' -> Gothic.s(bl2 ? Blocks.POLISHED_BLACKSTONE : Blocks.POLISHED_DEEPSLATE);
            case 'B' -> Gothic.s(bl2 ? Blocks.BLACKSTONE : Blocks.DEEPSLATE_TILES);
            default -> Gothic.s(bl ? Blocks.BRICKS : this.g.pal.brick());
        };
    }

    private static int faceDepth(char c) {
        return switch (c) {
            case 'M' -> 2;
            case 'B', 'G', 'H', 'N', 'S', 'h' -> 1;
            default -> 0;
        };
    }

    private void relief(String[] stringArray, int n, int n2, int n3, Direction direction, int n4, int n5, boolean bl) {
        int n6 = stringArray[0].length() * n4;
        boolean bl2 = direction.getAxis() == Direction.Axis.X;
        int n7 = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;
        for (int i = 0; i < stringArray.length; ++i) {
            for (int j = 0; j < stringArray[i].length(); ++j) {
                char c = stringArray[i].charAt(j);
                if (c == '.') continue;
                for (int k = 0; k < n4; ++k) {
                    for (int i2 = 0; i2 < n4; ++i2) {
                        int n8 = n2 - n6 / 2 + j * n4 + k;
                        int n9 = n - i * n4 - i2;
                        int n10 = AncientStructures.faceDepth(c) * n4;
                        for (int i3 = -n5; i3 <= n10; ++i3) {
                            int n11;
                            int n12 = n3 + n7 * i3;
                            int n13 = bl2 ? n12 : n8;
                            int n14 = n11 = bl2 ? n8 : n12;
                            if (i3 < 0) {
                                if (!this.g.isAir(n13, n9, n11)) continue;
                                this.g.set(n13, n9, n11, Gothic.s(this.g.pal.brick()));
                                continue;
                            }
                            this.g.set(n13, n9, n11, this.faceBlock(c, bl));
                        }
                    }
                }
            }
        }
    }

    private void brazier(int n, int n2, int n3) {
        this.g.set(n, n2, n3, Gothic.s(this.g.pal.chiseled()));
        this.g.set(n, n2 + 1, n3, Gothic.s(this.g.pal.wall()));
        this.g.set(n, n2 + 2, n3, Gothic.s(this.g.pal.trim()));
        this.g.set(n, n2 + 3, n3, Gothic.s(Blocks.CAMPFIRE));
    }

    private void banner(int n, int n2, int n3, Direction direction, boolean bl) {
        BlockState blockState = (BlockState)(bl ? Blocks.RED_WALL_BANNER : Blocks.BLACK_WALL_BANNER).defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction);
        this.g.set(n, n2, n3, blockState);
        this.g.set(n, n2 - 2, n3, blockState);
    }

    private void statue(int n, int n2, int n3, Direction direction) {
        Direction direction2 = direction.getClockWise();
        this.g.set(n, n2, n3, Gothic.s(this.g.pal.chiseled()));
        this.g.set(n + direction2.getStepX(), n2, n3 + direction2.getStepZ(), Gothic.stairs(this.g.pal.stairs(), direction.getOpposite(), false));
        this.g.set(n - direction2.getStepX(), n2, n3 - direction2.getStepZ(), Gothic.stairs(this.g.pal.stairs(), direction.getOpposite(), false));
        this.g.set(n, n2 + 1, n3, Gothic.s(this.g.pal.wall()));
        for (int i = 2; i <= 3; ++i) {
            this.g.set(n, n2 + i, n3, Gothic.s(this.g.pal.polished()));
        }
        this.g.set(n + direction.getStepX(), n2 + 2, n3 + direction.getStepZ(), Gothic.s(Blocks.DEEPSLATE_TILES));
        this.g.set(n + direction.getStepX(), n2 + 3, n3 + direction.getStepZ(), Gothic.s(Blocks.POLISHED_DEEPSLATE));
        for (int n4 : new int[]{-1, 1}) {
            this.g.set(n + direction2.getStepX() * n4, n2 + 3, n3 + direction2.getStepZ() * n4, Gothic.s(this.g.pal.polished()));
        }
        this.g.set(n, n2 + 4, n3, Gothic.s(this.g.pal.brick()));
        this.g.set(n, n2 + 5, n3, Gothic.s(this.g.pal.trim()));
        for (int n4 : new int[]{-1, 1}) {
            this.g.set(n + direction2.getStepX() * n4, n2 + 5, n3 + direction2.getStepZ() * n4, Gothic.s(Blocks.BONE_BLOCK));
            this.g.set(n + direction2.getStepX() * n4, n2 + 6, n3 + direction2.getStepZ() * n4, Gothic.s(Blocks.BONE_BLOCK));
        }
        this.g.set(n - direction2.getStepX() * 2, n2 + 2, n3 - direction2.getStepZ() * 2, Gothic.s(Blocks.LIGHTNING_ROD));
        this.g.set(n - direction2.getStepX() * 2, n2 + 3, n3 - direction2.getStepZ() * 2, Gothic.s(Blocks.LIGHTNING_ROD));
        this.g.set(n - direction2.getStepX() * 2, n2 + 4, n3 - direction2.getStepZ() * 2, Gothic.s(this.g.pal.trim()));
    }

    private BlockState rock(int n) {
        float f = this.r.nextFloat();
        if (n > 40 && f < 0.7f) {
            return Gothic.s(Blocks.SNOW_BLOCK);
        }
        return Gothic.s(f < 0.4f ? Blocks.STONE : (f < 0.6f ? Blocks.ANDESITE : (f < 0.75f ? Blocks.TUFF : (f < 0.88f ? Blocks.COBBLESTONE : (f < 0.95f ? Blocks.MOSSY_COBBLESTONE : Blocks.DEEPSLATE)))));
    }

    private void aGate(int n, int n2, Direction direction, int n3, int n4, int n5) {
        boolean bl = direction.getAxis() == Direction.Axis.X;
        int n6 = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;
        for (int i = 0; i <= n4 + 1; ++i) {
            for (int j = -n3 - 1; j <= n3 + 1; ++j) {
                double d = (double)n3 * (1.0 - (double)i / (double)(n4 + 1));
                boolean bl2 = (double)Math.abs(j) <= d && i <= n4;
                boolean bl3 = !bl2 && (double)Math.abs(j) <= d + 1.2 && i <= n4 + 1;
                for (int k = 0; k < n5; ++k) {
                    int n7;
                    int n8 = n2 - n6 * k;
                    int n9 = bl ? n8 : n + j;
                    int n10 = n7 = bl ? n + j : n8;
                    if (bl2) {
                        this.g.air(n9, i, n7);
                        continue;
                    }
                    if (!bl3 || k != 0) continue;
                    this.g.set(n9, i, n7, Gothic.s(this.g.pal.trim()));
                }
            }
        }
    }

    private void dwarfHouse(int n, int n2, int n3, boolean bl, Direction direction) {
        int n4;
        int n5;
        int n6 = 7;
        int n7 = 5;
        int n8 = 4;
        int n9 = n6 / 2;
        int n10 = n7 / 2;
        int n11 = bl ? n - n9 : n - n10;
        int n12 = bl ? n + n9 : n + n10;
        int n13 = bl ? n3 - n10 : n3 - n9;
        int n14 = bl ? n3 + n10 : n3 + n9;
        this.g.hall(n11, n13, n12, n14, n2, n8, 3);
        for (n5 = n11; n5 <= n12; ++n5) {
            for (n4 = n13; n4 <= n14; ++n4) {
                if (n5 != n11 && n5 != n12 && n4 != n13 && n4 != n14) continue;
                this.g.set(n5, n2 + n8, n4, Gothic.s(this.g.pal.trim()));
            }
        }
        this.g.gable(n11, n13, n12, n14, n2 + n8 + 1, bl);
        for (n5 = (bl ? n11 : n13) + 2; n5 < (bl ? n12 : n14); n5 += 2) {
            if (bl) {
                this.g.set(n5, n2 + 2, n13, Gothic.s(this.g.pal.glass()));
                this.g.set(n5, n2 + 2, n14, Gothic.s(this.g.pal.glass()));
                continue;
            }
            this.g.set(n11, n2 + 2, n5, Gothic.s(this.g.pal.glass()));
            this.g.set(n12, n2 + 2, n5, Gothic.s(this.g.pal.glass()));
        }
        int n15 = direction == Direction.EAST ? n12 : (n5 = direction == Direction.WEST ? n11 : n);
        n4 = direction == Direction.SOUTH ? n14 : (direction == Direction.NORTH ? n13 : n3);
        this.g.air(n5, n2, n4);
        this.g.air(n5, n2 + 1, n4);
        this.g.set(n5, n2 + 2, n4, Gothic.s(this.g.pal.trim()));
        this.g.set(n5 + direction.getStepX(), n2 + 2, n4 + direction.getStepZ(), Gothic.s(Blocks.LANTERN));
        int n16 = bl ? n11 + 1 : n12 - 1;
        int n17 = bl ? n14 - 1 : n13 + 1;
        for (int i = n2; i < n2 + n8 + n10 + 3; ++i) {
            this.g.set(n16, i, n17, Gothic.s(Blocks.BRICKS));
        }
        this.g.set(n16, n2 + n8 + n10 + 3, n17, Gothic.s(Blocks.CAMPFIRE));
        this.g.set(n16, n2, n17, Gothic.s(Blocks.BLAST_FURNACE));
        this.g.set(bl ? n12 - 1 : n11 + 1, n2, bl ? n13 + 1 : n14 - 1, Gothic.s(Blocks.BARREL));
        this.g.set(n, n2, n3, Gothic.s(Blocks.SPRUCE_PLANKS));
        this.g.set(n, n2 + 1, n3, Gothic.candle(2));
        this.g.set(bl ? n12 - 1 : n11 + 1, n2, bl ? n14 - 1 : n13 + 1, Gothic.s(Blocks.ANVIL));
        if (this.r.nextFloat() < 0.5f) {
            this.g.chest(bl ? n11 + 1 : n12 - 1, n2, bl ? n13 + 1 : n14 - 1, false);
        }
    }

    private void bridge(int n, int n2, int n3, int n4, int n5, int n6) {
        boolean bl;
        int n7 = n4 - n;
        int n8 = n6 - n3;
        int n9 = Math.max(Math.abs(n7), Math.abs(n8));
        if (n9 == 0) {
            return;
        }
        boolean bl2 = bl = Math.abs(n7) >= Math.abs(n8);
        Direction direction = bl ? (n7 > 0 ? Direction.EAST : Direction.WEST) : (n8 > 0 ? Direction.SOUTH : Direction.NORTH);
        int n10 = n2;
        for (int i = 0; i <= n9; ++i) {
            int n11 = n + Math.round((float)n7 * (float)i / (float)n9);
            int n12 = n3 + Math.round((float)n8 * (float)i / (float)n9);
            int n13 = n2 + Math.round((float)(n5 - n2) * (float)i / (float)n9);
            Direction direction2 = n13 > n10 ? direction : (n13 < n10 ? direction.getOpposite() : null);
            for (int j = -2; j <= 2; ++j) {
                int n14;
                int n15 = bl ? n11 : n11 + j;
                int n16 = n14 = bl ? n12 + j : n12;
                if (Math.abs(j) <= 1) {
                    this.g.set(n15, n13 - 1, n14, Gothic.s(Blocks.SPRUCE_PLANKS));
                    for (int k = 0; k <= 2; ++k) {
                        this.g.air(n15, n13 + k, n14);
                    }
                    if (direction2 == null) continue;
                    this.g.set(n15, n13 - 1, n14, Gothic.stairs(Blocks.SPRUCE_STAIRS, direction2, false));
                    continue;
                }
                this.g.set(n15, n13 - 1, n14, Gothic.s(Blocks.STRIPPED_SPRUCE_LOG));
                this.g.set(n15, n13, n14, i % 5 == 0 ? Gothic.s(Blocks.SPRUCE_FENCE) : Gothic.s(Blocks.SPRUCE_FENCE));
                if (i % 5 != 0) continue;
                this.g.set(n15, n13 + 1, n14, Gothic.s(Blocks.LANTERN));
            }
            n10 = n13;
        }
    }

    private double wallR(int n, int n2) {
        return Math.sqrt(Math.max(0.0, (double)(n * n) - 1.3 * (double)n2 * (double)n2));
    }

    private void caveTown(int n, int n2, int n3, boolean bl) {
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10;
        double d;
        int n11;
        double d2;
        int n12;
        int n13;
        int n14;
        double d3 = this.r.nextDouble() * 6.0;
        if (bl) {
            for (n14 = n - n3 - 4; n14 <= n + n3 + 4; ++n14) {
                for (n13 = n2 - n3 - 4; n13 <= n2 + n3 + 4; ++n13) {
                    for (n12 = 0; n12 <= n3 + 4; ++n12) {
                        double d4 = Math.sqrt((double)((n14 - n) * (n14 - n)) + (double)(n12 * n12) * 1.3 + (double)((n13 - n2) * (n13 - n2)));
                        if (d4 > (d2 = (double)n3 + 1.5 * Math.sin(Math.atan2(n13 - n2, n14 - n) * 4.0 + d3) + Math.sin((double)n12 * 0.5)) + 3.5 || !this.onDisc(n14, n13)) continue;
                        boolean bl2 = n14 > n && Math.abs(n13 - n2) <= 4 && n12 < 9;
                        int n15 = n11 = (n14 - n) * (n14 - n) + (n13 - n2) * (n13 - n2) < 30 && n12 > n3 / 2 ? 1 : 0;
                        if (d4 > d2 && !bl2 && n11 == 0) {
                            this.g.set(n14, n12, n13, this.rock(n12));
                            continue;
                        }
                        this.g.air(n14, n12, n13);
                    }
                }
            }
        }
        n14 = (int)((double)n3 * 0.48);
        n13 = 7;
        for (n12 = n - n14 - 2; n12 <= n + n14 + 2; ++n12) {
            for (int i = n2 - n14 - 2; i <= n2 + n14 + 2; ++i) {
                d = Math.sqrt((n12 - n) * (n12 - n) + (i - n2) * (i - n2));
                if (d > (double)n14 + 1.5 * Math.sin(Math.atan2(i - n2, n12 - n) * 3.0 + d3)) continue;
                if (d <= (double)n13) {
                    this.g.set(n12, -1, i, this.g.floorTile(n12, i));
                    continue;
                }
                this.g.set(n12, -1, i, Gothic.s(Blocks.WATER));
                this.g.set(n12, -2, i, Gothic.s(Blocks.WATER));
                this.g.set(n12, -3, i, Gothic.s(Blocks.GRAVEL));
            }
        }
        for (n12 = n - 6; n12 <= n + 6; ++n12) {
            for (int i = n2 - 6; i <= n2 + 6; ++i) {
                d = Math.sqrt((n12 - n) * (n12 - n) + (i - n2) * (i - n2));
                if (d > 6.4) continue;
                for (n10 = 0; n10 < 7; ++n10) {
                    this.g.set(n12, n10, i, d > 5.4 ? (n10 == 3 && Math.floorMod(n12 + i, 3) == 0 ? Gothic.s(this.g.pal.glass()) : (n10 == 6 ? Gothic.s(this.g.pal.trim()) : this.g.brick())) : Gothic.s(Blocks.AIR));
                }
            }
        }
        for (n12 = 0; n12 <= 6; ++n12) {
            for (int i = n - 7; i <= n + 7; ++i) {
                for (int j = n2 - 7; j <= n2 + 7; ++j) {
                    double d5;
                    d2 = Math.sqrt((i - n) * (i - n) + (j - n2) * (j - n2));
                    if (!(d2 <= (d5 = Math.sqrt(Math.max(0.0, 40.96000000000001 - (double)(n12 * n12))))) || !(d2 > d5 - 1.2)) continue;
                    this.g.set(i, 7 + n12, j, (i + j + n12) % 4 == 0 ? Gothic.s(this.g.pal.trim()) : Gothic.s(this.g.pal.roof()));
                }
            }
        }
        this.g.set(n - 4, 0, n2, Gothic.s(this.g.pal.trim()));
        this.g.set(n - 4, 1, n2, Gothic.stairs(this.g.pal.stairs(), Direction.EAST, false));
        this.g.set(n - 4, 2, n2, Gothic.s(this.g.pal.chiseled()));
        this.statue(n - 3, 0, n2 - 3, Direction.EAST);
        this.statue(n - 3, 0, n2 + 3, Direction.EAST);
        this.g.chainLantern(n, 11, n2, 4);
        this.g.chest(n - 5, 0, n2 + 1, true);
        for (Direction direction : H4) {
            n10 = n + direction.getStepX() * 6;
            int n16 = n2 + direction.getStepZ() * 6;
            for (n11 = 0; n11 < 3; ++n11) {
                this.g.air(n10, n11, n16);
                this.g.air(n + direction.getStepX() * 5, n11, n2 + direction.getStepZ() * 5);
            }
            this.bridge(n + direction.getStepX() * 7, 0, n2 + direction.getStepZ() * 7, n + direction.getStepX() * (n14 + 3), 0, n2 + direction.getStepZ() * (n14 + 3));
        }
        Object object = null;
        for (n9 = 0; n9 < 3; ++n9) {
            int[] nArray;
            n8 = n9 * 7;
            double d6 = this.wallR(n3, n8) - 7.0;
            int n17 = Math.max(6, (int)(Math.PI * 2 * d6 / 15.0));
            ArrayList<int[]> arrayList = new ArrayList<int[]>();
            for (n7 = 0; n7 < n17; ++n7) {
                Direction direction;
                int n18;
                double d7 = ((double)n7 + 0.5 * (double)n9) * Math.PI * 2.0 / (double)n17 + 0.2;
                if (Math.cos(d7) > 0.92 && n9 == 0) continue;
                int n19 = n + (int)Math.round(Math.cos(d7) * d6);
                n6 = n2 + (int)Math.round(Math.sin(d7) * d6);
                for (n18 = n19 - 5; n18 <= n19 + 5; ++n18) {
                    for (int i = n6 - 5; i <= n6 + 5; ++i) {
                        if ((n18 - n19) * (n18 - n19) + (i - n6) * (i - n6) > 28) continue;
                        for (n5 = -3; n5 < n8; ++n5) {
                            this.g.set(n18, n5, i, this.rock(n5));
                        }
                        this.g.set(n18, n8 - 1, i, Gothic.s(this.g.pal.floor()));
                        for (n5 = n8; n5 < n8 + 11; ++n5) {
                            this.g.air(n18, n5, i);
                        }
                    }
                }
                int n20 = n18 = Math.abs(Math.sin(d7)) > 0.7 ? 1 : 0;
                Direction direction2 = n18 != 0 ? (Math.sin(d7) > 0.0 ? Direction.NORTH : Direction.SOUTH) : (direction = Math.cos(d7) > 0.0 ? Direction.WEST : Direction.EAST);
                if (n9 == 0 && n7 % 4 == 1) {
                    this.forgeShop(n19, n8, n6, direction);
                } else if (n9 == 0 && n7 % 4 == 3) {
                    this.marketSquare(n19, n8, n6);
                } else {
                    this.dwarfHouse(n19, n8, n6, n18 != 0, direction);
                }
                arrayList.add(new int[]{n19 - (int)Math.round(Math.cos(d7) * 5.0), n8, n6 - (int)Math.round(Math.sin(d7) * 5.0)});
            }
            for (n7 = 0; n7 < arrayList.size(); ++n7) {
                int[] nArray2 = (int[])arrayList.get(n7);
                nArray = (int[])arrayList.get((n7 + 1) % arrayList.size());
                this.bridge(nArray2[0], nArray2[1], nArray2[2], nArray[0], nArray[1], nArray[2]);
            }
            if (object != null) {
                for (n7 = 0; n7 < arrayList.size(); n7 += 2) {
                    int[] nArray3 = (int[])arrayList.get(n7);
                    nArray = (int[])object.get(n7 % object.size());
                    this.bridge(nArray3[0], nArray3[1], nArray3[2], nArray[0], nArray[1], nArray[2]);
                }
            }
            object = arrayList;
        }
        n9 = 5;
        for (n8 = 0; n8 < n9; ++n8) {
            int n21;
            double d8 = (double)n8 * Math.PI * 2.0 / (double)n9 + 0.6;
            double d9 = (double)n14 * 0.72;
            n7 = n + (int)Math.round(Math.cos(d8) * d9);
            int n22 = n2 + (int)Math.round(Math.sin(d8) * d9);
            for (n21 = -3; n21 <= n3; ++n21) {
                double d10 = 1.8 + 1.6 * Math.abs(Math.cos((double)n21 * 0.1)) + (n21 < 2 ? 1.2 : 0.0);
                for (int i = n7 - 5; i <= n7 + 5; ++i) {
                    for (int j = n22 - 5; j <= n22 + 5; ++j) {
                        if (!(Math.sqrt((i - n7) * (i - n7) + (j - n22) * (j - n22)) <= d10) || !bl && n21 >= n3 - 4) continue;
                        this.g.set(i, n21, j, this.rock(n21));
                    }
                }
            }
            n21 = 10 + n8 % 3 * 3;
            for (int i = n7 - 4; i <= n7 + 4; ++i) {
                for (n6 = n22 - 4; n6 <= n22 + 4; ++n6) {
                    if ((i - n7) * (i - n7) + (n6 - n22) * (n6 - n22) > 18) continue;
                    this.g.set(i, n21 - 1, n6, Gothic.s(Blocks.SPRUCE_PLANKS));
                    for (int j = n21; j < n21 + 4; ++j) {
                        this.g.air(i, j, n6);
                    }
                    if ((i - n7) * (i - n7) + (n6 - n22) * (n6 - n22) <= 12) continue;
                    this.g.set(i, n21, n6, Gothic.s(Blocks.SPRUCE_FENCE));
                }
            }
            this.g.set(n7, n21, n22 + 1, Blocks.LANTERN.defaultBlockState());
            this.g.chest(n7 - 1, n21, n22, false);
            double d11 = Math.atan2(n22 - n2, n7 - n);
            double d12 = this.wallR(n3, 7) - 12.0;
            this.bridge(n7 + (int)Math.round(Math.cos(d11) * 3.0), n21, n22 + (int)Math.round(Math.sin(d11) * 3.0), n + (int)Math.round(Math.cos(d11) * d12), 7, n2 + (int)Math.round(Math.sin(d11) * d12));
        }
        for (n4 = n8 = n - (int)this.wallR(n3, 0) + 4; n4 <= n8 + 4; ++n4) {
            for (n10 = n2 - 3; n10 <= n2 + 3; ++n10) {
                boolean bl3 = n4 == n8 + 4 || Math.abs(n10 - n2) == 3;
                this.g.set(n4, -1, n10, bl3 ? Gothic.s(this.g.pal.polished()) : Gothic.s(Blocks.LAVA));
                this.g.set(n4, -2, n10, Gothic.s(Blocks.MAGMA_BLOCK));
                if (!bl3) continue;
                this.g.set(n4, 0, n10, Gothic.s(this.g.pal.wall()));
            }
        }
        for (n4 = n3 / 2; n4 > 2; --n4) {
            if (this.g.isAir(n8 - 1, n4, n2)) continue;
            this.g.set(n8 - 1, n4, n2, Gothic.s(Blocks.LAVA));
            break;
        }
        double d13 = 2.356194490192345;
        int n23 = n + (int)Math.round(Math.cos(d13) * (this.wallR(n3, 0) - 3.0));
        int n24 = n2 + (int)Math.round(Math.sin(d13) * (this.wallR(n3, 0) - 3.0));
        for (n7 = 0; n7 < 16; ++n7) {
            int n25 = n23 + (int)Math.round(Math.cos(d13) * (double)n7);
            int n26 = n24 + (int)Math.round(Math.sin(d13) * (double)n7);
            for (int i = -1; i <= 1; ++i) {
                for (n6 = 0; n6 < 3; ++n6) {
                    int n27 = n25 + (int)Math.round(-Math.sin(d13) * (double)i);
                    int n28 = n26 + (int)Math.round(Math.cos(d13) * (double)i);
                    this.g.air(n27, n6, n28);
                    this.g.set(n27, -1, n28, Gothic.s(Blocks.COBBLESTONE));
                    if (Math.abs(i) != 1 || !(this.r.nextFloat() < 0.15f)) continue;
                    this.g.set(n27 + (int)Math.round(-Math.sin(d13) * (double)i), n6, n28 + (int)Math.round(Math.cos(d13) * (double)i), Gothic.s(this.r.nextFloat() < 0.4f ? Blocks.IRON_ORE : (this.r.nextFloat() < 0.5f ? Blocks.COPPER_ORE : Blocks.COAL_ORE)));
                }
            }
            this.g.set(n25, 0, n26, Gothic.s(Blocks.RAIL));
            if (n7 % 4 != 0) continue;
            this.g.set(n25, 3, n26, Gothic.s(Blocks.SPRUCE_PLANKS));
            this.g.set(n25, 2, n26, (BlockState)Blocks.LANTERN.defaultBlockState().setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(true)));
        }
        for (n7 = 0; n7 < n3 * 3; ++n7) {
            int n29;
            int n30 = n - n3 + 3 + this.r.nextInt(2 * n3 - 6);
            int n31 = n2 - n3 + 3 + this.r.nextInt(2 * n3 - 6);
            for (n29 = 6; n29 < n3 + 6 && this.g.isAir(n30, n29, n31) && this.g.get(n30, n29, n31) != null; ++n29) {
            }
            if (n29 >= n3 + 6 || this.g.get(n30, n29, n31) == null || this.g.isAir(n30, n29, n31)) continue;
            n6 = 3 + this.r.nextInt(9);
            for (int i = 1; i <= n6 && this.g.isAir(n30, n29 - i, n31); ++i) {
                this.g.set(n30, n29 - i, n31, i == n6 ? Gothic.s(Blocks.GLOWSTONE) : Gothic.s(Blocks.CHAIN));
            }
        }
        block37: for (double d14 : new double[]{2.3, 4.0}) {
            int n32 = n + (int)Math.round(Math.cos(d14) * (double)(n3 - 6));
            int n33 = n2 + (int)Math.round(Math.sin(d14) * (double)(n3 - 6));
            for (n5 = n3 / 2 + 4; n5 > 4; --n5) {
                if (this.g.isAir(n32, n5, n33)) continue;
                this.g.set(n32, n5, n33, Gothic.s(Blocks.WATER));
                continue block37;
            }
        }
    }

    private void forgeShop(int n, int n2, int n3, Direction direction) {
        int n4;
        int n5;
        for (n5 = n - 3; n5 <= n + 3; ++n5) {
            for (n4 = n3 - 3; n4 <= n3 + 3; ++n4) {
                this.g.set(n5, n2 - 1, n4, Gothic.s(this.g.pal.floorAlt()));
                if (Math.abs(n5 - n) == 3 && Math.abs(n4 - n3) == 3) {
                    for (int i = n2; i < n2 + 4; ++i) {
                        this.g.set(n5, i, n4, Gothic.s(Blocks.STRIPPED_SPRUCE_LOG));
                    }
                }
                this.g.set(n5, n2 + 4, n4, Gothic.slab(this.g.pal.roofSlab(), false));
            }
        }
        for (n5 = -1; n5 <= 1; ++n5) {
            for (n4 = -1; n4 <= 1; ++n4) {
                this.g.set(n + n5, n2, n3 + n4, n5 == 0 && n4 == 0 ? Gothic.s(Blocks.LAVA) : Gothic.s(Blocks.BRICKS));
            }
        }
        for (n5 = n2 + 1; n5 <= n2 + 6; ++n5) {
            this.g.set(n, n5, n3, n5 < n2 + 3 ? Gothic.s(Blocks.AIR) : Gothic.s(Blocks.BRICKS));
        }
        this.g.set(n, n2 + 1, n3, Gothic.s(Blocks.IRON_BARS));
        this.g.set(n + 2, n2, n3 - 2, Gothic.s(Blocks.ANVIL));
        this.g.set(n - 2, n2, n3 + 2, Gothic.s(Blocks.ANVIL));
        this.g.set(n - 2, n2, n3 - 2, Gothic.s(Blocks.BLAST_FURNACE));
        this.g.set(n + 2, n2, n3 + 2, Gothic.s(Blocks.BARREL));
        this.g.set(n + 2, n2, n3, Gothic.s(Blocks.SMITHING_TABLE));
        this.g.chest(n - 2, n2, n3, false);
    }

    private void marketSquare(int n, int n2, int n3) {
        Block[] blockArray = new Block[]{Blocks.RED_WOOL, Blocks.ORANGE_WOOL, Blocks.YELLOW_WOOL, Blocks.BLUE_WOOL};
        int n4 = 0;
        for (int[] nArray : new int[][]{{-3, -3}, {1, -3}, {-3, 1}, {1, 1}}) {
            int n5 = n + nArray[0];
            int n6 = n3 + nArray[1];
            int[][] nArrayArray = new int[][]{{0, 0}, {2, 0}, {0, 2}, {2, 2}};
            int n7 = nArrayArray.length;
            for (int i = 0; i < n7; ++i) {
                int[] nArray2 = nArrayArray[i];
                for (int j = 0; j < 3; ++j) {
                    this.g.set(n5 + nArray2[0], n2 + j, n6 + nArray2[1], Gothic.s(Blocks.SPRUCE_FENCE));
                }
            }
            for (int i = n5; i <= n5 + 2; ++i) {
                for (n7 = n6; n7 <= n6 + 2; ++n7) {
                    this.g.set(i, n2 + 3, n7, Gothic.s(blockArray[n4 % blockArray.length]));
                }
            }
            this.g.set(n5 + 1, n2, n6 + 1, Gothic.s(n4 % 2 == 0 ? Blocks.BARREL : Blocks.CRAFTING_TABLE));
            ++n4;
        }
        this.g.set(n, n2, n3, Gothic.s(Blocks.LANTERN));
    }

    private void dwarfTown() {
        int n;
        this.caveTown(0, 0, this.R - 5, true);
        for (n = this.R - 5; n > 0 && this.g.isAir(n, 12, 0); --n) {
        }
        for (int i = -8; i <= 8; ++i) {
            for (int j = 0; j <= 22; ++j) {
                if (!this.onDisc(n + 1, i)) continue;
                this.g.set(n + 1, j, i, Math.abs(i) <= 4 && j < 8 ? Gothic.s(Blocks.AIR) : this.g.brick());
            }
        }
        this.aGate(0, n + 1, Direction.EAST, 4, 8, 1);
        this.relief(DWARF_FACE, 22, 0, n + 2, Direction.EAST, 1, 0, false);
        this.brazier(n + 3, 0, -6);
        this.brazier(n + 3, 0, 6);
        this.banner(n + 2, 6, -7, Direction.EAST, true);
        this.banner(n + 2, 6, 7, Direction.EAST, false);
        this.statue(n + 6, 0, -5, Direction.EAST);
        this.statue(n + 6, 0, 5, Direction.EAST);
        this.street(n + 2, 0, this.R - 2, 0, 2);
    }

    private void dwarfHold() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n72;
        int n82;
        int n9;
        double d;
        int n10;
        int n11;
        int n12 = -16;
        int n13 = this.R - 3;
        int n14 = 66;
        int n15 = 16;
        int n16 = 22;
        double d2 = this.r.nextDouble() * 6.0;
        for (n11 = n12 - n13 - 2; n11 <= n12 + n13 + 2; ++n11) {
            for (n10 = -n13 - 2; n10 <= n13 + 2; ++n10) {
                d = Math.sqrt((n11 - n12) * (n11 - n12) + n10 * n10) / ((double)n13 * (1.0 + 0.1 * Math.sin(Math.atan2(n10, n11 - n12) * 5.0 + d2)));
                if (d > 1.0 || !this.onDisc(n11, n10)) continue;
                n9 = (int)Math.round((double)n14 * Math.pow(1.0 - d, 1.2) + 2.0 * Math.sin((double)n11 * 0.4 + (double)n10 * 0.3));
                for (n82 = 0; n82 < n9; ++n82) {
                    this.g.set(n11, n82, n10, this.rock(n82));
                }
            }
        }
        for (n11 = n12; n11 < n15; ++n11) {
            for (n10 = -26; n10 <= 26; ++n10) {
                if (!this.onDisc(n11, n10)) continue;
                d = Math.max(0, Math.abs(n10) - 12);
                n9 = (int)Math.round(55.0 - d * d * 0.09 - (double)(n15 - n11) * 0.25 + 3.0 * Math.sin((double)n10 * 0.45 + (double)n11 * 0.3 + d2) + (double)this.r.nextInt(3) + 6.0 * Math.max(0.0, 1.0 - (double)Math.abs(n11 - (n15 - 12)) / 10.0));
                for (n82 = 0; n82 < n9; ++n82) {
                    if (!this.g.isAir(n11, n82, n10)) continue;
                    this.g.set(n11, n82, n10, this.rock(n82));
                }
            }
        }
        for (n11 = n12 - n16 - 1; n11 <= n12 + n16 + 1; ++n11) {
            for (n10 = -n16 - 1; n10 <= n16 + 1; ++n10) {
                for (int i = 0; i <= n16 + 2; ++i) {
                    if (!(Math.sqrt((double)((n11 - n12) * (n11 - n12)) + (double)(i * i) * 1.3 + (double)(n10 * n10)) < (double)n16)) continue;
                    this.g.air(n11, i, n10);
                }
            }
        }
        this.caveTown(n12, 0, n16, false);
        for (n11 = n15; n11 <= n12 + n13 + 2; ++n11) {
            for (n10 = -24; n10 <= 24; ++n10) {
                for (int i = 0; i <= 54; ++i) {
                    if (this.g.get(n11, i, n10) == null || !AncientStructures.isRock(this.g.get(n11, i, n10))) continue;
                    this.g.air(n11, i, n10);
                }
            }
        }
        for (n11 = -22; n11 <= 22; ++n11) {
            for (n10 = 0; n10 <= 52; ++n10) {
                this.g.set(n15, n10, n11, n10 % 6 == 0 ? Gothic.s(this.g.pal.polished()) : (Math.abs(n11) % 6 == 0 ? Gothic.s(this.g.pal.chiseled()) : this.g.brick()));
                this.g.set(n15 - 1, n10, n11, this.g.brick());
            }
        }
        for (n11 = -23; n11 <= 23; ++n11) {
            this.g.set(n15 + 1, 53, n11, Gothic.s(this.g.pal.trim()));
            this.g.set(n15 + 1, 52, n11, Gothic.stairs(this.g.pal.stairs(), Direction.WEST, true));
        }
        for (int n72 : new int[]{-1, 1}) {
            n9 = n72 * 16;
            for (n82 = 3; n82 <= 48; ++n82) {
                for (n6 = -5; n6 <= 5; ++n6) {
                    n5 = n9 + n6;
                    if (n82 > 42 && Math.abs(n6) > 48 - n82) continue;
                    n4 = Math.abs(n6) == 5 || n82 == 3 || n82 == 48 || n82 > 42 && Math.abs(n6) >= 47 - n82 ? 1 : 0;
                    int n17 = n3 = (n82 + Math.abs(n6)) % 4 == 0 || Math.abs(n6) == 2 && n82 % 4 == 2 ? 1 : 0;
                    this.g.set(n15 + 1, n82, n5, n4 != 0 ? Gothic.s(this.g.pal.chiseled()) : (n3 != 0 ? Gothic.s(this.g.pal.trim()) : (n82 % 8 < 3 ? Gothic.s(this.g.pal.glass()) : Gothic.s(Blocks.IRON_BARS))));
                    this.g.set(n15, n82, n5, n4 != 0 ? Gothic.s(this.g.pal.polished()) : Gothic.s(Blocks.SHROOMLIGHT));
                }
            }
            this.g.spire(n15 + 1, n9, 54, 2, 10);
        }
        this.relief(DWARF_FACE, 52, 0, n15 + 1, Direction.EAST, 2, 0, true);
        this.aGate(0, n15 + 1, Direction.EAST, 7, 15, 4);
        int n18 = n12 + n16 - 4;
        n10 = n15;
        for (n2 = n18; n2 <= n10; ++n2) {
            for (n72 = -8; n72 <= 8; ++n72) {
                n9 = Math.abs(n72) == 8 ? 1 : 0;
                this.g.set(n2, -1, n72, this.g.floorTile(n2, n72));
                for (n82 = 0; n82 <= 14; ++n82) {
                    this.g.set(n2, n82, n72, n9 != 0 ? this.g.brick() : (n82 == 14 ? Gothic.s(this.g.pal.polished()) : Gothic.s(Blocks.AIR)));
                }
                if (n9 == 0 && Math.abs(n72) == 5 && Math.floorMod(n2, 5) == 0) {
                    for (n82 = 0; n82 < 14; ++n82) {
                        this.g.set(n2, n82, n72, n82 == 0 || n82 == 13 ? Gothic.s(this.g.pal.chiseled()) : Gothic.s(this.g.pal.polished()));
                    }
                }
                if (Math.abs(n72) == 7 && Math.floorMod(n2, 10) == 0) {
                    this.statue(n2, 0, n72, n72 > 0 ? Direction.NORTH : Direction.SOUTH);
                }
                if (Math.abs(n72) == 6 && Math.floorMod(n2, 10) == 5) {
                    this.brazier(n2, 0, n72);
                }
                if (n72 != 0) continue;
                this.g.set(n2, 0, n72, Gothic.s(Blocks.RED_CARPET));
            }
        }
        for (n2 = n18 + 2; n2 <= n10 - 2; n2 += 6) {
            this.g.chainLantern(n2, 13, 0, 3);
        }
        int[] nArray = new int[]{-1, 1};
        n72 = nArray.length;
        for (n9 = 0; n9 < n72; ++n9) {
            n82 = nArray[n9];
            for (n6 = n18 + 3; n6 <= n10; n6 += 8) {
                this.banner(n6, 11, n82 * 7, n82 > 0 ? Direction.NORTH : Direction.SOUTH, n6 % 16 == 3);
            }
        }
        for (n = n15 + 2; n <= this.R - 2; ++n) {
            for (n72 = -7; n72 <= 7; ++n72) {
                if (!this.onDisc(n, n72)) continue;
                this.g.set(n, -1, n72, this.g.floorTile(n, n72));
            }
        }
        for (n = n15 + 4; n <= this.R - 4; n += 5) {
            int[] nArray2 = new int[]{-1, 1};
            n9 = nArray2.length;
            for (n82 = 0; n82 < n9; ++n82) {
                n6 = nArray2[n82];
                if (!this.onDisc(n, n6 * 9)) continue;
                this.statue(n, 0, n6 * 9, n6 > 0 ? Direction.NORTH : Direction.SOUTH);
                this.brazier(n + 2, 0, n6 * 8);
            }
        }
        for (int n82 : new int[]{-1, 1}) {
            for (n6 = 42; n6 >= 18; n6 -= 8) {
                this.banner(n15 + 1, n6, n82 * 9, Direction.EAST, n6 % 16 == 10);
            }
        }
        int[] nArray3 = new int[]{-1, 1};
        int n19 = nArray3.length;
        for (n9 = 0; n9 < n19; ++n9) {
            n5 = 2;
            n82 = nArray3[n9];
            n6 = n82 * 30;
            if (!this.onDisc(n5, n6)) continue;
            for (n4 = 0; n4 < 50 && this.g.get(n5, n4, n6) != null && !this.g.get(n5, n4, n6).isAir(); ++n4) {
            }
            this.g.tower(n5, n6, n4, 3, 12, false);
            for (n3 = 0; n3 < 3; ++n3) {
                this.g.air(n5 + 3, n4 + n3, n6);
            }
        }
        this.g.chest(n12, 0, 2, true);
    }

    private static boolean isRock(BlockState blockState) {
        return blockState.is(Blocks.STONE) || blockState.is(Blocks.ANDESITE) || blockState.is(Blocks.TUFF) || blockState.is(Blocks.COBBLESTONE) || blockState.is(Blocks.MOSSY_COBBLESTONE) || blockState.is(Blocks.DEEPSLATE) || blockState.is(Blocks.SNOW_BLOCK);
    }

    private void deepKingdom() {
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
        double d;
        int n11;
        int n12;
        int n13 = 34;
        int n14 = 7;
        int n15 = 4;
        int n16 = 4;
        for (n12 = -n13 - 1; n12 <= n13 + 1; ++n12) {
            for (n11 = -n13 - 1; n11 <= n13 + 1; ++n11) {
                int n17;
                int n18;
                d = Math.sqrt(n12 * n12 + n11 * n11);
                if (d > (double)n13) continue;
                n10 = Math.min(n16, (int)(((double)n13 - d) / (double)n14) + 1);
                for (n18 = n17 = -n15 * n10; n18 <= -1; ++n18) {
                    this.g.air(n12, n18, n11);
                }
                this.g.set(n12, n17 - 1, n11, this.g.floorTile(n12, n11));
                for (n18 = n17 - 2; n18 >= n17 - 4; --n18) {
                    this.g.set(n12, n18, n11, this.g.brick());
                }
            }
        }
        for (n12 = -n13 - 1; n12 <= n13 + 1; ++n12) {
            for (n11 = -n13 - 1; n11 <= n13 + 1; ++n11) {
                d = Math.sqrt(n12 * n12 + n11 * n11);
                if (d > (double)(n13 + 1)) continue;
                n10 = d > (double)n13 ? 0 : Math.min(n16, (int)(((double)n13 - d) / (double)n14) + 1);
                for (int i = Math.max(1, n10 + 1); i <= n16; ++i) {
                }
                double d2 = n13 - n10 * n14;
                if (n10 < 1 || n10 >= n16 || !(d < d2 + 1.0)) continue;
                n9 = -n15 * n10;
                this.g.set(n12, n9, n11, Gothic.s(this.g.pal.wall()));
                if (Math.floorMod(n12 * 3 + n11 * 5, 9) == 0) {
                    this.g.set(n12, n9 + 1, n11, Blocks.LANTERN.defaultBlockState());
                }
                for (n8 = n9 - n15; n8 < n9 - 1; ++n8) {
                    this.g.set(n12, n8, n11, n8 == n9 - 2 ? Gothic.s(this.g.pal.trim()) : Gothic.s(this.g.pal.polished()));
                }
            }
        }
        for (Direction direction : H4) {
            Direction direction2 = direction.getClockWise();
            for (int i = 1; i <= n16; ++i) {
                double d3 = n13 - (i - 1) * n14;
                for (n8 = 1; n8 <= n15; ++n8) {
                    double d4 = d3 + 0.5 - (double)n8;
                    int n19 = -n15 * (i - 1) - 1 - n8;
                    for (n7 = -1; n7 <= 1; ++n7) {
                        n6 = (int)Math.round((double)direction.getStepX() * d4) + direction2.getStepX() * n7;
                        n5 = (int)Math.round((double)direction.getStepZ() * d4) + direction2.getStepZ() * n7;
                        this.g.set(n6, n19, n5, Gothic.stairs(this.g.pal.stairs(), direction, false));
                        for (n4 = 1; n4 <= 3; ++n4) {
                            this.g.air(n6, n19 + n4, n5);
                        }
                        this.g.set(n6, n19 - 1, n5, this.g.brick());
                    }
                }
            }
        }
        for (int i = 1; i <= n16; ++i) {
            double d5 = (double)(n13 - i * n14) + (double)n14 / 2.0 + 0.5;
            if (i == n16) break;
            int n20 = (int)(Math.PI * 2 * d5 / 11.0);
            int n21 = -n15 * i;
            for (int j = 0; j < n20; ++j) {
                boolean bl;
                double d6 = ((double)j + 0.5) * Math.PI * 2.0 / (double)n20 + (double)i * 0.37;
                if (Math.abs(Math.sin(2.0 * d6)) < 0.35) continue;
                n8 = (int)Math.round(Math.cos(d6) * (d5 + 0.5));
                int n22 = (int)Math.round(Math.sin(d6) * (d5 + 0.5));
                n3 = (j + i) % 6;
                if (n3 == 0) {
                    this.statue(n8, n21, n22, AncientStructures.dirTo(-n8, -n22));
                    continue;
                }
                if (n3 == 1) {
                    this.forgeShop(n8, n21, n22, AncientStructures.dirTo(-n8, -n22));
                    continue;
                }
                if (n3 == 2) {
                    double d7 = Math.atan2(n22, n8);
                    for (n6 = 0; n6 < 9; ++n6) {
                        n5 = (int)Math.round(Math.cos(d7) * (d5 + 3.0 + (double)n6));
                        n4 = (int)Math.round(Math.sin(d7) * (d5 + 3.0 + (double)n6));
                        for (int k = 0; k < 3; ++k) {
                            for (int i2 = -1; i2 <= 1; ++i2) {
                                this.g.air(n5 + (int)Math.round(-Math.sin(d7) * (double)i2), n21 + k, n4 + (int)Math.round(Math.cos(d7) * (double)i2));
                            }
                        }
                        this.g.set(n5, n21, n4, Gothic.s(Blocks.RAIL));
                        this.g.set(n5, n21 - 1, n4, Gothic.s(Blocks.COBBLESTONE));
                        if (n6 % 3 != 0) continue;
                        this.g.set(n5, n21 + 2, n4, (BlockState)Blocks.LANTERN.defaultBlockState().setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(true)));
                    }
                    continue;
                }
                boolean bl2 = bl = Math.abs(Math.sin(d6)) > 0.7;
                this.dwarfHouse(n8, n21, n22, bl, bl ? (n22 > 0 ? Direction.NORTH : Direction.SOUTH) : (n8 > 0 ? Direction.WEST : Direction.EAST));
            }
        }
        double d8 = (double)n13 + 5.5;
        int n23 = (int)(Math.PI * 2 * d8 / 10.0);
        for (n2 = 0; n2 < n23; ++n2) {
            double d9 = ((double)n2 + 0.5) * Math.PI * 2.0 / (double)n23;
            int n24 = (int)Math.round(Math.cos(d9) * d8);
            n9 = (int)Math.round(Math.sin(d9) * d8);
            if (Math.abs(n24) <= 5 || Math.abs(n9) <= 5 || !this.onDisc(n24 + 4, n9 + 4) || !this.onDisc(n24 - 4, n9 - 4)) continue;
            int n25 = n8 = Math.abs(Math.sin(d9)) > 0.7 ? 1 : 0;
            if (n2 % 5 == 2) {
                this.forgeShop(n24, 0, n9, AncientStructures.dirTo(-n24, -n9));
                continue;
            }
            this.dwarfHouse(n24, 0, n9, n8 != 0, n8 != 0 ? (n9 > 0 ? Direction.NORTH : Direction.SOUTH) : (n24 > 0 ? Direction.WEST : Direction.EAST));
        }
        n2 = -n15 * n16;
        int n26 = 4;
        double d10 = n13 - n16 * n14 + n14;
        for (n9 = -12; n9 <= 12; ++n9) {
            for (n8 = -12; n8 <= 12; ++n8) {
                double d11 = Math.sqrt(n9 * n9 + n8 * n8);
                if (!(d11 > (double)n26 + 0.5) || !(d11 <= (double)n26 + 2.2)) continue;
                this.g.set(n9, n2 - 1, n8, Gothic.s(Blocks.LAVA));
                this.g.set(n9, n2 - 2, n8, Gothic.s(Blocks.MAGMA_BLOCK));
            }
        }
        n9 = -n2 + 14;
        this.g.tower(0, 0, n2, n26, n9, false);
        this.g.spiralStair(0, 0, n2 + n9 - 2, n2, 1);
        Direction[] directionArray = H4;
        int n27 = directionArray.length;
        for (n3 = 0; n3 < n27; ++n3) {
            Direction direction = directionArray[n3];
            for (n7 = 0; n7 <= 2; ++n7) {
                this.g.air(direction.getStepX() * n26, n2 + n7, direction.getStepZ() * n26);
                this.g.air(direction.getStepX() * n26, n7, direction.getStepZ() * n26);
            }
            for (n7 = n26 + 1; n7 <= n26 + 2; ++n7) {
                this.g.set(direction.getStepX() * n7, n2 - 1, direction.getStepZ() * n7, Gothic.s(this.g.pal.polished()));
            }
            this.bridge(direction.getStepX() * (n13 + 1), 0, direction.getStepZ() * (n13 + 1), direction.getStepX() * (n26 + 1), 0, direction.getStepZ() * (n26 + 1));
        }
        for (n = 0; n <= 2; ++n) {
            for (n27 = -n26 + 1; n27 <= n26 - 1; ++n27) {
                for (n3 = -n26 + 1; n3 <= n26 - 1; ++n3) {
                    if (n27 * n27 + n3 * n3 >= (n26 - 1) * (n26 - 1) || Math.abs(n27) <= 1 && Math.abs(n3) <= 1) continue;
                    this.g.set(n27, -1, n3, Gothic.s(Blocks.SPRUCE_PLANKS));
                }
            }
        }
        this.g.set(2, n2, 2, Gothic.s(Blocks.ANVIL));
        this.g.set(-2, n2, 2, Gothic.s(Blocks.BLAST_FURNACE));
        this.g.set(2, n2, -2, Gothic.s(Blocks.SMITHING_TABLE));
        this.g.chest(-2, n2, -2, true);
        for (n = 0; n < 8; ++n) {
            double d12 = (double)n * Math.PI / 4.0 + 0.39269908169872414;
            int n28 = (int)Math.round(Math.cos(d12) * (d10 - 2.5));
            n7 = (int)Math.round(Math.sin(d12) * (d10 - 2.5));
            if (n % 2 == 0) {
                this.brazier(n28, n2, n7);
                continue;
            }
            this.marketStall(n28, n2, n7, n);
        }
        for (n = -n13 - 2; n <= n13 + 2; ++n) {
            for (int i = -n13 - 2; i <= n13 + 2; ++i) {
                double d13 = Math.sqrt(n * n + i * i);
                if (d13 < (double)n13 + 0.5 || d13 > (double)n13 + 1.5 || !this.onDisc(n, i)) continue;
                n7 = Math.abs(n) <= 2 || Math.abs(i) <= 2 ? 1 : 0;
                this.g.set(n, 0, i, n7 != 0 ? Gothic.s(Blocks.AIR) : Gothic.s(this.g.pal.wall()));
                if (n7 != 0 || Math.floorMod(n * 7 + i * 3, 13) != 0) continue;
                this.brazier(n, 0, i);
            }
        }
        for (int n29 : new int[]{-1, 1}) {
            n7 = n29 * (this.R - 3);
            if (!this.onDisc(0, n7)) continue;
            for (n6 = -8; n6 <= 8; ++n6) {
                for (n5 = 0; n5 <= 19; ++n5) {
                    this.g.set(n6, n5, n7 + n29, this.g.brick());
                }
            }
            this.relief(DWARF_FACE, 19, 0, n7, n29 > 0 ? Direction.NORTH : Direction.SOUTH, 1, 0, false);
        }
    }

    private static Direction dirTo(int n, int n2) {
        if (Math.abs(n) >= Math.abs(n2)) {
            return n >= 0 ? Direction.EAST : Direction.WEST;
        }
        return n2 >= 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private void marketStall(int n, int n2, int n3, int n4) {
        Block[] blockArray = new Block[]{Blocks.RED_WOOL, Blocks.ORANGE_WOOL, Blocks.YELLOW_WOOL, Blocks.BLUE_WOOL};
        int[][] nArrayArray = new int[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
        int n5 = nArrayArray.length;
        for (int i = 0; i < n5; ++i) {
            int[] nArray = nArrayArray[i];
            for (int j = 0; j < 3; ++j) {
                this.g.set(n + nArray[0], n2 + j, n3 + nArray[1], Gothic.s(Blocks.SPRUCE_FENCE));
            }
        }
        for (int i = -1; i <= 1; ++i) {
            for (n5 = -1; n5 <= 1; ++n5) {
                this.g.set(n + i, n2 + 3, n3 + n5, Gothic.s(blockArray[n4 % blockArray.length]));
            }
        }
        this.g.set(n, n2, n3, Gothic.s(Blocks.BARREL));
    }

    private void weather() {
        this.g.crumble(-this.R, 0, -this.R, this.R, this.R + 20, this.R, this.g.decay * 0.06f);
        for (int i = 0; i < this.R * 2; ++i) {
            BlockState blockState;
            int n;
            int n2 = -this.R + this.r.nextInt(2 * this.R + 1);
            if (!this.onDisc(n2, n = -this.R + this.r.nextInt(2 * this.R + 1)) || !this.g.isAir(n2, 0, n) || this.g.get(n2, 0, n) == null || (blockState = this.g.get(n2, -1, n)) == null || blockState.isAir()) continue;
            float f = this.r.nextFloat();
            if (f < 0.3f) {
                this.g.set(n2, 0, n, Gothic.s(Blocks.BONE_BLOCK));
                continue;
            }
            if (f < 0.5f) {
                this.g.set(n2, -1, n, Gothic.s(Blocks.SOUL_SOIL));
                this.g.set(n2, 0, n, Gothic.s(Blocks.SOUL_FIRE));
                continue;
            }
            if (f < 0.7f) {
                this.g.set(n2, 0, n, Gothic.s(Blocks.COBWEB));
                continue;
            }
            if (f < 0.85f && (blockState.is(Blocks.PODZOL) || blockState.is(Blocks.COARSE_DIRT))) {
                this.g.set(n2, 0, n, Gothic.s(Blocks.DEAD_BUSH));
                continue;
            }
            this.g.set(n2, 0, n, Gothic.s(Blocks.SKELETON_SKULL));
        }
    }

    public static enum Kind {
        SHRINE("shrine", 10),
        TEMPLE("temple", 15),
        CITY("city", 19),
        COLOSSAL_CITY("colossal_city", 26),
        TITAN("titan", 44);

        public final String id;
        public final int radius;

        private Kind(String string2, int n2) {
            this.id = string2;
            this.radius = n2;
        }

        public static Kind forRadius(double d) {
            return d < 3.4 ? SHRINE : (d < 6.5 ? TEMPLE : (d < 10.5 ? CITY : (d < 14.25 ? COLOSSAL_CITY : TITAN)));
        }
    }

    public record Result(UndergroundCity.Plan plan, Kind kind, String paletteId, String variantId) {
    }
}
