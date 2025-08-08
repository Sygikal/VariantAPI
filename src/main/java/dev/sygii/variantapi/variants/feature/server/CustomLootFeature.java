package dev.sygii.variantapi.variants.feature.server;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.util.Identifier;

public class CustomLootFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("custom_loot_table");
    private final Identifier lootTableId;

    public CustomLootFeature(Identifier lootTableId) {
        super(ID, true);

        this.lootTableId = lootTableId;
    }

    public Identifier getLootTableId() {
        return this.lootTableId;
    }
}
