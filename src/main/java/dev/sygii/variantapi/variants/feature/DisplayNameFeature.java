package dev.sygii.variantapi.variants.feature;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DisplayNameFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("display_name");
    private final String displayName;

    public DisplayNameFeature(String displayName) {
        super(ID);
        this.displayName = displayName;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeString(this.displayName);
    }

    public static DisplayNameFeature deserialize(PacketByteBuf buf) {
        return new DisplayNameFeature(buf.readString());
    }

    public Text getDisplayName() {
        return Text.translatable(this.displayName);
    }
}
