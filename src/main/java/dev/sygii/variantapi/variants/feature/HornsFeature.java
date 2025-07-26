package dev.sygii.variantapi.variants.feature;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class HornsFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("horns_feature");
    private final Identifier texture;
    private final int color;

    public HornsFeature(Identifier texture, int color) {
        super(ID);
        this.texture = texture;
        this.color = color;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeIdentifier(this.texture);
        buf.writeInt(this.color);
    }

    public static HornsFeature deserialize(PacketByteBuf buf) {
        return new HornsFeature(buf.readIdentifier(), buf.readInt());
    }

    public Identifier getTexture() {
        return this.texture;
    }

    public float[] getColors() {
        int j = (color & 0xFF0000) >> 16;
        int k = (color & 0xFF00) >> 8;
        int l = (color & 0xFF) >> 0;
        return new float[]{j / 255.0F, k / 255.0F, l / 255.0F};
    }
}
