package dev.sygii.variantapi.variants.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantCondition;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class AllOfCondition extends VariantCondition {
    public static Identifier ID = VariantAPI.id("all_of");
    private ArrayList<VariantCondition> conditions = new ArrayList<>();


    public AllOfCondition(JsonArray data) {
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
            if (!condition.condition(entity)) {
                return false;
            }
        }
        return true;
    }
}
