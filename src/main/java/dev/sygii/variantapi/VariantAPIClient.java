package dev.sygii.variantapi;

import dev.sygii.variantapi.features.ShearedSheepWoolColorFeatureRenderer;
import dev.sygii.variantapi.network.ClientPacketHandler;
import dev.sygii.variantapi.network.packet.C2SRequestVariantPacket;
import dev.sygii.variantapi.variants.Variant;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariantAPIClient implements ClientModInitializer {

	public static final Map<EntityType<?>, ArrayList<Variant>> variantMap = new HashMap<>();

	@Override
	public void onInitializeClient() {
		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			ClientPlayNetworking.send(new C2SRequestVariantPacket(entity.getUuid()));
		});
		/*LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			//registrationHelper.register(new VariantOverlayFeatureRenderer<>((FeatureRendererContext)entityRenderer));
			if (entityRenderer instanceof SheepEntityRenderer) {
				registrationHelper.register(new ShearedSheepWoolColorFeatureRenderer<>((FeatureRendererContext)entityRenderer));
			}
		});*/

		ClientPacketHandler.init();
	}

	public static void addVariant(EntityType<?> entityType, Variant variant) {
		if (VariantAPIClient.variantMap.get(entityType) == null) {
			VariantAPIClient.variantMap.put(entityType, new ArrayList<Variant>());
		}
		VariantAPIClient.variantMap.get(entityType).add(variant);
	}
}
