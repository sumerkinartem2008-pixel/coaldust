/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.animal.Cow
 *  net.minecraft.world.entity.animal.Sheep
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
 *  net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.SmokerBlockEntity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.coaldust.ideology;

import com.coaldust.ideology.Abyss;
import com.coaldust.ideology.Summons;
import com.coaldust.ideology.Workers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Crafts {
    static final Item[] FOODS = new Item[]{Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.CARROT, Items.POTATO, Items.BEETROOT, Items.GOLDEN_CARROT, Items.DANDELION, Items.SWEET_BERRIES, Items.HAY_BLOCK};
    private static final Map<Item, RecipeHolder<CraftingRecipe>> RECIPE_CACHE = new HashMap<Item, RecipeHolder<CraftingRecipe>>();

    static boolean isFood(Item item) {
        for (Item item2 : FOODS) {
            if (item2 != item) continue;
            return true;
        }
        return false;
    }

    static boolean isFuel(ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.isFuel((ItemStack)itemStack);
    }

    private static boolean approach(ServerLevel serverLevel, Mob mob, Workers.Job job, Vec3 vec3, double d) {
        if (mob.distanceToSqr(vec3) <= d * d) {
            mob.getNavigation().stop();
            mob.getLookControl().setLookAt(vec3.x, vec3.y, vec3.z);
            return true;
        }
        if (!mob.getNavigation().isInProgress() || mob.tickCount % 20 == 0) {
            mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.05);
        }
        return false;
    }

    static boolean fetchAny(ServerPlayer serverPlayer, Abyss.Entry entry, ServerLevel serverLevel, Mob mob, Workers.Job job, List<ItemStack> list, int n, int n2, Predicate<ItemStack> predicate, int n3) {
        Workers.ChestCfg chestCfg = null;
        double d = Double.MAX_VALUE;
        for (Workers.ChestCfg chestCfg2 : Workers.chestsFor(entry, serverLevel, n2)) {
            double d2;
            Container container = Workers.container(serverLevel, chestCfg2.pos);
            if (container == null) continue;
            boolean bl = false;
            for (int i = 0; i < container.getContainerSize() && !bl; ++i) {
                bl = !container.getItem(i).isEmpty() && predicate.test(container.getItem(i));
            }
            if (!bl || !((d2 = mob.distanceToSqr((double)chestCfg2.pos.getX() + 0.5, (double)chestCfg2.pos.getY() + 0.5, (double)chestCfg2.pos.getZ() + 0.5)) < d)) continue;
            d = d2;
            chestCfg = chestCfg2;
        }
        if (chestCfg == null) {
            return false;
        }
        if (!Crafts.approach(serverLevel, mob, job, Vec3.atCenterOf((Vec3i)chestCfg.pos), 2.8)) {
            return true;
        }
        Container container = Workers.container(serverLevel, chestCfg.pos);
        int n4 = n3;
        for (int i = 0; i < container.getContainerSize() && n4 > 0; ++i) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.isEmpty() || !predicate.test(itemStack)) continue;
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(Math.min(n4, itemStack.getCount()));
            int n5 = itemStack2.getCount();
            ItemStack itemStack3 = Workers.addToBag(list, itemStack2, n);
            int n6 = n5 - itemStack3.getCount();
            itemStack.shrink(n6);
            n4 -= n6;
            if (itemStack.isEmpty()) {
                container.setItem(i, ItemStack.EMPTY);
            }
            if (!itemStack3.isEmpty()) break;
        }
        container.setChanged();
        serverLevel.playSound(null, chestCfg.pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.4f, 1.2f);
        return true;
    }

    private static int count(List<ItemStack> list, Predicate<ItemStack> predicate) {
        int n = 0;
        for (ItemStack itemStack : list) {
            if (!predicate.test(itemStack)) continue;
            n += itemStack.getCount();
        }
        return n;
    }

    private static ItemStack takeOne(List<ItemStack> list, Predicate<ItemStack> predicate) {
        for (ItemStack itemStack : list) {
            if (itemStack.isEmpty() || !predicate.test(itemStack)) continue;
            ItemStack itemStack2 = itemStack.split(1);
            list.removeIf(ItemStack::isEmpty);
            return itemStack2;
        }
        return ItemStack.EMPTY;
    }

    private static AABB box(Workers.Job job) {
        return new AABB((double)job.a.getX(), (double)job.a.getY(), (double)job.a.getZ(), (double)(job.b.getX() + 1), (double)(job.b.getY() + 3), (double)(job.b.getZ() + 1));
    }

    private static void warnOnce(ServerPlayer serverPlayer, Workers.Job job, String string, Object ... objectArray) {
        if (Workers.NOW_TICK() - job.lastNeedWarn > 600L) {
            job.lastNeedWarn = Workers.NOW_TICK();
            Workers.warn(serverPlayer, string, objectArray);
        }
    }

    static void pasture(ServerPlayer serverPlayer, Abyss.Entry entry, ServerLevel serverLevel, Mob mob, Workers.Job job, List<ItemStack> list, int n, int n2) {
        Object object;
        Object object2;
        Object object3;
        Object object42;
        Object object5;
        Object object62;
        if (job.a == null) {
            Workers.finish(serverPlayer, mob, job);
            return;
        }
        AABB aABB = Crafts.box(job);
        Object object7 = null;
        double d = Double.MAX_VALUE;
        for (Object object62 : serverLevel.getEntitiesOfClass(ItemEntity.class, aABB)) {
            double d2 = mob.distanceToSqr((Entity)object62);
            if (!(d2 < d)) continue;
            d = d2;
            object7 = object62;
        }
        if (object7 != null && list.size() < n) {
            if (Crafts.approach(serverLevel, mob, job, object7.position(), 1.6)) {
                object5 = Workers.addToBag(list, object7.getItem().copy(), n);
                if (object5.isEmpty()) {
                    object7.discard();
                } else {
                    object7.setItem((ItemStack)object5);
                }
                serverLevel.playSound(null, mob.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.3f, 1.2f);
            }
            return;
        }
        object5 = serverLevel.getEntitiesOfClass(Animal.class, aABB, animal -> animal.isAlive() && !Summons.isOwned((Entity)animal));
        object62 = new HashMap();
        Iterator<Object> iterator = object5.iterator();
        while (iterator.hasNext()) {
            object42 = (Animal)iterator.next();
            object62.computeIfAbsent(object42.getType(), entityType -> new ArrayList()).add(object42);
        }
        if (job.cull) {
            for (Object object42 : object62.values()) {
                object3 = new ArrayList();
                Animal animal2 = object42.iterator();
                while (animal2.hasNext()) {
                    object2 = (Animal)animal2.next();
                    if (object2.isBaby()) continue;
                    object3.add(object2);
                }
                if (object3.size() <= job.limit) continue;
                animal2 = null;
                object2 = object3.iterator();
                while (object2.hasNext()) {
                    object = (Animal)object2.next();
                    if (object.isInLove() || animal2 != null && !(mob.distanceToSqr((Entity)object) < mob.distanceToSqr((Entity)animal2))) continue;
                    animal2 = object;
                }
                if (animal2 == null) continue;
                if (Crafts.approach(serverLevel, mob, job, animal2.position(), 2.2)) {
                    mob.swing(InteractionHand.MAIN_HAND);
                    animal2.hurt(mob.damageSources().mobAttack((LivingEntity)mob), 1000.0f);
                }
                return;
            }
        }
        if (job.shear) {
            iterator = object5.iterator();
            while (iterator.hasNext()) {
                object42 = (Animal)iterator.next();
                if (!(object42 instanceof Sheep) || !(object3 = (Sheep)object42).readyForShearing()) continue;
                if (Crafts.approach(serverLevel, mob, job, object3.position(), 2.2)) {
                    mob.swing(InteractionHand.MAIN_HAND);
                    object3.shear(SoundSource.NEUTRAL);
                }
                return;
            }
        }
        if (job.milk && Workers.countIn(list, Items.BUCKET) > 0) {
            iterator = object5.iterator();
            while (iterator.hasNext()) {
                object42 = (Animal)iterator.next();
                if (!(object42 instanceof Cow) || (object3 = (Cow)object42).isBaby()) continue;
                if (Crafts.approach(serverLevel, mob, job, object3.position(), 2.2)) {
                    Workers.take(list, Items.BUCKET, 1);
                    Workers.addToBag(list, new ItemStack((ItemLike)Items.MILK_BUCKET), n + 1);
                    serverLevel.playSound(null, object3.blockPosition(), SoundEvents.COW_MILK, SoundSource.NEUTRAL, 1.0f, 1.0f);
                    mob.swing(InteractionHand.MAIN_HAND);
                }
                return;
            }
        }
        if (job.breed) {
            for (Object object42 : object62.values()) {
                ItemStack itemStack2;
                if (object42.size() >= job.limit) continue;
                object3 = new ArrayList();
                Iterator iterator2 = object42.iterator();
                while (iterator2.hasNext()) {
                    object2 = (Animal)iterator2.next();
                    if (object2.isBaby() || object2.getAge() != 0 || object2.isInLove() || !object2.canFallInLove()) continue;
                    object3.add(object2);
                }
                boolean bl = object42.stream().anyMatch(Animal::isInLove);
                if (object3.size() < 2 && (object3.size() != 1 || !bl)) continue;
                object2 = (Animal)object3.get(0);
                object = arg_0 -> Crafts.lambda$pasture$2((Animal)object2, arg_0);
                if (Crafts.count(list, (Predicate<ItemStack>)object) == 0) {
                    if (!Crafts.fetchAny(serverPlayer, entry, serverLevel, mob, job, list, n, n2, (Predicate<ItemStack>)object, 16)) {
                        Crafts.warnOnce(serverPlayer, job, "ideology.coaldust.work.no_food", object2.getType().getDescription());
                    }
                    return;
                }
                if (Crafts.approach(serverLevel, mob, job, object2.position(), 2.2) && !(itemStack2 = Crafts.takeOne(list, (Predicate<ItemStack>)object)).isEmpty()) {
                    object2.setInLove(null);
                    mob.swing(InteractionHand.MAIN_HAND);
                }
                return;
            }
        }
        if (job.milk && Workers.countIn(list, Items.BUCKET) == 0 && object5.stream().anyMatch(animal -> animal instanceof Cow && !animal.isBaby()) && Crafts.fetchAny(serverPlayer, entry, serverLevel, mob, job, list, n, n2, itemStack -> itemStack.is(Items.BUCKET), 4)) {
            return;
        }
        Workers.idleNear(mob, job);
    }

    private static RecipeType<?> typeFor(BlockEntity blockEntity) {
        if (blockEntity instanceof BlastFurnaceBlockEntity) {
            return RecipeType.BLASTING;
        }
        if (blockEntity instanceof SmokerBlockEntity) {
            return RecipeType.SMOKING;
        }
        return RecipeType.SMELTING;
    }

    static boolean canSmelt(ServerLevel serverLevel, BlockEntity blockEntity, ItemStack itemStack) {
        return serverLevel.getRecipeManager().getRecipeFor(Crafts.typeFor(blockEntity), (RecipeInput)new SingleRecipeInput(itemStack), (Level)serverLevel).isPresent();
    }

    static void smelt(ServerPlayer serverPlayer, Abyss.Entry entry, ServerLevel serverLevel, Mob mob, Workers.Job job, List<ItemStack> list, int n, int n2) {
        int n3;
        int n4;
        Object object;
        AbstractFurnaceBlockEntity abstractFurnaceBlockEntity;
        BlockEntity blockEntity;
        BlockPos blockPos42;
        BlockPos blockPos222;
        if (job.a == null) {
            Workers.finish(serverPlayer, mob, job);
            return;
        }
        if (job.furnaceScan-- <= 0) {
            job.furnaceScan = 40;
            job.furnaces.clear();
            for (BlockPos blockPos222 : BlockPos.betweenClosed((BlockPos)job.a, (BlockPos)job.b)) {
                if (!(serverLevel.getBlockEntity(blockPos222) instanceof AbstractFurnaceBlockEntity)) continue;
                job.furnaces.add(blockPos222.immutable());
            }
        }
        if (job.furnaces.isEmpty()) {
            Crafts.warnOnce(serverPlayer, job, "ideology.coaldust.work.no_furnace", new Object[0]);
            Workers.idleNear(mob, job);
            return;
        }
        Predicate<ItemStack> predicate = itemStack -> job.fills.contains(itemStack.getItem());
        blockPos222 = itemStack -> Crafts.isFuel(itemStack) && !job.fills.contains(itemStack.getItem());
        BlockPos blockPos3 = null;
        double d = Double.MAX_VALUE;
        boolean bl = false;
        boolean bl2 = false;
        for (BlockPos blockPos42 : job.furnaces) {
            double d2;
            boolean bl3;
            blockEntity = serverLevel.getBlockEntity(blockPos42);
            if (!(blockEntity instanceof AbstractFurnaceBlockEntity)) continue;
            abstractFurnaceBlockEntity = (AbstractFurnaceBlockEntity)blockEntity;
            blockEntity = (Container)abstractFurnaceBlockEntity;
            object = blockEntity.getItem(0);
            ItemStack object2 = blockEntity.getItem(1);
            ItemStack itemStack2 = blockEntity.getItem(2);
            n4 = object.isEmpty() || object.getCount() < 16 ? 1 : 0;
            n3 = 0;
            for (ItemStack itemStack3 : list) {
                if (!predicate.test(itemStack3) || !object.isEmpty() && !ItemStack.isSameItemSameComponents((ItemStack)object, (ItemStack)itemStack3) || !Crafts.canSmelt(serverLevel, (BlockEntity)abstractFurnaceBlockEntity, itemStack3)) continue;
                n3 = 1;
                break;
            }
            boolean bl4 = object2.getCount() < 4 && (!object.isEmpty() || n3 != 0);
            boolean bl5 = bl3 = Crafts.count(list, (Predicate<ItemStack>)blockPos222) > 0;
            if (n4 != 0) {
                bl = true;
            }
            if (bl4) {
                bl2 = true;
            }
            if (itemStack2.isEmpty() && (n4 == 0 || n3 == 0) && (!bl4 || !bl3) || !((d2 = mob.distanceToSqr(Vec3.atCenterOf((Vec3i)blockPos42))) < d)) continue;
            d = d2;
            blockPos3 = blockPos42;
        }
        if (blockPos3 == null) {
            if (bl && Crafts.count(list, predicate) < 16 && Crafts.fetchAny(serverPlayer, entry, serverLevel, mob, job, list, n, n2, predicate, 64)) {
                return;
            }
            if (bl2 && Crafts.count(list, (Predicate<ItemStack>)blockPos222) < 8 && Crafts.fetchAny(serverPlayer, entry, serverLevel, mob, job, list, n, n2, (Predicate<ItemStack>)blockPos222, 32)) {
                return;
            }
            if (bl && Crafts.count(list, predicate) == 0) {
                Crafts.warnOnce(serverPlayer, job, "ideology.coaldust.work.no_smelt", new Object[0]);
            }
            Workers.idleNear(mob, job);
            return;
        }
        if (!Crafts.approach(serverLevel, mob, job, Vec3.atCenterOf(blockPos3), 2.8)) {
            return;
        }
        AbstractFurnaceBlockEntity abstractFurnaceBlockEntity2 = (AbstractFurnaceBlockEntity)serverLevel.getBlockEntity(blockPos3);
        blockPos42 = (Container)abstractFurnaceBlockEntity2;
        abstractFurnaceBlockEntity = blockPos42.getItem(2);
        if (!abstractFurnaceBlockEntity.isEmpty()) {
            blockEntity = Workers.addToBag(list, abstractFurnaceBlockEntity.copy(), n);
            blockPos42.setItem(2, (ItemStack)blockEntity);
        }
        blockEntity = blockPos42.getItem(0);
        for (ItemStack itemStack4 : list) {
            if (!predicate.test(itemStack4) || itemStack4.isEmpty() || !blockEntity.isEmpty() && !ItemStack.isSameItemSameComponents((ItemStack)blockEntity, (ItemStack)itemStack4) || !Crafts.canSmelt(serverLevel, (BlockEntity)abstractFurnaceBlockEntity2, itemStack4)) continue;
            int n5 = blockEntity.isEmpty() ? itemStack4.getMaxStackSize() : blockEntity.getMaxStackSize() - blockEntity.getCount();
            n4 = Math.min(n5, Math.min(itemStack4.getCount(), 32));
            if (n4 <= 0) break;
            if (blockEntity.isEmpty()) {
                blockPos42.setItem(0, itemStack4.split(n4));
                blockEntity = blockPos42.getItem(0);
                continue;
            }
            blockEntity.grow(n4);
            itemStack4.shrink(n4);
        }
        object = blockPos42.getItem(1);
        for (ItemStack itemStack5 : list) {
            if (!blockPos222.test(itemStack5) || itemStack5.isEmpty() || !object.isEmpty() && !ItemStack.isSameItemSameComponents((ItemStack)object, (ItemStack)itemStack5)) continue;
            n4 = object.isEmpty() ? itemStack5.getMaxStackSize() : object.getMaxStackSize() - object.getCount();
            n3 = Math.min(n4, Math.min(itemStack5.getCount(), 16));
            if (n3 <= 0) break;
            if (object.isEmpty()) {
                blockPos42.setItem(1, itemStack5.split(n3));
                object = blockPos42.getItem(1);
                continue;
            }
            object.grow(n3);
            itemStack5.shrink(n3);
        }
        list.removeIf(ItemStack::isEmpty);
        blockPos42.setChanged();
        mob.swing(InteractionHand.MAIN_HAND);
        serverLevel.playSound(null, blockPos3, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.6f, 1.0f);
    }

    static RecipeHolder<CraftingRecipe> recipe(ServerLevel serverLevel, Item item) {
        if (RECIPE_CACHE.containsKey(item)) {
            return RECIPE_CACHE.get(item);
        }
        RecipeHolder recipeHolder = null;
        for (RecipeHolder recipeHolder2 : serverLevel.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            ItemStack itemStack = ((CraftingRecipe)recipeHolder2.value()).getResultItem((HolderLookup.Provider)serverLevel.registryAccess());
            if (itemStack.isEmpty() || !itemStack.is(item)) continue;
            boolean bl = true;
            for (Ingredient ingredient : ((CraftingRecipe)recipeHolder2.value()).getIngredients()) {
                if (ingredient.isEmpty() || ingredient.getItems().length != 0) continue;
                bl = false;
            }
            if (!bl || ((CraftingRecipe)recipeHolder2.value()).getIngredients().isEmpty()) continue;
            recipeHolder = recipeHolder2;
            break;
        }
        RECIPE_CACHE.put(item, recipeHolder);
        return recipeHolder;
    }

    static boolean isIngredient(Workers.Job job, ItemStack itemStack) {
        if (job.craftOut == null) {
            return false;
        }
        RecipeHolder<CraftingRecipe> recipeHolder = RECIPE_CACHE.get(job.craftOut);
        if (recipeHolder == null) {
            return false;
        }
        for (Ingredient ingredient : ((CraftingRecipe)recipeHolder.value()).getIngredients()) {
            if (ingredient.isEmpty() || !ingredient.test(itemStack)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    static void craft(ServerPlayer serverPlayer, Abyss.Entry entry, ServerLevel serverLevel, Mob mob, Workers.Job job, List<ItemStack> list, int n, int n2) {
        int n3;
        void var14_20;
        ItemStack[] itemStackArray;
        if (job.craftOut == null || job.craftLeft <= 0) {
            Workers.finish(serverPlayer, mob, job);
            return;
        }
        RecipeHolder<CraftingRecipe> recipeHolder = Crafts.recipe(serverLevel, job.craftOut);
        if (recipeHolder == null) {
            Workers.warn(serverPlayer, "ideology.coaldust.work.no_recipe", new ItemStack((ItemLike)job.craftOut).getHoverName());
            Workers.finish(serverPlayer, mob, job);
            return;
        }
        ArrayList<Ingredient> arrayList = new ArrayList<Ingredient>();
        for (Ingredient ingredient : ((CraftingRecipe)recipeHolder.value()).getIngredients()) {
            if (ingredient.isEmpty()) continue;
            arrayList.add(ingredient);
        }
        int n4 = Math.max(1, ((CraftingRecipe)recipeHolder.value()).getResultItem((HolderLookup.Provider)serverLevel.registryAccess()).getCount());
        int n5 = (job.craftLeft + n4 - 1) / n4;
        ArrayList<ItemStack> arrayList2 = new ArrayList<ItemStack>();
        for (ItemStack object2 : list) {
            arrayList2.add(object2.copy());
        }
        Ingredient ingredient = null;
        for (Ingredient ingredient2 : arrayList) {
            itemStackArray = Crafts.takeOne(arrayList2, arg_0 -> ((Ingredient)ingredient2).test(arg_0));
            if (!itemStackArray.isEmpty()) continue;
            ingredient = ingredient2;
            break;
        }
        if (ingredient != null) {
            Ingredient ingredient3 = ingredient;
            int n6 = 0;
            for (Ingredient ingredient4 : arrayList) {
                if (ingredient4 != ingredient3 && !ingredient4.equals(ingredient3)) continue;
                ++n6;
            }
            if (!Crafts.fetchAny(serverPlayer, entry, serverLevel, mob, job, list, n, n2, arg_0 -> ((Ingredient)ingredient3).test(arg_0), Math.min(64, n6 * n5))) {
                itemStackArray = ingredient3.getItems();
                Crafts.warnOnce(serverPlayer, job, "ideology.coaldust.work.no_material", itemStackArray.length > 0 ? itemStackArray[0].getHoverName() : "?");
                Workers.idleNear(mob, job);
            }
            return;
        }
        Object var14_19 = null;
        double d = Double.MAX_VALUE;
        BlockPos blockPos = mob.blockPosition();
        for (Object object : BlockPos.betweenClosed((BlockPos)blockPos.offset(-16, -6, -16), (BlockPos)blockPos.offset(16, 6, 16))) {
            double d2;
            if (!serverLevel.getBlockState((BlockPos)object).is(Blocks.CRAFTING_TABLE) || !((d2 = object.distSqr((Vec3i)blockPos)) < d)) continue;
            d = d2;
            BlockPos blockPos2 = object.immutable();
        }
        if (var14_20 == null) {
            Crafts.warnOnce(serverPlayer, job, "ideology.coaldust.work.no_table", new Object[0]);
            return;
        }
        if (!Crafts.approach(serverLevel, mob, job, Vec3.atCenterOf((Vec3i)var14_20), 2.8)) {
            return;
        }
        for (n3 = 0; job.craftLeft > 0 && n3 < 16; ++n3) {
            ItemStack itemStack;
            Ingredient ingredient52;
            Object object;
            object = new ArrayList();
            boolean bl = true;
            for (Ingredient ingredient52 : arrayList) {
                ItemStack itemStack2 = Crafts.takeOne(list, arg_0 -> ((Ingredient)ingredient52).test(arg_0));
                if (itemStack2.isEmpty()) {
                    bl = false;
                    break;
                }
                object.add(itemStack2);
            }
            if (!bl) {
                itemStack = object.iterator();
                while (itemStack.hasNext()) {
                    ingredient52 = (ItemStack)itemStack.next();
                    Workers.addToBag(list, (ItemStack)ingredient52, n + 4);
                }
                break;
            }
            itemStack = object.iterator();
            while (itemStack.hasNext()) {
                ingredient52 = (ItemStack)itemStack.next();
                if (!ingredient52.hasCraftingRemainingItem()) continue;
                Workers.addToBag(list, ingredient52.getCraftingRemainingItem(), n + 4);
            }
            itemStack = ((CraftingRecipe)recipeHolder.value()).getResultItem((HolderLookup.Provider)serverLevel.registryAccess()).copy();
            job.craftLeft -= itemStack.getCount();
            ingredient52 = Workers.addToBag(list, itemStack, n + 4);
            if (ingredient52.isEmpty()) continue;
            Block.popResource((Level)serverLevel, (BlockPos)mob.blockPosition(), (ItemStack)ingredient52);
        }
        if (n3 > 0) {
            mob.swing(InteractionHand.MAIN_HAND);
            serverLevel.playSound(null, (BlockPos)var14_20, SoundEvents.VILLAGER_WORK_TOOLSMITH, SoundSource.BLOCKS, 0.7f, 1.1f);
            serverLevel.sendParticles((ParticleOptions)ParticleTypes.HAPPY_VILLAGER, (double)var14_20.getX() + 0.5, (double)var14_20.getY() + 1.1, (double)var14_20.getZ() + 0.5, 4, 0.3, 0.1, 0.3, 0.0);
        }
        if (job.craftLeft <= 0) {
            job.craftLeft = 0;
            Workers.finish(serverPlayer, mob, job);
        }
    }

    private Crafts() {
    }

    private static /* synthetic */ boolean lambda$pasture$2(Animal animal, ItemStack itemStack) {
        return animal.isFood(itemStack);
    }
}
