package dev.sygii.variantapi.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.VariantCondition;
import dev.sygii.variantapi.variants.VariantFeature;
import dev.sygii.variantapi.variants.condition.L2HostilityLevelCondition;
import dev.sygii.variantapi.variants.condition.NameCondition;
import dev.sygii.variantapi.variants.condition.PredicateCondition;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import dev.sygii.variantapi.VariantAPI;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class VariantLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return VariantAPI.id("variant_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        VariantAPI.variantOverlayMap.clear();
        VariantAPI.variantMap.clear();
        VariantAPI.entityToId.clear();
        manager.findResources("variants", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = null;
                stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                String fileName = id.getPath().replace("variants/", "").replace(".json", "");

                Identifier entityId = Identifier.tryParse(data.get("entity").getAsString());

                EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityId);
                if (entityType != null) {
                    Identifier texture = Identifier.tryParse(data.get("texture").getAsString());
                    Identifier variantId = Identifier.of(id.getNamespace(), fileName);

                    boolean overlay = false;
                    if (data.has("overlay")) {
                        overlay = data.get("overlay").getAsBoolean();
                    }

                    boolean defaultVariant = false;
                    if (data.has("default")) {
                        defaultVariant = data.get("default").getAsBoolean();
                    }

                    int weight = data.get("weight").getAsInt();

                    Variant variant = new Variant(variantId, texture, weight, overlay, defaultVariant);

                    if (data.has("conditions")) {
                        for (JsonElement elem : data.getAsJsonArray("conditions")) {
                            Identifier conditionType = Identifier.tryParse(elem.getAsJsonObject().get("type").getAsString());
                            boolean exclusive = false;
                            if (elem.getAsJsonObject().has("exclusive")) {
                                exclusive = elem.getAsJsonObject().get("exclusive").getAsBoolean();
                            }

                            VariantCondition condition = VariantAPI.createCondition(conditionType, elem.getAsJsonObject());
                            condition.setExclusive(exclusive);
                            variant.addCondition(condition);

                            if (condition instanceof NameCondition nameCondition) {
                                if (variant.overlay()) {
                                    if (VariantAPI.entityToCustomNameToVariantOverlayMap.get(entityType) == null) {
                                        VariantAPI.entityToCustomNameToVariantOverlayMap.put(entityType, new HashMap<String, ArrayList<Variant>>());
                                    }
                                    if (VariantAPI.entityToCustomNameToVariantOverlayMap.get(entityType).get(nameCondition.getName()) == null) {
                                        VariantAPI.entityToCustomNameToVariantOverlayMap.get(entityType).put(nameCondition.getName(), new ArrayList<Variant>());
                                    }
                                    VariantAPI.entityToCustomNameToVariantOverlayMap.get(entityType).get(nameCondition.getName()).add(variant);
                                }else {
                                    if (VariantAPI.entityToCustomNameToVariantMap.get(entityType) == null) {
                                        VariantAPI.entityToCustomNameToVariantMap.put(entityType, new HashMap<String, Variant>());
                                    }
                                    VariantAPI.entityToCustomNameToVariantMap.get(entityType).put(nameCondition.getName(), variant);
                                }
                            }
                        }
                    }

                    if (data.has("features")) {
                        for (JsonElement elem : data.getAsJsonArray("features")) {
                            Identifier conditionType = Identifier.tryParse(elem.getAsJsonObject().get("type").getAsString());

                            VariantFeature feature = VariantAPI.createFeature(conditionType, elem.getAsJsonObject());
                            variant.addFeature(feature);
                        }
                    }

                    //VariantAPI.entityToId.put(entityType, entityId);

                    if (overlay) {
                        if (VariantAPI.variantOverlayMap.get(entityType) == null) {
                            VariantAPI.variantOverlayMap.put(entityType, new ArrayList<Variant>());
                        }
                        VariantAPI.variantOverlayMap.get(entityType).add(variant);
                    }else {
                        VariantAPI.addVariant(entityType, variant);
                    }
                }else {
                    VariantAPI.LOGGER.error("Entity Type `" + entityType + "` is not a real entity");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String getBaseName(String filename) {
        if (filename == null)
            return null;

        String name = new File(filename).getName();
        int extPos = name.lastIndexOf('.');

        if (extPos < 0)
            return name;

        return name.substring(0, extPos);
    }
}
