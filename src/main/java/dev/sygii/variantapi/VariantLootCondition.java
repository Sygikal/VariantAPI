package dev.sygii.variantapi;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sygii.variantapi.acess.EntityAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import net.minecraft.util.JsonHelper;

import java.util.Set;

public record VariantLootCondition(Identifier variant) implements LootCondition {
    public static final Identifier ID = VariantAPI.id("variant");
    public static final VLCSerializer SERIALIZER = new VLCSerializer();
    public static final LootConditionType VLC = new LootConditionType(SERIALIZER);

    @Override
    public boolean test(LootContext lootContext)
    {
        if (lootContext.requireParameter(LootContextParameters.THIS_ENTITY) instanceof LivingEntity livingEntity) {
            return ((EntityAccess)livingEntity).getVariant().id().equals(variant);
        } else {
            return false;
        }
    }

    @NotNull
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters()
    {
        return ImmutableSet.of(LootContextParameters.THIS_ENTITY);
    }

    @NotNull
    @Override
    public LootConditionType getType()
    {
        return VLC;
    }

    public static LootCondition.Builder builder(LootContext.EntityTarget entity, Identifier variant)
    {
        return () -> new VariantLootCondition(variant);
    }

    public static class VLCSerializer implements JsonSerializer<VariantLootCondition>
    {
        @Override
        public void toJson(JsonObject json, VariantLootCondition condition, JsonSerializationContext context)
        {
            json.add("variant", context.serialize(condition.variant));
        }

        @NotNull
        @Override
        public VariantLootCondition fromJson(JsonObject json, JsonDeserializationContext context)
        {
            return new VariantLootCondition(
                    Identifier.tryParse(JsonHelper.deserialize(json, "variant", context, String.class))
            );
        }
    }
}
