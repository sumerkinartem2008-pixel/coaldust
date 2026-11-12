/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SweetBerryBushBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.coaldust.block;

import com.coaldust.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class AncientBerryBushBlock
extends SweetBerryBushBlock {
    public AncientBerryBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return new ItemStack((ItemLike)ModItems.ANCIENT_BERRIES.get());
    }

    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
    }

    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        int n = (Integer)blockState.getValue((Property)AGE);
        if (n > 1) {
            int n2 = 1 + level.random.nextInt(2) + (n == 3 ? 1 : 0);
            Block.popResource((Level)level, (BlockPos)blockPos, (ItemStack)new ItemStack((ItemLike)ModItems.ANCIENT_BERRIES.get(), n2));
            level.setBlock(blockPos, (BlockState)blockState.setValue((Property)AGE, (Comparable)Integer.valueOf(1)), 2);
            return InteractionResult.sidedSuccess((boolean)level.isClientSide());
        }
        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
    }
}
