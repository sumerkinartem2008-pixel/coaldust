/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package com.coaldust.client;

import com.coaldust.meteor.MeteorRemoteItem;
import com.coaldust.meteor.MeteorType;
import com.coaldust.meteor.RemoteSizePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class RemoteScreen
extends Screen {
    private static final MeteorType.Size[] ORDER = new MeteorType.Size[]{MeteorType.Size.TINY, MeteorType.Size.SMALL, MeteorType.Size.MEDIUM, MeteorType.Size.LARGE, MeteorType.Size.HUGE, MeteorType.Size.GIANT, MeteorType.Size.COLOSSAL, MeteorType.Size.TITANIC, MeteorType.Size.RANDOM};
    private final InteractionHand hand;
    private MeteorType.Size current;

    public RemoteScreen(InteractionHand interactionHand, MeteorType.Size size) {
        super((Component)Component.translatable((String)"screen.coaldust.remote"));
        this.hand = interactionHand;
        this.current = size;
    }

    public static void open(InteractionHand interactionHand, MeteorType.Size size) {
        Minecraft.getInstance().setScreen((Screen)new RemoteScreen(interactionHand, size));
    }

    protected void init() {
        int n = 150;
        int n2 = 20;
        int n3 = 21;
        int n4 = this.width / 2 - n / 2;
        int n5 = this.height / 2 - (ORDER.length + 1) * n3 / 2 + 6;
        for (int i = 0; i < ORDER.length; ++i) {
            MeteorType.Size size = ORDER[i];
            MutableComponent mutableComponent = Component.translatable((String)("meteor.coaldust.size." + size.id));
            if (size == this.current) {
                mutableComponent = Component.translatable((String)"screen.coaldust.remote.selected", (Object[])new Object[]{mutableComponent});
            }
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)mutableComponent, button -> this.choose(size)).bounds(n4, n5 + i * n3, n, n2).build());
        }
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.done"), button -> this.onClose()).bounds(n4, n5 + ORDER.length * n3 + 6, n, n2).build());
    }

    private void choose(MeteorType.Size size) {
        ItemStack itemStack;
        this.current = size;
        int n = this.hand == InteractionHand.OFF_HAND ? 1 : 0;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && (itemStack = minecraft.player.getItemInHand(this.hand)).getItem() instanceof MeteorRemoteItem) {
            MeteorRemoteItem.setSize(itemStack, size);
        }
        PacketDistributor.sendToServer((CustomPacketPayload)new RemoteSizePayload(n, size.ordinal()), (CustomPacketPayload[])new CustomPacketPayload[0]);
        if (minecraft.getConnection() != null) {
            minecraft.getConnection().sendCommand("coaldust_remote_size " + n + " " + size.ordinal());
        }
        this.rebuildWidgets();
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.render(guiGraphics, n, n2, f);
        int n3 = this.height / 2 - (ORDER.length + 1) * 21 / 2 + 6;
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, n3 - 30, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"screen.coaldust.remote.current", (Object[])new Object[]{Component.translatable((String)("meteor.coaldust.size." + this.current.id))}), this.width / 2, n3 - 16, 16765514);
    }

    public boolean isPauseScreen() {
        return false;
    }
}
