package dev.sygii.variantapi.variants.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantCondition;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class InvertedCondition extends VariantCondition {
    public static Identifier ID = VariantAPI.id("inverted");
    private final VariantCondition condition;


    public InvertedCondition(JsonObject data) {
        super(ID);
        Identifier conditionType = Identifier.tryParse(data.get("type").getAsString());
        condition = VariantAPI.createCondition(conditionType, data);
    }

    @Override
    public boolean condition(MobEntity entity) {
        return !condition.condition(entity);
    }
}
