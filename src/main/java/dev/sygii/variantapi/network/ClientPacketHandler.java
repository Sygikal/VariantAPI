package dev.sygii.variantapi.network;

import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.network.packet.S2CRespondVariantPacket;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.VariantFeature;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class ClientPacketHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(S2CRespondVariantPacket.PACKET_ID, ((client, handler, buffer, sender) -> {
            S2CRespondVariantPacket payload = new S2CRespondVariantPacket(buffer);
            int entityId = payload.entityId();
            Identifier id = payload.getId();
            Identifier texture = payload.getTexture();
            boolean overlay = payload.overlay();
            boolean defaultVariant = payload.defaultVariant();

            S2CRespondVariantPacket.VariantFeatureRecord features = payload.getFeatures();

            client.execute(() -> {
                //VariantAPIClient.addVariant(Registries.ENTITY_TYPE.get(payload.entity(), new Variant()));
                if (client.world != null) {
                    Entity entity = client.world.getEntityById(entityId);
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
        }));
    }

}
