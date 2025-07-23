package dev.sygii.variantapi.network;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.network.packet.C2SRequestVariantPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class ServerPacketHandler {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(C2SRequestVariantPacket.PACKET_ID, ((server, player, handler, buffer, sender) -> {
            C2SRequestVariantPacket payload = new C2SRequestVariantPacket(buffer);
            UUID entityId = payload.entityId();

            server.execute( () -> {
                Entity entity = server.getOverworld().getEntity(entityId);

                // If we couldn't find the mob in the overworld, start checking all other worlds
                if (entity == null) {
                    for (ServerWorld serverWorld : server.getWorlds()) {
                        Entity entity2 = serverWorld.getEntity(entityId);
                        if (entity2 != null) {
                            entity = entity2;
                        }
                    }
                }

                if (entity != null) {
                    VariantAPI.sendVariantPacket(handler.getPlayer(), entity);
                }
            });
        }));
    }

}
