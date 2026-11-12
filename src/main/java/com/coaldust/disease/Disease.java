/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodData
 */
package com.coaldust.disease;

import com.coaldust.CoalDust;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public abstract class Disease {
    public static final float SPONTANEOUS_CHANCE = 5.0E-4f;
    public static final int SPONTANEOUS_CHECK_INTERVAL = 1200;
    protected final String name;
    protected final int maxStage;
    protected final String stageKey;
    protected final String progressKey;
    protected final ResourceKey<DamageType> damageType;

    protected Disease(String string, int n) {
        this.name = string;
        this.maxStage = n;
        this.stageKey = "coaldust_" + string + "_stage";
        this.progressKey = "coaldust_" + string + "_progress";
        this.damageType = ResourceKey.create((ResourceKey)Registries.DAMAGE_TYPE, (ResourceLocation)CoalDust.id(string));
    }

    protected abstract int stageDuration(int var1);

    protected abstract Holder<MobEffect> indicator();

    protected boolean showIndicator(int n) {
        return true;
    }

    protected void onStageChanged(ServerPlayer serverPlayer, int n) {
    }

    protected void applyEffects(ServerPlayer serverPlayer, int n) {
    }

    protected void everyTick(ServerPlayer serverPlayer, CompoundTag compoundTag, int n) {
    }

    protected int progressRate(ServerPlayer serverPlayer) {
        return 1;
    }

    protected float spontaneousMultiplier(ServerPlayer serverPlayer) {
        return 1.0f;
    }

    protected boolean tryStart(ServerPlayer serverPlayer, CompoundTag compoundTag) {
        return serverPlayer.tickCount % 1200 == 0 && serverPlayer.getRandom().nextFloat() < 5.0E-4f * this.spontaneousMultiplier(serverPlayer);
    }

    public final String getName() {
        return this.name;
    }

    public final int getMaxStage() {
        return this.maxStage;
    }

    public final boolean advance(ServerPlayer serverPlayer) {
        int n = this.getStage(serverPlayer);
        if (n >= this.maxStage) {
            return false;
        }
        this.setStage(serverPlayer, n + 1);
        return true;
    }

    public final void infect(ServerPlayer serverPlayer) {
        if (this.getStage(serverPlayer) == 0) {
            this.setStage(serverPlayer, 1);
        }
    }

    public final int getStage(ServerPlayer serverPlayer) {
        return serverPlayer.getPersistentData().getInt(this.stageKey);
    }

    public final int getStageOf(Player player) {
        return player.getPersistentData().getInt(this.stageKey);
    }

    public final void setStage(ServerPlayer serverPlayer, int n) {
        CompoundTag compoundTag = serverPlayer.getPersistentData();
        compoundTag.putInt(this.stageKey, n);
        compoundTag.putInt(this.progressKey, 0);
        this.onStageChanged(serverPlayer, n);
        if (n > 0) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)("message.coaldust." + this.name + ".stage" + n)).withStyle(ChatFormatting.DARK_RED), true);
        }
    }

    public final void rollInfection(ServerPlayer serverPlayer, float f) {
        if (serverPlayer.isCreative() || serverPlayer.isSpectator()) {
            return;
        }
        if (this.getStage(serverPlayer) == 0 && serverPlayer.getRandom().nextFloat() < f) {
            this.setStage(serverPlayer, 1);
        }
    }

    public final void tick(ServerPlayer serverPlayer) {
        boolean bl;
        CompoundTag compoundTag = serverPlayer.getPersistentData();
        int n = Math.min(compoundTag.getInt(this.stageKey), this.maxStage);
        boolean bl2 = bl = !serverPlayer.isCreative() && !serverPlayer.isSpectator();
        if (n <= 0) {
            if (bl && this.tryStart(serverPlayer, compoundTag)) {
                this.setStage(serverPlayer, 1);
            }
            return;
        }
        if (bl && n < this.maxStage) {
            int n2 = compoundTag.getInt(this.progressKey) + this.progressRate(serverPlayer);
            if (n2 >= this.stageDuration(n)) {
                this.setStage(serverPlayer, ++n);
            } else {
                compoundTag.putInt(this.progressKey, n2);
            }
        }
        this.everyTick(serverPlayer, compoundTag, n);
        if (serverPlayer.tickCount % 20 == 0) {
            if (this.showIndicator(n)) {
                serverPlayer.addEffect(Disease.effect(this.indicator(), n - 1, 60));
            }
            this.applyEffects(serverPlayer, n);
        }
    }

    protected static MobEffectInstance effect(Holder<MobEffect> holder, int n) {
        return Disease.effect(holder, n, 60);
    }

    protected static MobEffectInstance effect(Holder<MobEffect> holder, int n, int n2) {
        return new MobEffectInstance(holder, n2, n, false, false, true);
    }

    protected final void hurt(ServerPlayer serverPlayer, float f) {
        DamageSource damageSource = new DamageSource((Holder)serverPlayer.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(this.damageType));
        serverPlayer.hurt(damageSource, f);
    }

    protected static void limitFoodLevel(ServerPlayer serverPlayer, CompoundTag compoundTag, String string, float f) {
        int n;
        FoodData foodData = serverPlayer.getFoodData();
        int n2 = foodData.getFoodLevel();
        if (compoundTag.contains(string) && n2 > (n = compoundTag.getInt(string))) {
            n2 = n + (int)Math.floor((float)(n2 - n) * f);
            foodData.setFoodLevel(n2);
        }
        compoundTag.putInt(string, n2);
    }

    protected static void limitSaturation(ServerPlayer serverPlayer, CompoundTag compoundTag, String string, float f) {
        float f2;
        FoodData foodData = serverPlayer.getFoodData();
        float f3 = foodData.getSaturationLevel();
        if (compoundTag.contains(string) && f3 > (f2 = compoundTag.getFloat(string))) {
            f3 = f2 + (f3 - f2) * f;
            foodData.setSaturation(f3);
        }
        compoundTag.putFloat(string, f3);
    }
}
