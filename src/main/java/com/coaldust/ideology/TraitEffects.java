/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.EntityTypeTags
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.animal.Cow
 *  net.minecraft.world.entity.animal.Sheep
 *  net.minecraft.world.entity.boss.EnderDragonPart
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.npc.Villager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.AbstractArrow
 *  net.minecraft.world.entity.vehicle.Boat
 *  net.minecraft.world.food.FoodData
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.PotionItem
 *  net.minecraft.world.item.ProjectileWeaponItem
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.common.Tags$Blocks
 *  net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent
 *  net.neoforged.neoforge.event.entity.living.AnimalTameEvent
 *  net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent
 *  net.neoforged.neoforge.event.entity.living.LivingDamageEvent$Post
 *  net.neoforged.neoforge.event.entity.living.LivingDeathEvent
 *  net.neoforged.neoforge.event.entity.player.AnvilRepairEvent
 *  net.neoforged.neoforge.event.entity.player.BonemealEvent
 *  net.neoforged.neoforge.event.entity.player.ItemFishedEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$ItemCraftedEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$ItemSmeltedEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerXpEvent$LevelChange
 *  net.neoforged.neoforge.event.entity.player.PlayerXpEvent$PickupXp
 *  net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent
 *  net.neoforged.neoforge.event.level.BlockEvent$BreakEvent
 *  net.neoforged.neoforge.event.level.BlockEvent$EntityPlaceEvent
 */
package com.coaldust.ideology;

import com.coaldust.effect.ModEffects;
import com.coaldust.ideology.Ideology;
import com.coaldust.ideology.IdeologyItems;
import com.coaldust.ideology.IdeologyManager;
import com.coaldust.ideology.IdeologyStructure;
import com.coaldust.ideology.Quest;
import com.coaldust.ideology.Trait;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class TraitEffects {
    public static final long TICKS_20_MIN = 24000L;
    public static final long TICKS_30_MIN = 36000L;
    public static final long TICKS_10_MIN = 12000L;
    private static final String K_BLOOD = "coaldust_ideo_blood";
    private static final String K_PAIN = "coaldust_ideo_pain";
    private static final String K_HIT = "coaldust_ideo_hit";
    private static final String K_PIRATE = "coaldust_ideo_pirate";
    private static final String K_RITUAL = "coaldust_ideo_ritual";
    private static final String K_PLANTS = "coaldust_ideo_plants";
    private static final Map<UUID, double[]> LAST_POS = new HashMap<UUID, double[]>();
    private static final String K_VOMIT = "coaldust_ideo_vomit";
    private static final int VOMIT_TICKS = 60;

    public static void resetTimers(ServerPlayer serverPlayer) {
        long l = serverPlayer.level().getGameTime();
        CompoundTag compoundTag = serverPlayer.getPersistentData();
        for (String string : new String[]{K_BLOOD, K_PAIN, K_HIT, K_PIRATE, K_RITUAL}) {
            compoundTag.putLong(string, l);
        }
    }

    public static void mark(ServerPlayer serverPlayer, String string) {
        serverPlayer.getPersistentData().putLong(string, serverPlayer.level().getGameTime());
    }

    public static void markBlood(ServerPlayer serverPlayer) {
        TraitEffects.mark(serverPlayer, K_BLOOD);
    }

    public static void markRitual(ServerPlayer serverPlayer) {
        TraitEffects.mark(serverPlayer, K_RITUAL);
    }

    private static long since(ServerPlayer serverPlayer, String string) {
        long l = serverPlayer.getPersistentData().getLong(string);
        if (l == 0L) {
            TraitEffects.mark(serverPlayer, string);
            return 0L;
        }
        return serverPlayer.level().getGameTime() - l;
    }

    static void give(LivingEntity livingEntity, Holder<MobEffect> holder, int n, int n2) {
        if (livingEntity instanceof ServerPlayer) {
            Ideology ideology;
            ServerPlayer serverPlayer = (ServerPlayer)livingEntity;
            if (((MobEffect)holder.value()).isBeneficial() && (ideology = IdeologyManager.of(serverPlayer)) != null && !ideology.buffsUnlocked()) {
                return;
            }
        }
        livingEntity.addEffect(new MobEffectInstance(holder, n2, n, true, false, true));
    }

    public static void giveFree(LivingEntity livingEntity, Holder<MobEffect> holder, int n, int n2) {
        livingEntity.addEffect(new MobEffectInstance(holder, n2, n, true, false, true));
    }

    private static boolean wearsArmor(Player player) {
        for (ItemStack itemStack : player.getArmorSlots()) {
            if (itemStack.isEmpty()) continue;
            return true;
        }
        return false;
    }

    private static boolean memberNear(ServerPlayer serverPlayer, Ideology ideology, double d) {
        for (ServerPlayer serverPlayer2 : IdeologyManager.online(serverPlayer.getServer(), ideology)) {
            if (serverPlayer2 == serverPlayer || serverPlayer2.level() != serverPlayer.level() || !(serverPlayer2.distanceToSqr(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ()) <= d * d)) continue;
            return true;
        }
        return false;
    }

    private static boolean otherPlayerNear(ServerPlayer serverPlayer, double d) {
        for (ServerPlayer serverPlayer2 : serverPlayer.getServer().getPlayerList().getPlayers()) {
            if (serverPlayer2 == serverPlayer || serverPlayer2.level() != serverPlayer.level() || !(serverPlayer2.distanceToSqr(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ()) <= d * d)) continue;
            return true;
        }
        return false;
    }

    private static boolean underground(ServerPlayer serverPlayer) {
        BlockPos blockPos = serverPlayer.blockPosition();
        return blockPos.getY() < 60 && !serverPlayer.level().canSeeSky(blockPos);
    }

    public static void tick(ServerPlayer serverPlayer) {
        ServerPlayer serverPlayer2;
        ItemStack itemStack;
        List list;
        double d;
        double d2;
        TraitEffects.vomitTick(serverPlayer);
        if (serverPlayer.tickCount % 20 != 0 || !serverPlayer.isAlive()) {
            return;
        }
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology == null) {
            LAST_POS.remove(serverPlayer.getUUID());
            return;
        }
        int n = ideology.level >= ideology.strongLevel() ? 1 : 0;
        int n2 = 50;
        int n3 = ideology.members.size();
        double[] dArray = LAST_POS.get(serverPlayer.getUUID());
        double d3 = 0.0;
        if (dArray != null && (d3 = Math.sqrt((d2 = serverPlayer.getX() - dArray[0]) * d2 + (d = serverPlayer.getZ() - dArray[1]) * d)) > 20.0) {
            d3 = 0.0;
        }
        LAST_POS.put(serverPlayer.getUUID(), new double[]{serverPlayer.getX(), serverPlayer.getZ()});
        if (serverPlayer.onGround() && serverPlayer.getVehicle() == null) {
            IdeologyManager.progress(serverPlayer, Quest.G_WALK, (int)Math.round(d3));
        }
        if (serverPlayer.isInWater() && !TraitEffects.wearsArmor((Player)serverPlayer)) {
            IdeologyManager.progress(serverPlayer, Quest.NUDE_SWIM, (int)Math.round(d3));
        }
        if (TraitEffects.underground(serverPlayer)) {
            IdeologyManager.progress(serverPlayer, Quest.CAVE_TIME, 1);
        }
        if (serverPlayer.isShiftKeyDown() && d3 < 0.05) {
            IdeologyManager.progress(serverPlayer, Quest.MEDITATE, 1);
        }
        if (ideology.has(Trait.COLLECTIVISM) && serverPlayer.level().getEntitiesOfClass(Villager.class, serverPlayer.getBoundingBox().inflate(16.0)).size() >= 3) {
            IdeologyManager.progress(serverPlayer, Quest.VILLAGE_TIME, 1);
        }
        if (ideology.has(Trait.BLOODEATING) && TraitEffects.since(serverPlayer, K_BLOOD) > 24000L) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WEAKNESS, 0, n2);
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HUNGER, 0, n2);
        }
        if (ideology.has(Trait.NUDISM)) {
            if (TraitEffects.wearsArmor((Player)serverPlayer)) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WEAKNESS, 0, n2);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0, n2);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SLOWDOWN, 0, n2);
            } else {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, n, n2);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 0, n2);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.JUMP, 0, n2);
                IdeologyManager.progress(serverPlayer, Quest.NUDE_TIME, 1);
                if (serverPlayer.onGround()) {
                    IdeologyManager.progress(serverPlayer, Quest.NUDE_WALK, (int)Math.round(d3));
                }
            }
        }
        if (ideology.has(Trait.FLAGELLANTISM) && TraitEffects.since(serverPlayer, K_PAIN) > 24000L) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WEAKNESS, 0, n2);
        }
        if (ideology.has(Trait.COLLECTIVISM)) {
            if (n3 >= 2) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, n, n2);
            }
            if (n3 >= 3) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 0, n2);
            }
            if (n3 >= 6 && !serverPlayer.hasEffect(MobEffects.HEALTH_BOOST)) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HEALTH_BOOST, 0, 1200);
            }
            boolean bl = TraitEffects.memberNear(serverPlayer, ideology, 32.0);
            if (n3 > 1 && !TraitEffects.memberNear(serverPlayer, ideology, 64.0)) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0, n2);
            }
            if (bl) {
                IdeologyManager.progress(serverPlayer, Quest.TOGETHER_TIME, 1);
            }
            IdeologyManager.setProgress(serverPlayer.getServer(), ideology, Quest.MEMBERS, Math.max(ideology.progress.getOrDefault((Object)Quest.MEMBERS, 0), n3), serverPlayer.getRandom());
        }
        if (ideology.has(Trait.INDIVIDUALISM)) {
            if (n3 == 1) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, n, n2);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 0, n2);
            } else if (n3 == 2) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, 0, n2);
            } else if (n3 >= 4) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WEAKNESS, 0, n2);
            }
        }
        if (ideology.has(Trait.HUSBANDRY) && (list = serverPlayer.level().getEntitiesOfClass(Animal.class, serverPlayer.getBoundingBox().inflate(8.0))).size() >= 3) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, n, n2);
        }
        if (ideology.has(Trait.PACIFISM)) {
            if (TraitEffects.since(serverPlayer, K_HIT) > 12000L) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HERO_OF_THE_VILLAGE, n, n2);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DAMAGE_RESISTANCE, 0, n2);
            }
            if (TraitEffects.since(serverPlayer, K_HIT) > 20L) {
                IdeologyManager.progress(serverPlayer, Quest.PEACE_TIME, 1);
            }
        }
        if (ideology.has(Trait.PIRACY)) {
            if (serverPlayer.isInWater() || serverPlayer.getVehicle() instanceof Boat) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DOLPHINS_GRACE, 0, n2);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WATER_BREATHING, 0, n2);
            }
            if (serverPlayer.getVehicle() instanceof Boat) {
                IdeologyManager.progress(serverPlayer, Quest.SAIL, (int)Math.round(d3));
            }
            if (TraitEffects.since(serverPlayer, K_PIRATE) > 36000L) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0, n2);
            }
        }
        if (ideology.has(Trait.RITUALISM) && TraitEffects.since(serverPlayer, K_RITUAL) > 36000L) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.UNLUCK, 0, n2);
        }
        if (ideology.has(Trait.UNDERGROUND)) {
            if (TraitEffects.underground(serverPlayer)) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, n, n2);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.NIGHT_VISION, 0, 300);
                if (serverPlayer.getY() < -40.0) {
                    IdeologyManager.progress(serverPlayer, Quest.DEEP_DIVE, 1);
                }
            } else if (serverPlayer.level().canSeeSky(serverPlayer.blockPosition()) && serverPlayer.level().isDay()) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WEAKNESS, 0, n2);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0, n2);
            }
        }
        if (ideology.has(Trait.ALCHEMY) && TraitEffects.hasBrewedEffect(serverPlayer)) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, 0, n2);
        }
        if (ideology.has(Trait.ASCETICISM)) {
            int n4 = serverPlayer.getFoodData().getFoodLevel();
            if (n4 <= 10) {
                if (!serverPlayer.hasEffect(MobEffects.REGENERATION)) {
                    TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, n, 100);
                }
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 0, n2);
                IdeologyManager.progress(serverPlayer, Quest.HUNGRY_TIME, 1);
                if (serverPlayer.onGround()) {
                    IdeologyManager.progress(serverPlayer, Quest.HUNGRY_WALK, (int)Math.round(d3));
                }
            } else if (n4 >= 20) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0, n2);
            }
        }
        if (ideology.has(Trait.HUNTING) && (itemStack = serverPlayer.getMainHandItem()).getItem() instanceof ProjectileWeaponItem) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, n, n2);
        }
        if (ideology.has(Trait.ENLIGHTENMENT) && serverPlayer.experienceLevel >= 30) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, n, n2);
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 0, n2);
        }
        if (ideology.structure == IdeologyStructure.CULT && !serverPlayer.getUUID().equals(ideology.leader) && (serverPlayer2 = serverPlayer.getServer().getPlayerList().getPlayer(ideology.leader)) != null && serverPlayer2.level() == serverPlayer.level() && serverPlayer2.distanceToSqr(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ()) <= 256.0) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, 0, n2);
            if (serverPlayer.tickCount % 200 == 0) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, 0, 60);
            }
        }
        if (serverPlayer.tickCount % 40 == 0) {
            IdeologyManager.syncManifest(serverPlayer);
        }
    }

    private static boolean hasBrewedEffect(ServerPlayer serverPlayer) {
        for (MobEffectInstance mobEffectInstance : serverPlayer.getActiveEffects()) {
            if (mobEffectInstance.isAmbient() || !((MobEffect)mobEffectInstance.getEffect().value()).isBeneficial()) continue;
            return true;
        }
        return false;
    }

    private static boolean nearHeat(ServerPlayer serverPlayer) {
        BlockPos blockPos = serverPlayer.blockPosition();
        for (int i = -3; i <= 3; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = -3; k <= 3; ++k) {
                    BlockState blockState = serverPlayer.level().getBlockState(blockPos.offset(i, j, k));
                    if (!blockState.is(BlockTags.FIRE) && !blockState.is(Blocks.LAVA) && !blockState.is(Blocks.MAGMA_BLOCK) && !blockState.is(BlockTags.CAMPFIRES)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public static void onDamage(LivingDamageEvent.Post post) {
        ServerPlayer serverPlayer;
        float f = post.getNewDamage();
        if (f <= 0.0f) {
            return;
        }
        LivingEntity livingEntity = post.getEntity();
        Entity entity = post.getSource().getEntity();
        if (livingEntity instanceof ServerPlayer && IdeologyManager.has(serverPlayer = (ServerPlayer)livingEntity, Trait.FLAGELLANTISM)) {
            Ideology ideology = IdeologyManager.of(serverPlayer);
            int n = ideology.level >= ideology.strongLevel() ? 1 : 0;
            TraitEffects.mark(serverPlayer, K_PAIN);
            if (f >= 2.0f) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DAMAGE_RESISTANCE, n, 300);
            }
            if (serverPlayer.getHealth() <= serverPlayer.getMaxHealth() * 0.3f) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, 0, 100);
            }
            IdeologyManager.progress(serverPlayer, Quest.PAIN_TAKEN, Math.max(1, Math.round(f)));
            if (serverPlayer.getHealth() <= 4.0f && serverPlayer.getHealth() > 0.0f) {
                IdeologyManager.progress(serverPlayer, Quest.LOW_HP, 1);
            }
            if ("fall".equals(post.getSource().getMsgId())) {
                IdeologyManager.progress(serverPlayer, Quest.FALL_PAIN, Math.max(1, Math.round(f)));
            }
        }
        if (entity instanceof ServerPlayer && (serverPlayer = (ServerPlayer)entity) != livingEntity && IdeologyManager.has(serverPlayer, Trait.PACIFISM)) {
            TraitEffects.mark(serverPlayer, K_HIT);
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WEAKNESS, 0, 600);
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0, 600);
            IdeologyManager.resetProgress(serverPlayer, Quest.PEACE_TIME);
        }
    }

    public static void onDeath(LivingDeathEvent livingDeathEvent) {
        Entity entity = livingDeathEvent.getSource().getEntity();
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)entity;
        entity = livingDeathEvent.getEntity();
        if (entity == serverPlayer) {
            return;
        }
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology == null) {
            return;
        }
        if (entity instanceof Enemy) {
            IdeologyManager.progress(serverPlayer, Quest.G_HOSTILE, 1);
        }
        if (entity.getType() == EntityType.DROWNED) {
            IdeologyManager.progress(serverPlayer, Quest.DROWNED_KILLS, 1);
        }
        if (livingDeathEvent.getSource().getDirectEntity() instanceof AbstractArrow && TraitEffects.flying((LivingEntity)entity)) {
            IdeologyManager.progress(serverPlayer, Quest.FLYING_SHOTS, 1);
        }
        if (ideology.has(Trait.PACIFISM)) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SLOWDOWN, 0, 1200);
        }
        if (ideology.has(Trait.SUMMONER)) {
            if (entity.getType().is(EntityTypeTags.UNDEAD)) {
                IdeologyManager.progress(serverPlayer, Quest.SUM_UNDEAD, 1);
            }
            if (entity instanceof Enemy && !serverPlayer.level().isDay()) {
                IdeologyManager.progress(serverPlayer, Quest.SUM_NIGHT, 1);
            }
        }
        if (ideology.has(Trait.PIRACY)) {
            TraitEffects.mark(serverPlayer, K_PIRATE);
            if (entity instanceof Player) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, 1, 1200);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, 0, 2400);
                IdeologyManager.progress(serverPlayer, Quest.KILL_PLAYER, 1);
            } else {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, 0, 400);
                IdeologyManager.progress(serverPlayer, Quest.KILL_MOBS, 1);
            }
        }
        if (ideology.has(Trait.HUSBANDRY) && entity instanceof Animal) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WEAKNESS, 0, 1200);
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HUNGER, 0, 1200);
        }
        if (ideology.has(Trait.BLOODEATING) && !serverPlayer.level().isDay()) {
            IdeologyManager.progress(serverPlayer, Quest.NIGHT_KILLS, 1);
        }
        if (ideology.has(Trait.NUDISM) && !TraitEffects.wearsArmor((Player)serverPlayer)) {
            IdeologyManager.progress(serverPlayer, Quest.NUDE_KILLS, 1);
        }
        if (ideology.has(Trait.INDIVIDUALISM) && !TraitEffects.otherPlayerNear(serverPlayer, 32.0)) {
            IdeologyManager.progress(serverPlayer, Quest.SOLO_KILLS, 1);
        }
        if (ideology.has(Trait.COLLECTIVISM) && TraitEffects.memberNear(serverPlayer, ideology, 32.0)) {
            IdeologyManager.progress(serverPlayer, Quest.GROUP_KILLS, 1);
        }
        if (ideology.has(Trait.ALCHEMY) && entity.getType() == EntityType.WITCH) {
            IdeologyManager.progress(serverPlayer, Quest.WITCH_KILLS, 1);
        }
        if (ideology.has(Trait.HUNTING)) {
            if (livingDeathEvent.getSource().getDirectEntity() instanceof AbstractArrow) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, 0, 400);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, 0, 1200);
                IdeologyManager.progress(serverPlayer, Quest.BOW_KILLS, 1);
                if (serverPlayer.distanceToSqr(entity.getX(), entity.getY(), entity.getZ()) >= 900.0) {
                    IdeologyManager.progress(serverPlayer, Quest.LONG_SHOTS, 1);
                }
                if (entity instanceof Animal) {
                    IdeologyManager.progress(serverPlayer, Quest.HUNT_ANIMALS, 1);
                }
            } else if (livingDeathEvent.getSource().getDirectEntity() == serverPlayer) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 0, 200);
            }
        }
    }

    private static boolean flying(LivingEntity livingEntity) {
        EntityType entityType = livingEntity.getType();
        return entityType == EntityType.PHANTOM || entityType == EntityType.BAT || entityType == EntityType.BLAZE || entityType == EntityType.GHAST || entityType == EntityType.BEE || entityType == EntityType.PARROT || entityType == EntityType.VEX || entityType == EntityType.ALLAY;
    }

    public static void onConsumed(ServerPlayer serverPlayer, ItemStack itemStack) {
        Ideology ideology = IdeologyManager.of(serverPlayer);
        if (ideology == null) {
            return;
        }
        if (!(itemStack.getItem() instanceof PotionItem) && !itemStack.is(Items.MILK_BUCKET)) {
            IdeologyManager.progress(serverPlayer, Quest.G_EAT, 1);
        }
        if (itemStack.is(Items.BEEF) || itemStack.is(Items.PORKCHOP) || itemStack.is(Items.CHICKEN) || itemStack.is(Items.MUTTON) || itemStack.is(Items.RABBIT) || itemStack.is(Items.COD) || itemStack.is(Items.SALMON) || itemStack.is(Items.ROTTEN_FLESH)) {
            IdeologyManager.progress(serverPlayer, Quest.EAT_RAW, 1);
        }
        if (ideology.has(Trait.HUSBANDRY) && TraitEffects.isMeat(itemStack)) {
            TraitEffects.vomit(serverPlayer, itemStack);
        }
        if (ideology.has(Trait.ALCHEMY)) {
            if (itemStack.getItem() instanceof PotionItem) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.ABSORPTION, 0, 1200);
                IdeologyManager.progress(serverPlayer, Quest.DRINK_POTIONS, 1);
            } else if (itemStack.is(Items.MILK_BUCKET)) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.CONFUSION, 0, 200);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WEAKNESS, 0, 600);
            }
        }
        if (ideology.has(Trait.ASCETICISM)) {
            if (itemStack.is(Items.BREAD) || itemStack.is(Items.CARROT) || itemStack.is(Items.POTATO) || itemStack.is(Items.BAKED_POTATO) || itemStack.is(Items.APPLE) || itemStack.is(Items.BEETROOT) || itemStack.is(Items.BEETROOT_SOUP) || itemStack.is(Items.MUSHROOM_STEW) || itemStack.is(Items.DRIED_KELP)) {
                IdeologyManager.progress(serverPlayer, Quest.SIMPLE_FOOD, 1);
            } else if (itemStack.is(Items.GOLDEN_APPLE) || itemStack.is(Items.ENCHANTED_GOLDEN_APPLE) || itemStack.is(Items.GOLDEN_CARROT) || itemStack.is(Items.CAKE) || itemStack.is(Items.PUMPKIN_PIE) || itemStack.is(Items.COOKIE) || itemStack.is(Items.RABBIT_STEW) || itemStack.is(Items.HONEY_BOTTLE)) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.CONFUSION, 0, 200);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HUNGER, 1, 400);
            }
        }
    }

    static boolean isMeat(ItemStack itemStack) {
        return itemStack.is(Items.BEEF) || itemStack.is(Items.COOKED_BEEF) || itemStack.is(Items.PORKCHOP) || itemStack.is(Items.COOKED_PORKCHOP) || itemStack.is(Items.CHICKEN) || itemStack.is(Items.COOKED_CHICKEN) || itemStack.is(Items.MUTTON) || itemStack.is(Items.COOKED_MUTTON) || itemStack.is(Items.RABBIT) || itemStack.is(Items.COOKED_RABBIT) || itemStack.is(Items.RABBIT_STEW) || itemStack.is(Items.ROTTEN_FLESH);
    }

    private static void vomit(ServerPlayer serverPlayer, ItemStack itemStack) {
        TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.CONFUSION, 0, 200);
        serverPlayer.addEffect(new MobEffectInstance(ModEffects.VOMITING, 60, 0, false, false, true));
        TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 1, 60);
        serverPlayer.getPersistentData().putInt(K_VOMIT, 60);
        FoodData foodData = serverPlayer.getFoodData();
        FoodProperties foodProperties = itemStack.getFoodProperties((LivingEntity)serverPlayer);
        int n = foodProperties != null ? foodProperties.nutrition() : 4;
        foodData.setFoodLevel(Math.max(0, foodData.getFoodLevel() - n));
        foodData.setSaturation(0.0f);
        serverPlayer.hurt(serverPlayer.damageSources().magic(), 3.0f);
        serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getEyeY(), serverPlayer.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.2f, 0.4f);
    }

    private static void vomitTick(ServerPlayer serverPlayer) {
        Level level;
        CompoundTag compoundTag = serverPlayer.getPersistentData();
        int n = compoundTag.getInt(K_VOMIT);
        if (n <= 0) {
            return;
        }
        compoundTag.putInt(K_VOMIT, --n);
        if (n % 20 == 0) {
            serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getEyeY(), serverPlayer.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.0f, 0.5f + serverPlayer.getRandom().nextFloat() * 0.2f);
        }
        if (n % 2 != 0 || !((level = serverPlayer.level()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        level = serverPlayer.getLookAngle();
        double d = serverPlayer.getX() + level.x * 0.4;
        double d2 = serverPlayer.getEyeY() - 0.25;
        double d3 = serverPlayer.getZ() + level.z * 0.4;
        ItemParticleOption itemParticleOption = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack((ItemLike)Items.ROTTEN_FLESH));
        for (int i = 0; i < 4; ++i) {
            double d4 = level.x + (serverPlayer.getRandom().nextDouble() - 0.5) * 0.3;
            double d5 = -0.4 + (serverPlayer.getRandom().nextDouble() - 0.5) * 0.2;
            double d6 = level.z + (serverPlayer.getRandom().nextDouble() - 0.5) * 0.3;
            serverLevel.sendParticles((ParticleOptions)itemParticleOption, d, d2, d3, 0, d4, d5, d6, 0.25);
        }
    }

    public static void onBrew(PlayerBrewedPotionEvent playerBrewedPotionEvent) {
        ServerPlayer serverPlayer;
        Player player = playerBrewedPotionEvent.getEntity();
        if (player instanceof ServerPlayer && IdeologyManager.has(serverPlayer = (ServerPlayer)player, Trait.ALCHEMY)) {
            IdeologyManager.progress(serverPlayer, Quest.BREW, 1);
        }
    }

    public static void onPickupXp(PlayerXpEvent.PickupXp pickupXp) {
        int n;
        Object object = pickupXp.getEntity();
        if (!(object instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)object;
        object = IdeologyManager.of(serverPlayer);
        if (object == null || !((Ideology)object).has(Trait.ENLIGHTENMENT)) {
            return;
        }
        int n2 = pickupXp.getOrb().getValue();
        int n3 = ((Ideology)object).buffsUnlocked() ? (int)Math.round((double)n2 * (((Ideology)object).level >= ((Ideology)object).strongLevel() ? 0.5 : 0.25)) : (n = 0);
        if (n > 0) {
            serverPlayer.giveExperiencePoints(n);
        }
        IdeologyManager.progress(serverPlayer, Quest.XP_ORBS, n2);
    }

    public static void onLevelChange(PlayerXpEvent.LevelChange levelChange) {
        ServerPlayer serverPlayer;
        Player player;
        if (levelChange.getLevels() > 0 && (player = levelChange.getEntity()) instanceof ServerPlayer && IdeologyManager.has(serverPlayer = (ServerPlayer)player, Trait.ENLIGHTENMENT)) {
            IdeologyManager.progress(serverPlayer, Quest.XP_LEVELS, levelChange.getLevels());
        }
    }

    public static void onBreak(BlockEvent.BreakEvent breakEvent) {
        Object object = breakEvent.getPlayer();
        if (!(object instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)object;
        object = IdeologyManager.of(serverPlayer);
        if (object == null) {
            return;
        }
        BlockState blockState = breakEvent.getState();
        BlockPos blockPos = breakEvent.getPos();
        IdeologyManager.progress(serverPlayer, Quest.G_MINE, 1);
        if (blockState.is(BlockTags.COAL_ORES)) {
            IdeologyManager.progress(serverPlayer, Quest.G_COAL, 1);
        }
        if (((Ideology)object).has(Trait.COLLECTIVISM) && TraitEffects.memberNear(serverPlayer, (Ideology)object, 32.0)) {
            IdeologyManager.progress(serverPlayer, Quest.GROUP_MINE, 1);
        }
        if (((Ideology)object).has(Trait.INDIVIDUALISM) && !TraitEffects.otherPlayerNear(serverPlayer, 32.0)) {
            IdeologyManager.progress(serverPlayer, Quest.SOLO_MINE, 1);
        }
        if (blockState.is(Blocks.NETHER_WART) && (Integer)blockState.getValue((Property)BlockStateProperties.AGE_3) >= 3) {
            IdeologyManager.progress(serverPlayer, Quest.NETHER_WART, 1);
        }
        if (blockState.is(Blocks.AMETHYST_CLUSTER)) {
            IdeologyManager.progress(serverPlayer, Quest.AMETHYST, 1);
        }
        if (((Ideology)object).has(Trait.NATURE)) {
            Block block = blockState.getBlock();
            if (block instanceof CropBlock) {
                CropBlock cropBlock = (CropBlock)block;
                if (cropBlock.isMaxAge(blockState)) {
                    IdeologyManager.progress(serverPlayer, Quest.HARVEST, 1);
                }
            } else if (blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES) || blockState.is(BlockTags.FLOWERS) || blockState.is(BlockTags.SAPLINGS)) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SLOWDOWN, 0, 400);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WEAKNESS, 0, 400);
            }
        }
        if (((Ideology)object).has(Trait.INDIVIDUALISM) && (blockState.is(Blocks.IRON_ORE) || blockState.is(Blocks.DEEPSLATE_IRON_ORE))) {
            IdeologyManager.progress(serverPlayer, Quest.MINE_IRON, 1);
        }
        if (((Ideology)object).has(Trait.UNDERGROUND) && blockPos.getY() < 60 && !serverPlayer.level().canSeeSky(blockPos.above())) {
            if (blockState.is(BlockTags.BASE_STONE_OVERWORLD)) {
                IdeologyManager.progress(serverPlayer, Quest.MINE_STONE, 1);
            }
            if (blockState.is(Tags.Blocks.ORES)) {
                IdeologyManager.progress(serverPlayer, Quest.MINE_ORES, 1);
            }
            if (blockState.is(Blocks.DIAMOND_ORE) || blockState.is(Blocks.DEEPSLATE_DIAMOND_ORE)) {
                IdeologyManager.progress(serverPlayer, Quest.DIAMONDS, 1);
            }
        }
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent entityPlaceEvent) {
        Object object = entityPlaceEvent.getEntity();
        if (!(object instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)object;
        object = IdeologyManager.of(serverPlayer);
        if (object == null) {
            return;
        }
        BlockState blockState = entityPlaceEvent.getPlacedBlock();
        IdeologyManager.progress(serverPlayer, Quest.G_PLACE, 1);
        if (blockState.is(BlockTags.CANDLES)) {
            IdeologyManager.progress(serverPlayer, Quest.CANDLES, 1);
        }
        if (((Ideology)object).has(Trait.INDIVIDUALISM)) {
            IdeologyManager.progress(serverPlayer, Quest.PLACE_BLOCKS, 1);
        }
        if (((Ideology)object).has(Trait.ENLIGHTENMENT) && (blockState.is(Blocks.BOOKSHELF) || blockState.is(Blocks.CHISELED_BOOKSHELF))) {
            IdeologyManager.progress(serverPlayer, Quest.BOOKSHELVES, 1);
        }
        if (((Ideology)object).has(Trait.NATURE) && (blockState.is(BlockTags.CROPS) || blockState.is(BlockTags.SAPLINGS) || blockState.is(BlockTags.FLOWERS))) {
            IdeologyManager.progress(serverPlayer, Quest.PLANT, 1);
            if (blockState.is(BlockTags.SAPLINGS)) {
                IdeologyManager.progress(serverPlayer, Quest.SAPLINGS, 1);
            }
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, 0, 200);
            CompoundTag compoundTag = serverPlayer.getPersistentData();
            int n = compoundTag.getInt(K_PLANTS) + 1;
            compoundTag.putInt(K_PLANTS, n);
            if (n % 20 == 0) {
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, ((Ideology)object).level >= ((Ideology)object).strongLevel() ? 1 : 0, 3600);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, 0, 3600);
            }
        }
    }

    public static void onBreed(BabyEntitySpawnEvent babyEntitySpawnEvent) {
        ServerPlayer serverPlayer;
        Player player = babyEntitySpawnEvent.getCausedByPlayer();
        if (player instanceof ServerPlayer && IdeologyManager.has(serverPlayer = (ServerPlayer)player, Trait.HUSBANDRY)) {
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, 0, 400);
            TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, 0, 2400);
            IdeologyManager.progress(serverPlayer, Quest.BREED, 1);
        }
    }

    public static void onTame(AnimalTameEvent animalTameEvent) {
        ServerPlayer serverPlayer;
        Player player = animalTameEvent.getTamer();
        if (player instanceof ServerPlayer && IdeologyManager.has(serverPlayer = (ServerPlayer)player, Trait.HUSBANDRY)) {
            IdeologyManager.progress(serverPlayer, Quest.TAME, 1);
        }
    }

    public static void onTrade(TradeWithVillagerEvent tradeWithVillagerEvent) {
        ServerPlayer serverPlayer;
        Player player = tradeWithVillagerEvent.getEntity();
        if (player instanceof ServerPlayer && IdeologyManager.has(serverPlayer = (ServerPlayer)player, Trait.PACIFISM)) {
            IdeologyManager.progress(serverPlayer, Quest.TRADE, 1);
        }
    }

    public static void onBonemeal(BonemealEvent bonemealEvent) {
        ServerPlayer serverPlayer;
        Player player = bonemealEvent.getPlayer();
        if (player instanceof ServerPlayer && IdeologyManager.has(serverPlayer = (ServerPlayer)player, Trait.NATURE)) {
            IdeologyManager.progress(serverPlayer, Quest.BONEMEAL, 1);
        }
    }

    public static void onRepair(AnvilRepairEvent anvilRepairEvent) {
        ServerPlayer serverPlayer;
        Player player = anvilRepairEvent.getEntity();
        if (player instanceof ServerPlayer && IdeologyManager.has(serverPlayer = (ServerPlayer)player, Trait.PACIFISM)) {
            IdeologyManager.progress(serverPlayer, Quest.REPAIR, 1);
        }
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract entityInteract) {
        Animal animal;
        Object object = entityInteract.getEntity();
        if (!(object instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)object;
        Item item = entityInteract.getItemStack().getItem();
        if (item instanceof IdeologyItems.BloodLancet) {
            object = (IdeologyItems.BloodLancet)item;
            item = entityInteract.getTarget();
            if (item instanceof EnderDragonPart) {
                EnderDragonPart enderDragonPart = (EnderDragonPart)item;
                ((IdeologyItems.BloodLancet)((Object)object)).draw(entityInteract.getItemStack(), serverPlayer, (LivingEntity)enderDragonPart.parentMob);
                entityInteract.setCanceled(true);
                return;
            }
        }
        if (!IdeologyManager.has(serverPlayer, Trait.HUSBANDRY)) {
            return;
        }
        object = entityInteract.getItemStack();
        item = entityInteract.getTarget();
        if (item instanceof Animal) {
            animal = (Animal)item;
            if (!object.isEmpty() && animal.isFood((ItemStack)object)) {
                IdeologyManager.progress(serverPlayer, Quest.FEED_ANIMALS, 1);
            }
        }
        if (object.is(Items.SHEARS) && (item = entityInteract.getTarget()) instanceof Sheep && (animal = (Sheep)item).readyForShearing()) {
            IdeologyManager.progress(serverPlayer, Quest.SHEAR, 1);
        }
        if (object.is(Items.BUCKET) && (item = entityInteract.getTarget()) instanceof Cow && !(animal = (Cow)item).isBaby()) {
            IdeologyManager.progress(serverPlayer, Quest.MILK, 1);
        }
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock rightClickBlock) {
        Player player = rightClickBlock.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        if (!rightClickBlock.getItemStack().isEmpty() && serverPlayer.level().getBlockState(rightClickBlock.getPos()).is(Blocks.COMPOSTER)) {
            IdeologyManager.progress(serverPlayer, Quest.COMPOST, 1);
        }
    }

    public static void onCrafted(PlayerEvent.ItemCraftedEvent itemCraftedEvent) {
        Player player = itemCraftedEvent.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        player = itemCraftedEvent.getCrafting();
        IdeologyManager.progress(serverPlayer, Quest.G_CRAFT, Math.max(1, player.getCount()));
        if (player.is(Items.BOOK)) {
            IdeologyManager.progress(serverPlayer, Quest.CRAFT_BOOKS, Math.max(1, player.getCount()));
        }
    }

    public static void onSmelted(PlayerEvent.ItemSmeltedEvent itemSmeltedEvent) {
        Player player = itemSmeltedEvent.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        int n = Math.max(1, itemSmeltedEvent.getSmelting().getCount());
        IdeologyManager.progress(serverPlayer, Quest.G_SMELT, n);
    }

    public static void onFished(ItemFishedEvent itemFishedEvent) {
        Player player = itemFishedEvent.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        IdeologyManager.progress(serverPlayer, Quest.G_FISH, 1);
        IdeologyManager.progress(serverPlayer, Quest.FISH, 1);
    }

    public static void onWake(PlayerWakeUpEvent playerWakeUpEvent) {
        ServerPlayer serverPlayer;
        block3: {
            block2: {
                Player player = playerWakeUpEvent.getEntity();
                if (!(player instanceof ServerPlayer)) break block2;
                serverPlayer = (ServerPlayer)player;
                if (!playerWakeUpEvent.wakeImmediately() && serverPlayer.level().isDay()) break block3;
            }
            return;
        }
        IdeologyManager.progress(serverPlayer, Quest.G_SLEEP, 1);
        IdeologyManager.progress(serverPlayer, Quest.SLEEP_NIGHTS, 1);
    }

    private TraitEffects() {
    }
}
