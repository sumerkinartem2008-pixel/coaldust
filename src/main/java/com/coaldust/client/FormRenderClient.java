/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.animal.Pig
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.neoforge.client.event.RenderPlayerEvent$Pre
 *  net.neoforged.neoforge.common.NeoForge
 */
package com.coaldust.client;

import com.coaldust.network.FormSync;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class FormRenderClient {
    private static final Map<Integer, Pig> PIGS = new HashMap<Integer, Pig>();
    private static final Map<Integer, Integer> LAST_TICK = new HashMap<Integer, Integer>();

    public static void init() {
        NeoForge.EVENT_BUS.addListener(pre -> FormRenderClient.onRenderPlayer(pre));
    }

    private static void onRenderPlayer(RenderPlayerEvent.Pre pre) {
        Player player = pre.getEntity();
        int n = FormSync.clientForm((Entity)player);
        if (FormSync.isPigForm(n)) {
            FormRenderClient.renderPig(pre, player);
            pre.setCanceled(true);
            return;
        }
        PIGS.remove(player.getId());
    }

    private static void renderPig(RenderPlayerEvent.Pre pre, Player player) {
        int n = player.getId();
        Pig pig = PIGS.get(n);
        if (pig == null || pig.level() != player.level()) {
            pig = new Pig(EntityType.PIG, player.level());
            PIGS.put(n, pig);
        }
        pig.yBodyRot = player.yBodyRot;
        pig.yBodyRotO = player.yBodyRotO;
        pig.yHeadRot = player.yHeadRot;
        pig.yHeadRotO = player.yHeadRotO;
        pig.setXRot(player.getXRot());
        pig.xRotO = player.xRotO;
        pig.tickCount = player.tickCount;
        Integer n2 = LAST_TICK.get(n);
        if (n2 == null || n2 != player.tickCount) {
            LAST_TICK.put(n, player.tickCount);
            pig.walkAnimation.update(player.walkAnimation.speed(), 1.0f);
        }
        Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)pig).render((Entity)pig, player.yBodyRot, pre.getPartialTick(), pre.getPoseStack(), pre.getMultiBufferSource(), pre.getPackedLight());
    }

    private FormRenderClient() {
    }
}
