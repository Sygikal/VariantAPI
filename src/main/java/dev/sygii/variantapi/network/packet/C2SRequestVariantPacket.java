package dev.sygii.variantapi.network.packet;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.Variant;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class C2SRequestVariantPacket implements FabricPacket {
    public static final Identifier PACKET_ID = VariantAPI.id("request_variant");
    protected final UUID entityId;

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

}
