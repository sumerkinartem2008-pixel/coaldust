/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Fallable
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.ChestBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 */
package com.coaldust.meteor;

import com.coaldust.meteor.ancient.AncientLoot;
import com.coaldust.meteor.underground.UndergroundCity;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class BuildJob {
    public static final int BLOCKS_PER_TICK = 6000;
    final ServerLevel level;
    private final BlockPos origin;
    private final long[] keys;
    private final BlockState[] states;
    private final UndergroundCity.Plan plan;
    private int index;

    public BuildJob(ServerLevel serverLevel, BlockPos blockPos, UndergroundCity.Plan plan) {
        this.level = serverLevel;
        this.origin = blockPos;
        this.plan = plan;
        int n3 = plan.blocks.size();
        this.keys = new long[n3];
        this.states = new BlockState[n3];
        int n4 = 0;
        for (Map.Entry<Long, BlockState> entry : plan.blocks.entrySet()) {
            this.keys[n4] = entry.getKey();
            this.states[n4] = entry.getValue();
            ++n4;
        }
        Integer[] integerArray = new Integer[n3];
        for (int i = 0; i < n3; ++i) {
            integerArray[i] = i;
        }
        Arrays.sort(integerArray, (n, n2) -> Integer.compare(UndergroundCity.Plan.ky(this.keys[n]), UndergroundCity.Plan.ky(this.keys[n2])));
        long[] lArray = new long[n3];
        BlockState[] blockStateArray = new BlockState[n3];
        for (int i = 0; i < n3; ++i) {
            lArray[i] = this.keys[integerArray[i]];
            blockStateArray[i] = this.states[integerArray[i]];
        }
        System.arraycopy(lArray, 0, this.keys, 0, n3);
        System.arraycopy(blockStateArray, 0, this.states, 0, n3);
    }

    private void seal() {
        int[][] nArrayArray = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {0, 1, 0}};
        BlockState blockState = Blocks.COBBLED_DEEPSLATE.defaultBlockState();
        for (int i = 0; i < this.keys.length; ++i) {
            if (!this.states[i].isAir()) continue;
            long l = this.keys[i];
            int n = UndergroundCity.Plan.kx(l);
            int n2 = UndergroundCity.Plan.ky(l);
            int n3 = UndergroundCity.Plan.kz(l);
            for (int[] nArray : nArrayArray) {
                BlockState blockState2;
                BlockPos blockPos;
                long l2 = UndergroundCity.Plan.key(n + nArray[0], n2 + nArray[1], n3 + nArray[2]);
                if (this.plan.blocks.containsKey(l2) || !this.level.isLoaded(blockPos = this.origin.offset(n + nArray[0], n2 + nArray[1], n3 + nArray[2])) || !(blockState2 = this.level.getBlockState(blockPos)).is(Blocks.WATER) && !blockState2.is(Blocks.LAVA)) continue;
                this.level.setBlock(blockPos, blockState, 2);
            }
        }
    }

    private void cleanFalling() {
        int[][] nArrayArray = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {0, 1, 0}, {0, -1, 0}};
        BlockState blockState = Blocks.AIR.defaultBlockState();
        int n = Integer.MAX_VALUE;
        int n2 = Integer.MAX_VALUE;
        int n3 = Integer.MAX_VALUE;
        int n4 = Integer.MIN_VALUE;
        int n5 = Integer.MIN_VALUE;
        int n6 = Integer.MIN_VALUE;
        for (int i = 0; i < this.keys.length; ++i) {
            long l = this.keys[i];
            int n7 = UndergroundCity.Plan.kx(l);
            int n8 = UndergroundCity.Plan.ky(l);
            int n9 = UndergroundCity.Plan.kz(l);
            n = Math.min(n, n7);
            n2 = Math.min(n2, n8);
            n3 = Math.min(n3, n9);
            n4 = Math.max(n4, n7);
            n5 = Math.max(n5, n8);
            n6 = Math.max(n6, n9);
            if (!this.states[i].isAir()) continue;
            BlockPos blockPos = this.origin.offset(n7, n8, n9);
            if (this.level.isLoaded(blockPos) && this.level.getBlockState(blockPos).getBlock() instanceof Fallable) {
                this.level.setBlock(blockPos, blockState, 18);
            }
            for (int[] nArray : nArrayArray) {
                BlockState blockState2;
                BlockPos blockPos2;
                long l2 = UndergroundCity.Plan.key(n7 + nArray[0], n8 + nArray[1], n9 + nArray[2]);
                if (this.plan.blocks.containsKey(l2) || !this.level.isLoaded(blockPos2 = this.origin.offset(n7 + nArray[0], n8 + nArray[1], n9 + nArray[2])) || !((blockState2 = this.level.getBlockState(blockPos2)).getBlock() instanceof Fallable)) continue;
                this.level.setBlock(blockPos2, BuildJob.solidOf(blockState2), 18);
            }
        }
        if (n > n4) {
            return;
        }
        BlockPos blockPos = this.origin.offset(n - 4, n2 - 4, n3 - 4);
        BlockPos blockPos3 = this.origin.offset(n4 + 4, n5 + 24, n6 + 4);
        List<FallingBlockEntity> list = this.level.getEntitiesOfClass(FallingBlockEntity.class, new AABB((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (double)(blockPos3.getX() + 1), (double)(blockPos3.getY() + 1), (double)(blockPos3.getZ() + 1)));
        if (list != null) {
            for (FallingBlockEntity fallingBlockEntity : list) {
                fallingBlockEntity.discard();
            }
        }
    }

    private static BlockState solidOf(BlockState blockState) {
        if (blockState.is(Blocks.SAND) || blockState.is(Blocks.SUSPICIOUS_SAND)) {
            return Blocks.SANDSTONE.defaultBlockState();
        }
        if (blockState.is(Blocks.RED_SAND)) {
            return Blocks.RED_SANDSTONE.defaultBlockState();
        }
        if (blockState.is(Blocks.GRAVEL) || blockState.is(Blocks.SUSPICIOUS_GRAVEL)) {
            return Blocks.COBBLESTONE.defaultBlockState();
        }
        return Blocks.COBBLED_DEEPSLATE.defaultBlockState();
    }

        public boolean tick() {
        int end = Math.min(this.keys.length, this.index + 6000);
        while (this.index < end) {
            long key = this.keys[this.index];
            BlockPos pos = this.origin.offset(UndergroundCity.Plan.kx(key), UndergroundCity.Plan.ky(key), UndergroundCity.Plan.kz(key));
            if (this.level.isLoaded(pos) && !this.level.getBlockState(pos).is(Blocks.BEDROCK) && (this.level.getBlockEntity(pos) == null || this.states[this.index].is(Blocks.CHEST))) {
                this.level.setBlock(pos, this.states[this.index], 18);
            }
            ++this.index;
        }
        if (this.index < this.keys.length) {
            return false;
        }
        this.seal();
        this.cleanFalling();
        AncientLoot.Pools pools = AncientLoot.Pools.collect();
        for (int i = 0; i < this.plan.chests.size(); ++i) {
            var chest = this.plan.chests.get(i);
            BlockPos chestPos = this.origin.offset((int)chest[0], (int)chest[1], (int)chest[2]);
            BlockEntity blockEntity = this.level.getBlockEntity(chestPos);
            if (!(blockEntity instanceof ChestBlockEntity chestEntity)) continue;
            AncientLoot.fill(chestEntity, this.level.getRandom(), pools, this.plan.richChests.get(i));
        }
        return true;
    }
}
