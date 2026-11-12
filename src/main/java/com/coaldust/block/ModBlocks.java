/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.TallGrassBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.level.material.PushReaction
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.coaldust.block;

import com.coaldust.block.AncientBerryBushBlock;
import com.coaldust.block.CoalDustBlock;
import com.coaldust.block.DeathFlowerBlock;
import com.coaldust.block.FallingMeteorBlock;
import com.coaldust.block.MintCropBlock;
import com.coaldust.block.StainBlock;
import com.coaldust.ideology.RitualAltarBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((ResourceKey)Registries.BLOCK, (String)"coaldust");
    public static final DeferredHolder<Block, CoalDustBlock> COAL_DUST = BLOCKS.register("coal_dust", () -> new CoalDustBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).replaceable().noCollission().noOcclusion().instabreak().noLootTable().pushReaction(PushReaction.DESTROY).sound(SoundType.SAND)));
    public static final DeferredHolder<Block, MintCropBlock> MINT_CROP = BLOCKS.register("mint_crop", () -> new MintCropBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.WHEAT)));
    public static final DeferredHolder<Block, RitualAltarBlock> RITUAL_ALTAR = BLOCKS.register("ritual_altar", () -> new RitualAltarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).lightLevel(blockState -> RitualAltarBlock.isMaster(blockState) ? 12 : 4).sound(SoundType.DEEPSLATE)));
    public static final DeferredHolder<Block, StainBlock> DIARRHEA_STAIN = BLOCKS.register("diarrhea_stain", () -> new StainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).replaceable().noCollission().noOcclusion().instabreak().noLootTable().pushReaction(PushReaction.DESTROY).sound(SoundType.SLIME_BLOCK), 0.05f));
    public static final DeferredHolder<Block, StainBlock> BLOOD_STAIN = BLOCKS.register("blood_stain", () -> new StainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).replaceable().noCollission().noOcclusion().instabreak().noLootTable().pushReaction(PushReaction.DESTROY).sound(SoundType.SLIME_BLOCK), 0.15f));
    public static final DeferredHolder<Block, DeathFlowerBlock> DEATH_FLOWER = BLOCKS.register("death_flower", () -> new DeathFlowerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).noCollission().noOcclusion().instabreak().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredHolder<Block, FallingMeteorBlock> FALLING_METEOR = BLOCKS.register("falling_meteor", () -> new FallingMeteorBlock(BlockBehaviour.Properties.of().noCollission().noOcclusion().noLootTable().instabreak().lightLevel(blockState -> 15)));
    public static final DeferredHolder<Block, RotatedPillarBlock> ANCIENT_LOG = BLOCKS.register("ancient_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.OAK_LOG).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredHolder<Block, LeavesBlock> ANCIENT_LEAVES = BLOCKS.register("ancient_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.CHERRY_LEAVES).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredHolder<Block, LeavesBlock> ANCIENT_BLOSSOM_LEAVES = BLOCKS.register("ancient_blossom_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.CHERRY_LEAVES).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 9)));
    public static final DeferredHolder<Block, Block> ANCIENT_PLANKS = BLOCKS.register("ancient_planks", () -> new Block(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredHolder<Block, StairBlock> ANCIENT_STAIRS = BLOCKS.register("ancient_stairs", () -> new StairBlock(((Block)ANCIENT_PLANKS.get()).defaultBlockState(), BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.OAK_STAIRS).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredHolder<Block, SlabBlock> ANCIENT_SLAB = BLOCKS.register("ancient_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.OAK_SLAB).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredHolder<Block, Block> ANCIENT_GRASS = BLOCKS.register("ancient_grass", () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.SHORT_GRASS).mapColor(MapColor.COLOR_CYAN).lightLevel(blockState -> 3)));
    public static final DeferredHolder<Block, Block> ANCIENT_FERN = BLOCKS.register("ancient_fern", () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.FERN).mapColor(MapColor.COLOR_CYAN).lightLevel(blockState -> 4)));
    public static final DeferredHolder<Block, Block> ANCIENT_SHRUB = BLOCKS.register("ancient_shrub", () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.FERN).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 7)));
    public static final DeferredHolder<Block, AncientBerryBushBlock> ANCIENT_BERRY_BUSH = BLOCKS.register("ancient_berry_bush", () -> new AncientBerryBushBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.SWEET_BERRY_BUSH).mapColor(MapColor.COLOR_CYAN).lightLevel(blockState -> 2 + (Integer)blockState.getValue((Property)AncientBerryBushBlock.AGE) * 3)));
    public static final DeferredHolder<Block, Block> ANCIENT_LUMEN = BLOCKS.register("ancient_lumen", () -> new Block(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.SHROOMLIGHT).mapColor(MapColor.COLOR_CYAN).lightLevel(blockState -> 15)));

    private ModBlocks() {
    }
}
