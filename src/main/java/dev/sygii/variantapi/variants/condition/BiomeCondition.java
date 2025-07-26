package dev.sygii.variantapi.variants.condition;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantCondition;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class BiomeCondition extends VariantCondition {
    public static Identifier ID = VariantAPI.id("biome_condition");
    private final Identifier biome;
    private TagKey<Biome> biomeTag = null;

    public BiomeCondition(String biome) {
        super(ID);
        if (biome.startsWith("#")) {
            biomeTag = TagKey.of(RegistryKeys.BIOME, Identifier.tryParse(biome.replace("#", "")));
        }
        this.biome = Identifier.tryParse(biome.replace("#", ""));
    }

    @Override
    public boolean condition(MobEntity entity) {
        RegistryEntry<Biome> currentBiome = entity.getWorld().getBiome(entity.getBlockPos());

        if (biomeTag != null) {
            return currentBiome.isIn(biomeTag);
        }else {
            return currentBiome.getKey().isPresent() && this.biome.equals(currentBiome.getKey().get().getValue());
        }
    }
}
