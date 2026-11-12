/*
 * Decompiled with CFR 0.152.
 */
package com.coaldust.ideology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AbyssSkills {
    public static final int TIERS = 10;
    public static final int COLS = 12;
    static final String[] ROOT_ICONS = new String[]{"minecraft:wheat", "minecraft:shield", "minecraft:netherite_sword", "minecraft:zombie_head", "minecraft:skeleton_skull", "minecraft:iron_pickaxe"};
    public static final List<Stat> STATS = new ArrayList<Stat>();
    public static final List<Skill> ALL = new ArrayList<Skill>();
    private static final Map<String, Skill> BY_ID = new HashMap<String, Skill>();
    private static final Map<String, Stat> STAT_BY_ID = new HashMap<String, Stat>();

    private static Stat st(int n, int n2, String string, double d, boolean bl, String string2, String string3) {
        Stat stat = new Stat(string, n, n2, d, bl, string2, string3);
        STATS.add(stat);
        STAT_BY_ID.put(string, stat);
        return stat;
    }

    private static void build(int n, int n2) {
        ArrayList<Stat> arrayList = new ArrayList<Stat>();
        for (Stat stat : STATS) {
            if (stat.tree != n || stat.pillar != n2) continue;
            arrayList.add(stat);
        }
        String string = id(n, n2, 0, 6);
        add(new Skill(string, n, n2, 0, 6, 1, null, new String[0]));
        long l = 9137L * (long)(n * 3 + n2 + 1);
        for (int i = 1; i <= 10; ++i) {
            for (int j = 0; j < 12; ++j) {
                Stat stat = (Stat)arrayList.get(Math.floorMod(j + (i - 1) / 3 * 5, arrayList.size()));
                long l2 = (l ^ (long)i * 73856093L ^ (long)j * 19349663L) & Integer.MAX_VALUE;
                int n3 = 1 + (int)(l2 % 3L);
                ArrayList<Object> arrayList2 = new ArrayList<Object>();
                if (i == 1) {
                    arrayList2.add(string);
                } else {
                    arrayList2.add(id(n, n2, i - 1, j));
                    if (l2 % 5L == 0L && j > 0) {
                        arrayList2.add(id(n, n2, i - 1, j - 1));
                    }
                    if (l2 % 7L == 0L && j < 11) {
                        arrayList2.add(id(n, n2, i - 1, j + 1));
                    }
                }
                add(new Skill(id(n, n2, i, j), n, n2, i, j, n3, stat, arrayList2.toArray(new String[0])));
            }
        }
    }

    static String id(int n, int n2, int n3, int n4) {
        return "n" + n + n2 + "_" + n3 + "_" + n4;
    }

    private static void add(Skill skill) {
        ALL.add(skill);
        BY_ID.put(skill.id, skill);
    }

    public static Skill byId(String string) {
        return BY_ID.get(string);
    }

    public static Stat stat(String string) {
        return STAT_BY_ID.get(string);
    }

    public static List<Skill> tree(int n) {
        ArrayList<Skill> arrayList = new ArrayList<Skill>();
        for (Skill skill : ALL) {
            if (skill.tree != n) continue;
            arrayList.add(skill);
        }
        return arrayList;
    }

    public static List<Skill> pillar(int n, int n2) {
        ArrayList<Skill> arrayList = new ArrayList<Skill>();
        for (Skill skill : ALL) {
            if (skill.tree != n || skill.pillar != n2) continue;
            arrayList.add(skill);
        }
        return arrayList;
    }

    public static List<Stat> stats(int n, int n2) {
        ArrayList<Stat> arrayList = new ArrayList<Stat>();
        for (Stat stat : STATS) {
            if (stat.tree != n || stat.pillar != n2) continue;
            arrayList.add(stat);
        }
        return arrayList;
    }

    public static double total(Map<String, Integer> map, String string) {
        Stat stat = STAT_BY_ID.get(string);
        if (stat == null) {
            return 0.0;
        }
        double d = 0.0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Skill skill = BY_ID.get(entry.getKey());
            if (skill == null || skill.stat != stat) continue;
            d += (double)entry.getValue().intValue() * stat.perRank;
        }
        d = Math.min(d, stat.cap);
        return stat.whole ? Math.floor(d + 1.0E-6) : d;
    }

    private AbyssSkills() {
    }

    static {
        st(0, 0, "crops", 1.5, false, "minecraft:wheat", "x");
        st(0, 0, "furnace", 30.0, false, "minecraft:furnace", "t");
        st(0, 0, "brew", 30.0, false, "minecraft:brewing_stand", "t");
        st(0, 0, "animals", 4.0, false, "minecraft:egg", "x");
        st(0, 0, "breed", 4.0, false, "minecraft:golden_carrot", "x");
        st(0, 0, "radius", 1.0, true, "minecraft:ender_eye", "+");
        st(0, 0, "harvest", 1.0, false, "minecraft:carrot", "%");
        // Рост (+5)
        st(0, 0, "saplings", 1.5, false, "minecraft:oak_sapling", "x");
        st(0, 0, "twins", 0.5, false, "minecraft:lead", "%");
        st(0, 0, "livestock", 0.6, false, "minecraft:leather", "%");
        st(0, 0, "fertile", 0.5, false, "minecraft:bone_meal", "%");
        st(0, 0, "fresh", 0.5, false, "minecraft:bread", "%");
        st(0, 1, "resist", 0.3, false, "minecraft:iron_chestplate", "%");
        st(0, 1, "regen", 4.0, false, "minecraft:glistering_melon_slice", "+");
        st(0, 1, "knock", 0.8, false, "minecraft:shield", "%");
        st(0, 1, "fire", 0.75, false, "minecraft:magma_cream", "%");
        st(0, 1, "fall", 0.9, false, "minecraft:feather", "%");
        st(0, 1, "health", 10.0, false, "minecraft:golden_apple", "+");
        st(0, 1, "projectile", 0.4, false, "minecraft:arrow", "%");
        st(0, 1, "blast", 0.5, false, "minecraft:tnt", "%");
        st(0, 1, "second", 10.0, false, "minecraft:totem_of_undying", "+");
        // Стойкость (+3)
        st(0, 1, "hunger", 0.5, false, "minecraft:cooked_beef", "%");
        st(0, 1, "immunity", 0.6, false, "minecraft:milk_bucket", "%");
        st(0, 1, "gills", 20, false, "minecraft:turtle_helmet", "s");
        st(0, 2, "melee", 0.6, false, "minecraft:iron_sword", "%");
        st(0, 2, "aspeed", 0.4, false, "minecraft:golden_sword", "%");
        st(0, 2, "mining", 1.0, false, "minecraft:golden_pickaxe", "%");
        st(0, 2, "speed", 0.3, false, "minecraft:sugar", "%");
        st(0, 2, "luck", 4.0, false, "minecraft:rabbit_foot", "+");
        st(0, 2, "loot", 0.6, false, "minecraft:emerald", "%");
        st(0, 2, "crit", 0.3, false, "minecraft:blaze_powder", "%");
        st(0, 2, "bow", 0.6, false, "minecraft:bow", "%");
        st(0, 2, "xp", 0.5, false, "minecraft:experience_bottle", "%");
        st(0, 2, "reach", 1.5, false, "minecraft:spyglass", "+");
        // Мощь (+2)
        st(0, 2, "vamp", 0.15, false, "minecraft:ghast_tear", "%");
        st(0, 2, "jump", 0.5, false, "minecraft:slime_ball", "%");
        st(1, 0, "s_zombies", 6.0, true, "minecraft:rotten_flesh", "+");
        st(1, 0, "s_skeletons", 6.0, true, "minecraft:bone", "+");
        st(1, 0, "s_life", 180.0, false, "minecraft:clock", "s");
        st(1, 0, "s_cool", 15.0, false, "minecraft:recovery_compass", "m");
        st(1, 0, "s_hp", 1.0, false, "minecraft:glistering_melon_slice", "%");
        st(1, 0, "s_dmg", 0.8, false, "minecraft:iron_sword", "%");
        st(1, 0, "s_armor", 10.0, false, "minecraft:iron_chestplate", "+");
        // Отряд (+5)
        st(1, 0, "s_speed", 0.5, false, "minecraft:sugar", "%");
        st(1, 0, "s_regen", 4, false, "minecraft:ghast_tear", "+");
        st(1, 0, "s_fire", 0.8, false, "minecraft:magma_cream", "%");
        st(1, 0, "s_knock", 0.8, false, "minecraft:shield", "%");
        st(1, 0, "s_aim", 0.5, false, "minecraft:spectral_arrow", "%");
        st(1, 1, "a_zombies", 5.0, true, "minecraft:zombie_head", "+");
        st(1, 1, "a_skeletons", 5.0, true, "minecraft:skeleton_skull", "+");
        st(1, 1, "a_healers", 2.0, true, "minecraft:splash_potion", "+");
        st(1, 1, "a_respawn", 4.0, false, "minecraft:totem_of_undying", "m");
        st(1, 1, "a_hp", 1.0, false, "minecraft:golden_apple", "%");
        st(1, 1, "a_dmg", 0.8, false, "minecraft:diamond_sword", "%");
        st(1, 1, "a_armor", 10.0, false, "minecraft:netherite_helmet", "+");
        st(1, 1, "a_heal", 1.0, false, "minecraft:glistering_melon_slice", "%");
        // Армия (+4)
        st(1, 1, "a_speed", 0.5, false, "minecraft:sugar", "%");
        st(1, 1, "a_regen", 4, false, "minecraft:ghast_tear", "+");
        st(1, 1, "a_fire", 0.8, false, "minecraft:magma_cream", "%");
        st(1, 1, "a_rate", 0.5, false, "minecraft:crossbow", "%");
        st(1, 2, "w_count", 5.0, true, "minecraft:iron_shovel", "+");
        st(1, 2, "w_speed", 2.0, false, "minecraft:golden_pickaxe", "%");
        st(1, 2, "w_bag", 27.0, true, "minecraft:chest", "+");
        st(1, 2, "w_luck", 0.6, false, "minecraft:lapis_lazuli", "%");
        st(1, 2, "w_stride", 0.6, false, "minecraft:leather_boots", "%");
        // Рабочие (+7)
        st(1, 2, "w_range", 8, true, "minecraft:spyglass", "+");
        st(1, 2, "w_hp", 1.0, false, "minecraft:golden_apple", "%");
        st(1, 2, "w_regen", 4, false, "minecraft:ghast_tear", "+");
        st(1, 2, "w_armor", 10, true, "minecraft:iron_chestplate", "+");
        st(1, 2, "w_xp", 1.0, false, "minecraft:experience_bottle", "%");
        st(1, 2, "w_unload", 0.8, false, "minecraft:hopper", "%");
        st(1, 2, "w_durab", 0.8, false, "minecraft:anvil", "%");
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 3; ++j) {
                AbyssSkills.build(i, j);
            }
        }
        HashMap<Stat, Integer> hashMap = new HashMap<Stat, Integer>();
        for (Skill object : ALL) {
            if (object.stat == null) continue;
            hashMap.merge(object.stat, object.max, Integer::sum);
        }
        for (Stat stat : STATS) {
            stat.perRank = stat.cap / (double)Math.max(1, hashMap.getOrDefault(stat, 1));
        }
    }

    public static final class Stat {
        public final String id;
        public final int tree;
        public final int pillar;
        public final double cap;
        public final boolean whole;
        public final String icon;
        public final String fmt;
        double perRank;

        Stat(String string, int n, int n2, double d, boolean bl, String string2, String string3) {
            this.id = string;
            this.tree = n;
            this.pillar = n2;
            this.cap = d;
            this.whole = bl;
            this.icon = string2;
            this.fmt = string3;
        }

        public double perRank() {
            return this.perRank;
        }
    }

    public static final class Skill {
        public final String id;
        public final int tree;
        public final int pillar;
        public final int tier;
        public final int col;
        public final int max;
        public final Stat stat;
        public final String[] parents;

        Skill(String string, int n, int n2, int n3, int n4, int n5, Stat stat, String ... stringArray) {
            this.id = string;
            this.tree = n;
            this.pillar = n2;
            this.tier = n3;
            this.col = n4;
            this.max = n5;
            this.stat = stat;
            this.parents = stringArray;
        }

        public int cost(int n) {
            return this.tier == 0 ? 1 : this.tier + n;
        }

        public String icon() {
            return this.stat != null ? this.stat.icon : ROOT_ICONS[this.tree * 3 + this.pillar];
        }
    }
}

