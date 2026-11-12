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
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.ItemLike
 */
package com.coaldust.client;

import com.coaldust.client.IdeologyClient;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public class CraftScreen
extends Screen {
    private final Screen parent;
    private EditBox search;
    private String query = "";
    private Item chosen;
    private int amount = 16;
    private List<Item> all;

    public CraftScreen(Screen screen) {
        super((Component)Component.translatable((String)"ideology.coaldust.craft.title"));
        this.parent = screen;
    }

    private List<Item> craftable() {
        if (this.all != null) {
            return this.all;
        }
        LinkedHashSet<Item> linkedHashSet = new LinkedHashSet<Item>();
        Minecraft minecraft = Minecraft.getInstance();
        for (RecipeHolder recipeHolder : minecraft.level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            ItemStack itemStack = ((CraftingRecipe)recipeHolder.value()).getResultItem((HolderLookup.Provider)minecraft.level.registryAccess());
            if (itemStack.isEmpty()) continue;
            linkedHashSet.add(itemStack.getItem());
        }
        this.all = new ArrayList<Item>(linkedHashSet);
        return this.all;
    }

    private List<Item> shown() {
        ArrayList<Item> arrayList = new ArrayList<Item>();
        String string = this.query.toLowerCase();
        for (Item item : this.craftable()) {
            if (!string.isEmpty() && !new ItemStack((ItemLike)item).getHoverName().getString().toLowerCase().contains(string) && !BuiltInRegistries.ITEM.getKey((Object)item).getPath().contains(string)) continue;
            arrayList.add(item);
            if (arrayList.size() < 60) continue;
            break;
        }
        return arrayList;
    }

    private int pw() {
        return Math.min(this.width - 16, 300);
    }

    private int x0() {
        return (this.width - this.pw()) / 2;
    }

    private int y0() {
        return Math.max(8, this.height / 2 - 115);
    }

    private int gridY() {
        return this.y0() + 40;
    }

    protected void init() {
        int n = this.x0() + 8;
        this.search = new EditBox(this.font, n, this.y0() + 20, this.pw() - 16, 14, (Component)Component.empty());
        this.search.setValue(this.query);
        this.search.setResponder(string -> {
            this.query = string;
        });
        this.addRenderableWidget((GuiEventListener)this.search);
        this.setInitialFocus((GuiEventListener)this.search);
        int n2 = this.gridY() + 100 + 26;
        int[] nArray = new int[]{-16, -1, 1, 16};
        for (int i = 0; i < nArray.length; ++i) {
            int n3 = nArray[i];
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)((n3 > 0 ? "+" : "") + n3)), button -> {
                this.amount = Math.max(1, Math.min(1024, this.amount + n3));
            }).bounds(n + 120 + i * 36, n2, 34, 16).build());
        }
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.craft.go"), button -> {
            if (this.chosen == null) {
                return;
            }
            IdeologyClient.send("ideology work craft " + String.valueOf(BuiltInRegistries.ITEM.getKey((Object)this.chosen)) + " " + this.amount);
            IdeologyClient.request();
            Minecraft.getInstance().setScreen(this.parent);
        }).bounds(this.x0() + this.pw() / 2 - 104, n2 + 24, 100, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.cancel"), button -> Minecraft.getInstance().setScreen(this.parent)).bounds(this.x0() + this.pw() / 2 + 4, n2 + 24, 100, 16).build());
    }

    public void renderBackground(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.renderBackground(guiGraphics, n, n2, f);
        int n3 = this.x0();
        int n4 = this.y0();
        int n5 = this.pw();
        int n6 = this.gridY() + 100 + 70 - n4;
        guiGraphics.fillGradient(n3, n4, n3 + n5, n4 + n6, -267122142, -267910128);
        guiGraphics.renderOutline(n3, n4, n5, n6, -6266656);
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        int n3;
        super.render(guiGraphics, n, n2, f);
        int n4 = this.x0() + 8;
        guiGraphics.drawCenteredString(this.font, this.title, this.x0() + this.pw() / 2, this.y0() + 6, -2051841);
        List<Item> list = this.shown();
        ItemStack itemStack = null;
        for (n3 = 0; n3 < list.size(); ++n3) {
            int n5 = n4 + n3 % 12 * 23;
            int n6 = this.gridY() + n3 / 12 * 20;
            if (n3 / 12 >= 5) break;
            boolean bl = list.get(n3) == this.chosen;
            guiGraphics.fill(n5, n6, n5 + 18, n6 + 18, -15069146);
            guiGraphics.renderOutline(n5, n6, 18, 18, bl ? -2051841 : -10864006);
            ItemStack itemStack2 = new ItemStack((ItemLike)list.get(n3));
            guiGraphics.renderItem(itemStack2, n5 + 1, n6 + 1);
            if (n < n5 || n >= n5 + 18 || n2 < n6 || n2 >= n6 + 18) continue;
            itemStack = itemStack2;
        }
        n3 = this.gridY() + 100 + 26;
        if (this.chosen != null) {
            guiGraphics.renderItem(new ItemStack((ItemLike)this.chosen), n4, n3);
            guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(new ItemStack((ItemLike)this.chosen).getHoverName().getString(), 92), n4 + 20, n3 + 4, -6401);
        } else {
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.craft.pick"), n4, n3 + 4, -6649680);
        }
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.craft.amount", (Object[])new Object[]{this.amount}), n4 + 120, n3 - 11, -3563265);
        if (itemStack != null) {
            guiGraphics.renderTooltip(this.font, itemStack, n, n2);
        }
    }

    public boolean mouseClicked(double d, double d2, int n) {
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        int n2 = this.x0() + 8;
        List<Item> list = this.shown();
        for (int i = 0; i < list.size() && i / 12 < 5; ++i) {
            int n3 = n2 + i % 12 * 23;
            int n4 = this.gridY() + i / 12 * 20;
            if (!(d >= (double)n3) || !(d < (double)(n3 + 18)) || !(d2 >= (double)n4) || !(d2 < (double)(n4 + 18))) continue;
            this.chosen = list.get(i);
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
