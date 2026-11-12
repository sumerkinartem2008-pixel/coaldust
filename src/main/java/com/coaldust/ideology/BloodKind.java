/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 */
package com.coaldust.ideology;

import com.coaldust.ideology.TraitEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public enum BloodKind {
    COMMON("blood_bottle", 0, 40),
    BEAST("beast_blood", 1, 200),
    ENDER("ender_blood", 1, 200),
    WITCH("witch_blood", 1, 200),
    DRAGON("dragon_blood", 2, 1200),
    WITHER("wither_blood", 2, 1200),
    WARDEN("warden_blood", 2, 1200),
    ELDER("elder_blood", 2, 1200);

    public final String item;
    public final int tier;
    public final int cooldown;

    private BloodKind(String string2, int n2, int n3) {
        this.item = string2;
        this.tier = n2;
        this.cooldown = n3;
    }

    public static BloodKind of(LivingEntity livingEntity) {
        EntityType entityType = livingEntity.getType();
        if (entityType == EntityType.ENDER_DRAGON) {
            return DRAGON;
        }
        if (entityType == EntityType.WITHER) {
            return WITHER;
        }
        if (entityType == EntityType.WARDEN) {
            return WARDEN;
        }
        if (entityType == EntityType.ELDER_GUARDIAN) {
            return ELDER;
        }
        if (entityType == EntityType.RAVAGER || entityType == EntityType.HOGLIN || entityType == EntityType.ZOGLIN || entityType == EntityType.PIGLIN_BRUTE || entityType == EntityType.POLAR_BEAR || entityType == EntityType.VINDICATOR) {
            return BEAST;
        }
        if (entityType == EntityType.ENDERMAN || entityType == EntityType.SHULKER || entityType == EntityType.ENDERMITE) {
            return ENDER;
        }
        if (entityType == EntityType.WITCH || entityType == EntityType.EVOKER || entityType == EntityType.ILLUSIONER) {
            return WITCH;
        }
        return COMMON;
    }

    public void drinkMember(ServerPlayer serverPlayer, int n) {
        switch (this.ordinal()) {
            case 0: {
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, n, 200);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.NIGHT_VISION, 0, 2400);
                serverPlayer.getFoodData().eat(4, 0.6f);
                break;
            }
            case 1: {
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DAMAGE_RESISTANCE, n, 3600);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HEALTH_BOOST, 1, 3600);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, 0, 200);
                serverPlayer.getFoodData().eat(8, 0.8f);
                break;
            }
            case 2: {
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, 1 + n, 2400);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.NIGHT_VISION, 0, 6000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.SLOW_FALLING, 0, 1200);
                serverPlayer.getFoodData().eat(4, 0.6f);
                BloodKind.teleport(serverPlayer);
                break;
            }
            case 3: {
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, 1 + n, 400);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.FIRE_RESISTANCE, 0, 3600);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WATER_BREATHING, 0, 3600);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, 0, 3600);
                serverPlayer.getFoodData().eat(4, 0.6f);
                break;
            }
            case 4: {
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, 1 + n, 600);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DAMAGE_RESISTANCE, 1, 6000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.FIRE_RESISTANCE, 0, 12000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HEALTH_BOOST, 2, 6000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.ABSORPTION, 2, 2400);
                serverPlayer.getFoodData().eat(20, 1.0f);
                break;
            }
            case 5: {
                serverPlayer.removeEffect(MobEffects.WITHER);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DAMAGE_RESISTANCE, 1, 6000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.ABSORPTION, 3, 2400);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 1 + n, 6000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.NIGHT_VISION, 0, 12000);
                serverPlayer.getFoodData().eat(12, 0.8f);
                break;
            }
            case 6: {
                serverPlayer.removeEffect(MobEffects.DARKNESS);
                serverPlayer.removeEffect(MobEffects.BLINDNESS);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.NIGHT_VISION, 0, 12000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, 1 + n, 6000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 1, 6000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DAMAGE_RESISTANCE, 0, 6000);
                serverPlayer.getFoodData().eat(12, 0.8f);
                break;
            }
            case 7: {
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WATER_BREATHING, 0, 12000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.CONDUIT_POWER, 0, 12000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DOLPHINS_GRACE, n, 6000);
                TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 1, 6000);
                serverPlayer.getFoodData().eat(12, 0.8f);
            }
        }
    }

    public void drinkStranger(ServerPlayer serverPlayer) {
        TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.CONFUSION, 0, 200 + 100 * this.tier);
        TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HUNGER, 1, 400);
        TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.POISON, this.tier, 100);
        if (this.tier >= 1) {
            TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WITHER, this.tier - 1, 200);
        }
        if (this.tier >= 2) {
            TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.BLINDNESS, 0, 200);
        }
        if (this == ENDER) {
            BloodKind.teleport(serverPlayer);
        }
    }

    private static void teleport(ServerPlayer serverPlayer) {
        for (int i = 0; i < 16; ++i) {
            double d;
            double d2;
            double d3 = serverPlayer.getX() + (serverPlayer.getRandom().nextDouble() - 0.5) * 16.0;
            if (!serverPlayer.randomTeleport(d3, d2 = Math.max((double)serverPlayer.level().getMinBuildHeight(), Math.min(serverPlayer.getY() + (double)serverPlayer.getRandom().nextInt(16) - 8.0, (double)(serverPlayer.level().getMaxBuildHeight() - 1))), d = serverPlayer.getZ() + (serverPlayer.getRandom().nextDouble() - 0.5) * 16.0, true)) continue;
            serverPlayer.level().playSound(null, d3, d2, d, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            return;
        }
    }
}
