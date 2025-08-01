package dev.sygii.variantapi.variants;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class VariantFeature {
    private final Identifier id;
    private final boolean serverOnly;

    public VariantFeature(Identifier id) {
        this.id = id;
        this.serverOnly = false;
    }

    public VariantFeature(Identifier id, boolean serverOnly) {
        this.id = id;
        this.serverOnly = serverOnly;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public boolean isServerOnly() {
        return this.serverOnly;
    }

    public void serialize(PacketByteBuf buf) {}
}
