/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.ItemLike
 */
package com.coaldust.ideology;

import com.coaldust.ideology.Abyss;
import com.coaldust.ideology.Ideology;
import com.coaldust.ideology.IdeologyData;
import com.coaldust.ideology.IdeologyStructure;
import com.coaldust.ideology.Quest;
import com.coaldust.ideology.Trait;
import com.coaldust.ideology.TraitEffects;
import com.coaldust.item.ModItems;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;

public final class IdeologyManager {
    public static final long REROLL_TICKS = 24000L;
    public static final String SYNC_PREFIX = "\u00a7coaldust:ideology\u00a7";

    public static IdeologyData data(MinecraftServer minecraftServer) {
        return IdeologyData.get(minecraftServer);
    }

    public static Ideology of(ServerPlayer serverPlayer) {
        MinecraftServer minecraftServer = serverPlayer.getServer();
        return minecraftServer == null ? null : IdeologyManager.data(minecraftServer).of(serverPlayer.getUUID());
    }

    public static boolean has(ServerPlayer serverPlayer, Trait trait) {
        Ideology ideology = IdeologyManager.of(serverPlayer);
        return ideology != null && ideology.has(trait);
    }

    public static String shortId(UUID uUID) {
        return uUID.toString().substring(0, 8);
    }

    public static void create(ServerPlayer serverPlayer, String string, IdeologyStructure ideologyStructure, Trait trait) {
        IdeologyManager.create(serverPlayer, string, ideologyStructure, trait, 13936714, "");
    }

    public static void create(ServerPlayer serverPlayer, String string, IdeologyStructure ideologyStructure, Trait trait, int n, String string2) {
        IdeologyData ideologyData = IdeologyManager.data(serverPlayer.getServer());
        if (ideologyData.of(serverPlayer.getUUID()) != null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.already_member");
            return;
        }
        if (trait.abyss && !Abyss.inAbyss(serverPlayer)) {
            return;
        }
        if ((string = string.trim()).isEmpty() || string.length() > 32) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.bad_name");
            return;
        }
        Ideology ideology = new Ideology();
        ideology.id = UUID.randomUUID();
        ideology.name = string;
        ideology.structure = ideologyStructure;
        ideology.traits.add(trait);
        ideology.color = n & 0xFFFFFF;
        ideology.icon = Ideology.validIcon(string2) ? string2 : "";
        ideology.leader = serverPlayer.getUUID();
        IdeologyManager.clearRequests(ideologyData, serverPlayer.getUUID());
        ideology.members.put(serverPlayer.getUUID(), serverPlayer.getScoreboardName());
        IdeologyManager.ensureQuests(ideology, serverPlayer.getRandom());
        ideologyData.ideologies.put(ideology.id, ideology);
        ideologyData.changed();
        TraitEffects.resetTimers(serverPlayer);
        serverPlayer.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.created", (Object[])new Object[]{string}).withStyle(ChatFormatting.GOLD));
        serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.2f);
        IdeologyManager.refreshMembers(serverPlayer.getServer(), ideology);
    }

    public static void invite(ServerPlayer serverPlayer, String string) {
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology == null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.no_ideology");
            return;
        }
        ServerPlayer serverPlayer2 = serverPlayer.getServer().getPlayerList().getPlayerByName(string);
        if (serverPlayer2 == null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.no_player");
            return;
        }
        if (IdeologyManager.of(serverPlayer2) != null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.target_has");
            return;
        }
        ideology.invites.add(serverPlayer2.getUUID());
        IdeologyManager.data(serverPlayer.getServer()).changed();
        serverPlayer.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.invited", (Object[])new Object[]{serverPlayer2.getScoreboardName()}));
        serverPlayer2.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.invite_got", (Object[])new Object[]{ideology.name, serverPlayer.getScoreboardName()}).withStyle(ChatFormatting.GOLD));
    }

    public static void accept(ServerPlayer serverPlayer, String string) {
        IdeologyData ideologyData = IdeologyManager.data(serverPlayer.getServer());
        if (ideologyData.of(serverPlayer.getUUID()) != null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.already_member");
            return;
        }
        for (Ideology ideology : ideologyData.ideologies.values()) {
            if (!IdeologyManager.shortId(ideology.id).equals(string) || !ideology.invites.contains(serverPlayer.getUUID())) continue;
            ideology.invites.remove(serverPlayer.getUUID());
            ideology.members.put(serverPlayer.getUUID(), serverPlayer.getScoreboardName());
            IdeologyManager.clearRequests(ideologyData, serverPlayer.getUUID());
            ideologyData.changed();
            TraitEffects.resetTimers(serverPlayer);
            IdeologyManager.notifyAll(serverPlayer.getServer(), ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.joined", (Object[])new Object[]{serverPlayer.getScoreboardName(), ideology.name}));
            IdeologyManager.refreshMembers(serverPlayer.getServer(), ideology);
            return;
        }
        IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.no_invite");
    }

    public static void leave(ServerPlayer serverPlayer) {
        IdeologyData ideologyData = IdeologyManager.data(serverPlayer.getServer());
        Ideology ideology = ideologyData.of(serverPlayer.getUUID());
        if (ideology == null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.no_ideology");
            return;
        }
        ideology.members.remove(serverPlayer.getUUID());
        if (ideology.members.isEmpty()) {
            ideologyData.ideologies.remove(ideology.id);
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.disbanded");
        } else {
            if (ideology.leader.equals(serverPlayer.getUUID())) {
                ideology.leader = ideology.members.keySet().iterator().next();
            }
            IdeologyManager.notifyAll(serverPlayer.getServer(), ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.left", (Object[])new Object[]{serverPlayer.getScoreboardName()}));
            IdeologyManager.refreshMembers(serverPlayer.getServer(), ideology);
        }
        ideologyData.changed();
        IdeologyManager.syncManifest(serverPlayer);
    }

    public static void kick(ServerPlayer serverPlayer, String string) {
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology == null || !ideology.leader.equals(serverPlayer.getUUID())) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.not_leader");
            return;
        }
        UUID uUID = null;
        for (Map.Entry<UUID, String> entry : ideology.members.entrySet()) {
            if (!entry.getValue().equalsIgnoreCase(string)) continue;
            uUID = entry.getKey();
        }
        if (uUID == null || uUID.equals(serverPlayer.getUUID())) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.no_player");
            return;
        }
        ideology.members.remove(uUID);
        IdeologyManager.data(serverPlayer.getServer()).changed();
        IdeologyManager.notifyAll(serverPlayer.getServer(), ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.kicked", (Object[])new Object[]{string}));
        ServerPlayer serverPlayer2 = serverPlayer.getServer().getPlayerList().getPlayer(uUID);
        if (serverPlayer2 != null) {
            IdeologyManager.syncManifest(serverPlayer2);
        }
        IdeologyManager.refreshMembers(serverPlayer.getServer(), ideology);
    }

    private static Ideology byShort(IdeologyData ideologyData, String string) {
        for (Ideology ideology : ideologyData.ideologies.values()) {
            if (!IdeologyManager.shortId(ideology.id).equals(string)) continue;
            return ideology;
        }
        return null;
    }

    private static void clearRequests(IdeologyData ideologyData, UUID uUID) {
        for (Ideology ideology : ideologyData.ideologies.values()) {
            ideology.requests.remove(uUID);
        }
    }

    public static void request(ServerPlayer serverPlayer, String string) {
        IdeologyData ideologyData = IdeologyManager.data(serverPlayer.getServer());
        if (ideologyData.of(serverPlayer.getUUID()) != null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.already_member");
            return;
        }
        Ideology ideology = IdeologyManager.byShort(ideologyData, string);
        if (ideology == null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.no_ideology_found");
            return;
        }
        if (ideology.requests.remove(serverPlayer.getUUID()) != null) {
            serverPlayer.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.request_cancel", (Object[])new Object[]{ideology.name}));
        } else {
            ideology.requests.put(serverPlayer.getUUID(), serverPlayer.getScoreboardName());
            serverPlayer.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.request_sent", (Object[])new Object[]{ideology.name}).withStyle(ChatFormatting.YELLOW));
            ServerPlayer serverPlayer2 = serverPlayer.getServer().getPlayerList().getPlayer(ideology.leader);
            if (serverPlayer2 != null) {
                serverPlayer2.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.request_got", (Object[])new Object[]{serverPlayer.getScoreboardName()}).withStyle(ChatFormatting.GOLD));
            }
        }
        ideologyData.changed();
    }

    public static void approve(ServerPlayer serverPlayer, String string, boolean bl) {
        IdeologyData ideologyData = IdeologyManager.data(serverPlayer.getServer());
        Ideology ideology = ideologyData.of(serverPlayer.getUUID());
        if (ideology == null || !ideology.leader.equals(serverPlayer.getUUID())) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.not_leader");
            return;
        }
        UUID uUID = null;
        for (Map.Entry<UUID, String> serverPlayer22 : ideology.requests.entrySet()) {
            if (!serverPlayer22.getValue().equalsIgnoreCase(string)) continue;
            uUID = serverPlayer22.getKey();
        }
        if (uUID == null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.no_player");
            return;
        }
        String string2 = (String)ideology.requests.remove(uUID);
        ServerPlayer serverPlayer2 = serverPlayer.getServer().getPlayerList().getPlayer(uUID);
        if (!bl) {
            if (serverPlayer2 != null) {
                serverPlayer2.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.request_denied", (Object[])new Object[]{ideology.name}).withStyle(ChatFormatting.RED));
            }
            ideologyData.changed();
            return;
        }
        if (ideologyData.of(uUID) != null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.target_has");
            ideologyData.changed();
            return;
        }
        ideology.members.put(uUID, string2);
        IdeologyManager.clearRequests(ideologyData, uUID);
        ideology.invites.remove(uUID);
        ideologyData.changed();
        if (serverPlayer2 != null) {
            TraitEffects.resetTimers(serverPlayer2);
        }
        IdeologyManager.notifyAll(serverPlayer.getServer(), ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.joined", (Object[])new Object[]{string2, ideology.name}).withStyle(ChatFormatting.GOLD));
    }

    private static Ideology leaderOf(ServerPlayer serverPlayer) {
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology == null || !ideology.leader.equals(serverPlayer.getUUID())) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.not_leader");
            return null;
        }
        return ideology;
    }

    public static void setName(ServerPlayer serverPlayer, String string) {
        Ideology ideology = IdeologyManager.leaderOf(serverPlayer);
        if (ideology == null) {
            return;
        }
        if ((string = string.trim()).isEmpty() || string.length() > 32) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.bad_name");
            return;
        }
        ideology.name = string;
        IdeologyManager.data(serverPlayer.getServer()).changed();
    }

    public static void setDesc(ServerPlayer serverPlayer, String string) {
        Ideology ideology = IdeologyManager.leaderOf(serverPlayer);
        if (ideology == null) {
            return;
        }
        if ((string = string.replace('\n', ' ').trim()).equals("-")) {
            string = "";
        }
        ideology.desc = string.length() > 200 ? string.substring(0, 200) : string;
        IdeologyManager.data(serverPlayer.getServer()).changed();
    }

    public static void setColor(ServerPlayer serverPlayer, int n) {
        Ideology ideology = IdeologyManager.leaderOf(serverPlayer);
        if (ideology == null) {
            return;
        }
        ideology.color = n & 0xFFFFFF;
        IdeologyManager.data(serverPlayer.getServer()).changed();
    }

    public static void setIcon(ServerPlayer serverPlayer, String string) {
        Ideology ideology = IdeologyManager.leaderOf(serverPlayer);
        if (ideology == null || !Ideology.validIcon(string)) {
            return;
        }
        ideology.icon = string;
        IdeologyManager.data(serverPlayer.getServer()).changed();
    }

    public static void setStructure(ServerPlayer serverPlayer, IdeologyStructure ideologyStructure) {
        Ideology ideology = IdeologyManager.leaderOf(serverPlayer);
        if (ideology == null || ideologyStructure == null || ideology.structure == ideologyStructure) {
            return;
        }
        ideology.structure = ideologyStructure;
        IdeologyManager.data(serverPlayer.getServer()).changed();
        IdeologyManager.notifyAll(serverPlayer.getServer(), ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.structure_changed", (Object[])new Object[]{Component.translatable((String)("ideology.coaldust.structure." + ideologyStructure.id))}).withStyle(ChatFormatting.GOLD));
    }

    public static void pickTrait(ServerPlayer serverPlayer, Trait trait) {
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology == null || !ideology.leader.equals(serverPlayer.getUUID())) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.not_leader");
            return;
        }
        if (ideology.picks <= 0 || ideology.traits.size() >= 3) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.no_picks");
            return;
        }
        if (ideology.has(trait)) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.has_trait");
            return;
        }
        if (trait.abyss && !Abyss.inAbyss(serverPlayer)) {
            return;
        }
        ideology.traits.add(trait);
        --ideology.picks;
        IdeologyManager.ensureQuests(ideology, serverPlayer.getRandom());
        IdeologyManager.data(serverPlayer.getServer()).changed();
        IdeologyManager.notifyAll(serverPlayer.getServer(), ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.trait_added", (Object[])new Object[]{Component.translatable((String)("ideology.coaldust.trait." + trait.id))}).withStyle(ChatFormatting.GOLD));
        for (ServerPlayer serverPlayer2 : IdeologyManager.online(serverPlayer.getServer(), ideology)) {
            TraitEffects.resetTimers(serverPlayer2);
        }
        IdeologyManager.refreshMembers(serverPlayer.getServer(), ideology);
    }

    public static void ensureQuests(Ideology ideology, RandomSource randomSource) {
        ideology.active.keySet().removeIf(trait -> !ideology.traits.contains(trait));
        Object object = ideology.traits.iterator();
        while (object.hasNext()) {
            Trait trait2 = object.next();
            Quest quest2 = ideology.active.get((Object)trait2);
            if (quest2 != null && IdeologyManager.possible(ideology, quest2)) continue;
            if (quest2 != null) {
                ideology.progress.remove((Object)quest2);
            }
            ideology.active.put(trait2, IdeologyManager.randomQuest(ideology, trait2, quest2, randomSource));
        }
        ideology.general.removeIf(quest -> !IdeologyManager.possible(ideology, quest));
        while (ideology.general.size() < 3 && (object = IdeologyManager.randomQuest(ideology, null, null, randomSource)) != null) {
            ideology.general.add((Quest)((Object)object));
        }
    }

    static boolean possible(Ideology ideology, Quest quest) {
        return !quest.needsGroup() || ideology.members.size() >= (quest == Quest.MEMBERS ? 3 : 2);
    }

    private static Quest randomQuest(Ideology ideology, Trait trait, Quest quest, RandomSource randomSource) {
        ArrayList<Quest> arrayList = new ArrayList<Quest>();
        for (Quest quest2 : Quest.values()) {
            if (quest2.trait != trait || quest2 == quest || !IdeologyManager.possible(ideology, quest2) || ideology.general.contains((Object)quest2)) continue;
            arrayList.add(quest2);
        }
        if (arrayList.isEmpty()) {
            for (Quest quest2 : Quest.values()) {
                if (quest2.trait != trait || quest2 == quest) continue;
                arrayList.add(quest2);
            }
        }
        return arrayList.isEmpty() ? null : (Quest)((Object)arrayList.get(randomSource.nextInt(arrayList.size())));
    }

    static boolean isActive(Ideology ideology, Quest quest) {
        return quest.trait == null ? ideology.general.contains((Object)quest) : ideology.active.get((Object)quest.trait) == quest;
    }

    static List<Quest> activeQuests(Ideology ideology) {
        ArrayList<Quest> arrayList = new ArrayList<Quest>(ideology.active.values());
        arrayList.addAll(ideology.general);
        return arrayList;
    }

    public static void progress(ServerPlayer serverPlayer, Quest quest, int n) {
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology == null || !IdeologyManager.isActive(ideology, quest) || n <= 0) {
            return;
        }
        IdeologyManager.setProgress(serverPlayer.getServer(), ideology, quest, ideology.progress.getOrDefault((Object)quest, 0) + n, serverPlayer.getRandom());
    }

    public static void setProgress(MinecraftServer minecraftServer, Ideology ideology, Quest quest, int n, RandomSource randomSource) {
        if (!IdeologyManager.isActive(ideology, quest)) {
            return;
        }
        if (n >= ideology.target(quest)) {
            int n2 = ideology.reward(quest);
            ideology.progress.remove((Object)quest);
            if (quest.trait == null) {
                int n3 = ideology.general.indexOf((Object)quest);
                ideology.general.remove((Object)quest);
                Quest quest2 = IdeologyManager.randomQuest(ideology, null, quest, randomSource);
                if (quest2 != null) {
                    ideology.general.add(Math.min(n3, ideology.general.size()), quest2);
                }
            } else {
                Quest quest3 = IdeologyManager.randomQuest(ideology, quest.trait, quest, randomSource);
                if (quest3 != null) {
                    ideology.active.put(quest.trait, quest3);
                }
            }
            IdeologyManager.notifyAll(minecraftServer, ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.quest_done", (Object[])new Object[]{Component.translatable((String)("ideology.coaldust.quest." + quest.id())), n2}).withStyle(ChatFormatting.GREEN));
            IdeologyManager.addXp(minecraftServer, ideology, n2);
            Abyss.onQuestDone(minecraftServer, ideology);
        } else {
            ideology.progress.put(quest, n);
        }
        IdeologyManager.data(minecraftServer).setDirty();
    }

    public static void reroll(ServerPlayer serverPlayer) {
        Quest quest;
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology == null) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.no_ideology");
            return;
        }
        if (!ideology.leader.equals(serverPlayer.getUUID())) {
            IdeologyManager.msg(serverPlayer, "ideology.coaldust.msg.not_leader");
            return;
        }
        long l = serverPlayer.level().getGameTime();
        if (l - ideology.lastReroll < 24000L) {
            serverPlayer.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.reroll_wait", (Object[])new Object[]{(24000L - (l - ideology.lastReroll) + 1199L) / 1200L}).withStyle(ChatFormatting.RED));
            return;
        }
        ideology.lastReroll = l;
        for (Quest enum_2 : IdeologyManager.activeQuests(ideology)) {
            ideology.progress.remove((Object)enum_2);
        }
        for (Trait trait : ideology.traits) {
            ideology.active.put(trait, IdeologyManager.randomQuest(ideology, trait, ideology.active.get((Object)trait), serverPlayer.getRandom()));
        }
        ArrayList<Quest> arrayList = new ArrayList<Quest>(ideology.general);
        ideology.general.clear();
        while (ideology.general.size() < 3 && (quest = IdeologyManager.randomQuest(ideology, null, null, serverPlayer.getRandom())) != null) {
            if (arrayList.contains((Object)quest) && IdeologyManager.r2(serverPlayer) < 0.7f) continue;
            ideology.general.add(quest);
        }
        IdeologyManager.data(serverPlayer.getServer()).setDirty();
        IdeologyManager.notifyAll(serverPlayer.getServer(), ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.rerolled").withStyle(ChatFormatting.YELLOW));
    }

    private static float r2(ServerPlayer serverPlayer) {
        return serverPlayer.getRandom().nextFloat();
    }

    public static void resetProgress(ServerPlayer serverPlayer, Quest quest) {
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology != null && ideology.progress.containsKey((Object)quest)) {
            ideology.progress.put(quest, 0);
            IdeologyManager.data(serverPlayer.getServer()).setDirty();
        }
    }

    public static void addXp(MinecraftServer minecraftServer, Ideology ideology, int n) {
        double d = 1.0;
        if (ideology.structure == IdeologyStructure.CHURCH) {
            d *= 1.25;
        }
        if (ideology.has(Trait.COLLECTIVISM)) {
            d *= 1.0 + 0.1 * (double)ideology.members.size();
        }
        if (ideology.has(Trait.INDIVIDUALISM) && ideology.members.size() == 1) {
            d *= 1.5;
        }
        ideology.xp += (int)Math.round((double)n * d);
        while (ideology.level < 15 && ideology.xp >= ideology.xpForNext()) {
            ideology.xp -= ideology.xpForNext();
            ++ideology.level;
            if ((ideology.level == 2 || ideology.level == 3) && ideology.traits.size() + ideology.picks < 3) {
                ++ideology.picks;
            }
            IdeologyManager.notifyAll(minecraftServer, ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.level_up", (Object[])new Object[]{ideology.name, ideology.level}).withStyle(ChatFormatting.GOLD));
            if (ideology.level == 5) {
                IdeologyManager.notifyAll(minecraftServer, ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.buffs_unlocked").withStyle(ChatFormatting.GREEN));
            }
            if (ideology.level == ideology.strongLevel()) {
                IdeologyManager.notifyAll(minecraftServer, ideology, (Component)Component.translatable((String)"ideology.coaldust.msg.buffs_strong").withStyle(ChatFormatting.AQUA));
            }
            for (ServerPlayer serverPlayer : IdeologyManager.online(minecraftServer, ideology)) {
                serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                if (ideology.picks <= 0 || !serverPlayer.getUUID().equals(ideology.leader)) continue;
                serverPlayer.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.pick_trait").withStyle(ChatFormatting.YELLOW));
            }
        }
        if (ideology.level >= 15) {
            ideology.xp = Math.min(ideology.xp, ideology.xpForNext());
        }
        IdeologyManager.data(minecraftServer).setDirty();
    }

    public static List<ServerPlayer> online(MinecraftServer minecraftServer, Ideology ideology) {
        ArrayList<ServerPlayer> arrayList = new ArrayList<ServerPlayer>();
        for (UUID uUID : ideology.members.keySet()) {
            ServerPlayer serverPlayer = minecraftServer.getPlayerList().getPlayer(uUID);
            if (serverPlayer == null) continue;
            arrayList.add(serverPlayer);
        }
        return arrayList;
    }

    public static void notifyAll(MinecraftServer minecraftServer, Ideology ideology, Component component) {
        for (ServerPlayer serverPlayer : IdeologyManager.online(minecraftServer, ideology)) {
            serverPlayer.sendSystemMessage(component);
        }
    }

    private static void msg(ServerPlayer serverPlayer, String string) {
        serverPlayer.sendSystemMessage((Component)Component.translatable((String)string).withStyle(ChatFormatting.RED));
    }

    private static void refreshMembers(MinecraftServer minecraftServer, Ideology ideology) {
        for (ServerPlayer serverPlayer : IdeologyManager.online(minecraftServer, ideology)) {
            IdeologyManager.syncManifest(serverPlayer);
        }
    }

    public static void giveManifest(ServerPlayer serverPlayer) {
        for (ItemStack itemStack : serverPlayer.getInventory().items) {
            if (!itemStack.is((Item)ModItems.MANIFEST.get())) continue;
            return;
        }
        Iterator iterator = new ItemStack((ItemLike)ModItems.MANIFEST.get());
        if (!serverPlayer.getInventory().add((ItemStack)iterator)) {
            serverPlayer.drop((ItemStack)iterator, false);
        }
    }

    public static CompoundTag view(ServerPlayer serverPlayer) {
        CompoundTag compoundTag = new CompoundTag();
        IdeologyData ideologyData = IdeologyManager.data(serverPlayer.getServer());
        Ideology ideology = ideologyData.of(serverPlayer.getUUID());
        if (ideology == null) {
            compoundTag.putBoolean("has", false);
            StringBuilder stringBuilder = new StringBuilder();
            for (Ideology ideology2 : ideologyData.ideologies.values()) {
                if (!ideology2.invites.contains(serverPlayer.getUUID())) continue;
                stringBuilder.append(IdeologyManager.shortId(ideology2.id)).append('=').append(ideology2.name.replace(";", "")).append(';');
            }
            compoundTag.putString("invites", stringBuilder.toString());
            return compoundTag;
        }
        compoundTag.putBoolean("has", true);
        compoundTag.putString("name", ideology.name);
        compoundTag.putString("structure", ideology.structure.id);
        StringBuilder stringBuilder = new StringBuilder();
        for (Trait object2 : ideology.traits) {
            stringBuilder.append(object2.id).append(',');
        }
        compoundTag.putString("traits", stringBuilder.toString());
        compoundTag.putInt("level", ideology.level);
        compoundTag.putInt("xp", ideology.xp);
        compoundTag.putInt("next", ideology.xpForNext());
        compoundTag.putBoolean("leader", ideology.leader.equals(serverPlayer.getUUID()));
        compoundTag.putInt("picks", ideology.leader.equals(serverPlayer.getUUID()) ? ideology.picks : 0);
        StringBuilder stringBuilder2 = new StringBuilder();
        for (Map.Entry<UUID, String> entry : ideology.members.entrySet()) {
            boolean bl = serverPlayer.getServer().getPlayerList().getPlayer(entry.getKey()) != null;
            stringBuilder2.append(entry.getKey().equals(ideology.leader) ? "*" : "").append(entry.getValue()).append(bl ? "" : " (off)").append(';');
        }
        compoundTag.putString("members", stringBuilder2.toString());
        StringBuilder stringBuilder3 = new StringBuilder();
        for (Quest quest : IdeologyManager.activeQuests(ideology)) {
            stringBuilder3.append(quest.id()).append(':').append(ideology.progress.getOrDefault((Object)quest, 0)).append(':').append(ideology.target(quest)).append(quest.trait == null ? ":g" : "").append(';');
        }
        compoundTag.putString("quests", stringBuilder3.toString());
        return compoundTag;
    }

    public static void sync(ServerPlayer serverPlayer) {
        Object object2;
        Object object3;
        int n;
        IdeologyData ideologyData = IdeologyManager.data(serverPlayer.getServer());
        Ideology ideology = ideologyData.of(serverPlayer.getUUID());
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("me", ideology == null ? "" : IdeologyManager.shortId(ideology.id));
        List<Ideology> list = new ArrayList<Ideology>(ideologyData.ideologies.values());
        list.sort((ideology2, ideology3) -> ideology2 == ideology ? -1 : (ideology3 == ideology ? 1 : (ideology3.level != ideology2.level ? ideology3.level - ideology2.level : ideology3.members.size() - ideology2.members.size())));
        if (list.size() > 60) {
            list = list.subList(0, 60);
        }
        compoundTag.putInt("n", list.size());
        for (n = 0; n < list.size(); ++n) {
            object3 = list.get(n);
            object2 = new CompoundTag();
            object2.putString("id", IdeologyManager.shortId(((Ideology)object3).id));
            object2.putString("name", ((Ideology)object3).name);
            object2.putString("desc", ((Ideology)object3).desc);
            object2.putInt("color", ((Ideology)object3).color);
            object2.putString("icon", ((Ideology)object3).iconId());
            object2.putString("structure", ((Ideology)object3).structure.id);
            StringBuilder object4 = new StringBuilder();
            for (Trait trait : ((Ideology)object3).traits) {
                object4.append(trait.id).append(',');
            }
            object2.putString("traits", object4.toString());
            object2.putInt("level", ((Ideology)object3).level);
            object2.putInt("xp", ((Ideology)object3).xp);
            object2.putInt("next", ((Ideology)object3).xpForNext());
            object2.putInt("count", ((Ideology)object3).members.size());
            object2.putString("leaderName", ((Ideology)object3).members.getOrDefault(((Ideology)object3).leader, "?"));
            object2.putBoolean("requested", ((Ideology)object3).requests.containsKey(serverPlayer.getUUID()));
            compoundTag.put("i" + n, (Tag)object2);
        }
        if (ideology != null) {
            n = ideology.leader.equals(serverPlayer.getUUID()) ? 1 : 0;
            compoundTag.putBoolean("leader", n != 0);
            compoundTag.putInt("picks", n != 0 ? ideology.picks : 0);
            object3 = new StringBuilder();
            for (Map.Entry entry : ideology.members.entrySet()) {
                boolean bl = serverPlayer.getServer().getPlayerList().getPlayer((UUID)entry.getKey()) != null;
                ((StringBuilder)object3).append((String)entry.getValue()).append('|').append(bl ? 1 : 0).append('|').append(((UUID)entry.getKey()).equals(ideology.leader) ? 1 : 0).append(';');
            }
            compoundTag.putString("members", ((StringBuilder)object3).toString());
            object2 = new StringBuilder();
            for (String string : ideology.requests.values()) {
                ((StringBuilder)object2).append(string).append(';');
            }
            compoundTag.putString("requests", ((StringBuilder)object2).toString());
            StringBuilder stringBuilder = new StringBuilder();
            IdeologyManager.ensureQuests(ideology, serverPlayer.getRandom());
            for (Quest quest : IdeologyManager.activeQuests(ideology)) {
                stringBuilder.append(quest.id()).append(':').append(ideology.progress.getOrDefault((Object)quest, 0)).append(':').append(ideology.target(quest)).append(quest.trait == null ? ":g" : "").append(';');
            }
            compoundTag.putBoolean("canReroll", ideology.leader.equals(serverPlayer.getUUID()));
            compoundTag.putInt("rerollMin", (int)Math.max(0L, (24000L - (serverPlayer.level().getGameTime() - ideology.lastReroll) + 1199L) / 1200L));
            compoundTag.putString("quests", stringBuilder.toString());
        }
        Abyss.writeSync(serverPlayer, compoundTag);
        String string = compoundTag.toString();
        object3 = Component.literal((String)SYNC_PREFIX);
        for (int i = 0; i < string.length(); i += 8000) {
            object3.append((Component)Component.literal((String)string.substring(i, Math.min(string.length(), i + 8000))));
        }
        serverPlayer.sendSystemMessage((Component)object3, true);
    }

    public static void syncManifest(ServerPlayer serverPlayer) {
        CompoundTag compoundTag = IdeologyManager.view(serverPlayer);
        for (ItemStack itemStack : serverPlayer.getInventory().items) {
            IdeologyManager.writeView(itemStack, compoundTag);
        }
        IdeologyManager.writeView((ItemStack)serverPlayer.getInventory().offhand.get(0), compoundTag);
    }

    private static void writeView(ItemStack itemStack, CompoundTag compoundTag) {
        if (itemStack.isEmpty() || !itemStack.is((Item)ModItems.MANIFEST.get())) {
            return;
        }
        CompoundTag compoundTag2 = ((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY)).copyTag();
        if (compoundTag.equals(compoundTag2.getCompound("ideology"))) {
            return;
        }
        compoundTag2.put("ideology", (Tag)compoundTag);
        itemStack.set(DataComponents.CUSTOM_DATA, (Object)CustomData.of((CompoundTag)compoundTag2));
    }

    private IdeologyManager() {
    }
}
