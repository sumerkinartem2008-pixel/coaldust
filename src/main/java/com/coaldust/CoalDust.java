/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.loading.FMLEnvironment
 *  net.neoforged.neoforge.common.NeoForge
 */
package com.coaldust;

import com.coaldust.block.AncientBerryBushBlock;
import com.coaldust.block.ModBlocks;
import com.coaldust.client.DiseaseMarksLayer;
import com.coaldust.client.DustFogClient;
import com.coaldust.client.FormRenderClient;
import com.coaldust.client.IdeologyClient;
import com.coaldust.disease.DiseaseEvents;
import com.coaldust.disease.Transformations;
import com.coaldust.dust.DustEvents;
import com.coaldust.effect.ModEffects;
import com.coaldust.ideology.Abyss;
import com.coaldust.ideology.IdeologyCommands;
import com.coaldust.ideology.RitualAltarBlock;
import com.coaldust.ideology.RitualFx;
import com.coaldust.ideology.Summons;
import com.coaldust.ideology.TraitEffects;
import com.coaldust.item.ModItems;
import com.coaldust.meteor.KindRemoteItem;
import com.coaldust.meteor.MeteorManager;
import com.coaldust.meteor.MeteorRemoteItem;
import com.coaldust.meteor.MeteorVisual;
import com.coaldust.meteor.underground.Flora;
import com.coaldust.network.FormSync;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value="coaldust")
public class CoalDust {
    public static final String MOD_ID = "coaldust";

    public CoalDust(IEventBus iEventBus) {
        ModBlocks.BLOCKS.register(iEventBus);
        ModEffects.EFFECTS.register(iEventBus);
        ModItems.ITEMS.register(iEventBus);
        ModItems.TABS.register(iEventBus);
        Flora.LOG = () -> ((RotatedPillarBlock)ModBlocks.ANCIENT_LOG.get()).defaultBlockState();
        Flora.LEAVES = () -> (BlockState)((LeavesBlock)ModBlocks.ANCIENT_LEAVES.get()).defaultBlockState().setValue((Property)BlockStateProperties.PERSISTENT, (Comparable)Boolean.valueOf(true));
        Flora.BLOSSOM = () -> (BlockState)((LeavesBlock)ModBlocks.ANCIENT_BLOSSOM_LEAVES.get()).defaultBlockState().setValue((Property)BlockStateProperties.PERSISTENT, (Comparable)Boolean.valueOf(true));
        Flora.PLANKS = () -> ((Block)ModBlocks.ANCIENT_PLANKS.get()).defaultBlockState();
        Flora.GRASS = () -> ((Block)ModBlocks.ANCIENT_GRASS.get()).defaultBlockState();
        Flora.FERN = () -> ((Block)ModBlocks.ANCIENT_FERN.get()).defaultBlockState();
        Flora.LUMEN = () -> ((Block)ModBlocks.ANCIENT_LUMEN.get()).defaultBlockState();
        Flora.SHRUB = () -> ((Block)ModBlocks.ANCIENT_SHRUB.get()).defaultBlockState();
        Flora.BERRY = () -> (BlockState)((AncientBerryBushBlock)((Object)((Object)ModBlocks.ANCIENT_BERRY_BUSH.get()))).defaultBlockState().setValue((Property)AncientBerryBushBlock.AGE, (Comparable)Integer.valueOf(3));
        iEventBus.addListener(buildCreativeModeTabContentsEvent -> ModItems.onBuildCreativeTab(buildCreativeModeTabContentsEvent));
        iEventBus.addListener(registerPayloadHandlersEvent -> FormSync.register(registerPayloadHandlersEvent));
        NeoForge.EVENT_BUS.addListener(breakEvent -> DustEvents.onBlockBreak(breakEvent));
        NeoForge.EVENT_BUS.addListener(post -> DustEvents.onLevelTick(post));
        NeoForge.EVENT_BUS.addListener(livingHealEvent -> DiseaseEvents.onHeal(livingHealEvent));
        NeoForge.EVENT_BUS.addListener(post -> MeteorManager.onLevelTick(post));
        NeoForge.EVENT_BUS.addListener(post -> RitualFx.onLevelTick(post));
        NeoForge.EVENT_BUS.addListener(post -> RitualAltarBlock.onEntityTick(post));
        NeoForge.EVENT_BUS.addListener(post -> MeteorVisual.onEntityTick(post));
        NeoForge.EVENT_BUS.addListener(post -> DiseaseEvents.onPlayerTick(post));
        NeoForge.EVENT_BUS.addListener(finish -> DiseaseEvents.onItemUseFinish(finish));
        NeoForge.EVENT_BUS.addListener(attackEntityEvent -> Transformations.onAttack(attackEntityEvent));
        NeoForge.EVENT_BUS.addListener(start -> Transformations.onUseStart(start));
        NeoForge.EVENT_BUS.addListener(livingIncomingDamageEvent -> Transformations.onIncomingDamage(livingIncomingDamageEvent));
        NeoForge.EVENT_BUS.addListener(clone -> Transformations.onClone(clone));
        NeoForge.EVENT_BUS.addListener(registerCommandsEvent -> MeteorRemoteItem.registerCommand(registerCommandsEvent));
        NeoForge.EVENT_BUS.addListener(registerCommandsEvent -> KindRemoteItem.registerCommand(registerCommandsEvent));
        NeoForge.EVENT_BUS.addListener(registerCommandsEvent -> DiseaseEvents.registerCommand(registerCommandsEvent));
        NeoForge.EVENT_BUS.addListener(registerCommandsEvent -> IdeologyCommands.register(registerCommandsEvent));
        NeoForge.EVENT_BUS.addListener(post -> TraitEffects.onDamage(post));
        NeoForge.EVENT_BUS.addListener(livingDeathEvent -> TraitEffects.onDeath(livingDeathEvent));
        NeoForge.EVENT_BUS.addListener(livingDeathEvent -> Abyss.onKill(livingDeathEvent));
        NeoForge.EVENT_BUS.addListener(livingDeathEvent -> Summons.onDeath(livingDeathEvent));
        NeoForge.EVENT_BUS.addListener(livingIncomingDamageEvent -> Summons.onIncomingDamage(livingIncomingDamageEvent));
        NeoForge.EVENT_BUS.addListener(livingDropsEvent -> Summons.onDrops(livingDropsEvent));
        NeoForge.EVENT_BUS.addListener(livingExperienceDropEvent -> Summons.onXp(livingExperienceDropEvent));
        NeoForge.EVENT_BUS.addListener(pre -> Summons.onConvert(pre));
        NeoForge.EVENT_BUS.addListener(entityJoinLevelEvent -> Summons.onJoin(entityJoinLevelEvent));
        NeoForge.EVENT_BUS.addListener(playerLoggedOutEvent -> Summons.onLogout(playerLoggedOutEvent));
        NeoForge.EVENT_BUS.addListener(livingIncomingDamageEvent -> Abyss.onIncomingDamage(livingIncomingDamageEvent));
        NeoForge.EVENT_BUS.addListener(breakSpeed -> Abyss.onBreakSpeed(breakSpeed));
        NeoForge.EVENT_BUS.addListener(livingDropsEvent -> Abyss.onDrops(livingDropsEvent));
        NeoForge.EVENT_BUS.addListener(blockDropsEvent -> Abyss.onBlockDrops(blockDropsEvent));
        NeoForge.EVENT_BUS.addListener(pickupXp -> Abyss.onPickupXp(pickupXp));
        NeoForge.EVENT_BUS.addListener(breakEvent -> TraitEffects.onBreak(breakEvent));
        NeoForge.EVENT_BUS.addListener(entityPlaceEvent -> TraitEffects.onPlace(entityPlaceEvent));
        NeoForge.EVENT_BUS.addListener(itemCraftedEvent -> TraitEffects.onCrafted(itemCraftedEvent));
        NeoForge.EVENT_BUS.addListener(itemSmeltedEvent -> TraitEffects.onSmelted(itemSmeltedEvent));
        NeoForge.EVENT_BUS.addListener(itemFishedEvent -> TraitEffects.onFished(itemFishedEvent));
        NeoForge.EVENT_BUS.addListener(playerWakeUpEvent -> TraitEffects.onWake(playerWakeUpEvent));
        NeoForge.EVENT_BUS.addListener(babyEntitySpawnEvent -> TraitEffects.onBreed(babyEntitySpawnEvent));
        NeoForge.EVENT_BUS.addListener(animalTameEvent -> TraitEffects.onTame(animalTameEvent));
        NeoForge.EVENT_BUS.addListener(tradeWithVillagerEvent -> TraitEffects.onTrade(tradeWithVillagerEvent));
        NeoForge.EVENT_BUS.addListener(bonemealEvent -> TraitEffects.onBonemeal(bonemealEvent));
        NeoForge.EVENT_BUS.addListener(anvilRepairEvent -> TraitEffects.onRepair(anvilRepairEvent));
        NeoForge.EVENT_BUS.addListener(entityInteract -> TraitEffects.onEntityInteract(entityInteract));
        NeoForge.EVENT_BUS.addListener(rightClickBlock -> TraitEffects.onRightClickBlock(rightClickBlock));
        NeoForge.EVENT_BUS.addListener(playerBrewedPotionEvent -> TraitEffects.onBrew(playerBrewedPotionEvent));
        NeoForge.EVENT_BUS.addListener(pickupXp -> TraitEffects.onPickupXp(pickupXp));
        NeoForge.EVENT_BUS.addListener(levelChange -> TraitEffects.onLevelChange(levelChange));
        if (FMLEnvironment.dist == Dist.CLIENT) {
            DustFogClient.init();
            FormRenderClient.init();
            IdeologyClient.init(iEventBus);
            DiseaseMarksLayer.init(iEventBus);
        }
    }

    public static ResourceLocation id(String string) {
        return ResourceLocation.fromNamespaceAndPath((String)MOD_ID, (String)string);
    }
}
