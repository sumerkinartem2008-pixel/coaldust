/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.coaldust.mixin.client;

import com.coaldust.effect.ModEffects;
import com.coaldust.network.FormSync;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerModel.class})
public abstract class PlayerModelMixin {
    @Inject(method={"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at={@At(value="TAIL")}, require=0)
    private void coaldust$vomitPose(LivingEntity livingEntity, float f, float f2, float f3, float f4, float f5, CallbackInfo callbackInfo) {
        float f6;
        PlayerModel playerModel = (PlayerModel)this;
        int n = FormSync.fatStage(FormSync.clientForm((Entity)livingEntity));
        float f7 = n == 1 ? 1.15f : (f6 = n == 2 ? 1.3f : 1.0f);
        float f8 = n == 1 ? 1.7f : (n == 2 ? 2.3f : 1.0f);
        playerModel.body.xScale = f6;
        playerModel.body.zScale = f8;
        playerModel.jacket.xScale = f6;
        playerModel.jacket.zScale = f8;
        if (!livingEntity.hasEffect(ModEffects.VOMITING)) {
            return;
        }
        float f9 = (float)Math.sin(f3 * 1.6f) * 0.12f;
        playerModel.head.xRot = 0.95f + f9;
        playerModel.body.xRot = 0.45f + f9 * 0.5f;
        playerModel.rightArm.xRot = -0.55f + f9;
        playerModel.rightArm.yRot = -0.45f;
        playerModel.leftArm.xRot = -0.55f + f9;
        playerModel.leftArm.yRot = 0.45f;
        playerModel.hat.copyFrom(playerModel.head);
        playerModel.jacket.copyFrom(playerModel.body);
        playerModel.rightSleeve.copyFrom(playerModel.rightArm);
        playerModel.leftSleeve.copyFrom(playerModel.leftArm);
    }
}
