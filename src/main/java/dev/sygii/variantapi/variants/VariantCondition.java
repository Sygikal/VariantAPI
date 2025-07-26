package dev.sygii.variantapi.variants;

import com.google.gson.JsonObject;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class VariantCondition {
    private final Identifier id;
    private boolean exclusive;

    public VariantCondition(Identifier id) {
        this.id = id;
        this.exclusive = false;
    }

    public VariantCondition(Identifier id, boolean exclusive) {
        this.id = id;
        this.exclusive = exclusive;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public boolean condition(MobEntity entity) {
        return false;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isExclusive() {
        return this.exclusive;
    }
}
