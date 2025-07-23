package dev.sygii.variantapi;

import dev.sygii.variantapi.network.ClientPacketHandler;
import dev.sygii.variantapi.network.packet.C2SRequestVariantPacket;
import dev.sygii.variantapi.variants.Variant;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
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
