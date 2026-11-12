/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 */
package com.coaldust.ideology;

import com.coaldust.ideology.IdeologyStructure;
import com.coaldust.ideology.Quest;
import com.coaldust.ideology.Trait;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;

public class Ideology {
    public static final int MAX_LEVEL = 15;
    public static final int BUFF_LEVEL = 5;
    public static final int MAX_TRAITS = 3;
    public UUID id;
    public String name;
    public IdeologyStructure structure;
    public final List<Trait> traits = new ArrayList<Trait>();
    public int level = 1;
    public int xp;
    public int picks;
    public UUID leader;
    public final LinkedHashMap<UUID, String> members = new LinkedHashMap();
    public final Set<UUID> invites = new HashSet<UUID>();
    public final LinkedHashMap<UUID, String> requests = new LinkedHashMap();
    public String desc = "";
    public int color = 13936714;
    public String icon = "";
    public static final int MAX_NAME = 32;
    public static final int MAX_DESC = 200;
    public static final int[] COLORS = new int[]{0xF9FFFE, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 7040883, 0x9D9D97, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 13936714};
    public static final String[] EMBLEMS = new String[]{"sun", "moon", "skull", "swords", "anchor", "tree", "wheat", "pickaxes", "eye", "flame", "blood", "crown", "star", "hammer", "fist", "mountain"};
    public final Map<Quest, Integer> progress = new HashMap<Quest, Integer>();
    public final Map<Trait, Quest> active = new LinkedHashMap<Trait, Quest>();
    public final List<Quest> general = new ArrayList<Quest>();
    public long lastReroll = -1000000L;
    public final Set<String> ritualKinds = new HashSet<String>();

    public String iconId() {
        if (Ideology.validIcon(this.icon)) {
            return this.icon;
        }
        return Ideology.defaultEmblem(this.traits.isEmpty() ? null : this.traits.get(0));
    }

    public static String defaultEmblem(Trait trait) {
        if (trait == null) {
            return "sun";
        }
        return switch (trait) {
            default -> throw new MatchException(null, null);
            case Trait.BLOODEATING -> "blood";
            case Trait.NUDISM -> "fist";
            case Trait.FLAGELLANTISM -> "skull";
            case Trait.COLLECTIVISM -> "hammer";
            case Trait.INDIVIDUALISM -> "crown";
            case Trait.NATURE -> "tree";
            case Trait.HUSBANDRY -> "wheat";
            case Trait.PACIFISM -> "sun";
            case Trait.PIRACY -> "anchor";
            case Trait.RITUALISM -> "eye";
            case Trait.UNDERGROUND -> "pickaxes";
            case Trait.ALCHEMY -> "moon";
            case Trait.ASCETICISM -> "mountain";
            case Trait.HUNTING -> "swords";
            case Trait.ENLIGHTENMENT -> "star";
            case Trait.EVOLVING -> "eye";
            case Trait.SUMMONER -> "skull";
        };
    }

    public static boolean validIcon(String string) {
        if (string == null) {
            return false;
        }
        for (String string2 : EMBLEMS) {
            if (!string2.equals(string)) continue;
            return true;
        }
        return false;
    }

    public Trait mainTrait() {
        return this.traits.isEmpty() ? null : this.traits.get(0);
    }

    public boolean has(Trait trait) {
        return this.traits.contains((Object)trait);
    }

    public int xpForNext() {
        int n = 250 + 150 * (this.level - 1);
        return this.structure == IdeologyStructure.DOCTRINE ? (int)((double)n * 0.8) : n;
    }

    public int target(Quest quest) {
        return Math.max(1, (int)Math.round((double)quest.target * (1.0 + 0.25 * (double)(this.level - 1))));
    }

    public int reward(Quest quest) {
        return (int)Math.round((double)quest.xp * (1.0 + 0.15 * (double)(this.level - 1)));
    }

    public int strongLevel() {
        return this.structure == IdeologyStructure.ANIMISM ? 8 : 10;
    }

    public boolean buffsUnlocked() {
        return this.level >= 5;
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", this.id.toString());
        compoundTag.putString("name", this.name);
        compoundTag.putString("structure", this.structure.id);
        StringBuilder stringBuilder = new StringBuilder();
        for (Trait object2 : this.traits) {
            stringBuilder.append(object2.id).append(',');
        }
        compoundTag.putString("traits", stringBuilder.toString());
        compoundTag.putInt("level", this.level);
        compoundTag.putInt("xp", this.xp);
        compoundTag.putInt("picks", this.picks);
        compoundTag.putString("leader", this.leader.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        this.members.forEach((uUID, string) -> stringBuilder2.append(uUID).append('=').append(string.replace(";", "")).append(';'));
        compoundTag.putString("members", stringBuilder2.toString());
        StringBuilder stringBuilder3 = new StringBuilder();
        for (UUID uUID2 : this.invites) {
            stringBuilder3.append(uUID2).append(';');
        }
        compoundTag.putString("invites", stringBuilder3.toString());
        StringBuilder stringBuilder4 = new StringBuilder();
        this.progress.forEach((quest, n) -> stringBuilder4.append(quest.id()).append('=').append(n).append(';'));
        compoundTag.putString("progress", stringBuilder4.toString());
        StringBuilder stringBuilder5 = new StringBuilder();
        this.active.forEach((trait, quest) -> stringBuilder5.append(trait.id).append('=').append(quest.id()).append(';'));
        compoundTag.putString("active", stringBuilder5.toString());
        StringBuilder stringBuilder6 = new StringBuilder();
        for (Quest quest2 : this.general) {
            stringBuilder6.append(quest2.id()).append(';');
        }
        compoundTag.putString("general", stringBuilder6.toString());
        compoundTag.putLong("lastReroll", this.lastReroll);
        compoundTag.putString("rituals", String.join((CharSequence)",", this.ritualKinds));
        compoundTag.putString("desc", this.desc);
        compoundTag.putInt("color", this.color);
        compoundTag.putString("icon", this.icon);
        StringBuilder stringBuilder7 = new StringBuilder();
        this.requests.forEach((uUID, string) -> stringBuilder7.append(uUID).append('=').append(string.replace(";", "")).append(';'));
        compoundTag.putString("requests", stringBuilder7.toString());
        return compoundTag;
    }

    public static Ideology load(CompoundTag compoundTag) {
        Enum enum_;
        Ideology ideology = new Ideology();
        ideology.id = UUID.fromString(compoundTag.getString("id"));
        ideology.name = compoundTag.getString("name");
        ideology.structure = IdeologyStructure.byId(compoundTag.getString("structure"));
        if (ideology.structure == null) {
            ideology.structure = IdeologyStructure.CHURCH;
        }
        for (String string : compoundTag.getString("traits").split(",")) {
            Trait trait = Trait.byId(string);
            if (trait == null) continue;
            ideology.traits.add(trait);
        }
        ideology.level = Math.max(1, compoundTag.getInt("level"));
        ideology.xp = compoundTag.getInt("xp");
        ideology.picks = compoundTag.getInt("picks");
        ideology.leader = UUID.fromString(compoundTag.getString("leader"));
        for (String string : compoundTag.getString("members").split(";")) {
            int n = string.indexOf(61);
            if (n <= 0) continue;
            ideology.members.put(UUID.fromString(string.substring(0, n)), string.substring(n + 1));
        }
        for (String string : compoundTag.getString("invites").split(";")) {
            if (string.isEmpty()) continue;
            ideology.invites.add(UUID.fromString(string));
        }
        for (String string : compoundTag.getString("progress").split(";")) {
            int n = string.indexOf(61);
            if (n <= 0 || (enum_ = Quest.byId(string.substring(0, n))) == null) continue;
            ideology.progress.put((Quest)enum_, Integer.parseInt(string.substring(n + 1)));
        }
        for (String string : compoundTag.getString("active").split(";")) {
            int n = string.indexOf(61);
            if (n <= 0) continue;
            enum_ = Trait.byId(string.substring(0, n));
            Quest quest = Quest.byId(string.substring(n + 1));
            if (enum_ == null || quest == null) continue;
            ideology.active.put((Trait)enum_, quest);
        }
        for (String string : compoundTag.getString("general").split(";")) {
            Quest quest = Quest.byId(string);
            if (quest == null || quest.trait != null || ideology.general.contains((Object)quest)) continue;
            ideology.general.add(quest);
        }
        if (compoundTag.contains("lastReroll")) {
            ideology.lastReroll = compoundTag.getLong("lastReroll");
        }
        for (String string : compoundTag.getString("rituals").split(",")) {
            if (string.isEmpty()) continue;
            ideology.ritualKinds.add(string);
        }
        ideology.desc = compoundTag.getString("desc");
        ideology.color = compoundTag.contains("color") ? compoundTag.getInt("color") : 13936714;
        ideology.icon = compoundTag.getString("icon");
        for (String string : compoundTag.getString("requests").split(";")) {
            int n = string.indexOf(61);
            if (n <= 0) continue;
            ideology.requests.put(UUID.fromString(string.substring(0, n)), string.substring(n + 1));
        }
        return ideology;
    }
}
