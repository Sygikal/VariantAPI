package dev.sygii.variantapi.variants.feature;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CustomRenderLayerFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("custom_render_layer_feature");

    private final RenderLayers layer;
    public CustomRenderLayerFeature(RenderLayers layer) {
        super(ID);
        this.layer = layer;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeString(this.layer.toString());
    }

    public static CustomRenderLayerFeature deserialize(PacketByteBuf buf) {
        return new CustomRenderLayerFeature(RenderLayers.valueOf(buf.readString().toUpperCase()));
    }

    public RenderLayers getLayer() {
        return this.layer;
    }

    public enum RenderLayers {
        ENTITY_CUTOUT_NO_CULL,
        ENTITY_ALPHA,
        EYES;
    }
}
