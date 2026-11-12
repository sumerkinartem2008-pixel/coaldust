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
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 */
package com.coaldust.meteor.gothic;

import com.coaldust.meteor.underground.UndergroundCity;
import java.util.ArrayList;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;

public final class Gothic {
    public static final Pal OBSIDIAN = new Pal("obsidian", Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.RED_STAINED_GLASS_PANE, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE, Blocks.CRYING_OBSIDIAN, true);
    public static final Pal BONE = new Pal("bone", Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.CHISELED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE, Blocks.BONE_BLOCK, true);
    public static final Pal BLOOD = new Pal("blood", Blocks.RED_NETHER_BRICKS, Blocks.NETHER_BRICKS, Blocks.CHISELED_NETHER_BRICKS, Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICK_WALL, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICK_SLAB, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE, false);
    public static final Pal ASH = new Pal("ash", Blocks.TUFF_BRICKS, Blocks.POLISHED_TUFF, Blocks.CHISELED_TUFF_BRICKS, Blocks.POLISHED_TUFF, Blocks.TUFF_BRICK_STAIRS, Blocks.TUFF_BRICK_SLAB, Blocks.TUFF_BRICK_WALL, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.POLISHED_BASALT, Blocks.SMOOTH_BASALT, Blocks.CHISELED_TUFF, false);
    public static final Pal SCULK = new Pal("sculk", Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES, Blocks.CHISELED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILE_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_TILES, Blocks.CHISELED_POLISHED_BLACKSTONE, true);
    public static final Pal DWARF = new Pal("dwarf", Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS, Blocks.POLISHED_ANDESITE, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICK_WALL, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.POLISHED_ANDESITE, Blocks.DEEPSLATE_TILES, Blocks.WAXED_CUT_COPPER, false);
    public static final Pal DWARF_DEEP = new Pal("dwarf_deep", Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.CHISELED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_TILES, Blocks.WAXED_CHISELED_COPPER, false);
    public static final Pal[] PALETTES = new Pal[]{OBSIDIAN, BONE, BLOOD, ASH, SCULK};
    public final UndergroundCity.Plan p;
    public final RandomSource r;
    public final Pal pal;
    public float decay = 0.15f;

    public static Pal roll(RandomSource randomSource) {
        int n = randomSource.nextInt(100);
        return n < 30 ? OBSIDIAN : (n < 55 ? BONE : (n < 72 ? BLOOD : (n < 87 ? ASH : SCULK)));
    }

    public Gothic(UndergroundCity.Plan plan, RandomSource randomSource, Pal pal) {
        this.p = plan;
        this.r = randomSource;
        this.pal = pal;
    }

    public static BlockState s(Block block) {
        return block.defaultBlockState();
    }

    public static BlockState stairs(Block block, Direction direction, boolean bl) {
        return (BlockState)((BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.HALF, (Comparable)(bl ? Half.TOP : Half.BOTTOM));
    }

    public static BlockState slab(Block block, boolean bl) {
        return (BlockState)block.defaultBlockState().setValue((Property)BlockStateProperties.SLAB_TYPE, (Comparable)(bl ? SlabType.TOP : SlabType.BOTTOM));
    }

    public BlockState lantern(boolean bl) {
        return (BlockState)(this.pal.soul() ? Blocks.SOUL_LANTERN : Blocks.LANTERN).defaultBlockState().setValue((Property)BlockStateProperties.HANGING, (Comparable)Boolean.valueOf(bl));
    }

    public static BlockState candle(int n) {
        return (BlockState)((BlockState)Blocks.CANDLE.defaultBlockState().setValue((Property)BlockStateProperties.LIT, (Comparable)Boolean.valueOf(true))).setValue((Property)BlockStateProperties.CANDLES, (Comparable)Integer.valueOf(Math.max(1, Math.min(4, n))));
    }

    public void set(int n, int n2, int n3, BlockState blockState) {
        this.p.blocks.put(UndergroundCity.Plan.key(n, n2, n3), blockState);
    }

    public BlockState get(int n, int n2, int n3) {
        return this.p.blocks.get(UndergroundCity.Plan.key(n, n2, n3));
    }

    public boolean isAir(int n, int n2, int n3) {
        BlockState blockState = this.get(n, n2, n3);
        return blockState == null || blockState.isAir();
    }

    public boolean solid(int n, int n2, int n3) {
        BlockState blockState = this.get(n, n2, n3);
        return blockState != null && !blockState.isAir() && !blockState.is(Blocks.WATER) && !blockState.is(Blocks.LAVA);
    }

    public void air(int n, int n2, int n3) {
        this.set(n, n2, n3, Gothic.s(Blocks.AIR));
    }

    public void fill(int n, int n2, int n3, int n4, int n5, int n6, BlockState blockState) {
        for (int i = Math.min(n, n4); i <= Math.max(n, n4); ++i) {
            for (int j = Math.min(n2, n5); j <= Math.max(n2, n5); ++j) {
                for (int k = Math.min(n3, n6); k <= Math.max(n3, n6); ++k) {
                    this.set(i, j, k, blockState);
                }
            }
        }
    }

    public void clear(int n, int n2, int n3, int n4, int n5, int n6) {
        this.fill(n, n2, n3, n4, n5, n6, Gothic.s(Blocks.AIR));
    }

    public BlockState brick() {
        return this.r.nextFloat() < 0.12f + this.decay * 0.5f ? Gothic.s(this.pal.cracked()) : Gothic.s(this.pal.brick());
    }

    public BlockState floorTile(int n, int n2) {
        return (n + n2 & 1) == 0 ? Gothic.s(this.pal.floor()) : Gothic.s(this.pal.floorAlt());
    }

    public void hall(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        for (int i = n; i <= n3; ++i) {
            for (int j = n2; j <= n4; ++j) {
                boolean bl = i == n || i == n3 || j == n2 || j == n4;
                this.set(i, n5 - 1, j, this.floorTile(i, j));
                for (int k = n5; k < n5 + n6; ++k) {
                    boolean bl2;
                    if (!bl) {
                        this.air(i, k, j);
                        continue;
                    }
                    boolean bl3 = bl2 = !((i != n && i != n3 || (j - n2) % n7 != 0 && j != n4) && (j != n2 && j != n4 || (i - n) % n7 != 0 && i != n3));
                    this.set(i, k, j, bl2 ? (k == n5 || k == n5 + n6 - 1 ? Gothic.s(this.pal.chiseled()) : Gothic.s(this.pal.polished())) : this.brick());
                }
                if (!bl) continue;
                this.set(i, n5 + n6, j, Gothic.s(this.pal.polished()));
            }
        }
    }

    public void window(int n, int n2, int n3, Direction.Axis axis, int n4, boolean bl) {
        int n5 = bl ? 1 : 0;
        for (int i = -n5; i <= n5; ++i) {
            int n6 = axis == Direction.Axis.X ? n + i : n;
            int n7 = axis == Direction.Axis.Z ? n3 + i : n3;
            int n8 = n2 + n4 - (i == 0 ? 0 : 1);
            for (int j = n2; j < n8; ++j) {
                this.set(n6, j, n7, Gothic.s(this.pal.glass()));
            }
            this.set(n6, n2 - 1, n7, Gothic.s(this.pal.chiseled()));
        }
        this.set(n, n2 + n4, n3, Gothic.s(this.pal.chiseled()));
    }

    public void windowRow(int n, int n2, int n3, int n4, int n5, int n6, Direction.Axis axis) {
        for (int i = Math.min(n, n2) + n6 / 2; i <= Math.max(n, n2) - 1; i += n6) {
            if (axis == Direction.Axis.X) {
                this.window(i, n4, n3, axis, n5, false);
                continue;
            }
            this.window(n3, n4, i, axis, n5, false);
        }
    }

    public void doorway(int n, int n2, int n3, Direction.Axis axis, int n4) {
        for (int i = -1; i <= 1; ++i) {
            int n5 = axis == Direction.Axis.X ? n + i : n;
            int n6 = axis == Direction.Axis.Z ? n3 + i : n3;
            int n7 = n2 + n4 - (i == 0 ? 0 : 1);
            for (int j = n2; j < n7; ++j) {
                this.air(n5, j, n6);
            }
            this.set(n5, n7, n6, Gothic.s(this.pal.chiseled()));
        }
    }

    public void rose(int n, int n2, int n3, Direction.Axis axis, int n4) {
        for (int i = -n4; i <= n4; ++i) {
            for (int j = -n4; j <= n4; ++j) {
                boolean bl;
                double d = Math.sqrt(i * i + j * j);
                if (d > (double)n4 + 0.3) continue;
                int n5 = axis == Direction.Axis.X ? n + i : n3;
                int n6 = axis == Direction.Axis.Z ? n + i : n3;
                int n7 = n2 + j;
                boolean bl2 = d > (double)n4 - 0.8;
                boolean bl3 = bl = Math.abs(i) == 0 || Math.abs(j) == 0 || Math.abs(Math.abs(i) - Math.abs(j)) == 0;
                this.set(n5, n7, n6, bl2 ? Gothic.s(this.pal.chiseled()) : (bl && d > 0.5 ? Gothic.s(Blocks.IRON_BARS) : Gothic.s(this.pal.glass())));
            }
        }
    }

    public void pillar(int n, int n2, int n3, int n4) {
        for (int i = n3; i < n3 + n4; ++i) {
            this.set(n, i, n2, i == n3 ? Gothic.s(this.pal.chiseled()) : (i == n3 + n4 - 1 ? Gothic.s(this.pal.chiseled()) : Gothic.s(this.pal.polished())));
        }
    }

    public void buttress(int n, int n2, Direction direction, int n3, int n4) {
        int n5 = direction.getStepX();
        int n6 = direction.getStepZ();
        for (int i = 1; i <= 2; ++i) {
            int n7 = n3 + n4 - 1 - (i - 1) * (n4 / 3);
            for (int j = n3; j <= n7; ++j) {
                this.set(n + n5 * i, j, n2 + n6 * i, i == 2 && j == n3 ? Gothic.s(this.pal.chiseled()) : this.brick());
            }
            this.set(n + n5 * i, n7 + 1, n2 + n6 * i, Gothic.stairs(this.pal.stairs(), direction.getOpposite(), false));
        }
        this.set(n + n5, n3 + n4 + 1, n2 + n6, Gothic.s(this.pal.wall()));
        this.set(n + n5, n3 + n4 + 2, n2 + n6, Gothic.s(Blocks.IRON_BARS));
    }

    public int gable(int n, int n2, int n3, int n4, int n5, boolean bl) {
        int n6 = n5;
        int n7 = 0;
        while (true) {
            int n8;
            int n9;
            int n10;
            int n11;
            int n12;
            int n13 = n5 + n7;
            int n14 = (bl ? n2 : n) - 1 + n7;
            if (n14 > (n12 = (bl ? n4 : n3) + 1 - n7)) break;
            n6 = n13;
            int n15 = (bl ? n : n2) - 1;
            int n16 = (bl ? n3 : n4) + 1;
            for (n11 = n15; n11 <= n16; ++n11) {
                int n17 = n10 = n11 == n15 + 1 || n11 == n16 - 1 ? 1 : 0;
                if (n14 < n12) {
                    if (bl) {
                        this.set(n11, n13, n14, Gothic.stairs(this.pal.roofStairs(), Direction.SOUTH, false));
                        this.set(n11, n13, n12, Gothic.stairs(this.pal.roofStairs(), Direction.NORTH, false));
                        for (n9 = n14 + 1; n9 < n12; ++n9) {
                            if (n10 == 0) continue;
                            this.set(n11, n13, n9, this.brick());
                        }
                        continue;
                    }
                    this.set(n14, n13, n11, Gothic.stairs(this.pal.roofStairs(), Direction.EAST, false));
                    this.set(n12, n13, n11, Gothic.stairs(this.pal.roofStairs(), Direction.WEST, false));
                    for (n9 = n14 + 1; n9 < n12; ++n9) {
                        if (n10 == 0) continue;
                        this.set(n9, n13, n11, this.brick());
                    }
                    continue;
                }
                n9 = bl ? n11 : n14;
                n8 = bl ? n14 : n11;
                this.set(n9, n13, n8, Gothic.s(this.pal.roof()));
                this.set(n9, n13 + 1, n8, n11 % 3 == 0 ? Gothic.s(Blocks.IRON_BARS) : Gothic.s(this.pal.wall()));
            }
            if (n14 == n12) break;
            if (n14 + 1 == n12) {
                n6 = n13 + 1;
                for (n11 = n15; n11 <= n16; ++n11) {
                    n10 = bl ? n11 : n14;
                    n9 = bl ? n14 : n11;
                    n8 = bl ? n11 : n12;
                    int n18 = bl ? n12 : n11;
                    this.set(n10, n13 + 1, n9, Gothic.slab(this.pal.roofSlab(), false));
                    this.set(n8, n13 + 1, n18, Gothic.slab(this.pal.roofSlab(), false));
                }
                break;
            }
            ++n7;
        }
        return n6;
    }

    public void spire(int n, int n2, int n3, int n4, int n5) {
        int n6;
        int n7 = n3;
        for (n6 = n4; n6 >= 0; --n6) {
            int n8 = Math.max(1, n5 / (n4 + 1));
            int n9 = 0;
            while (n9 < n8) {
                for (int i = n - n6; i <= n + n6; ++i) {
                    for (int j = n2 - n6; j <= n2 + n6; ++j) {
                        boolean bl;
                        boolean bl2 = bl = Math.abs(i - n) == n6 || Math.abs(j - n2) == n6;
                        if (!bl && n6 > 0 && n9 < n8 - 1) continue;
                        if (n9 == n8 - 1 && n6 > 0 && bl) {
                            Direction direction = Math.abs(i - n) == n6 ? (i > n ? Direction.WEST : Direction.EAST) : (j > n2 ? Direction.NORTH : Direction.SOUTH);
                            this.set(i, n7, j, Gothic.stairs(this.pal.roofStairs(), direction, false));
                            continue;
                        }
                        this.set(i, n7, j, Gothic.s(this.pal.roof()));
                    }
                }
                ++n9;
                ++n7;
            }
        }
        for (n6 = 0; n6 < 3 + n4; ++n6) {
            this.set(n, n7 + n6, n2, n6 < 1 + n4 / 2 ? Gothic.s(this.pal.wall()) : Gothic.s(Blocks.IRON_BARS));
        }
        this.set(n, n7 + 3 + n4, n2, Gothic.s(Blocks.LIGHTNING_ROD));
    }

    public void tower(int n, int n2, int n3, int n4, int n5, boolean bl) {
        double d;
        int n6;
        int n7;
        for (n7 = n - n4; n7 <= n + n4; ++n7) {
            for (n6 = n2 - n4; n6 <= n2 + n4; ++n6) {
                d = Math.sqrt((n7 - n) * (n7 - n) + (n6 - n2) * (n6 - n2));
                if (d > (double)n4 + 0.4) continue;
                this.set(n7, n3 - 1, n6, Gothic.s(this.pal.polished()));
                boolean bl2 = d > (double)n4 - 0.6;
                for (int i = n3; i < n3 + n5; ++i) {
                    boolean bl3;
                    if (!bl2) {
                        this.air(n7, i, n6);
                        continue;
                    }
                    boolean bl4 = bl3 = (i - n3) % 5 == 3 && (n7 == n || n6 == n2);
                    this.set(n7, i, n6, bl3 ? Gothic.s(this.pal.glass()) : ((i - n3) % 6 == 0 ? Gothic.s(this.pal.polished()) : this.brick()));
                }
            }
        }
        if (bl) {
            for (n7 = n - n4 - 1; n7 <= n + n4 + 1; ++n7) {
                for (n6 = n2 - n4 - 1; n6 <= n2 + n4 + 1; ++n6) {
                    d = Math.sqrt((n7 - n) * (n7 - n) + (n6 - n2) * (n6 - n2));
                    if (!(d > (double)n4 + 0.4) || !(d <= (double)n4 + 1.4)) continue;
                    this.set(n7, n3 + n5 - 1, n6, Gothic.slab(this.pal.slab(), true));
                }
            }
            this.spire(n, n2, n3 + n5, n4, n4 * 3 + 4);
        } else {
            for (n7 = n - n4 - 1; n7 <= n + n4 + 1; ++n7) {
                for (n6 = n2 - n4 - 1; n6 <= n2 + n4 + 1; ++n6) {
                    d = Math.sqrt((n7 - n) * (n7 - n) + (n6 - n2) * (n6 - n2));
                    if (d > (double)n4 + 1.4) continue;
                    if (d > (double)n4 - 0.6) {
                        this.set(n7, n3 + n5, n6, Gothic.s(this.pal.polished()));
                        if ((n7 + n6 & 1) != 0) continue;
                        this.set(n7, n3 + n5 + 1, n6, this.brick());
                        continue;
                    }
                    this.set(n7, n3 + n5, n6, Gothic.s(this.pal.floor()));
                }
            }
            this.set(n, n3 + n5 + 1, n2, Gothic.s(this.pal.soul() ? Blocks.SOUL_CAMPFIRE : Blocks.CAMPFIRE));
        }
    }

    public void gargoyle(int n, int n2, int n3, Direction direction) {
        this.set(n, n2, n3, Gothic.stairs(this.pal.stairs(), direction.getOpposite(), true));
        this.set(n, n2 + 1, n3, Gothic.s(this.r.nextFloat() < 0.3f ? Blocks.WITHER_SKELETON_SKULL : Blocks.SKELETON_SKULL));
    }

    public void chainLantern(int n, int n2, int n3, int n4) {
        for (int i = 0; i < n4; ++i) {
            this.set(n, n2 - i, n3, Gothic.s(Blocks.CHAIN));
        }
        this.set(n, n2 - n4, n3, this.lantern(true));
    }

    public void deadTree(int n, int n2, int n3, int n4) {
        Block block = this.r.nextBoolean() ? Blocks.DARK_OAK_LOG : Blocks.STRIPPED_DARK_OAK_LOG;
        int n5 = n;
        int n6 = n3;
        for (int i = 0; i < n4; ++i) {
            this.set(n5, n2 + i, n6, Gothic.s(block));
            if (i > 1 && this.r.nextFloat() < 0.25f) {
                this.set(n5 += this.r.nextInt(3) - 1, n2 + i, n6 += this.r.nextInt(3) - 1, Gothic.s(block));
            }
            if (i < n4 / 2 || !(this.r.nextFloat() < 0.5f)) continue;
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(this.r);
            int n7 = n5;
            int n8 = n6;
            int n9 = n2 + i;
            for (int j = 0; j < 2 + this.r.nextInt(3); ++j) {
                n7 += direction.getStepX();
                n8 += direction.getStepZ();
                if (this.r.nextBoolean()) {
                    ++n9;
                }
                this.set(n7, n9, n8, Gothic.s(Blocks.DARK_OAK_FENCE));
            }
        }
        this.set(n5, n2 + n4, n6, Gothic.s(Blocks.DARK_OAK_FENCE));
        if (this.r.nextFloat() < 0.4f) {
            this.set(n5 + 1, n2 + n4 - 2, n6, Gothic.s(Blocks.COBWEB));
        }
    }

    public void grave(int n, int n2, int n3, Direction direction) {
        float f = this.r.nextFloat();
        this.set(n, n2 - 1, n3, Gothic.s(Blocks.COARSE_DIRT));
        if (f < 0.4f) {
            this.set(n, n2, n3, Gothic.s(Blocks.POLISHED_DEEPSLATE_WALL));
            this.set(n, n2 + 1, n3, Gothic.slab(Blocks.POLISHED_DEEPSLATE_SLAB, false));
        } else if (f < 0.75f) {
            this.set(n, n2, n3, Gothic.s(Blocks.COBBLED_DEEPSLATE_WALL));
            this.set(n, n2 + 1, n3, Gothic.s(Blocks.COBBLED_DEEPSLATE_WALL));
            this.set(n + direction.getClockWise().getStepX(), n2 + 1, n3 + direction.getClockWise().getStepZ(), Gothic.s(Blocks.STONE_BUTTON));
        } else {
            this.set(n, n2, n3, Gothic.stairs(this.pal.stairs(), direction, false));
        }
        int n4 = n + direction.getStepX();
        int n5 = n3 + direction.getStepZ();
        this.set(n4, n2 - 1, n5, Gothic.s(Blocks.PODZOL));
        if (this.r.nextFloat() < 0.35f) {
            this.set(n4, n2, n5, Gothic.candle(1 + this.r.nextInt(3)));
        } else if (this.r.nextFloat() < 0.3f) {
            this.set(n4, n2, n5, Gothic.s(Blocks.WITHER_ROSE));
        }
    }

    public void cobwebs(int n, int n2, int n3, int n4, int n5, int n6, float f) {
        for (int i = n; i <= n4; ++i) {
            for (int j = n2; j <= n5; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    int n7;
                    if (!this.isAir(i, j, k) || this.get(i, j, k) == null || (n7 = (this.solid(i + 1, j, k) ? 1 : 0) + (this.solid(i - 1, j, k) ? 1 : 0) + (this.solid(i, j, k + 1) ? 1 : 0) + (this.solid(i, j, k - 1) ? 1 : 0) + (this.solid(i, j + 1, k) ? 1 : 0)) < 3 || !(this.r.nextFloat() < f)) continue;
                    this.set(i, j, k, Gothic.s(Blocks.COBWEB));
                }
            }
        }
    }

    public void altar(int n, int n2, int n3) {
        this.set(n, n2, n3, Gothic.s(this.pal.chiseled()));
        this.set(n, n2 + 1, n3, Gothic.s(this.pal.trim()));
        this.set(n, n2 + 2, n3, Gothic.s(Blocks.WITHER_SKELETON_SKULL));
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            this.set(n + direction.getStepX(), n2, n3 + direction.getStepZ(), Gothic.stairs(this.pal.stairs(), direction.getOpposite(), false));
            this.set(n + direction.getStepX() * 2, n2, n3 + direction.getStepZ() * 2, Gothic.candle(2 + this.r.nextInt(3)));
        }
    }

    public void sarcophagus(int n, int n2, int n3, boolean bl) {
        for (int i = 0; i < 3; ++i) {
            int n4 = bl ? n + i : n;
            int n5 = bl ? n3 : n3 + i;
            this.set(n4, n2, n5, Gothic.s(this.pal.polished()));
            this.set(n4, n2 + 1, n5, Gothic.slab(this.pal.slab(), false));
        }
        this.set(n, n2 + 1, n3, Gothic.s(Blocks.SKELETON_SKULL));
    }

    public void crumble(int n, int n2, int n3, int n4, int n5, int n6, float f) {
        for (int i = n; i <= n4; ++i) {
            for (int j = n2; j <= n5; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    int n7;
                    BlockState blockState = this.get(i, j, k);
                    if (blockState == null || blockState.isAir() || !blockState.is(this.pal.brick()) && !blockState.is(this.pal.cracked()) || !(this.r.nextFloat() < f)) continue;
                    this.air(i, j, k);
                    for (n7 = j; n7 > n2 && this.isAir(i, n7 - 1, k) && this.get(i, n7 - 1, k) != null; --n7) {
                    }
                    if (n7 == j || !(this.r.nextFloat() < 0.5f)) continue;
                    this.set(i, n7, k, Gothic.s(Blocks.COBBLED_DEEPSLATE));
                }
            }
        }
    }

    public void chest(int n, int n2, int n3, boolean bl) {
        this.set(n, n2, n3, Gothic.s(Blocks.CHEST));
        this.p.chests.add(new int[]{n, n2, n3});
        this.p.richChests.add(bl);
    }

    public void spiralStair(int n, int n2, int n3, int n4, int n5) {
        int n6;
        int[][] nArray = Gothic.ringCells(n5);
        int n7 = nArray.length;
        for (n6 = 0; n6 < 2; ++n6) {
            int n8 = 0;
            int n9 = n3;
            while (n9 >= n4) {
                int[] nArray2 = nArray[n8 % n7];
                int[] nArray3 = nArray[(n8 + 1) % n7];
                Direction direction = Gothic.dirTo(nArray3[0] - nArray2[0], nArray3[1] - nArray2[1]).getOpposite();
                for (int i = 0; i < 2; ++i) {
                    int n10 = n + nArray2[0] + (i == 1 ? Integer.signum(nArray2[0]) : 0);
                    int n11 = n2 + nArray2[1] + (i == 1 ? Integer.signum(nArray2[1]) : 0);
                    if (n6 == 0) {
                        for (int j = 1; j <= 3; ++j) {
                            this.air(n10, n9 + j, n11);
                        }
                        continue;
                    }
                    this.set(n10, n9, n11, Gothic.stairs(this.pal.stairs(), direction, false));
                    this.set(n10, n9 - 1, n11, this.brick());
                    if (i != 1 || nArray2[0] == 0 || nArray2[1] == 0) continue;
                    this.set(n10, n9 - 1, n11 - Integer.signum(nArray2[1]), this.brick());
                }
                --n9;
                ++n8;
            }
        }
        for (n6 = n4; n6 <= n3 + 2; ++n6) {
            this.set(n, n6, n2, Gothic.s(this.pal.polished()));
        }
        for (n6 = n3 - 2; n6 >= n4; n6 -= 6) {
            this.set(n, n6, n2, Gothic.s(Blocks.SHROOMLIGHT));
        }
    }

    private static Direction dirTo(int n, int n2) {
        if (Math.abs(n) >= Math.abs(n2)) {
            return n > 0 ? Direction.EAST : Direction.WEST;
        }
        return n2 > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static int[][] ringCells(int n) {
        ArrayList<int[]> arrayList = new ArrayList<int[]>();
        int n2 = -n;
        while (n2 < n) {
            arrayList.add(new int[]{n2++, -n});
        }
        n2 = -n;
        while (n2 < n) {
            arrayList.add(new int[]{n, n2++});
        }
        n2 = n;
        while (n2 > -n) {
            arrayList.add(new int[]{n2--, n});
        }
        n2 = n;
        while (n2 > -n) {
            arrayList.add(new int[]{-n, n2--});
        }
        return (int[][])arrayList.toArray((T[])new int[0][]);
    }

    public record Pal(String id, Block brick, Block cracked, Block chiseled, Block polished, Block stairs, Block slab, Block wall, Block roof, Block roofStairs, Block roofSlab, Block glass, Block floor, Block floorAlt, Block trim, boolean soul) {
    }
}
