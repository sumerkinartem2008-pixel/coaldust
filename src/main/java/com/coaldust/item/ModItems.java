/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.food.FoodProperties$Builder
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemNameBlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.coaldust.item;

import com.coaldust.block.ModBlocks;
import com.coaldust.disease.Disease;
import com.coaldust.disease.Diseases;
import com.coaldust.ideology.BloodKind;
import com.coaldust.ideology.IdeologyItems;
import com.coaldust.item.DiseaseFlaskItem;
import com.coaldust.meteor.KindRemoteItem;
import com.coaldust.meteor.MeteorRemoteItem;
import com.coaldust.meteor.MeteorType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create((ResourceKey)Registries.ITEM, (String)"coaldust");
    public static final List<DeferredHolder<Item, DiseaseFlaskItem>> FLASKS = new ArrayList<DeferredHolder<Item, DiseaseFlaskItem>>();
    public static final DeferredHolder<Item, ItemNameBlockItem> MINT_SEEDS = ITEMS.register("mint_seeds", () -> new ItemNameBlockItem((Block)ModBlocks.MINT_CROP.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> MINT = ITEMS.register("mint", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).build())));
    public static final List<DeferredHolder<Item, MeteorRemoteItem>> METEOR_REMOTES = new ArrayList<DeferredHolder<Item, MeteorRemoteItem>>();
    public static final DeferredHolder<Item, Item> MANIFEST = ITEMS.register("ideology_manifest", () -> new IdeologyItems.Manifest(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BLOOD_LANCET = ITEMS.register("blood_lancet", () -> new IdeologyItems.BloodLancet(new Item.Properties().stacksTo(1)));
    public static final Map<BloodKind, DeferredHolder<Item, Item>> BLOODS = new EnumMap<BloodKind, DeferredHolder<Item, Item>>(BloodKind.class);
    public static final DeferredHolder<Item, Item> BLOOD_BOTTLE;
    public static final DeferredHolder<Item, Item> SCOURGE;
    public static final DeferredHolder<Item, Item> RITUAL_ALTAR;
    public static final DeferredHolder<Item, KindRemoteItem> KIND_REMOTE;
    public static final DeferredHolder<Item, Item> ANCIENT_LOG;
    public static final DeferredHolder<Item, Item> ANCIENT_LEAVES;
    public static final DeferredHolder<Item, Item> ANCIENT_BLOSSOM_LEAVES;
    public static final DeferredHolder<Item, Item> ANCIENT_PLANKS;
    public static final DeferredHolder<Item, Item> ANCIENT_STAIRS;
    public static final DeferredHolder<Item, Item> ANCIENT_SLAB;
    public static final DeferredHolder<Item, Item> ANCIENT_GRASS;
    public static final DeferredHolder<Item, Item> ANCIENT_FERN;
    public static final DeferredHolder<Item, Item> ANCIENT_LUMEN;
    public static final DeferredHolder<Item, Item> ANCIENT_SHRUB;
    public static final DeferredHolder<Item, Item> ANCIENT_BERRIES;
    public static final DeferredHolder<Item, Item> ANCIENT_STICK;
    public static final DeferredHolder<Item, Item> DEATH_FLOWER;
    public static final DeferredRegister<CreativeModeTab> TABS;
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB;

    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent buildCreativeModeTabContentsEvent) {
    }

    private ModItems() {
    }

    private static /* synthetic */ MeteorRemoteItem lambda$static$22(MeteorType meteorType) {
        return new MeteorRemoteItem(meteorType, new Item.Properties().stacksTo(1));
    }

    static {
        for (BloodKind enum_ : BloodKind.values()) {
            BLOODS.put(enum_, (DeferredHolder<Item, Item>)ITEMS.register(enum_.item, () -> new IdeologyItems.BloodBottle(enum_, new Item.Properties().stacksTo(16).rarity(bloodKind.tier >= 2 ? Rarity.EPIC : (bloodKind.tier == 1 ? Rarity.UNCOMMON : Rarity.COMMON)))));
        }
        BLOOD_BOTTLE = BLOODS.get((Object)BloodKind.COMMON);
        SCOURGE = ITEMS.register("scourge", () -> new IdeologyItems.Scourge(new Item.Properties().stacksTo(1)));
        RITUAL_ALTAR = ITEMS.register("ritual_altar", () -> new BlockItem((Block)ModBlocks.RITUAL_ALTAR.get(), new Item.Properties()));
        KIND_REMOTE = ITEMS.register("meteor_remote_kinds", () -> new KindRemoteItem(new Item.Properties().stacksTo(1)));
        ANCIENT_LOG = ITEMS.register("ancient_log", () -> new BlockItem((Block)ModBlocks.ANCIENT_LOG.get(), new Item.Properties()));
        ANCIENT_LEAVES = ITEMS.register("ancient_leaves", () -> new BlockItem((Block)ModBlocks.ANCIENT_LEAVES.get(), new Item.Properties()));
        ANCIENT_BLOSSOM_LEAVES = ITEMS.register("ancient_blossom_leaves", () -> new BlockItem((Block)ModBlocks.ANCIENT_BLOSSOM_LEAVES.get(), new Item.Properties()));
        ANCIENT_PLANKS = ITEMS.register("ancient_planks", () -> new BlockItem((Block)ModBlocks.ANCIENT_PLANKS.get(), new Item.Properties()));
        ANCIENT_STAIRS = ITEMS.register("ancient_stairs", () -> new BlockItem((Block)ModBlocks.ANCIENT_STAIRS.get(), new Item.Properties()));
        ANCIENT_SLAB = ITEMS.register("ancient_slab", () -> new BlockItem((Block)ModBlocks.ANCIENT_SLAB.get(), new Item.Properties()));
        ANCIENT_GRASS = ITEMS.register("ancient_grass", () -> new BlockItem((Block)ModBlocks.ANCIENT_GRASS.get(), new Item.Properties()));
        ANCIENT_FERN = ITEMS.register("ancient_fern", () -> new BlockItem((Block)ModBlocks.ANCIENT_FERN.get(), new Item.Properties()));
        ANCIENT_LUMEN = ITEMS.register("ancient_lumen", () -> new BlockItem((Block)ModBlocks.ANCIENT_LUMEN.get(), new Item.Properties()));
        ANCIENT_SHRUB = ITEMS.register("ancient_shrub", () -> new BlockItem((Block)ModBlocks.ANCIENT_SHRUB.get(), new Item.Properties()));
        ANCIENT_BERRIES = ITEMS.register("ancient_berries", () -> new ItemNameBlockItem((Block)ModBlocks.ANCIENT_BERRY_BUSH.get(), new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).build())));
        ANCIENT_STICK = ITEMS.register("ancient_stick", () -> new Item(new Item.Properties()));
        DEATH_FLOWER = ITEMS.register("death_flower", () -> new BlockItem((Block)ModBlocks.DEATH_FLOWER.get(), new Item.Properties()));
        METEOR_REMOTES.add((DeferredHolder<Item, MeteorRemoteItem>)ITEMS.register("meteor_remote_resource", () -> new MeteorRemoteItem(null, new Item.Properties().stacksTo(1))));
        for (Enum enum_ : new MeteorType[]{MeteorType.PLAIN, MeteorType.ICE, MeteorType.LIVING, MeteorType.ANCIENT, MeteorType.UNDERGROUND}) {
            METEOR_REMOTES.add((DeferredHolder<Item, MeteorRemoteItem>)ITEMS.register("meteor_remote_" + ((MeteorType)enum_).id, () -> ModItems.lambda$static$22((MeteorType)enum_)));
        }
        for (Disease disease : Diseases.ALL) {
            FLASKS.add((DeferredHolder<Item, DiseaseFlaskItem>)ITEMS.register(disease.getName() + "_flask", () -> new DiseaseFlaskItem(disease, false, new Item.Properties().stacksTo(16))));
            FLASKS.add((DeferredHolder<Item, DiseaseFlaskItem>)ITEMS.register(disease.getName() + "_catalyst", () -> new DiseaseFlaskItem(disease, true, new Item.Properties().stacksTo(16))));
        }
        TABS = DeferredRegister.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (String)"coaldust");
        TAB = TABS.register("coaldust", () -> CreativeModeTab.builder().title((Component)Component.translatable((String)"itemGroup.coaldust")).icon(() -> new ItemStack((ItemLike)KIND_REMOTE.get())).displayItems((itemDisplayParameters, output) -> {
            for (DeferredHolder deferredHolder : ITEMS.getEntries()) {
                output.accept((ItemLike)deferredHolder.get());
            }
        }).build());
    }
}
