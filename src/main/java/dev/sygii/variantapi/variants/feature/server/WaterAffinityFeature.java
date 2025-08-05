package dev.sygii.variantapi.variants.feature.server;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.util.Identifier;

public class WaterAffinityFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("water_affinity");
    private final boolean sensitiveToWater;

    public WaterAffinityFeature(boolean sensitiveToWater) {
        super(ID, true);

        this.sensitiveToWater = sensitiveToWater;
    }

    public boolean isSensitiveToWater() {
        return this.sensitiveToWater;
    }
}
