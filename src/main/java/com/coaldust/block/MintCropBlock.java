/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.coaldust.block;

import com.coaldust.item.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MintCropBlock
extends CropBlock {
    public MintCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected ItemLike getBaseSeedId() {
        return (ItemLike)ModItems.MINT_SEEDS.get();
    }
}
