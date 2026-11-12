/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.coaldust.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class FallingMeteorBlock
extends Block {
    public static final int FIRE = 0;
    public static final int SMOKE = 1;
    public static final int ICE = 2;
    public static final int LIVING = 3;
    public static final int ANCIENT = 4;
    public static final int UNDERGROUND = 5;
    public static final int RING_ANCIENT = 6;
    public static final int RING_SOUL = 7;
    public static final IntegerProperty KIND = IntegerProperty.create((String)"kind", (int)0, (int)7);

    public FallingMeteorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)KIND, (Comparable)Integer.valueOf(0)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{KIND});
    }
}
