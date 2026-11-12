/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.Container
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.BoneMealItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.ChestBlock
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.pathfinder.Path
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.common.Tags$Blocks
 */
package com.coaldust.ideology;

import com.coaldust.ideology.Abyss;
import com.coaldust.ideology.Crafts;
import com.coaldust.ideology.IdeologyManager;
import com.coaldust.ideology.Summons;
import com.coaldust.ideology.Trait;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

public final class Workers {
    static final int WORKER_SLOT = 300;
    static final String WORKER = "worker";
    static final String K_TASK = "coaldust_task";
    static final String K_BOX = "coaldust_box";
    static final String K_FILL = "coaldust_fill";
    static final String K_BAG = "coaldust_bag";
    static final String K_ORIGIN = "coaldust_origin";
    static final int MAX_SIDE = 32;
    private static final Map<UUID, Owner> OWNERS = new HashMap<UUID, Owner>();
    private static final Map<UUID, Job> JOBS = new HashMap<UUID, Job>();
    private static final Map<BlockPos, Long> CLAIMS = new HashMap<BlockPos, Long>();
    private static long NOW;
    static final int ORE_R = 16;
    static final String[] MODES;

    static int count(Abyss.Entry entry) {
        return 1 + entry.istat("w_count");
    }

    static int bagSize(Abyss.Entry entry) {
        return 9 + entry.istat("w_bag");
    }

    static float speed(Abyss.Entry entry) {
        return 1.0f + (float)entry.stat("w_speed");
    }

    static float luck(Abyss.Entry entry) {
        return (float)entry.stat("w_luck");
    }

    static Owner owner(ServerPlayer serverPlayer) {
        return OWNERS.computeIfAbsent(serverPlayer.getUUID(), uUID -> new Owner());
    }

    static long NOW_TICK() {
        return NOW;
    }

    private static boolean claimed(BlockPos blockPos) {
        Long l = CLAIMS.get(blockPos);
        return l != null && l > NOW;
    }

    private static void claim(BlockPos blockPos) {
        CLAIMS.put(blockPos, NOW + 600L);
    }

    private static void unclaim(BlockPos blockPos) {
        if (blockPos != null) {
            CLAIMS.remove(blockPos);
        }
    }

    static Job job(Mob mob) {
        return JOBS.computeIfAbsent(mob.getUUID(), uUID -> Workers.restore(mob));
    }

    private static Job restore(Mob mob) {
        Job job = new Job();
        CompoundTag compoundTag = mob.getPersistentData();
        try {
            job.task = Task.valueOf(compoundTag.getString(K_TASK));
        }
        catch (Exception exception) {
            job.task = Task.FOLLOW;
        }
        int[] nArray = compoundTag.getIntArray(K_BOX);
        if (nArray.length == 6) {
            job.a = new BlockPos(nArray[0], nArray[1], nArray[2]);
            job.b = new BlockPos(nArray[3], nArray[4], nArray[5]);
        }
        if (compoundTag.contains(K_FILL)) {
            Workers.parseOpts(job, compoundTag.getString(K_FILL));
        }
        if (job.task == Task.COPY) {
            job.task = Task.FOLLOW;
        }
        if (job.a == null && job.task != Task.FOLLOW && job.task != Task.COLLECT && job.task != Task.ORE && job.task != Task.IDLE && job.task != Task.CRAFT) {
            job.task = Task.FOLLOW;
        }
        if (job.task == Task.CRAFT && (job.craftOut == null || job.craftLeft <= 0)) {
            job.task = Task.IDLE;
        }
        return job;
    }

    private static void save(Mob mob, Job job) {
        CompoundTag compoundTag = mob.getPersistentData();
        compoundTag.putString(K_TASK, job.task.name());
        if (job.task == Task.ORE) {
            compoundTag.remove(K_BOX);
        } else if (job.a != null) {
            compoundTag.putIntArray(K_BOX, new int[]{job.a.getX(), job.a.getY(), job.a.getZ(), job.b.getX(), job.b.getY(), job.b.getZ()});
        }
        String string = Workers.optString(job);
        if (string != null) {
            compoundTag.putString(K_FILL, string);
        } else {
            compoundTag.remove(K_FILL);
        }
    }

    public static boolean isWorker(Entity entity) {
        return Summons.isOwned(entity) && entity.getPersistentData().getInt("coaldust_slot") >= 300;
    }

    static List<ItemStack> bag(Mob mob) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        ListTag listTag = mob.getPersistentData().getList(K_BAG, 10);
        for (int i = 0; i < listTag.size(); ++i) {
            ItemStack itemStack = ItemStack.parseOptional((HolderLookup.Provider)mob.registryAccess(), (CompoundTag)listTag.getCompound(i));
            if (itemStack.isEmpty()) continue;
            arrayList.add(itemStack);
        }
        return arrayList;
    }

    static void setBag(Mob mob, List<ItemStack> list) {
        ListTag listTag = new ListTag();
        for (ItemStack itemStack : list) {
            if (itemStack.isEmpty()) continue;
            listTag.add((Object)itemStack.save((HolderLookup.Provider)mob.registryAccess()));
        }
        mob.getPersistentData().put(K_BAG, (Tag)listTag);
    }

    static ItemStack addToBag(List<ItemStack> list, ItemStack itemStack, int n) {
        for (ItemStack itemStack2 : list) {
            if (itemStack.isEmpty()) break;
            if (!ItemStack.isSameItemSameComponents((ItemStack)itemStack2, (ItemStack)itemStack) || itemStack2.getCount() >= itemStack2.getMaxStackSize()) continue;
            int n2 = Math.min(itemStack.getCount(), itemStack2.getMaxStackSize() - itemStack2.getCount());
            itemStack2.grow(n2);
            itemStack.shrink(n2);
        }
        while (!itemStack.isEmpty() && list.size() < n) {
            int n3 = Math.min(itemStack.getCount(), itemStack.getMaxStackSize());
            list.add(itemStack.split(n3));
        }
        return itemStack;
    }

    static int countIn(List<ItemStack> list, Item item) {
        int n = 0;
        for (ItemStack itemStack : list) {
            if (!itemStack.is(item)) continue;
            n += itemStack.getCount();
        }
        return n;
    }

    static boolean take(List<ItemStack> list, Item item, int n) {
        if (Workers.countIn(list, item) < n) {
            return false;
        }
        for (ItemStack itemStack : list) {
            if (n <= 0) break;
            if (!itemStack.is(item)) continue;
            int n2 = Math.min(n, itemStack.getCount());
            itemStack.shrink(n2);
            n -= n2;
        }
        list.removeIf(ItemStack::isEmpty);
        return true;
    }

    static String dim(ServerLevel serverLevel) {
        return serverLevel.dimension().location().toString();
    }

    static Container container(ServerLevel serverLevel, BlockPos blockPos) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof ChestBlock) {
            ChestBlock chestBlock = (ChestBlock)block;
            return ChestBlock.getContainer((ChestBlock)chestBlock, (BlockState)blockState, (Level)serverLevel, (BlockPos)blockPos, (boolean)true);
        }
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
        return blockEntity instanceof Container ? (block = (Container)blockEntity) : null;
    }

    static List<ChestCfg> chestsFor(Abyss.Entry entry, ServerLevel serverLevel, int n) {
        ArrayList<ChestCfg> arrayList = new ArrayList<ChestCfg>();
        String string = Workers.dim(serverLevel);
        entry.chests.removeIf(chestCfg -> chestCfg.dim.equals(string) && serverLevel.isLoaded(chestCfg.pos) && Workers.container(serverLevel, chestCfg.pos) == null);
        for (ChestCfg chestCfg2 : entry.chests) {
            if (!chestCfg2.dim.equals(string) || !chestCfg2.pub && !chestCfg2.bound.contains(n)) continue;
            arrayList.add(chestCfg2);
        }
        return arrayList;
    }

    static ChestCfg cfgAt(Abyss.Entry entry, ServerLevel serverLevel, BlockPos blockPos) {
        for (ChestCfg chestCfg : entry.chests) {
            if (!chestCfg.dim.equals(Workers.dim(serverLevel)) || !chestCfg.pos.equals(blockPos)) continue;
            return chestCfg;
        }
        return null;
    }

    static ItemStack insert(Container container, ItemStack itemStack) {
        int n;
        for (n = 0; n < container.getContainerSize() && !itemStack.isEmpty(); ++n) {
            ItemStack itemStack2 = container.getItem(n);
            if (itemStack2.isEmpty() || !ItemStack.isSameItemSameComponents((ItemStack)itemStack2, (ItemStack)itemStack) || itemStack2.getCount() >= itemStack2.getMaxStackSize()) continue;
            int n2 = Math.min(itemStack.getCount(), itemStack2.getMaxStackSize() - itemStack2.getCount());
            itemStack2.grow(n2);
            itemStack.shrink(n2);
        }
        for (n = 0; n < container.getContainerSize() && !itemStack.isEmpty(); ++n) {
            if (!container.getItem(n).isEmpty()) continue;
            container.setItem(n, itemStack.split(Math.min(itemStack.getCount(), itemStack.getMaxStackSize())));
        }
        container.setChanged();
        return itemStack;
    }

    static void compact(Container container) {
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.isEmpty() || itemStack.getCount() >= itemStack.getMaxStackSize()) continue;
            for (int j = i + 1; j < container.getContainerSize() && itemStack.getCount() < itemStack.getMaxStackSize(); ++j) {
                ItemStack itemStack2 = container.getItem(j);
                if (itemStack2.isEmpty() || !ItemStack.isSameItemSameComponents((ItemStack)itemStack, (ItemStack)itemStack2)) continue;
                int n = Math.min(itemStack2.getCount(), itemStack.getMaxStackSize() - itemStack.getCount());
                itemStack.grow(n);
                itemStack2.shrink(n);
                if (!itemStack2.isEmpty()) continue;
                container.setItem(j, ItemStack.EMPTY);
            }
        }
        container.setChanged();
    }

    static String id(Item item) {
        return BuiltInRegistries.ITEM.getKey((Object)item).toString();
    }

    public static void corner(ServerPlayer serverPlayer, int n, BlockPos blockPos) {
        if (!IdeologyManager.has(serverPlayer, Trait.SUMMONER)) {
            return;
        }
        if (serverPlayer.distanceToSqr((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) > 9216.0) {
            return;
        }
        Owner owner = Workers.owner(serverPlayer);
        if (n == 1) {
            owner.p1 = blockPos.immutable();
        } else {
            owner.p2 = blockPos.immutable();
        }
        owner.showUntil = serverPlayer.level().getGameTime() + 1200L;
        serverPlayer.displayClientMessage((Component)Component.translatable((String)(n == 1 ? "ideology.coaldust.work.p1" : "ideology.coaldust.work.p2"), (Object[])new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()}).withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    public static void show(ServerPlayer serverPlayer) {
        Workers.owner((ServerPlayer)serverPlayer).showUntil = serverPlayer.level().getGameTime() + 1200L;
    }

    public static void rename(ServerPlayer serverPlayer, int n, String string) {
        if (!IdeologyManager.has(serverPlayer, Trait.SUMMONER) || n < 0 || n >= Workers.count(Abyss.entry(serverPlayer))) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (c < ' ' || c == '\u00a7' || c == '\u007f') continue;
            stringBuilder.append(c);
        }
        Object object = stringBuilder.toString().trim().replaceAll(" +", " ");
        if (((String)object).length() > 24) {
            object = ((String)object).substring(0, 24);
        }
        Abyss.Entry entry = Abyss.entry(serverPlayer);
        if (((String)object).isEmpty() || ((String)object).equals("-")) {
            entry.names.remove(n);
        } else {
            entry.names.put(n, (String)object);
        }
        Abyss.data(serverPlayer.getServer()).setDirty();
        for (Mob mob : Workers.workers(serverPlayer)) {
            if (mob.getPersistentData().getInt("coaldust_slot") - 300 != n) continue;
            Summons.configure(mob);
        }
    }

    public static void select(ServerPlayer serverPlayer, int n) {
        Owner owner = Workers.owner(serverPlayer);
        if (n < 0) {
            owner.selected.clear();
            return;
        }
        if (n >= 1000) {
            for (int i = 0; i < Workers.count(Abyss.entry(serverPlayer)); ++i) {
                owner.selected.add(i);
            }
            return;
        }
        if (!owner.selected.remove(n)) {
            owner.selected.add(n);
        }
    }

    private static BlockPos min(BlockPos blockPos, BlockPos blockPos2) {
        return new BlockPos(Math.min(blockPos.getX(), blockPos2.getX()), Math.min(blockPos.getY(), blockPos2.getY()), Math.min(blockPos.getZ(), blockPos2.getZ()));
    }

    private static BlockPos max(BlockPos blockPos, BlockPos blockPos2) {
        return new BlockPos(Math.max(blockPos.getX(), blockPos2.getX()), Math.max(blockPos.getY(), blockPos2.getY()), Math.max(blockPos.getZ(), blockPos2.getZ()));
    }

    public static void order(ServerPlayer serverPlayer, String string, String string2) {
        boolean bl;
        Task task;
        if (!IdeologyManager.has(serverPlayer, Trait.SUMMONER)) {
            return;
        }
        Owner owner = Workers.owner(serverPlayer);
        if (string.equals("template")) {
            if (!Workers.box(serverPlayer, owner)) {
                return;
            }
            BlockPos blockPos = Workers.min(owner.p1, owner.p2);
            BlockPos blockPos2 = Workers.max(owner.p1, owner.p2);
            HashMap<BlockPos, BlockState> hashMap = new HashMap<BlockPos, BlockState>();
            for (BlockPos blockPos3 : BlockPos.betweenClosed((BlockPos)blockPos, (BlockPos)blockPos2)) {
                BlockState blockState = serverPlayer.level().getBlockState(blockPos3);
                if (blockState.isAir() || blockState.getBlock().asItem() == Items.AIR) continue;
                hashMap.put(blockPos3.subtract((Vec3i)blockPos).immutable(), blockState);
            }
            owner.template = hashMap;
            owner.tplA = owner.p1;
            owner.tplB = owner.p2;
            owner.tplSize = blockPos2.subtract((Vec3i)blockPos).offset(1, 1, 1);
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.template", (Object[])new Object[]{hashMap.size()}).withStyle(ChatFormatting.LIGHT_PURPLE), true);
            return;
        }
        try {
            task = Task.valueOf(string.toUpperCase());
        }
        catch (Exception exception) {
            return;
        }
        boolean bl2 = bl = task != Task.FOLLOW && task != Task.COLLECT && task != Task.COPY && task != Task.ORE && task != Task.CRAFT;
        if (bl && !Workers.box(serverPlayer, owner)) {
            return;
        }
        if (task == Task.COPY && (owner.template == null || owner.p1 == null)) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.need_template").withStyle(ChatFormatting.RED), true);
            return;
        }
        Job job = new Job();
        if (task == Task.PASTURE || task == Task.SMELT || task == Task.CRAFT) {
            job.task = task;
            if (string2 == null || !Workers.parseOpts(job, string2.trim().replaceFirst(" ", "|")) || task == Task.SMELT && job.fills.isEmpty() || task == Task.CRAFT && job.craftOut == null) {
                serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.bad_order").withStyle(ChatFormatting.RED), true);
                return;
            }
        }
        if (task == Task.FILL && (string2 == null || !Workers.parseFill(job, string2.trim().replaceFirst(" ", "|")) || job.fills.isEmpty())) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.need_block").withStyle(ChatFormatting.RED), true);
            return;
        }
        if (owner.selected.isEmpty()) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.none_selected").withStyle(ChatFormatting.RED), true);
            return;
        }
        if (task == Task.FOLLOW) {
            for (int n : owner.selected) {
                Summons.comeBack(serverPlayer.getUUID(), 300 + n);
            }
        }
        int n = 0;
        for (Mob mob : Workers.workers(serverPlayer)) {
            int n2 = mob.getPersistentData().getInt("coaldust_slot") - 300;
            if (!owner.selected.contains(n2)) continue;
            Job job2 = new Job();
            job2.task = task;
            if (bl || task == Task.COLLECT && owner.p1 != null && owner.p2 != null) {
                int n3;
                job2.a = Workers.min(owner.p1, owner.p2);
                job2.b = Workers.max(owner.p1, owner.p2);
                int n4 = task == Task.WOOD ? 32 : (task == Task.FARM ? 1 : (n3 = task == Task.COLLECT ? 2 : 0));
                if (n3 > 0) {
                    job2.b = new BlockPos(job2.b.getX(), Math.min(job2.b.getY() + n3, serverPlayer.level().getMaxBuildHeight() - 1), job2.b.getZ());
                }
            }
            if (task == Task.COPY) {
                BlockPos blockPos;
                job2.origin = blockPos = owner.p2 != null && !owner.p2.equals(owner.tplB) && !owner.p2.equals(owner.tplA) ? Workers.min(owner.p1, owner.p2) : owner.p1;
                job2.template = owner.template;
                job2.a = blockPos;
                job2.b = blockPos.offset((Vec3i)owner.tplSize).offset(-1, -1, -1);
            }
            job2.fills.addAll(job.fills);
            job2.mode = job.mode;
            job2.breed = job.breed;
            job2.shear = job.shear;
            job2.milk = job.milk;
            job2.cull = job.cull;
            job2.limit = job.limit;
            job2.craftOut = job.craftOut;
            job2.craftLeft = job.craftLeft;
            Workers.release(JOBS.put(mob.getUUID(), job2));
            Workers.save(mob, job2);
            Workers.tool(mob, task);
            ++n;
        }
        serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.ordered", (Object[])new Object[]{n, Component.translatable((String)("ideology.coaldust.work.task." + task.name().toLowerCase()))}).withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    private static boolean box(ServerPlayer serverPlayer, Owner owner) {
        if (owner.p1 == null || owner.p2 == null) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.need_box").withStyle(ChatFormatting.RED), true);
            return false;
        }
        BlockPos blockPos = Workers.max(owner.p1, owner.p2).subtract((Vec3i)Workers.min(owner.p1, owner.p2));
        if (blockPos.getX() >= 32 || blockPos.getY() >= 32 || blockPos.getZ() >= 32) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.too_big", (Object[])new Object[]{32}).withStyle(ChatFormatting.RED), true);
            return false;
        }
        return true;
    }

    private static void release(Job job) {
        if (job != null) {
            Workers.unclaim(job.cur);
        }
    }

    static void tool(Mob mob, Task task) {
        Item item = switch (task.ordinal()) {
            case 1 -> Items.IRON_AXE;
            case 2 -> Items.IRON_PICKAXE;
            case 3 -> Items.IRON_SHOVEL;
            case 6 -> Items.IRON_HOE;
            case 4, 5 -> Items.BRICKS;
            case 8 -> Items.AIR;
            default -> Items.AIR;
        };
        mob.setItemSlot(EquipmentSlot.MAINHAND, item == Items.AIR ? ItemStack.EMPTY : new ItemStack((ItemLike)item));
    }

    public static void chest(ServerPlayer serverPlayer, BlockPos blockPos, String string, String string2) {
        if (!IdeologyManager.has(serverPlayer, Trait.SUMMONER)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)serverPlayer.level();
        if (serverPlayer.distanceToSqr((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) > 64.0 || Workers.container(serverLevel, blockPos) == null) {
            return;
        }
        Abyss.Entry entry = Abyss.entry(serverPlayer);
        ChestCfg chestCfg = Workers.cfgAt(entry, serverLevel, blockPos);
        if (chestCfg == null) {
            chestCfg = new ChestCfg();
            chestCfg.dim = Workers.dim(serverLevel);
            chestCfg.pos = blockPos.immutable();
            entry.chests.add(chestCfg);
        }
        switch (string) {
            case "pub": {
                chestCfg.pub = !chestCfg.pub;
                break;
            }
            case "bind": {
                try {
                    int n = Integer.parseInt(string2);
                    if (chestCfg.bound.remove(n)) break;
                    chestCfg.bound.add(n);
                }
                catch (Exception exception) {}
                break;
            }
            case "add": {
                if (string2 == null || chestCfg.filter.contains(string2) || chestCfg.filter.size() >= 18 || BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)string2)) == Items.AIR) break;
                chestCfg.filter.add(string2);
                break;
            }
            case "del": {
                chestCfg.filter.remove(string2);
                break;
            }
            case "forget": {
                entry.chests.remove(chestCfg);
                break;
            }
        }
        Abyss.data(serverPlayer.getServer()).setDirty();
    }

    static List<Mob> workers(ServerPlayer serverPlayer) {
        ArrayList<Mob> arrayList = new ArrayList<Mob>();
        for (Mob mob : Summons.list(serverPlayer.getUUID())) {
            if (!Workers.isWorker((Entity)mob) || !mob.isAlive() || mob.isRemoved()) continue;
            arrayList.add(mob);
        }
        return arrayList;
    }

    static void tick(ServerPlayer serverPlayer, Abyss.Entry entry) {
        ServerLevel serverLevel = (ServerLevel)serverPlayer.level();
        NOW = serverLevel.getGameTime();
        if (NOW % 1200L == 0L) {
            CLAIMS.values().removeIf(l -> l <= NOW);
        }
        for (Mob mob : Workers.workers(serverPlayer)) {
            if (mob.level() != serverLevel) continue;
            Job job = Workers.job(mob);
            try {
                Workers.step(serverPlayer, entry, serverLevel, mob, job);
            }
            catch (Exception exception) {
                job.cur = null;
            }
        }
        if (serverPlayer.tickCount % 10 == 0 && Workers.owner((ServerPlayer)serverPlayer).showUntil > NOW) {
            Workers.outline(serverPlayer, serverLevel);
        }
    }

    private static void outline(ServerPlayer serverPlayer, ServerLevel serverLevel) {
        Owner owner = Workers.owner(serverPlayer);
        if (owner.p1 != null) {
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.END_ROD, true, (double)owner.p1.getX() + 0.5, (double)owner.p1.getY() + 1.1, (double)owner.p1.getZ() + 0.5, 2, 0.1, 0.1, 0.1, 0.0);
        }
        if (owner.p2 != null) {
            serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.ELECTRIC_SPARK, true, (double)owner.p2.getX() + 0.5, (double)owner.p2.getY() + 1.1, (double)owner.p2.getZ() + 0.5, 2, 0.1, 0.1, 0.1, 0.0);
        }
        if (owner.p1 == null || owner.p2 == null) {
            return;
        }
        BlockPos blockPos = Workers.min(owner.p1, owner.p2);
        BlockPos blockPos2 = Workers.max(owner.p1, owner.p2).offset(1, 1, 1);
        double[] dArray = new double[]{blockPos.getX(), blockPos2.getX()};
        double[] dArray2 = new double[]{blockPos.getY(), blockPos2.getY()};
        double[] dArray3 = new double[]{blockPos.getZ(), blockPos2.getZ()};
        int n = 0;
        for (int i = 0; i < 12; ++i) {
            double d;
            double d2;
            double d3;
            double d4;
            double d5;
            double d6;
            int n2 = i % 4;
            if (i < 4) {
                d6 = dArray[0];
                d5 = dArray[1];
                d3 = d4 = dArray2[n2 & 1];
                d = d2 = dArray3[n2 >> 1];
            } else if (i < 8) {
                d3 = dArray2[0];
                d4 = dArray2[1];
                d6 = d5 = dArray[n2 & 1];
                d = d2 = dArray3[n2 >> 1];
            } else {
                d = dArray3[0];
                d2 = dArray3[1];
                d6 = d5 = dArray[n2 & 1];
                d3 = d4 = dArray2[n2 >> 1];
            }
            double d7 = Math.max(Math.abs(d5 - d6), Math.max(Math.abs(d4 - d3), Math.abs(d2 - d)));
            int n3 = (int)Math.min(48.0, Math.max(2.0, d7));
            for (int j = 0; j <= n3 && n < 400; ++j, ++n) {
                double d8 = (double)j / (double)n3;
                serverLevel.sendParticles(serverPlayer, (ParticleOptions)ParticleTypes.WITCH, true, d6 + (d5 - d6) * d8, d3 + (d4 - d3) * d8, d + (d2 - d) * d8, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    private static boolean inBox(Job job, BlockPos blockPos) {
        return job.a != null && blockPos.getX() >= job.a.getX() && blockPos.getX() <= job.b.getX() && blockPos.getY() >= job.a.getY() && blockPos.getY() <= job.b.getY() && blockPos.getZ() >= job.a.getZ() && blockPos.getZ() <= job.b.getZ();
    }

    private static void step(ServerPlayer serverPlayer, Abyss.Entry entry, ServerLevel serverLevel, Mob mob, Job job) {
        BlockPos blockPos;
        boolean bl;
        boolean bl2;
        int n = mob.getPersistentData().getInt("coaldust_slot");
        List<ItemStack> list = Workers.bag(mob);
        int n2 = Workers.bagSize(entry);
        if (!job.dig.isEmpty()) {
            Workers.digStep(serverLevel, mob, job, list, n2, entry);
            Workers.setBag(mob, list);
            return;
        }
        if (job.task == Task.FOLLOW || job.task == Task.IDLE) {
            if (job.cantDeliver && NOW - job.cantSince > 600L) {
                job.cantDeliver = false;
                job.warned = false;
            }
            if (!list.isEmpty() && !job.cantDeliver) {
                if (Workers.deliver(serverPlayer, entry, serverLevel, mob, list, n, job)) {
                    Workers.setBag(mob, list);
                }
                return;
            }
            if (job.task == Task.FOLLOW) {
                Workers.follow(serverLevel, serverPlayer, mob, job);
            }
            return;
        }
        boolean bl3 = list.size() >= n2 && list.stream().allMatch(itemStack -> itemStack.getCount() >= itemStack.getMaxStackSize());
        boolean bl4 = list.stream().anyMatch(itemStack -> !Workers.keep(job, itemStack));
        if (bl4 && (bl3 || list.size() >= n2 - 1 && job.task != Task.FILL && job.task != Task.COPY)) {
            if (Workers.deliver(serverPlayer, entry, serverLevel, mob, list, n, job)) {
                Workers.setBag(mob, list);
            }
            return;
        }
        if (job.trip == 1) {
            Workers.fetch(serverPlayer, entry, serverLevel, mob, list, n, job);
            Workers.setBag(mob, list);
            return;
        }
        if (job.trip == 2) {
            Workers.deliver(serverPlayer, entry, serverLevel, mob, list, n, job);
            Workers.setBag(mob, list);
            return;
        }
        if (!job.dig.isEmpty()) {
            Workers.digStep(serverLevel, mob, job, list, n2, entry);
            Workers.setBag(mob, list);
            return;
        }
        if (job.task == Task.PASTURE) {
            Crafts.pasture(serverPlayer, entry, serverLevel, mob, job, list, n2, n);
            Workers.setBag(mob, list);
            return;
        }
        if (job.task == Task.SMELT) {
            Crafts.smelt(serverPlayer, entry, serverLevel, mob, job, list, n2, n);
            Workers.setBag(mob, list);
            return;
        }
        if (job.task == Task.CRAFT) {
            Crafts.craft(serverPlayer, entry, serverLevel, mob, job, list, n2, n);
            Workers.setBag(mob, list);
            return;
        }
        if (job.cur != null && !Workers.valid(serverLevel, mob, job, job.cur, list)) {
            Workers.unclaim(job.cur);
            job.cur = null;
        }
        if (job.cur == null) {
            if (--job.rescan > 0) {
                Workers.idleNear(mob, job);
                return;
            }
            job.cur = Workers.find(serverLevel, mob, job, list);
            job.work = 0;
            job.stuck = 0;
            job.best = Double.MAX_VALUE;
            if (job.cur == null) {
                boolean bl5;
                job.rescan = 8;
                boolean bl6 = bl5 = job.task == Task.WOOD || job.task == Task.DIG || job.task == Task.ORE || (job.task == Task.FILL || job.task == Task.COPY) && !Workers.anyLeft(serverLevel, job);
                if (bl5) {
                    Workers.finish(serverPlayer, mob, job);
                    return;
                }
                if (job.task == Task.FILL || job.task == Task.COPY) {
                    Workers.fetch(serverPlayer, entry, serverLevel, mob, list, n, job);
                } else if (!list.isEmpty() && job.task != Task.FARM) {
                    Workers.deliver(serverPlayer, entry, serverLevel, mob, list, n, job);
                } else if (job.task == Task.FARM && list.size() > 3) {
                    Workers.deliver(serverPlayer, entry, serverLevel, mob, list, n, job);
                }
                Workers.setBag(mob, list);
                Workers.idleNear(mob, job);
                return;
            }
            Workers.claim(job.cur);
        }
        BlockPos blockPos2 = job.cur;
        Workers.claim(blockPos2);
        boolean bl7 = bl2 = job.task == Task.FILL || job.task == Task.COPY;
        double d = job.task == Task.WOOD ? 3.2 : (bl2 ? 3.6 : 4.3);
        Vec3 vec3 = Vec3.atCenterOf((Vec3i)blockPos2);
        double d2 = mob.getX() - vec3.x;
        double d3 = mob.getZ() - vec3.z;
        double d4 = mob.getY() + 1.0 - vec3.y;
        double d5 = Math.sqrt(d2 * d2 + d3 * d3);
        boolean bl8 = bl = job.task == Task.ORE || job.task == Task.DIG;
        if (bl) {
            d = 3.0;
        }
        boolean bl9 = job.task == Task.WOOD ? d5 <= d && d4 > -34.0 && d4 < 3.0 : (bl2 ? d5 <= d && d4 > -10.0 && d4 < 6.0 : Math.sqrt(d2 * d2 + d3 * d3 + d4 * d4) <= d);
        BlockPos blockPos3 = blockPos = bl ? Workers.standSpot(serverLevel, blockPos2) : null;
        if (bl9 && bl && !Workers.clearSight(serverLevel, mob, blockPos2)) {
            if (blockPos != null && (job.stuck < 30 || Workers.reachable(mob, blockPos))) {
                mob.getNavigation().moveTo((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, 1.0);
                job.stuck = job.stuck < 30 ? ++job.stuck : 0;
                return;
            }
            Workers.planTunnel(serverLevel, mob, blockPos2);
            if (!job.dig.isEmpty()) {
                return;
            }
            bl9 = false;
        }
        if (!bl9) {
            if (blockPos != null) {
                mob.getNavigation().moveTo((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, 1.0);
            } else {
                mob.getNavigation().moveTo(vec3.x, (double)blockPos2.getY(), vec3.z, 1.0);
            }
            double d6 = mob.distanceToSqr(vec3.x, vec3.y, vec3.z);
            if (d6 < job.best - 0.5) {
                job.best = d6;
                job.stuck = 0;
            } else if (++job.stuck > 24) {
                if (bl && blockPos != null && Workers.reachable(mob, blockPos)) {
                    job.stuck = 0;
                    job.best = Double.MAX_VALUE;
                    return;
                }
                if (bl) {
                    Workers.planTunnel(serverLevel, mob, blockPos2);
                    if (!job.dig.isEmpty()) {
                        job.stuck = 0;
                        job.best = Double.MAX_VALUE;
                        return;
                    }
                }
                Workers.unclaim(blockPos2);
                job.cur = null;
                job.rescan = 2;
            }
            return;
        }
        mob.getNavigation().stop();
        mob.getLookControl().setLookAt(vec3.x, vec3.y, vec3.z);
        Workers.act(serverPlayer, entry, serverLevel, mob, job, list, n2);
        Workers.setBag(mob, list);
    }

    private static void follow(ServerLevel serverLevel, ServerPlayer serverPlayer, Mob mob, Job job) {
        double d = mob.distanceToSqr((Entity)serverPlayer);
        if (d > 25.0) {
            Workers.moveOrDig(serverLevel, mob, job, serverPlayer.blockPosition(), d > 144.0 ? 1.3 : 1.05);
        } else {
            job.navStuck = 0;
            job.navTarget = null;
        }
    }

    static void moveOrDig(ServerLevel serverLevel, Mob mob, Job job, BlockPos blockPos, double d) {
        if (job.navTarget == null || job.navTarget.distSqr((Vec3i)blockPos) > 4.0) {
            job.navTarget = blockPos.immutable();
            job.navBest = Double.MAX_VALUE;
            job.navStuck = 0;
        }
        double d2 = mob.distanceToSqr(Vec3.atCenterOf((Vec3i)blockPos));
        boolean bl = job.navLast != null && job.navLast.distanceToSqr(mob.position()) > 0.04;
        job.navLast = mob.position();
        if (d2 < job.navBest - 0.3) {
            job.navBest = d2;
            job.navStuck = 0;
        } else if (bl && mob.getNavigation().isInProgress()) {
            job.navStuck = Math.max(0, job.navStuck - 1);
        } else if (++job.navStuck > 16) {
            job.navStuck = 0;
            job.navBest = d2;
            Path path = mob.getNavigation().createPath(blockPos, 1);
            if (path != null && path.canReach()) {
                mob.getNavigation().moveTo(path, d);
                return;
            }
            Workers.planTunnel(serverLevel, mob, blockPos);
            if (!job.dig.isEmpty()) {
                mob.getNavigation().stop();
                return;
            }
        }
        if (!mob.getNavigation().isInProgress() || mob.tickCount % 40 == 0) {
            mob.getNavigation().moveTo((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, d);
        }
    }

    static void idleNear(Mob mob, Job job) {
        if (job.a == null || job.task == Task.ORE) {
            return;
        }
        BlockPos blockPos = new BlockPos((job.a.getX() + job.b.getX()) / 2, job.a.getY(), (job.a.getZ() + job.b.getZ()) / 2);
        if (mob.distanceToSqr((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()) > 64.0 && !mob.getNavigation().isInProgress()) {
            mob.getNavigation().moveTo((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 1.0);
        }
    }

    private static boolean wanted(ServerLevel serverLevel, Job job, BlockPos blockPos, BlockState blockState, List<ItemStack> list) {
        switch (job.task.ordinal()) {
            case 1: {
                return blockState.is(BlockTags.LOGS);
            }
            case 2: {
                return blockState.is(Tags.Blocks.ORES);
            }
            case 3: {
                return Workers.diggable(serverLevel, blockPos, blockState);
            }
            case 4: {
                return (blockState.isAir() || blockState.canBeReplaced()) && blockState.getFluidState().isEmpty();
            }
            case 6: {
                Block block = blockState.getBlock();
                if (block instanceof CropBlock) {
                    CropBlock cropBlock = (CropBlock)block;
                    return cropBlock.isMaxAge(blockState) || Workers.countIn(list, Items.BONE_MEAL) > 0;
                }
                if (blockState.is(Blocks.FARMLAND) && serverLevel.getBlockState(blockPos.above()).isAir()) {
                    return Workers.seed(list) != null;
                }
                return false;
            }
            case 5: {
                BlockState blockState2 = job.template.get(blockPos.subtract((Vec3i)job.origin));
                return blockState2 != null && blockState != blockState2 && (blockState.isAir() || blockState.canBeReplaced());
            }
        }
        return false;
    }

    private static boolean diggable(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        if (blockState.isAir() || !blockState.getFluidState().isEmpty() && blockState.getBlock().asItem() == Items.AIR) {
            return false;
        }
        return !(blockState.getDestroySpeed((BlockGetter)serverLevel, blockPos) < 0.0f) && serverLevel.getBlockEntity(blockPos) == null;
    }

    private static boolean valid(ServerLevel serverLevel, Mob mob, Job job, BlockPos blockPos, List<ItemStack> list) {
        if (job.task == Task.COLLECT) {
            return true;
        }
        if (!Workers.inBox(job, blockPos) && job.task != Task.COPY) {
            return false;
        }
        return Workers.wanted(serverLevel, job, blockPos, serverLevel.getBlockState(blockPos), list);
    }

    private static BlockPos find(ServerLevel serverLevel, Mob mob, Job job, List<ItemStack> list) {
        Job job2;
        UUID uUID;
        BlockPos blockPos;
        if (job.task == Task.COLLECT) {
            AABB aABB = job.a != null ? new AABB((double)job.a.getX(), (double)job.a.getY(), (double)job.a.getZ(), (double)(job.b.getX() + 1), (double)(job.b.getY() + 1), (double)(job.b.getZ() + 1)) : mob.getBoundingBox().inflate(12.0);
            ItemEntity itemEntity = null;
            double d = Double.MAX_VALUE;
            for (ItemEntity itemEntity2 : serverLevel.getEntitiesOfClass(ItemEntity.class, aABB)) {
                double d2 = mob.distanceToSqr((Entity)itemEntity2);
                if (!(d2 < d) || Workers.claimed(itemEntity2.blockPosition())) continue;
                d = d2;
                itemEntity = itemEntity2;
            }
            return itemEntity == null ? null : itemEntity.blockPosition();
        }
        if (job.task == Task.ORE) {
            blockPos = mob.blockPosition();
            job.a = blockPos.offset(-16, -16, -16);
            job.b = blockPos.offset(16, 8, 16);
        }
        if (job.a == null) {
            return null;
        }
        blockPos = null;
        double d = Double.MAX_VALUE;
        int n = Integer.MIN_VALUE;
        boolean bl = job.task == Task.DIG;
        boolean bl2 = job.task == Task.FILL || job.task == Task.COPY;
        ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
        if (bl2 && (uUID = Summons.owner((Entity)mob)) != null) {
            for (Mob mob2 : Summons.list(uUID)) {
                if (mob2 == mob || !Workers.isWorker((Entity)mob2) || mob2.isRemoved() || (job2 = JOBS.get(mob2.getUUID())) == null || job2.task != job.task) continue;
                arrayList.add(mob2.blockPosition());
                if (job2.cur == null) continue;
                arrayList.add(job2.cur);
            }
        }
        if (bl || bl2) {
            int n2;
            int n3 = n2 = bl ? job.b.getY() : job.a.getY();
            while (bl ? n2 >= job.a.getY() : n2 <= job.b.getY()) {
                block3: for (int i = job.a.getX(); i <= job.b.getX() && n == Integer.MIN_VALUE; ++i) {
                    for (int j = job.a.getZ(); j <= job.b.getZ(); ++j) {
                        job2 = new BlockPos(i, n2, j);
                        if (Workers.claimed((BlockPos)job2) || Workers.skipped(job, (BlockPos)job2) || !Workers.wanted(serverLevel, job, (BlockPos)job2, serverLevel.getBlockState((BlockPos)job2), list) || bl2 && !Workers.haveFor(job, (BlockPos)job2, list, serverLevel)) continue;
                        n = n2;
                        continue block3;
                    }
                }
                if (n != Integer.MIN_VALUE) break;
                n2 += bl ? -1 : 1;
            }
            if (n == Integer.MIN_VALUE) {
                if (bl2) {
                    Workers.noteNeed(mob, job, serverLevel, list);
                }
                return null;
            }
        }
        for (int i = job.a.getY(); i <= job.b.getY(); ++i) {
            if (n != Integer.MIN_VALUE && i != n) continue;
            for (int j = job.a.getX(); j <= job.b.getX(); ++j) {
                for (int k = job.a.getZ(); k <= job.b.getZ(); ++k) {
                    BlockState blockState;
                    job2 = new BlockPos(j, i, k);
                    if (Workers.claimed((BlockPos)job2) || Workers.skipped(job, (BlockPos)job2) || !Workers.wanted(serverLevel, job, (BlockPos)job2, blockState = serverLevel.getBlockState((BlockPos)job2), list) || bl2 && !Workers.haveFor(job, (BlockPos)job2, list, serverLevel)) continue;
                    double d3 = mob.distanceToSqr((double)j + 0.5, (double)i + 0.5, (double)k + 0.5) + (job.task == Task.WOOD ? (double)i * 0.01 : 0.0) + (double)(job.task == Task.ORE && !Workers.exposed(serverLevel, (BlockPos)job2) ? 40 : 0) + (bl2 ? Workers.crowd(arrayList, (BlockPos)job2) : 0.0);
                    if (!(d3 < d)) continue;
                    d = d3;
                    blockPos = job2.immutable();
                }
            }
        }
        return blockPos;
    }

    static boolean keep(Job job, ItemStack itemStack) {
        if (job.task == Task.FOLLOW || job.task == Task.IDLE) {
            return false;
        }
        return switch (job.task.ordinal()) {
            case 6 -> {
                BlockItem var2_3;
                Item var3_2;
                if (itemStack.is(Items.BONE_MEAL) || (var3_2 = itemStack.getItem()) instanceof BlockItem && (var2_3 = (BlockItem)var3_2).getBlock() instanceof CropBlock && itemStack.getCount() <= 16) {
                    yield true;
                }
                yield false;
            }
            case 9 -> {
                if (Crafts.isFood(itemStack.getItem()) && itemStack.getCount() <= 32 || itemStack.is(Items.BUCKET) || itemStack.is(Items.SHEARS)) {
                    yield true;
                }
                yield false;
            }
            case 10 -> {
                if (job.fills.contains(itemStack.getItem()) || Crafts.isFuel(itemStack) && !job.fills.contains(itemStack.getItem())) {
                    yield true;
                }
                yield false;
            }
            case 11 -> Crafts.isIngredient(job, itemStack);
            default -> false;
        };
    }

    static String optString(Job job) {
        return switch (job.task.ordinal()) {
            case 4 -> {
                if (job.fills.isEmpty()) {
                    yield null;
                }
                yield Workers.fillString(job);
            }
            case 9 -> (job.breed ? "b" : "") + (job.shear ? "s" : "") + (job.milk ? "m" : "") + (job.cull ? "c" : "") + "_|" + job.limit;
            case 10 -> {
                StringBuilder var1_1 = new StringBuilder("smelt|");
                for (Item var3_3 : job.fills) {
                    var1_1.append(Workers.id(var3_3)).append(',');
                }
                yield var1_1.toString();
            }
            case 11 -> {
                if (job.craftOut == null) {
                    yield null;
                }
                yield Workers.id(job.craftOut) + "|" + job.craftLeft;
            }
            default -> null;
        };
    }

    static boolean parseOpts(Job job, String string) {
        int n = string.indexOf(124);
        String string2 = n < 0 ? string : string.substring(0, n);
        String string3 = n < 0 ? "" : string.substring(n + 1).trim();
        switch (job.task.ordinal()) {
            case 9: {
                job.breed = string2.contains("b");
                job.shear = string2.contains("s");
                job.milk = string2.contains("m");
                job.cull = string2.contains("c");
                try {
                    job.limit = Math.max(2, Math.min(40, Integer.parseInt(string3)));
                }
                catch (NumberFormatException numberFormatException) {
                    job.limit = 10;
                }
                return true;
            }
            case 10: {
                job.fills.clear();
                for (String string4 : (n < 0 ? string2 : string3).split(",")) {
                    Item item;
                    if (string4.isBlank() || string4.equals("smelt") || (item = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)string4.trim()))) == Items.AIR || job.fills.contains(item) || job.fills.size() >= 6) continue;
                    job.fills.add(item);
                }
                return true;
            }
            case 11: {
                Item item = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)string2.trim()));
                if (item == Items.AIR) {
                    return false;
                }
                job.craftOut = item;
                try {
                    job.craftLeft = Math.max(1, Math.min(1024, Integer.parseInt(string3)));
                }
                catch (NumberFormatException numberFormatException) {
                    job.craftLeft = 1;
                }
                return true;
            }
        }
        return Workers.parseFill(job, string);
    }

    static boolean parseFill(Job job, String string) {
        String string2 = "random";
        String string3 = string;
        int n = string.indexOf(124);
        if (n >= 0) {
            string2 = string.substring(0, n);
            string3 = string.substring(n + 1);
        }
        job.mode = Arrays.asList(MODES).contains(string2) ? string2 : "random";
        job.fills.clear();
        for (String string4 : string3.split(",")) {
            if (string4.isBlank()) continue;
            Item item = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)string4.trim()));
            if (!(item instanceof BlockItem)) {
                return false;
            }
            if (job.fills.contains(item) || job.fills.size() >= 6) continue;
            job.fills.add(item);
        }
        return true;
    }

    static String fillString(Job job) {
        StringBuilder stringBuilder = new StringBuilder(job.mode).append('|');
        for (Item item : job.fills) {
            stringBuilder.append(Workers.id(item)).append(',');
        }
        return stringBuilder.toString();
    }

    static Item pattern(Job job, BlockPos blockPos) {
        int n = job.fills.size();
        if (n == 0) {
            return Items.AIR;
        }
        if (n == 1) {
            return job.fills.get(0);
        }
        int n2 = blockPos.getX() - job.a.getX();
        int n3 = blockPos.getY() - job.a.getY();
        int n4 = blockPos.getZ() - job.a.getZ();
        int n5 = job.b.getX() - job.a.getX() + 1;
        int n6 = job.b.getY() - job.a.getY() + 1;
        int n7 = job.b.getZ() - job.a.getZ() + 1;
        int n8 = blockPos.getX() * 73856093 ^ blockPos.getY() * 19349663 ^ blockPos.getZ() * 83492791;
        return switch (job.mode) {
            case "checker" -> job.fills.get(Math.floorMod(n2 + n3 + n4, n));
            case "layers" -> job.fills.get(Math.floorMod(n3, n));
            case "stripes" -> job.fills.get(Math.floorMod(n5 >= n7 ? n2 : n4, n));
            case "border" -> {
                boolean var12_12;
                boolean v1 = var12_12 = n5 > 1 && (n2 == 0 || n2 == n5 - 1) || n6 > 1 && (n3 == 0 || n3 == n6 - 1) || n7 > 1 && (n4 == 0 || n4 == n7 - 1);
                if (var12_12) {
                    yield job.fills.get(0);
                }
                yield job.fills.get(1 + Math.floorMod(n8, n - 1));
            }
            default -> job.fills.get(Math.floorMod(n8, n));
        };
    }

    private static boolean anyLeft(ServerLevel serverLevel, Job job) {
        if (job.a == null) {
            return false;
        }
        for (int i = job.a.getY(); i <= job.b.getY(); ++i) {
            for (int j = job.a.getX(); j <= job.b.getX(); ++j) {
                for (int k = job.a.getZ(); k <= job.b.getZ(); ++k) {
                    BlockState blockState;
                    boolean bl;
                    BlockPos blockPos = new BlockPos(j, i, k);
                    if (Workers.claimed(blockPos)) {
                        return true;
                    }
                    BlockState blockState2 = serverLevel.getBlockState(blockPos);
                    boolean bl2 = bl = (blockState2.isAir() || blockState2.canBeReplaced()) && blockState2.getFluidState().isEmpty();
                    if (job.task == Task.FILL && bl) {
                        return true;
                    }
                    if (job.task != Task.COPY || (blockState = job.template.get(blockPos.subtract((Vec3i)job.origin))) == null || blockState2 == blockState || !blockState2.isAir() && !blockState2.canBeReplaced()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    static void finish(ServerPlayer serverPlayer, Mob mob, Job job) {
        Task task = job.task;
        Workers.unclaim(job.cur);
        job.cur = null;
        job.task = Task.IDLE;
        job.cantDeliver = false;
        job.warned = false;
        Workers.save(mob, job);
        Workers.tool(mob, Task.IDLE);
        mob.getNavigation().stop();
        int n = mob.getPersistentData().getInt("coaldust_slot") - 300 + 1;
        serverPlayer.displayClientMessage((Component)Component.translatable((String)(task == Task.ORE ? "ideology.coaldust.work.done_ore" : "ideology.coaldust.work.done"), (Object[])new Object[]{n, 16}).withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    private static void stepAside(ServerLevel serverLevel, Mob mob, Job job) {
        BlockPos blockPos = mob.blockPosition();
        BlockPos blockPos2 = null;
        double d = Double.MAX_VALUE;
        for (int i = -3; i <= 3; ++i) {
            for (int j = -3; j <= 3; ++j) {
                for (int k = -2; k <= 1; ++k) {
                    double d2;
                    BlockPos blockPos3 = blockPos.offset(i, k, j);
                    if (Workers.inBox(job, blockPos3) || !serverLevel.getBlockState(blockPos3).isAir() || !serverLevel.getBlockState(blockPos3.above()).isAir() || serverLevel.getBlockState(blockPos3.below()).isAir() || !((d2 = (double)(i * i + j * j + k * k * 2)) < d)) continue;
                    d = d2;
                    blockPos2 = blockPos3;
                }
            }
        }
        if (blockPos2 != null) {
            mob.getNavigation().moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5, 1.2);
        }
    }

    private static double crowd(List<BlockPos> list, BlockPos blockPos) {
        double d = 0.0;
        for (BlockPos blockPos2 : list) {
            double d2 = blockPos2.distSqr((Vec3i)blockPos);
            if (!(d2 < 9.0)) continue;
            d += (9.0 - d2) * 3.0;
        }
        return d;
    }

    private static boolean skipped(Job job, BlockPos blockPos) {
        Long l = job.skip.get(blockPos);
        if (l == null) {
            return false;
        }
        if (l <= NOW) {
            job.skip.remove(blockPos);
            return false;
        }
        return true;
    }

    private static int needOf(ServerLevel serverLevel, Job job, Item item) {
        int n = 0;
        for (int i = job.a.getY(); i <= job.b.getY(); ++i) {
            for (int j = job.a.getX(); j <= job.b.getX(); ++j) {
                for (int k = job.a.getZ(); k <= job.b.getZ(); ++k) {
                    BlockPos blockPos = new BlockPos(j, i, k);
                    BlockState blockState = serverLevel.getBlockState(blockPos);
                    if (!blockState.isAir() && !blockState.canBeReplaced() || !blockState.getFluidState().isEmpty() || Workers.itemFor(job, blockPos) != item) continue;
                    ++n;
                }
            }
        }
        return n;
    }

    private static int sameJob(ServerPlayer serverPlayer, Job job) {
        int n = 0;
        for (Mob mob : Workers.workers(serverPlayer)) {
            Job job2 = Workers.job(mob);
            if (job2.task != job.task || job.a == null || !job.a.equals(job2.a) || !job.b.equals(job2.b)) continue;
            ++n;
        }
        return Math.max(1, n);
    }

    private static boolean exposed(ServerLevel serverLevel, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (!serverLevel.getBlockState(blockPos.relative(direction)).isAir()) continue;
            return true;
        }
        return false;
    }

    private static Item seed(List<ItemStack> list) {
        for (ItemStack itemStack : list) {
            BlockItem blockItem;
            Item item = itemStack.getItem();
            if (!(item instanceof BlockItem) || !((blockItem = (BlockItem)item).getBlock() instanceof CropBlock)) continue;
            return itemStack.getItem();
        }
        return null;
    }

    private static Item itemFor(Job job, BlockPos blockPos) {
        if (job.task == Task.FILL) {
            return Workers.pattern(job, blockPos);
        }
        BlockState blockState = job.template.get(blockPos.subtract((Vec3i)job.origin));
        return blockState == null ? Items.AIR : blockState.getBlock().asItem();
    }

    private static boolean haveFor(Job job, BlockPos blockPos, List<ItemStack> list, ServerLevel serverLevel) {
        Item item = Workers.itemFor(job, blockPos);
        return item != Items.AIR && Workers.countIn(list, item) > 0;
    }

    private static void noteNeed(Mob mob, Job job, ServerLevel serverLevel, List<ItemStack> list) {
        job.needs.clear();
        for (int i = job.a.getY(); i <= job.b.getY(); ++i) {
            for (int j = job.a.getX(); j <= job.b.getX(); ++j) {
                for (int k = job.a.getZ(); k <= job.b.getZ(); ++k) {
                    Item item;
                    BlockPos blockPos = new BlockPos(j, i, k);
                    if (Workers.skipped(job, blockPos) || !Workers.wanted(serverLevel, job, blockPos, serverLevel.getBlockState(blockPos), list) || (item = Workers.itemFor(job, blockPos)) == Items.AIR || Workers.countIn(list, item) != 0 || job.needs.contains(item)) continue;
                    job.needs.add(item);
                }
            }
        }
        job.need = job.needs.isEmpty() ? null : Workers.id(job.needs.get(0));
    }

    private static void act(ServerPlayer serverPlayer, Abyss.Entry entry, ServerLevel serverLevel, Mob mob, Job job, List<ItemStack> list, int n) {
        BlockPos blockPos = job.cur;
        if (job.task == Task.COLLECT) {
            for (ItemEntity itemEntity : serverLevel.getEntitiesOfClass(ItemEntity.class, new AABB(blockPos).inflate(1.5))) {
                ItemStack itemStack = Workers.addToBag(list, itemEntity.getItem().copy(), n);
                if (itemStack.isEmpty()) {
                    itemEntity.discard();
                    continue;
                }
                itemEntity.setItem(itemStack);
            }
            serverLevel.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.3f, 1.2f);
            Workers.unclaim(blockPos);
            job.cur = null;
            return;
        }
        BlockState blockState = serverLevel.getBlockState(blockPos);
        if (job.task == Task.FILL || job.task == Task.COPY) {
            Item item = Workers.itemFor(job, blockPos);
            List list2 = serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos));
            if (!list2.isEmpty()) {
                job.skip.put(blockPos, NOW + (long)(list2.size() == 1 && list2.get(0) == mob ? 60 : 160));
                if (list2.contains(mob)) {
                    Workers.stepAside(serverLevel, mob, job);
                }
                Workers.unclaim(blockPos);
                job.cur = null;
                job.rescan = 1;
                return;
            }
            if (!Workers.take(list, item, 1)) {
                Workers.unclaim(blockPos);
                job.cur = null;
                return;
            }
            BlockState blockState2 = job.task == Task.FILL ? ((BlockItem)item).getBlock().defaultBlockState() : job.template.get(blockPos.subtract((Vec3i)job.origin));
            serverLevel.setBlock(blockPos, blockState2, 3);
            serverLevel.playSound(null, blockPos, blockState2.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 0.8f, 1.0f);
            mob.swing(InteractionHand.MAIN_HAND);
            Workers.unclaim(blockPos);
            job.cur = null;
            return;
        }
        if (job.task == Task.FARM) {
            Workers.farm(serverLevel, mob, job, list, n, blockPos, blockState, entry);
            Workers.unclaim(blockPos);
            job.cur = null;
            return;
        }
        float f = Math.max(0.3f, blockState.getDestroySpeed((BlockGetter)serverLevel, blockPos));
        int n2 = (int)Math.max(4.0f, Math.min(80.0f, f * 18.0f / Workers.speed(entry)));
        job.work += 5;
        mob.swing(InteractionHand.MAIN_HAND);
        serverLevel.destroyBlockProgress(mob.getId(), blockPos, Math.min(9, job.work * 10 / n2));
        if (job.work % 10 == 0) {
            serverLevel.playSound(null, blockPos, blockState.getSoundType().getHitSound(), SoundSource.BLOCKS, 0.5f, 0.8f);
        }
        if (job.work < n2) {
            return;
        }
        serverLevel.destroyBlockProgress(mob.getId(), blockPos, -1);
        Workers.harvest(serverLevel, mob, blockPos, blockState, list, n, entry);
        Workers.unclaim(blockPos);
        job.cur = null;
    }

    private static void harvest(ServerLevel serverLevel, Mob mob, BlockPos blockPos, BlockState blockState, List<ItemStack> list, int n, Abyss.Entry entry) {
        ItemStack itemStack = Workers.pickTool(blockState);
        List list2 = Block.getDrops((BlockState)blockState, (ServerLevel)serverLevel, (BlockPos)blockPos, (BlockEntity)serverLevel.getBlockEntity(blockPos), (Entity)mob, (ItemStack)itemStack);
        serverLevel.destroyBlock(blockPos, false, (Entity)mob);
        boolean bl = Workers.luck(entry) > 0.0f && serverLevel.random.nextFloat() < Workers.luck(entry) && (blockState.is(Tags.Blocks.ORES) || blockState.getBlock() instanceof CropBlock);
        for (ItemStack itemStack2 : list2) {
            ItemStack itemStack3;
            if (bl) {
                itemStack2.grow(itemStack2.getCount());
            }
            if ((itemStack3 = Workers.addToBag(list, itemStack2, n)).isEmpty()) continue;
            Block.popResource((Level)serverLevel, (BlockPos)blockPos, (ItemStack)itemStack3);
        }
    }

    static ItemStack pickTool(BlockState blockState) {
        ItemStack[] itemStackArray;
        for (ItemStack itemStack : itemStackArray = new ItemStack[]{new ItemStack((ItemLike)Items.IRON_PICKAXE), new ItemStack((ItemLike)Items.IRON_AXE), new ItemStack((ItemLike)Items.IRON_SHOVEL), new ItemStack((ItemLike)Items.IRON_HOE)}) {
            if (!itemStack.isCorrectToolForDrops(blockState)) continue;
            return itemStack;
        }
        return new ItemStack((ItemLike)Items.IRON_PICKAXE);
    }

    private static void farm(ServerLevel serverLevel, Mob mob, Job job, List<ItemStack> list, int n, BlockPos blockPos, BlockState blockState, Abyss.Entry entry) {
        mob.swing(InteractionHand.MAIN_HAND);
        Block block = blockState.getBlock();
        if (block instanceof CropBlock) {
            Item[] itemArray = (Item[])block;
            if (itemArray.isMaxAge(blockState)) {
                block = itemArray.asItem();
                Workers.harvest(serverLevel, mob, blockPos, blockState, list, n, entry);
                if (Workers.take(list, (Item)block, 1)) {
                    serverLevel.setBlock(blockPos, itemArray.defaultBlockState(), 3);
                }
            } else {
                block = null;
                for (ItemStack itemStack : list) {
                    if (!itemStack.is(Items.BONE_MEAL)) continue;
                    block = itemStack;
                    break;
                }
                if (block != null && BoneMealItem.growCrop((ItemStack)block, (Level)serverLevel, (BlockPos)blockPos)) {
                    serverLevel.levelEvent(1505, blockPos, 15);
                    list.removeIf(ItemStack::isEmpty);
                }
            }
        } else if (blockState.is(Blocks.FARMLAND) && (block = Workers.seed(list)) != null && Workers.take(list, (Item)block, 1)) {
            serverLevel.setBlock(blockPos.above(), ((BlockItem)block).getBlock().defaultBlockState(), 3);
            serverLevel.playSound(null, blockPos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 0.8f, 1.0f);
        }
        for (Item item : new Item[]{Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS}) {
            while (Workers.countIn(list, item) > 32 && Workers.take(list, item, 7)) {
                Workers.addToBag(list, new ItemStack((ItemLike)Items.BONE_MEAL), n + 1);
            }
        }
    }

    private static boolean clearSight(ServerLevel serverLevel, Mob mob, BlockPos blockPos) {
        BlockPos blockPos2;
        Vec3 vec3 = mob.position().add(0.0, 1.5, 0.0);
        Vec3 vec32 = Vec3.atCenterOf((Vec3i)blockPos);
        Vec3 vec33 = vec32.subtract(vec3);
        double d = vec33.length();
        for (double d2 = 0.1; d2 < d && !(blockPos2 = BlockPos.containing((Position)vec3.add(vec33.scale(d2 / d)))).equals(blockPos); d2 += 0.1) {
            BlockState blockState;
            if (blockPos2.equals(mob.blockPosition()) || blockPos2.equals(mob.blockPosition().above()) || (blockState = serverLevel.getBlockState(blockPos2)).isAir() || !blockState.getFluidState().isEmpty() || blockState.canBeReplaced()) continue;
            return false;
        }
        return true;
    }

    private static void planTunnel(ServerLevel serverLevel, Mob mob, BlockPos blockPos) {
        Object object;
        BlockPos blockPos2 = mob.blockPosition();
        int n = blockPos.getX() - blockPos2.getX();
        int n2 = blockPos.getY() - blockPos2.getY();
        int n3 = blockPos.getZ() - blockPos2.getZ();
        ArrayList<Object> arrayList = new ArrayList<Object>();
        if (n == 0 && n3 == 0 && n2 <= 0) {
            arrayList.add(blockPos2.below());
        } else {
            Object object2 = n == 0 && n3 == 0 ? blockPos2.relative(mob.getDirection()) : (object = Math.abs(n) >= Math.abs(n3) ? blockPos2.offset(Integer.signum(n), 0, 0) : blockPos2.offset(0, 0, Integer.signum(n3)));
            if (n2 < -1) {
                arrayList.add(object.above());
                arrayList.add(object);
                arrayList.add(object.below());
            } else if (n2 > 1) {
                arrayList.add(blockPos2.above(2));
                arrayList.add(object.above(2));
                arrayList.add(object.above());
            } else {
                arrayList.add(object.above());
                arrayList.add(object);
            }
        }
        for (BlockPos blockPos3 : arrayList) {
            BlockState blockState = serverLevel.getBlockState(blockPos3);
            if (blockState.isAir() || !blockState.getFluidState().isEmpty() || blockState.canBeReplaced()) continue;
            if (!Workers.diggable(serverLevel, blockPos3, blockState)) {
                mob.getPersistentData().putBoolean("coaldust_blocked", true);
                return;
            }
            mob.getPersistentData().remove("coaldust_blocked");
            if (Workers.jobDigContains(mob, blockPos3)) continue;
            Workers.job((Mob)mob).dig.add(blockPos3.immutable());
        }
        Workers.job((Mob)mob).digFrom = blockPos2.immutable();
        if (Workers.job((Mob)mob).dig.isEmpty()) {
            object = arrayList.isEmpty() ? blockPos : (BlockPos)arrayList.get(arrayList.size() - 1);
            mob.getNavigation().moveTo((double)object.getX() + 0.5, (double)Math.min(object.getY(), blockPos2.getY() + 1), (double)object.getZ() + 0.5, 1.0);
        }
    }

    private static boolean reachable(Mob mob, BlockPos blockPos) {
        Path path = mob.getNavigation().createPath(blockPos, 0);
        return path != null && path.canReach();
    }

    private static BlockPos standSpot(ServerLevel serverLevel, BlockPos blockPos) {
        BlockPos[] blockPosArray;
        for (BlockPos blockPos2 : blockPosArray = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.above()}) {
            for (int i = 0; i >= -1; --i) {
                BlockPos blockPos3 = blockPos2.offset(0, i, 0);
                if (!serverLevel.getBlockState(blockPos3).isAir() || !serverLevel.getBlockState(blockPos3.above()).isAir() || serverLevel.getBlockState(blockPos3.below()).isAir()) continue;
                return blockPos3;
            }
        }
        return null;
    }

    private static boolean jobDigContains(Mob mob, BlockPos blockPos) {
        for (BlockPos blockPos2 : Workers.job((Mob)mob).dig) {
            if (!blockPos2.equals(blockPos)) continue;
            return true;
        }
        return false;
    }

    private static void digStep(ServerLevel serverLevel, Mob mob, Job job, List<ItemStack> list, int n, Abyss.Entry entry) {
        boolean bl;
        BlockPos blockPos = job.dig.peek();
        BlockState blockState = serverLevel.getBlockState(blockPos);
        if (blockState.isAir() || !blockState.getFluidState().isEmpty() || blockState.canBeReplaced() || !Workers.diggable(serverLevel, blockPos, blockState)) {
            job.dig.poll();
            job.digWork = 0;
            if (job.dig.isEmpty()) {
                Workers.stepInto(mob, blockPos);
            }
            return;
        }
        boolean bl2 = bl = job.digFrom != null && job.digFrom.distSqr((Vec3i)mob.blockPosition()) > 1.0;
        if (bl || mob.position().add(0.0, 1.0, 0.0).distanceToSqr(Vec3.atCenterOf((Vec3i)blockPos)) > 6.760000000000001) {
            if (job.digWork > 0) {
                serverLevel.destroyBlockProgress(mob.getId(), blockPos, -1);
            }
            job.dig.clear();
            job.digWork = 0;
            return;
        }
        mob.getNavigation().stop();
        mob.getLookControl().setLookAt((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
        float f = Math.max(0.3f, blockState.getDestroySpeed((BlockGetter)serverLevel, blockPos));
        int n2 = (int)Math.max(4.0f, Math.min(80.0f, f * 18.0f / Workers.speed(entry)));
        job.digWork += 5;
        mob.swing(InteractionHand.MAIN_HAND);
        serverLevel.destroyBlockProgress(mob.getId(), blockPos, Math.min(9, job.digWork * 10 / n2));
        if (job.digWork % 10 == 0) {
            serverLevel.playSound(null, blockPos, blockState.getSoundType().getHitSound(), SoundSource.BLOCKS, 0.5f, 0.8f);
        }
        if (job.digWork < n2) {
            return;
        }
        serverLevel.destroyBlockProgress(mob.getId(), blockPos, -1);
        Workers.harvest(serverLevel, mob, blockPos, blockState, list, n, entry);
        job.dig.poll();
        job.digWork = 0;
        if (job.dig.isEmpty()) {
            Workers.stepInto(mob, blockPos);
        }
    }

    private static void stepInto(Mob mob, BlockPos blockPos) {
        mob.getNavigation().moveTo((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, 1.0);
    }

    private static boolean deliver(ServerPlayer serverPlayer, Abyss.Entry entry, ServerLevel serverLevel, Mob mob, List<ItemStack> list, int n, Job job) {
        List<ChestCfg> list2;
        if (job.task != Task.FOLLOW) {
            job.cantDeliver = false;
        }
        if ((list2 = Workers.chestsFor(entry, serverLevel, n)).isEmpty() || list.isEmpty()) {
            job.trip = 0;
            if (!job.warned && !list.isEmpty()) {
                Workers.warn(serverPlayer, "ideology.coaldust.work.no_chest", new Object[0]);
                job.warned = true;
            }
            job.cantDeliver = true;
            job.cantSince = NOW;
            return false;
        }
        ChestCfg chestCfg = null;
        double d = Double.MAX_VALUE;
        for (ChestCfg chestCfg2 : list2) {
            double d2;
            boolean bl = false;
            for (ItemStack itemStack : list) {
                if (Workers.keep(job, itemStack) || !Workers.accepts(chestCfg2, list2, itemStack)) continue;
                bl = true;
                break;
            }
            if (!bl || !((d2 = mob.distanceToSqr((double)chestCfg2.pos.getX() + 0.5, (double)chestCfg2.pos.getY() + 0.5, (double)chestCfg2.pos.getZ() + 0.5)) < d)) continue;
            d = d2;
            chestCfg = chestCfg2;
        }
        if (chestCfg == null) {
            job.trip = 0;
            if (!job.warned) {
                Workers.warn(serverPlayer, "ideology.coaldust.work.no_room", new Object[0]);
                job.warned = true;
            }
            job.cantDeliver = true;
            job.cantSince = NOW;
            return false;
        }
        if (d > 7.839999999999999) {
            if (job.task != Task.FOLLOW && job.task != Task.IDLE) {
                job.trip = 2;
            }
            Workers.moveOrDig(serverLevel, mob, job, chestCfg.pos, 1.1);
            if (d < job.tripBest - 0.5) {
                job.tripBest = d;
                job.stuck = 0;
            } else if (++job.stuck > 240) {
                job.cantDeliver = true;
                job.cantSince = NOW;
                job.stuck = 0;
                job.trip = 0;
                job.tripBest = Double.MAX_VALUE;
                Workers.warn(serverPlayer, "ideology.coaldust.work.cant_reach", new Object[0]);
            }
            return false;
        }
        job.trip = 0;
        job.stuck = 0;
        job.tripBest = Double.MAX_VALUE;
        Container container = Workers.container(serverLevel, chestCfg.pos);
        if (container == null) {
            return false;
        }
        boolean bl = false;
        for (ItemStack itemStack : list) {
            if (!Workers.accepts(chestCfg, list2, itemStack) || Workers.keep(job, itemStack)) continue;
            int n2 = itemStack.getCount();
            ItemStack itemStack2 = Workers.insert(container, itemStack.copy());
            itemStack.setCount(itemStack2.getCount());
            if (itemStack.getCount() == n2) continue;
            bl = true;
        }
        list.removeIf(ItemStack::isEmpty);
        Workers.compact(container);
        if (bl) {
            job.warned = false;
            serverLevel.playSound(null, chestCfg.pos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.4f, 1.2f);
        } else {
            if (!job.warned) {
                Workers.warn(serverPlayer, "ideology.coaldust.work.no_room", new Object[0]);
                job.warned = true;
            }
            job.cantDeliver = true;
            job.cantSince = NOW;
        }
        return bl;
    }

    private static boolean accepts(ChestCfg chestCfg, List<ChestCfg> list, ItemStack itemStack) {
        String string = Workers.id(itemStack.getItem());
        if (!chestCfg.filter.isEmpty()) {
            return chestCfg.filter.contains(string);
        }
        for (ChestCfg chestCfg2 : list) {
            if (!chestCfg2.filter.contains(string)) continue;
            return false;
        }
        return true;
    }

    private static void fetch(ServerPlayer serverPlayer, Abyss.Entry entry, ServerLevel serverLevel, Mob mob, List<ItemStack> list, int n, Job job) {
        MutableComponent mutableComponent;
        int n2;
        Container container;
        if (job.needs.isEmpty()) {
            job.trip = 0;
            return;
        }
        List<ChestCfg> list2 = Workers.chestsFor(entry, serverLevel, n);
        ChestCfg chestCfg = null;
        Item item = null;
        double d = Double.MAX_VALUE;
        for (Item item2 : job.needs) {
            for (ChestCfg chestCfg2 : list2) {
                double d2;
                container = Workers.container(serverLevel, chestCfg2.pos);
                if (container == null) continue;
                boolean bl = false;
                for (n2 = 0; n2 < container.getContainerSize(); ++n2) {
                    if (!container.getItem(n2).is(item2)) continue;
                    bl = true;
                    break;
                }
                if (!bl || !((d2 = mob.distanceToSqr((double)chestCfg2.pos.getX() + 0.5, (double)chestCfg2.pos.getY() + 0.5, (double)chestCfg2.pos.getZ() + 0.5)) < d)) continue;
                d = d2;
                chestCfg = chestCfg2;
            }
            if (chestCfg == null) continue;
            item = item2;
            break;
        }
        if (chestCfg == null) {
            job.trip = 0;
            if (NOW - job.lastNeedWarn > 600L) {
                job.lastNeedWarn = NOW;
                mutableComponent = Component.empty();
                for (int i = 0; i < Math.min(3, job.needs.size()); ++i) {
                    if (i > 0) {
                        mutableComponent.append(", ");
                    }
                    mutableComponent.append(new ItemStack((ItemLike)job.needs.get(i)).getHoverName());
                }
                if (job.needs.size() > 3) {
                    mutableComponent.append(" \u2026");
                }
                Workers.warn(serverPlayer, "ideology.coaldust.work.no_material", mutableComponent);
            }
            return;
        }
        if (d > 7.839999999999999) {
            if (job.trip != 1) {
                job.stuck = 0;
            }
            job.trip = 1;
            Workers.moveOrDig(serverLevel, mob, job, chestCfg.pos, 1.1);
            if (d < job.tripBest - 0.5) {
                job.tripBest = d;
                job.stuck = 0;
            } else if (++job.stuck > 240) {
                job.trip = 0;
                job.stuck = 0;
                job.tripBest = Double.MAX_VALUE;
                Workers.warn(serverPlayer, "ideology.coaldust.work.cant_reach", new Object[0]);
            }
            return;
        }
        job.trip = 0;
        job.stuck = 0;
        job.tripBest = Double.MAX_VALUE;
        mutableComponent = Workers.container(serverLevel, chestCfg.pos);
        int n3 = Workers.bagSize(entry);
        int n4 = (Workers.needOf(serverLevel, job, item) + Workers.sameJob(serverPlayer, job) - 1) / Workers.sameJob(serverPlayer, job) - Workers.countIn(list, item);
        for (int i = 0; i < mutableComponent.getContainerSize() && n4 > 0; ++i) {
            container = mutableComponent.getItem(i);
            if (!container.is(item)) continue;
            ItemStack itemStack = container.copy();
            itemStack.setCount(Math.min(n4, container.getCount()));
            n2 = itemStack.getCount();
            ItemStack itemStack2 = Workers.addToBag(list, itemStack, n3);
            int n5 = n2 - itemStack2.getCount();
            container.shrink(n5);
            n4 -= n5;
            if (container.isEmpty()) {
                mutableComponent.setItem(i, ItemStack.EMPTY);
            }
            if (!itemStack2.isEmpty()) break;
        }
        mutableComponent.setChanged();
        job.warned = false;
        job.rescan = 0;
        serverLevel.playSound(null, chestCfg.pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.4f, 1.2f);
    }

    static void warn(ServerPlayer serverPlayer, String string, Object ... objectArray) {
        serverPlayer.displayClientMessage((Component)Component.translatable((String)string, (Object[])objectArray).withStyle(ChatFormatting.GOLD), true);
    }

    static void forget(Mob mob) {
        Workers.release(JOBS.remove(mob.getUUID()));
    }

    static void onDeath(Mob mob) {
        Level level = mob.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        for (ItemStack itemStack : Workers.bag(mob)) {
            Block.popResource((Level)serverLevel, (BlockPos)mob.blockPosition(), (ItemStack)itemStack);
        }
        mob.getPersistentData().remove(K_BAG);
        Workers.release(JOBS.remove(mob.getUUID()));
    }

    static void writeSync(ServerPlayer serverPlayer, Abyss.Entry entry, CompoundTag compoundTag) {
        Owner owner = Workers.owner(serverPlayer);
        compoundTag.putInt("wcount", Workers.count(entry));
        StringBuilder stringBuilder = new StringBuilder();
        for (Mob object2 : Workers.workers(serverPlayer)) {
            int stringBuilder2 = object2.getPersistentData().getInt("coaldust_slot") - 300;
            stringBuilder.append(stringBuilder2).append(':').append(Workers.job((Mob)object2).task.name().toLowerCase()).append(':').append(Workers.bag(object2).size()).append(';');
        }
        compoundTag.putString("wtasks", stringBuilder.toString());
        CompoundTag compoundTag2 = new CompoundTag();
        for (Mob mob : Workers.workers(serverPlayer)) {
            StringBuilder stringBuilder2 = new StringBuilder();
            for (ItemStack itemStack : Workers.bag(mob)) {
                stringBuilder2.append(Workers.id(itemStack.getItem())).append('*').append(itemStack.getCount()).append(',');
            }
            compoundTag2.putString(String.valueOf(mob.getPersistentData().getInt("coaldust_slot") - 300), stringBuilder2.toString());
        }
        compoundTag.put("wbags", (Tag)compoundTag2);
        CompoundTag compoundTag3 = new CompoundTag();
        for (Map.Entry<Integer, String> entry2 : entry.names.entrySet()) {
            compoundTag3.putString(String.valueOf(entry2.getKey()), entry2.getValue());
        }
        compoundTag.put("wnames", (Tag)compoundTag3);
        StringBuilder stringBuilder3 = new StringBuilder();
        for (int n : owner.selected) {
            stringBuilder3.append(n).append(',');
        }
        compoundTag.putString("wsel", stringBuilder3.toString());
        if (owner.p1 != null) {
            compoundTag.putString("wp1", owner.p1.getX() + " " + owner.p1.getY() + " " + owner.p1.getZ());
        }
        if (owner.p2 != null) {
            compoundTag.putString("wp2", owner.p2.getX() + " " + owner.p2.getY() + " " + owner.p2.getZ());
        }
        if (owner.template != null) {
            compoundTag.putInt("wtpl", owner.template.size());
        }
        compoundTag.putInt("wbag", Workers.bagSize(entry));
        ListTag listTag = new ListTag();
        for (ChestCfg chestCfg : entry.chests) {
            if (!chestCfg.dim.equals(Workers.dim((ServerLevel)serverPlayer.level()))) continue;
            listTag.add((Object)chestCfg.save());
        }
        compoundTag.put("wchests", (Tag)listTag);
    }

    private Workers() {
    }

    static {
        MODES = new String[]{"random", "checker", "layers", "stripes", "border"};
    }

    static final class Owner {
        BlockPos p1;
        BlockPos p2;
        final Set<Integer> selected = new HashSet<Integer>();
        Map<BlockPos, BlockState> template;
        BlockPos tplSize;
        BlockPos tplA;
        BlockPos tplB;
        long showUntil;

        Owner() {
        }
    }

    static final class Job {
        Task task = Task.FOLLOW;
        BlockPos a;
        BlockPos b;
        BlockPos origin;
        Map<BlockPos, BlockState> template;
        List<Item> fills = new ArrayList<Item>();
        String mode = "random";
        final Map<BlockPos, Long> skip = new HashMap<BlockPos, Long>();
        BlockPos cur;
        int work;
        int stuck;
        int rescan;
        double best = Double.MAX_VALUE;
        String need;
        final List<Item> needs = new ArrayList<Item>();
        long lastNeedWarn = -100000L;
        double tripBest = Double.MAX_VALUE;
        long cantSince;
        int trip;
        final ArrayDeque<BlockPos> dig = new ArrayDeque();
        int digWork;
        BlockPos digFrom;
        BlockPos navTarget;
        boolean breed = true;
        boolean shear = true;
        boolean milk = true;
        boolean cull = false;
        int limit = 10;
        Item craftOut;
        int craftLeft;
        List<BlockPos> furnaces = new ArrayList<BlockPos>();
        int furnaceScan;
        double navBest = Double.MAX_VALUE;
        int navStuck;
        Vec3 navLast;
        boolean warned;
        boolean cantDeliver;

        Job() {
        }
    }

    public static enum Task {
        FOLLOW,
        WOOD,
        ORE,
        DIG,
        FILL,
        COPY,
        FARM,
        COLLECT,
        IDLE,
        PASTURE,
        SMELT,
        CRAFT;

    }

    static final class ChestCfg {
        String dim;
        BlockPos pos;
        boolean pub;
        final List<String> filter = new ArrayList<String>();
        final Set<Integer> bound = new HashSet<Integer>();

        ChestCfg() {
        }

        CompoundTag save() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("dim", this.dim);
            compoundTag.putIntArray("pos", new int[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()});
            compoundTag.putBoolean("pub", this.pub);
            compoundTag.putString("filter", String.join((CharSequence)",", this.filter));
            StringBuilder stringBuilder = new StringBuilder();
            for (int n : this.bound) {
                stringBuilder.append(n).append(',');
            }
            compoundTag.putString("bound", stringBuilder.toString());
            return compoundTag;
        }

        static ChestCfg load(CompoundTag compoundTag) {
            ChestCfg chestCfg = new ChestCfg();
            chestCfg.dim = compoundTag.getString("dim");
            int[] nArray = compoundTag.getIntArray("pos");
            if (nArray.length != 3) {
                return null;
            }
            chestCfg.pos = new BlockPos(nArray[0], nArray[1], nArray[2]);
            chestCfg.pub = compoundTag.getBoolean("pub");
            for (String string : compoundTag.getString("filter").split(",")) {
                if (string.isEmpty()) continue;
                chestCfg.filter.add(string);
            }
            for (String string : compoundTag.getString("bound").split(",")) {
                if (string.isEmpty()) continue;
                try {
                    chestCfg.bound.add(Integer.parseInt(string));
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            return chestCfg;
        }
    }
}
