/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.entity.ChestBlockEntity
 *  net.neoforged.neoforge.registries.DeferredHolder
 */
package com.coaldust.meteor.ancient;

import com.coaldust.item.DiseaseFlaskItem;
import com.coaldust.item.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class AncientLoot {
    public static final String[] ARTIFACT_MODS = new String[]{"artifacts", "relics"};
    public static final float ARTIFACT_CHANCE_NORMAL = 0.15f;
    public static final float MOD_RESOURCE_SHARE = 0.25f;
    private static final TagKey<Item> INGOTS = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"c", (String)"ingots"));
    private static final TagKey<Item> GEMS = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"c", (String)"gems"));

    public static void fill(ChestBlockEntity chestBlockEntity, RandomSource randomSource, Pools pools, boolean bl) {
        int n = chestBlockEntity.getContainerSize();
        int n2 = bl ? 8 + randomSource.nextInt(5) : 4 + randomSource.nextInt(4);
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        for (int i = 0; i < n2; ++i) {
            arrayList.add(AncientLoot.roll(randomSource, pools, bl));
        }
        if (!pools.artifacts.isEmpty() && (bl || randomSource.nextFloat() < 0.15f)) {
            arrayList.add(new ItemStack((ItemLike)pools.artifacts.get(randomSource.nextInt(pools.artifacts.size()))));
        } else if (bl) {
            arrayList.add(new ItemStack((ItemLike)(randomSource.nextFloat() < 0.5f ? Items.TOTEM_OF_UNDYING : Items.ENCHANTED_GOLDEN_APPLE)));
        }
        float f = randomSource.nextFloat();
        float f2 = bl ? 0.25f : 0.06f;
        if (f < f2 && !ModItems.FLASKS.isEmpty()) {
            DeferredHolder<Item, DiseaseFlaskItem> deferredHolder = ModItems.FLASKS.get(randomSource.nextInt(ModItems.FLASKS.size()));
            arrayList.add(new ItemStack((ItemLike)deferredHolder.get()));
        }
        block1: for (ItemStack itemStack : arrayList) {
            for (int i = 0; i < 20; ++i) {
                int n3 = randomSource.nextInt(n);
                if (!chestBlockEntity.getItem(n3).isEmpty()) continue;
                chestBlockEntity.setItem(n3, itemStack);
                continue block1;
            }
        }
    }

    private static ItemStack roll(RandomSource randomSource, Pools pools, boolean bl) {
        if (!pools.modResources.isEmpty() && randomSource.nextFloat() < 0.25f) {
            return new ItemStack((ItemLike)pools.modResources.get(randomSource.nextInt(pools.modResources.size())), 2 + randomSource.nextInt(bl ? 10 : 6));
        }
        int n = randomSource.nextInt(bl ? 100 : 88);
        if (n < 14) {
            return new ItemStack((ItemLike)Items.IRON_INGOT, 3 + randomSource.nextInt(8));
        }
        if (n < 24) {
            return new ItemStack((ItemLike)Items.GOLD_INGOT, 2 + randomSource.nextInt(6));
        }
        if (n < 32) {
            return new ItemStack((ItemLike)Items.COPPER_INGOT, 4 + randomSource.nextInt(10));
        }
        if (n < 38) {
            return new ItemStack((ItemLike)Items.GOLD_NUGGET, 6 + randomSource.nextInt(14));
        }
        if (n < 45) {
            return new ItemStack((ItemLike)Items.LAPIS_LAZULI, 4 + randomSource.nextInt(10));
        }
        if (n < 52) {
            return new ItemStack((ItemLike)Items.REDSTONE, 4 + randomSource.nextInt(12));
        }
        if (n < 58) {
            return new ItemStack((ItemLike)Items.AMETHYST_SHARD, 3 + randomSource.nextInt(8));
        }
        if (n < 64) {
            return new ItemStack((ItemLike)Items.EMERALD, 1 + randomSource.nextInt(5));
        }
        if (n < 69) {
            return new ItemStack((ItemLike)Items.DIAMOND, 1 + randomSource.nextInt(3));
        }
        if (n < 74) {
            return new ItemStack((ItemLike)Items.EXPERIENCE_BOTTLE, 3 + randomSource.nextInt(8));
        }
        if (n < 78) {
            return new ItemStack((ItemLike)Items.ENDER_PEARL, 1 + randomSource.nextInt(3));
        }
        if (n < 82) {
            return new ItemStack((ItemLike)Items.GOLDEN_APPLE, 1 + randomSource.nextInt(2));
        }
        if (n < 85) {
            return new ItemStack((ItemLike)Items.NAME_TAG);
        }
        if (n < 88) {
            return new ItemStack((ItemLike)Items.NETHERITE_SCRAP, 1 + randomSource.nextInt(2));
        }
        if (n < 94) {
            return new ItemStack((ItemLike)Items.DIAMOND, 2 + randomSource.nextInt(4));
        }
        if (n < 97) {
            return new ItemStack((ItemLike)Items.ANCIENT_DEBRIS, 1 + randomSource.nextInt(2));
        }
        return new ItemStack((ItemLike)Items.ENCHANTED_GOLDEN_APPLE);
    }

    private AncientLoot() {
    }

    public static final class Pools {
        final List<Item> modResources = new ArrayList<Item>();
        final List<Item> artifacts = new ArrayList<Item>();

        public static Pools collect() {
            Pools pools = new Pools();
            for (TagKey<Item> item : List.of(INGOTS, GEMS)) {
                for (String[] stringArray : BuiltInRegistries.ITEM.getTagOrEmpty(item)) {
                    Item item2 = (Item)stringArray.value();
                    ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey((Object)item2);
                    if (resourceLocation == null || "minecraft".equals(resourceLocation.getNamespace()) || pools.modResources.contains(item2)) continue;
                    pools.modResources.add(item2);
                }
            }
            for (Item item : BuiltInRegistries.ITEM) {
                ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey((Object)item);
                if (resourceLocation == null || resourceLocation.getPath().contains("spawn_egg")) continue;
                for (String string : ARTIFACT_MODS) {
                    if (!string.equals(resourceLocation.getNamespace())) continue;
                    pools.artifacts.add(item);
                }
            }
            return pools;
        }
    }
}
