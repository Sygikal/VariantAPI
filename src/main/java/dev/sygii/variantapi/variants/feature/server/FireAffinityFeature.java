package dev.sygii.variantapi.variants.feature.server;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.util.Identifier;

public class FireAffinityFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("fire_affinity");
    private final boolean immuneToFire;

    public FireAffinityFeature(boolean immuneToFire) {
        super(ID, true);

        this.immuneToFire = immuneToFire;
    }

    public boolean isImmuneToFire() {
        return this.immuneToFire;
    }
}
