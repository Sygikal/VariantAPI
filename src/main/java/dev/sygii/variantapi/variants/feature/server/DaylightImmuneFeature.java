package dev.sygii.variantapi.variants.feature.server;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class DaylightImmuneFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("daylight_immune");

    public DaylightImmuneFeature() {
        super(ID, true);
    }
}
