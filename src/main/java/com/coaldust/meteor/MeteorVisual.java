/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.FloatTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Display$BlockDisplay
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.event.tick.EntityTickEvent$Post
 */
package com.coaldust.meteor;

import com.coaldust.block.FallingMeteorBlock;
import com.coaldust.block.ModBlocks;
import com.coaldust.meteor.MeteorType;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public final class MeteorVisual {
    public static final String TAG = "coaldust_meteor_fx";
    private static final Set<UUID> LIVE = new HashSet<UUID>();
    private static final int STEP = 3;

    static int kindOf(MeteorType meteorType) {
        return switch (meteorType) {
            case MeteorType.PLAIN -> 1;
            case MeteorType.ICE -> 2;
            case MeteorType.LIVING -> 3;
            case MeteorType.ANCIENT -> 4;
            case MeteorType.UNDERGROUND -> 5;
            default -> 0;
        };
    }

    static float scaleFor(double d) {
        return (float)Math.max(2.0, Math.min(22.0, d * 2.6));
    }

    static Visual spawn(ServerLevel serverLevel, MeteorType meteorType, double d, int n, double d2, double d3, double d4, float f, float f2) {
        Visual visual = new Visual(meteorType, MeteorVisual.scaleFor(d), n);
        visual.body = MeteorVisual.display(serverLevel, MeteorVisual.kindOf(meteorType), d2, d3, d4, f, f2);
        if (meteorType == MeteorType.ANCIENT) {
            visual.ring = MeteorVisual.display(serverLevel, 6, d2, d3, d4, f, f2);
        }
        if (meteorType == MeteorType.UNDERGROUND) {
            visual.ring = MeteorVisual.display(serverLevel, 7, d2, d3, d4, f, f2);
        }
        MeteorVisual.animate(visual, true);
        return visual;
    }

    private static Display.BlockDisplay display(ServerLevel serverLevel, int n, double d, double d2, double d3, float f, float f2) {
        Display.BlockDisplay blockDisplay = (Display.BlockDisplay)EntityType.BLOCK_DISPLAY.create((Level)serverLevel);
        if (blockDisplay == null) {
            return null;
        }
        CompoundTag compoundTag = blockDisplay.saveWithoutId(new CompoundTag());
        compoundTag.put("block_state", (Tag)NbtUtils.writeBlockState((BlockState)((BlockState)((FallingMeteorBlock)((Object)ModBlocks.FALLING_METEOR.get())).defaultBlockState().setValue((Property)FallingMeteorBlock.KIND, (Comparable)Integer.valueOf(n)))));
        compoundTag.putInt("teleport_duration", 3);
        compoundTag.putFloat("view_range", 8.0f);
        compoundTag.putFloat("shadow_radius", 0.0f);
        CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putInt("sky", 15);
        compoundTag2.putInt("block", 15);
        compoundTag.put("brightness", (Tag)compoundTag2);
        blockDisplay.load(compoundTag);
        blockDisplay.moveTo(d, d2, d3, f, f2);
        blockDisplay.addTag(TAG);
        serverLevel.addFreshEntity((Entity)blockDisplay);
        LIVE.add(blockDisplay.getUUID());
        return blockDisplay;
    }

    static void move(Visual visual, double d, double d2, double d3, float f, float f2) {
        if (visual == null) {
            return;
        }
        if (visual.body != null && !visual.body.isRemoved()) {
            visual.body.moveTo(d, d2, d3, f, f2);
        }
        if (visual.ring != null && !visual.ring.isRemoved()) {
            visual.ring.moveTo(d, d2, d3, f, f2);
        }
        ++visual.age;
        if (visual.age % 3 == 0) {
            MeteorVisual.animate(visual, false);
        }
    }

    static void remove(Visual visual) {
        if (visual == null) {
            return;
        }
        for (Display.BlockDisplay blockDisplay : new Display.BlockDisplay[]{visual.body, visual.ring}) {
            if (blockDisplay == null) continue;
            LIVE.remove(blockDisplay.getUUID());
            blockDisplay.discard();
        }
    }

    private static void animate(Visual visual, boolean bl) {
        double[] dArray;
        double d = visual.age + (bl ? 0 : 3);
        double d2 = visual.scale;
        double[] dArray2 = null;
        double d3 = d2;
        switch (visual.type) {
            case PLAIN: {
                dArray = MeteorVisual.mul(MeteorVisual.qz(Math.toRadians(d * 4.0)), MeteorVisual.qx(Math.toRadians(16.0 * Math.sin(d * 0.17))));
                dArray = MeteorVisual.mul(dArray, MeteorVisual.qy(Math.toRadians(10.0 * Math.sin(d * 0.11 + 1.0))));
                break;
            }
            case ICE: {
                dArray = MeteorVisual.qz(Math.toRadians(d * 5.0));
                d2 *= 1.0 + 0.05 * Math.sin(d * 0.4);
                break;
            }
            case LIVING: {
                dArray = MeteorVisual.qz(Math.toRadians(d * 3.0));
                double d4 = Math.pow(Math.max(0.0, Math.sin(d * 0.55)), 6.0);
                d2 *= 1.0 + 0.13 * d4;
                break;
            }
            case ANCIENT: {
                dArray = MeteorVisual.qz(Math.toRadians(d * 6.0));
                dArray2 = MeteorVisual.mul(MeteorVisual.qx(Math.toRadians(28.0)), MeteorVisual.qz(Math.toRadians(-d * 11.0)));
                d3 = d2 * 1.05;
                break;
            }
            case UNDERGROUND: {
                dArray = MeteorVisual.qz(Math.toRadians(d * 2.0));
                dArray2 = MeteorVisual.mul(MeteorVisual.qy(Math.toRadians(25.0)), MeteorVisual.qz(Math.toRadians(d * 8.0)));
                d3 = (d2 *= 0.8 + 0.2 * Math.min(1.0, d / (double)Math.max(1, visual.fallTicks))) * 1.1;
                break;
            }
            default: {
                dArray = MeteorVisual.qz(Math.toRadians(d * 12.0));
                d2 *= 1.0 + 0.04 * Math.sin(d * 1.3);
            }
        }
        MeteorVisual.setTransform(visual.body, dArray, d2, bl);
        if (visual.ring != null) {
            MeteorVisual.setTransform(visual.ring, dArray2, d3, bl);
        }
    }

    private static void setTransform(Display.BlockDisplay blockDisplay, double[] dArray, double d, boolean bl) {
        if (blockDisplay == null || blockDisplay.isRemoved()) {
            return;
        }
        double d2 = d / 2.0;
        double[] dArray2 = MeteorVisual.rotate(dArray, d2, d2, d2);
        CompoundTag compoundTag = blockDisplay.saveWithoutId(new CompoundTag());
        CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.put("translation", (Tag)MeteorVisual.floats(-dArray2[0], -dArray2[1], -dArray2[2]));
        compoundTag2.put("left_rotation", (Tag)MeteorVisual.floats(dArray[0], dArray[1], dArray[2], dArray[3]));
        compoundTag2.put("scale", (Tag)MeteorVisual.floats(d, d, d));
        compoundTag2.put("right_rotation", (Tag)MeteorVisual.floats(0.0, 0.0, 0.0, 1.0));
        compoundTag.put("transformation", (Tag)compoundTag2);
        compoundTag.putInt("interpolation_duration", bl ? 0 : 3);
        compoundTag.putInt("start_interpolation", 0);
        blockDisplay.load(compoundTag);
    }

    private static double[] qx(double d) {
        return new double[]{Math.sin(d / 2.0), 0.0, 0.0, Math.cos(d / 2.0)};
    }

    private static double[] qy(double d) {
        return new double[]{0.0, Math.sin(d / 2.0), 0.0, Math.cos(d / 2.0)};
    }

    private static double[] qz(double d) {
        return new double[]{0.0, 0.0, Math.sin(d / 2.0), Math.cos(d / 2.0)};
    }

    private static double[] mul(double[] dArray, double[] dArray2) {
        return new double[]{dArray[3] * dArray2[0] + dArray[0] * dArray2[3] + dArray[1] * dArray2[2] - dArray[2] * dArray2[1], dArray[3] * dArray2[1] - dArray[0] * dArray2[2] + dArray[1] * dArray2[3] + dArray[2] * dArray2[0], dArray[3] * dArray2[2] + dArray[0] * dArray2[1] - dArray[1] * dArray2[0] + dArray[2] * dArray2[3], dArray[3] * dArray2[3] - dArray[0] * dArray2[0] - dArray[1] * dArray2[1] - dArray[2] * dArray2[2]};
    }

    private static double[] rotate(double[] dArray, double d, double d2, double d3) {
        double[] dArray2 = MeteorVisual.mul(MeteorVisual.mul(dArray, new double[]{d, d2, d3, 0.0}), new double[]{-dArray[0], -dArray[1], -dArray[2], dArray[3]});
        return new double[]{dArray2[0], dArray2[1], dArray2[2]};
    }

    public static void onEntityTick(EntityTickEvent.Post post) {
        Entity entity = post.getEntity();
        if (entity.tickCount % 10 == 0 && entity.getTags().contains(TAG) && !entity.level().isClientSide() && !LIVE.contains(entity.getUUID())) {
            entity.discard();
        }
    }

    private static ListTag floats(double ... dArray) {
        ListTag listTag = new ListTag();
        for (double d : dArray) {
            listTag.add((Object)FloatTag.valueOf((float)((float)d)));
        }
        return listTag;
    }

    private MeteorVisual() {
    }

    public static final class Visual {
        final MeteorType type;
        final float scale;
        final int fallTicks;
        Display.BlockDisplay body;
        Display.BlockDisplay ring;
        int age;

        Visual(MeteorType meteorType, float f, int n) {
            this.type = meteorType;
            this.scale = f;
            this.fallTicks = n;
        }
    }
}
