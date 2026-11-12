/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectCategory
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.coaldust.effect;

import com.coaldust.CoalDust;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create((ResourceKey)Registries.MOB_EFFECT, (String)"coaldust");
    public static final DeferredHolder<MobEffect, MobEffect> LUNG_CANCER = EFFECTS.register("lung_cancer", () -> new DiseaseEffect(0x2B2B2B));
    public static final DeferredHolder<MobEffect, MobEffect> BLOOD_CANCER = EFFECTS.register("blood_cancer", () -> new DiseaseEffect(0x8B0000));
    public static final DeferredHolder<MobEffect, MobEffect> ASTHMA = EFFECTS.register("asthma", () -> new DiseaseEffect(7317724));
    public static final DeferredHolder<MobEffect, MobEffect> STOMACH_CANCER = EFFECTS.register("stomach_cancer", () -> new DiseaseEffect(8022570));
    public static final DeferredHolder<MobEffect, MobEffect> ASTHMA_ATTACK = EFFECTS.register("asthma_attack", () -> new DiseaseEffect(10797812).addAttributeModifier(Attributes.MOVEMENT_SPEED, CoalDust.id("asthma_attack_speed"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).addAttributeModifier(Attributes.JUMP_STRENGTH, CoalDust.id("asthma_attack_jump"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> VOMITING = EFFECTS.register("vomiting", () -> new DiseaseEffect(9411118));
    public static final DeferredHolder<MobEffect, MobEffect> IN_DUST = EFFECTS.register("in_dust", () -> new DiseaseEffect(0x3C3C3C));
    public static final DeferredHolder<MobEffect, MobEffect> OBESITY = EFFECTS.register("obesity", () -> new DiseaseEffect(15254430).addAttributeModifier(Attributes.JUMP_STRENGTH, CoalDust.id("obesity_jump"), -0.38, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> SYPHILIS = EFFECTS.register("syphilis", () -> new DiseaseEffect(9132587));
    public static final DeferredHolder<MobEffect, MobEffect> PLAGUE = EFFECTS.register("plague", () -> new DiseaseEffect(4873507));
    public static final DeferredHolder<MobEffect, MobEffect> BONE_DECAY = EFFECTS.register("bone_decay", () -> new DiseaseEffect(15261896));
    public static final DeferredHolder<MobEffect, MobEffect> DYSENTERY = EFFECTS.register("dysentery", () -> new DiseaseEffect(10169632));
    public static final DeferredHolder<MobEffect, MobEffect> DEATH_FLOWER = EFFECTS.register("death_flower", () -> new DiseaseEffect(2758704));
    public static final DeferredHolder<MobEffect, MobEffect> DOTAFICATION = EFFECTS.register("dotafication", () -> new DiseaseEffect(12729122));
    public static final DeferredHolder<MobEffect, MobEffect> WASTING = EFFECTS.register("wasting", () -> new DiseaseEffect(6045747));
    public static final DeferredHolder<MobEffect, MobEffect> FRAGILITY = EFFECTS.register("fragility", () -> new DiseaseEffect(14209208));

    private ModEffects() {
    }

    public static class DiseaseEffect
    extends MobEffect {
        public DiseaseEffect(int n) {
            super(MobEffectCategory.HARMFUL, n);
        }
    }
}
