package dev.sygii.variantapi.variants.feature.server;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.util.Identifier;

public class ExplosionRadiusFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("explosion_radius");
    private final float radius;

    public ExplosionRadiusFeature(float radius) {
        super(ID, true);
        this.radius = radius;
    }

    public float getRadius() {
        return this.radius;
    }
}
