/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 */
package com.coaldust.ideology;

import com.coaldust.ideology.Ideology;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class IdeologyData
extends SavedData {
    public static final String NAME = "coaldust_ideologies";
    public final Map<UUID, Ideology> ideologies = new LinkedHashMap<UUID, Ideology>();
    private final Map<UUID, UUID> playerIndex = new HashMap<UUID, UUID>();

    public static IdeologyData get(MinecraftServer minecraftServer) {
        return (IdeologyData)minecraftServer.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory(IdeologyData::new, IdeologyData::load, null), NAME);
    }

    public static IdeologyData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        IdeologyData ideologyData = new IdeologyData();
        int n = compoundTag.getInt("count");
        for (int i = 0; i < n; ++i) {
            Ideology ideology = Ideology.load(compoundTag.getCompound("i" + i));
            ideologyData.ideologies.put(ideology.id, ideology);
        }
        ideologyData.reindex();
        return ideologyData;
    }

    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        int n = 0;
        for (Ideology ideology : this.ideologies.values()) {
            compoundTag.put("i" + n++, (Tag)ideology.save());
        }
        compoundTag.putInt("count", n);
        return compoundTag;
    }

    public void reindex() {
        this.playerIndex.clear();
        for (Ideology ideology : this.ideologies.values()) {
            for (UUID uUID : ideology.members.keySet()) {
                this.playerIndex.put(uUID, ideology.id);
            }
        }
    }

    public Ideology of(UUID uUID) {
        UUID uUID2 = this.playerIndex.get(uUID);
        return uUID2 == null ? null : this.ideologies.get(uUID2);
    }

    public void changed() {
        this.reindex();
        this.setDirty();
    }
}
