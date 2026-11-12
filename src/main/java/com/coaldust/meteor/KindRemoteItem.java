/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 */
package com.coaldust.meteor;

import com.coaldust.client.KindRemoteScreen;
import com.coaldust.meteor.MeteorKind;
import com.coaldust.meteor.MeteorManager;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class KindRemoteItem
extends Item {
    private static final String KEY = "meteor_kind";
    public static final String COMMAND = "coaldust_remote_kind";

    public KindRemoteItem(Item.Properties properties) {
        super(properties);
    }

    public static MeteorKind getKind(ItemStack itemStack) {
        CompoundTag compoundTag = ((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY)).copyTag();
        return compoundTag.contains(KEY) ? MeteorKind.byIndex(compoundTag.getInt(KEY)) : MeteorKind.RANDOM;
    }

    public static void setKind(ItemStack itemStack, MeteorKind meteorKind) {
        CompoundTag compoundTag = ((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY)).copyTag();
        compoundTag.putInt(KEY, meteorKind.ordinal());
        itemStack.set(DataComponents.CUSTOM_DATA, (Object)CustomData.of((CompoundTag)compoundTag));
    }

    public static void registerCommand(RegisterCommandsEvent registerCommandsEvent) {
        registerCommandsEvent.getDispatcher().register((LiteralArgumentBuilder)Commands.literal((String)COMMAND).then(Commands.argument((String)"hand", (ArgumentType)IntegerArgumentType.integer((int)0, (int)1)).then(Commands.argument((String)"kind", (ArgumentType)IntegerArgumentType.integer((int)0, (int)63)).executes(commandContext -> {
            InteractionHand interactionHand;
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            ItemStack itemStack = serverPlayer.getItemInHand(interactionHand = IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"hand") == 1 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (!(itemStack.getItem() instanceof KindRemoteItem)) {
                return 0;
            }
            MeteorKind meteorKind = MeteorKind.byIndex(IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"kind"));
            KindRemoteItem.setKind(itemStack, meteorKind);
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"message.coaldust.remote_kind", (Object[])new Object[]{Component.translatable((String)("meteor.coaldust.kind." + meteorKind.id))}), true);
            return 1;
        }))));
    }
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                KindRemoteScreen.open(hand, KindRemoteItem.getKind(stack));
            }
            return InteractionResultHolder.sidedSuccess((Object)stack, (boolean)level.isClientSide());
        }
        if (level instanceof ServerLevel serverLevel) {
            HitResult hit = player.pick(160.0, 1.0f, false);
            BlockPos target;
            if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
                target = blockHit.getBlockPos().above();
            } else {
                target = BlockPos.containing(hit.getLocation().x, hit.getLocation().y, hit.getLocation().z);
            }
            MeteorKind kind = KindRemoteItem.getKind(stack);
            if (kind == MeteorKind.RANDOM) {
                MeteorKind[] all = MeteorKind.values();
                kind = all[serverLevel.getRandom().nextInt(all.length - 1)];
            }
            if (MeteorManager.launch(serverLevel, target, kind.type, kind.radius(serverLevel.getRandom()), kind.variant)) {
                player.displayClientMessage((Component)Component.translatable((String)"message.coaldust.remote_launch", (Object[])new Object[]{Component.translatable((String)("meteor.coaldust.kind." + kind.id))}), true);
            }
        }
        return InteractionResultHolder.sidedSuccess((Object)stack, (boolean)level.isClientSide());
    }

    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add((Component)Component.translatable((String)"tooltip.coaldust.remote_kind", (Object[])new Object[]{Component.translatable((String)("meteor.coaldust.kind." + KindRemoteItem.getKind((ItemStack)itemStack).id))}).withStyle(ChatFormatting.GOLD));
        list.add((Component)Component.translatable((String)"tooltip.coaldust.remote_kind_hint").withStyle(ChatFormatting.GRAY));
    }
}
