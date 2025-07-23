package dev.sygii.variantapi.variants.feature;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CustomEyesFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("custom_eyes_feature");
    
    private final Identifier texture;
    public CustomEyesFeature(Identifier texture) {
        super(ID);
        this.texture = texture;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeIdentifier(texture);
    }

    public static CustomEyesFeature deserialize(PacketByteBuf buf) {
        return new CustomEyesFeature(buf.readIdentifier());
    }

    public Identifier getTexture() {
        return this.texture;
    }
}
