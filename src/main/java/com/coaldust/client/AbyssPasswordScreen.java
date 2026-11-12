/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 */
package com.coaldust.client;

import com.coaldust.CoalDust;
import com.coaldust.client.IdeologyClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class AbyssPasswordScreen
extends Screen {
    private static final ResourceLocation SKIN = CoalDust.id("textures/gui/ideology/menu_abyss.png");
    private final Screen parent;
    private EditBox box;

    public AbyssPasswordScreen(Screen screen) {
        super((Component)Component.translatable((String)"ideology.coaldust.abyss.title"));
        this.parent = screen;
    }

    private int frameW() {
        int n = this.font.width((FormattedText)Component.translatable((String)"ideology.coaldust.abyss.prompt"));
        return Math.min(this.width - 20, Math.max(260, n + 70));
    }

    private int frameH() {
        return 104;
    }

    private int frameX() {
        return (this.width - this.frameW()) / 2;
    }

    private int frameY() {
        return (this.height - this.frameH()) / 2;
    }

    protected void init() {
        int n2 = Math.min(180, this.frameW() - 60);
        int n3 = (this.width - n2) / 2;
        int n4 = this.frameY() + 56;
        this.box = new EditBox(this.font, n3, n4, n2, 18, (Component)Component.empty());
        this.box.setMaxLength(32);
        this.box.setFormatter((string, n) -> FormattedCharSequence.forward((String)"\u2022".repeat(string.length()), (Style)Style.EMPTY.withColor(13667327)));
        this.addRenderableWidget((GuiEventListener)this.box);
        this.setInitialFocus((GuiEventListener)this.box);
    }

    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 257 || n == 335) {
            String string = this.box.getValue().trim();
            if (!string.isEmpty()) {
                IdeologyClient.send("ideology veil " + string);
            }
            this.box.setValue("");
            Minecraft.getInstance().setScreen(this.parent);
            IdeologyClient.request();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    public void renderBackground(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.renderBackground(guiGraphics, n, n2, f);
        int n3 = this.frameW();
        int n4 = this.frameH();
        int n5 = this.frameX();
        int n6 = this.frameY();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int n7 = 26;
        int n8 = 128;
        guiGraphics.blit(SKIN, n5, n6, n7, n7, 0.0f, 0.0f, n8, n8, 1024, 1024);
        guiGraphics.blit(SKIN, n5 + n3 - n7, n6, n7, n7, (float)(512 - n8), 0.0f, n8, n8, 1024, 1024);
        guiGraphics.blit(SKIN, n5, n6 + n4 - n7, n7, n7, 0.0f, (float)(512 - n8), n8, n8, 1024, 1024);
        guiGraphics.blit(SKIN, n5 + n3 - n7, n6 + n4 - n7, n7, n7, (float)(512 - n8), (float)(512 - n8), n8, n8, 1024, 1024);
        guiGraphics.blit(SKIN, n5 + n7, n6, n3 - 2 * n7, n7, (float)n8, 0.0f, 512 - 2 * n8, n8, 1024, 1024);
        guiGraphics.blit(SKIN, n5 + n7, n6 + n4 - n7, n3 - 2 * n7, n7, (float)n8, (float)(512 - n8), 512 - 2 * n8, n8, 1024, 1024);
        guiGraphics.blit(SKIN, n5, n6 + n7, n7, n4 - 2 * n7, 0.0f, (float)n8, n8, 512 - 2 * n8, 1024, 1024);
        guiGraphics.blit(SKIN, n5 + n3 - n7, n6 + n7, n7, n4 - 2 * n7, (float)(512 - n8), (float)n8, n8, 512 - 2 * n8, 1024, 1024);
        guiGraphics.blit(SKIN, n5 + n7, n6 + n7, n3 - 2 * n7, n4 - 2 * n7, (float)n8, (float)n8, 512 - 2 * n8, 512 - 2 * n8, 1024, 1024);
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.render(guiGraphics, n, n2, f);
        int n3 = this.frameY() + 24;
        guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.abyss.title"), this.width / 2, n3, -2051841);
        for (FormattedCharSequence formattedCharSequence : this.font.split((FormattedText)Component.translatable((String)"ideology.coaldust.abyss.prompt"), this.frameW() - 56)) {
            guiGraphics.drawCenteredString(this.font, formattedCharSequence, this.width / 2, n3 += 11, -3703297);
        }
    }

    public boolean isPauseScreen() {
        return false;
    }
}
