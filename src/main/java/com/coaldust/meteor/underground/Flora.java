/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.coaldust.meteor.underground;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public final class Flora {
    public static Supplier<BlockState> LOG = () -> Blocks.DARK_OAK_LOG.defaultBlockState();
    public static Supplier<BlockState> LEAVES = () -> (BlockState)Blocks.AZALEA_LEAVES.defaultBlockState().setValue((Property)BlockStateProperties.PERSISTENT, (Comparable)Boolean.valueOf(true));
    public static Supplier<BlockState> BLOSSOM = () -> (BlockState)Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState().setValue((Property)BlockStateProperties.PERSISTENT, (Comparable)Boolean.valueOf(true));
    public static Supplier<BlockState> PLANKS = () -> Blocks.DARK_OAK_PLANKS.defaultBlockState();
    public static Supplier<BlockState> GRASS = () -> Blocks.SHORT_GRASS.defaultBlockState();
    public static Supplier<BlockState> FERN = () -> Blocks.FERN.defaultBlockState();
    public static Supplier<BlockState> LUMEN = () -> Blocks.SHROOMLIGHT.defaultBlockState();
    public static Supplier<BlockState> SHRUB = () -> Blocks.AZALEA.defaultBlockState();
    public static Supplier<BlockState> BERRY = () -> (BlockState)Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue((Property)BlockStateProperties.AGE_3, (Comparable)Integer.valueOf(3));

    private Flora() {
    }
}
