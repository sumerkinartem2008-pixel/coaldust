/*
 * Decompiled with CFR 0.152.
 */
package com.coaldust.ideology;

import com.coaldust.ideology.Trait;

public enum Quest {
    BLOOD_COLLECT(Trait.BLOODEATING, 8, 40),
    EAT_RAW(Trait.BLOODEATING, 10, 40),
    BLOOD_DRINK(Trait.BLOODEATING, 5, 40),
    NIGHT_KILLS(Trait.BLOODEATING, 15, 50),
    RARE_BLOOD(Trait.BLOODEATING, 3, 60),
    BOSS_BLOOD(Trait.BLOODEATING, 1, 90),
    NUDE_WALK(Trait.NUDISM, 2000, 50),
    NUDE_KILLS(Trait.NUDISM, 15, 50),
    NUDE_TIME(Trait.NUDISM, 1800, 40),
    NUDE_SWIM(Trait.NUDISM, 300, 40),
    PAIN_TAKEN(Trait.FLAGELLANTISM, 150, 50),
    SCOURGE_USE(Trait.FLAGELLANTISM, 10, 40),
    LOW_HP(Trait.FLAGELLANTISM, 3, 50),
    FALL_PAIN(Trait.FLAGELLANTISM, 30, 40),
    MEMBERS(Trait.COLLECTIVISM, 3, 60),
    TOGETHER_TIME(Trait.COLLECTIVISM, 1200, 50),
    GROUP_KILLS(Trait.COLLECTIVISM, 30, 50),
    VILLAGE_TIME(Trait.COLLECTIVISM, 900, 50),
    GROUP_MINE(Trait.COLLECTIVISM, 200, 40),
    SOLO_KILLS(Trait.INDIVIDUALISM, 25, 50),
    MINE_IRON(Trait.INDIVIDUALISM, 32, 40),
    PLACE_BLOCKS(Trait.INDIVIDUALISM, 500, 40),
    SOLO_MINE(Trait.INDIVIDUALISM, 250, 40),
    PLANT(Trait.NATURE, 40, 40),
    SAPLINGS(Trait.NATURE, 15, 40),
    BONEMEAL(Trait.NATURE, 20, 30),
    HARVEST(Trait.NATURE, 64, 50),
    COMPOST(Trait.NATURE, 30, 40),
    BREED(Trait.HUSBANDRY, 12, 50),
    TAME(Trait.HUSBANDRY, 3, 40),
    SHEAR(Trait.HUSBANDRY, 10, 30),
    MILK(Trait.HUSBANDRY, 8, 30),
    FEED_ANIMALS(Trait.HUSBANDRY, 20, 40),
    TRADE(Trait.PACIFISM, 25, 50),
    PEACE_TIME(Trait.PACIFISM, 1800, 50),
    REPAIR(Trait.PACIFISM, 10, 40),
    SLEEP_NIGHTS(Trait.PACIFISM, 3, 40),
    KILL_MOBS(Trait.PIRACY, 40, 40),
    KILL_PLAYER(Trait.PIRACY, 1, 60),
    SAIL(Trait.PIRACY, 1000, 40),
    FISH(Trait.PIRACY, 10, 40),
    DROWNED_KILLS(Trait.PIRACY, 8, 50),
    RITUALS(Trait.RITUALISM, 6, 50),
    RITUAL_KINDS(Trait.RITUALISM, 4, 60),
    NIGHT_RITUALS(Trait.RITUALISM, 2, 40),
    CANDLES(Trait.RITUALISM, 12, 30),
    AMETHYST(Trait.RITUALISM, 8, 40),
    MINE_STONE(Trait.UNDERGROUND, 600, 50),
    MINE_ORES(Trait.UNDERGROUND, 40, 50),
    DEEP_DIVE(Trait.UNDERGROUND, 1, 30),
    DIAMONDS(Trait.UNDERGROUND, 5, 60),
    CAVE_TIME(Trait.UNDERGROUND, 900, 40),
    BREW(Trait.ALCHEMY, 12, 50),
    DRINK_POTIONS(Trait.ALCHEMY, 8, 40),
    WITCH_KILLS(Trait.ALCHEMY, 3, 50),
    NETHER_WART(Trait.ALCHEMY, 24, 40),
    HUNGRY_TIME(Trait.ASCETICISM, 1200, 50),
    SIMPLE_FOOD(Trait.ASCETICISM, 20, 40),
    HUNGRY_WALK(Trait.ASCETICISM, 1500, 50),
    MEDITATE(Trait.ASCETICISM, 600, 40),
    BOW_KILLS(Trait.HUNTING, 25, 50),
    LONG_SHOTS(Trait.HUNTING, 5, 60),
    HUNT_ANIMALS(Trait.HUNTING, 15, 40),
    FLYING_SHOTS(Trait.HUNTING, 5, 50),
    XP_LEVELS(Trait.ENLIGHTENMENT, 30, 50),
    XP_ORBS(Trait.ENLIGHTENMENT, 500, 40),
    BOOKSHELVES(Trait.ENLIGHTENMENT, 15, 40),
    CRAFT_BOOKS(Trait.ENLIGHTENMENT, 10, 40),
    EVO_KILLS(Trait.EVOLVING, 40, 50),
    EVO_LEARN(Trait.EVOLVING, 2, 60),
    EVO_POINTS(Trait.EVOLVING, 10, 50),
    SUM_UNDEAD(Trait.SUMMONER, 25, 50),
    SUM_NIGHT(Trait.SUMMONER, 30, 50),
    G_HOSTILE(null, 30, 40),
    G_MINE(null, 300, 40),
    G_COAL(null, 24, 40),
    G_CRAFT(null, 64, 30),
    G_SMELT(null, 48, 40),
    G_EAT(null, 15, 30),
    G_SLEEP(null, 2, 40),
    G_WALK(null, 3000, 40),
    G_FISH(null, 6, 40),
    G_PLACE(null, 200, 30);

    public static final int GENERAL_SLOTS = 3;
    public final Trait trait;
    public final int target;
    public final int xp;

    public boolean needsGroup() {
        return this == MEMBERS || this == TOGETHER_TIME || this == GROUP_KILLS || this == GROUP_MINE || this == KILL_PLAYER;
    }

    private Quest(Trait trait, int n2, int n3) {
        this.trait = trait;
        this.target = n2;
        this.xp = n3;
    }

    public String id() {
        return this.name().toLowerCase();
    }

    public static Quest byId(String string) {
        for (Quest quest : Quest.values()) {
            if (!quest.id().equals(string)) continue;
            return quest;
        }
        return null;
    }
}
