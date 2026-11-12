/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 */
package com.coaldust.network;

import com.coaldust.CoalDust;
import com.coaldust.disease.DeathFlower;
import com.coaldust.disease.Dotafication;
import com.coaldust.disease.Plague;
import com.coaldust.meteor.RemoteSizePayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class FormSync {
    public static final ResourceLocation FORM_ID = CoalDust.id("form");
    private static final ResourceLocation OLD_MELON_HEALTH = CoalDust.id("melon_bonus_health");

    public static boolean tumors(int n) {
        return (n >> 8 & 1) != 0;
    }

    public static boolean ulcers(int n) {
        return (n >> 9 & 1) != 0;
    }

    public static int dotaStage(int n) {
        return n >> 10 & 7;
    }

    public static boolean isPigForm(int n) {
        return FormSync.dotaStage(n) >= 5;
    }

    public static int fatStage(int n) {
        int n2 = FormSync.dotaStage(n);
        return n2 >= 1 && n2 <= 4 ? (n2 >= 4 ? 2 : 1) : 0;
    }

    public static int clientForm(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return 0;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        AttributeInstance attributeInstance = livingEntity.getAttribute(Attributes.LUCK);
        if (attributeInstance == null) {
            return 0;
        }
        AttributeModifier attributeModifier = attributeInstance.getModifier(FORM_ID);
        return attributeModifier == null ? 0 : (int)Math.round(attributeModifier.amount());
    }

    public static void register(RegisterPayloadHandlersEvent registerPayloadHandlersEvent) {
        registerPayloadHandlersEvent.registrar("1").playToServer(RemoteSizePayload.TYPE, RemoteSizePayload.STREAM_CODEC, RemoteSizePayload::handle);
    }

    public static int formOf(ServerPlayer serverPlayer) {
        int n = Math.max(0, Math.min(5, Dotafication.INSTANCE.getStage(serverPlayer)));
        int n2 = Plague.hasTumors(serverPlayer) ? 1 : 0;
        int n3 = DeathFlower.hasUlcers(serverPlayer) ? 1 : 0;
        return n2 << 8 | n3 << 9 | n << 10;
    }

    public static void sendForm(ServerPlayer serverPlayer) {
        int n;
        AttributeInstance attributeInstance = serverPlayer.getAttribute(Attributes.LUCK);
        if (attributeInstance == null) {
            return;
        }
        int n2 = FormSync.formOf(serverPlayer);
        AttributeModifier attributeModifier = attributeInstance.getModifier(FORM_ID);
        int n3 = n = attributeModifier == null ? 0 : (int)Math.round(attributeModifier.amount());
        if (n == n2 && (attributeModifier != null || n2 == 0)) {
            return;
        }
        attributeInstance.removeModifier(FORM_ID);
        if (n2 > 0) {
            attributeInstance.addTransientModifier(new AttributeModifier(FORM_ID, (double)n2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    public static void heartbeat(ServerPlayer serverPlayer) {
        if (serverPlayer.tickCount % 20 != 0) {
            return;
        }
        FormSync.sendForm(serverPlayer);
        AttributeInstance attributeInstance = serverPlayer.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance != null && attributeInstance.getModifier(OLD_MELON_HEALTH) != null) {
            attributeInstance.removeModifier(OLD_MELON_HEALTH);
        }
    }

    private FormSync() {
    }
}
