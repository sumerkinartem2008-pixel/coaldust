/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.NeutralMob
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.AvoidEntityGoal
 *  net.minecraft.world.entity.ai.goal.FleeSunGoal
 *  net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal
 *  net.minecraft.world.entity.ai.goal.RandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.RemoveBlockGoal
 *  net.minecraft.world.entity.ai.goal.RestrictSunGoal
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.DyedItemColor
 *  net.minecraft.world.item.component.Unbreakable
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
 *  net.neoforged.neoforge.event.entity.living.LivingConversionEvent$Pre
 *  net.neoforged.neoforge.event.entity.living.LivingDeathEvent
 *  net.neoforged.neoforge.event.entity.living.LivingDropsEvent
 *  net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent
 *  net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 */
package com.coaldust.ideology;

import com.coaldust.CoalDust;
import com.coaldust.ideology.Abyss;
import com.coaldust.ideology.IdeologyManager;
import com.coaldust.ideology.Trait;
import com.coaldust.ideology.Workers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class Summons {
    static final String K_OWNER = "coaldust_owner";
    static final String K_ROLE = "coaldust_role";
    static final String K_SLOT = "coaldust_slot";
    static final String K_EXPIRE = "coaldust_expire";
    static final String K_HEAL = "coaldust_heal_cd";
    static final String SQUAD = "squad";
    static final String ARMY = "army";
    static final int SKELETON_SLOT = 100;
    static final int HEALER_SLOT = 200;
    private static final ResourceLocation HP_ID = CoalDust.id("summon_health");
    private static final ResourceLocation DMG_ID = CoalDust.id("summon_damage");
    private static final ResourceLocation ARMOR_ID = CoalDust.id("summon_armor");
    private static final Map<UUID, List<Mob>> OWNED = new HashMap<UUID, List<Mob>>();
    private static final Map<UUID, Set<Integer>> AWAY = new HashMap<UUID, Set<Integer>>();

    static void comeBack(UUID uUID, int n) {
        Set<Integer> set = AWAY.get(uUID);
        if (set != null) {
            set.remove(n);
        }
    }

    public static boolean isOwned(Entity entity) {
        return entity != null && entity.getPersistentData().contains(K_OWNER);
    }

    static UUID owner(Entity entity) {
        try {
            return UUID.fromString(entity.getPersistentData().getString(K_OWNER));
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static ServerPlayer ownerOf(Entity entity) {
        if (!Summons.isOwned(entity) || entity.getServer() == null) {
            return null;
        }
        UUID uUID = Summons.owner(entity);
        return uUID == null ? null : entity.getServer().getPlayerList().getPlayer(uUID);
    }

    private static boolean sameSide(Entity entity, Entity entity2) {
        UUID uUID;
        UUID uUID2 = entity instanceof Player ? entity.getUUID() : (uUID = Summons.isOwned(entity) ? Summons.owner(entity) : null);
        UUID uUID3 = entity2 instanceof Player ? entity2.getUUID() : (Summons.isOwned(entity2) ? Summons.owner(entity2) : null);
        return uUID != null && uUID.equals(uUID3);
    }

    static List<Mob> list(UUID uUID2) {
        return OWNED.computeIfAbsent(uUID2, uUID -> new ArrayList());
    }

    static boolean isHealer(Entity entity) {
        int n = Summons.slot(entity);
        return n >= 200 && n < 300;
    }

    private static String role(Entity entity) {
        return entity.getPersistentData().getString(K_ROLE);
    }

    private static int slot(Entity entity) {
        return entity.getPersistentData().getInt(K_SLOT);
    }

    static int squadZombies(Abyss.Entry entry) {
        return 1 + entry.istat("s_zombies");
    }

    static int squadSkeletons(Abyss.Entry entry) {
        return entry.istat("s_skeletons");
    }

    static int squadLife(Abyss.Entry entry) {
        return (int)(20.0 * (60.0 + entry.stat("s_life")));
    }

    static long squadCooldown(Abyss.Entry entry) {
        return (long)(1200.0 * Math.max(5.0, 20.0 - entry.stat("s_cool")));
    }

    static int armyZombies(Abyss.Entry entry) {
        return 1 + entry.istat("a_zombies");
    }

    static int armySkeletons(Abyss.Entry entry) {
        return entry.istat("a_skeletons");
    }

    static int armyHealers(Abyss.Entry entry) {
        return entry.istat("a_healers");
    }

    static long respawnTicks(Abyss.Entry entry) {
        return (long)(1200.0 * Math.max(1.0, 5.0 - entry.stat("a_respawn")));
    }

    private static boolean summoner(ServerPlayer serverPlayer) {
        return IdeologyManager.has(serverPlayer, Trait.SUMMONER);
    }

    public static void summonSquad(ServerPlayer serverPlayer) {
        int n;
        if (!Summons.summoner(serverPlayer)) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.abyss.need_summoner").withStyle(ChatFormatting.RED), true);
            return;
        }
        Abyss.Entry entry = Abyss.entry(serverPlayer);
        long l = serverPlayer.level().getGameTime();
        long l2 = entry.lastSquad + Summons.squadCooldown(entry) - l;
        if (l2 > 0L) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.summon.cooldown", (Object[])new Object[]{(l2 + 1199L) / 1200L}).withStyle(ChatFormatting.DARK_PURPLE), true);
            return;
        }
        entry.lastSquad = l;
        Abyss.data(serverPlayer.getServer()).setDirty();
        long l3 = l + (long)Summons.squadLife(entry);
        for (n = 0; n < Summons.squadZombies(entry); ++n) {
            Summons.spawn(serverPlayer, EntityType.ZOMBIE, SQUAD, n, l3, entry);
        }
        for (n = 0; n < Summons.squadSkeletons(entry); ++n) {
            Summons.spawn(serverPlayer, EntityType.SKELETON, SQUAD, 100 + n, l3, entry);
        }
        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 1.0f, 0.7f);
        serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.summon.squad", (Object[])new Object[]{Summons.squadLife(entry) / 20}).withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    private static Mob spawn(ServerPlayer serverPlayer, EntityType<?> entityType, String string, int n, long l, Abyss.Entry entry) {
        CompoundTag compoundTag;
        ServerLevel serverLevel = serverPlayer.serverLevel();
        Entity entity = entityType.create((Level)serverLevel);
        if (!(entity instanceof Mob)) {
            return null;
        }
        Mob mob = (Mob)entity;
        Vec3 vec3 = Summons.near(serverPlayer, 2.5);
        mob.moveTo(vec3.x, vec3.y, vec3.z, serverPlayer.getRandom().nextFloat() * 360.0f, 0.0f);
        CompoundTag compoundTag2 = mob.getPersistentData();
        compoundTag2.putString(K_OWNER, serverPlayer.getUUID().toString());
        compoundTag2.putString(K_ROLE, string);
        compoundTag2.putInt(K_SLOT, n);
        if (l > 0L) {
            compoundTag2.putLong(K_EXPIRE, l);
        }
        if ((compoundTag = entry.parked.remove(n)) != null && n >= 300) {
            for (String string2 : compoundTag.getAllKeys()) {
                compoundTag2.put(string2, compoundTag.get(string2));
            }
        }
        if (n >= 100 && n < 200) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.BOW));
        }
        Summons.stats(mob, string, entry);
        mob.setHealth(mob.getMaxHealth());
        Summons.configure(mob);
        serverLevel.addFreshEntity((Entity)mob);
        serverLevel.sendParticles((ParticleOptions)ParticleTypes.SOUL, mob.getX(), mob.getY() + 1.0, mob.getZ(), 16, 0.3, 0.6, 0.3, 0.03);
        serverLevel.sendParticles((ParticleOptions)ParticleTypes.REVERSE_PORTAL, mob.getX(), mob.getY() + 0.5, mob.getZ(), 24, 0.4, 0.4, 0.4, 0.05);
        return mob;
    }

    private static Vec3 near(ServerPlayer serverPlayer, double d) {
        for (int i = 0; i < 8; ++i) {
            double d2 = serverPlayer.getRandom().nextDouble() * Math.PI * 2.0;
            double d3 = 1.2 + serverPlayer.getRandom().nextDouble() * d;
            double d4 = serverPlayer.getX() + Math.cos(d2) * d3;
            double d5 = serverPlayer.getZ() + Math.sin(d2) * d3;
            BlockPos blockPos = BlockPos.containing((double)d4, (double)serverPlayer.getY(), (double)d5);
            if (!serverPlayer.level().getBlockState(blockPos).isAir() || !serverPlayer.level().getBlockState(blockPos.above()).isAir() || serverPlayer.level().getBlockState(blockPos.below()).isAir()) continue;
            return new Vec3(d4, serverPlayer.getY(), d5);
        }
        return serverPlayer.position();
    }

    private static void mod(Mob mob, Holder<Attribute> holder, ResourceLocation resourceLocation, double d, AttributeModifier.Operation operation) {
        AttributeInstance attributeInstance = mob.getAttribute(holder);
        if (attributeInstance == null) {
            return;
        }
        if (attributeInstance.getModifier(resourceLocation) != null) {
            attributeInstance.removeModifier(resourceLocation);
        }
        if (d != 0.0) {
            attributeInstance.addPermanentModifier(new AttributeModifier(resourceLocation, d, operation));
        }
    }

    static ItemStack hood(boolean bl) {
        ItemStack itemStack = new ItemStack((ItemLike)(bl ? Items.IRON_HELMET : Items.LEATHER_HELMET));
        itemStack.set(DataComponents.UNBREAKABLE, (Object)new Unbreakable(false));
        if (!bl) {
            itemStack.set(DataComponents.DYED_COLOR, (Object)new DyedItemColor(4923251, false));
        }
        return itemStack;
    }

    private static void stats(Mob mob, String string, Abyss.Entry entry) {
        boolean bl;
        double d;
        double d2;
        double d3;
        boolean bl2 = Summons.isHealer((Entity)mob);
        if (!bl2 || Summons.slot((Entity)mob) >= 300) {
            mob.setItemSlot(EquipmentSlot.HEAD, Summons.hood(false));
        }
        if (Summons.slot((Entity)mob) >= 300) {
            Summons.mod(mob, (Holder<Attribute>)Attributes.MOVEMENT_SPEED, ARMOR_ID, 0.25 + entry.stat("w_stride"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                mob.setDropChance(equipmentSlot, 0.0f);
            }
            return;
        }
        if (SQUAD.equals(string)) {
            d3 = entry.stat("s_hp");
            d2 = entry.stat("s_dmg");
            d = entry.stat("s_armor");
            bl = d >= 5.0;
        } else {
            d3 = entry.stat("a_hp");
            d2 = entry.stat("a_dmg");
            d = entry.stat("a_armor");
            bl = d >= 5.0;
        }
        Summons.mod(mob, (Holder<Attribute>)Attributes.MAX_HEALTH, HP_ID, d3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        if (!bl2) {
            Summons.mod(mob, (Holder<Attribute>)Attributes.ATTACK_DAMAGE, DMG_ID, d2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
        Summons.mod(mob, (Holder<Attribute>)Attributes.ARMOR, ARMOR_ID, d, AttributeModifier.Operation.ADD_VALUE);
        if (bl && !bl2) {
            mob.setItemSlot(EquipmentSlot.HEAD, Summons.hood(true));
            mob.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)Items.IRON_CHESTPLATE));
        }
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            mob.setDropChance(equipmentSlot, 0.0f);
        }
    }

    static void configure(Mob mob) {
        Object object;
        mob.targetSelector.removeAllGoals(goal -> true);
        mob.goalSelector.removeAllGoals(goal -> goal instanceof RandomStrollGoal || goal instanceof MoveThroughVillageGoal || goal instanceof RemoveBlockGoal || goal instanceof FleeSunGoal || goal instanceof RestrictSunGoal || goal instanceof AvoidEntityGoal);
        mob.setPersistenceRequired();
        mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, true, false));
        if (Summons.slot((Entity)mob) >= 300) {
            object = Summons.ownerOf((Entity)mob);
            String string = object == null ? null : Abyss.entry((ServerPlayer)object).names.get(Summons.slot((Entity)mob) - 300);
            mob.setCustomName((Component)(string != null ? Component.literal(string) : Component.translatable((String)"ideology.coaldust.summon.name_worker", (Object[])new Object[]{Summons.slot((Entity)mob) - 300 + 1})).withStyle(ChatFormatting.DARK_PURPLE));
            Workers.tool(mob, Workers.job((Mob)mob).task);
        } else {
            mob.setCustomName((Component)Component.translatable((String)(Summons.slot((Entity)mob) >= 200 ? "ideology.coaldust.summon.name_healer" : (SQUAD.equals(Summons.role((Entity)mob)) ? "ideology.coaldust.summon.name_squad" : "ideology.coaldust.summon.name_army"))).withStyle(ChatFormatting.DARK_PURPLE));
        }
        object = Summons.owner((Entity)mob);
        if (object != null && !Summons.list((UUID)object).contains(mob)) {
            Summons.list((UUID)object).add(mob);
        }
    }

    public static void tick(ServerPlayer serverPlayer) {
        if (serverPlayer.tickCount % 5 == 1 && serverPlayer.isAlive() && Summons.summoner(serverPlayer)) {
            Workers.tick(serverPlayer, Abyss.entry(serverPlayer));
        }
        if (serverPlayer.tickCount % 10 != 3) {
            return;
        }
        List<Mob> list = Summons.list(serverPlayer.getUUID());
        for (Mob mob2 : list) {
            if (!mob2.isRemoved() || mob2.getRemovalReason() != Entity.RemovalReason.UNLOADED_TO_CHUNK || !Workers.isWorker((Entity)mob2) || Workers.job((Mob)mob2).task == Workers.Task.FOLLOW) continue;
            AWAY.computeIfAbsent(serverPlayer.getUUID(), uUID -> new HashSet()).add(Summons.slot((Entity)mob2));
        }
        list.removeIf(mob -> mob.isRemoved() || !mob.isAlive());
        if (!Summons.summoner(serverPlayer)) {
            if (!list.isEmpty()) {
                for (Mob mob2 : list) {
                    Summons.vanish(mob2);
                }
                list.clear();
            }
            return;
        }
        long l = serverPlayer.level().getGameTime();
        Abyss.Entry entry = Abyss.entry(serverPlayer);
        for (Mob mob3 : new ArrayList<Mob>(list)) {
            if (!SQUAD.equals(Summons.role((Entity)mob3)) || mob3.getPersistentData().getLong(K_EXPIRE) > l) continue;
            Summons.vanish(mob3);
            list.remove(mob3);
        }
        for (Mob mob3 : new ArrayList<Mob>(list)) {
            if (mob3.level() != serverPlayer.level()) {
                Summons.park(mob3, entry);
                mob3.discard();
                list.remove(mob3);
                continue;
            }
            double d = mob3.distanceToSqr((Entity)serverPlayer);
            if (!(d > 900.0) || !serverPlayer.onGround() || Workers.isWorker((Entity)mob3) && Workers.job((Mob)mob3).task != Workers.Task.FOLLOW) continue;
            Vec3 vec3 = Summons.near(serverPlayer, 2.5);
            mob3.teleportTo(vec3.x, vec3.y, vec3.z);
            mob3.getNavigation().stop();
        }
        if (serverPlayer.tickCount % 40 == 3) {
            Summons.maintainArmy(serverPlayer, entry, list, l);
        }
        if (!serverPlayer.isAlive()) {
            return;
        }
        Summons.assignTargets(serverPlayer, list);
        Summons.follow(serverPlayer, list);
        Summons.heal(serverPlayer, list, l);
    }

    static void park(Mob mob, Abyss.Entry entry) {
        if (!Workers.isWorker((Entity)mob)) {
            return;
        }
        CompoundTag compoundTag = mob.getPersistentData();
        CompoundTag compoundTag2 = new CompoundTag();
        for (String string : new String[]{"coaldust_task", "coaldust_box", "coaldust_fill", "coaldust_bag"}) {
            if (!compoundTag.contains(string)) continue;
            compoundTag2.put(string, compoundTag.get(string));
        }
        entry.parked.put(Summons.slot((Entity)mob), compoundTag2);
        Workers.forget(mob);
    }

    private static void vanish(Mob mob) {
        Level level = mob.level();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            serverLevel.sendParticles((ParticleOptions)ParticleTypes.LARGE_SMOKE, mob.getX(), mob.getY() + 1.0, mob.getZ(), 12, 0.3, 0.5, 0.3, 0.02);
        }
        mob.discard();
    }

    private static void maintainArmy(ServerPlayer serverPlayer, Abyss.Entry entry, List<Mob> list, long l) {
        int n;
        if (!serverPlayer.isAlive()) {
            return;
        }
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (n = 0; n < Summons.armyZombies(entry); ++n) {
            arrayList.add(n);
        }
        for (n = 0; n < Summons.armySkeletons(entry); ++n) {
            arrayList.add(100 + n);
        }
        for (n = 0; n < Summons.armyHealers(entry); ++n) {
            arrayList.add(200 + n);
        }
        for (n = 0; n < Workers.count(entry); ++n) {
            arrayList.add(300 + n);
        }
        HashMap<Integer, Mob> hashMap = new HashMap<Integer, Mob>();
        for (Mob object2 : new ArrayList<Mob>(list)) {
            if (!ARMY.equals(Summons.role((Entity)object2))) continue;
            int iterator = Summons.slot((Entity)object2);
            if (!arrayList.contains(iterator) || hashMap.containsKey(iterator)) {
                object2.discard();
                list.remove(object2);
                continue;
            }
            hashMap.put(iterator, object2);
        }
        boolean bl = false;
        Set set = AWAY.getOrDefault(serverPlayer.getUUID(), Set.of());
        Iterator<Object> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            Long l2;
            int mob = (Integer)iterator.next();
            if (hashMap.containsKey(mob) || set.contains(mob) || (l2 = entry.respawn.get(mob)) != null && l2 > l) continue;
            if (l2 != null) {
                entry.respawn.remove(mob);
                bl = true;
            }
            EntityType entityType = mob >= 300 ? EntityType.ZOMBIE_VILLAGER : (mob >= 200 ? EntityType.WITCH : (mob >= 100 ? EntityType.SKELETON : EntityType.ZOMBIE));
            Summons.spawn(serverPlayer, entityType, ARMY, mob, 0L, entry);
        }
        for (Mob mob : list) {
            if (!ARMY.equals(Summons.role((Entity)mob)) || mob.tickCount % 400 >= 40) continue;
            Summons.stats(mob, ARMY, entry);
        }
        if (bl) {
            Abyss.data(serverPlayer.getServer()).setDirty();
        }
    }

    private static List<LivingEntity> enemies(ServerPlayer serverPlayer, List<Mob> list) {
        ArrayList<LivingEntity> arrayList = new ArrayList<LivingEntity>();
        LivingEntity livingEntity = serverPlayer.getLastHurtByMob();
        LivingEntity livingEntity2 = serverPlayer.tickCount - serverPlayer.getLastHurtMobTimestamp() < 200 ? serverPlayer.getLastHurtMob() : null;
        AABB aABB = serverPlayer.getBoundingBox().inflate(18.0, 8.0, 18.0);
        for (LivingEntity livingEntity3 : serverPlayer.level().getEntitiesOfClass(LivingEntity.class, aABB)) {
            boolean bl;
            Player player;
            boolean bl2;
            if (!livingEntity3.isAlive() || livingEntity3 == serverPlayer || Summons.sameSide((Entity)livingEntity3, (Entity)serverPlayer)) continue;
            boolean bl3 = bl2 = livingEntity3 == livingEntity || livingEntity3 == livingEntity2;
            if (!bl2) {
                player = list.iterator();
                while (player.hasNext()) {
                    Mob mob = (Mob)player.next();
                    if (mob.getLastHurtByMob() != livingEntity3) continue;
                    bl2 = true;
                    break;
                }
            }
            if (livingEntity3 instanceof Player && ((player = (Player)livingEntity3).isCreative() || player.isSpectator())) continue;
            boolean bl4 = bl = livingEntity3 instanceof Enemy && !(livingEntity3 instanceof NeutralMob) && !Summons.isOwned((Entity)livingEntity3);
            if (!bl && !bl2) continue;
            arrayList.add(livingEntity3);
        }
        return arrayList;
    }

    private static void assignTargets(ServerPlayer serverPlayer, List<Mob> list) {
        LivingEntity livingEntity;
        ArrayList<Mob> arrayList = new ArrayList<Mob>();
        for (Mob object : list) {
            if (Summons.slot((Entity)object) >= 200 || object.level() != serverPlayer.level()) continue;
            arrayList.add(object);
        }
        if (arrayList.isEmpty()) {
            return;
        }
        List<LivingEntity> list2 = Summons.enemies(serverPlayer, list);
        if (list2.isEmpty()) {
            for (Mob hashMap : arrayList) {
                if (hashMap.getTarget() == null) continue;
                hashMap.setTarget(null);
            }
            return;
        }
        int n = (arrayList.size() + list2.size() - 1) / list2.size();
        HashMap<LivingEntity, Integer> hashMap = new HashMap<LivingEntity, Integer>();
        ArrayList<Mob> arrayList2 = new ArrayList<Mob>();
        for (Mob mob : arrayList) {
            livingEntity = mob.getTarget();
            if (livingEntity != null && list2.contains(livingEntity) && hashMap.getOrDefault(livingEntity, 0) < n) {
                hashMap.merge(livingEntity, 1, Integer::sum);
                continue;
            }
            arrayList2.add(mob);
        }
        for (Mob mob : arrayList2) {
            livingEntity = null;
            int n2 = Integer.MAX_VALUE;
            double d = Double.MAX_VALUE;
            for (LivingEntity livingEntity2 : list2) {
                int n3 = hashMap.getOrDefault(livingEntity2, 0);
                double d2 = mob.distanceToSqr((Entity)livingEntity2);
                if (n3 >= n2 && (n3 != n2 || !(d2 < d))) continue;
                livingEntity = livingEntity2;
                n2 = n3;
                d = d2;
            }
            mob.setTarget(livingEntity);
            if (livingEntity == null) continue;
            hashMap.merge(livingEntity, 1, Integer::sum);
        }
    }

    private static void follow(ServerPlayer serverPlayer, List<Mob> list) {
        for (Mob mob : list) {
            double d;
            if (Workers.isWorker((Entity)mob) || mob.getTarget() != null && mob.getTarget().isAlive()) continue;
            double d2 = mob.distanceToSqr((Entity)serverPlayer);
            double d3 = d = Summons.isHealer((Entity)mob) ? 3.5 : 5.5;
            if (d2 > d * d) {
                mob.getNavigation().moveTo((Entity)serverPlayer, d2 > 144.0 ? 1.3 : 1.05);
                continue;
            }
            if (!mob.getNavigation().isInProgress() || !(d2 < 4.0)) continue;
            mob.getNavigation().stop();
        }
    }

    private static void heal(ServerPlayer serverPlayer, List<Mob> list, long l) {
        for (Mob mob : list) {
            Mob mob22;
            float f;
            CompoundTag compoundTag;
            if (!Summons.isHealer((Entity)mob) || (compoundTag = mob.getPersistentData()).getLong(K_HEAL) > l) continue;
            ServerPlayer serverPlayer2 = null;
            if (serverPlayer.getHealth() < serverPlayer.getMaxHealth() - 1.0f && mob.distanceToSqr((Entity)serverPlayer) < 196.0) {
                serverPlayer2 = serverPlayer;
            } else {
                f = 0.85f;
                for (Mob mob22 : list) {
                    float f2;
                    if (mob22 == mob || mob.distanceToSqr((Entity)mob22) > 196.0 || !((f2 = mob22.getHealth() / mob22.getMaxHealth()) < f)) continue;
                    f = f2;
                    serverPlayer2 = mob22;
                }
            }
            if (serverPlayer2 == null) continue;
            compoundTag.putLong(K_HEAL, l + 50L);
            f = 1.0f + (float)Abyss.entry(serverPlayer).stat("a_heal");
            serverPlayer2.heal((serverPlayer2 == serverPlayer ? 4.0f : 6.0f) * f);
            mob.swing(InteractionHand.MAIN_HAND);
            mob.getLookControl().setLookAt((Entity)serverPlayer2, 30.0f, 30.0f);
            mob22 = serverPlayer.level();
            if (!(mob22 instanceof ServerLevel)) continue;
            ServerLevel serverLevel = (ServerLevel)mob22;
            mob22 = mob.position().add(0.0, 1.6, 0.0);
            Vec3 vec3 = serverPlayer2.position().add(0.0, (double)serverPlayer2.getBbHeight() * 0.6, 0.0);
            for (int i = 0; i <= 10; ++i) {
                Vec3 vec32 = mob22.lerp(vec3, (double)i / 10.0);
                serverLevel.sendParticles((ParticleOptions)ParticleTypes.WITCH, vec32.x, vec32.y, vec32.z, 1, 0.02, 0.02, 0.02, 0.0);
            }
            serverLevel.sendParticles((ParticleOptions)ParticleTypes.HEART, vec3.x, vec3.y + 0.4, vec3.z, 2, 0.3, 0.2, 0.3, 0.0);
            serverLevel.playSound(null, serverPlayer2.blockPosition(), SoundEvents.WITCH_DRINK, SoundSource.NEUTRAL, 0.6f, 1.4f);
        }
    }

    public static void onJoin(EntityJoinLevelEvent entityJoinLevelEvent) {
        Mob mob;
        Entity entity;
        if (entityJoinLevelEvent.getLevel().isClientSide() || !((entity = entityJoinLevelEvent.getEntity()) instanceof Mob) || !Summons.isOwned((Entity)(mob = (Mob)entity))) {
            return;
        }
        entity = Summons.ownerOf((Entity)mob);
        if (entity == null || entity.level() != entityJoinLevelEvent.getLevel()) {
            entityJoinLevelEvent.setCanceled(true);
            return;
        }
        if (SQUAD.equals(Summons.role((Entity)mob)) && mob.getPersistentData().getLong(K_EXPIRE) <= entity.level().getGameTime()) {
            entityJoinLevelEvent.setCanceled(true);
            return;
        }
        for (Mob mob2 : Summons.list(entity.getUUID())) {
            if (mob2 == mob || mob2.isRemoved() || !mob2.isAlive() || !Summons.role((Entity)mob2).equals(Summons.role((Entity)mob)) || Summons.slot((Entity)mob2) != Summons.slot((Entity)mob)) continue;
            entityJoinLevelEvent.setCanceled(true);
            return;
        }
        Summons.comeBack(entity.getUUID(), Summons.slot((Entity)mob));
        Summons.configure(mob);
    }

    public static void onDeath(LivingDeathEvent livingDeathEvent) {
        Mob mob;
        LivingEntity livingEntity = livingDeathEvent.getEntity();
        if (!Summons.isOwned((Entity)livingEntity) || !ARMY.equals(Summons.role((Entity)livingEntity))) {
            return;
        }
        if (livingEntity instanceof Mob && Workers.isWorker((Entity)(mob = (Mob)livingEntity))) {
            Workers.onDeath(mob);
        }
        if ((mob = Summons.ownerOf((Entity)livingEntity)) == null) {
            return;
        }
        Abyss.Entry entry = Abyss.entry((ServerPlayer)mob);
        long l = mob.level().getGameTime() + Summons.respawnTicks(entry);
        entry.respawn.put(Summons.slot((Entity)livingEntity), l);
        Abyss.data(mob.getServer()).setDirty();
        mob.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.summon.fallen", (Object[])new Object[]{(double)Summons.respawnTicks(entry) / 1200.0 >= 1.0 ? String.format("%.1f", (double)Summons.respawnTicks(entry) / 1200.0) : "1"}).withStyle(ChatFormatting.DARK_PURPLE), true);
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent livingIncomingDamageEvent) {
        Entity entity = livingIncomingDamageEvent.getSource().getEntity();
        if (entity == null || entity == livingIncomingDamageEvent.getEntity()) {
            return;
        }
        if ((Summons.isOwned((Entity)livingIncomingDamageEvent.getEntity()) || Summons.isOwned(entity)) && Summons.sameSide(entity, (Entity)livingIncomingDamageEvent.getEntity())) {
            livingIncomingDamageEvent.setCanceled(true);
        }
    }

    public static void onDrops(LivingDropsEvent livingDropsEvent) {
        if (Summons.isOwned((Entity)livingDropsEvent.getEntity())) {
            livingDropsEvent.setCanceled(true);
        }
    }

    public static void onXp(LivingExperienceDropEvent livingExperienceDropEvent) {
        if (Summons.isOwned((Entity)livingExperienceDropEvent.getEntity())) {
            livingExperienceDropEvent.setCanceled(true);
        }
    }

    public static void onConvert(LivingConversionEvent.Pre pre) {
        if (Summons.isOwned((Entity)pre.getEntity())) {
            pre.setCanceled(true);
        }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent playerLoggedOutEvent) {
        Player player;
        Object object;
        Abyss.Entry entry;
        AWAY.remove(playerLoggedOutEvent.getEntity().getUUID());
        List<Mob> list = OWNED.remove(playerLoggedOutEvent.getEntity().getUUID());
        Player player2 = playerLoggedOutEvent.getEntity();
        Abyss.Entry entry2 = entry = player2 instanceof ServerPlayer && (object = (ServerPlayer)player2).getServer() != null ? Abyss.entry((ServerPlayer)object) : null;
        if (list != null) {
            for (Mob mob : list) {
                if (mob.isRemoved()) continue;
                if (entry != null) {
                    Summons.park(mob, entry);
                }
                mob.discard();
            }
        }
        if (entry != null && (player = playerLoggedOutEvent.getEntity()) instanceof ServerPlayer) {
            object = (ServerPlayer)player;
            Abyss.data(object.getServer()).setDirty();
        }
    }

    static void writeSync(ServerPlayer serverPlayer, Abyss.Entry entry, CompoundTag compoundTag) {
        long l = serverPlayer.level().getGameTime();
        compoundTag.putInt("scool", (int)Math.max(0L, (entry.lastSquad + Summons.squadCooldown(entry) - l) / 20L));
        compoundTag.putInt("ssquad", Summons.squadZombies(entry) * 100 + Summons.squadSkeletons(entry));
        compoundTag.putInt("slife", Summons.squadLife(entry) / 20);
        compoundTag.putInt("sarmy", Summons.armyZombies(entry) * 10000 + Summons.armySkeletons(entry) * 100 + Summons.armyHealers(entry));
        int n = 0;
        int n2 = 0;
        for (long l2 : entry.respawn.values()) {
            if (l2 <= l) continue;
            ++n;
            n2 = n2 == 0 ? (int)((l2 - l) / 20L) : Math.min(n2, (int)((l2 - l) / 20L));
        }
        compoundTag.putInt("sdead", n);
        compoundTag.putInt("snext", n2);
    }

    private Summons() {
    }
}
