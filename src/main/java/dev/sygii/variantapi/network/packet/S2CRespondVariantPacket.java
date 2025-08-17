package dev.sygii.variantapi.network.packet;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
//? if =1.20.1 {
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
public record S2CRespondVariantPacket(int entityId, Identifier id, Identifier texture, boolean overlay, boolean defaultVariant, VariantFeatureRecord features) implements FabricPacket {
 //?} else {
/*
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
public record S2CRespondVariantPacket(int entityId, Identifier id, Identifier texture, boolean overlay, boolean defaultVariant, VariantFeatureRecord features) implements CustomPayload {
*///?}
    public static final Identifier PACKET_ID = VariantAPI.id("sync_variant");

    //? if >=1.21.1 {
    /*public static final CustomPayload.Id<S2CRespondVariantPacket> CODEC_ID = new CustomPayload.Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, S2CRespondVariantPacket> PACKET_CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeInt(value.entityId);
                buf.writeIdentifier(value.id);
                buf.writeIdentifier(value.texture);
                buf.writeBoolean(value.overlay);
                buf.writeBoolean(value.defaultVariant);
                value.features.write(buf);
            },
            buf -> new S2CRespondVariantPacket(buf.readInt(), buf.readIdentifier(), buf.readIdentifier(), buf.readBoolean(), buf.readBoolean(), VariantFeatureRecord.read(buf)));


    @Override
    public Id<? extends CustomPayload> getId() {
        return CODEC_ID;
    }
    *///?}

    //? if =1.20.1 {
    public static final PacketType<S2CRespondVariantPacket> TYPE = PacketType.create(
            PACKET_ID, S2CRespondVariantPacket::new
    );

    public S2CRespondVariantPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readIdentifier(), buf.readIdentifier(), buf.readBoolean(), buf.readBoolean(), VariantFeatureRecord.read(buf));
    }

    public S2CRespondVariantPacket(int entityId, Identifier id, Identifier texture, boolean overlay, boolean defaultVariant, VariantFeatureRecord features) {
        this.entityId = entityId;
        this.id = id;
        this.texture = texture;
        this.overlay = overlay;
        this.defaultVariant = defaultVariant;
        this.features = features;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeIdentifier(this.id);
        buf.writeIdentifier(this.texture);
        buf.writeBoolean(this.overlay);
        buf.writeBoolean(this.defaultVariant);
        this.features.write(buf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public int entityId() {
        return this.entityId;
    }

    public Identifier getId() {
        return this.id;
    }

    public Identifier getTexture() {
        return this.texture;
    }

    public VariantFeatureRecord getFeatures() {
        return this.features;
    }

    public boolean overlay() {
        return this.overlay;
    }

    public boolean defaultVariant() {
        return this.defaultVariant;
    }
    //?}

    public record VariantFeatureRecord(List<VariantFeature> variantFeatures) {

        public void write(PacketByteBuf buf) {
            buf.writeInt(variantFeatures().size());
            for (int i = 0; i < variantFeatures().size(); i++) {
                VariantFeature feat = variantFeatures().get(i);
                buf.writeIdentifier(feat.getIdentifier());
                feat.serialize(buf);
            }
        }

        public static VariantFeatureRecord read(PacketByteBuf buf) {
            List<VariantFeature> variantFeatures2 = new ArrayList<>();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                Identifier id = buf.readIdentifier();
                variantFeatures2.add(VariantAPI.deserialize(id, buf));
            }
            return new VariantFeatureRecord(variantFeatures2);
        }

    }

}
