package dev.sygii.variantapi.variants;

import com.google.gson.JsonObject;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class VariantCondition {
    private final Identifier id;

    public VariantCondition(Identifier id) {
        this.id = id;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public boolean condition(MobEntity entity) {
        return false;
    }
}
