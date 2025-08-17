package dev.sygii.variantapi.network;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.network.packet.C2SRequestVariantPacket;
import dev.sygii.variantapi.network.packet.S2CRespondVariantPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

//? if >=1.21.1
//import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
public class ServerPacketHandler {

    public static void init() {
        //? if =1.20.1 {
		ServerPlayNetworking.registerGlobalReceiver(C2SRequestVariantPacket.PACKET_ID, (server, player, handler, buffer, sender) -> {
		    MinecraftServer serverInstance = server;
            C2SRequestVariantPacket payload = new C2SRequestVariantPacket(buffer);
		//?} else {
        /*PayloadTypeRegistry.playS2C().register(S2CRespondVariantPacket.CODEC_ID, S2CRespondVariantPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SRequestVariantPacket.CODEC_ID, C2SRequestVariantPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(C2SRequestVariantPacket.CODEC_ID, (payload, context) -> {
            MinecraftServer serverInstance = context.server();
            *///?}
            UUID entityId = payload.entityId();

            serverInstance.execute( () -> {
                Entity entity = serverInstance.getOverworld().getEntity(entityId);

                // If we couldn't find the mob in the overworld, start checking all other worlds
                if (entity == null) {
                    for (ServerWorld serverWorld : serverInstance.getWorlds()) {
                        Entity entity2 = serverWorld.getEntity(entityId);
                        if (entity2 != null) {
                            entity = entity2;
                        }
                    }
                }

                if (entity != null) {
                    //? if =1.20.1 {
                    VariantAPI.sendVariantPacket(handler.getPlayer(), entity);
                     //?} else {
                    /*VariantAPI.sendVariantPacket(context.player(), entity);
                    *///?}
                }
            });
        });
    }

}
