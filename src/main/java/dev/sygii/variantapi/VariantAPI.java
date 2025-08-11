package dev.sygii.variantapi;

import com.google.gson.JsonObject;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.data.VariantLoader;
import dev.sygii.variantapi.network.ServerPacketHandler;
import dev.sygii.variantapi.network.packet.S2CRespondVariantPacket;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.VariantCondition;
import dev.sygii.variantapi.variants.VariantFeature;
import dev.sygii.variantapi.variants.condition.*;
import dev.sygii.variantapi.variants.feature.*;
import dev.sygii.variantapi.variants.feature.server.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Function;

public class VariantAPI implements ModInitializer {
	public static final String MOD_ID = "variantapi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String VARIANT_NBT_KEY = "variantapi:variantid";
	public static final String VARIANT_OVERLAYS_NBT_KEY = "variantapi:variantoverlays";

	public static final Map<Identifier, VariantCondition> conditionRegistry = new HashMap<>();

	public static final Map<EntityType<?>, ArrayList<Variant>> variantMap = new HashMap<>();
	public static final Map<EntityType<?>, Map<String, Variant>> entityToCustomNameToVariantMap = new HashMap<>();
	public static final Map<EntityType<?>, ArrayList<Variant>> variantOverlayMap = new HashMap<>();
	public static final Map<EntityType<?>, Map<String, ArrayList<Variant>>> entityToCustomNameToVariantOverlayMap = new HashMap<>();


	public static final Map<EntityType<?>, Identifier> entityToId = new HashMap<>();

	private static final Map<Identifier, Function<JsonObject, VariantCondition>> conditionCreators = new HashMap<>();
	private static final Map<Identifier, Function<JsonObject, VariantFeature>> featureCreators = new HashMap<>();

	private static final Map<Identifier, Function<PacketByteBuf, VariantFeature>> featureDeserializers = new HashMap<>();

	public static VariantFeature deserialize(Identifier type, PacketByteBuf buf) {
		Function<PacketByteBuf, VariantFeature> creator = featureDeserializers.get(type);
		if (creator != null) {
			return creator.apply(buf);
		}
		throw new IllegalArgumentException("Unknown deserializer type: " + type);
	}

	public static VariantFeature createFeature(Identifier type, JsonObject data) {
		Function<JsonObject, VariantFeature> creator = featureCreators.get(type);
		if (creator != null) {
			return creator.apply(data);
		}
		throw new IllegalArgumentException("Unknown feature type: " + type);
	}

	public static VariantCondition createCondition(Identifier type, JsonObject data) {
		Function<JsonObject, VariantCondition> creator = conditionCreators.get(type);
		if (creator != null) {
			return creator.apply(data);
		}
		throw new IllegalArgumentException("Unknown condition type: " + type);
	}

	public static void registerFeature(Identifier id, Function<JsonObject, VariantFeature> serializer, @Nullable Function<PacketByteBuf, VariantFeature> deserializer) {
		featureCreators.put(id, serializer);
		if (deserializer != null) {
			featureDeserializers.put(id, deserializer);
		}
	}

	public static void registerCondition(Identifier id, Function<JsonObject, VariantCondition> serializer) {
		conditionCreators.put(id, serializer);
	}

	@Override
	public void onInitialize() {
		Registry.register(Registries.LOOT_CONDITION_TYPE, VariantLootCondition.ID, VariantLootCondition.VLC);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new VariantLoader());

		conditionCreators.put(L2HostilityLevelCondition.ID, data -> new L2HostilityLevelCondition(data.get("min").getAsInt()));
		conditionCreators.put(PredicateCondition.ID, data -> new PredicateCondition(Identifier.tryParse(data.get("predicate").getAsString())));
		conditionCreators.put(MoonPhaseCondition.ID, data -> new MoonPhaseCondition(data.get("size").getAsFloat()));
		conditionCreators.put(BreedingCondition.ID, data -> new BreedingCondition(Identifier.tryParse(data.get("father").getAsString()), Identifier.tryParse(data.get("mother").getAsString())));
		conditionCreators.put(NameCondition.ID, data -> new NameCondition(data.get("name").getAsString()));
		conditionCreators.put(BiomeCondition.ID, data -> new BiomeCondition(data.get("biome").getAsString()));
		conditionCreators.put(AnyOfCondition.ID, data -> new AnyOfCondition(data.get("conditions").getAsJsonArray()));
		registerCondition(StructureCondition.ID, data -> new StructureCondition(data.get("structure").getAsString()));

		featureCreators.put(CustomEyesFeature.ID, data -> new CustomEyesFeature(Identifier.tryParse(data.get("texture").getAsString())));
		featureCreators.put(CustomLightingFeature.ID, data -> new CustomLightingFeature(data.get("light").getAsInt()));
		featureCreators.put(CustomRenderLayerFeature.ID, data -> new CustomRenderLayerFeature(CustomRenderLayerFeature.RenderLayers.valueOf(data.get("layer").getAsString())));
		featureCreators.put(CustomWoolFeature.ID, data -> new CustomWoolFeature(Identifier.tryParse(data.get("texture").getAsString())));
		featureCreators.put(CustomShearedWoolFeature.ID, data -> new CustomShearedWoolFeature(Identifier.tryParse(data.get("texture").getAsString())));
		featureCreators.put(HornsFeature.ID, data -> new HornsFeature(Identifier.tryParse(data.get("texture").getAsString()), data.has("color") ? data.get("color").getAsInt() : -1));
		registerFeature(WolfTexturesFeature.ID, data -> new WolfTexturesFeature(Identifier.tryParse(data.get("tame").getAsString()), Identifier.tryParse(data.get("angry").getAsString())), WolfTexturesFeature::deserialize);

		featureCreators.put(DaylightImmuneFeature.ID, data -> new DaylightImmuneFeature());
		featureCreators.put(WaterAffinityFeature.ID, data -> new WaterAffinityFeature(data.get("sensitive_to_water").getAsBoolean()));
		featureCreators.put(AttributesFeature.ID, AttributesFeature::new);
		featureCreators.put(ExplosionRadiusFeature.ID, data -> new ExplosionRadiusFeature(data.get("radius").getAsFloat()));
		registerFeature(FireAffinityFeature.ID, data -> new FireAffinityFeature(data.get("immune_to_fire").getAsBoolean()), null);
		registerFeature(CustomLootFeature.ID, data -> new CustomLootFeature(Identifier.tryParse(data.get("loot_table_id").getAsString())), null);

		featureDeserializers.put(CustomEyesFeature.ID, CustomEyesFeature::deserialize);
		featureDeserializers.put(CustomLightingFeature.ID, CustomLightingFeature::deserialize);
		featureDeserializers.put(CustomRenderLayerFeature.ID, CustomRenderLayerFeature::deserialize);
		featureDeserializers.put(CustomWoolFeature.ID, CustomWoolFeature::deserialize);
		featureDeserializers.put(CustomShearedWoolFeature.ID, CustomShearedWoolFeature::deserialize);
		featureDeserializers.put(HornsFeature.ID, HornsFeature::deserialize);
		registerFeature(CustomSoundsFeature.ID, CustomSoundsFeature::new, CustomSoundsFeature::deserialize);
		registerFeature(SlimeOverlayFeature.ID, data -> new SlimeOverlayFeature(Identifier.tryParse(data.get("texture").getAsString())), SlimeOverlayFeature::deserialize);
		registerFeature(DisplayNameFeature.ID, data -> new DisplayNameFeature(data.get("translate_key").getAsString()), DisplayNameFeature::deserialize);

		//featureDeserializers.put(DaylightImmuneFeature.ID, DaylightImmuneFeature::deserialize);

		/*ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(VariantAPI.id("sync_variants"), (player, joined) -> {
			ServerPlayNetworking.send(player, new S2CResetVariants());
			VariantAPI.variantPacketQueue.forEach((packet) -> ServerPlayNetworking.send(player, packet));
		});*/

		ServerPacketHandler.init();
	}

	public static Variant sort(ArrayList<Variant> variants) {
		if (variants == null || variants.isEmpty()) {
			return getDefaultVariant();
		}

		// Compute the total weight of all items together.
		// This can be skipped of course if sum is already 1.
		double totalWeight = 0.0;
		for (Variant i : variants) {
			totalWeight += i.weight();
		}

		int idx = 0;
		for (double r = Math.random() * totalWeight; idx < variants.size() - 1; ++idx) {
			r -= variants.get(idx).weight();
			if (r <= 0.0) break;
		}
		return variants.get(idx);
	}

	public static void rollRandomVariants(MobEntity entity) {
		((EntityAccess)entity).setVariant(VariantAPI.getRandomVariant(entity));
		((EntityAccess)entity).setVariantOverlays(VariantAPI.getOverlays(entity));
	}

	/*public static void rerollVariants(MobEntity entity) {
		VariantAPI.syncVariants(entity);
	}*/

	public static void syncVariantAttributes(MobEntity entity) {
		if (((EntityAccess)entity).getVariant().getFeatures().containsKey(AttributesFeature.ID)) {
			for (AttributesFeature.AttributeFeature attributeFeature : ((AttributesFeature)((EntityAccess)entity).getVariant().getFeatures().get(AttributesFeature.ID)).getAttributes()) {
				EntityAttributeInstance attr = entity.getAttributeInstance(attributeFeature.getAttribute().value());
				if (attr != null) {
					Identifier identifier = ((EntityAccess)entity).getVariant().id();
					UUID uid = UUID.nameUUIDFromBytes(identifier.toString().getBytes());
					if (attr.getModifier(uid) != null && attr.hasModifier(attr.getModifier(uid))) {
						attr.removeModifier(uid);
					}
					attr.addTemporaryModifier(new EntityAttributeModifier(uid, identifier.toString(), attributeFeature.getValue(), attributeFeature.getOperation()));
				}
			}
		}
	}

	public static void syncVariants(MobEntity entity) {
		MinecraftServer server = entity.getServer();
		syncVariantAttributes(entity);
		/*if (entity instanceof EndermanEntity) {
			entity.setPathfindingPenalty(PathNodeType.WATER, 8.0f);
		}*/
		if (server != null) {
			server.getPlayerManager().getPlayerList().forEach((player) -> {
				sendVariantPacket(player, entity);
			});
		}
	}

	public static void sendVariantPacket(ServerPlayerEntity player, Entity entity) {
		ServerPlayNetworking.send(player, new S2CRespondVariantPacket(entity.getId(), ((EntityAccess)entity).getVariant()));

		for (Variant variant : ((EntityAccess)entity).getVariantOverlays()) {
			ServerPlayNetworking.send(player, new S2CRespondVariantPacket(entity.getId(), variant));
		}
	}

	public static ArrayList<Variant> getSuitable(ArrayList<Variant> variantsToChooseFrom, MobEntity entity) {
		ArrayList<Variant> sorted = new ArrayList<Variant>();

		for (Variant variant : variantsToChooseFrom) {
			sorted.add(variant);
		}

		Iterator<Variant> i = sorted.iterator();
		Variant variant;
		while (i.hasNext()) {
			variant = i.next();

			for (VariantCondition condition : variant.getConditions()) {
				if (!condition.condition(entity)) {
					i.remove();
					continue;
				}else {
					if (condition.isExclusive()) {
						ArrayList<Variant> exclusive = new ArrayList<Variant>();
						exclusive.add(variant);
						return exclusive;
					}
				}
			}
		}
		return sorted;
	}

	public static Variant getRandomVariant(MobEntity entity) {
		ArrayList<Variant> variants = variantMap.get(entity.getType());
		if (variants == null || variants.isEmpty()) {
			return getDefaultVariant();
		}
		ArrayList<Variant> sorted = getSuitable(variants, entity);
		if (((EntityAccess)entity).variantapi$getParents() != null && !((EntityAccess)entity).isBreedingHandled()) {
			Pair<PassiveEntity, PassiveEntity> parents = ((EntityAccess)entity).variantapi$getParents();
			return entity.getRandom().nextBoolean() ? ((EntityAccess)parents.getA()).getVariant() : ((EntityAccess)parents.getB()).getVariant();
		}
		return sort(sorted);
	}

	public static ArrayList<Variant> getOverlays(MobEntity entity) {
		ArrayList<Variant> variants = variantOverlayMap.get(entity.getType());
		if (variants == null || variants.isEmpty()) {
			return new ArrayList<>();
		}
		ArrayList<Variant> sorted = getSuitable(variants, entity);
        return sorted;
		//return new ArrayList<>();
	}

	public static int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	public static Variant getVariant(EntityType<?> entityType, Identifier variantId) {
		ArrayList<Variant> variants = variantMap.get(entityType);

		if (variants != null && !variants.isEmpty()) {
			for (Variant variant : variants) {
				if (variant.id().equals(variantId)) {
					return variant;
				}
			}
		}

		return getDefaultVariant();
	}

	public static Variant getVariantOverlay(EntityType<?> entityType, Identifier variantId) {
		ArrayList<Variant> variants = variantOverlayMap.get(entityType);

		if (variants != null && !variants.isEmpty()) {
			for (Variant variant : variants) {
				if (variant.id().equals(variantId)) {
					return variant;
				}
			}
		}

		return null;
	}

	public static void addVariant(EntityType<?> entityType, Variant variant) {
		if (VariantAPI.variantMap.get(entityType) == null) {
			VariantAPI.variantMap.put(entityType, new ArrayList<Variant>());
		}
		VariantAPI.variantMap.get(entityType).add(variant);
	}

	public static Variant getDefaultVariant() {
		return new Variant(VariantAPI.id("default"), VariantAPI.id("textures/default"), 1, false, true);
	}

	public static Identifier id(String string) {
		return Identifier.of(MOD_ID, string);
	}
}
