/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 */
package com.coaldust.disease;

import com.coaldust.block.ModBlocks;
import com.coaldust.block.StainBlock;
import com.coaldust.disease.Disease;
import com.coaldust.disease.Symptoms;
import com.coaldust.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Dysentery
extends Disease {
    public static final Dysentery INSTANCE = new Dysentery();

    private Dysentery() {
        super("dysentery", 4);
    }

    @Override
    protected int stageDuration(int n) {
        return 24000;
    }

    @Override
    protected Holder<MobEffect> indicator() {
        return ModEffects.DYSENTERY;
    }

    @Override
    protected void applyEffects(ServerPlayer serverPlayer, int n) {
        if (n >= 4) {
            serverPlayer.addEffect(Dysentery.effect(ModEffects.WASTING, 0));
        }
    }

    @Override
    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
        Symptoms.trail(serverPlayer, compoundTag, "coaldust_dysentery_trail", n >= 3 ? (StainBlock)((Object)ModBlocks.BLOOD_STAIN.get()) : (StainBlock)((Object)ModBlocks.DIARRHEA_STAIN.get()));
        if (n >= 2) {
            Symptoms.hunger(serverPlayer);
        }
        if (n >= 4 && serverPlayer.tickCount % 120 == 0) {
            this.hurt(serverPlayer, 1.0f);
        }
    }

    public void onAte(ServerPlayer serverPlayer, ItemStack itemStack) {
        if (itemStack.is(Items.CHICKEN)) {
            this.rollInfection(serverPlayer, 0.06f);
        } else if (itemStack.is(Items.BEEF) || itemStack.is(Items.PORKCHOP) || itemStack.is(Items.MUTTON) || itemStack.is(Items.RABBIT) || itemStack.is(Items.COD) || itemStack.is(Items.SALMON)) {
            this.rollInfection(serverPlayer, 0.03f);
        }
    }
}
