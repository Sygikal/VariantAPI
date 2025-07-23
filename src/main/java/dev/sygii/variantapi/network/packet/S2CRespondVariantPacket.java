package dev.sygii.variantapi.network.packet;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.VariantFeature;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class S2CRespondVariantPacket implements FabricPacket {
    public static final Identifier PACKET_ID = VariantAPI.id("sync_variant");
    protected final int entityId;
    protected final Identifier id;
    protected final Identifier texture;
    protected final boolean overlay;
    protected final VariantFeatureRecord features;

    public static final PacketType<S2CRespondVariantPacket> TYPE = PacketType.create(
            PACKET_ID, S2CRespondVariantPacket::new
    );

    public S2CRespondVariantPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readIdentifier(), buf.readIdentifier(), buf.readBoolean(), VariantFeatureRecord.read(buf));
    }

    public S2CRespondVariantPacket(int entityId, Variant variant) {
        List<VariantFeature> skillAttributeList = new ArrayList<>(variant.getFeatures().values());

        this.entityId = entityId;
        this.id = variant.id();
        this.texture = variant.texture();
        this.overlay = variant.overlay();
        this.features = new VariantFeatureRecord(skillAttributeList);
    }

    public S2CRespondVariantPacket(int entityId, Identifier id, Identifier texture, boolean overlay, VariantFeatureRecord features) {
        this.entityId = entityId;
        this.id = id;
        this.texture = texture;
        this.overlay = overlay;
        this.features = features;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeIdentifier(this.id);
        buf.writeIdentifier(this.texture);
        buf.writeBoolean(this.overlay);
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
