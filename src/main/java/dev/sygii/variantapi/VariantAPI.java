package dev.sygii.variantapi;

import com.google.gson.JsonObject;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.data.VariantLoader;
import dev.sygii.variantapi.network.ServerPacketHandler;
import dev.sygii.variantapi.network.packet.S2CRespondVariantPacket;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.VariantCondition;
import dev.sygii.variantapi.variants.VariantFeature;
import dev.sygii.variantapi.variants.condition.L2HostilityLevelCondition;
import dev.sygii.variantapi.variants.condition.MoonPhaseCondition;
import dev.sygii.variantapi.variants.condition.PredicateCondition;
import dev.sygii.variantapi.variants.feature.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

public class VariantAPI implements ModInitializer {
	public static final String MOD_ID = "variantapi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String VARIANT_NBT_KEY = "variantapi:variantid";
	public static final String VARIANT_OVERLAYS_NBT_KEY = "variantapi:variantoverlays";

	//public static final Map<Identifier, ArrayList<Variant>> variantMap = new HashMap<>();

	public static final Map<Identifier, VariantCondition> conditionRegistry = new HashMap<>();

	public static final Map<EntityType<?>, ArrayList<Variant>> variantMap = new HashMap<>();
	public static final Map<EntityType<?>, ArrayList<Variant>> variantOverlayMap = new HashMap<>();

	public static final Map<EntityType<?>, Identifier> entityToId = new HashMap<>();

	private static final Map<Identifier, Function<JsonObject, VariantCondition>> conditionCreators = new HashMap<>();
	private static final Map<Identifier, Function<JsonObject, VariantFeature>> conditionFeatures = new HashMap<>();

	private static final Map<Identifier, Function<PacketByteBuf, VariantFeature>> featureDeserializers = new HashMap<>();

	public static VariantFeature deserialize(Identifier type, PacketByteBuf buf) {
		Function<PacketByteBuf, VariantFeature> creator = featureDeserializers.get(type);
		if (creator != null) {
			return creator.apply(buf);
		}
		throw new IllegalArgumentException("Unknown deserializer type: " + type);
	}

	public static VariantFeature createFeature(Identifier type, JsonObject data) {
		Function<JsonObject, VariantFeature> creator = conditionFeatures.get(type);
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

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new VariantLoader());

		conditionCreators.put(L2HostilityLevelCondition.ID, data -> new L2HostilityLevelCondition(data.get("min").getAsInt()));
		conditionCreators.put(PredicateCondition.ID, data -> new PredicateCondition(Identifier.tryParse(data.get("predicate").getAsString())));
		conditionCreators.put(MoonPhaseCondition.ID, data -> new MoonPhaseCondition(data.get("size").getAsInt()));

		conditionFeatures.put(CustomEyesFeature.ID, data -> new CustomEyesFeature(Identifier.tryParse(data.get("texture").getAsString())));
		conditionFeatures.put(CustomLightingFeature.ID, data -> new CustomLightingFeature(data.get("light").getAsInt()));
		conditionFeatures.put(CustomRenderLayerFeature.ID, data -> new CustomRenderLayerFeature(CustomRenderLayerFeature.RenderLayers.valueOf(data.get("layer").getAsString())));
		conditionFeatures.put(CustomWoolFeature.ID, data -> new CustomWoolFeature(Identifier.tryParse(data.get("texture").getAsString())));
		conditionFeatures.put(CustomShearedWoolFeature.ID, data -> new CustomShearedWoolFeature(Identifier.tryParse(data.get("texture").getAsString())));

		featureDeserializers.put(CustomEyesFeature.ID, CustomEyesFeature::deserialize);
		featureDeserializers.put(CustomLightingFeature.ID, CustomLightingFeature::deserialize);
		featureDeserializers.put(CustomRenderLayerFeature.ID, CustomRenderLayerFeature::deserialize);
		featureDeserializers.put(CustomWoolFeature.ID, CustomWoolFeature::deserialize);
		featureDeserializers.put(CustomShearedWoolFeature.ID, CustomShearedWoolFeature::deserialize);

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

	public static void rerollVariants(MobEntity entity) {
		((EntityAccess)entity).setVariant(VariantAPI.getRandomVariant(entity, entity.getType(), null, entity.getWorld(), entity.getRandom().nextLong(), entity.getWorld().getBiome(entity.getBlockPos()), entity.getWorld().getMoonSize()));
		((EntityAccess)entity).setVariantOverlays(VariantAPI.getOverlays(entity, entity.getType(), null, entity.getWorld(), entity.getRandom().nextLong(), entity.getWorld().getBiome(entity.getBlockPos()), entity.getWorld().getMoonSize()));
		VariantAPI.syncVariants(entity);
	}

	public static void syncVariants(MobEntity entity) {
		MinecraftServer server = entity.getServer();
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

	public static ArrayList<Variant> getSuitable(ArrayList<Variant> variantsToChooseFrom, MobEntity entity, EntityType<?> entityType, NbtCompound nbt, World world, @Nullable long randomSeed, @Nullable RegistryEntry<Biome> spawnBiome, /*@Nullable BreedingResultData breedingResultData,*/ @Nullable Float moonSize) {
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
				}
			}
		}
		//testLootCondition(context.getSource(), IdentifierArgumentType.getPredicateArgument(context, "predicate"))
		return sorted;
	}

	public static Variant getRandomVariant(MobEntity entity, EntityType<?> entityType, NbtCompound nbt, World world, @Nullable long randomSeed, @Nullable RegistryEntry<Biome> spawnBiome, /*@Nullable BreedingResultData breedingResultData,*/ @Nullable Float moonSize) {
		ArrayList<Variant> variants = variantMap.get(entityType);
		if (variants == null || variants.isEmpty()) {
			return getDefaultVariant();
		}
		ArrayList<Variant> sorted = getSuitable(variants, entity, entityType, nbt, world, randomSeed, spawnBiome, moonSize);
		return sort(sorted);
	}

	public static ArrayList<Variant> getOverlays(MobEntity entity, EntityType<?> entityType, NbtCompound nbt, World world, @Nullable long randomSeed, @Nullable RegistryEntry<Biome> spawnBiome, /*@Nullable BreedingResultData breedingResultData,*/ @Nullable Float moonSize) {
		ArrayList<Variant> variants = variantOverlayMap.get(entityType);
		if (variants == null || variants.isEmpty()) {
			return new ArrayList<>();
		}
		ArrayList<Variant> sorted = getSuitable(variants, entity, entityType, nbt, world, randomSeed, spawnBiome, moonSize);
        return sorted;
		//return new ArrayList<>();
	}

	private static boolean testLootCondition(ServerCommandSource source, LootCondition condition) {
		ServerWorld serverWorld = source.getWorld();
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld)
				.add(LootContextParameters.ORIGIN, source.getPosition())
				.addOptional(LootContextParameters.THIS_ENTITY, source.getEntity())
				.build(LootContextTypes.COMMAND);
		LootContext lootContext = new LootContext.Builder(lootContextParameterSet).build(null);
		lootContext.markActive(LootContext.predicate(condition));
		return condition.test(lootContext);
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
		return new Variant(VariantAPI.id("default"), VariantAPI.id("textures/default"), 1, false);
	}

	public static Identifier id(String string) {
		return Identifier.of(MOD_ID, string);
	}
}
