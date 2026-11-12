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
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.coaldust.client;

import com.coaldust.client.IdeologyClient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ChestScreen
extends Screen {
    private final BlockPos pos;
    private int ticks;
    private int builtVersion = -1;

    public ChestScreen(BlockPos blockPos) {
        super((Component)Component.translatable((String)"ideology.coaldust.chest.title"));
        this.pos = blockPos.immutable();
    }

    private CompoundTag cfg() {
        if (IdeologyClient.data == null) {
            return null;
        }
        ListTag listTag = IdeologyClient.data.getList("wchests", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int[] nArray = compoundTag.getIntArray("pos");
            if (nArray.length != 3 || nArray[0] != this.pos.getX() || nArray[1] != this.pos.getY() || nArray[2] != this.pos.getZ()) continue;
            return compoundTag;
        }
        return null;
    }

    private List<String> filter() {
        ArrayList<String> arrayList = new ArrayList<String>();
        CompoundTag compoundTag = this.cfg();
        if (compoundTag != null) {
            for (String string : compoundTag.getString("filter").split(",")) {
                if (string.isEmpty()) continue;
                arrayList.add(string);
            }
        }
        return arrayList;
    }

    private Set<Integer> bound() {
        HashSet<Integer> hashSet = new HashSet<Integer>();
        CompoundTag compoundTag = this.cfg();
        if (compoundTag != null) {
            for (String string : compoundTag.getString("bound").split(",")) {
                if (string.isEmpty()) continue;
                try {
                    hashSet.add(Integer.parseInt(string));
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
        return hashSet;
    }

    private void cmd(String string) {
        IdeologyClient.send("ideology wchest " + this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ() + " " + string);
        IdeologyClient.request();
    }

    private int pw() {
        return Math.min(this.width - 16, 260);
    }

    private int ph() {
        return Math.min(this.height - 16, 230);
    }

    private int x0() {
        return (this.width - this.pw()) / 2;
    }

    private int y0() {
        return (this.height - this.ph()) / 2;
    }

    protected void init() {
        this.builtVersion = IdeologyClient.version;
        int n = this.x0() + 8;
        int n2 = this.y0() + 20;
        int n3 = this.pw() - 16;
        CompoundTag compoundTag = this.cfg();
        boolean bl = compoundTag != null && compoundTag.getBoolean("pub");
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)(bl ? "ideology.coaldust.chest.pub_on" : "ideology.coaldust.chest.pub_off")), button -> this.cmd("pub")).bounds(n, n2, n3 / 2 - 2, 16).tooltip(Tooltip.create((Component)Component.translatable((String)"ideology.coaldust.chest.pub_tip"))).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.chest.forget"), button -> {
            this.cmd("forget");
            this.onClose();
        }).bounds(n + n3 / 2 + 2, n2, n3 / 2 - 2, 16).build());
        int n4 = IdeologyClient.data == null ? 0 : IdeologyClient.data.getInt("wcount");
        Set<Integer> set = this.bound();
        for (int i = 0; i < n4 && i < 12; ++i) {
            int n5 = i;
            int n6 = n + i % 4 * (n3 / 4);
            int n7 = n2 + 32 + i / 4 * 18;
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)((set.contains(i) ? "\u2714 " : "") + (i + 1))), button -> this.cmd("bind " + n5)).bounds(n6, n7, n3 / 4 - 2, 16).build());
        }
    }

    public void tick() {
        super.tick();
        if (++this.ticks % 20 == 0) {
            IdeologyClient.request();
        }
        if (this.builtVersion != IdeologyClient.version) {
            this.rebuildWidgets();
        }
    }

    private int filterY() {
        return this.y0() + 20 + 32 + 54 + 16;
    }

    private int hotbarY() {
        return this.filterY() + 40 + 26;
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
        super.render(guiGraphics, n, n2, f);
        int n5 = this.x0() + 8;
        int n6 = this.pw() - 16;
        guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.chest.title_at", (Object[])new Object[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()}), this.x0() + this.pw() / 2, this.y0() + 6, -2051841);
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.chest.bound"), n5, this.y0() + 20 + 22, -3563265);
        int n7 = this.filterY();
        List<String> list = this.filter();
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)(list.isEmpty() ? "ideology.coaldust.chest.filter_none" : "ideology.coaldust.chest.filter")), n5, n7 - 11, -3563265);
        for (n4 = 0; n4 < 18; ++n4) {
            int n8 = n5 + n4 % 9 * 20;
            n3 = n7 + n4 / 9 * 20;
            guiGraphics.fill(n8, n3, n8 + 18, n3 + 18, -14478029);
            guiGraphics.renderOutline(n8, n3, 18, 18, -10864006);
            if (n4 >= list.size()) continue;
            ItemStack itemStack = new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)list.get(n4))));
            guiGraphics.renderItem(itemStack, n8 + 1, n3 + 1);
            if (n < n8 || n >= n8 + 18 || n2 < n3 || n2 >= n3 + 18) continue;
            guiGraphics.renderTooltip(this.font, itemStack, n, n2);
        }
        n4 = this.hotbarY();
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.chest.hotbar"), n5, n4 - 11, -6649680);
        Inventory inventory = Minecraft.getInstance().player.getInventory();
        for (n3 = 0; n3 < 9; ++n3) {
            int n9 = n5 + n3 * 20;
            guiGraphics.fill(n9, n4, n9 + 18, n4 + 18, -15069146);
            guiGraphics.renderOutline(n9, n4, 18, 18, -7710016);
            ItemStack itemStack = inventory.getItem(n3);
            if (itemStack.isEmpty()) continue;
            guiGraphics.renderItem(itemStack, n9 + 1, n4 + 1);
            if (n < n9 || n >= n9 + 18 || n2 < n4 || n2 >= n4 + 18) continue;
            guiGraphics.renderTooltip(this.font, itemStack, n, n2);
        }
    }

    public boolean mouseClicked(double d, double d2, int n) {
        int n2;
        int n3;
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        int n4 = this.x0() + 8;
        int n5 = this.filterY();
        int n6 = this.hotbarY();
        List<String> list = this.filter();
        for (int i = 0; i < list.size(); ++i) {
            n3 = n4 + i % 9 * 20;
            n2 = n5 + i / 9 * 20;
            if (!(d >= (double)n3) || !(d < (double)(n3 + 18)) || !(d2 >= (double)n2) || !(d2 < (double)(n2 + 18))) continue;
            this.cmd("del " + list.get(i));
            return true;
        }
        Inventory inventory = Minecraft.getInstance().player.getInventory();
        for (n3 = 0; n3 < 9; ++n3) {
            n2 = n4 + n3 * 20;
            ItemStack itemStack = inventory.getItem(n3);
            if (itemStack.isEmpty() || !(d >= (double)n2) || !(d < (double)(n2 + 18)) || !(d2 >= (double)n6) || !(d2 < (double)(n6 + 18))) continue;
            this.cmd("add " + String.valueOf(BuiltInRegistries.ITEM.getKey((Object)itemStack.getItem())));
            return true;
        }
        return false;
    }

    public boolean isPauseScreen() {
        return false;
    }
}
