package dev.sygii.variantapi.variants.feature.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantCondition;
import dev.sygii.variantapi.variants.VariantFeature;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Optional;

public class AttributesFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("attributes");

    private final ArrayList<AttributeFeature> attributes = new ArrayList<>();

    public AttributesFeature(JsonObject data) {
        super(ID, true);

        for (JsonElement elem : data.getAsJsonArray("attributes")) {
            JsonObject obj = elem.getAsJsonObject();

            Identifier id = Identifier.tryParse(obj.get("type").getAsString());

            RegistryKey<EntityAttribute> asd = RegistryKey.of(RegistryKeys.ATTRIBUTE, id);
            Optional<RegistryEntry.Reference<EntityAttribute>> entityAttribute = Registries.ATTRIBUTE.getEntry(asd);

            if (entityAttribute.isPresent()) {
                RegistryEntry<EntityAttribute> attribute = entityAttribute.get();
                /*float baseValue = -10000.0f;
                if (obj.has("base")) {
                    baseValue = obj.get("base").getAsFloat();
                }
                boolean useBaseValue = false;
                if (obj.has("set_base_value")) {
                    useBaseValue = obj.get("set_base_value").getAsBoolean();
                }*/
                float value = obj.get("value").getAsFloat();


                EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.valueOf(obj.get("operation").getAsString().toUpperCase());
                this.attributes.add(new AttributeFeature(attribute, value, operation));
            } else {
                VariantAPI.LOGGER.warn("Attribute {} does not exist", obj.get("type").getAsString());
                continue;
            }
        }
    }

    public ArrayList<AttributeFeature> getAttributes() {
        return this.attributes;
    }

    public class AttributeFeature {
        private final RegistryEntry<EntityAttribute> attribute;
        private final float value;
        private final EntityAttributeModifier.Operation operation;

        public AttributeFeature(RegistryEntry<EntityAttribute> attribute, float value, EntityAttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.value = value;
            this.operation = operation;
        }

        public RegistryEntry<EntityAttribute> getAttribute() {
            return this.attribute;
        }

        public float getValue() {
            return this.value;
        }

        public EntityAttributeModifier.Operation getOperation() {
            return this.operation;
        }
    }
}
