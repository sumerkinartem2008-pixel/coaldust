/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.RandomSource
 */
package com.coaldust.meteor;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.RandomSource;

public final class MeteorShape {
    public final Kind kind;
    public final double radius;
    private double sx = 1.0;
    private double sy = 1.0;
    private double sz = 1.0;
    private final List<double[]> parts = new ArrayList<double[]>();
    private double[] axis = new double[]{1.0, 0.0, 0.0};

    private MeteorShape(Kind kind, double d, RandomSource randomSource) {
        this.kind = kind;
        this.radius = d;
        switch (kind.ordinal()) {
            case 1: {
                this.stretch(randomSource, 1.35 + randomSource.nextDouble() * 0.35, 0.75 + randomSource.nextDouble() * 0.1);
                break;
            }
            case 3: {
                this.stretch(randomSource, 2.0 + randomSource.nextDouble() * 0.6, 0.45 + randomSource.nextDouble() * 0.15);
                break;
            }
            case 4: {
                this.sx = 1.25;
                this.sz = 1.25;
                this.sy = 0.55;
                break;
            }
            case 2: {
                for (int i = 0; i < 4; ++i) {
                    double[] dArray = MeteorShape.randomUnit(randomSource);
                    this.parts.add(new double[]{dArray[0], dArray[1], dArray[2], 0.1 + randomSource.nextDouble() * 0.12});
                }
                break;
            }
            case 5: {
                this.parts.add(new double[]{0.0, 0.0, 0.0, d * 0.8});
                int n = 2 + randomSource.nextInt(2);
                for (int i = 0; i < n; ++i) {
                    double[] dArray = MeteorShape.randomUnit(randomSource);
                    double d2 = d * (0.85 + randomSource.nextDouble() * 0.25);
                    this.parts.add(new double[]{dArray[0] * d2, dArray[1] * d2 * 0.6, dArray[2] * d2, d * (0.45 + randomSource.nextDouble() * 0.2)});
                }
                break;
            }
            case 6: {
                int n = 3 + randomSource.nextInt(3);
                for (int i = 0; i < n; ++i) {
                    double[] dArray = MeteorShape.randomUnit(randomSource);
                    this.parts.add(new double[]{dArray[0], dArray[1], dArray[2], d * (1.5 + randomSource.nextDouble() * 0.5)});
                }
                break;
            }
            case 7: {
                double d3 = randomSource.nextDouble() * Math.PI * 2.0;
                this.axis = new double[]{Math.cos(d3), (randomSource.nextDouble() - 0.5) * 0.4, Math.sin(d3)};
                MeteorShape.normalize(this.axis);
                double d4 = d * 0.6;
                this.parts.add(new double[]{this.axis[0] * d4, this.axis[1] * d4, this.axis[2] * d4, d * 0.78});
                this.parts.add(new double[]{-this.axis[0] * d4, -this.axis[1] * d4, -this.axis[2] * d4, d * (0.62 + randomSource.nextDouble() * 0.15)});
                break;
            }
            case 8: {
                double d5 = randomSource.nextDouble() * Math.PI * 2.0;
                this.axis = new double[]{Math.cos(d5), 0.25 + randomSource.nextDouble() * 0.35, Math.sin(d5)};
                MeteorShape.normalize(this.axis);
                break;
            }
            case 9: {
                int n = 3 + randomSource.nextInt(3);
                for (int i = 0; i < n; ++i) {
                    double[] dArray = MeteorShape.randomUnit(randomSource);
                    double d6 = d * 1.0;
                    this.parts.add(new double[]{dArray[0] * d6, dArray[1] * d6, dArray[2] * d6, d * (0.32 + randomSource.nextDouble() * 0.2)});
                }
                break;
            }
            case 11: {
                this.axis = MeteorShape.randomUnit(randomSource);
                break;
            }
            case 12: {
                int n = 5 + randomSource.nextInt(4);
                for (int i = 0; i < n; ++i) {
                    double[] dArray = MeteorShape.randomUnit(randomSource);
                    double d7 = d * (0.3 + randomSource.nextDouble() * 1.4);
                    this.parts.add(new double[]{dArray[0] * d7, dArray[1] * d7 * 0.45, dArray[2] * d7, d * (0.22 + randomSource.nextDouble() * 0.22)});
                }
                break;
            }
        }
    }

    public static MeteorShape roll(RandomSource randomSource, double d) {
        return new MeteorShape(Kind.roll(randomSource, d), d, randomSource);
    }

    public int bound() {
        return (int)Math.ceil(this.radius * 2.7) + 1;
    }

    public double norm(double d, double d2, double d3) {
        double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
        switch (this.kind.ordinal()) {
            case 1: 
            case 3: 
            case 4: {
                double d5 = d / (this.radius * this.sx);
                double d6 = d2 / (this.radius * this.sy);
                double d7 = d3 / (this.radius * this.sz);
                return Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
            }
            case 2: {
                if (d4 < 1.0E-6) {
                    return 0.0;
                }
                double d8 = 1.0;
                for (double[] dArray : this.parts) {
                    d8 += dArray[3] * (d * dArray[0] + d2 * dArray[1] + d3 * dArray[2]) / d4;
                }
                return d4 / (this.radius * Math.max(0.6, d8));
            }
            case 5: 
            case 7: 
            case 12: {
                return this.nearestPart(d, d2, d3);
            }
            case 6: {
                double d9 = d4 / this.radius;
                for (double[] dArray : this.parts) {
                    double d10 = d * dArray[0] + d2 * dArray[1] + d3 * dArray[2];
                    if (d10 <= this.radius * 0.5 || d10 >= dArray[3]) continue;
                    double d11 = d - dArray[0] * d10;
                    double d12 = d2 - dArray[1] * d10;
                    double d13 = d3 - dArray[2] * d10;
                    double d14 = Math.sqrt(d11 * d11 + d12 * d12 + d13 * d13);
                    double d15 = this.radius * 0.6 * (1.0 - d10 / dArray[3]) + 0.5;
                    d9 = Math.min(d9, d14 / d15);
                }
                return d9;
            }
            case 8: {
                double d16 = d * this.axis[0] + d2 * this.axis[1] + d3 * this.axis[2];
                if (d16 <= 0.0) {
                    return d4 / this.radius;
                }
                double d17 = this.radius * 2.2;
                if (d16 >= d17) {
                    return 2.0;
                }
                double d18 = d - this.axis[0] * d16;
                double d19 = d2 - this.axis[1] * d16;
                double d20 = d3 - this.axis[2] * d16;
                double d21 = Math.sqrt(d18 * d18 + d19 * d19 + d20 * d20);
                double d22 = this.radius * (1.0 - d16 / d17) + 0.4;
                return Math.min(d4 / this.radius, d21 / d22);
            }
            case 9: {
                for (double[] dArray : this.parts) {
                    double d23;
                    double d24;
                    double d25 = d - dArray[0];
                    if (!(d25 * d25 + (d24 = d2 - dArray[1]) * d24 + (d23 = d3 - dArray[2]) * d23 < dArray[3] * dArray[3])) continue;
                    return 2.0;
                }
                return d4 / this.radius;
            }
        }
        return d4 / this.radius;
    }

    public boolean isCavity(double d, double d2, double d3) {
        if (this.kind == Kind.HOLLOW) {
            double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
            return d4 < this.radius * 0.55;
        }
        if (this.kind == Kind.SPLIT) {
            double d5 = d * this.axis[0] + d2 * this.axis[1] + d3 * this.axis[2];
            return Math.abs(d5) < 0.7;
        }
        return false;
    }

    private double nearestPart(double d, double d2, double d3) {
        double d4 = Double.MAX_VALUE;
        for (double[] dArray : this.parts) {
            double d5 = d - dArray[0];
            double d6 = d2 - dArray[1];
            double d7 = d3 - dArray[2];
            d4 = Math.min(d4, Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7) / dArray[3]);
        }
        return d4;
    }

    private void stretch(RandomSource randomSource, double d, double d2) {
        int n = randomSource.nextInt(3);
        this.sx = n == 0 ? d : d2;
        this.sy = n == 1 ? d : d2;
        this.sz = n == 2 ? d : d2;
    }

    private static void normalize(double[] dArray) {
        double d = Math.sqrt(dArray[0] * dArray[0] + dArray[1] * dArray[1] + dArray[2] * dArray[2]);
        dArray[0] = dArray[0] / d;
        dArray[1] = dArray[1] / d;
        dArray[2] = dArray[2] / d;
    }

    private static double[] randomUnit(RandomSource randomSource) {
        double d = randomSource.nextDouble() * 2.0 - 1.0;
        double d2 = randomSource.nextDouble() * Math.PI * 2.0;
        double d3 = Math.sqrt(1.0 - d * d);
        return new double[]{d3 * Math.cos(d2), d, d3 * Math.sin(d2)};
    }

    public static enum Kind {
        SPHERE("sphere", 0.0, 99.0, 20),
        ELLIPSOID("ellipsoid", 0.0, 99.0, 15),
        LUMPY("lumpy", 0.0, 99.0, 18),
        SHARD("shard", 1.0, 3.2, 10),
        FLAT("flat", 2.2, 99.0, 8),
        CLUSTER("cluster", 2.2, 99.0, 8),
        SPIKY("spiky", 2.2, 99.0, 8),
        PEANUT("peanut", 2.4, 99.0, 8),
        TEARDROP("teardrop", 2.4, 99.0, 7),
        CRATERED("cratered", 3.5, 99.0, 9),
        HOLLOW("hollow", 3.5, 99.0, 6),
        SPLIT("split", 3.5, 99.0, 6),
        SHATTERED("shattered", 5.0, 99.0, 7);

        public final String id;
        final double minRadius;
        final double maxRadius;
        final int weight;

        private Kind(String string2, double d, double d2, int n2) {
            this.id = string2;
            this.minRadius = d;
            this.maxRadius = d2;
            this.weight = n2;
        }

        static Kind roll(RandomSource randomSource, double d) {
            ArrayList<Kind> arrayList = new ArrayList<Kind>();
            int n = 0;
            for (Kind kind : Kind.values()) {
                if (!(d >= kind.minRadius) || !(d <= kind.maxRadius)) continue;
                arrayList.add(kind);
                n += kind.weight;
            }
            int n2 = randomSource.nextInt(n);
            for (Kind kind : arrayList) {
                if ((n2 -= kind.weight) >= 0) continue;
                return kind;
            }
            return SPHERE;
        }
    }
}
