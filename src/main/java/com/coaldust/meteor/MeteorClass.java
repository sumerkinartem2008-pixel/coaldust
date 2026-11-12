/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.RandomSource
 */
package com.coaldust.meteor;

import net.minecraft.util.RandomSource;

public final class MeteorClass {
    private MeteorClass() {
    }

    public static enum Density {
        POROUS("porous", 0.75f, 0.18f, 30),
        NORMAL("normal", 1.0f, 0.0f, 45),
        DENSE("dense", 1.25f, 0.0f, 25);

        public final String id;
        public final float craterMultiplier;
        public final float holeChance;
        final int weight;

        private Density(String string2, float f, float f2, int n2) {
            this.id = string2;
            this.craterMultiplier = f;
            this.holeChance = f2;
            this.weight = n2;
        }

        public static Density roll(RandomSource randomSource) {
            int n = randomSource.nextInt(100);
            return n < Density.POROUS.weight ? POROUS : (n < Density.POROUS.weight + Density.NORMAL.weight ? NORMAL : DENSE);
        }
    }

    public static enum Richness {
        POOR("poor", 0.4f, 35),
        NORMAL("normal", 1.0f, 45),
        RICH("rich", 1.8f, 20);

        public final String id;
        public final float oreMultiplier;
        final int weight;

        private Richness(String string2, float f, int n2) {
            this.id = string2;
            this.oreMultiplier = f;
            this.weight = n2;
        }

        public static Richness roll(RandomSource randomSource) {
            int n = randomSource.nextInt(100);
            return n < Richness.POOR.weight ? POOR : (n < Richness.POOR.weight + Richness.NORMAL.weight ? NORMAL : RICH);
        }
    }
}
