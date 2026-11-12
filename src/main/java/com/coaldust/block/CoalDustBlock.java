/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.coaldust.block;

import com.coaldust.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CoalDustBlock
extends Block {
    public static final int MAX_SPREAD = 2;
    public static final int MAX_DENSITY = 2;
    public static final int MAX_AGE = 7;
    public static final IntegerProperty SPREAD = IntegerProperty.create((String)"spread", (int)0, (int)2);
    public static final IntegerProperty DENSITY = IntegerProperty.create((String)"density", (int)0, (int)2);
    public static final IntegerProperty AGE = IntegerProperty.create((String)"age", (int)0, (int)7);
    public static final int SPREAD_DELAY = 4;
    public static final int LIFE_TICK = 100;
    public static final int THICKEN_UNTIL_AGE = 4;
    public static final int THIN_FROM_AGE = 4;
    public static final float THIN_CHANCE = 0.25f;
    private static final Direction[] SIDES = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static final Direction[] ALL = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN};

    public CoalDustBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SPREAD, DENSITY, AGE});
    }

    public boolean placeDust(ServerLevel serverLevel, BlockPos blockPos, int n, RandomSource randomSource) {
        BlockState blockState = (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)SPREAD, (Comparable)Integer.valueOf(n))).setValue((Property)DENSITY, (Comparable)Integer.valueOf(0))).setValue((Property)AGE, (Comparable)Integer.valueOf(0));
        if (!serverLevel.setBlock(blockPos, blockState, 3)) {
            return false;
        }
        serverLevel.scheduleTick(blockPos, (Block)this, n > 0 ? 4 : 100);
        return true;
    }

    protected void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        int n = (Integer)blockState.getValue((Property)SPREAD);
        if (n > 0) {
            this.spreadAround(serverLevel, blockPos, n - 1, randomSource);
            serverLevel.setBlock(blockPos, (BlockState)blockState.setValue((Property)SPREAD, (Comparable)Integer.valueOf(0)), 2);
            serverLevel.scheduleTick(blockPos, (Block)this, 100);
            return;
        }
        int n2 = (Integer)blockState.getValue((Property)DENSITY);
        int n3 = (Integer)blockState.getValue((Property)AGE);
        if (n3 < 4 && n2 < 2 && this.isSurroundedByDust(serverLevel, blockPos)) {
            ++n2;
        } else if (n3 >= 4 && randomSource.nextFloat() < 0.25f) {
            if (n2 == 0) {
                serverLevel.removeBlock(blockPos, false);
                return;
            }
            --n2;
        }
        BlockState blockState2 = (BlockState)((BlockState)blockState.setValue((Property)DENSITY, (Comparable)Integer.valueOf(n2))).setValue((Property)AGE, (Comparable)Integer.valueOf(Math.min(7, n3 + 1)));
        serverLevel.setBlock(blockPos, blockState2, 2);
        serverLevel.scheduleTick(blockPos, (Block)this, 100);
    }

    private boolean isSurroundedByDust(ServerLevel serverLevel, BlockPos blockPos) {
        int n = 0;
        for (Direction direction : ALL) {
            BlockPos blockPos2 = blockPos.relative(direction);
            if (serverLevel.isEmptyBlock(blockPos2)) {
                return false;
            }
            if (!serverLevel.getBlockState(blockPos2).is((Block)this)) continue;
            ++n;
        }
        return n >= 2;
    }

    private void spreadAround(ServerLevel serverLevel, BlockPos blockPos, int n, RandomSource randomSource) {
        boolean bl = false;
        for (Direction direction : SIDES) {
            BlockPos blockPos2 = blockPos.relative(direction);
            if (!serverLevel.isEmptyBlock(blockPos2)) continue;
            bl |= this.placeDust(serverLevel, blockPos2, n, randomSource);
        }
        if (bl) {
            return;
        }
        BlockPos blockPos3 = blockPos.above();
        if (serverLevel.isEmptyBlock(blockPos3) && this.placeDust(serverLevel, blockPos3, n, randomSource)) {
            return;
        }
        BlockPos blockPos4 = blockPos.below();
        if (serverLevel.isEmptyBlock(blockPos4)) {
            this.placeDust(serverLevel, blockPos4, n, randomSource);
        }
    }

    public static int densityAt(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.is((Block)ModBlocks.COAL_DUST.get())) {
            return -1;
        }
        return (Integer)blockState.getValue((Property)DENSITY);
    }

    public static int densityAround(Player player) {
        Level level = player.level();
        int n = CoalDustBlock.densityAt(level, player.blockPosition());
        int n2 = CoalDustBlock.densityAt(level, BlockPos.containing((double)player.getX(), (double)player.getEyeY(), (double)player.getZ()));
        return Math.max(n, n2);
    }

    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.empty();
    }

    protected boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
        return blockState2.is((Block)this) || super.skipRendering(blockState, blockState2, direction);
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        int n = (Integer)blockState.getValue((Property)DENSITY) + 1;
        for (int i = 0; i < n; ++i) {
            if (randomSource.nextInt(3) != 0) continue;
            level.addParticle((ParticleOptions)ParticleTypes.ASH, (double)blockPos.getX() + randomSource.nextDouble(), (double)blockPos.getY() + randomSource.nextDouble(), (double)blockPos.getZ() + randomSource.nextDouble(), 0.0, 0.0, 0.0);
        }
    }
}
