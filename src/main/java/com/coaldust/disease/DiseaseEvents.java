/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 *  net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent$Finish
 *  net.neoforged.neoforge.event.entity.living.LivingHealEvent
 *  net.neoforged.neoforge.event.tick.PlayerTickEvent$Post
 */
package com.coaldust.disease;

import com.coaldust.disease.Disease;
import com.coaldust.disease.Diseases;
import com.coaldust.disease.Dysentery;
import com.coaldust.disease.StomachCancer;
import com.coaldust.dust.DustPlayerEffects;
import com.coaldust.effect.ModEffects;
import com.coaldust.ideology.Abyss;
import com.coaldust.ideology.Summons;
import com.coaldust.ideology.TraitEffects;
import com.coaldust.network.FormSync;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class DiseaseEvents {
    public static void onPlayerTick(PlayerTickEvent.Post post) {
        Player player = post.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        if (!serverPlayer.isAlive()) {
            return;
        }
        DustPlayerEffects.tick(serverPlayer);
        TraitEffects.tick(serverPlayer);
        Abyss.tick(serverPlayer);
        Summons.tick(serverPlayer);
        FormSync.heartbeat(serverPlayer);
        for (Disease disease : Diseases.ALL) {
            disease.tick(serverPlayer);
        }
    }

    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish finish) {
        LivingEntity livingEntity = finish.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)livingEntity;
            StomachCancer.INSTANCE.onAte(serverPlayer, finish.getItem());
            Dysentery.INSTANCE.onAte(serverPlayer, finish.getItem());
            TraitEffects.onConsumed(serverPlayer, finish.getItem());
        }
    }

    public static void onHeal(LivingHealEvent livingHealEvent) {
        if (livingHealEvent.getEntity().hasEffect(ModEffects.WASTING)) {
            livingHealEvent.setCanceled(true);
        }
    }

    public static void registerCommand(RegisterCommandsEvent registerCommandsEvent) {
        registerCommandsEvent.getDispatcher().register((LiteralArgumentBuilder)Commands.literal((String)"coaldust_status").executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            StringBuilder stringBuilder = new StringBuilder("[Coal Dust] ");
            for (Disease disease : Diseases.ALL) {
                stringBuilder.append(disease.getName()).append('=').append(disease.getStage(serverPlayer)).append("  ");
            }
            FormSync.sendForm(serverPlayer);
            int n = FormSync.clientForm((Entity)serverPlayer);
            stringBuilder.append("| form dota=").append(FormSync.dotaStage(n));
            ((CommandSourceStack)commandContext.getSource()).sendSystemMessage((Component)Component.literal((String)stringBuilder.toString()));
            return 1;
        }));
    }

    private DiseaseEvents() {
    }
}
