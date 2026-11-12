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
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 */
package com.coaldust.client;

import com.coaldust.meteor.KindRemoteItem;
import com.coaldust.meteor.MeteorKind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class KindRemoteScreen
extends Screen {
    private static final MeteorKind[][] COLUMNS = new MeteorKind[][]{{MeteorKind.RESOURCE_SMALL, MeteorKind.RESOURCE_MEDIUM, MeteorKind.RESOURCE_LARGE, MeteorKind.PLAIN, MeteorKind.ICE, MeteorKind.LIVING}, {MeteorKind.SHRINE, MeteorKind.GALLOWS, MeteorKind.TEMPLE, MeteorKind.WARLOCK_TOWER, MeteorKind.MONASTERY, MeteorKind.NECROPOLIS_TOWN, MeteorKind.VAMPIRE_CASTLE, MeteorKind.CURSED_VILLAGE, MeteorKind.CITADEL, MeteorKind.CATHEDRAL}, {MeteorKind.DWARF_TOWN, MeteorKind.DWARF_HOLD, MeteorKind.DEEP_KINGDOM}, {MeteorKind.DWARF_CITY, MeteorKind.NECROPOLIS, MeteorKind.ABYSS, MeteorKind.NEST, MeteorKind.FORGE, MeteorKind.METROPOLIS, MeteorKind.CANYON, MeteorKind.SPIRES}};
    private static final String[] HEADERS = new String[]{"screen.coaldust.remote_kind.meteors", "screen.coaldust.remote_kind.ancient", "screen.coaldust.remote_kind.dwarf", "screen.coaldust.remote_kind.underground"};
    private static final int W = 116;
    private static final int H = 16;
    private static final int GAP = 18;
    private static final int COL = 122;
    private static final int ROWS = 10;
    private final InteractionHand hand;
    private MeteorKind current;

    public KindRemoteScreen(InteractionHand interactionHand, MeteorKind meteorKind) {
        super((Component)Component.translatable((String)"screen.coaldust.remote_kind"));
        this.hand = interactionHand;
        this.current = meteorKind;
    }

    public static void open(InteractionHand interactionHand, MeteorKind meteorKind) {
        Minecraft.getInstance().setScreen((Screen)new KindRemoteScreen(interactionHand, meteorKind));
    }

    private int left() {
        return this.width / 2 - (122 * COLUMNS.length - 6) / 2;
    }

    private int top() {
        return Math.max(44, this.height / 2 - 105 + 14);
    }

    protected void init() {
        int n;
        for (n = 0; n < COLUMNS.length; ++n) {
            for (int i = 0; i < COLUMNS[n].length; ++i) {
                MeteorKind meteorKind = COLUMNS[n][i];
                this.addRenderableWidget((GuiEventListener)Button.builder((Component)this.label(meteorKind), button -> this.choose(meteorKind)).bounds(this.left() + n * 122, this.top() + i * 18, 116, 16).build());
            }
        }
        n = this.top() + 180 + 4;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)this.label(MeteorKind.RANDOM), button -> this.choose(MeteorKind.RANDOM)).bounds(this.width / 2 - 116 - 4, n, 116, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.done"), button -> this.onClose()).bounds(this.width / 2 + 4, n, 116, 16).build());
    }

    private Component label(MeteorKind meteorKind) {
        MutableComponent mutableComponent = Component.translatable((String)("meteor.coaldust.kind." + meteorKind.id));
        return meteorKind == this.current ? Component.translatable((String)"screen.coaldust.remote.selected", (Object[])new Object[]{mutableComponent}) : mutableComponent;
    }

    private void choose(MeteorKind meteorKind) {
        ItemStack itemStack;
        this.current = meteorKind;
        int n = this.hand == InteractionHand.OFF_HAND ? 1 : 0;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && (itemStack = minecraft.player.getItemInHand(this.hand)).getItem() instanceof KindRemoteItem) {
            KindRemoteItem.setKind(itemStack, meteorKind);
        }
        if (minecraft.getConnection() != null) {
            minecraft.getConnection().sendCommand("coaldust_remote_kind " + n + " " + meteorKind.ordinal());
        }
        this.rebuildWidgets();
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.render(guiGraphics, n, n2, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.top() - 38, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"screen.coaldust.remote.current", (Object[])new Object[]{Component.translatable((String)("meteor.coaldust.kind." + this.current.id))}), this.width / 2, this.top() - 26, 16765514);
        for (int i = 0; i < HEADERS.length; ++i) {
            guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)HEADERS[i]), this.left() + i * 122 + 58, this.top() - 12, 11575551);
        }
    }

    public boolean isPauseScreen() {
        return false;
    }
}
