package dev.sygii.variantapi.variants;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class VariantFeature {
    private final Identifier id;

    public VariantFeature(Identifier id) {
        this.id = id;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public void serialize(PacketByteBuf buf) {}
}
