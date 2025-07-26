package dev.sygii.variantapi.variants.condition;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.VariantCondition;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class NameCondition extends VariantCondition {
    public static Identifier ID = VariantAPI.id( "name_condition");
    private final String name;

    public NameCondition(String name) {
        super(ID);
        this.name = name;
    }

    @Override
    public boolean condition(MobEntity entity) {
        if (entity.hasCustomName()) {
            return entity.getName().getString().equals(this.name);
        }
        return false;
    }

    public String getName() {
        return this.name;
    }
}
