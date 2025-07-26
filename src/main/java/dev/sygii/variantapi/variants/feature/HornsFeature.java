package dev.sygii.variantapi.variants.feature;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class HornsFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("horns_feature");
    private final Identifier texture;

    public HornsFeature(Identifier texture) {
        super(ID);
        this.texture = texture;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeIdentifier(texture);
    }

    public static HornsFeature deserialize(PacketByteBuf buf) {
        return new HornsFeature(buf.readIdentifier());
    }

    public Identifier getTexture() {
        return this.texture;
    }
}
