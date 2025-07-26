package dev.sygii.variantapi.variants.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.VariantCondition;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;

public class AnyOfCondition extends VariantCondition {
    public static Identifier ID = VariantAPI.id("any_of");
    private ArrayList<VariantCondition> conditions = new ArrayList<>();


    public AnyOfCondition(JsonArray data) {
        super(ID);
        for (JsonElement elem : data) {
            Identifier conditionType = Identifier.tryParse(elem.getAsJsonObject().get("type").getAsString());
            VariantCondition condition = VariantAPI.createCondition(conditionType, elem.getAsJsonObject());
            this.conditions.add(condition);
        }
    }

    @Override
    public boolean condition(MobEntity entity) {
        for (VariantCondition condition : this.conditions) {
            if (condition.condition(entity)) {
                return true;
            }
        }
        return false;
    }
}
