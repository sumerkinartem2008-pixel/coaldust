/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.RandomSource
 */
package com.coaldust.meteor;

import net.minecraft.util.RandomSource;

public enum MeteorType {
    SMALL_RESOURCE("small", 1.5, 2.1, 0.35f, 0.0f),
    MEDIUM_RESOURCE("medium", 2.4, 3.2, 0.45f, 0.07f),
    LARGE_RESOURCE("large", 3.6, 5.0, 0.52f, 0.12f),
    PLAIN("plain", 1.5, 5.0, 0.03f, 0.0f),
    ICE("ice", 1.5, 5.0, 0.0f, 0.0f),
    LIVING("living", 1.5, 5.0, 0.08f, 0.0f),
    ANCIENT("ancient", 2.4, 16.0, 0.0f, 0.0f),
    UNDERGROUND("underground", 24.0, 24.0, 0.0f, 0.0f);

    public final String id;
    public final double minRadius;
    public final double maxRadius;
    public final float oreChance;
    public final float rawBlockChance;

    public double rollRadius(RandomSource randomSource, Size size) {
        if (size == Size.RANDOM) {
            size = Size.rollNatural(randomSource);
        }
        return size.min + randomSource.nextDouble() * (size.max - size.min);
    }

    public static MeteorType resourceFor(Size size, RandomSource randomSource) {
        return switch (size.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1, 4 -> SMALL_RESOURCE;
            case 2 -> MEDIUM_RESOURCE;
            case 3, 5, 6, 7, 8 -> LARGE_RESOURCE;
            case 0 -> MeteorType.resourceFor(Size.rollNatural(randomSource), randomSource);
        };
    }

    private MeteorType(String string2, double d, double d2, float f, float f2) {
        this.id = string2;
        this.minRadius = d;
        this.maxRadius = d2;
        this.oreChance = f;
        this.rawBlockChance = f2;
    }

    public double rollRadius(RandomSource randomSource) {
        return this.minRadius + randomSource.nextDouble() * (this.maxRadius - this.minRadius);
    }

    public boolean hasRedstone() {
        return this == MEDIUM_RESOURCE || this == LARGE_RESOURCE;
    }

    public boolean hasGems() {
        return this == LARGE_RESOURCE;
    }

    public static MeteorType rollNatural(RandomSource randomSource, Size size) {
        int n = randomSource.nextInt(100);
        return switch (size.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1, 4 -> {
                if (n < 50) {
                    yield MeteorType.resourceFor(size, randomSource);
                }
                if (n < 75) {
                    yield PLAIN;
                }
                if (n < 90) {
                    yield ICE;
                }
                yield LIVING;
            }
            case 2, 3 -> {
                if (n < 45) {
                    yield MeteorType.resourceFor(size, randomSource);
                }
                if (n < 67) {
                    yield PLAIN;
                }
                if (n < 82) {
                    yield ICE;
                }
                if (n < 92) {
                    yield LIVING;
                }
                yield ANCIENT;
            }
            case 5, 6 -> {
                if (n < 35) {
                    yield MeteorType.resourceFor(size, randomSource);
                }
                if (n < 50) {
                    yield PLAIN;
                }
                if (n < 62) {
                    yield ICE;
                }
                if (n < 75) {
                    yield LIVING;
                }
                if (n < 90) {
                    yield ANCIENT;
                }
                yield UNDERGROUND;
            }
            case 7 -> {
                if (n < 20) {
                    yield LARGE_RESOURCE;
                }
                if (n < 30) {
                    yield PLAIN;
                }
                if (n < 40) {
                    yield ICE;
                }
                if (n < 65) {
                    yield ANCIENT;
                }
                yield UNDERGROUND;
            }
            case 8 -> {
                if (n < 10) {
                    yield LARGE_RESOURCE;
                }
                if (n < 55) {
                    yield ANCIENT;
                }
                yield UNDERGROUND;
            }
            case 0 -> MeteorType.rollNatural(randomSource, Size.rollNatural(randomSource));
        };
    }

    public static enum Size {
        RANDOM("random", 0.0, 0.0, 0),
        SMALL("small", 1.5, 2.1, 300),
        MEDIUM("medium", 2.4, 3.2, 250),
        LARGE("large", 3.6, 5.0, 150),
        TINY("tiny", 1.0, 1.4, 200),
        HUGE("huge", 5.5, 7.5, 70),
        GIANT("giant", 8.0, 10.0, 30),
        COLOSSAL("colossal", 11.0, 13.5, 10),
        TITANIC("titanic", 14.5, 16.0, 3);

        public final String id;
        final double min;
        final double max;
        final int weight;

        private Size(String string2, double d, double d2, int n2) {
            this.id = string2;
            this.min = d;
            this.max = d2;
            this.weight = n2;
        }

        public static Size rollNatural(RandomSource randomSource) {
            int n = 0;
            for (Size size : Size.values()) {
                n += size.weight;
            }
            int n2 = randomSource.nextInt(n);
            for (Size size : Size.values()) {
                if ((n2 -= size.weight) >= 0 || size.weight <= 0) continue;
                return size;
            }
            return SMALL;
        }

        public static Size byIndex(int n) {
            Size[] sizeArray = Size.values();
            return n >= 0 && n < sizeArray.length ? sizeArray[n] : RANDOM;
        }
    }
}
