package dev.sygii.variantapi.network.packet;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.Variant;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//? if =1.20.1 {
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
public record C2SRequestVariantPacket(UUID entityId) implements FabricPacket {
//?} else {
/*
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
public record C2SRequestVariantPacket(UUID entityId) implements CustomPayload {
*///?}
    public static final Identifier PACKET_ID = VariantAPI.id("request_variant");

    //? if >=1.21.1 {
    /*public static final CustomPayload.Id<C2SRequestVariantPacket> CODEC_ID = new CustomPayload.Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, C2SRequestVariantPacket> PACKET_CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeUuid(value.entityId());
            },
            buf -> new C2SRequestVariantPacket(buf.readUuid()));


    @Override
    public Id<? extends CustomPayload> getId() {
        return CODEC_ID;
    }
    *///?}

    //? if =1.20.1 {
    public static final PacketType<C2SRequestVariantPacket> TYPE = PacketType.create(
            PACKET_ID, C2SRequestVariantPacket::new
    );

    public C2SRequestVariantPacket(PacketByteBuf buf) {
        this(buf.readUuid());
    }

    public C2SRequestVariantPacket(UUID entityId) {
        this.entityId = entityId;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.entityId);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public UUID entityId() {
        return entityId;
    }
    //?}

}
