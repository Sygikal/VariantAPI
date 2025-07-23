package dev.sygii.variantapi.variants.feature;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CustomLightingFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("custom_lighting_feature");

    private final int light;
    public CustomLightingFeature(int light) {
        super(ID);
        this.light = light;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeInt(this.light);
    }

    public static CustomLightingFeature deserialize(PacketByteBuf buf) {
        return new CustomLightingFeature(buf.readInt());
    }

    public int getLight() {
        return this.light;
    }
}
