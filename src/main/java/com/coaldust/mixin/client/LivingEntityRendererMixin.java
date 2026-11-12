/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.coaldust.mixin.client;

import com.coaldust.network.FormSync;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntityRenderer.class})
public abstract class LivingEntityRendererMixin {
    private static final int COALDUST_PINK = -22332;
    private static int coaldust$tint = -1;

    @Inject(method={"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="HEAD")}, require=0)
    private void coaldust$pickTint(LivingEntity livingEntity, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, CallbackInfo callbackInfo) {
        int n2 = livingEntity instanceof Player ? FormSync.clientForm((Entity)livingEntity) : 0;
        int n3 = FormSync.dotaStage(n2);
        coaldust$tint = n3 >= 3 && n3 <= 4 ? -22332 : -1;
    }

    @ModifyArg(method={"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"), index=4, require=0)
    private int coaldust$applyTint(int n) {
        int n2 = coaldust$tint;
        if (n2 == -1) {
            return n;
        }
        int n3 = n >>> 24 & 0xFF;
        int n4 = n >> 16 & 0xFF;
        int n5 = n >> 8 & 0xFF;
        int n6 = n & 0xFF;
        n4 = n4 * (n2 >> 16 & 0xFF) / 255;
        n5 = n5 * (n2 >> 8 & 0xFF) / 255;
        n6 = n6 * (n2 & 0xFF) / 255;
        return n3 << 24 | n4 << 16 | n5 << 8 | n6;
    }
}
