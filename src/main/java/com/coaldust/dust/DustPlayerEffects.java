/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.player.Player
 */
package com.coaldust.dust;

import com.coaldust.block.CoalDustBlock;
import com.coaldust.effect.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public final class DustPlayerEffects {
    public static void tick(ServerPlayer serverPlayer) {
        if (serverPlayer.isSpectator() || serverPlayer.tickCount % 10 != 0) {
            return;
        }
        int n = CoalDustBlock.densityAround((Player)serverPlayer);
        if (n < 0) {
            return;
        }
        serverPlayer.addEffect(new MobEffectInstance(ModEffects.IN_DUST, 30, n, false, false, true));
        if (n >= 2) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false, true));
        }
    }

    private DustPlayerEffects() {
    }
}
