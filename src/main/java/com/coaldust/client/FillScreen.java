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
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.coaldust.client;

import com.coaldust.client.IdeologyClient;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class FillScreen
extends Screen {
    private static final String[] MODES = new String[]{"random", "checker", "layers", "stripes", "border"};
    private final Screen parent;
    private final boolean smelt;
    private final List<Item> chosen = new ArrayList<Item>();
    private static String mode = "random";

    public FillScreen(Screen screen) {
        this(screen, false);
    }

    public FillScreen(Screen screen, boolean bl) {
        super((Component)Component.translatable((String)(bl ? "ideology.coaldust.smelt.title" : "ideology.coaldust.fill.title")));
        this.parent = screen;
        this.smelt = bl;
    }

    private List<Item> available() {
        ArrayList<Item> arrayList = new ArrayList<Item>();
        Inventory inventory = Minecraft.getInstance().player.getInventory();
        for (int i = 0; i < 36; ++i) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack.isEmpty() || !this.smelt && !(itemStack.getItem() instanceof BlockItem) || arrayList.contains(itemStack.getItem())) continue;
            arrayList.add(itemStack.getItem());
        }
        return arrayList;
    }

    private int pw() {
        return Math.min(this.width - 16, 300);
    }

    private int ph() {
        return Math.min(this.height - 16, 220);
    }

    private int x0() {
        return (this.width - this.pw()) / 2;
    }

    private int y0() {
        return (this.height - this.ph()) / 2;
    }

    private int gridY() {
        return this.y0() + 36;
    }

    private int chosenY() {
        return this.gridY() + 80 + 16;
    }

    protected void init() {
        int n;
        int n2 = this.x0() + 8;
        int n3 = this.pw() - 16;
        int n4 = this.chosenY() + 30;
        int n5 = (n3 - 12) / 5;
        for (n = 0; n < MODES.length && !this.smelt; ++n) {
            String string = MODES[n];
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)((string.equals(mode) ? "\u25b6" : "") + Component.translatable((String)("ideology.coaldust.fill.mode." + string)).getString())), button -> {
                mode = string;
                this.rebuildWidgets();
            }).bounds(n2 + n * (n5 + 3), n4, n5, 16).tooltip(Tooltip.create((Component)Component.translatable((String)("ideology.coaldust.fill.mode." + string + ".tip")))).build());
        }
        n = this.y0() + this.ph() - 22;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.fill.go"), button -> {
            if (this.chosen.isEmpty()) {
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (Item item : this.chosen) {
                stringBuilder.append(BuiltInRegistries.ITEM.getKey((Object)item)).append(',');
            }
            IdeologyClient.send(this.smelt ? "ideology work smelt smelt " + String.valueOf(stringBuilder) : "ideology work fill " + mode + " " + String.valueOf(stringBuilder));
            IdeologyClient.request();
            Minecraft.getInstance().setScreen(this.parent);
        }).bounds(this.x0() + this.pw() / 2 - 104, n, 100, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.cancel"), button -> Minecraft.getInstance().setScreen(this.parent)).bounds(this.x0() + this.pw() / 2 + 4, n, 100, 16).build());
    }

    public void renderBackground(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.renderBackground(guiGraphics, n, n2, f);
        int n3 = this.x0();
        int n4 = this.y0();
        int n5 = this.pw();
        int n6 = this.ph();
        guiGraphics.fillGradient(n3, n4, n3 + n5, n4 + n6, -267122142, -267910128);
        guiGraphics.renderOutline(n3, n4, n5, n6, -6266656);
        guiGraphics.renderOutline(n3 + 2, n4 + 2, n5 - 4, n6 - 4, -12244897);
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        int n3;
        int n4;
        int n5;
        super.render(guiGraphics, n, n2, f);
        int n6 = this.x0() + 8;
        guiGraphics.drawCenteredString(this.font, this.title, this.x0() + this.pw() / 2, this.y0() + 6, -2051841);
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)(this.smelt ? "ideology.coaldust.smelt.pick" : "ideology.coaldust.fill.pick")), n6, this.gridY() - 12, -3563265);
        List<Item> list = this.available();
        ItemStack itemStack = null;
        for (n5 = 0; n5 < 36; ++n5) {
            n4 = n6 + n5 % 12 * 20;
            n3 = this.gridY() + n5 / 12 * 20;
            if (n3 > this.chosenY() - 22) break;
            guiGraphics.fill(n4, n3, n4 + 18, n3 + 18, -15069146);
            boolean bl = n5 < list.size() && this.chosen.contains(list.get(n5));
            guiGraphics.renderOutline(n4, n3, 18, 18, bl ? -2051841 : -10864006);
            if (n5 >= list.size()) continue;
            ItemStack itemStack2 = new ItemStack((ItemLike)list.get(n5));
            guiGraphics.renderItem(itemStack2, n4 + 1, n3 + 1);
            if (n < n4 || n >= n4 + 18 || n2 < n3 || n2 >= n3 + 18) continue;
            itemStack = itemStack2;
        }
        if (list.isEmpty()) {
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)(this.smelt ? "ideology.coaldust.smelt.no_items" : "ideology.coaldust.fill.no_blocks")), n6, this.gridY() + 4, -6649680);
        }
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.fill.chosen", (Object[])new Object[]{this.chosen.size()}), n6, this.chosenY() - 11, -3563265);
        for (n5 = 0; n5 < 6; ++n5) {
            n4 = n6 + n5 * 22;
            n3 = this.chosenY();
            guiGraphics.fill(n4, n3, n4 + 18, n3 + 18, -14478029);
            guiGraphics.renderOutline(n4, n3, 18, 18, -7710016);
            if (n5 >= this.chosen.size()) continue;
            ItemStack itemStack3 = new ItemStack((ItemLike)this.chosen.get(n5));
            guiGraphics.renderItem(itemStack3, n4 + 1, n3 + 1);
            guiGraphics.drawString(this.font, String.valueOf(n5 + 1), n4 + 12, n3 + 10, -6401);
            if (n < n4 || n >= n4 + 18 || n2 < n3 || n2 >= n3 + 18) continue;
            itemStack = itemStack3;
        }
        if (this.chosen.size() > 1 && !this.smelt) {
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.fill.mix"), n6, this.chosenY() + 20, -6649680);
        }
        if (itemStack != null) {
            guiGraphics.renderTooltip(this.font, itemStack, n, n2);
        }
    }

    public boolean mouseClicked(double d, double d2, int n) {
        int n2;
        int n3;
        int n4;
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        int n5 = this.x0() + 8;
        List<Item> list = this.available();
        for (n4 = 0; n4 < list.size() && n4 < 36; ++n4) {
            n3 = n5 + n4 % 12 * 20;
            n2 = this.gridY() + n4 / 12 * 20;
            if (!(d >= (double)n3) || !(d < (double)(n3 + 18)) || !(d2 >= (double)n2) || !(d2 < (double)(n2 + 18))) continue;
            Item item = list.get(n4);
            if (!this.chosen.remove(item) && this.chosen.size() < 6) {
                this.chosen.add(item);
            }
            return true;
        }
        for (n4 = 0; n4 < this.chosen.size(); ++n4) {
            n3 = n5 + n4 * 22;
            n2 = this.chosenY();
            if (!(d >= (double)n3) || !(d < (double)(n3 + 18)) || !(d2 >= (double)n2) || !(d2 < (double)(n2 + 18))) continue;
            this.chosen.remove(n4);
            return true;
        }
        return false;
    }

    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    public boolean isPauseScreen() {
        return false;
    }
}
