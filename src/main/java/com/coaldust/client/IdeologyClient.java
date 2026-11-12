/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.TagParser
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.client.event.ClientChatReceivedEvent$System
 *  net.neoforged.neoforge.common.NeoForge
 */
package com.coaldust.client;

import com.coaldust.client.EvolveScreen;
import com.coaldust.client.IdeologyScreen;
import com.coaldust.client.WorkerScreen;
import com.coaldust.ideology.Abyss;
import com.coaldust.ideology.AbyssSkills;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class IdeologyClient {
    public static final KeyMapping KEY = new KeyMapping("key.coaldust.ideology", 73, "key.categories.coaldust");
    public static final KeyMapping KEY_EVOLVE = new KeyMapping("key.coaldust.evolve", 71, "key.categories.coaldust");
    public static final KeyMapping KEY_WORKERS = new KeyMapping("key.coaldust.workers", 66, "key.categories.coaldust");
    public static final KeyMapping KEY_SUMMON = new KeyMapping("key.coaldust.summon", 86, "key.categories.coaldust");
    public static CompoundTag data;
    public static int version;

    public static void init(IEventBus iEventBus) {
        iEventBus.addListener(registerKeyMappingsEvent -> {
            registerKeyMappingsEvent.register(KEY);
            registerKeyMappingsEvent.register(KEY_EVOLVE);
            registerKeyMappingsEvent.register(KEY_SUMMON);
            registerKeyMappingsEvent.register(KEY_WORKERS);
        });
        NeoForge.EVENT_BUS.addListener(post -> IdeologyClient.onTick());
        NeoForge.EVENT_BUS.addListener(system -> IdeologyClient.onSystemChat(system));
    }

    private static void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        while (KEY.consumeClick()) {
            if (minecraft.player == null || minecraft.screen != null) continue;
            IdeologyScreen.open();
        }
        if (minecraft.player == null) {
            data = null;
        } else if (data == null && minecraft.player.tickCount % 100 == 40) {
            IdeologyClient.request();
        }
        while (KEY_WORKERS.consumeClick()) {
            if (minecraft.player == null || minecraft.screen != null) continue;
            WorkerScreen.open();
        }
        while (KEY_SUMMON.consumeClick()) {
            if (minecraft.player == null || minecraft.screen != null) continue;
            IdeologyClient.send("ideology summon");
        }
        while (KEY_EVOLVE.consumeClick()) {
            if (minecraft.player == null || minecraft.screen != null) continue;
            EvolveScreen.open();
        }
    }

    private static void onSystemChat(ClientChatReceivedEvent.System system) {
        String string = system.getMessage().getString();
        if (string == null || !string.startsWith("\u00a7coaldust:ideology\u00a7")) {
            return;
        }
        system.setCanceled(true);
        try {
            data = TagParser.parseTag((String)string.substring("\u00a7coaldust:ideology\u00a7".length()));
            ++version;
            Abyss.clientMining = IdeologyClient.ownTraits().contains("evolving") ? AbyssSkills.total(IdeologyClient.skillRanks(), "mining") : 0.0;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static String ownTraits() {
        if (data == null) {
            return "";
        }
        String string = data.getString("me");
        for (int i = 0; i < data.getInt("n"); ++i) {
            CompoundTag compoundTag = data.getCompound("i" + i);
            if (!compoundTag.getString("id").equals(string)) continue;
            return compoundTag.getString("traits");
        }
        return "";
    }

    public static Map<String, Integer> skillRanks() {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        if (data == null) {
            return hashMap;
        }
        for (String string : data.getString("askills").split(";")) {
            int n = string.indexOf(61);
            if (n <= 0) continue;
            try {
                hashMap.put(string.substring(0, n), Integer.parseInt(string.substring(n + 1)));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return hashMap;
    }

    static int skillRank(String string) {
        if (data == null) {
            return 0;
        }
        for (String string2 : data.getString("askills").split(";")) {
            if (!string2.startsWith(string + "=")) continue;
            try {
                return Integer.parseInt(string2.substring(string.length() + 1));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return 0;
    }

    public static void request() {
        IdeologyClient.send("ideology sync");
    }

    public static void send(String string) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            minecraft.getConnection().sendCommand(string);
        }
    }

    private IdeologyClient() {
    }
}
