/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package com.coaldust.client;

import com.coaldust.client.IdeologyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RenameScreen
extends Screen {
    private final Screen parent;
    private final int index;
    private final String current;
    private EditBox box;

    public RenameScreen(Screen screen, int n, String string) {
        super((Component)Component.translatable((String)"ideology.coaldust.work.rename_title", (Object[])new Object[]{n + 1}));
        this.parent = screen;
        this.index = n;
        this.current = string == null ? "" : string;
    }

    private void apply(String string) {
        IdeologyClient.send("ideology wname " + this.index + " " + (string.isBlank() ? "-" : string.trim()));
        IdeologyClient.request();
        Minecraft.getInstance().setScreen(this.parent);
    }

    protected void init() {
        int n = 200;
        int n2 = (this.width - n) / 2;
        int n3 = this.height / 2 - 10;
        this.box = new EditBox(this.font, n2, n3, n, 18, (Component)Component.empty());
        this.box.setMaxLength(24);
        this.box.setValue(this.current);
        this.addRenderableWidget((GuiEventListener)this.box);
        this.setInitialFocus((GuiEventListener)this.box);
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.work.rename_ok"), button -> this.apply(this.box.getValue())).bounds(n2, n3 + 26, 64, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.work.rename_reset"), button -> this.apply("")).bounds(n2 + 68, n3 + 26, 64, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.cancel"), button -> Minecraft.getInstance().setScreen(this.parent)).bounds(n2 + 136, n3 + 26, 64, 16).build());
    }

    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 257 || n == 335) {
            this.apply(this.box.getValue());
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    public void renderBackground(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.renderBackground(guiGraphics, n, n2, f);
        int n3 = 220;
        int n4 = 78;
        int n5 = (this.width - n3) / 2;
        int n6 = this.height / 2 - 34;
        guiGraphics.fillGradient(n5, n6, n5 + n3, n6 + n4, -267122142, -267910128);
        guiGraphics.renderOutline(n5, n6, n3, n4, -6266656);
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.render(guiGraphics, n, n2, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 26, -2051841);
    }

    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    public boolean isPauseScreen() {
        return false;
    }
}
