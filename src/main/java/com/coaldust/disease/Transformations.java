/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.UseAnim
 *  net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent$Start
 *  net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
 *  net.neoforged.neoforge.event.entity.player.AttackEntityEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$Clone
 */
package com.coaldust.disease;

import com.coaldust.disease.BoneDecay;
import com.coaldust.disease.Dotafication;
import com.coaldust.network.FormSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class Transformations {
    public static boolean isTransformed(Player player) {
        if (Transformations.isClient(player)) {
            int n = FormSync.clientForm((Entity)player);
            return FormSync.isPigForm(n);
        }
        return Dotafication.isPig(player);
    }

    private static boolean isClient(Player player) {
        return !(player instanceof ServerPlayer);
    }

    public static void onAttack(AttackEntityEvent attackEntityEvent) {
        Player player = attackEntityEvent.getEntity();
        if (Transformations.isTransformed(player) && !player.getMainHandItem().isEmpty()) {
            attackEntityEvent.setCanceled(true);
        }
    }

    public static void onUseStart(LivingEntityUseItemEvent.Start start) {
        UseAnim useAnim;
        LivingEntity livingEntity = start.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        livingEntity = start.getItem();
        if (Transformations.isTransformed(player) && ((useAnim = livingEntity.getUseAnimation()) == UseAnim.BOW || useAnim == UseAnim.CROSSBOW || useAnim == UseAnim.SPEAR)) {
            start.setCanceled(true);
        }
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent livingIncomingDamageEvent) {
        ServerPlayer serverPlayer;
        float f;
        Player player;
        Entity entity = livingIncomingDamageEvent.getSource().getEntity();
        if (entity instanceof Player && Transformations.isTransformed(player = (Player)entity)) {
            boolean bl;
            boolean bl2 = bl = livingIncomingDamageEvent.getSource().getDirectEntity() == player;
            if (!bl || !player.getMainHandItem().isEmpty()) {
                livingIncomingDamageEvent.setCanceled(true);
                return;
            }
        }
        if ((player = livingIncomingDamageEvent.getEntity()) instanceof ServerPlayer && (f = BoneDecay.damageMultiplier((Player)(serverPlayer = (ServerPlayer)player))) > 1.0f) {
            livingIncomingDamageEvent.setAmount(livingIncomingDamageEvent.getAmount() * f);
        }
    }

    public static void onClone(PlayerEvent.Clone clone) {
        Player player = clone.getOriginal();
        CompoundTag compoundTag = clone.getEntity().getPersistentData();
        if (Dotafication.isPig(player)) {
            compoundTag.putInt(Dotafication.INSTANCE.stageKey, 5);
        }
    }

    private Transformations() {
    }
}
