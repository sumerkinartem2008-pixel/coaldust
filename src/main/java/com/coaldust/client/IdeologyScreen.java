/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.MultiLineEditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 */
package com.coaldust.client;

import com.coaldust.CoalDust;
import com.coaldust.client.AbyssPasswordScreen;
import com.coaldust.client.EvolveScreen;
import com.coaldust.client.IdeologyClient;
import com.coaldust.ideology.Ideology;
import com.coaldust.ideology.IdeologyStructure;
import com.coaldust.ideology.Trait;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class IdeologyScreen
extends Screen {
    private static final int LEFT_W = 108;
    private static final int RIGHT_W = 124;
    private static final int GAP = 6;
    private static final int ROW = 18;
    private static final String[] ICONS = Ideology.EMBLEMS;
    private Mode mode = Mode.MAIN;
    private String selected;
    private int builtVersion = -1;
    private int scroll;
    private int ticks;
    private EditBox nameBox;
    private MultiLineEditBox descBox;
    private String editName = "";
    private String editDesc = "";
    private int editColor = Ideology.COLORS[15];
    private String editIcon = Ideology.defaultEmblem(Trait.NATURE);
    private boolean emblemChosen;
    private IdeologyStructure editStructure = IdeologyStructure.CHURCH;
    private Trait editTrait = Trait.NATURE;
    private static final ResourceLocation SKIN_NORMAL = CoalDust.id("textures/gui/ideology/menu.png");
    private static final ResourceLocation SKIN_ABYSS = CoalDust.id("textures/gui/ideology/menu_abyss.png");
    private static ResourceLocation SKIN = SKIN_NORMAL;
    static boolean ABYSS;
    private static final int AT = 1024;
    private int[] rerollBox;

    public IdeologyScreen() {
        super((Component)Component.translatable((String)"ideology.coaldust.screen.title"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen((Screen)new IdeologyScreen());
        IdeologyClient.request();
    }

    private static String sized(int n) {
        return n <= 16 ? "_64" : (n <= 48 ? "_128" : "");
    }

    static ResourceLocation emblem(String string, int n) {
        return CoalDust.id("textures/gui/ideology/emblem/" + (Ideology.validIcon(string) ? string : "sun") + IdeologyScreen.sized(n) + ".png");
    }

    static ResourceLocation icon(String string, int n) {
        return CoalDust.id("textures/gui/ideology/" + string + IdeologyScreen.sized(n) + ".png");
    }

    static ResourceLocation icon(String string) {
        return CoalDust.id("textures/gui/ideology/" + string + ".png");
    }

    private CompoundTag data() {
        return IdeologyClient.data == null ? new CompoundTag() : IdeologyClient.data;
    }

    private String me() {
        return this.data().getString("me");
    }

    private boolean member() {
        return !this.me().isEmpty();
    }

    private boolean leader() {
        return this.member() && this.data().getBoolean("leader");
    }

    private List<CompoundTag> list() {
        CompoundTag compoundTag = this.data();
        ArrayList<CompoundTag> arrayList = new ArrayList<CompoundTag>();
        int n = compoundTag.getInt("n");
        for (int i = 0; i < n; ++i) {
            arrayList.add(compoundTag.getCompound("i" + i));
        }
        return arrayList;
    }

    private CompoundTag find(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        for (CompoundTag compoundTag : this.list()) {
            if (!compoundTag.getString("id").equals(string)) continue;
            return compoundTag;
        }
        return null;
    }

    private CompoundTag shown() {
        CompoundTag compoundTag = this.find(this.selected);
        if (compoundTag == null && this.member()) {
            compoundTag = this.find(this.me());
        }
        return compoundTag;
    }

    private static List<String> split(String string, char c) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (string == null) {
            return arrayList;
        }
        for (String string2 : string.split(String.valueOf(c))) {
            if (string2.isEmpty()) continue;
            arrayList.add(string2);
        }
        return arrayList;
    }

    private int pw() {
        return Math.min(this.width - 28, 500);
    }

    private int ph() {
        return Math.min(this.height - 46, 256);
    }

    private int x0() {
        return (this.width - this.pw()) / 2;
    }

    private int y0() {
        return (this.height - this.ph()) / 2 + 4;
    }

    private int cx() {
        return this.x0() + 108 + 6;
    }

    private int cw() {
        return this.pw() - 108 - 124 - 12;
    }

    private int rx() {
        return this.x0() + this.pw() - 124;
    }

    private int listTop() {
        return this.y0() + 32;
    }

    private int listRows() {
        return Math.max(1, (this.y0() + this.ph() - 6 - this.listTop()) / 18);
    }

    protected void init() {
        CompoundTag compoundTag;
        Iterator<String> iterator;
        int n;
        int n2;
        boolean bl;
        this.builtVersion = IdeologyClient.version;
        int n3 = this.x0();
        int n4 = this.y0();
        int n5 = this.pw();
        int n6 = this.ph();
        if (this.mode == Mode.CREATE || this.mode == Mode.EDIT) {
            this.initEditor(n3, n4, n5, n6);
            return;
        }
        int n7 = this.cx();
        int n8 = this.cw();
        CompoundTag compoundTag2 = this.shown();
        boolean bl2 = bl = compoundTag2 != null && compoundTag2.getString("id").equals(this.me());
        if (!this.member()) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.create_new"), button -> this.startEditor(Mode.CREATE, null)).bounds(n7, n4 + 14, n8, 16).build());
        } else if (!bl) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.mine"), button -> {
                this.selected = this.me();
                this.rebuild();
            }).bounds(n7, n4 + 14, n8, 16).build());
        } else if (this.leader()) {
            n2 = this.data().getInt("picks") > 0 ? n8 / 2 - 1 : n8;
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.edit"), button -> this.startEditor(Mode.EDIT, compoundTag2)).bounds(n7, n4 + 14, n2, 16).build());
            if (n2 != n8) {
                this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.pick_btn", (Object[])new Object[]{this.data().getInt("picks")}), button -> {
                    this.mode = this.mode == Mode.PICK ? Mode.MAIN : Mode.PICK;
                    this.rebuild();
                }).bounds(n7 + n2 + 2, n4 + 14, n8 - n2 - 2, 16).build());
            }
        }
        n2 = n4 + n6 - 22;
        if (this.member() && bl) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.leave"), button -> {
                IdeologyClient.send("ideology leave");
                this.selected = null;
                IdeologyClient.request();
            }).bounds(n7 + n8 / 2 - 50, n2, 100, 18).build());
        } else if (!this.member() && compoundTag2 != null) {
            n = compoundTag2.getBoolean("requested");
            iterator = compoundTag2.getString("id");
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)(n != 0 ? "ideology.coaldust.screen.request_cancel" : "ideology.coaldust.screen.request")), arg_0 -> IdeologyScreen.lambda$init$5((String)((Object)iterator), arg_0)).bounds(n7 + n8 / 2 - 60, n2, 120, 18).build());
        }
        if (this.abyss() && this.member() && (compoundTag = this.find(this.me())) != null && (compoundTag.getString("traits").contains("evolving") || compoundTag.getString("traits").contains("summoner"))) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.evolve.open"), button -> EvolveScreen.open()).bounds(n3 + 4, n4 + n6 - 20, 100, 14).build());
        }
        if (this.member()) {
            String[] stringArray;
            n = n4 + 26;
            if (this.leader() && !(iterator = IdeologyScreen.split(this.data().getString("requests"), ';')).isEmpty()) {
                n += 11;
                Iterator object = iterator.iterator();
                while (object.hasNext()) {
                    stringArray = (String[])object.next();
                    this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"\u2714"), arg_0 -> IdeologyScreen.lambda$init$7((String)stringArray, arg_0)).bounds(n3 + 108 - 30, n, 14, 14).build());
                    this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"\u2716"), arg_0 -> IdeologyScreen.lambda$init$8((String)stringArray, arg_0)).bounds(n3 + 108 - 15, n, 14, 14).build());
                    n += 16;
                }
                n += 4;
            }
            n += 11;
            if (this.leader()) {
                for (String string : IdeologyScreen.split(this.data().getString("members"), ';')) {
                    stringArray = string.split("\\|");
                    if (stringArray.length >= 3 && !stringArray[2].equals("1")) {
                        String string2 = stringArray[0];
                        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"\u2716"), button -> {
                            IdeologyClient.send("ideology kick " + string);
                            IdeologyClient.request();
                        }).bounds(n3 + 108 - 15, n - 2, 12, 12).build());
                    }
                    n += 12;
                }
            }
        }
    }

    private void initEditor(int n, int n2, int n3, int n4) {
        int n5 = n + 90;
        int n6 = Math.min(n3 - 150, 300);
        this.nameBox = new EditBox(this.font, n5, n2 + 22, n6, 16, (Component)Component.translatable((String)"ideology.coaldust.screen.name"));
        this.nameBox.setMaxLength(32);
        this.nameBox.setValue(this.editName);
        this.addRenderableWidget((GuiEventListener)this.nameBox);
        this.descBox = new MultiLineEditBox(this.font, n5, n2 + 42, n6, 44, (Component)Component.translatable((String)"ideology.coaldust.screen.desc_hint"), (Component)Component.translatable((String)"ideology.coaldust.screen.desc"));
        this.descBox.setCharacterLimit(200);
        this.descBox.setValue(this.editDesc);
        this.addRenderableWidget((GuiEventListener)this.descBox);
        int n7 = n2 + n4 - 24;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)(this.mode == Mode.CREATE ? "ideology.coaldust.screen.create" : "ideology.coaldust.screen.save")), button -> this.submitEditor()).bounds(n + n3 / 2 - 104, n7, 100, 18).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"ideology.coaldust.screen.cancel"), button -> {
            this.mode = Mode.MAIN;
            this.rebuild();
        }).bounds(n + n3 / 2 + 4, n7, 100, 18).build());
    }

    private void startEditor(Mode mode, CompoundTag compoundTag) {
        this.mode = mode;
        if (compoundTag != null) {
            this.nameBox = null;
            this.descBox = null;
            this.editName = compoundTag.getString("name");
            this.editDesc = compoundTag.getString("desc");
            this.editColor = compoundTag.getInt("color");
            this.editIcon = compoundTag.getString("icon");
            this.emblemChosen = true;
            IdeologyStructure ideologyStructure = IdeologyStructure.byId(compoundTag.getString("structure"));
            this.editStructure = ideologyStructure == null ? IdeologyStructure.CHURCH : ideologyStructure;
        } else {
            this.editName = "";
            this.editDesc = "";
            this.emblemChosen = false;
            this.editIcon = Ideology.defaultEmblem(this.editTrait);
        }
        this.nameBox = null;
        this.descBox = null;
        this.rebuildWidgets();
    }

    private static String clean(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : string.toCharArray()) {
            stringBuilder.append(c < ' ' || c == '\u00a7' || c == '\u007f' ? (char)' ' : c);
        }
        return stringBuilder.toString().trim().replaceAll(" +", " ");
    }

    private void submitEditor() {
        String string = IdeologyScreen.clean(this.nameBox.getValue());
        String string2 = IdeologyScreen.clean(this.descBox.getValue());
        if (string2.length() > 200) {
            string2 = string2.substring(0, 200);
        }
        if (string.isEmpty()) {
            return;
        }
        if (this.mode == Mode.CREATE) {
            IdeologyClient.send("ideology found " + this.editStructure.id + " " + this.editTrait.id + " " + this.editColor + " " + this.editIcon + " " + string);
            if (!string2.isEmpty()) {
                IdeologyClient.send("ideology set desc " + string2);
            }
            this.selected = null;
        } else {
            CompoundTag compoundTag = this.find(this.me());
            if (compoundTag == null || !string.equals(compoundTag.getString("name"))) {
                IdeologyClient.send("ideology set name " + string);
            }
            if (compoundTag == null || !string2.equals(compoundTag.getString("desc"))) {
                IdeologyClient.send("ideology set desc " + (string2.isEmpty() ? "-" : string2));
            }
            if (compoundTag == null || this.editColor != compoundTag.getInt("color")) {
                IdeologyClient.send("ideology set color " + this.editColor);
            }
            if (compoundTag == null || !this.editIcon.equals(compoundTag.getString("icon"))) {
                IdeologyClient.send("ideology set icon " + this.editIcon);
            }
            if (compoundTag == null || !this.editStructure.id.equals(compoundTag.getString("structure"))) {
                IdeologyClient.send("ideology set structure " + this.editStructure.id);
            }
        }
        IdeologyClient.request();
        this.mode = Mode.MAIN;
        this.rebuild();
    }

    private void rebuild() {
        if (this.nameBox != null) {
            this.editName = this.nameBox.getValue();
        }
        if (this.descBox != null) {
            this.editDesc = this.descBox.getValue();
        }
        this.nameBox = null;
        this.descBox = null;
        this.rebuildWidgets();
    }

    public void tick() {
        super.tick();
        if (++this.ticks % 40 == 0) {
            IdeologyClient.request();
        }
        if (IdeologyClient.version != this.builtVersion && this.mode != Mode.CREATE && this.mode != Mode.EDIT) {
            if (!this.member() && this.mode == Mode.PICK) {
                this.mode = Mode.MAIN;
            }
            this.rebuild();
        }
    }

    public boolean keyPressed(int n, int n2, int n3) {
        if (this.mode != Mode.CREATE && this.mode != Mode.EDIT && IdeologyClient.KEY.matches(n, n2)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    public boolean isPauseScreen() {
        return false;
    }

    static int gold() {
        return ABYSS ? 13667327 : 16765514;
    }

    private boolean abyss() {
        return this.data().getBoolean("abyss");
    }

    private List<Trait> traits() {
        ArrayList<Trait> arrayList = new ArrayList<Trait>();
        for (Trait trait : Trait.values()) {
            if (trait.abyss && !this.abyss()) continue;
            arrayList.add(trait);
        }
        return arrayList;
    }

    private boolean secret(int n, int n2) {
        return n >= this.x0() - 12 && n < this.x0() + 14 && n2 >= this.y0() - 12 && n2 < this.y0() + 14;
    }

    private static void part(GuiGraphics guiGraphics, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        if (n3 <= 0 || n4 <= 0) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(SKIN, n, n2, n3, n4, (float)n5, (float)n6, n7, n8, 1024, 1024);
    }

    private static void nine(GuiGraphics guiGraphics, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10) {
        n10 = Math.min(n10, Math.min(n3, n4) / 2);
        int n11 = n3 - 2 * n10;
        int n12 = n4 - 2 * n10;
        int n13 = n7 - 2 * n9;
        int n14 = n8 - 2 * n9;
        IdeologyScreen.part(guiGraphics, n, n2, n10, n10, n5, n6, n9, n9);
        IdeologyScreen.part(guiGraphics, n + n3 - n10, n2, n10, n10, n5 + n7 - n9, n6, n9, n9);
        IdeologyScreen.part(guiGraphics, n, n2 + n4 - n10, n10, n10, n5, n6 + n8 - n9, n9, n9);
        IdeologyScreen.part(guiGraphics, n + n3 - n10, n2 + n4 - n10, n10, n10, n5 + n7 - n9, n6 + n8 - n9, n9, n9);
        IdeologyScreen.part(guiGraphics, n + n10, n2, n11, n10, n5 + n9, n6, n13, n9);
        IdeologyScreen.part(guiGraphics, n + n10, n2 + n4 - n10, n11, n10, n5 + n9, n6 + n8 - n9, n13, n9);
        IdeologyScreen.part(guiGraphics, n, n2 + n10, n10, n12, n5, n6 + n9, n9, n14);
        IdeologyScreen.part(guiGraphics, n + n3 - n10, n2 + n10, n10, n12, n5 + n7 - n9, n6 + n9, n9, n14);
        IdeologyScreen.part(guiGraphics, n + n10, n2 + n10, n11, n12, n5 + n9, n6 + n9, n13, n14);
    }

    private static void three(GuiGraphics guiGraphics, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10) {
        n10 = Math.min(n10, n3 / 2);
        IdeologyScreen.part(guiGraphics, n, n2, n10, n4, n5, n6, n9, n8);
        IdeologyScreen.part(guiGraphics, n + n3 - n10, n2, n10, n4, n5 + n7 - n9, n6, n9, n8);
        IdeologyScreen.part(guiGraphics, n + n10, n2, n3 - 2 * n10, n4, n5 + n9, n6, n7 - 2 * n9, n8);
    }

    private static void tint(int n, float f) {
        RenderSystem.setShaderColor((float)((float)(n >> 16 & 0xFF) / 255.0f), (float)((float)(n >> 8 & 0xFF) / 255.0f), (float)((float)(n & 0xFF) / 255.0f), (float)f);
    }

    private static void untint() {
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static void bar(GuiGraphics guiGraphics, int n, int n2, int n3, int n4, double d, int n5) {
        IdeologyScreen.three(guiGraphics, n, n2, n3, n4, 512, 592, 512, 32, 16, n4 / 2);
        int n6 = (int)Math.round((double)n3 * Math.max(0.0, Math.min(1.0, d)));
        if (n6 >= n4) {
            IdeologyScreen.tint(n5, 1.0f);
            IdeologyScreen.three(guiGraphics, n, n2, n6, n4, 512, 640, 512, 32, 16, n4 / 2);
            IdeologyScreen.untint();
        }
    }

    private static void divider(GuiGraphics guiGraphics, int n, int n2, int n3) {
        IdeologyScreen.three(guiGraphics, n, n2, n3, 6, 512, 384, 512, 32, 64, 12);
    }

    public void renderBackground(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.renderBackground(guiGraphics, n, n2, f);
        ABYSS = this.abyss();
        SKIN = ABYSS ? SKIN_ABYSS : SKIN_NORMAL;
        int n3 = this.x0();
        int n4 = this.y0();
        int n5 = this.pw();
        int n6 = this.ph();
        IdeologyScreen.nine(guiGraphics, n3 - 12, n4 - 12, n5 + 24, n6 + 24, 0, 0, 512, 512, 128, 28);
        if (this.mode == Mode.CREATE || this.mode == Mode.EDIT) {
            IdeologyScreen.panel(guiGraphics, n3, n4, n5, n6, -7701936);
        } else {
            IdeologyScreen.panel(guiGraphics, n3, n4, 108, n6, 0);
            CompoundTag compoundTag = this.shown();
            IdeologyScreen.panel(guiGraphics, this.cx(), n4, this.cw(), n6, compoundTag == null ? 0 : 0xFF000000 | compoundTag.getInt("color"));
            int n7 = Math.min(this.cw(), n6) - 20;
            IdeologyScreen.part(guiGraphics, this.cx() + (this.cw() - n7) / 2, n4 + (n6 - n7) / 2, n7, n7, 0, 512, 512, 512);
            IdeologyScreen.panel(guiGraphics, this.rx(), n4, 124, n6, 0);
        }
        int n8 = Math.min(180, n5 - 40);
        IdeologyScreen.three(guiGraphics, n3 + (n5 - n8) / 2, n4 - 21, n8, 18, 512, 256, 512, 96, 96, 18);
    }

    private static void panel(GuiGraphics guiGraphics, int n, int n2, int n3, int n4, int n5) {
        IdeologyScreen.nine(guiGraphics, n, n2, n3, n4, 512, 0, 256, 256, 48, 10);
        if (n5 != 0) {
            guiGraphics.renderOutline(n + 2, n2 + 2, n3 - 4, n4 - 4, n5 & 0xFFFFFF | 0x90000000);
        }
    }

    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        super.render(guiGraphics, n, n2, f);
        guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.title"), this.width / 2, this.y0() - 16, ABYSS ? -2051841 : -863622);
        if (IdeologyClient.data == null) {
            guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.loading"), this.width / 2, this.height / 2, 0xAAAAAA);
            return;
        }
        if (this.mode == Mode.CREATE || this.mode == Mode.EDIT) {
            this.renderEditor(guiGraphics, n, n2);
            return;
        }
        this.renderLeft(guiGraphics, n, n2);
        this.renderRight(guiGraphics, n, n2);
        if (this.mode == Mode.PICK) {
            this.renderPick(guiGraphics, n, n2);
        } else {
            this.renderCenter(guiGraphics, n, n2);
        }
    }

    private void renderLeft(GuiGraphics guiGraphics, int n, int n2) {
        List<String> list;
        int n3 = this.x0() + 5;
        int n4 = this.y0() + 5;
        if (!this.member()) {
            List list2 = this.font.split((FormattedText)Component.translatable((String)"ideology.coaldust.screen.no_ideology"), 98);
            int n5 = n4 + 26;
            for (FormattedCharSequence formattedCharSequence : list2) {
                guiGraphics.drawString(this.font, formattedCharSequence, n3, n5, 0x888888);
                n5 += 10;
            }
            return;
        }
        int n6 = this.y0() + 26;
        if (this.leader() && !(list = IdeologyScreen.split(this.data().getString("requests"), ';')).isEmpty()) {
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.requests", (Object[])new Object[]{list.size()}), n3, n6, 0x9CFF9C);
            n6 += 11;
            for (String string : list) {
                guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(string, 68), n3, n6 + 3, 0xFFFFFF);
                n6 += 16;
            }
            n6 += 4;
        }
        list = IdeologyScreen.split(this.data().getString("members"), ';');
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.members_n", (Object[])new Object[]{list.size()}), n3, n6, IdeologyScreen.gold());
        n6 += 11;
        for (String string : list) {
            String[] stringArray = string.split("\\|");
            if (stringArray.length < 3) continue;
            boolean bl = stringArray[1].equals("1");
            boolean bl2 = stringArray[2].equals("1");
            String string2 = (bl2 ? "\u2605 " : "") + stringArray[0];
            guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(string2, 84), n3, n6, bl2 ? IdeologyScreen.gold() : (bl ? 0xFFFFFF : 0x777777));
            if ((n6 += 12) <= this.y0() + this.ph() - 10) continue;
            break;
        }
    }

    private void renderRight(GuiGraphics guiGraphics, int n, int n2) {
        int n3;
        int n4;
        int n5 = this.rx();
        int n6 = this.y0();
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.existing"), n5 + 5, n6 + 5, IdeologyScreen.gold());
        List<CompoundTag> list = this.list();
        if (list.isEmpty()) {
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.none"), n5 + 5, n6 + 20, 0x777777);
            return;
        }
        int n7 = this.listRows();
        this.scroll = Math.max(0, Math.min(this.scroll, list.size() - n7));
        CompoundTag compoundTag = this.shown();
        for (n4 = 0; n4 < n7 && n4 + this.scroll < list.size(); ++n4) {
            CompoundTag compoundTag2 = list.get(n4 + this.scroll);
            n3 = this.listTop() + n4 * 18;
            boolean bl = compoundTag != null && compoundTag.getString("id").equals(compoundTag2.getString("id"));
            boolean bl2 = n >= n5 + 2 && n < n5 + 124 - 2 && n2 >= n3 && n2 < n3 + 18 - 1;
            IdeologyScreen.three(guiGraphics, n5 + 2, n3, 118, 17, 512, 512, 512, 64, 32, 6);
            if (bl || bl2) {
                if (!bl) {
                    IdeologyScreen.tint(0xFFFFFF, 0.5f);
                }
                IdeologyScreen.three(guiGraphics, n5 + 2, n3, 118, 17, 512, 448, 512, 64, 60, 10);
                IdeologyScreen.untint();
            }
            guiGraphics.blit(IdeologyScreen.emblem(compoundTag2.getString("icon"), 16), n5 + 4, n3 + 1, 0.0f, 0.0f, 16, 16, 16, 16);
            String string = this.font.plainSubstrByWidth(compoundTag2.getString("name"), 94);
            guiGraphics.drawString(this.font, string, n5 + 23, n3 + 1, compoundTag2.getInt("color"));
            String string2 = "\u2605" + compoundTag2.getInt("level") + "  \u263a" + compoundTag2.getInt("count") + (compoundTag2.getBoolean("requested") ? "  \u231b" : "") + (compoundTag2.getString("id").equals(this.me()) ? "  \u2714" : "");
            guiGraphics.drawString(this.font, string2, n5 + 23, n3 + 9, 0x999999);
        }
        if (list.size() > n7) {
            n4 = Math.max(8, n7 * 18 * n7 / list.size());
            int n8 = n7 * 18 - n4;
            n3 = this.listTop() + (list.size() - n7 == 0 ? 0 : n8 * this.scroll / (list.size() - n7));
            guiGraphics.fill(n5 + 124 - 3, n3, n5 + 124 - 1, n3 + n4, -7829368);
        }
    }

    private void renderCenter(GuiGraphics guiGraphics, int n, int n2) {
        String string;
        String[] stringArray;
        int n3;
        int n4;
        String string2;
        int n5 = this.cx();
        int n6 = this.cw();
        int n7 = this.y0() + 34;
        CompoundTag compoundTag = this.shown();
        if (compoundTag == null) {
            List list = this.font.split((FormattedText)Component.translatable((String)"ideology.coaldust.screen.empty"), n6 - 20);
            int n8 = this.y0() + this.ph() / 2 - list.size() * 5;
            for (FormattedCharSequence formattedCharSequence : list) {
                guiGraphics.drawString(this.font, formattedCharSequence, n5 + 10, n8, 0x777777);
                n8 += 10;
            }
            return;
        }
        int n9 = compoundTag.getInt("color");
        guiGraphics.blit(IdeologyScreen.emblem(compoundTag.getString("icon"), 32), n5 + 8, n7 + 2, 0.0f, 0.0f, 32, 32, 32, 32);
        int n10 = ((n9 >> 16 & 0xFF) + 255) / 2 << 16 | ((n9 >> 8 & 0xFF) + 215) / 2 << 8 | ((n9 & 0xFF) + 110) / 2;
        IdeologyScreen.tint(n10, 1.0f);
        IdeologyScreen.part(guiGraphics, n5 + 1, n7 - 5, 46, 46, 768, 0, 256, 256);
        IdeologyScreen.untint();
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(compoundTag.getString("name"), n6 - 54), n5 + 48, n7 + 1, n9);
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)("ideology.coaldust.structure." + compoundTag.getString("structure"))), n5 + 48, n7 + 12, 0xAAAAAA);
        String string3 = Component.translatable((String)"ideology.coaldust.screen.level_info", (Object[])new Object[]{compoundTag.getInt("level") + "/15", compoundTag.getInt("count"), "\u2605" + compoundTag.getString("leaderName")}).getString();
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(string3, n6 - 54), n5 + 48, n7 + 23, IdeologyScreen.gold());
        if (n >= n5 + 48 && n < n5 + n6 && n2 >= n7 + 11 && n2 < n7 + 21) {
            this.tooltip(guiGraphics, (Component)Component.translatable((String)("ideology.coaldust.structure." + compoundTag.getString("structure") + ".desc")), n, n2);
        }
        int n11 = Math.max(1, compoundTag.getInt("next"));
        int n12 = n6 - 12;
        IdeologyScreen.bar(guiGraphics, n5 + 6, (n7 += 40) - 1, n12, 6, (double)compoundTag.getInt("xp") / (double)n11, n9);
        if (n >= n5 + 6 && n < n5 + 6 + n12 && n2 >= n7 - 1 && n2 < n7 + 5) {
            this.tooltip(guiGraphics, (Component)Component.literal((String)(compoundTag.getInt("xp") + " / " + n11 + " XP")), n, n2);
        }
        n7 += 8;
        if (compoundTag.getInt("level") < 5) {
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.buffs_locked", (Object[])new Object[]{5}), n5 + 6, n7, 0xFF6B6B);
            n7 += 11;
        }
        List list = this.font.split((FormattedText)((string2 = compoundTag.getString("desc")).isEmpty() ? Component.translatable((String)"ideology.coaldust.screen.no_desc") : Component.literal((String)string2)), n6 - 12);
        int n13 = Math.min(list.size(), 4);
        for (int i = 0; i < n13; ++i) {
            guiGraphics.drawString(this.font, (FormattedCharSequence)list.get(i), n5 + 6, n7 + i * 10, string2.isEmpty() ? 0x777777 : 0xDDDDDD);
        }
        IdeologyScreen.divider(guiGraphics, n5 + 6, n7 += n13 * 10 + 1, n6 - 12);
        List<String> list2 = IdeologyScreen.split(compoundTag.getString("traits"), ',');
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.traits"), n5 + 6, (n7 += 8) + 4, 0xFFFFFF);
        int n14 = n5 + 6 + this.font.width((FormattedText)Component.translatable((String)"ideology.coaldust.screen.traits")) + 6;
        for (n4 = 0; n4 < list2.size(); ++n4) {
            int n15 = n14 + n4 * 20;
            if (n4 == 0) {
                guiGraphics.fill(n15 - 1, n7 - 1, n15 + 17, n7 + 17, -11702);
            }
            guiGraphics.fill(n15, n7, n15 + 16, n7 + 16, -15066592);
            guiGraphics.blit(IdeologyScreen.icon(list2.get(n4), 16), n15, n7, 0.0f, 0.0f, 16, 16, 16, 16);
            if (n < n15 || n >= n15 + 16 || n2 < n7 || n2 >= n7 + 16) continue;
            this.tooltipTitled(guiGraphics, (Component)Component.translatable((String)("ideology.coaldust.trait." + list2.get(n4))), (Component)Component.translatable((String)("ideology.coaldust.trait." + list2.get(n4) + ".desc")), n, n2);
        }
        n7 += 20;
        if (compoundTag.getString("id").equals(this.me())) {
            IdeologyScreen.divider(guiGraphics, n5 + 6, n7, n6 - 12);
            n7 += 7;
        }
        if (!compoundTag.getString("id").equals(this.me())) {
            this.rerollBox = null;
            return;
        }
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.quests"), n5 + 6, n7, 0xFFFFFF);
        this.rerollBox = null;
        if (this.data().getBoolean("canReroll")) {
            boolean bl;
            n4 = this.data().getInt("rerollMin");
            MutableComponent mutableComponent = n4 > 0 ? Component.translatable((String)"ideology.coaldust.screen.reroll_wait", (Object[])new Object[]{n4}) : Component.translatable((String)"ideology.coaldust.screen.reroll");
            n3 = this.font.width((FormattedText)mutableComponent);
            int n16 = n5 + n6 - 6 - n3;
            boolean bl2 = bl = n4 == 0 && n >= n16 && n < n16 + n3 && n2 >= n7 - 1 && n2 < n7 + 9;
            guiGraphics.drawString(this.font, (Component)mutableComponent, n16, n7, n4 > 0 ? 0x777777 : (bl ? 0xFFFF80 : IdeologyScreen.gold()));
            if (n4 == 0) {
                this.rerollBox = new int[]{n16, n7 - 1, n3, 10};
            }
        }
        n7 += 11;
        n4 = this.y0() + this.ph() - 26;
        List<String> list3 = IdeologyScreen.split(this.data().getString("quests"), ';');
        n3 = list3.size() > 4 ? 12 : 15;
        Iterator<String> iterator = list3.iterator();
        while (iterator.hasNext() && (stringArray = (string = iterator.next()).split(":")).length >= 3 && n7 + n3 - 1 <= n4) {
            boolean bl = stringArray.length > 3 && stringArray[3].equals("g");
            int n17 = Integer.parseInt(stringArray[1]);
            int n18 = Math.max(1, Integer.parseInt(stringArray[2]));
            String string4 = (bl ? "\u25c6 " : "") + Component.translatable((String)("ideology.coaldust.quest." + stringArray[0])).getString();
            guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(string4, n6 - 60), n5 + 6, n7, bl ? 16767370 : 0xDDDDDD);
            String string5 = n17 + "/" + n18;
            guiGraphics.drawString(this.font, string5, n5 + n6 - 6 - this.font.width(string5), n7, 0xAAAAAA);
            IdeologyScreen.bar(guiGraphics, n5 + 6, n7 + 9, n6 - 12, n3 > 12 ? 4 : 2, (double)Math.min(n17, n18) / (double)n18, bl ? 14725184 : 6017883);
            n7 += n3;
        }
    }

    private void renderPick(GuiGraphics guiGraphics, int n, int n2) {
        int n3 = this.cx();
        int n4 = this.cw();
        int n5 = this.y0() + 36;
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.pick", (Object[])new Object[]{this.data().getInt("picks")}), n3 + 6, n5, 0x9CFF9C);
        CompoundTag compoundTag = this.find(this.me());
        List list = compoundTag == null ? List.of() : IdeologyScreen.split(compoundTag.getString("traits"), ',');
        int n6 = 0;
        for (Trait trait : this.traits()) {
            boolean bl;
            int n7 = n3 + 6 + n6 % 2 * ((n4 - 12) / 2);
            int n8 = n5 + 14 + n6 / 2 * 20;
            boolean bl2 = list.contains(trait.id);
            boolean bl3 = bl = !bl2 && n >= n7 && n < n7 + (n4 - 12) / 2 - 2 && n2 >= n8 && n2 < n8 + 18;
            if (bl) {
                guiGraphics.fill(n7 - 1, n8 - 1, n7 + (n4 - 12) / 2 - 2, n8 + 18, 0x40FFFFFF);
            }
            guiGraphics.blit(IdeologyScreen.icon(trait.id, 16), n7, n8, 0.0f, 0.0f, 16, 16, 16, 16);
            guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(Component.translatable((String)("ideology.coaldust.trait." + trait.id)).getString(), (n4 - 12) / 2 - 24), n7 + 19, n8 + 4, bl2 ? 0x555555 : 0xFFFFFF);
            if (bl) {
                this.tooltip(guiGraphics, (Component)Component.translatable((String)("ideology.coaldust.trait." + trait.id + ".desc")), n, n2);
            }
            ++n6;
        }
    }

    private void renderEditor(GuiGraphics guiGraphics, int n, int n2) {
        int n3;
        int n4;
        int n5;
        int n6 = this.x0();
        int n7 = this.y0();
        int n8 = this.pw();
        guiGraphics.drawCenteredString(this.font, (Component)Component.translatable((String)(this.mode == Mode.CREATE ? "ideology.coaldust.screen.create_new" : "ideology.coaldust.screen.edit")), n6 + n8 / 2, n7 + 7, IdeologyScreen.gold());
        int n9 = n6 + 10;
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.name"), n9, n7 + 26, 0xFFFFFF);
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.desc"), n9, n7 + 46, 0xFFFFFF);
        int n10 = n6 + 90;
        int n11 = n7 + 92;
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.color"), n9, n11 + 2, 0xFFFFFF);
        for (n5 = 0; n5 < Ideology.COLORS.length; ++n5) {
            n4 = n10 + n5 * 15;
            if (Ideology.COLORS[n5] == this.editColor) {
                guiGraphics.fill(n4 - 2, n11 - 2, n4 + 14, n11 + 14, -1);
            }
            guiGraphics.fill(n4, n11, n4 + 12, n11 + 12, 0xFF000000 | Ideology.COLORS[n5]);
        }
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.emblem"), n9, (n11 += 20) + 4, 0xFFFFFF);
        for (n5 = 0; n5 < ICONS.length; ++n5) {
            n4 = n10 + n5 * 19;
            if (ICONS[n5].equals(this.editIcon)) {
                guiGraphics.fill(n4 - 2, n11 - 2, n4 + 18, n11 + 18, 0xFF000000 | this.editColor);
            }
            guiGraphics.fill(n4 - 1, n11 - 1, n4 + 17, n11 + 17, -15066592);
            guiGraphics.blit(IdeologyScreen.emblem(ICONS[n5], 16), n4, n11, 0.0f, 0.0f, 16, 16, 16, 16);
        }
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.structure"), n9, (n11 += 24) + 4, 0xFFFFFF);
        IdeologyStructure[] ideologyStructureArray = IdeologyStructure.values();
        for (n4 = 0; n4 < ideologyStructureArray.length; ++n4) {
            n3 = n10 + n4 * 19;
            if (ideologyStructureArray[n4] == this.editStructure) {
                guiGraphics.fill(n3 - 2, n11 - 2, n3 + 18, n11 + 18, -11702);
            }
            guiGraphics.fill(n3 - 1, n11 - 1, n3 + 17, n11 + 17, -15066592);
            guiGraphics.blit(IdeologyScreen.icon(ideologyStructureArray[n4].id, 16), n3, n11, 0.0f, 0.0f, 16, 16, 16, 16);
            if (n < n3 || n >= n3 + 16 || n2 < n11 || n2 >= n11 + 16) continue;
            this.tooltipTitled(guiGraphics, (Component)Component.translatable((String)("ideology.coaldust.structure." + ideologyStructureArray[n4].id)), (Component)Component.translatable((String)("ideology.coaldust.structure." + ideologyStructureArray[n4].id + ".desc")), n, n2);
        }
        guiGraphics.drawString(this.font, (Component)Component.translatable((String)("ideology.coaldust.structure." + this.editStructure.id)), n10 + ideologyStructureArray.length * 19 + 4, n11 + 4, IdeologyScreen.gold());
        if (this.mode == Mode.CREATE) {
            int n12;
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)"ideology.coaldust.screen.main_trait"), n9, (n11 += 24) + 4, 0xFFFFFF);
            Trait[] traitArray = this.traits().toArray(new Trait[0]);
            for (n3 = 0; n3 < traitArray.length; ++n3) {
                n12 = n10 + n3 * 19;
                if (traitArray[n3] == this.editTrait) {
                    guiGraphics.fill(n12 - 2, n11 - 2, n12 + 18, n11 + 18, -11702);
                }
                guiGraphics.fill(n12 - 1, n11 - 1, n12 + 17, n11 + 17, -15066592);
                guiGraphics.blit(IdeologyScreen.icon(traitArray[n3].id, 16), n12, n11, 0.0f, 0.0f, 16, 16, 16, 16);
                if (n < n12 || n >= n12 + 16 || n2 < n11 || n2 >= n11 + 16) continue;
                this.tooltipTitled(guiGraphics, (Component)Component.translatable((String)("ideology.coaldust.trait." + traitArray[n3].id)), (Component)Component.translatable((String)("ideology.coaldust.trait." + traitArray[n3].id + ".desc")), n, n2);
            }
            guiGraphics.drawString(this.font, (Component)Component.translatable((String)("ideology.coaldust.trait." + this.editTrait.id)), n10, n11 + 20, IdeologyScreen.gold());
            List list = this.font.split((FormattedText)Component.translatable((String)"ideology.coaldust.screen.hint"), n8 - 20);
            n12 = n11 + 32;
            for (FormattedCharSequence formattedCharSequence : list) {
                guiGraphics.drawString(this.font, formattedCharSequence, n9, n12, 0x888888);
                n12 += 10;
            }
        }
        int n13 = n6 + n8 - 46;
        int n14 = n7 + 22;
        guiGraphics.fill(n13, n14, n13 + 36, n14 + 36, 0xFF000000 | this.editColor);
        guiGraphics.fill(n13 + 2, n14 + 2, n13 + 34, n14 + 34, -15066592);
        guiGraphics.blit(IdeologyScreen.emblem(this.editIcon, 32), n13 + 2, n14 + 2, 0.0f, 0.0f, 32, 32, 32, 32);
    }

    private void tooltip(GuiGraphics guiGraphics, Component component, int n, int n2) {
        guiGraphics.renderTooltip(this.font, this.font.split((FormattedText)component, 200), n, n2);
    }

    private void tooltipTitled(GuiGraphics guiGraphics, Component component, Component component2, int n, int n2) {
        ArrayList arrayList = new ArrayList(this.font.split((FormattedText)component.copy().withStyle(ChatFormatting.GOLD), 200));
        arrayList.addAll(this.font.split((FormattedText)component2, 200));
        guiGraphics.renderTooltip(this.font, arrayList, n, n2);
    }

    public boolean mouseClicked(double d, double d2, int n) {
        int n2;
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        if (this.rerollBox != null && d >= (double)this.rerollBox[0] && d < (double)(this.rerollBox[0] + this.rerollBox[2]) && d2 >= (double)this.rerollBox[1] && d2 < (double)(this.rerollBox[1] + this.rerollBox[3])) {
            IdeologyClient.send("ideology reroll");
            IdeologyClient.request();
            return true;
        }
        int n3 = (int)d;
        int n4 = (int)d2;
        if (this.mode == Mode.CREATE || this.mode == Mode.EDIT) {
            return this.clickEditor(n3, n4);
        }
        if (this.secret(n3, n4) && IdeologyClient.data != null) {
            if (this.abyss()) {
                IdeologyClient.send("ideology unveil");
                IdeologyClient.request();
            } else {
                Minecraft.getInstance().setScreen((Screen)new AbyssPasswordScreen(this));
            }
            return true;
        }
        int n5 = this.rx();
        if (n3 >= n5 + 2 && n3 < n5 + 124 - 2 && n4 >= this.listTop()) {
            n2 = (n4 - this.listTop()) / 18 + this.scroll;
            List<CompoundTag> list = this.list();
            if (n2 >= 0 && n2 < list.size() && (n4 - this.listTop()) / 18 < this.listRows()) {
                this.selected = list.get(n2).getString("id");
                if (this.mode == Mode.PICK) {
                    this.mode = Mode.MAIN;
                }
                this.rebuild();
                return true;
            }
        }
        if (this.mode == Mode.PICK) {
            n2 = this.cx();
            int n6 = this.cw();
            int n7 = this.y0() + 36;
            CompoundTag compoundTag = this.find(this.me());
            List list = compoundTag == null ? List.of() : IdeologyScreen.split(compoundTag.getString("traits"), ',');
            int n8 = 0;
            for (Trait trait : this.traits()) {
                int n9 = n2 + 6 + n8 % 2 * ((n6 - 12) / 2);
                int n10 = n7 + 14 + n8 / 2 * 20;
                if (!list.contains(trait.id) && n3 >= n9 && n3 < n9 + (n6 - 12) / 2 - 2 && n4 >= n10 && n4 < n10 + 18) {
                    IdeologyClient.send("ideology trait " + trait.id);
                    IdeologyClient.request();
                    this.mode = Mode.MAIN;
                    this.rebuild();
                    return true;
                }
                ++n8;
            }
        }
        return false;
    }

    private boolean clickEditor(int n, int n2) {
        int n3;
        int n4;
        int n5;
        int n6 = this.x0() + 90;
        int n7 = this.y0() + 92;
        for (n5 = 0; n5 < Ideology.COLORS.length; ++n5) {
            n4 = n6 + n5 * 15;
            if (n < n4 || n >= n4 + 12 || n2 < n7 || n2 >= n7 + 12) continue;
            this.editColor = Ideology.COLORS[n5];
            return true;
        }
        n7 += 20;
        for (n5 = 0; n5 < ICONS.length; ++n5) {
            n4 = n6 + n5 * 19;
            if (n < n4 || n >= n4 + 16 || n2 < n7 || n2 >= n7 + 16) continue;
            this.editIcon = ICONS[n5];
            this.emblemChosen = true;
            return true;
        }
        n7 += 24;
        IdeologyStructure[] ideologyStructureArray = IdeologyStructure.values();
        for (n4 = 0; n4 < ideologyStructureArray.length; ++n4) {
            n3 = n6 + n4 * 19;
            if (n < n3 || n >= n3 + 16 || n2 < n7 || n2 >= n7 + 16) continue;
            this.editStructure = ideologyStructureArray[n4];
            return true;
        }
        if (this.mode == Mode.CREATE) {
            n7 += 24;
            Trait[] traitArray = this.traits().toArray(new Trait[0]);
            for (n3 = 0; n3 < traitArray.length; ++n3) {
                int n8 = n6 + n3 * 19;
                if (n < n8 || n >= n8 + 16 || n2 < n7 || n2 >= n7 + 16) continue;
                this.editTrait = traitArray[n3];
                if (!this.emblemChosen) {
                    this.editIcon = Ideology.defaultEmblem(traitArray[n3]);
                }
                return true;
            }
        }
        return false;
    }

    public boolean mouseScrolled(double d, double d2, double d3, double d4) {
        if ((this.mode == Mode.MAIN || this.mode == Mode.PICK) && d >= (double)this.rx()) {
            this.scroll = Math.max(0, this.scroll - (int)Math.signum(d4));
            return true;
        }
        return super.mouseScrolled(d, d2, d3, d4);
    }

    private static /* synthetic */ void lambda$init$8(String string, Button button) {
        IdeologyClient.send("ideology deny " + string);
        IdeologyClient.request();
    }

    private static /* synthetic */ void lambda$init$7(String string, Button button) {
        IdeologyClient.send("ideology approve " + string);
        IdeologyClient.request();
    }

    private static /* synthetic */ void lambda$init$5(String string, Button button) {
        IdeologyClient.send("ideology request " + string);
        IdeologyClient.request();
    }

    private static enum Mode {
        MAIN,
        CREATE,
        EDIT,
        PICK;

    }
}
