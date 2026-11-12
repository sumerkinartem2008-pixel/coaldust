/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.neoforge.client.event.ViewportEvent$ComputeFogColor
 *  net.neoforged.neoforge.client.event.ViewportEvent$RenderFog
 *  net.neoforged.neoforge.common.NeoForge
 */
package com.coaldust.client;

import com.coaldust.block.CoalDustBlock;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class DustFogClient {
    private static final float[] FAR = new float[]{16.0f, 5.0f, 2.0f};
    private static final float[] NEAR = new float[]{0.0f, -2.0f, -4.0f};

    public static void init() {
        NeoForge.EVENT_BUS.addListener(renderFog -> DustFogClient.onRenderFog(renderFog));
        NeoForge.EVENT_BUS.addListener(computeFogColor -> DustFogClient.onFogColor(computeFogColor));
    }

    private static int density(Camera camera) {
        Entity entity = camera.getEntity();
        if (entity == null) {
            return -1;
        }
        return CoalDustBlock.densityAt(entity.level(), camera.getBlockPosition());
    }

    private static void onRenderFog(ViewportEvent.RenderFog renderFog) {
        int n = DustFogClient.density(renderFog.getCamera());
        if (n < 0) {
            return;
        }
        renderFog.setNearPlaneDistance(NEAR[n]);
        renderFog.setFarPlaneDistance(FAR[n]);
        renderFog.setCanceled(true);
    }

    private static void onFogColor(ViewportEvent.ComputeFogColor computeFogColor) {
        int n = DustFogClient.density(computeFogColor.getCamera());
        if (n < 0) {
            return;
        }
        float f = 0.16f - (float)n * 0.05f;
        computeFogColor.setRed(f);
        computeFogColor.setGreen(f);
        computeFogColor.setBlue(f + 0.01f);
    }

    private DustFogClient() {
    }
}
