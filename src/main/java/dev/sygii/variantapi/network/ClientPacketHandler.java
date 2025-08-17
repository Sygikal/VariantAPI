package dev.sygii.variantapi.network;

import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.network.packet.C2SRequestVariantPacket;
import dev.sygii.variantapi.network.packet.S2CRespondVariantPacket;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.VariantFeature;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

//? if >=1.21.1
//import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
public class ClientPacketHandler {

    public static void init() {
        //? if =1.20.1 {
		ClientPlayNetworking.registerGlobalReceiver(S2CRespondVariantPacket.PACKET_ID, (client, handler, buffer, sender) -> {
			MinecraftClient clientInstance = client;
			S2CRespondVariantPacket payload = new S2CRespondVariantPacket(buffer);
		//?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(S2CRespondVariantPacket.CODEC_ID, (payload, context) -> {
            MinecraftClient clientInstance = context.client();
        *///?}
            int entityId = payload.entityId();
            Identifier id = payload.id();
            Identifier texture = payload.texture();
            boolean overlay = payload.overlay();
            boolean defaultVariant = payload.defaultVariant();

            S2CRespondVariantPacket.VariantFeatureRecord features = payload.features();

            clientInstance.execute(() -> {
                //VariantAPIClient.addVariant(Registries.ENTITY_TYPE.get(payload.entity(), new Variant()));
                if (clientInstance.world != null) {
                    Entity entity = clientInstance.world.getEntityById(entityId);
                    Variant variant = new Variant(id, texture, overlay, defaultVariant);
                    for (VariantFeature feature : features.variantFeatures()) {
                        variant.addFeature(feature);
                    }
                    if (entity != null) {
                        if (overlay) {
                            ((EntityAccess) entity).addVariantOverlay(variant);
                        }else {
                            ((EntityAccess) entity).setVariant(variant);
                        }
                    }
                }
            });
        });
    }

}
