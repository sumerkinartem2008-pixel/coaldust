/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ItemUtils
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 */
package com.coaldust.item;

import com.coaldust.disease.Disease;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class DiseaseFlaskItem
extends Item {
    private final Disease disease;
    private final boolean catalyst;

    public DiseaseFlaskItem(Disease disease, boolean bl, Item.Properties properties) {
        super(properties);
        this.disease = disease;
        this.catalyst = bl;
    }

    public boolean isCatalyst() {
        return this.catalyst;
    }

    public Disease getDisease() {
        return this.disease;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        return ItemUtils.startUsingInstantly((Level)level, (Player)player, (InteractionHand)interactionHand);
    }

    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.DRINK;
    }

    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 32;
    }

    
    public ItemStack finishUsingItem(ItemStack var1_1, Level var2_2, LivingEntity var3_3) {
               if (var3_3 instanceof ServerPlayer var4_4) {
            if (!this.catalyst) {
                this.disease.infect(var4_4);
            } else if (!this.disease.advance(var4_4)) {
                var4_4.displayClientMessage((Component)Component.translatable((String)"message.coaldust.catalyst.max"), true);
            }
        }
        boolean var4_5 = var3_3 instanceof Player player && player.getAbilities().instabuild;
        if (var4_5) {
            return var1_1;
        }
        var1_1.shrink(1);
        if (var1_1.isEmpty()) {
            return new ItemStack((ItemLike)Items.GLASS_BOTTLE);
        }
        if (var3_3 instanceof Player var5_6) {
            ItemStack var6_7 = new ItemStack((ItemLike)Items.GLASS_BOTTLE);
            if (!var5_6.getInventory().add(var6_7)) {
                var5_6.drop(var6_7, false);
            }
        }
        return var1_1;
    }
}