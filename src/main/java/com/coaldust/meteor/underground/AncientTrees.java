/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.coaldust.meteor.underground;

import com.coaldust.meteor.underground.Flora;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public final class AncientTrees {
    public static Shape random(RandomSource randomSource) {
        return Shape.values()[randomSource.nextInt(Shape.values().length)];
    }

    public static int grow(Setter setter, RandomSource randomSource, int n, int n2, int n3, Shape shape, double d) {
        return switch (shape.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> AncientTrees.round(setter, randomSource, n, n2, n3, d);
            case 1 -> AncientTrees.weeping(setter, randomSource, n, n2, n3, d);
            case 2 -> AncientTrees.spire(setter, randomSource, n, n2, n3, d);
            case 3 -> AncientTrees.umbrella(setter, randomSource, n, n2, n3, d);
            case 4 -> AncientTrees.twin(setter, randomSource, n, n2, n3, d);
            case 5 -> AncientTrees.gnarled(setter, randomSource, n, n2, n3, d);
        };
    }

    private static BlockState log() {
        return Flora.LOG.get();
    }

    private static BlockState log(Direction.Axis axis) {
        return (BlockState)Flora.LOG.get().setValue((Property)BlockStateProperties.AXIS, (Comparable)axis);
    }

    private static BlockState leaf(RandomSource randomSource, float f) {
        return randomSource.nextFloat() < f ? Flora.BLOSSOM.get() : Flora.LEAVES.get();
    }

    private static void leafAt(Setter setter, RandomSource randomSource, int n, int n2, int n3, float f) {
        if (setter.free(n, n2, n3)) {
            setter.set(n, n2, n3, AncientTrees.leaf(randomSource, f));
        }
    }

    private static void roots(Setter setter, RandomSource randomSource, int n, int n2, int n3, int n4) {
        int[][] nArrayArray;
        setter.set(n, n2 - 1, n3, Blocks.ROOTED_DIRT.defaultBlockState());
        for (int[] nArray : nArrayArray = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int n5;
            int n6;
            if (randomSource.nextFloat() < 0.25f) continue;
            int n7 = 1 + randomSource.nextInt(n4);
            for (int i = 1; i <= n7 && setter.free(n6 = n + nArray[0] * i, n2, n5 = n3 + nArray[1] * i); ++i) {
                setter.set(n6, n2, n5, i == n7 && n7 > 1 ? AncientTrees.log(nArray[0] != 0 ? Direction.Axis.X : Direction.Axis.Z) : AncientTrees.log());
            }
        }
    }

    private static void blob(Setter setter, RandomSource randomSource, int n, int n2, int n3, double d, double d2, float f, float f2) {
        int n4 = (int)Math.ceil(d);
        int n5 = (int)Math.ceil(d2);
        for (int i = -n4; i <= n4; ++i) {
            for (int j = -n5; j <= n5; ++j) {
                for (int k = -n4; k <= n4; ++k) {
                    double d3 = (double)(i * i + k * k) / (d * d) + (double)(j * j) / (d2 * d2);
                    if (d3 > 1.0 || d3 > 0.7 && randomSource.nextFloat() < f2) continue;
                    AncientTrees.leafAt(setter, randomSource, n + i, n2 + j, n3 + k, f);
                }
            }
        }
    }

    private static int[] trunk(Setter setter, RandomSource randomSource, int n, int n2, int n3, int n4, double d) {
        double d2 = (randomSource.nextDouble() - 0.5) * d;
        double d3 = (randomSource.nextDouble() - 0.5) * d;
        int n5 = n;
        int n6 = n3;
        for (int i = 0; i < n4; ++i) {
            int n7 = n + (int)Math.round(d2 * (double)i);
            int n8 = n3 + (int)Math.round(d3 * (double)i);
            if (n7 != n5 || n8 != n6) {
                setter.set(n5, n2 + i, n6, AncientTrees.log());
            }
            n5 = n7;
            n6 = n8;
            setter.set(n5, n2 + i, n6, AncientTrees.log());
        }
        return new int[]{n5, n6};
    }

    private static int round(Setter setter, RandomSource randomSource, int n, int n2, int n3, double d) {
        AncientTrees.roots(setter, randomSource, n, n2, n3, 2);
        int n4 = 4 + (int)(d * 2.0) + randomSource.nextInt(2);
        int[] nArray = AncientTrees.trunk(setter, randomSource, n, n2, n3, n4, 0.5);
        double d2 = 2.0 + d * 0.9;
        AncientTrees.blob(setter, randomSource, nArray[0], n2 + n4, nArray[1], d2, d2 * 0.75, 0.3f, 0.35f);
        for (int i = 0; i < 3; ++i) {
            AncientTrees.blob(setter, randomSource, nArray[0] + randomSource.nextInt(5) - 2, n2 + n4 + randomSource.nextInt(2), nArray[1] + randomSource.nextInt(5) - 2, d2 * 0.6, d2 * 0.5, 0.45f, 0.3f);
        }
        setter.set(nArray[0], n2 + n4, nArray[1], Flora.LUMEN.get());
        return (int)Math.ceil(d2 + 1.0);
    }

    private static int weeping(Setter setter, RandomSource randomSource, int n, int n2, int n3, double d) {
        AncientTrees.roots(setter, randomSource, n, n2, n3, 3);
        int n4 = 5 + (int)(d * 2.0);
        int[] nArray = AncientTrees.trunk(setter, randomSource, n, n2, n3, n4, 0.3);
        double d2 = 3.0 + d;
        AncientTrees.blob(setter, randomSource, nArray[0], n2 + n4, nArray[1], d2, 1.6, 0.25f, 0.2f);
        int n5 = (int)Math.ceil(d2);
        for (int i = -n5; i <= n5; ++i) {
            for (int j = -n5; j <= n5; ++j) {
                int n6;
                double d3 = Math.sqrt(i * i + j * j);
                if (d3 < d2 - 1.2 || d3 > d2 || randomSource.nextFloat() < 0.45f) continue;
                int n7 = 2 + randomSource.nextInt(n4 - 1);
                for (int k = 1; k <= n7 && (n6 = n2 + n4 - k) > n2 && setter.free(nArray[0] + i, n6, nArray[1] + j); ++k) {
                    setter.set(nArray[0] + i, n6, nArray[1] + j, AncientTrees.leaf(randomSource, k == n7 ? 0.6f : 0.15f));
                }
            }
        }
        setter.set(nArray[0], n2 + n4 + 1, nArray[1], Flora.LUMEN.get());
        return n5 + 1;
    }

    private static int spire(Setter setter, RandomSource randomSource, int n, int n2, int n3, double d) {
        int n4;
        AncientTrees.roots(setter, randomSource, n, n2, n3, 2);
        int n5 = 8 + (int)(d * 3.0);
        for (n4 = 0; n4 < n5; ++n4) {
            setter.set(n, n2 + n4, n3, AncientTrees.log());
        }
        n4 = 2 + randomSource.nextInt(2);
        double d2 = 2.0 + d * 0.8;
        for (int i = n4; i <= n5 + 1; ++i) {
            double d3 = (double)(i - n4) / (double)(n5 + 1 - n4);
            double d4 = d2 * (1.0 - d3) + 0.6;
            if ((i - n4) % 3 == 2) {
                d4 *= 0.55;
            }
            int n6 = (int)Math.ceil(d4);
            for (int j = -n6; j <= n6; ++j) {
                for (int k = -n6; k <= n6; ++k) {
                    if (!((double)(j * j + k * k) <= d4 * d4) || j == 0 && k == 0 && i < n5) continue;
                    AncientTrees.leafAt(setter, randomSource, n + j, n2 + i, n3 + k, (i - n4) % 3 == 0 ? 0.35f : 0.1f);
                }
            }
        }
        setter.set(n, n2 + n5 / 2, n3, Flora.LUMEN.get());
        return (int)Math.ceil(d2 + 1.0);
    }

    private static int umbrella(Setter setter, RandomSource randomSource, int n, int n2, int n3, double d) {
        AncientTrees.roots(setter, randomSource, n, n2, n3, 2);
        int n4 = 5 + (int)(d * 2.0);
        int[] nArray = AncientTrees.trunk(setter, randomSource, n, n2, n3, n4, 0.9);
        int n5 = 1;
        int n6 = 2 + (int)d;
        for (int i = 0; i < n6; ++i) {
            double d2 = randomSource.nextDouble() * Math.PI * 2.0;
            int n7 = 2 + randomSource.nextInt(2 + (int)d);
            int n8 = n2 + n4 - 1 - randomSource.nextInt(3);
            int n9 = nArray[0];
            int n10 = nArray[1];
            for (int j = 1; j <= n7; ++j) {
                n9 = nArray[0] + (int)Math.round(Math.cos(d2) * (double)j);
                n10 = nArray[1] + (int)Math.round(Math.sin(d2) * (double)j);
                if (j % 2 == 0) {
                    ++n8;
                }
                if (!setter.free(n9, n8, n10)) continue;
                setter.set(n9, n8, n10, AncientTrees.log(Math.abs(Math.cos(d2)) > Math.abs(Math.sin(d2)) ? Direction.Axis.X : Direction.Axis.Z));
            }
            double d3 = 1.8 + d * 0.5;
            int n11 = (int)Math.ceil(d3);
            for (int j = -n11; j <= n11; ++j) {
                for (int k = -n11; k <= n11; ++k) {
                    if (!((double)(j * j + k * k) <= d3 * d3)) continue;
                    AncientTrees.leafAt(setter, randomSource, n9 + j, n8 + 1, n10 + k, 0.3f);
                    if (!((double)(j * j + k * k) <= (d3 - 1.0) * (d3 - 1.0))) continue;
                    AncientTrees.leafAt(setter, randomSource, n9 + j, n8 + 2, n10 + k, 0.5f);
                }
            }
            n5 = Math.max(n5, n7 + n11);
        }
        AncientTrees.blob(setter, randomSource, nArray[0], n2 + n4 + 1, nArray[1], 2.0, 1.2, 0.4f, 0.3f);
        setter.set(nArray[0], n2 + n4, nArray[1], Flora.LUMEN.get());
        return n5 + 1;
    }

    private static int twin(Setter setter, RandomSource randomSource, int n, int n2, int n3, double d) {
        AncientTrees.roots(setter, randomSource, n, n2, n3, 2);
        int n4 = 5 + (int)(d * 2.0);
        int n5 = randomSource.nextBoolean() ? 1 : -1;
        boolean bl = randomSource.nextBoolean();
        int[] nArray = AncientTrees.trunk(setter, randomSource, n, n2, n3, 2, 0.0);
        int n6 = 0;
        for (int i = -1; i <= 1; i += 2) {
            int n7 = bl ? i * n5 : 0;
            int n8 = bl ? 0 : i * n5;
            int n9 = nArray[0];
            int n10 = nArray[1];
            int n11 = n4 + (i > 0 ? 1 : -1) + randomSource.nextInt(2);
            for (int j = 2; j < n11; ++j) {
                if (j == 3 || j == 5) {
                    n9 += n7;
                    n10 += n8;
                }
                setter.set(n9, n2 + j, n10, AncientTrees.log());
            }
            double d2 = 1.8 + d * 0.7;
            AncientTrees.blob(setter, randomSource, n9, n2 + n11, n10, d2, d2 * 0.8, 0.35f, 0.3f);
            setter.set(n9, n2 + n11, n10, Flora.LUMEN.get());
            n6 = Math.max(n6, (int)Math.ceil(d2) + 3);
        }
        return n6;
    }

    private static int gnarled(Setter setter, RandomSource randomSource, int n, int n2, int n3, double d) {
        int n4;
        int n5;
        AncientTrees.roots(setter, randomSource, n, n2, n3, 4);
        int n6 = 3 + (int)d;
        for (n5 = 0; n5 < n6; ++n5) {
            int[][] nArrayArray = new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}};
            n4 = nArrayArray.length;
            for (int i = 0; i < n4; ++i) {
                int[] nArray = nArrayArray[i];
                if (n5 >= n6 - 1 && (nArray[0] != 0 || nArray[1] != 0)) continue;
                setter.set(n + nArray[0], n2 + n5, n3 + nArray[1], AncientTrees.log());
            }
        }
        n5 = 2;
        int n7 = 3 + randomSource.nextInt(2);
        for (n4 = 0; n4 < n7; ++n4) {
            double d2 = (double)n4 * Math.PI * 2.0 / (double)n7 + randomSource.nextDouble() * 0.8;
            int n8 = 2 + randomSource.nextInt(3) + (int)d;
            int n9 = n;
            int n10 = n3;
            int n11 = n2 + n6 - 1;
            for (int i = 1; i <= n8; ++i) {
                n9 = n + (int)Math.round(Math.cos(d2) * (double)i);
                n10 = n3 + (int)Math.round(Math.sin(d2) * (double)i);
                if (randomSource.nextFloat() < 0.5f) {
                    ++n11;
                }
                if (!setter.free(n9, n11, n10)) continue;
                setter.set(n9, n11, n10, AncientTrees.log());
            }
            AncientTrees.blob(setter, randomSource, n9, n11 + 1, n10, 1.6 + d * 0.3, 1.3, 0.45f, 0.25f);
            n5 = Math.max(n5, n8 + 2);
        }
        setter.set(n, n2 + n6, n3, Flora.LUMEN.get());
        return n5 + 1;
    }

    private AncientTrees() {
    }

    public static enum Shape {
        ROUND,
        WEEPING,
        SPIRE,
        UMBRELLA,
        TWIN,
        GNARLED;

    }

    public static interface Setter {
        public void set(int var1, int var2, int var3, BlockState var4);

        public boolean free(int var1, int var2, int var3);
    }
}
