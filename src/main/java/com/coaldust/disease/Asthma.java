/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffect
 */
package com.coaldust.disease;

import com.coaldust.disease.Disease;
import com.coaldust.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;

public class Asthma
extends Disease {
    public static final Asthma INSTANCE = new Asthma();
    public static final int STAGE_DURATION = 36000;
    public static final int ATTACK_INTERVAL_MIN = 200;
    public static final int ATTACK_INTERVAL_MAX = 1200;
    public static final int ATTACK_DURATION = 40;
    private static final String KEY_NEXT_ATTACK = "coaldust_asthma_next_attack";
    private static final String KEY_ATTACK_LEFT = "coaldust_asthma_attack_left";

    private Asthma() {
        super("asthma", 2);
    }

    @Override
    protected int stageDuration(int n) {
        return 36000;
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.ASTHMA;
    }

    @Override
    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
        int n2 = compoundTag.getInt(KEY_ATTACK_LEFT);
        if (n2 > 0) {
            compoundTag.putInt(KEY_ATTACK_LEFT, --n2);
            if (n2 == 20 || n2 == 0) {
                float f = n >= 2 ? 2.0f : 1.0f;
                this.hurt(serverPlayer, f / 2.0f);
            }
            return;
        }
        int n3 = compoundTag.getInt(KEY_NEXT_ATTACK);
        if (n3 <= 0) {
            compoundTag.putInt(KEY_NEXT_ATTACK, Asthma.randomInterval(serverPlayer));
            return;
        }
        if (--n3 <= 0) {
            this.startAttack(serverPlayer, compoundTag);
        }
        compoundTag.putInt(KEY_NEXT_ATTACK, n3);
    }

    private void startAttack(ServerPlayer serverPlayer, CompoundTag compoundTag) {
        compoundTag.putInt(KEY_ATTACK_LEFT, 40);
        serverPlayer.addEffect(Asthma.effect(ModEffects.ASTHMA_ATTACK, 0, 40));
    }

    private static int randomInterval(ServerPlayer serverPlayer) {
        return 200 + serverPlayer.getRandom().nextInt(1001);
    }
}
