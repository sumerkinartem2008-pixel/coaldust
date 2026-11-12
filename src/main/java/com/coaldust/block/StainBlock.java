/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.coaldust.block;

import com.coaldust.disease.Dysentery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StainBlock
extends Block {
    private static final VoxelShape SHAPE = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)0.5, (double)16.0);
    private final float infectChance;

    public StainBlock(BlockBehaviour.Properties properties, float f) {
        super(properties);
        this.infectChance = f;
    }

    public void place(ServerLevel serverLevel, BlockPos blockPos) {
        serverLevel.setBlock(blockPos, this.defaultBlockState(), 3);
        serverLevel.scheduleTick(blockPos, (Block)this, 2400 + serverLevel.getRandom().nextInt(2400));
    }

    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos.below()).isFaceSturdy((BlockGetter)levelReader, blockPos.below(), Direction.UP);
    }

    protected BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        return direction == Direction.DOWN && !this.canSurvive(blockState, (LevelReader)levelAccessor, blockPos) ? Blocks.AIR.defaultBlockState() : blockState;
    }

    protected void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        serverLevel.removeBlock(blockPos, false);
    }

    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (entity instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)entity;
            if (serverPlayer.tickCount % 20 == 0) {
                Dysentery.INSTANCE.rollInfection(serverPlayer, this.infectChance);
            }
        }
    }
}
