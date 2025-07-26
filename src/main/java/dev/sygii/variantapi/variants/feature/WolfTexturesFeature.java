package dev.sygii.variantapi.variants.feature;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class WolfTexturesFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("wolf_textures");
    private final Identifier tameTexture;
    private final Identifier angryTexture;

    public WolfTexturesFeature(Identifier tameTexture, Identifier angryTexture) {
        super(ID);
        this.tameTexture = tameTexture;
        this.angryTexture = angryTexture;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeIdentifier(this.tameTexture);
        buf.writeIdentifier(this.angryTexture);
    }

    public static WolfTexturesFeature deserialize(PacketByteBuf buf) {
        return new WolfTexturesFeature(buf.readIdentifier(), buf.readIdentifier());
    }

    public Identifier getTame() {
        return this.tameTexture;
    }

    public Identifier getAngry() {
        return this.angryTexture;
    }
}
