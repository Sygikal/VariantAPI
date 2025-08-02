package dev.sygii.variantapi.variants.feature.server;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.util.Identifier;

public class WaterImmuneFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("water_immune");

    public WaterImmuneFeature() {
        super(ID, true);
    }
}
