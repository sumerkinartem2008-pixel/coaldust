/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
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
package com.coaldust.ideology;

import com.coaldust.client.IdeologyScreen;
import com.coaldust.ideology.BloodKind;
import com.coaldust.ideology.Ideology;
import com.coaldust.ideology.IdeologyManager;
import com.coaldust.ideology.Quest;
import com.coaldust.ideology.Trait;
import com.coaldust.ideology.TraitEffects;
import com.coaldust.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public final class IdeologyItems {
    private IdeologyItems() {
    }

    public static class Scourge
    extends Item {
        public Scourge(Item.Properties properties) {
            super(properties);
        }

        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
            ItemStack itemStack = player.getItemInHand(interactionHand);
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                if (!IdeologyManager.has(serverPlayer, Trait.FLAGELLANTISM)) {
                    serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.msg.need_trait", (Object[])new Object[]{Component.translatable((String)"ideology.coaldust.trait.flagellantism")}), true);
                    return InteractionResultHolder.fail((Object)itemStack);
                }
                serverPlayer.hurt(serverPlayer.damageSources().generic(), 2.0f);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DAMAGE_RESISTANCE, 0, 600);
                TraitEffects.give((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 0, 600);
                serverPlayer.getCooldowns().addCooldown((Item)this, 1200);
                IdeologyManager.progress(serverPlayer, Quest.SCOURGE_USE, 1);
            }
            return InteractionResultHolder.sidedSuccess((Object)itemStack, (boolean)level.isClientSide());
        }
    }

    public static class BloodBottle
    extends Item {
        private final BloodKind kind;

        public BloodBottle(BloodKind bloodKind, Item.Properties properties) {
            super(properties);
            this.kind = bloodKind;
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

        public boolean isFoil(ItemStack itemStack) {
            return this.kind.tier >= 2;
        }

                public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                if (IdeologyManager.has(serverPlayer, Trait.BLOODEATING)) {
                    Ideology ideology = IdeologyManager.of(serverPlayer);
                    this.kind.drinkMember(serverPlayer, ideology.level >= ideology.strongLevel() ? 1 : 0);
                    TraitEffects.markBlood(serverPlayer);
                    IdeologyManager.progress(serverPlayer, Quest.BLOOD_DRINK, 1);
                } else {
                    this.kind.drinkStranger(serverPlayer);
                }
            }
            if (livingEntity instanceof Player creative && creative.getAbilities().instabuild) {
                return itemStack;
            }
            itemStack.shrink(1);
            if (itemStack.isEmpty()) {
                return new ItemStack((ItemLike)Items.GLASS_BOTTLE);
            }
            if (livingEntity instanceof Player holder) {
                ItemStack bottle = new ItemStack((ItemLike)Items.GLASS_BOTTLE);
                if (!holder.getInventory().add(bottle)) {
                    holder.drop(bottle, false);
                }
            }
            return itemStack;
        }
    }

    public static class BloodLancet
    extends Item {
        public BloodLancet(Item.Properties properties) {
            super(properties);
        }

        public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
            if (!(player instanceof ServerPlayer)) {
                return InteractionResult.SUCCESS;
            }
            ServerPlayer serverPlayer = (ServerPlayer)player;
            if (livingEntity instanceof Player) {
                return InteractionResult.PASS;
            }
            return this.draw(itemStack, serverPlayer, livingEntity) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }

                public boolean draw(ItemStack itemStack, ServerPlayer serverPlayer, LivingEntity livingEntity) {
            if (!IdeologyManager.has(serverPlayer, Trait.BLOODEATING)) {
                serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.msg.need_trait", (Object[])new Object[]{Component.translatable((String)"ideology.coaldust.trait.bloodeating")}), true);
                return false;
            }
            if (serverPlayer.getCooldowns().isOnCooldown((Item)this) || !livingEntity.isAlive()) {
                return false;
            }
            BloodKind bloodKind = BloodKind.of(livingEntity);
            livingEntity.hurt(serverPlayer.damageSources().playerAttack((Player)serverPlayer), bloodKind.tier >= 2 ? 6.0f : 2.0f);
            if (livingEntity instanceof Mob targetMob) {
                targetMob.setTarget((LivingEntity)serverPlayer);
            }
            ItemStack bloodStack = new ItemStack((ItemLike)ModItems.BLOODS.get((Object)bloodKind).get());
            if (!serverPlayer.getInventory().add(bloodStack)) {
                serverPlayer.drop(bloodStack, false);
            }
            serverPlayer.getCooldowns().addCooldown((Item)this, bloodKind.cooldown);
            serverPlayer.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH, SoundSource.PLAYERS, 1.0f, 0.8f);
            if (bloodKind.tier >= 1 && serverPlayer.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles((ParticleOptions)ParticleTypes.DAMAGE_INDICATOR, livingEntity.getX(), livingEntity.getY() + (double)livingEntity.getBbHeight() * 0.6, livingEntity.getZ(), 6 * bloodKind.tier, 0.3, 0.3, 0.3, 0.1);
            }
            IdeologyManager.progress(serverPlayer, Quest.BLOOD_COLLECT, 1);
            if (bloodKind.tier == 1) {
                IdeologyManager.progress(serverPlayer, Quest.RARE_BLOOD, 1);
            }
            if (bloodKind.tier == 2) {
                IdeologyManager.progress(serverPlayer, Quest.RARE_BLOOD, 1);
                IdeologyManager.progress(serverPlayer, Quest.BOSS_BLOOD, 1);
            }
            return true;
        }
    }

    public static class Manifest
    extends Item {
        public Manifest(Item.Properties properties) {
            super(properties);
        }

        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
            if (level.isClientSide()) {
                IdeologyScreen.open();
            }
            return InteractionResultHolder.sidedSuccess((Object)player.getItemInHand(interactionHand), (boolean)level.isClientSide());
        }
    }
}
