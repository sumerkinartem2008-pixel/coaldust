/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.Container
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 */
package com.coaldust.client;

import com.coaldust.client.ChestScreen;
import com.coaldust.client.CraftScreen;
import com.coaldust.client.FillScreen;
import com.coaldust.client.IdeologyClient;
import com.coaldust.client.PastureScreen;
import com.coaldust.client.RenameScreen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WorkerScreen
extends Screen {
    private int ticks;
    private int builtVersion = -1;

    public WorkerScreen() {
        super((Component)Component.translatable((String)"ideology.coaldust.work.title"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen((Screen)new WorkerScreen());
        IdeologyClient.send("ideology wshow");
        IdeologyClient.request();
    }

    private static BlockPos aimed() {
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.player.pick(64.0, 1.0f, false);
        if (hitResult instanceof BlockHitResult) {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                return blockHitResult.getBlockPos();
            }
        }
        return minecraft.player.blockPosition();
    }

    private CompoundTag data() {
        return IdeologyClient.data == null ? new CompoundTag() : IdeologyClient.data;
    }

    private int pw() {
        return Math.min(this.width - 16, 340);
    }

    private int ph() {
        return Math.min(this.height - 16, 270);
    }

    private int x0() {
        return (this.width - this.pw()) / 2;
    }

    private int y0() {
        return (this.height - this.ph()) / 2;
    }

    private int split() {
        return this.x0() + 128;
    }

    private String name(int n) {
        String string = this.data().getCompound("wnames").getString(String.valueOf(n));
        return string.isEmpty() ? Component.translatable((String)"ideology.coaldust.work.worker", (Object[])new Object[]{n + 1}).getString() : string;
    }

    private Set<Integer> selected() {
        HashSet<Integer> hashSet = new HashSet<Integer>();
        for (String string : this.data().getString("wsel").split(",")) {
            if (string.isEmpty()) continue;
            try {
                hashSet.add(Integer.parseInt(string));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return hashSet;
    }

    private Map<Integer, String[]> tasks() {
        HashMap<Integer, String[]> hashMap = new HashMap<Integer, String[]>();
        for (String string : this.data().getString("wtasks").split(";")) {
            String[] stringArray = string.split(":");
            if (stringArray.length < 3) continue;
            try {
                hashMap.put(Integer.parseInt(stringArray[0]), stringArray);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return hashMap;
    }

    private void cmd(String string) {
        IdeologyClient.send("ideology " + string);
        IdeologyClient.request();
    }

    protected void init() {
        int n;
        this.builtVersion = IdeologyClient.version;
        int n2 = this.split() + 6;
        int n3 = this.x0() + this.pw() - 6 - n2;
        int n4 = this.y0() + 74;
        int n5 = (n3 - 4) / 2;
        int n6 = 16;
        String[][] stringArrayArray = new String[][]{{"wood", "work wood"}, {"ore", "work ore"}, {"dig", "work dig"}, {"fill", null}, {"template", "work template"}, {"copy", "work copy"}, {"farm", "work farm"}, {"collect", "work collect"}, {"pasture", null}, {"smelt", null}, {"craft", null}, {"follow", "work follow"}, {"storage", null}};
        for (n = 0; n < stringArrayArray.length; ++n) {
            String[] stringArray = stringArrayArray[n];
            int n7 = n2 + n % 2 * (n5 + 4);
            int n8 = n4 + n / 2 * (n6 + 3);
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)("ideology.coaldust.work.btn." + stringArray[0])), button -> {
                if (stringArray[1] != null) {
                    this.cmd(stringArray[1]);
                    return;
                }
                if (stringArray[0].equals("pasture")) {
                    Minecraft.getInstance().setScreen((Screen)new PastureScreen(this));
                    return;
                }
                if (stringArray[0].equals("smelt")) {
                    Minecraft.getInstance().setScreen((Screen)new FillScreen(this, true));
                    return;
                }
                if (stringArray[0].equals("craft")) {
                    Minecraft.getInstance().setScreen((Screen)new CraftScreen(this));
                    return;
                }
                if (stringArray[0].equals("storage")) {
                    Minecraft minecraft = Minecraft.getInstance();
                    BlockPos blockPos = WorkerScreen.aimed();
                    if (minecraft.level.getBlockEntity(blockPos) instanceof Container) {
                        minecraft.setScreen((Screen)new ChestScreen(blockPos));
                    } else {
                        minecraft.player.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.look_chest"), true);
                    }
                    return;
                }
                Minecraft.getInstance().setScreen((Screen)new FillScreen(this));
            }).bounds(n7, n8, n5, n6).tooltip(Tooltip.create((Component)Component.translatable((String)("ideology.coaldust.work.btn." + stringArray[0] + ".tip")))).build());
        }
        n = this.x0() + this.pw() - 6 - 50;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.work.set1"), button -> {
            BlockPos blockPos = WorkerScreen.aimed();
            this.cmd("wcorner 1 " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ());
        }).bounds(n, this.y0() + 19, 50, 12).tooltip(Tooltip.create((Component)Component.translatable((String)"ideology.coaldust.work.set_tip"))).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.work.set2"), button -> {
            BlockPos blockPos = WorkerScreen.aimed();
            this.cmd("wcorner 2 " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ());
        }).bounds(n, this.y0() + 32, 50, 12).tooltip(Tooltip.create((Component)Component.translatable((String)"ideology.coaldust.work.set_tip"))).build());
        int n9 = this.x0() + 6;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.work.all"), button -> this.cmd("wsel 1000")).bounds(n9, this.y0() + this.ph() - 22, 38, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.work.none"), button -> this.cmd("wsel -1")).bounds(n9 + 40, this.y0() + this.ph() - 22, 38, 16).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.work.rename"), button -> {
            Set<Integer> set = this.selected();
            if (set.size() != 1) {
                Minecraft.getInstance().player.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.work.rename_one"), true);
                return;
            }
            int n = set.iterator().next();
            Minecraft.getInstance().setScreen((Screen)new RenameScreen(this, n, this.data().getCompound("wnames").getString(String.valueOf(n))));
        }).bounds(n9 + 80, this.y0() + this.ph() - 22, 40, 16).tooltip(Tooltip.create((Component)Component.translatable((String)"ideology.coaldust.work.rename_tip"))).build());
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

    public void renderBackground(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.renderBackground(guiGraphics, n, n2, f);
        int n3 = this.x0();
        int n4 = this.y0();
        int n5 = this.pw();
        int n6 = this.ph();
        guiGraphics.fillGradient(n3, n4, n3 + n5, n4 + n6, -267122142, -267910128);
        guiGraphics.renderOutline(n3, n4, n5, n6, -6266656);
        guiGraphics.renderOutline(n3 + 2, n4 + 2, n5 - 4, n6 - 4, -12244897);
        guiGraphics.fill(this.split(), n4 + 6, this.split() + 1, n4 + n6 - 6, -12244897);
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        int n3;
        String[] stringArray;
        String[] stringArray2;
        int n4;
        super.render(guiGraphics, n, n2, f);
        int n5 = this.x0();
        int n6 = this.y0();
        guiGraphics.drawCenteredString(this.font, this.title, n5 + this.pw() / 2, n6 + 6, -2051841);
        int n7 = this.data().getInt("wcount");
        Set<Integer> set = this.selected();
        Map<Integer, String[]> map = this.tasks();
        int n8 = n6 + 22;
        for (n4 = 0; n4 < n7 && n8 < n6 + this.ph() - 34; ++n4, n8 += 14) {
            boolean bl;
            boolean bl2 = set.contains(n4);
            boolean bl3 = bl = n >= n5 + 4 && n < this.split() - 2 && n2 >= n8 - 2 && n2 < n8 + 11;
            if (bl2) {
                guiGraphics.fill(n5 + 4, n8 - 2, this.split() - 2, n8 + 11, 1883905914);
            } else if (bl) {
                guiGraphics.fill(n5 + 4, n8 - 2, this.split() - 2, n8 + 11, 0x30FFFFFF);
            }
            stringArray2 = map.get(n4);
            stringArray = stringArray2 == null ? Component.translatable((String)"ideology.coaldust.work.absent").getString() : Component.translatable((String)("ideology.coaldust.work.task." + stringArray2[1])).getString();
            String string = (bl2 ? "\u25b6 " : "  ") + this.name(n4) + ": " + (String)stringArray;
            guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(string, this.split() - n5 - 10), n5 + 6, n8, bl2 ? -6401 : -4478260);
        }
        n8 = n6 + 22;
        for (n4 = 0; n4 < n7 && n8 < n6 + this.ph() - 34; ++n4, n8 += 14) {
            if (n < n5 + 4 || n >= this.split() - 2 || n2 < n8 - 2 || n2 >= n8 + 11) continue;
            this.bagTip(guiGraphics, n4, n, n2);
        }
        n4 = this.split() + 6;
        String string = this.data().getString("wp1");
        String string2 = this.data().getString("wp2");
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.work.corner1", (Object[])new Object[]{string.isEmpty() ? "\u2014" : string}), n4, n6 + 22, -3563265);
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.work.corner2", (Object[])new Object[]{string2.isEmpty() ? "\u2014" : string2}), n4, n6 + 33, -3563265);
        if (!string.isEmpty() && !string2.isEmpty()) {
            stringArray2 = string.split(" ");
            stringArray = string2.split(" ");
            try {
                int n9 = Math.abs(Integer.parseInt(stringArray2[0]) - Integer.parseInt(stringArray[0])) + 1;
                int n10 = Math.abs(Integer.parseInt(stringArray2[1]) - Integer.parseInt(stringArray[1])) + 1;
                int n11 = Math.abs(Integer.parseInt(stringArray2[2]) - Integer.parseInt(stringArray[2])) + 1;
                guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.work.size", (Object[])new Object[]{n9, n10, n11}), n4, n6 + 44, -6649680);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        guiGraphics.drawString(this.font, (Component)((n3 = this.data().getInt("wtpl")) > 0 ? Component.translatable((String)"ideology.coaldust.work.tpl", (Object[])new Object[]{n3}) : Component.translatable((String)"ideology.coaldust.work.no_tpl")), n4, n6 + 56, -6649680);
        int n12 = this.y0() + 74 + 133 + 2;
        int n13 = this.x0() + this.pw() - 6 - n4;
        for (FormattedCharSequence formattedCharSequence : this.font.split((FormattedText)Component.translatable((String)"ideology.coaldust.work.hint"), n13)) {
            if (n12 > this.y0() + this.ph() - 10) break;
            guiGraphics.drawString(this.font, formattedCharSequence, n4, n12, -8491370);
            n12 += 9;
        }
    }

    private void bagTip(GuiGraphics guiGraphics, int n, int n2, int n3) {
        ArrayList<FormattedCharSequence> arrayList = new ArrayList<FormattedCharSequence>();
        String string = this.data().getCompound("wbags").getString(String.valueOf(n));
        ArrayList<String> arrayList2 = new ArrayList<String>();
        for (String string2 : string.split(",")) {
            if (string2.isEmpty()) continue;
            arrayList2.add(string2);
        }
        arrayList.add(Component.translatable((String)"ideology.coaldust.work.bag", (Object[])new Object[]{arrayList2.size(), this.data().getInt("wbag")}).withStyle(ChatFormatting.LIGHT_PURPLE).getVisualOrderText());
        if (arrayList2.isEmpty()) {
            arrayList.add(Component.translatable((String)"ideology.coaldust.work.bag_empty").withStyle(ChatFormatting.GRAY).getVisualOrderText());
        }
        for (String string3 : arrayList2) {
            String string2;
            int n4 = string3.lastIndexOf(42);
            if (n4 < 0) continue;
            string2 = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)string3.substring(0, n4)));
            arrayList.add(Component.literal((String)(string3.substring(n4 + 1) + " \u00d7 ")).append(new ItemStack((ItemLike)string2).getHoverName()).getVisualOrderText());
        }
        guiGraphics.renderTooltip(this.font, arrayList, n2, n3);
    }

    public boolean mouseClicked(double d, double d2, int n) {
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        int n2 = this.x0();
        int n3 = this.y0();
        int n4 = this.data().getInt("wcount");
        int n5 = n3 + 22;
        for (int i = 0; i < n4 && n5 < n3 + this.ph() - 34; ++i, n5 += 14) {
            if (!(d >= (double)(n2 + 4)) || !(d < (double)(this.split() - 2)) || !(d2 >= (double)(n5 - 2)) || !(d2 < (double)(n5 + 11))) continue;
            if (n == 1) {
                Minecraft.getInstance().setScreen((Screen)new RenameScreen(this, i, this.data().getCompound("wnames").getString(String.valueOf(i))));
                return true;
            }
            this.cmd("wsel " + i);
            return true;
        }
        return false;
    }

    public boolean isPauseScreen() {
        return false;
    }
}
