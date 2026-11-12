/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.entity.player.PlayerRenderer
 *  net.minecraft.client.resources.PlayerSkin$Model
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.client.event.EntityRenderersEvent$AddLayers
 */
package com.coaldust.client;

import com.coaldust.CoalDust;
import com.coaldust.network.FormSync;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class DiseaseMarksLayer
extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation TEX = CoalDust.id("textures/entity/disease_marks.png");
    private static final float PX = 0.0625f;
    private static final float[][] BOXES = new float[][]{{-4.0f, -8.0f, -4.0f, 4.0f, 0.0f, 4.0f}, {-4.0f, 0.0f, -2.0f, 4.0f, 12.0f, 2.0f}, {-4.0f, 0.0f, -2.0f, 4.0f, 12.0f, 2.0f}, {-2.0f, -2.0f, -2.0f, 1.0f, 10.0f, 2.0f}, {-1.0f, -2.0f, -2.0f, 2.0f, 10.0f, 2.0f}, {-2.0f, 0.0f, -2.0f, 2.0f, 12.0f, 2.0f}, {-2.0f, 0.0f, -2.0f, 2.0f, 12.0f, 2.0f}};

    public DiseaseMarksLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    public static void init(IEventBus iEventBus) {
        iEventBus.addListener(addLayers -> DiseaseMarksLayer.addLayers(addLayers));
    }

    private static void addLayers(EntityRenderersEvent.AddLayers addLayers) {
        for (PlayerSkin.Model model : addLayers.getSkins()) {
            EntityRenderer entityRenderer = addLayers.getSkin(model);
            if (!(entityRenderer instanceof PlayerRenderer)) continue;
            PlayerRenderer playerRenderer = (PlayerRenderer)entityRenderer;
            playerRenderer.addLayer((RenderLayer)new DiseaseMarksLayer((RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)playerRenderer));
        }
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, AbstractClientPlayer abstractClientPlayer, float f, float f2, float f3, float f4, float f5, float f6) {
        int n2;
        int n3;
        int n4;
        boolean bl;
        if (abstractClientPlayer.isInvisible()) {
            return;
        }
        int n5 = FormSync.clientForm((Entity)abstractClientPlayer);
        boolean bl2 = FormSync.tumors(n5);
        boolean bl3 = FormSync.ulcers(n5);
        boolean bl4 = bl = FormSync.dotaStage(n5) == 4;
        if (!(bl2 || bl3 || bl)) {
            return;
        }
        PlayerModel playerModel = (PlayerModel)this.getParentModel();
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull((ResourceLocation)TEX));
        Random random = new Random(abstractClientPlayer.getUUID().getLeastSignificantBits() ^ 0x5DEECE66DL);
        if (bl) {
            poseStack.pushPose();
            playerModel.head.translateAndRotate(poseStack);
            n4 = -1007440;
            n3 = -9819594;
            n2 = -2062184;
            DiseaseMarksLayer.box(poseStack, vertexConsumer, n, -2.5f, -4.0f, -5.6f, 2.5f, -1.0f, -4.0f, n4);
            DiseaseMarksLayer.box(poseStack, vertexConsumer, n, -1.8f, -3.2f, -5.7f, -0.6f, -2.0f, -5.5f, n3);
            DiseaseMarksLayer.box(poseStack, vertexConsumer, n, 0.6f, -3.2f, -5.7f, 1.8f, -2.0f, -5.5f, n3);
            DiseaseMarksLayer.box(poseStack, vertexConsumer, n, -4.4f, -9.6f, -2.0f, -2.2f, -8.0f, 0.0f, n2);
            DiseaseMarksLayer.box(poseStack, vertexConsumer, n, 2.2f, -9.6f, -2.0f, 4.4f, -8.0f, 0.0f, n2);
            poseStack.popPose();
        }
        if (bl2) {
            n4 = -14937578;
            n3 = -12968412;
            this.bump(poseStack, vertexConsumer, n, playerModel.body, -2.6f, 0.6f, -2.0f, 2.2f, 1.4f, n3, n4, 0);
            this.bump(poseStack, vertexConsumer, n, playerModel.body, 1.0f, 0.4f, -2.0f, 1.8f, 1.3f, n3, n4, 0);
            this.bump(poseStack, vertexConsumer, n, playerModel.body, -1.0f, 0.5f, 2.0f, 2.0f, 1.2f, n3, n4, 1);
            this.bump(poseStack, vertexConsumer, n, playerModel.body, -4.0f, 2.0f, -0.8f, 1.8f, 1.3f, n3, n4, 2);
            this.bump(poseStack, vertexConsumer, n, playerModel.body, 4.0f, 2.4f, -0.6f, 1.8f, 1.3f, n3, n4, 3);
            this.bump(poseStack, vertexConsumer, n, playerModel.rightLeg, -0.6f, 0.4f, -2.0f, 1.8f, 1.2f, n3, n4, 0);
            this.bump(poseStack, vertexConsumer, n, playerModel.leftLeg, -1.2f, 0.6f, -2.0f, 1.6f, 1.2f, n3, n4, 0);
            for (n2 = 0; n2 < 12; ++n2) {
                this.spot(poseStack, vertexConsumer, n, (PlayerModel<AbstractClientPlayer>)playerModel, random, 0.6f + random.nextFloat() * 0.6f, -15395563, 0);
            }
        }
        if (bl3) {
            for (n4 = 0; n4 < 16; ++n4) {
                this.spot(poseStack, vertexConsumer, n, (PlayerModel<AbstractClientPlayer>)playerModel, random, 1.0f + random.nextFloat() * 1.2f, -16119286, -10875372);
            }
        }
    }

    private ModelPart part(PlayerModel<AbstractClientPlayer> playerModel, int n) {
        return switch (n) {
            case 0 -> playerModel.head;
            case 1, 2 -> playerModel.body;
            case 3 -> playerModel.rightArm;
            case 4 -> playerModel.leftArm;
            case 5 -> playerModel.rightLeg;
            default -> playerModel.leftLeg;
        };
    }

    private void spot(PoseStack poseStack, VertexConsumer vertexConsumer, int n, PlayerModel<AbstractClientPlayer> playerModel, Random random, float f, int n2, int n3) {
        int n4 = random.nextInt(BOXES.length);
        float[] fArray = BOXES[n4];
        int n5 = n4 <= 2 ? random.nextInt(4) : random.nextInt(2);
        float f2 = n4 == 0 ? 0.6f : 0.32f;
        float f3 = random.nextFloat();
        float f4 = random.nextFloat();
        ModelPart modelPart = this.part(playerModel, n4);
        if (!modelPart.visible) {
            return;
        }
        poseStack.pushPose();
        modelPart.translateAndRotate(poseStack);
        if (n3 != 0) {
            this.face(poseStack, vertexConsumer, n, fArray, n5, f3, f4, f + 0.6f, f2 - 0.04f, n3);
        }
        this.face(poseStack, vertexConsumer, n, fArray, n5, f3, f4, f, f2, n2);
        poseStack.popPose();
    }

    private void face(PoseStack poseStack, VertexConsumer vertexConsumer, int n, float[] fArray, int n2, float f, float f2, float f3, float f4, int n3) {
        float f5 = n2 < 2 ? fArray[3] - fArray[0] : fArray[5] - fArray[2];
        float f6 = fArray[4] - fArray[1];
        float f7 = (n2 < 2 ? fArray[0] : fArray[2]) + f * Math.max(0.0f, f5 - f3);
        float f8 = fArray[1] + f2 * Math.max(0.0f, f6 - f3);
        float f9 = f7 + Math.min(f3, f5);
        float f10 = f8 + Math.min(f3, f6);
        switch (n2) {
            case 0: {
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f7, f8, fArray[2] - f4, f9, f10, fArray[2] - f4 + 0.05f, n3);
                break;
            }
            case 1: {
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f7, f8, fArray[5] + f4 - 0.05f, f9, f10, fArray[5] + f4, n3);
                break;
            }
            case 2: {
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, fArray[0] - f4, f8, f7, fArray[0] - f4 + 0.05f, f10, f9, n3);
                break;
            }
            default: {
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, fArray[3] + f4 - 0.05f, f8, f7, fArray[3] + f4, f10, f9, n3);
            }
        }
    }

    private void bump(PoseStack poseStack, VertexConsumer vertexConsumer, int n, ModelPart modelPart, float f, float f2, float f3, float f4, float f5, int n2, int n3, int n4) {
        if (!modelPart.visible) {
            return;
        }
        poseStack.pushPose();
        modelPart.translateAndRotate(poseStack);
        float f6 = f4 / 2.0f;
        float f7 = 0.3f + f5;
        switch (n4) {
            case 0: {
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f - f6, f2 - f6, f3 - f7, f + f6, f2 + f6, f3, n2);
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f - f6 * 0.6f, f2 - f6 * 0.6f, f3 - f7 - 0.3f, f + f6 * 0.6f, f2 + f6 * 0.6f, f3 - f7 + 0.1f, n3);
                break;
            }
            case 1: {
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f - f6, f2 - f6, f3, f + f6, f2 + f6, f3 + f7, n2);
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f - f6 * 0.6f, f2 - f6 * 0.6f, f3 + f7 - 0.1f, f + f6 * 0.6f, f2 + f6 * 0.6f, f3 + f7 + 0.3f, n3);
                break;
            }
            case 2: {
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f - f7, f2 - f6, f3 - f6, f, f2 + f6, f3 + f6, n2);
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f - f7 - 0.3f, f2 - f6 * 0.6f, f3 - f6 * 0.6f, f - f7 + 0.1f, f2 + f6 * 0.6f, f3 + f6 * 0.6f, n3);
                break;
            }
            default: {
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f, f2 - f6, f3 - f6, f + f7, f2 + f6, f3 + f6, n2);
                DiseaseMarksLayer.box(poseStack, vertexConsumer, n, f + f7 - 0.1f, f2 - f6 * 0.6f, f3 - f6 * 0.6f, f + f7 + 0.3f, f2 + f6 * 0.6f, f3 + f6 * 0.6f, n3);
            }
        }
        poseStack.popPose();
    }

    private static void box(PoseStack poseStack, VertexConsumer vertexConsumer, int n, float f, float f2, float f3, float f4, float f5, float f6, int n2) {
        PoseStack.Pose pose = poseStack.last();
        DiseaseMarksLayer.quad(vertexConsumer, pose, n, n2, f, f2, f3 *= 0.0625f, f4 *= 0.0625f, f2 *= 0.0625f, f3, f4, f5 *= 0.0625f, f3, f *= 0.0625f, f5, f3, 0.0f, 0.0f, -1.0f);
        DiseaseMarksLayer.quad(vertexConsumer, pose, n, n2, f4, f2, f6 *= 0.0625f, f, f2, f6, f, f5, f6, f4, f5, f6, 0.0f, 0.0f, 1.0f);
        DiseaseMarksLayer.quad(vertexConsumer, pose, n, n2, f, f2, f6, f, f2, f3, f, f5, f3, f, f5, f6, -1.0f, 0.0f, 0.0f);
        DiseaseMarksLayer.quad(vertexConsumer, pose, n, n2, f4, f2, f3, f4, f2, f6, f4, f5, f6, f4, f5, f3, 1.0f, 0.0f, 0.0f);
        DiseaseMarksLayer.quad(vertexConsumer, pose, n, n2, f, f2, f6, f4, f2, f6, f4, f2, f3, f, f2, f3, 0.0f, -1.0f, 0.0f);
        DiseaseMarksLayer.quad(vertexConsumer, pose, n, n2, f, f5, f3, f4, f5, f3, f4, f5, f6, f, f5, f6, 0.0f, 1.0f, 0.0f);
    }

    private static void quad(VertexConsumer vertexConsumer, PoseStack.Pose pose, int n, int n2, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15) {
        DiseaseMarksLayer.vertex(vertexConsumer, pose, n, n2, f, f2, f3, 0.0f, 0.0f, f13, f14, f15);
        DiseaseMarksLayer.vertex(vertexConsumer, pose, n, n2, f4, f5, f6, 1.0f, 0.0f, f13, f14, f15);
        DiseaseMarksLayer.vertex(vertexConsumer, pose, n, n2, f7, f8, f9, 1.0f, 1.0f, f13, f14, f15);
        DiseaseMarksLayer.vertex(vertexConsumer, pose, n, n2, f10, f11, f12, 0.0f, 1.0f, f13, f14, f15);
    }

    private static void vertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, int n, int n2, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        vertexConsumer.addVertex(pose, f, f2, f3).setColor(n2).setUv(f4, f5).setOverlay(655360).setLight(n).setNormal(pose, f6, f7, f8);
    }
}
