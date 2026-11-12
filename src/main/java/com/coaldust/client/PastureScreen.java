/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package com.coaldust.client;

import com.coaldust.client.IdeologyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PastureScreen
extends Screen {
    private final Screen parent;
    private static boolean breed = true;
    private static boolean shear = true;
    private static boolean milk = true;
    private static boolean cull = false;
    private static int limit = 10;

    public PastureScreen(Screen screen) {
        super((Component)Component.translatable((String)"ideology.coaldust.pasture.title"));
        this.parent = screen;
    }

    private Component onOff(String string, boolean bl) {
        return Component.literal((String)((bl ? "\u2714 " : "\u2716 ") + Component.translatable((String)("ideology.coaldust.pasture." + string)).getString()));
    }

    protected void init() {
        int n = 220;
        int n2 = (this.width - n) / 2;
        int n3 = this.height / 2 - 60;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)this.onOff("breed", breed), button -> {
            breed = !breed;
            this.rebuildWidgets();
        }).bounds(n2, n3, n, 16).tooltip(Tooltip.create((Component)Component.translatable((String)"ideology.coaldust.pasture.breed.tip"))).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)this.onOff("shear", shear), button -> {
            shear = !shear;
            this.rebuildWidgets();
        }).bounds(n2, n3 + 19, n, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)this.onOff("milk", milk), button -> {
            milk = !milk;
            this.rebuildWidgets();
        }).bounds(n2, n3 + 38, n, 16).tooltip(Tooltip.create((Component)Component.translatable((String)"ideology.coaldust.pasture.milk.tip"))).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)this.onOff("cull", cull), button -> {
            cull = !cull;
            this.rebuildWidgets();
        }).bounds(n2, n3 + 57, n, 16).tooltip(Tooltip.create((Component)Component.translatable((String)"ideology.coaldust.pasture.cull.tip"))).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"\u2212"), button -> {
            limit = Math.max(2, limit - 1);
        }).bounds(n2 + 120, n3 + 78, 20, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"+"), button -> {
            limit = Math.min(40, limit + 1);
        }).bounds(n2 + 172, n3 + 78, 20, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.pasture.go"), button -> {
            String string = (breed ? "b" : "") + (shear ? "s" : "") + (milk ? "m" : "") + (cull ? "c" : "") + "_";
            IdeologyClient.send("ideology work pasture " + string + " " + limit);
            IdeologyClient.request();
            Minecraft.getInstance().setScreen(this.parent);
        }).bounds(n2, n3 + 104, 106, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.cancel"), button -> Minecraft.getInstance().setScreen(this.parent)).bounds(n2 + 114, n3 + 104, 106, 16).build());
    }

    public void renderBackground(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.renderBackground(guiGraphics, n, n2, f);
        int n3 = 236;
        int n4 = 150;
        int n5 = (this.width - n3) / 2;
        int n6 = this.height / 2 - 82;
        guiGraphics.fillGradient(n5, n6, n5 + n3, n6 + n4, -267122142, -267910128);
        guiGraphics.renderOutline(n5, n6, n3, n4, -6266656);
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.render(guiGraphics, n, n2, f);
        int n3 = (this.width - 220) / 2;
        int n4 = this.height / 2 - 60;
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, n4 - 15, -2051841);
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.pasture.limit"), n3, n4 + 82, -3563265);
        guiGraphics.drawCenteredString(this.font, String.valueOf(limit), n3 + 156, n4 + 82, -6401);
    }

    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    public boolean isPauseScreen() {
        return false;
    }
}
