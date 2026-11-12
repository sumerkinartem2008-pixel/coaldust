/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.coaldust.client;

import com.coaldust.CoalDust;
import com.coaldust.client.IdeologyClient;
import com.coaldust.client.IdeologyScreen;
import com.coaldust.client.PillarScreen;
import com.coaldust.client.WorkerScreen;
import com.coaldust.ideology.AbyssSkills;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class EvolveScreen
extends Screen {
    private static final ResourceLocation BG = CoalDust.id("textures/gui/abyss/pillars.png");
    private static final ResourceLocation BG_SUMMON = CoalDust.id("textures/gui/abyss/pillars_summon.png");
    private static int tree = 0;
    private static final ResourceLocation NODES = CoalDust.id("textures/gui/abyss/nodes.png");
    private int ticks;
    private int builtVersion = -1;
    private static final int Abyss_KILLS = 2;

    public EvolveScreen() {
        super((Component)Component.translatable((String)"ideology.coaldust.evolve.title"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen((Screen)new EvolveScreen());
        IdeologyClient.request();
    }

    private CompoundTag data() {
        return IdeologyClient.data == null ? new CompoundTag() : IdeologyClient.data;
    }

    private Map<String, Integer> ranks() {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        for (String string : this.data().getString("askills").split(";")) {
            int n = string.indexOf(61);
            if (n <= 0) continue;
            try {
                hashMap.put(string.substring(0, n), Integer.parseInt(string.substring(n + 1)));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return hashMap;
    }

    private int size() {
        return Math.max(240, Math.min(Math.min(this.width - 10, this.height - 8), 460));
    }

    private int px() {
        return (this.width - this.size()) / 2;
    }

    private int py() {
        return (this.height - this.size()) / 2;
    }

    private boolean has(String string) {
        String string2 = this.data().getString("me");
        for (int i = 0; i < this.data().getInt("n"); ++i) {
            CompoundTag compoundTag = this.data().getCompound("i" + i);
            if (!compoundTag.getString("id").equals(string2)) continue;
            return compoundTag.getString("traits").contains(string);
        }
        return false;
    }

    private int colX(int n) {
        return this.px() + this.size() / 3 * n;
    }

    private int colTop() {
        return this.py() + (int)((double)this.size() * 0.1);
    }

    private int colBottom() {
        return this.py() + (int)((double)this.size() * 0.92);
    }

    private int hoverPillar(int n, int n2) {
        int n3 = this.size() / 3;
        if (n2 < this.colTop() || n2 > this.colBottom()) {
            return -1;
        }
        for (int i = 0; i < 3; ++i) {
            if (n < this.colX(i) || n >= this.colX(i) + n3) continue;
            return i;
        }
        return -1;
    }

    static String[] heads(int n) {
        String[] stringArray;
        if (n == 1) {
            String[] stringArray2 = new String[3];
            stringArray2[0] = "squad";
            stringArray2[1] = "army";
            stringArray = stringArray2;
            stringArray2[2] = "workers";
        } else {
            String[] stringArray3 = new String[3];
            stringArray3[0] = "growth";
            stringArray3[1] = "endurance";
            stringArray = stringArray3;
            stringArray3[2] = "might";
        }
        return stringArray;
    }

    protected void init() {
        this.builtVersion = IdeologyClient.version;
        if (tree == 0 && !this.has("evolving") && this.has("summoner")) {
            tree = 1;
        }
        if (tree == 1 && !this.has("summoner") && this.has("evolving")) {
            tree = 0;
        }
        int n = this.size();
        if (this.has("evolving") && this.has("summoner")) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)(tree == 0 ? "ideology.coaldust.evolve.to_summon" : "ideology.coaldust.evolve.to_evolve")), button -> {
                tree = 1 - tree;
                this.rebuildWidgets();
            }).bounds(this.px() + (int)((double)n * 0.03), this.py() + (int)((double)n * 0.0575) - 7, 90, 14).build());
        }
        if (tree == 1 && this.has("summoner")) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.work.title"), button -> WorkerScreen.open()).bounds(this.px() + n - (int)((double)n * 0.03) - 90, this.py() + (int)((double)n * 0.0575) - 7, 90, 14).build());
        }
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.evolve.ideology"), button -> IdeologyScreen.open()).bounds(this.px() + n - (int)((double)n * 0.03) - 80, this.py() + (int)((double)n * 0.955) - 7, 80, 14).build());
    }

    public void tick() {
        super.tick();
        if (++this.ticks % 40 == 0) {
            IdeologyClient.request();
        }
        if (this.builtVersion != IdeologyClient.version) {
            this.rebuildWidgets();
        }
    }

    public void renderBackground(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.renderBackground(guiGraphics, n, n2, f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(tree == 1 ? BG_SUMMON : BG, this.px(), this.py(), this.size(), this.size(), 0.0f, 0.0f, 2048, 2048, 2048, 2048);
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        int n3;
        int n4;
        Map<String, Integer> map = this.ranks();
        int n5 = this.data().getInt("apoints");
        int n6 = this.size();
        int n7 = n6 / 3;
        int n8 = this.hoverPillar(n, n2);
        if (n8 >= 0) {
            guiGraphics.fill(this.colX(n8) + 6, this.colTop(), this.colX(n8) + n7 - 6, this.colBottom(), 816865535);
            guiGraphics.renderOutline(this.colX(n8) + 6, this.colTop(), n7 - 12, this.colBottom() - this.colTop(), -1596945409);
        }
        super.render(guiGraphics, n, n2, f);
        guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)(tree == 1 ? "ideology.coaldust.evolve.title_summon" : "ideology.coaldust.evolve.title")), this.px() + n6 / 2, this.py() + (int)((double)n6 * 0.0575) - 4, -2051841);
        String[] stringArray = EvolveScreen.heads(tree);
        for (n4 = 0; n4 < 3; ++n4) {
            int n9;
            int n10 = this.colX(n4) + n7 / 2;
            n3 = this.py() + (int)((double)n6 * 0.12);
            guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)("ideology.coaldust.evolve.pillar." + stringArray[n4])), n10, n3, -3563265);
            List<AbyssSkills.Skill> list = AbyssSkills.pillar(tree, n4);
            int n11 = 0;
            int n12 = 0;
            int n13 = 0;
            for (AbyssSkills.Skill skill : list) {
                n9 = map.getOrDefault(skill.id, 0);
                if (n9 > 0) {
                    ++n11;
                }
                n12 += n9;
                n13 += skill.max;
            }
            guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.evolve.nodes", (Object[])new Object[]{n11, list.size()}), n10, n3 + 12, -4479274);
            int n14 = n7 - 30;
            int n15 = n10 - n14 / 2;
            n9 = n3 + 24;
            guiGraphics.fill(n15, n9, n15 + n14, n9 + 4, -14015949);
            guiGraphics.fill(n15, n9, n15 + n14 * n12 / Math.max(1, n13), n9 + 4, -4031489);
            int n16 = n9 + 12;
            for (AbyssSkills.Stat stat : AbyssSkills.stats(tree, n4)) {
                if (n16 > this.colBottom() - 30) break;
                double d = AbyssSkills.total(map, stat.id);
                guiGraphics.renderItem(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)stat.icon))), this.colX(n4) + 12, n16);
                String string = Component.translatable((String)("ideology.coaldust.stat." + stat.id)).getString();
                int n17 = n7 - 40 - 48;
                guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(string, n17), this.colX(n4) + 30, n16 + 4, d > 0.0 ? -1650433 : -7701862, false);
                String string2 = PillarScreen.fmt(stat, d);
                guiGraphics.drawString(this.font, string2, this.colX(n4) + n7 - 12 - this.font.width(string2), n16 + 4, d > 0.0 ? -6488164 : -9807238, false);
                n16 += 18;
            }
            guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.evolve.click"), n10, this.colBottom() - 12, n8 == n4 ? -7937 : -8754550);
            if (tree != 1 || n8 != n4 || n2 >= n3 + 10 || n2 < n3 - 2 || n4 >= 2) continue;
            this.summonTip(guiGraphics, n4, n, n2);
        }
        if (tree == 1) {
            n4 = this.data().getInt("scool");
            MutableComponent mutableComponent = n4 > 0 ? Component.translatable((String)"ideology.coaldust.summon.cool_short", (Object[])new Object[]{(n4 + 59) / 60}) : Component.translatable((String)"ideology.coaldust.summon.ready");
            guiGraphics.drawCenteredString(this.font, (Component)mutableComponent, this.colX(0) + n7 / 2, this.colBottom() - 24, n4 > 0 ? -5209984 : -6488164);
            n3 = this.data().getInt("sdead");
            if (n3 > 0) {
                guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.summon.dead_short", (Object[])new Object[]{n3, this.data().getInt("snext")}), this.colX(1) + n7 / 2, this.colBottom() - 24, -5209984);
            }
        }
        String string = Component.translatable((String)"ideology.coaldust.evolve.points", (Object[])new Object[]{n5, this.data().getInt("ashards"), 2}).getString();
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(string, (int)((double)n6 * 0.56)), this.px() + (int)((double)n6 * 0.035), this.py() + (int)((double)n6 * 0.955) - 4, -10497);
        if (IdeologyClient.data != null && !this.has("evolving") && !this.has("summoner")) {
            guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.abyss.need_any"), this.px() + n6 / 2, this.py() + n6 / 2, -38037);
        }
    }

    private void summonTip(GuiGraphics guiGraphics, int n, int n2, int n3) {
        ArrayList<FormattedCharSequence> arrayList = new ArrayList<FormattedCharSequence>();
        if (n == 0) {
            int n4 = this.data().getInt("ssquad");
            arrayList.add(Component.translatable((String)"ideology.coaldust.summon.tip_squad", (Object[])new Object[]{n4 / 100, n4 % 100, this.data().getInt("slife")}).getVisualOrderText());
            int n5 = this.data().getInt("scool");
            arrayList.add((n5 > 0 ? Component.translatable((String)"ideology.coaldust.summon.cooldown", (Object[])new Object[]{(n5 + 59) / 60}) : Component.translatable((String)"ideology.coaldust.summon.ready_key")).getVisualOrderText());
        } else {
            int n6 = this.data().getInt("sarmy");
            arrayList.add(Component.translatable((String)"ideology.coaldust.summon.tip_army", (Object[])new Object[]{n6 / 10000, n6 / 100 % 100, n6 % 100}).getVisualOrderText());
            int n7 = this.data().getInt("sdead");
            if (n7 > 0) {
                arrayList.add(Component.translatable((String)"ideology.coaldust.summon.dead_short", (Object[])new Object[]{n7, this.data().getInt("snext")}).getVisualOrderText());
            }
        }
        guiGraphics.renderTooltip(this.font, arrayList, n2, n3);
    }

    public boolean mouseClicked(double d, double d2, int n) {
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        int n2 = this.hoverPillar((int)d, (int)d2);
        if (n2 >= 0 && (tree == 1 ? this.has("summoner") : this.has("evolving"))) {
            Minecraft.getInstance().setScreen((Screen)new PillarScreen(this, tree, n2));
            return true;
        }
        return false;
    }

    public boolean keyPressed(int n, int n2, int n3) {
        if (IdeologyClient.KEY_EVOLVE.matches(n, n2)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    public boolean isPauseScreen() {
        return false;
    }
}
