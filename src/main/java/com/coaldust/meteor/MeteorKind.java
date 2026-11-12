/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.RandomSource
 */
package com.coaldust.meteor;

import com.coaldust.meteor.MeteorType;
import com.coaldust.meteor.underground.UndergroundCity;
import net.minecraft.util.RandomSource;

public enum MeteorKind {
    RESOURCE_SMALL("resource_small", MeteorType.SMALL_RESOURCE, MeteorType.Size.SMALL, -1),
    RESOURCE_MEDIUM("resource_medium", MeteorType.MEDIUM_RESOURCE, MeteorType.Size.MEDIUM, -1),
    RESOURCE_LARGE("resource_large", MeteorType.LARGE_RESOURCE, MeteorType.Size.LARGE, -1),
    PLAIN("plain", MeteorType.PLAIN, MeteorType.Size.RANDOM, -1),
    ICE("ice", MeteorType.ICE, MeteorType.Size.RANDOM, -1),
    LIVING("living", MeteorType.LIVING, MeteorType.Size.RANDOM, -1),
    SHRINE("shrine", MeteorType.ANCIENT, null, 0),
    GALLOWS("gallows", MeteorType.ANCIENT, null, 1),
    TEMPLE("temple", MeteorType.ANCIENT, null, 10),
    WARLOCK_TOWER("warlock_tower", MeteorType.ANCIENT, null, 11),
    MONASTERY("monastery", MeteorType.ANCIENT, null, 12),
    NECROPOLIS_TOWN("necropolis_town", MeteorType.ANCIENT, null, 20),
    VAMPIRE_CASTLE("vampire_castle", MeteorType.ANCIENT, null, 21),
    CURSED_VILLAGE("cursed_village", MeteorType.ANCIENT, null, 22),
    CITADEL("citadel", MeteorType.ANCIENT, null, 30),
    CATHEDRAL("cathedral", MeteorType.ANCIENT, null, 31),
    DWARF_TOWN("dwarf_town", MeteorType.ANCIENT, null, 40),
    DWARF_HOLD("dwarf_hold", MeteorType.ANCIENT, null, 41),
    DEEP_KINGDOM("deep_kingdom", MeteorType.ANCIENT, null, 42),
    DWARF_CITY("dwarf_city", MeteorType.UNDERGROUND, null, 0),
    NECROPOLIS("necropolis", MeteorType.UNDERGROUND, null, 1),
    ABYSS("abyss", MeteorType.UNDERGROUND, null, 2),
    NEST("nest", MeteorType.UNDERGROUND, null, 3),
    FORGE("forge", MeteorType.UNDERGROUND, null, 4),
    METROPOLIS("metropolis", MeteorType.UNDERGROUND, null, 5),
    CANYON("canyon", MeteorType.UNDERGROUND, null, 6),
    SPIRES("spires", MeteorType.UNDERGROUND, null, 7),
    RANDOM("random", null, null, -1);

    public final String id;
    public final MeteorType type;
    public final MeteorType.Size size;
    public final int variant;

    private MeteorKind(String string2, MeteorType meteorType, MeteorType.Size size, int n2) {
        this.id = string2;
        this.type = meteorType;
        this.size = size;
        this.variant = n2;
    }

    public static MeteorKind byIndex(int n) {
        MeteorKind[] meteorKindArray = MeteorKind.values();
        return n >= 0 && n < meteorKindArray.length ? meteorKindArray[n] : RANDOM;
    }

    public double radius(RandomSource randomSource) {
        if (this.type == MeteorType.ANCIENT) {
            return switch (this.variant / 10) {
                case 0 -> 3.0;
                case 1 -> 5.5;
                case 2 -> 9.0;
                case 3 -> 12.0;
                default -> 15.0;
            };
        }
        if (this.type == MeteorType.UNDERGROUND) {
            return UndergroundCity.radiusFor(this.variant);
        }
        return this.size == MeteorType.Size.RANDOM ? this.type.rollRadius(randomSource) : this.type.rollRadius(randomSource, this.size);
    }
}
