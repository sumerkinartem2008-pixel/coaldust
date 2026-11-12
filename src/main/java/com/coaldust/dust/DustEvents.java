/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.event.level.BlockEvent$BreakEvent
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 */
package com.coaldust.dust;

import com.coaldust.block.CoalDustBlock;
import com.coaldust.block.ModBlocks;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class DustEvents {
    private static final Map<Level, List<BlockPos>> PENDING = new IdentityHashMap<Level, List<BlockPos>>();

    public static void onBlockBreak(BlockEvent.BreakEvent breakEvent) {
        LevelAccessor levelAccessor = breakEvent.getLevel();
        if (!(levelAccessor instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)levelAccessor;
        if (DustEvents.isCoal(breakEvent.getState())) {
            PENDING.computeIfAbsent((Level)serverLevel, level -> new ArrayList()).add(breakEvent.getPos().immutable());
        }
    }

    public static void onLevelTick(LevelTickEvent.Post post) {
        Object object = post.getLevel();
        if (!(object instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)object;
        object = PENDING.remove(serverLevel);
        if (object == null) {
            return;
        }
        CoalDustBlock coalDustBlock = (CoalDustBlock)((Object)ModBlocks.COAL_DUST.get());
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            BlockPos blockPos = (BlockPos)iterator.next();
            if (!serverLevel.isEmptyBlock(blockPos)) continue;
            coalDustBlock.placeDust(serverLevel, blockPos, 2, serverLevel.getRandom());
        }
    }

    public static boolean isCoal(BlockState blockState) {
        return blockState.is(BlockTags.COAL_ORES) || blockState.is(Blocks.COAL_BLOCK);
    }

    private DustEvents() {
    }
}
