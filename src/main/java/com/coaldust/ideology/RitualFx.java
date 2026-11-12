/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 */
package com.coaldust.ideology;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class RitualFx {
    public static final int DURATION = 60;
    private static final List<Anim> ACTIVE = new ArrayList<Anim>();

    public static void start(ServerLevel serverLevel, BlockPos blockPos, String string, ServerPlayer serverPlayer, List<ServerPlayer> list) {
        ACTIVE.add(new Anim(serverLevel, blockPos, string, serverPlayer, new ArrayList<ServerPlayer>(list)));
    }

    public static void onLevelTick(LevelTickEvent.Post post) {
        ServerLevel serverLevel;
        Object object;
        block6: {
            block5: {
                object = post.getLevel();
                if (!(object instanceof ServerLevel)) break block5;
                serverLevel = (ServerLevel)object;
                if (!ACTIVE.isEmpty()) break block6;
            }
            return;
        }
        object = ACTIVE.iterator();
        while (object.hasNext()) {
            Anim anim = (Anim)object.next();
            if (anim.level != serverLevel) continue;
            try {
                RitualFx.step(anim);
            }
            catch (Exception exception) {
                anim.age = 60;
            }
            if (++anim.age <= 60) continue;
            object.remove();
        }
    }

    private static void p(Anim anim, ParticleOptions particleOptions, double d, double d2, double d3) {
        anim.level.sendParticles(particleOptions, d, d2, d3, 1, 0.0, 0.0, 0.0, 0.0);
    }

    private static void pv(Anim anim, ParticleOptions particleOptions, double d, double d2, double d3, double d4, double d5, double d6, double d7) {
        anim.level.sendParticles(particleOptions, d, d2, d3, 0, d4, d5, d6, d7);
    }

    private static void sound(Anim anim, SoundEvent soundEvent, float f, float f2) {
        anim.level.playSound(null, (double)anim.pos.getX() + 0.5, (double)(anim.pos.getY() + 1), (double)anim.pos.getZ() + 0.5, soundEvent, SoundSource.BLOCKS, f, f2);
    }

    private static void ring(Anim anim, ParticleOptions particleOptions, double d, double d2, int n, double d3) {
        double d4 = (double)anim.pos.getX() + 0.5;
        double d5 = (double)anim.pos.getZ() + 0.5;
        for (int i = 0; i < n; ++i) {
            double d6 = Math.PI * 2 * (double)i / (double)n;
            double d7 = Math.cos(d6);
            double d8 = Math.sin(d6);
            if (d3 > 0.0) {
                RitualFx.pv(anim, particleOptions, d4 + d7 * d2, d, d5 + d8 * d2, d7, 0.05, d8, d3);
                continue;
            }
            RitualFx.p(anim, particleOptions, d4 + d7 * d2, d, d5 + d8 * d2);
        }
    }

    private static void beam(Anim anim, ParticleOptions particleOptions, double d, double d2, double d3, double d4, double d5, double d6, double d7) {
        double d8 = d4 - d;
        double d9 = d5 - d2;
        double d10 = d6 - d3;
        double d11 = Math.sqrt(d8 * d8 + d9 * d9 + d10 * d10);
        int n = Math.max(1, (int)(d11 / d7));
        for (int i = 0; i <= n; ++i) {
            RitualFx.p(anim, particleOptions, d + d8 * (double)i / (double)n, d2 + d9 * (double)i / (double)n, d3 + d10 * (double)i / (double)n);
        }
    }

    private static void step(Anim anim) {
        double d = (double)anim.pos.getX() + 0.5;
        double d2 = (double)anim.pos.getY() + 1.1;
        double d3 = (double)anim.pos.getZ() + 0.5;
        int n = anim.age;
        double d4 = (double)n / 60.0;
        switch (anim.type) {
            case "fire": {
                double d5;
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.FIRECHARGE_USE, 1.2f, 0.7f);
                }
                double d6 = 1.4 * (1.0 - d4) + 0.2;
                double d7 = 3.2 * d4;
                for (int i = 0; i < 2; ++i) {
                    d5 = (double)n * 0.45 + (double)i * Math.PI;
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.FLAME, d + Math.cos(d5) * d6, d2 + d7, d3 + Math.sin(d5) * d6);
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.FLAME, d + Math.cos(d5 - 0.2) * d6, d2 + d7 - 0.15, d3 + Math.sin(d5 - 0.2) * d6);
                }
                if (n % 8 == 0) {
                    anim.level.sendParticles((ParticleOptions)ParticleTypes.LAVA, d, d2, d3, 3, 0.3, 0.1, 0.3, 0.0);
                }
                if (n != 60) break;
                RitualFx.ring(anim, (ParticleOptions)ParticleTypes.FLAME, d2 + 0.2, 0.4, 40, 0.25);
                RitualFx.ring(anim, (ParticleOptions)ParticleTypes.LARGE_SMOKE, d2 + 0.4, 0.5, 16, 0.08);
                RitualFx.sound(anim, SoundEvents.BLAZE_SHOOT, 1.2f, 0.6f);
                break;
            }
            case "night": {
                double d8;
                double d9;
                double d10;
                double d11;
                double d5;
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.ENDERMAN_TELEPORT, 1.0f, 0.5f);
                }
                for (int i = 0; i < 6; ++i) {
                    d11 = anim.level.random.nextDouble() * Math.PI * 2.0;
                    d10 = Math.acos(2.0 * anim.level.random.nextDouble() - 1.0);
                    d5 = Math.sin(d10) * Math.cos(d11);
                    d9 = Math.cos(d10);
                    d8 = Math.sin(d10) * Math.sin(d11);
                    RitualFx.pv(anim, (ParticleOptions)ParticleTypes.REVERSE_PORTAL, d + d5 * 3.0, d2 + 1.0 + d9 * 3.0, d3 + d8 * 3.0, -d5, -d9, -d8, 0.25);
                }
                double d12 = (double)n * 0.3;
                for (int i = 0; i < 3; ++i) {
                    d10 = d12 + (double)i * Math.PI * 2.0 / 3.0;
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.SQUID_INK, d + Math.cos(d10) * 1.8, d2 - 0.9, d3 + Math.sin(d10) * 1.8);
                }
                if (n % 15 == 0) {
                    anim.level.sendParticles((ParticleOptions)ParticleTypes.PORTAL, d, d2 + 0.5, d3, 40, 0.3, 0.6, 0.3, 1.0);
                }
                if (n != 60) break;
                RitualFx.ring(anim, (ParticleOptions)ParticleTypes.END_ROD, d2 + 3.0, 1.6, 12, 0.0);
                RitualFx.ring(anim, (ParticleOptions)ParticleTypes.GLOW, d2 + 3.0, 1.0, 8, 0.0);
                RitualFx.sound(anim, SoundEvents.AMETHYST_BLOCK_CHIME, 1.5f, 0.6f);
                break;
            }
            case "strength": {
                if (n % 15 == 0 && n < 60) {
                    RitualFx.sound(anim, SoundEvents.ANVIL_LAND, 0.7f, 0.6f + (float)n / 100.0f);
                    RitualFx.ring(anim, (ParticleOptions)ParticleTypes.CRIT, d2 - 0.9, 0.6, 32, 0.5);
                    RitualFx.ring(anim, (ParticleOptions)ParticleTypes.ENCHANTED_HIT, d2 - 0.8, 0.4, 16, 0.35);
                }
                for (int i = 0; i < 3; ++i) {
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.ELECTRIC_SPARK, d + (anim.level.random.nextDouble() - 0.5) * 0.4, d2 + anim.level.random.nextDouble() * 3.0 * d4, d3 + (anim.level.random.nextDouble() - 0.5) * 0.4);
                }
                if (n != 60) break;
                anim.level.sendParticles((ParticleOptions)ParticleTypes.TOTEM_OF_UNDYING, d, d2 + 1.0, d3, 80, 0.4, 0.8, 0.4, 0.6);
                RitualFx.sound(anim, SoundEvents.TOTEM_USE, 0.6f, 1.3f);
                break;
            }
            case "harvest": {
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.BONE_MEAL_USE, 1.5f, 0.8f);
                }
                double d13 = 8.0 * d4;
                int n2 = 8 + (int)(d13 * 6.0);
                if (n % 3 == 0) {
                    RitualFx.ring(anim, (ParticleOptions)ParticleTypes.HAPPY_VILLAGER, d2 - 0.6, d13, n2, 0.0);
                }
                for (int i = 0; i < 2; ++i) {
                    RitualFx.pv(anim, (ParticleOptions)ParticleTypes.COMPOSTER, d + (anim.level.random.nextDouble() - 0.5) * 0.5, d2 + 0.2, d3 + (anim.level.random.nextDouble() - 0.5) * 0.5, (anim.level.random.nextDouble() - 0.5) * 0.4, 1.0, (anim.level.random.nextDouble() - 0.5) * 0.4, 0.35);
                }
                if (n % 4 == 0) {
                    anim.level.sendParticles((ParticleOptions)ParticleTypes.CHERRY_LEAVES, d, d2 + 3.0, d3, 3, 2.5, 0.3, 2.5, 0.0);
                }
                if (n != 60) break;
                anim.level.sendParticles((ParticleOptions)ParticleTypes.HAPPY_VILLAGER, d, d2, d3, 60, 3.0, 0.5, 3.0, 0.0);
                RitualFx.sound(anim, SoundEvents.BONE_MEAL_USE, 1.5f, 1.2f);
                break;
            }
            case "wind": {
                double d11;
                double d5;
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.ELYTRA_FLYING, 0.8f, 1.4f);
                }
                for (int i = 0; i < 3; ++i) {
                    d11 = ((double)n * 0.12 + (double)i / 3.0) % 1.0 * 3.5;
                    double d14 = 0.3 + d11 * 0.45;
                    d5 = (double)n * 0.55 + (double)i * 2.1;
                    RitualFx.pv(anim, (ParticleOptions)ParticleTypes.CLOUD, d + Math.cos(d5) * d14, d2 - 0.8 + d11, d3 + Math.sin(d5) * d14, -Math.sin(d5), 0.3, Math.cos(d5), 0.15);
                }
                if (n % 2 == 0) {
                    double d15 = (double)(-n) * 0.4;
                    RitualFx.pv(anim, (ParticleOptions)ParticleTypes.WHITE_ASH, d + Math.cos(d15) * 2.5, d2 - 0.7, d3 + Math.sin(d15) * 2.5, -Math.cos(d15), 0.2, -Math.sin(d15), 0.3);
                }
                if (n != 60) break;
                RitualFx.ring(anim, (ParticleOptions)ParticleTypes.CLOUD, d2 - 0.7, 0.5, 32, 0.45);
                RitualFx.sound(anim, SoundEvents.PHANTOM_FLAP, 1.2f, 0.7f);
                break;
            }
            case "shadow": {
                double d9;
                double d11;
                double d5;
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.PHANTOM_AMBIENT, 1.0f, 0.5f);
                }
                for (int i = 0; i < 5; ++i) {
                    d11 = anim.level.random.nextDouble() * Math.PI * 2.0;
                    double d16 = 3.5 * (1.0 - d4 * 0.5);
                    d5 = Math.cos(d11);
                    d9 = Math.sin(d11);
                    RitualFx.pv(anim, (ParticleOptions)ParticleTypes.SQUID_INK, d + d5 * d16, d2 + anim.level.random.nextDouble() * 2.0, d3 + d9 * d16, -d5, 0.0, -d9, 0.3);
                }
                if (n % 4 == 0) {
                    anim.level.sendParticles((ParticleOptions)ParticleTypes.LARGE_SMOKE, d, d2 + 0.5, d3, 6, 0.3, 0.6, 0.3, 0.01);
                }
                if (n % 15 == 0) {
                    RitualFx.ring(anim, (ParticleOptions)ParticleTypes.SMOKE, d2 - 0.8, 0.4, 24, 0.25);
                }
                if (n != 60) break;
                RitualFx.ring(anim, (ParticleOptions)ParticleTypes.SQUID_INK, d2, 0.3, 40, 0.5);
                RitualFx.ring(anim, (ParticleOptions)ParticleTypes.REVERSE_PORTAL, d2 + 1.5, 0.3, 24, 0.3);
                RitualFx.sound(anim, SoundEvents.ENDERMAN_TELEPORT, 1.0f, 0.6f);
                break;
            }
            case "life": {
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.AMETHYST_BLOCK_CHIME, 1.5f, 1.6f);
                }
                double d17 = (double)n * 0.35;
                double d18 = d4 * 3.0;
                double d19 = 1.6 - d4;
                if (n % 3 == 0) {
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.HEART, d + Math.cos(d17) * d19, d2 - 0.6 + d18, d3 + Math.sin(d17) * d19);
                }
                RitualFx.p(anim, (ParticleOptions)ParticleTypes.HAPPY_VILLAGER, d + Math.cos(d17 + Math.PI) * d19, d2 - 0.6 + d18, d3 + Math.sin(d17 + Math.PI) * d19);
                if (n % 12 == 0) {
                    RitualFx.ring(anim, (ParticleOptions)ParticleTypes.COMPOSTER, d2 - 0.8, 0.5, 28, 0.3);
                }
                if (n != 60) break;
                anim.level.sendParticles((ParticleOptions)ParticleTypes.HEART, d, d2 + 1.5, d3, 12, 1.2, 0.6, 1.2, 0.0);
                anim.level.sendParticles((ParticleOptions)ParticleTypes.TOTEM_OF_UNDYING, d, d2 + 1.0, d3, 40, 0.4, 0.6, 0.4, 0.4);
                RitualFx.sound(anim, SoundEvents.PLAYER_LEVELUP, 1.0f, 1.3f);
                break;
            }
            case "depths": {
                double d5;
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.CONDUIT_ACTIVATE, 1.2f, 1.0f);
                }
                double d20 = (double)n * 0.3;
                for (int i = 0; i < 4; ++i) {
                    double d21 = d20 + (double)i * Math.PI / 2.0;
                    d5 = 2.4 * (1.0 - d4) + 0.4;
                    RitualFx.pv(anim, (ParticleOptions)ParticleTypes.NAUTILUS, d + Math.cos(d21) * d5, d2 + 0.5 + Math.sin((double)n * 0.2 + (double)i) * 0.5, d3 + Math.sin(d21) * d5, -Math.cos(d21), 0.0, -Math.sin(d21), 0.2);
                }
                if (n % 2 == 0) {
                    anim.level.sendParticles((ParticleOptions)ParticleTypes.FALLING_WATER, d, d2 + 1.8, d3, 3, 0.4, 0.1, 0.4, 0.0);
                }
                if (n % 10 == 0) {
                    RitualFx.ring(anim, (ParticleOptions)ParticleTypes.SPLASH, d2 - 0.8, 1.2, 24, 0.2);
                }
                if (n != 60) break;
                RitualFx.ring(anim, (ParticleOptions)ParticleTypes.BUBBLE_POP, d2, 0.5, 40, 0.3);
                anim.level.sendParticles((ParticleOptions)ParticleTypes.GLOW, d, d2 + 1.0, d3, 30, 0.6, 0.8, 0.6, 0.05);
                RitualFx.sound(anim, SoundEvents.CONDUIT_ACTIVATE, 1.0f, 1.6f);
                break;
            }
            case "crystal": {
                double d8;
                double d9;
                double d5;
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, 1.0f);
                }
                double d22 = Math.min(1.0, d4 * 1.6) * 2.4;
                for (int i = 0; i < 6; ++i) {
                    double d23 = (double)i * Math.PI / 3.0 + 0.3;
                    d5 = d + Math.cos(d23) * 1.8;
                    d9 = d3 + Math.sin(d23) * 1.8;
                    if (n % 2 == 0) {
                        for (d8 = 0.0; d8 < d22; d8 += 0.4) {
                            RitualFx.p(anim, (ParticleOptions)ParticleTypes.END_ROD, d5, d2 - 0.8 + d8, d9);
                        }
                    }
                    if (anim.level.random.nextInt(4) != 0) continue;
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.ELECTRIC_SPARK, d5, d2 - 0.8 + d22, d9);
                }
                if (n % 5 == 0) {
                    RitualFx.ring(anim, (ParticleOptions)ParticleTypes.WAX_ON, d2 + 1.6, 0.5, 12, 0.1);
                }
                if (n % 20 == 10) {
                    RitualFx.sound(anim, SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, 0.8f + (float)d4);
                }
                if (n != 60) break;
                anim.level.sendParticles((ParticleOptions)ParticleTypes.END_ROD, d, d2 + 1.6, d3, 40, 0.1, 0.1, 0.1, 0.2);
                RitualFx.sound(anim, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, 1.5f);
                break;
            }
            case "guardians": {
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.IRON_GOLEM_REPAIR, 1.2f, 0.8f);
                }
                if (n % 12 == 0) {
                    RitualFx.ring(anim, (ParticleOptions)ParticleTypes.ENCHANTED_HIT, d2 - 0.8, 0.5, 32, 0.45);
                }
                double d24 = (double)n * 0.25;
                for (int i = 0; i < 6; ++i) {
                    double d25 = d24 + (double)i * Math.PI / 3.0;
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.CRIT, d + Math.cos(d25) * 2.0, d2 + 1.2 + Math.sin((double)n * 0.2 + (double)i) * 0.3, d3 + Math.sin(d25) * 2.0);
                }
                if (n == 30) {
                    RitualFx.sound(anim, SoundEvents.WOLF_HOWL, 1.2f, 1.0f);
                }
                if (n != 60) break;
                anim.level.sendParticles((ParticleOptions)ParticleTypes.TOTEM_OF_UNDYING, d, d2 + 0.5, d3, 50, 1.0, 0.6, 1.0, 0.3);
                RitualFx.sound(anim, SoundEvents.ANVIL_LAND, 0.8f, 0.6f);
                break;
            }
            case "meteor": {
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.BEACON_ACTIVATE, 1.5f, 0.6f);
                }
                double d26 = Math.min(1.0, d4 * 1.5) * 20.0;
                if (n % 2 == 0) {
                    for (double d27 = 0.0; d27 < d26; d27 += 0.7) {
                        double d28 = d27 * 0.6 + (double)n * 0.2;
                        RitualFx.p(anim, (ParticleOptions)ParticleTypes.FLAME, d + Math.cos(d28) * 0.35, d2 + d27, d3 + Math.sin(d28) * 0.35);
                    }
                }
                if (n % 6 == 0) {
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.LAVA, d, d2 + 0.3, d3);
                }
                if (n % 15 == 0) {
                    RitualFx.ring(anim, (ParticleOptions)ParticleTypes.FLAME, d2 - 0.8, 0.6, 28, 0.35);
                }
                if (n != 60) break;
                anim.level.sendParticles((ParticleOptions)ParticleTypes.EXPLOSION, d, d2 + d26, d3, 1, 0.0, 0.0, 0.0, 0.0);
                RitualFx.sound(anim, SoundEvents.BLAZE_SHOOT, 1.5f, 0.5f);
                break;
            }
            case "prosperity": {
                double d5;
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.VILLAGER_CELEBRATE, 1.2f, 1.0f);
                }
                for (int i = 0; i < 2; ++i) {
                    RitualFx.pv(anim, (ParticleOptions)ParticleTypes.HAPPY_VILLAGER, d, d2 + 0.3, d3, (anim.level.random.nextDouble() - 0.5) * 0.6, 1.0, (anim.level.random.nextDouble() - 0.5) * 0.6, 0.45);
                }
                double d29 = (double)n * 0.35;
                for (int i = 0; i < 4; ++i) {
                    double d30 = d29 + (double)i * Math.PI / 2.0;
                    d5 = 2.2 * (1.0 - d4) + 0.3;
                    RitualFx.pv(anim, (ParticleOptions)ParticleTypes.COMPOSTER, d + Math.cos(d30) * d5, d2 + 3.5 - 3.0 * d4, d3 + Math.sin(d30) * d5, 0.0, -1.0, 0.0, 0.1);
                }
                if (n % 10 == 0) {
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.NOTE, d + (anim.level.random.nextDouble() - 0.5) * 2.0, d2 + 1.5, d3 + (anim.level.random.nextDouble() - 0.5) * 2.0);
                }
                if (n != 60) break;
                anim.level.sendParticles((ParticleOptions)ParticleTypes.TOTEM_OF_UNDYING, d, d2 + 0.5, d3, 50, 0.6, 0.6, 0.6, 0.4);
                RitualFx.sound(anim, SoundEvents.VILLAGER_CELEBRATE, 1.2f, 1.3f);
                break;
            }
            case "cleansing": {
                double d5;
                ServerPlayer serverPlayer = anim.caster;
                double d11 = serverPlayer != null && serverPlayer.isAlive() ? serverPlayer.getX() : d;
                double d31 = serverPlayer != null && serverPlayer.isAlive() ? serverPlayer.getY() : d2;
                double d32 = d5 = serverPlayer != null && serverPlayer.isAlive() ? serverPlayer.getZ() : d3;
                if (n == 0) {
                    RitualFx.sound(anim, SoundEvents.BEACON_ACTIVATE, 1.0f, 1.5f);
                }
                if (d4 < 0.6) {
                    for (int i = 0; i < 3; ++i) {
                        RitualFx.pv(anim, (ParticleOptions)ParticleTypes.WITCH, d11 + (anim.level.random.nextDouble() - 0.5) * 0.8, d31 + anim.level.random.nextDouble() * 1.8, d5 + (anim.level.random.nextDouble() - 0.5) * 0.8, 0.0, 1.0, 0.0, 0.3);
                    }
                    if (n % 5 == 0) {
                        anim.level.sendParticles((ParticleOptions)ParticleTypes.ASH, d11, d31 + 1.0, d5, 10, 0.4, 0.8, 0.4, 0.0);
                    }
                }
                double d9 = (double)n * 0.4;
                double d8 = 1.0;
                double d33 = d31 + 3.2 - 3.2 * d4;
                for (int i = 0; i < 2; ++i) {
                    RitualFx.p(anim, (ParticleOptions)ParticleTypes.END_ROD, d11 + Math.cos(d9 + (double)i * Math.PI) * d8, d33, d5 + Math.sin(d9 + (double)i * Math.PI) * d8);
                }
                if (n != 60) break;
                anim.level.sendParticles((ParticleOptions)ParticleTypes.HEART, d11, d31 + 1.2, d5, 8, 0.5, 0.4, 0.5, 0.0);
                anim.level.sendParticles((ParticleOptions)ParticleTypes.GLOW, d11, d31 + 1.0, d5, 30, 0.5, 0.8, 0.5, 0.1);
                RitualFx.sound(anim, SoundEvents.AMETHYST_BLOCK_CHIME, 1.5f, 1.4f);
                break;
            }
        }
        if (n == 0) {
            anim.level.sendParticles((ParticleOptions)ParticleTypes.ENCHANT, d, d2 + 0.6, d3, 60, 0.8, 0.6, 0.8, 1.0);
        }
        if (n == 60) {
            for (ServerPlayer serverPlayer : anim.targets) {
                if (!serverPlayer.isAlive() || serverPlayer.level() != anim.level) continue;
                RitualFx.beam(anim, (ParticleOptions)ParticleTypes.END_ROD, d, d2 + 0.5, d3, serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(), 0.6);
            }
        }
    }

    private RitualFx() {
    }

    private static final class Anim {
        final ServerLevel level;
        final BlockPos pos;
        final String type;
        final ServerPlayer caster;
        final List<ServerPlayer> targets;
        int age;

        Anim(ServerLevel serverLevel, BlockPos blockPos, String string, ServerPlayer serverPlayer, List<ServerPlayer> list) {
            this.level = serverLevel;
            this.pos = blockPos.immutable();
            this.type = string;
            this.caster = serverPlayer;
            this.targets = list;
        }
    }
}
