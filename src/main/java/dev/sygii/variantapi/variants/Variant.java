package dev.sygii.variantapi.variants;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Variant {
    private final Identifier id;
    private final Identifier texture;
    private final int weight;
    private final boolean overlay;

    private boolean customLighting;
    private int light = 0;

    private ArrayList<VariantCondition> conditions = new ArrayList<>();
    private Map<Identifier, VariantFeature> features = new HashMap<>();

    public Variant(Identifier id, Identifier texture, boolean overlay) {
        this.id = id;
        this.texture = texture;
        this.weight = 1;
        this.overlay = overlay;
    }

    public Variant(Identifier id, Identifier texture, int weight, boolean overlay/*, boolean customLighting, int light*/) {
        this.id = id;
        this.texture = texture;
        this.weight = weight;
        this.overlay = overlay;
        //this.customLighting = customLighting;
        //this.light = light;
    }

    public void addCondition(VariantCondition condition) {
        this.conditions.add(condition);
    }

    public ArrayList<VariantCondition> getConditions() {
        return this.conditions;
    }

    public void addFeature(VariantFeature feature) {
        this.features.put(feature.getIdentifier(), feature);
    }

    public VariantFeature getFeature(Identifier id) {
        return this.features.get(id);
    }

    public Map<Identifier, VariantFeature> getFeatures() {
        return this.features;
    }

    public Identifier id() {
        return id;
    }

    public Identifier texture() {
        return texture;
    }

    public int weight() {
        return weight;
    }

    public boolean overlay() {
        return this.overlay;
    }
}
