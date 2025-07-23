package dev.sygii.variantapi.variants.feature;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CustomShearedWoolFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("custom_sheared_wool_feature");
    private final Identifier texture;

    public CustomShearedWoolFeature(Identifier texture) {
        super(ID);
        this.texture = texture;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeIdentifier(texture);
    }

    public static CustomShearedWoolFeature deserialize(PacketByteBuf buf) {
        return new CustomShearedWoolFeature(buf.readIdentifier());
    }

    public Identifier getTexture() {
        return this.texture;
    }
}
