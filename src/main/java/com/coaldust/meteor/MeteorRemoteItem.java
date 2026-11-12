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

import com.coaldust.client.RemoteScreen;
import com.coaldust.meteor.MeteorManager;
import com.coaldust.meteor.MeteorType;
import com.coaldust.meteor.underground.UndergroundCity;
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

public class MeteorRemoteItem
extends Item {
    public static final double RANGE = 160.0;
    public static final int COOLDOWN = 0;
    private static final String KEY_SIZE = "meteor_size";
    public static final String COMMAND = "coaldust_remote_size";
    private final MeteorType type;

    public MeteorRemoteItem(MeteorType meteorType, Item.Properties properties) {
        super(properties);
        this.type = meteorType;
    }

    public static MeteorType.Size getSize(ItemStack itemStack) {
        CompoundTag compoundTag = ((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY)).copyTag();
        return MeteorType.Size.byIndex(compoundTag.getInt(KEY_SIZE));
    }

    public static void setSize(ItemStack itemStack, MeteorType.Size size) {
        CompoundTag compoundTag = ((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY)).copyTag();
        compoundTag.putInt(KEY_SIZE, size.ordinal());
        itemStack.set(DataComponents.CUSTOM_DATA, (Object)CustomData.of((CompoundTag)compoundTag));
    }

    public static void registerCommand(RegisterCommandsEvent registerCommandsEvent) {
        registerCommandsEvent.getDispatcher().register((LiteralArgumentBuilder)Commands.literal((String)COMMAND).then(Commands.argument((String)"hand", (ArgumentType)IntegerArgumentType.integer((int)0, (int)1)).then(Commands.argument((String)"size", (ArgumentType)IntegerArgumentType.integer((int)0, (int)15)).executes(commandContext -> {
            MeteorRemoteItem.applySelection((Player)((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"hand"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"size"));
            return 1;
        }))));
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public InteractionResultHolder<ItemStack> use(Level var1_1, Player var2_2, InteractionHand var3_3) {
        block5: {
            var4_4 = var2_2.getItemInHand(var3_3);
            if (var2_2.isShiftKeyDown()) {
                if (var1_1.isClientSide()) {
                    RemoteScreen.open(var3_3, MeteorRemoteItem.getSize(var4_4));
                }
                return InteractionResultHolder.sidedSuccess((Object)var4_4, (boolean)var1_1.isClientSide());
            }
            if (!(var1_1 instanceof ServerLevel)) break block5;
            var5_5 = (ServerLevel)var1_1;
            var6_6 = var2_2.pick(160.0, 1.0f, false);
            if (!(var6_6 instanceof BlockHitResult)) ** GOTO lbl-1000
            var8_7 /* !! */  = (BlockHitResult)var6_6;
            if (var6_6.getType() == HitResult.Type.BLOCK) {
                v0 = var8_7 /* !! */ .getBlockPos().above();
            } else lbl-1000:
            // 2 sources

            {
                v0 = BlockPos.containing((double)var6_6.getLocation().x, (double)var6_6.getLocation().y, (double)var6_6.getLocation().z);
            }
            var7_8 = v0;
            var8_7 /* !! */  = MeteorRemoteItem.getSize(var4_4);
            var9_9 = this.type != null ? this.type : MeteorType.resourceFor((MeteorType.Size)var8_7 /* !! */ , var5_5.getRandom());
            var10_10 = var9_9.rollRadius(var5_5.getRandom(), (MeteorType.Size)var8_7 /* !! */ );
            var12_11 = -1;
            if (var9_9 == MeteorType.UNDERGROUND) {
                var13_12 /* !! */  = var8_7 /* !! */  == MeteorType.Size.RANDOM ? MeteorType.Size.rollNatural(var5_5.getRandom()) : var8_7 /* !! */ ;
                var12_11 = var13_12 /* !! */  == MeteorType.Size.TITANIC ? UndergroundCity.rollBig(var5_5.getRandom()) : UndergroundCity.rollVariant(var5_5.getRandom(), false);
                var10_10 = UndergroundCity.radiusFor(var12_11);
            }
            MeteorManager.launch(var5_5, var7_8, var9_9, var10_10, var12_11);
        }
        return InteractionResultHolder.sidedSuccess((Object)var4_4, (boolean)var1_1.isClientSide());
    }

    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add((Component)Component.translatable((String)"tooltip.coaldust.remote_size", (Object[])new Object[]{Component.translatable((String)("meteor.coaldust.size." + MeteorRemoteItem.getSize((ItemStack)itemStack).id))}).withStyle(ChatFormatting.GOLD));
        list.add((Component)Component.translatable((String)"tooltip.coaldust.remote_hint").withStyle(ChatFormatting.GRAY));
    }

    public static void applySelection(Player player, int n, int n2) {
        InteractionHand interactionHand = n == 1 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (!(itemStack.getItem() instanceof MeteorRemoteItem)) {
            return;
        }
        MeteorType.Size size = MeteorType.Size.byIndex(n2);
        if (MeteorRemoteItem.getSize(itemStack) == size && itemStack.has(DataComponents.CUSTOM_DATA)) {
            return;
        }
        MeteorRemoteItem.setSize(itemStack, size);
        player.displayClientMessage((Component)Component.translatable((String)"message.coaldust.remote_size", (Object[])new Object[]{Component.translatable((String)("meteor.coaldust.size." + size.id))}), true);
    }
}
