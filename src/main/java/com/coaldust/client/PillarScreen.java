/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.coaldust.client;

import com.coaldust.CoalDust;
import com.coaldust.client.EvolveScreen;
import com.coaldust.client.IdeologyClient;
import com.coaldust.ideology.AbyssSkills;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class PillarScreen
extends Screen {
    private static final ResourceLocation NODES = CoalDust.id("textures/gui/abyss/nodes.png");
    private static final int NODE = 20;
    private static final int GAP_X = 32;
    private static final int GAP_Y = 40;
    private final Screen parent;
    private final int tree;
    private final int pillar;
    private double scrollX;
    private double scrollY;
    private boolean dragging;
    private double dragDist;
    private int ticks;
    private int builtVersion = -1;

    public PillarScreen(Screen screen, int n, int n2) {
        super((Component)Component.translatable((String)("ideology.coaldust.evolve.pillar." + EvolveScreen.heads(n)[n2])));
        this.parent = screen;
        this.tree = n;
        this.pillar = n2;
    }

    private int sideW() {
        return Math.min(170, Math.max(120, this.width / 4));
    }

    private int areaX() {
        return 8;
    }

    private int areaY() {
        return 26;
    }

    private int areaW() {
        return this.width - this.sideW() - 20;
    }

    private int areaH() {
        return this.height - 34;
    }

    private int contentW() {
        return 384;
    }

    private int contentH() {
        return 440;
    }

    private int nodeX(AbyssSkills.Skill skill) {
        int n = this.areaX() + Math.max(0, (this.areaW() - this.contentW()) / 2);
        return n + skill.col * 32 + 6 - (int)this.scrollX;
    }

    private int nodeY(AbyssSkills.Skill skill) {
        return this.areaY() + 10 + skill.tier * 40 - (int)this.scrollY;
    }

    private void clampScroll() {
        this.scrollX = Mth.clamp((double)this.scrollX, (double)0.0, (double)Math.max(0, this.contentW() - this.areaW()));
        this.scrollY = Mth.clamp((double)this.scrollY, (double)0.0, (double)Math.max(0, this.contentH() + 10 - this.areaH()));
    }

    protected void init() {
        this.builtVersion = IdeologyClient.version;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.evolve.back"), button -> this.onClose()).bounds(8, 5, 70, 16).build());
        this.clampScroll();
    }

    public void tick() {
        super.tick();
        if (++this.ticks % 40 == 0) {
            IdeologyClient.request();
        }
        if (this.builtVersion != IdeologyClient.version) {
            this.builtVersion = IdeologyClient.version;
        }
    }

    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    private static boolean open(AbyssSkills.Skill skill, Map<String, Integer> map) {
        if (skill.parents.length == 0) {
            return true;
        }
        for (String string : skill.parents) {
            if (map.getOrDefault(string, 0) <= 0) continue;
            return true;
        }
        return false;
    }

    static String fmt(AbyssSkills.Stat stat, double d) {
        return PillarScreen.fmt(stat, d, true);
    }

    static String fmt(AbyssSkills.Stat stat, double d, boolean bl) {
        switch (stat.fmt) {
            case "%": {
                return "+" + PillarScreen.num(d * 100.0) + "%";
            }
            case "s": {
                return "+" + PillarScreen.num(d) + " \u0441";
            }
            case "m": {
                return "\u2212" + PillarScreen.num(d) + " \u043c\u0438\u043d";
            }
            case "x": {
                return "+" + PillarScreen.num(d * 100.0) + "%";
            }
            case "t": {
                return "+" + PillarScreen.num(d * 100.0 / 20.0) + "%";
            }
        }
        return "+" + (stat.whole && bl ? String.valueOf((int)Math.floor(d + 1.0E-6)) : PillarScreen.num(d));
    }

    private static String num(double d) {
        if (Math.abs(d - (double)Math.round(d)) < 0.05) {
            return String.valueOf(Math.round(d));
        }
        return String.format(Locale.ROOT, Math.abs(d) < 1.0 ? "%.2f" : "%.1f", d);
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        int n3;
        boolean bl;
        super.render(guiGraphics, n, n2, f);
        Map<String, Integer> map = IdeologyClient.skillRanks();
        int n4 = IdeologyClient.data == null ? 0 : IdeologyClient.data.getInt("apoints");
        List<AbyssSkills.Skill> list = AbyssSkills.pillar(this.tree, this.pillar);
        guiGraphics.drawCenteredString(this.font, (Component)this.title.copy().withStyle(ChatFormatting.LIGHT_PURPLE), this.areaX() + this.areaW() / 2, 9, -2051841);
        String string = Component.translatable((String)"ideology.coaldust.evolve.points_short", (Object[])new Object[]{n4}).getString();
        guiGraphics.drawString(this.font, string, this.areaX() + this.areaW() - this.font.width(string), 9, -10497);
        int n5 = this.areaX();
        int n6 = this.areaY();
        int n7 = this.areaW();
        int n8 = this.areaH();
        guiGraphics.fill(n5, n6, n5 + n7, n6 + n8, -1072690666);
        guiGraphics.renderOutline(n5 - 1, n6 - 1, n7 + 2, n8 + 2, -10864006);
        guiGraphics.enableScissor(n5, n6, n5 + n7, n6 + n8);
        for (int i = 1; i <= 10; ++i) {
            int n9 = this.areaY() + 10 + i * 40 - (int)this.scrollY;
            guiGraphics.fill(n5, n9 - 20 + 2, n5 + n7, n9 - 20 + 3, 548438271);
            guiGraphics.drawString(this.font, String.valueOf(i), n5 + 3, n9 + 6, 1617582720, false);
        }
        for (AbyssSkills.Skill skill : list) {
            for (String string2 : skill.parents) {
                AbyssSkills.Skill skill2 = AbyssSkills.byId(string2);
                if (skill2 == null) continue;
                bl = map.getOrDefault(string2, 0) > 0 && map.getOrDefault(skill.id, 0) > 0;
                n3 = map.getOrDefault(string2, 0) > 0 ? 1 : 0;
                PillarScreen.link(guiGraphics, this.nodeX(skill2) + 10, this.nodeY(skill2) + 20 + 5, this.nodeX(skill) + 10, this.nodeY(skill) - 1, bl ? -3109889 : (n3 != 0 ? -8758630 : -13424321), bl);
            }
        }
        Object object = null;
        boolean bl2 = n >= n5 && n < n5 + n7 && n2 >= n6 && n2 < n6 + n8;
        for (AbyssSkills.Skill skill : list) {
            int n10 = this.nodeX(skill);
            int n11 = this.nodeY(skill);
            if (n10 + 20 < n5 - 4 || n10 > n5 + n7 + 4 || n11 + 20 < n6 - 8 || n11 > n6 + n8 + 4) continue;
            int n12 = map.getOrDefault(skill.id, 0);
            bl = PillarScreen.open(skill, map);
            n3 = n12 >= skill.max ? 3 : (n12 > 0 ? 2 : (bl ? 1 : 0));
            RenderSystem.enableBlend();
            int n13 = skill.tier == 0 ? 6 : 3;
            guiGraphics.blit(NODES, n10 - n13, n11 - n13, 20 + n13 * 2, 20 + n13 * 2, (float)(n3 * 128), 0.0f, 128, 128, 512, 512);
            if (n3 == 0) {
                RenderSystem.setShaderColor((float)0.35f, (float)0.3f, (float)0.4f, (float)1.0f);
            }
            guiGraphics.renderItem(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)skill.icon()))), n10 + 2, n11 + 2);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            for (int i = 0; i < skill.max; ++i) {
                int n14 = n10 + 10 - skill.max * 3 + i * 6 + 1;
                guiGraphics.fill(n14, n11 + 20 + 2, n14 + 4, n11 + 20 + 5, i < n12 ? -1657857 : -14015949);
            }
            if (n3 == 1 && n4 >= skill.cost(n12) && this.ticks / 10 % 2 == 0) {
                guiGraphics.renderOutline(n10 - 2, n11 - 2, 24, 24, -8070);
            }
            if (!bl2 || n < n10 || n >= n10 + 20 || n2 < n11 || n2 >= n11 + 20) continue;
            object = skill;
        }
        guiGraphics.disableScissor();
        if (this.contentH() + 10 > n8) {
            int n15 = Math.max(20, n8 * n8 / (this.contentH() + 10));
            int n16 = n6 + (int)((double)(n8 - n15) * this.scrollY / (double)Math.max(1, this.contentH() + 10 - n8));
            guiGraphics.fill(n5 + n7 - 3, n16, n5 + n7 - 1, n16 + n15, -1597867009);
        }
        guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.evolve.drag"), n5 + n7 / 2, n6 + n8 - 10, -2139460982);
        this.side(guiGraphics, map, n, n2);
        if (object != null && !this.dragging) {
            this.tooltip(guiGraphics, (AbyssSkills.Skill)object, map, n4, n, n2);
        }
    }

    private void side(GuiGraphics guiGraphics, Map<String, Integer> map, int n, int n2) {
        int n3 = this.width - this.sideW() - 6;
        int n4 = this.areaY();
        int n5 = this.sideW();
        int n6 = this.areaH();
        guiGraphics.fill(n3, n4, n3 + n5, n4 + n6, -1072690666);
        guiGraphics.renderOutline(n3 - 1, n4 - 1, n5 + 2, n6 + 2, -10864006);
        guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.evolve.totals"), n3 + n5 / 2, n4 + 5, -3563265);
        int n7 = n4 + 18;
        for (AbyssSkills.Stat stat : AbyssSkills.stats(this.tree, this.pillar)) {
            double d = AbyssSkills.total(map, stat.id);
            guiGraphics.renderItem(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)stat.icon))), n3 + 4, n7);
            String string = Component.translatable((String)("ideology.coaldust.stat." + stat.id)).getString();
            String string2 = PillarScreen.fmt(stat, d);
            guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(string, n5 - 26), n3 + 22, n7, d > 0.0 ? -1650433 : -7701862, false);
            String string3 = Component.translatable((String)"ideology.coaldust.evolve.of", (Object[])new Object[]{string2, PillarScreen.fmt(stat, stat.cap)}).getString();
            guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(string3, n5 - 26), n3 + 22, n7 + 9, d > 0.0 ? -6488164 : -9807238, false);
            if (n >= n3 && n < n3 + n5 && n2 >= n7 - 1 && n2 < n7 + 19) {
                ArrayList arrayList = new ArrayList(this.font.split((FormattedText)Component.translatable((String)("ideology.coaldust.stat." + stat.id + ".desc")), 200));
                guiGraphics.renderTooltip(this.font, arrayList, n, n2);
            }
            if ((n7 += 22) <= n4 + n6 - 20) continue;
            break;
        }
    }

    private void tooltip(GuiGraphics guiGraphics, AbyssSkills.Skill skill, Map<String, Integer> map, int n, int n2, int n3) {
        int n4 = map.getOrDefault(skill.id, 0);
        ArrayList<FormattedCharSequence> arrayList = new ArrayList<FormattedCharSequence>();
        if (skill.stat == null) {
            arrayList.add(Component.translatable((String)"ideology.coaldust.evolve.root", (Object[])new Object[]{this.title}).withStyle(ChatFormatting.LIGHT_PURPLE).getVisualOrderText());
            arrayList.addAll(this.font.split((FormattedText)Component.translatable((String)"ideology.coaldust.evolve.root.desc").withStyle(ChatFormatting.GRAY), 210));
        } else {
            arrayList.add(Component.translatable((String)("ideology.coaldust.stat." + skill.stat.id)).withStyle(ChatFormatting.LIGHT_PURPLE).getVisualOrderText());
            arrayList.addAll(this.font.split((FormattedText)Component.translatable((String)("ideology.coaldust.stat." + skill.stat.id + ".desc")).withStyle(ChatFormatting.GRAY), 210));
            arrayList.add(Component.translatable((String)"ideology.coaldust.evolve.per_rank", (Object[])new Object[]{PillarScreen.fmt(skill.stat, skill.stat.perRank(), false)}).withStyle(ChatFormatting.AQUA).getVisualOrderText());
            arrayList.add(Component.translatable((String)"ideology.coaldust.evolve.total", (Object[])new Object[]{PillarScreen.fmt(skill.stat, AbyssSkills.total(map, skill.stat.id)), PillarScreen.fmt(skill.stat, skill.stat.cap)}).withStyle(ChatFormatting.DARK_AQUA).getVisualOrderText());
        }
        arrayList.add(Component.translatable((String)"ideology.coaldust.evolve.tier", (Object[])new Object[]{skill.tier}).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
        arrayList.add(Component.translatable((String)"ideology.coaldust.evolve.rank", (Object[])new Object[]{n4, skill.max}).getVisualOrderText());
        if (n4 < skill.max) {
            int n5 = skill.cost(n4);
            arrayList.add(Component.translatable((String)"ideology.coaldust.evolve.cost", (Object[])new Object[]{n5}).withStyle(n >= n5 ? ChatFormatting.GREEN : ChatFormatting.RED).getVisualOrderText());
            if (!PillarScreen.open(skill, map)) {
                arrayList.add(Component.translatable((String)"ideology.coaldust.evolve.needs_link").withStyle(ChatFormatting.DARK_RED).getVisualOrderText());
            }
        }
        guiGraphics.renderTooltip(this.font, arrayList, n2, n3);
    }

    private static void link(GuiGraphics guiGraphics, int n, int n2, int n3, int n4, int n5, boolean bl) {
        int n6;
        int n7 = (n2 + n4) / 2;
        int n8 = n6 = bl ? 2 : 1;
        if (bl) {
            int n9 = 1085300991;
            guiGraphics.fill(n - 2, n2, n + 3, n7 + 2, n9);
            guiGraphics.fill(Math.min(n, n3) - 2, n7 - 2, Math.max(n, n3) + 3, n7 + 3, n9);
            guiGraphics.fill(n3 - 2, n7, n3 + 3, n4, n9);
        }
        guiGraphics.fill(n - n6 / 2, n2, n - n6 / 2 + n6, n7 + 1, n5);
        guiGraphics.fill(Math.min(n, n3) - n6 / 2, n7 - n6 / 2, Math.max(n, n3) + n6 - n6 / 2, n7 - n6 / 2 + n6, n5);
        guiGraphics.fill(n3 - n6 / 2, n7, n3 - n6 / 2 + n6, n4, n5);
    }

    public boolean mouseClicked(double d, double d2, int n) {
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        this.dragDist = 0.0;
        this.dragging = d >= (double)this.areaX() && d < (double)(this.areaX() + this.areaW()) && d2 >= (double)this.areaY() && d2 < (double)(this.areaY() + this.areaH());
        return this.dragging;
    }

    public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        if (this.dragging) {
            this.dragDist += Math.abs(d3) + Math.abs(d4);
            this.scrollX -= d3;
            this.scrollY -= d4;
            this.clampScroll();
            return true;
        }
        return super.mouseDragged(d, d2, n, d3, d4);
    }

    public boolean mouseReleased(double d, double d2, int n) {
        boolean bl = this.dragging;
        this.dragging = false;
        if (bl && this.dragDist < 4.0 && n == 0) {
            for (AbyssSkills.Skill skill : AbyssSkills.pillar(this.tree, this.pillar)) {
                int n2 = this.nodeX(skill);
                int n3 = this.nodeY(skill);
                if (!(d >= (double)n2) || !(d < (double)(n2 + 20)) || !(d2 >= (double)n3) || !(d2 < (double)(n3 + 20))) continue;
                IdeologyClient.send("ideology evolve " + skill.id);
                IdeologyClient.request();
                return true;
            }
        }
        return super.mouseReleased(d, d2, n) || bl;
    }

    public boolean mouseScrolled(double d, double d2, double d3, double d4) {
        if (PillarScreen.hasShiftDown()) {
            this.scrollX -= d4 * 30.0;
        } else {
            this.scrollY -= d4 * 30.0;
        }
        this.scrollX -= d3 * 30.0;
        this.clampScroll();
        return true;
    }

    public boolean keyPressed(int n, int n2, int n3) {
        if (IdeologyClient.KEY_EVOLVE.matches(n, n2)) {
            Minecraft.getInstance().setScreen(null);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    public boolean isPauseScreen() {
        return false;
    }
}
