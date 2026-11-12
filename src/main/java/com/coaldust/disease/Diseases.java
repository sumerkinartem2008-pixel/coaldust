/*
 * Decompiled with CFR 0.152.
 */
package com.coaldust.disease;

import com.coaldust.disease.Asthma;
import com.coaldust.disease.BloodCancer;
import com.coaldust.disease.BoneDecay;
import com.coaldust.disease.DeathFlower;
import com.coaldust.disease.Disease;
import com.coaldust.disease.Dotafication;
import com.coaldust.disease.Dysentery;
import com.coaldust.disease.LungCancer;
import com.coaldust.disease.Plague;
import com.coaldust.disease.StomachCancer;
import com.coaldust.disease.Syphilis;
import java.util.List;

public final class Diseases {
    public static final List<Disease> ALL = List.of(LungCancer.INSTANCE, BloodCancer.INSTANCE, Asthma.INSTANCE, StomachCancer.INSTANCE, Syphilis.INSTANCE, Plague.INSTANCE, BoneDecay.INSTANCE, Dysentery.INSTANCE, DeathFlower.INSTANCE, Dotafication.INSTANCE);

    private Diseases() {
    }
}
