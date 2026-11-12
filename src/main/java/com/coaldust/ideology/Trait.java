/*
 * Decompiled with CFR 0.152.
 */
package com.coaldust.ideology;

public enum Trait {
    BLOODEATING("bloodeating"),
    NUDISM("nudism"),
    FLAGELLANTISM("flagellantism"),
    COLLECTIVISM("collectivism"),
    INDIVIDUALISM("individualism"),
    NATURE("nature"),
    HUSBANDRY("husbandry"),
    PACIFISM("pacifism"),
    PIRACY("piracy"),
    RITUALISM("ritualism"),
    UNDERGROUND("underground"),
    ALCHEMY("alchemy"),
    ASCETICISM("asceticism"),
    HUNTING("hunting"),
    ENLIGHTENMENT("enlightenment"),
    EVOLVING("evolving", true),
    SUMMONER("summoner", true);

    public final String id;
    public final boolean abyss;

    private Trait(String string2) {
        this(string2, false);
    }

    private Trait(String string2, boolean bl) {
        this.id = string2;
        this.abyss = bl;
    }

    public static Trait byId(String string) {
        for (Trait trait : Trait.values()) {
            if (!trait.id.equals(string)) continue;
            return trait;
        }
        return null;
    }
}
