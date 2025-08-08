package dev.sygii.variantapi.variants.feature;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SlimeOverlayFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("slime_overlay");
    private final Identifier texture;

    public SlimeOverlayFeature(Identifier texture) {
        super(ID);
        this.texture = texture;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeIdentifier(texture);
    }

    public static SlimeOverlayFeature deserialize(PacketByteBuf buf) {
        return new SlimeOverlayFeature(buf.readIdentifier());
    }

    public Identifier getTexture() {
        return this.texture;
    }
}
